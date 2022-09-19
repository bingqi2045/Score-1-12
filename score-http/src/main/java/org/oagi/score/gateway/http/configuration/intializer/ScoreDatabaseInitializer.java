package org.oagi.score.gateway.http.configuration.intializer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oagi.score.gateway.http.helper.ScriptRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class ScoreDatabaseInitializer {

    private static final Log logger = LogFactory.getLog(ScoreDatabaseInitializer.class);

    private DataSource dataSource;

    private ResourceLoader resourceLoader;

    private Yaml yaml;

    private String migrationConfigurationLocation;

    public ScoreDatabaseInitializer(DataSource dataSource, ResourceLoader resourceLoader) {
        this(dataSource, resourceLoader, new Yaml());
    }

    public ScoreDatabaseInitializer(DataSource dataSource, ResourceLoader resourceLoader,
                                    Yaml yaml) {
        this.dataSource = dataSource;
        this.resourceLoader = resourceLoader;
        this.yaml = yaml;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public String getMigrationConfigurationLocation() {
        return migrationConfigurationLocation;
    }

    public void setMigrationConfigurationLocation(String migrationConfigurationLocation) {
        this.migrationConfigurationLocation = migrationConfigurationLocation;
    }

    public void perform() throws Throwable {
        MigrationHistory migrationHistory = loadMigrationHistory(dataSource);

        String configurationLocation = StringUtils.trimToNull(getMigrationConfigurationLocation());
        if (configurationLocation == null) {
            throw new IllegalStateException("Can't find a location of the configuration for Score migration.");
        }

        try (Connection connection = getConnection()) {
            MigrationConfiguration configuration =
                    loadMigrationConfiguration(connection, configurationLocation, migrationHistory);

            for (MigrationProcess migrationProcess : configuration.getMigrationProcesses()) {
                if (!migrationProcess.tryLock(TimeUnit.SECONDS, 5)) {
                    continue;
                }
                try {
                    Throwable error = null;
                    try {
                        migrationProcess.execute();
                    } catch (Throwable e) {
                        logger.error("Migration process execution failed: " + migrationProcess, e);
                        error = e;
                    }

                    try {
                        if (error == null) {
                            migrationProcess.saveStatus(true);
                        } else {
                            migrationProcess.saveStatus(false, error.getMessage());
                        }
                    } catch (Exception e) {
                        logger.error("Unexpected error occurs when schema history is archiving.", e);
                    }
                    if (error != null) {
                        throw error;
                    }
                } finally {
                    migrationProcess.unlock();
                }
            }
        }
    }

    private void executeSql(Connection conn, String sql) throws SQLException {
        try (Statement statement = conn.createStatement()) {
            statement.execute(sql);
        }
    }

    private MigrationHistory loadMigrationHistory(DataSource dataSource) throws Exception {
        MigrationHistory migrationHistory = new MigrationHistory(dataSource);
        migrationHistory.createHistoryTableIfNotExists();
        return migrationHistory;
    }

    private MigrationConfiguration loadMigrationConfiguration(
            Connection conn, String migrationConfigurationLocation, MigrationHistory migrationHistory)
            throws IOException {
        Resource resource = resourceLoader.getResource(migrationConfigurationLocation);
        Map<String, Object> obj;
        try (InputStream inputStream = resource.getInputStream()) {
            obj = yaml.load(inputStream);
        }

        MigrationConfiguration configuration = new MigrationConfiguration();
        Map<String, Object> migrationObj = (Map<String, Object>) obj.get("migration");

        List<Map<String, Object>> processesObj = (List<Map<String, Object>>) migrationObj.get("processes");
        configuration.setMigrationProcesses(
                processesObj.stream().map(e -> toMigrationProcess(e)
                                .withHistory(migrationHistory)
                                .withConnection(conn))
                        .sorted(Comparator.comparing(MigrationProcess::getOrder))
                        .collect(Collectors.toList()));

        return configuration;
    }

    private MigrationProcess toMigrationProcess(Map<String, Object> obj) {
        MigrationProcess migrationProcess = new MigrationProcess(getResourceLoader());
        migrationProcess.setHistoryId((String) obj.get("id"));
        migrationProcess.setOrder((Integer) obj.get("order"));
        migrationProcess.setAuthor(StringUtils.trimToNull((String) obj.get("author")));
        migrationProcess.setComment(StringUtils.trimToNull((String) obj.get("comment")));
        migrationProcess.setScriptLocation(StringUtils.trimToNull((String) obj.get("script")));
        return migrationProcess;
    }

    private class MigrationConfiguration {

        private Collection<MigrationProcess> migrationProcesses;

        public Collection<MigrationProcess> getMigrationProcesses() {
            return migrationProcesses;
        }

        public void setMigrationProcesses(Collection<MigrationProcess> migrationProcesses) {
            this.migrationProcesses = migrationProcesses;
        }
    }

    private class MigrationHistory {

        public static final String DEFAULT_HISTORY_TABLE_NAME = "score_schema_history";

        private DataSource dataSource;

        private String historyTableName;

        public MigrationHistory(DataSource dataSource) {
            this(dataSource, DEFAULT_HISTORY_TABLE_NAME);
        }

        public MigrationHistory(DataSource dataSource, String historyTableName) {
            this.dataSource = dataSource;
            this.historyTableName = historyTableName;
        }

        public String getHistoryTableName() {
            return historyTableName;
        }

        private JdbcTemplate getJdbcTemplate() {
            return new JdbcTemplate(getDataSource());
        }

        private boolean exists(String tableName) {
            JdbcTemplate jdbcTemplate = getJdbcTemplate();
            List<String> tableNames = new ArrayList();
            jdbcTemplate.query("SHOW TABLE STATUS", row -> {
                tableNames.add(StringUtils.trimToNull(row.getString("name")));
            });
            return tableNames.contains(tableName);
        }

        public void createHistoryTableIfNotExists() throws Exception {
            if (exists(getHistoryTableName())) {
                return;
            }

            JdbcTemplate jdbcTemplate = getJdbcTemplate();
            jdbcTemplate.execute("CREATE TABLE `" + getHistoryTableName() + "` (\n" +
                    "  `history_id` char(36) NOT NULL,\n" +
                    "  `order` int(10) unsigned NOT NULL,\n" +
                    "  `executed_on` datetime(6) NOT NULL,\n" +
                    "  `result` smallint(2) NOT NULL DEFAULT '-1',\n" +
                    "  `message` text DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`history_id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        }
    }

    private enum MigrationProcessState {
        INITIATED(-1),
        FAILED(0),
        SUCCEED(1);

        private final int value;

        MigrationProcessState(int value) {
            this.value = value;
        }

        public static MigrationProcessState valueOf(int value) {
            for (MigrationProcessState state : MigrationProcessState.values()) {
                if (state.value == value)
                    return state;

            }
            throw new IllegalArgumentException(
                    "No enum constant " + MigrationProcessState.class.getCanonicalName() + "." + value);
        }
    }

    private class MigrationProcess {

        private MigrationHistory migrationHistory;

        private ResourceLoader resourceLoader;

        private Connection connection;

        private String historyId;

        private int order;

        private String author;

        private String comment;

        private String scriptLocation;

        public MigrationProcess(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

        public MigrationProcess withHistory(MigrationHistory migrationHistory) {
            this.migrationHistory = migrationHistory;
            return this;
        }

        public MigrationHistory getMigrationHistory() {
            return migrationHistory;
        }

        public MigrationProcess withConnection(Connection connection) {
            this.connection = connection;
            return this;
        }

        private Connection getConnection() {
            return connection;
        }

        public String getHistoryId() {
            return historyId;
        }

        public void setHistoryId(String historyId) {
            this.historyId = historyId;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getScriptLocation() {
            return scriptLocation;
        }

        public void setScriptLocation(String scriptLocation) {
            this.scriptLocation = scriptLocation;
        }

        public boolean tryLock(TimeUnit timeUnit, long duration) {
            try {
                executeSql(getConnection(), "SET wait_timeout=" + timeUnit.toMillis(duration));
            } catch (SQLException e) {
                throw new IllegalStateException("Failed to set `wait_timeout` parameter", e);
            }

            try {
                getConnection().setAutoCommit(false);
                executeSql(getConnection(), "START TRANSACTION");
                MigrationProcessState state = queryForUpdate();
                if (state == MigrationProcessState.SUCCEED) {
                    logger.info("The process with History ID " + getHistoryId() + " has already been executed.");
                    return false;
                }
                return true;
            } catch (SQLException e) {
                logger.error("Unexpected error occurs when it tried to acquire the record lock.", e);
                return false;
            }
        }

        private MigrationProcessState queryForUpdate() throws SQLException {
            String queryForUpdate = "SELECT `result` FROM `" + getMigrationHistory().getHistoryTableName() +
                    "` WHERE `history_id` = '" + getHistoryId() + "' FOR UPDATE";
            try (Statement statement = getConnection().createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(queryForUpdate)) {
                    if (resultSet.next()) {
                        return MigrationProcessState.valueOf(resultSet.getInt("result"));
                    }
                }
            }

            String insertStmt = "INSERT INTO `" + getMigrationHistory().getHistoryTableName() +
                    "` (`history_id`, `order`, `executed_on`, `result`) VALUES (?, ?, CURRENT_TIMESTAMP(6), -1)";
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(insertStmt)) {
                int paramIdx = 1;
                preparedStatement.setString(paramIdx++, getHistoryId());
                preparedStatement.setInt(paramIdx++, getOrder());
                preparedStatement.execute();
            }

            // try again
            try (Statement statement = getConnection().createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(queryForUpdate)) {
                    if (resultSet.next()) {
                        return MigrationProcessState.valueOf(resultSet.getInt("result"));
                    }
                }
            }
            throw new IllegalStateException("Can't acquire the record for update with ID " + getHistoryId());
        }

        public void unlock() {
            try {
                getConnection().commit();
            } catch (SQLException ignore) {
                logger.warn("Failed to commit statements.", ignore);
            }
        }

        public void execute() throws Exception {
            Resource resource = getResourceLoader().getResource(getScriptLocation());
            logger.info("Execute the script " + resource);

            ScriptRunner scriptRunner = new ScriptRunner(getConnection(), false, true);
            scriptRunner.runScript(getConnection(), new EncodedResource(resource).getReader());
        }

        public void saveStatus(boolean result) throws SQLException {
            saveStatus(result, null);
        }

        public void saveStatus(boolean result, String message) throws SQLException {
            String query;
            if (message == null) {
                query = "UPDATE `" + getMigrationHistory().getHistoryTableName() +
                        "` SET `result` = ? WHERE `history_id` = ?";
            } else {
                query = "UPDATE `" + getMigrationHistory().getHistoryTableName() +
                        "` SET `message` = ?, `result` = ? WHERE `history_id` = ?";
            }

            try (PreparedStatement statement = getConnection().prepareStatement(query)) {
                int paramIdx = 1;
                if (message != null) {
                    statement.setString(paramIdx++, message);
                }
                statement.setInt(paramIdx++, (result) ? 1 : 0);
                statement.setString(paramIdx++, getHistoryId());
                statement.execute();
            }
        }
    }
}
