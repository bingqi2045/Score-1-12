package org.oagi.score.gateway.http.api.release_management.service;

import com.google.gson.stream.JsonWriter;
import org.jooq.*;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.gateway.http.api.release_management.data.MigrationMetadata;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.Zip;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ReleaseRecord;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.service.common.data.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.max;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Service
@Transactional
public class MigrationService {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    public File makeDumpFile(MigrationMetadata metadata, Path tempDir) throws IOException {
        String scriptFileName = metadata.getScriptFileName();
        File dumpFile = new File(tempDir.toFile(), scriptFileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(dumpFile, false))) {
            writeTitleBox(writer, metadata);
            writer.println("");
            writePreConditions(writer, metadata);
            writer.println("");
            writeInsertStatements(writer, metadata);
            writer.println("");
            writePostConditions(writer, metadata);
        }

        return dumpFile;
    }

    private void writeTitleBox(PrintWriter writer, MigrationMetadata metadata) {
        String title = "Migration script for OAGIS " + metadata.getTargetReleaseNum();
        String author = "Author: " + metadata.getMaintainer() +
                (StringUtils.hasLength(metadata.getMaintainerEmail()) ? (" <" + metadata.getMaintainerEmail() + ">") : "");

        int len = (int) (Math.max(title.length(), author.length()) * 1.5);
        String border = "-- ";
        border += IntStream.range(0, (len - border.length() - 2)).mapToObj(e -> "-").collect(Collectors.joining());
        border += "--";

        String breakline = "-- ";
        breakline += IntStream.range(0, (len - breakline.length() - 2)).mapToObj(e -> " ").collect(Collectors.joining());
        breakline += "--";

        title = "-- " + title;
        title += IntStream.range(0, (len - title.length() - 2)).mapToObj(e -> " ").collect(Collectors.joining());
        title += "--";

        author = "-- " + author;
        author += IntStream.range(0, (len - author.length() - 2)).mapToObj(e -> " ").collect(Collectors.joining());
        author += "--";

        writer.println(border);
        writer.println(title);
        writer.println(breakline);
        writer.println(author);
        writer.println(border);
    }

    private void writePreConditions(PrintWriter writer, MigrationMetadata metadata) {
        BigInteger delimiterId = metadata.getDelimiterId();

        writer.println("SET FOREIGN_KEY_CHECKS = 0;");
        writer.println("");
        writer.println("-- Deleting OAGIS " + metadata.getPrevReleaseNum() + " --");
        writer.println("");
        writer.println("DELETE FROM `acc` WHERE `acc_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `acc_manifest` WHERE `acc_manifest_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `ascc` WHERE `ascc_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `ascc_manifest` WHERE `ascc_manifest_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `bcc` WHERE `bcc_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `bcc_manifest` WHERE `bcc_manifest_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `asccp` WHERE `asccp_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `asccp_manifest` WHERE `asccp_manifest_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `bccp` WHERE `bccp_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `bccp_manifest` WHERE `bccp_manifest_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `dt` WHERE `dt_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `dt_manifest` WHERE `dt_manifest_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `dt_sc` WHERE `dt_sc_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `dt_sc_manifest` WHERE `dt_sc_manifest_id`< " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `agency_id_list` WHERE `agency_id_list_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `agency_id_list_manifest` WHERE `agency_id_list_manifest_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `agency_id_list_value` WHERE `agency_id_list_value_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `agency_id_list_value_manifest` WHERE `agency_id_list_value_manifest_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `code_list` WHERE `code_list_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `code_list_manifest` WHERE `code_list_manifest_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `code_list_value` WHERE `code_list_value_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `code_list_value_manifest` WHERE `code_list_value_manifest_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `xbt` WHERE `xbt_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `xbt_manifest` WHERE `xbt_manifest_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `log` WHERE `log_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `seq_key` WHERE `seq_key_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `release` WHERE `release_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `app_user` WHERE `app_user_id` >= 2 AND `app_user_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DELETE FROM `module` WHERE `module_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `module_set` WHERE `module_set_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `module_set_release` WHERE `module_set_release_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `module_acc_manifest` WHERE `module_acc_manifest_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `module_asccp_manifest` WHERE `module_asccp_manifest_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `module_bccp_manifest` WHERE `module_bccp_manifest_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `module_dt_manifest` WHERE `module_dt_manifest_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `module_agency_id_list_manifest` WHERE `module_agency_id_list_manifest_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `module_code_list_manifest` WHERE `module_code_list_manifest_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `module_xbt_manifest` WHERE `module_xbt_manifest_id` < " + delimiterId + ";");
        writer.println("DELETE FROM `module_blob_content_manifest` WHERE `module_blob_content_manifest_id` < " + delimiterId + ";");
        writer.println("");
        writer.println("DROP TABLE IF EXISTS `module_dep`;");
        writer.println("DROP TABLE IF EXISTS `module_dir`;");
        writer.println("DROP TABLE IF EXISTS `module_set_assignment`;");
        writer.println("");
        writer.println("-- End of 'Deleting OAGIS " + metadata.getPrevReleaseNum() + "' --");
    }

    private void writePostConditions(PrintWriter writer, MigrationMetadata metadata) {
        BigInteger autoIncrementId = metadata.getDelimiterId().add(BigInteger.ONE);

        writer.println("-- End of 'Adding OAGIS " + metadata.getTargetReleaseNum() + "' --");
        writer.println("");
        writer.println("");
        writer.println("ALTER TABLE `acc` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `acc_manifest` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `ascc` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `ascc_manifest` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `bcc` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `bcc_manifest` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `asccp` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `asccp_manifest` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `bccp` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `bccp_manifest` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `dt` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `dt_manifest` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `dt_sc` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `dt_sc_manifest` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `agency_id_list` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `agency_id_list_manifest` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `agency_id_list_value` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `agency_id_list_value_manifest` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `code_list` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `code_list_manifest` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `code_list_value` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `code_list_value_manifest` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `log` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `namespace` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `seq_key` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("ALTER TABLE `app_user` AUTO_INCREMENT = " + autoIncrementId + ";");
        writer.println("");
        writer.println("SET FOREIGN_KEY_CHECKS = 1;");
        writer.println("-- End of 'OAGIS " + metadata.getTargetReleaseNum() + "' --");
    }

    private String value(org.jooq.Record record, Field field) {
        Object val = record.get(field);
        if (val instanceof LocalDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            return "'" + ((LocalDateTime) val).format(formatter) + "'";
        } else if (val instanceof String) {
            return "'" + ((String) val)
                    .replaceAll("'", "\\\\'")
                    .replaceAll("\n", "\\\\n") + "'";
        } else if (val instanceof JSON) {
            String json = ((JSON) val).data();
            json = json.replaceAll("\"", "\\\\\"")
                    .replaceAll("'", "\\\\'")
                    .replaceAll("\n", "\\\\n");
            return "'" + json + "'";
        }

        return (val != null) ? val.toString() : "NULL";
    }

    private void writeInsertStatements(PrintWriter writer,
                                       TableImpl<? extends org.jooq.Record> table, Condition condition) {
        writer.println("--");
        writer.println("-- Dumping data for table `" + table.getName() + "`");
        writer.println("--");
        writer.println("");

        writer.println("LOCK TABLES `" + table.getName() + "` WRITE;");
        SelectWhereStep whereStep = dslContext.selectFrom(table);
        List<org.jooq.Record> records;
        if (condition != null) {
            records = whereStep.where(condition).fetch();
        } else {
            records = whereStep.fetch();
        }

        for (org.jooq.Record record : records) {
            String insertStatement = "INSERT INTO `" + table.getName() + "` (" +
                    Arrays.stream(table.fields()).map(e -> "`" + e.getName() + "`")
                            .collect(Collectors.joining(", ")) + ") VALUES (" +
                    Arrays.stream(table.fields()).map(e -> value(record, e))
                            .collect(Collectors.joining(", ")) + ");";
            writer.println(insertStatement);
        }
        writer.println("UNLOCK TABLES;");
        writer.println("");
    }

    private void writeInsertUpdateStatements(PrintWriter writer,
                                             TableImpl<? extends org.jooq.Record> table,
                                             Field primaryKey,
                                             Condition... conditions) {

        writer.println("--");
        writer.println("-- Dumping data for table `" + table.getName() + "`");
        writer.println("--");
        writer.println("");

        writer.println("LOCK TABLES `" + table.getName() + "` WRITE;");
        for (org.jooq.Record record : dslContext.selectFrom(table)
                .where(and(conditions))
                .fetch()) {
            String insertUpdateStatements = "INSERT INTO `" + table.getName() + "` (" +
                    Arrays.stream(table.fields()).map(e -> "`" + e.getName() + "`")
                            .collect(Collectors.joining(", ")) + ") VALUES (" +
                    Arrays.stream(table.fields()).map(e -> value(record, e))
                            .collect(Collectors.joining(", ")) + ") ON DUPLICATE KEY UPDATE " +
                    Arrays.stream(table.fields()).filter(e -> !primaryKey.equals(e)).map(e -> "`" + e.getName() + "` = " + value(record, e))
                            .collect(Collectors.joining(", ")) + ";";
            writer.println(insertUpdateStatements);
        }
        writer.println("UNLOCK TABLES;");
        writer.println("");
    }

    private void writeInsertStatements(PrintWriter writer, MigrationMetadata metadata) {
        writeInsertStatements(writer, ACC, null);
        writeInsertStatements(writer, ACC_MANIFEST, null);

        writeInsertStatements(writer, ASCC, null);
        writeInsertStatements(writer, ASCC_MANIFEST, null);

        writeInsertStatements(writer, BCC, null);
        writeInsertStatements(writer, BCC_MANIFEST, null);

        writeInsertStatements(writer, ASCCP, null);
        writeInsertStatements(writer, ASCCP_MANIFEST, null);

        writeInsertStatements(writer, BCCP, null);
        writeInsertStatements(writer, BCCP_MANIFEST, null);

        writeInsertStatements(writer, DT, null);
        writeInsertStatements(writer, DT_MANIFEST, null);

        writeInsertStatements(writer, DT_SC, null);
        writeInsertStatements(writer, DT_SC_MANIFEST, null);

        writeInsertStatements(writer, AGENCY_ID_LIST, null);
        writeInsertStatements(writer, AGENCY_ID_LIST_MANIFEST, null);

        writeInsertStatements(writer, AGENCY_ID_LIST_VALUE, null);
        writeInsertStatements(writer, AGENCY_ID_LIST_VALUE_MANIFEST, null);

        writeInsertStatements(writer, CODE_LIST, null);
        writeInsertStatements(writer, CODE_LIST_MANIFEST, null);

        writeInsertStatements(writer, CODE_LIST_VALUE, null);
        writeInsertStatements(writer, CODE_LIST_VALUE_MANIFEST, null);

        writeInsertStatements(writer, XBT, null);
        writeInsertStatements(writer, XBT_MANIFEST, null);

        writeInsertStatements(writer, MODULE, null);
        writeInsertStatements(writer, MODULE_SET, null);
        writeInsertStatements(writer, MODULE_SET_RELEASE, null);
        writeInsertStatements(writer, MODULE_ACC_MANIFEST, null);
        writeInsertStatements(writer, MODULE_ASCCP_MANIFEST, null);
        writeInsertStatements(writer, MODULE_BCCP_MANIFEST, null);
        writeInsertStatements(writer, MODULE_DT_MANIFEST, null);
        writeInsertStatements(writer, MODULE_AGENCY_ID_LIST_MANIFEST, null);
        writeInsertStatements(writer, MODULE_CODE_LIST_MANIFEST, null);
        writeInsertStatements(writer, MODULE_XBT_MANIFEST, null);
        writeInsertStatements(writer, MODULE_BLOB_CONTENT_MANIFEST, null);

        writeInsertStatements(writer, LOG, null);
        writeInsertStatements(writer, SEQ_KEY, SEQ_KEY.SEQ_KEY_ID.lessOrEqual(ULong.valueOf(metadata.getMaxSeqKeyId())));
        writeInsertStatements(writer, NAMESPACE, null);
        writeInsertStatements(writer, RELEASE, null);

        writeInsertUpdateStatements(writer, APP_USER, APP_USER.APP_USER_ID,
                APP_USER.IS_DEVELOPER.eq((byte) 1));

        writer.println("");
        writer.println("-- Dump completed on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    public File makeMigrationDataFile(AuthenticatedPrincipal user, String targetReleaseId) throws IOException {
        Path tempDir = Files.createTempDirectory("mig-" + UUID.randomUUID());

        MigrationMetadata metadata = makeMigrationMetadata(sessionService.getAppUserByUsername(user),
                targetReleaseId);
        File dumpFile = makeDumpFile(metadata, tempDir);

        String targetReleaseNum = metadata.getReleaseIdNumMap().get(targetReleaseId);
        String fileName = "mig_info_" + targetReleaseNum + ".json";
        File infoFile = new File(tempDir.toFile(), fileName);

        try (JsonWriter jsonWriter = new JsonWriter(new FileWriter(infoFile, false))) {
            jsonWriter.setLenient(true);
            jsonWriter.setIndent("  ");

            jsonWriter.beginObject();

            jsonWriter.name("version").value("1.0");
            jsonWriter.name("maintainer").value(
                    metadata.getMaintainer() +
                            (StringUtils.hasLength(metadata.getMaintainerEmail()) ? (" <" + metadata.getMaintainerEmail() + ">") : ""));
            jsonWriter.name("description").value("Add OAGIS " + targetReleaseNum + " release");

            jsonWriter.name("migration");
            jsonWriter.beginObject();

            jsonWriter.name("release");
            jsonWriter.beginObject();

            jsonWriter.name("id").value(targetReleaseId);
            jsonWriter.name("release_num").value(targetReleaseNum);

            jsonWriter.endObject(); // end of "release" property.

            jsonWriter.name("processes");
            jsonWriter.beginArray();
            jsonWriter.beginObject();

            jsonWriter.name("order").value(1);
            jsonWriter.name("executable");
            jsonWriter.beginObject();

            String scriptFileName = metadata.getScriptFileName();
            jsonWriter.name("script").value(scriptFileName);

            jsonWriter.endObject(); // end of "executable" property.

            jsonWriter.endObject();
            jsonWriter.endArray(); // end of "processes" property.

            jsonWriter.name("metadata");
            jsonWriter.beginObject();

            jsonWriter.name("releases");
            jsonWriter.beginArray();

            List<String> releaseIdList = new ArrayList(metadata.getReleaseIdNumMap().keySet());
            releaseIdList.remove(targetReleaseId);
            releaseIdList.sort(Comparator.naturalOrder());
            for (String releaseId : releaseIdList) {
                jsonWriter.beginObject();

                jsonWriter.name("id").value(releaseId);
                jsonWriter.name("release_num").value(metadata.getReleaseIdNumMap().get(releaseId));

                jsonWriter.endObject();
            }

            jsonWriter.endArray(); // end of "releases" property.

            jsonWriter.name("tables");
            jsonWriter.beginArray();

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("acc");
            jsonWriter.name("max_id").value(metadata.getMaxAccId().longValue());
            jsonWriter.endObject(); // end of "acc" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("acc_manifest");
            jsonWriter.name("max_id").value(metadata.getMaxAccManifestId().longValue());
            jsonWriter.endObject(); // end of "acc_manifest" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("ascc");
            jsonWriter.name("max_id").value(metadata.getMaxAsccId().longValue());
            jsonWriter.endObject(); // end of "ascc" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("ascc_manifest");
            jsonWriter.name("max_id").value(metadata.getMaxAsccManifestId().longValue());
            jsonWriter.endObject(); // end of "ascc_manifest" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("bcc");
            jsonWriter.name("max_id").value(metadata.getMaxBccId().longValue());
            jsonWriter.endObject(); // end of "bcc" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("bcc_manifest");
            jsonWriter.name("max_id").value(metadata.getMaxBccManifestId().longValue());
            jsonWriter.endObject(); // end of "bcc_manifest" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("asccp");
            jsonWriter.name("max_id").value(metadata.getMaxAsccpId().longValue());
            jsonWriter.endObject(); // end of "asccp" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("asccp_manifest");
            jsonWriter.name("max_id").value(metadata.getMaxAsccpManifestId().longValue());
            jsonWriter.endObject(); // end of "asccp_manifest" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("bccp");
            jsonWriter.name("max_id").value(metadata.getMaxBccpId().longValue());
            jsonWriter.endObject(); // end of "bccp" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("bccp_manifest");
            jsonWriter.name("max_id").value(metadata.getMaxBccpManifestId().longValue());
            jsonWriter.endObject(); // end of "bccp_manifest" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("dt");
            jsonWriter.name("max_id").value(metadata.getMaxDtId().longValue());
            jsonWriter.endObject(); // end of "dt" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("dt_manifest");
            jsonWriter.name("max_id").value(metadata.getMaxDtManifestId().longValue());
            jsonWriter.endObject(); // end of "dt_manifest" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("dt_sc");
            jsonWriter.name("max_id").value(metadata.getMaxDtScId().longValue());
            jsonWriter.endObject(); // end of "dt_sc" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("dt_sc_manifest");
            jsonWriter.name("max_id").value(metadata.getMaxDtScManifestId().longValue());
            jsonWriter.endObject(); // end of "dt_sc_manifest" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("code_list");
            jsonWriter.name("max_id").value(metadata.getMaxCodeListId().longValue());
            jsonWriter.endObject(); // end of "code_list" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("code_list_manifest");
            jsonWriter.name("max_id").value(metadata.getMaxCodeListManifestId().longValue());
            jsonWriter.endObject(); // end of "code_list_manifest" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("code_list_value");
            jsonWriter.name("max_id").value(metadata.getMaxCodeListValueId().longValue());
            jsonWriter.endObject(); // end of "code_list_value" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("code_list_value_manifest");
            jsonWriter.name("max_id").value(metadata.getMaxCodeListValueManifestId().longValue());
            jsonWriter.endObject(); // end of "code_list_value_manifest" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("agency_id_list");
            jsonWriter.name("max_id").value(metadata.getMaxAgencyIdListId().longValue());
            jsonWriter.endObject(); // end of "agency_id_list" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("agency_id_list_manifest");
            jsonWriter.name("max_id").value(metadata.getMaxAgencyIdListManifestId().longValue());
            jsonWriter.endObject(); // end of "agency_id_list_manifest" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("agency_id_list_value");
            jsonWriter.name("max_id").value(metadata.getMaxAgencyIdListValueId().longValue());
            jsonWriter.endObject(); // end of "agency_id_list_value" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("agency_id_list_value_manifest");
            jsonWriter.name("max_id").value(metadata.getMaxAgencyIdListValueManifestId().longValue());
            jsonWriter.endObject(); // end of "agency_id_list_value_manifest" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("xbt_manifest");
            jsonWriter.name("max_id").value(metadata.getMaxXbtManifestId().longValue());
            jsonWriter.endObject(); // end of "xbt_manifest" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("log");
            jsonWriter.name("max_id").value(metadata.getMaxLogId().longValue());
            jsonWriter.endObject(); // end of "log" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("seq_key");
            jsonWriter.name("max_id").value(metadata.getMaxSeqKeyId().longValue());
            jsonWriter.endObject(); // end of "seq_key" table.

            jsonWriter.beginObject();
            jsonWriter.name("table_name").value("namespace");
            jsonWriter.name("max_id").value(metadata.getMaxNamespaceId().longValue());
            jsonWriter.endObject(); // end of "namespace" table.

            jsonWriter.endArray(); // end of "tables" property.

            jsonWriter.endObject(); // end of "metadata" property.

            jsonWriter.endObject(); // end of "migration" property.

            jsonWriter.endObject(); // end of the file.
        }

        return Zip.compression(Arrays.asList(dumpFile, infoFile),
                metadata.getTargetReleaseNum().replaceAll("\\.", "_"));
    }

    private MigrationMetadata makeMigrationMetadata(AppUser user, String releaseId) {
        MigrationMetadata metadata = new MigrationMetadata();
        metadata.setTargetReleaseId(releaseId);
        metadata.setMaintainer(user.getName());
        metadata.setMaintainerEmail(null);

        for (ReleaseRecord releaseRecord : dslContext.selectFrom(RELEASE)
                .where(RELEASE.RELEASE_ID.lessOrEqual(releaseId))
                .fetch()) {
            metadata.addRelease(releaseRecord.getReleaseId(), releaseRecord.getReleaseNum());
        }

        metadata.setMaxAccManifestId(dslContext.select(max(ACC_MANIFEST.ACC_MANIFEST_ID))
                .from(ACC_MANIFEST)
                .where(ACC_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchOneInto(BigInteger.class));
        metadata.setMaxAccId(dslContext.select(max(ACC_MANIFEST.ACC_ID))
                .from(ACC_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxLogId(dslContext.select(max(ACC_MANIFEST.LOG_ID))
                .from(ACC_MANIFEST)
                .fetchOneInto(BigInteger.class));

        metadata.setMaxAsccManifestId(dslContext.select(max(ASCC_MANIFEST.ASCC_MANIFEST_ID))
                .from(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchOneInto(BigInteger.class));
        metadata.setMaxAsccId(dslContext.select(max(ASCC_MANIFEST.ASCC_ID))
                .from(ASCC_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxSeqKeyId(dslContext.select(max(ASCC_MANIFEST.SEQ_KEY_ID))
                .from(ASCC_MANIFEST)
                .fetchOneInto(BigInteger.class));

        metadata.setMaxBccManifestId(dslContext.select(max(BCC_MANIFEST.BCC_MANIFEST_ID))
                .from(BCC_MANIFEST)
                .where(BCC_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchOneInto(BigInteger.class));
        metadata.setMaxBccId(dslContext.select(max(BCC_MANIFEST.BCC_ID))
                .from(BCC_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxSeqKeyId(dslContext.select(max(BCC_MANIFEST.SEQ_KEY_ID))
                .from(BCC_MANIFEST)
                .fetchOneInto(BigInteger.class));

        metadata.setMaxAsccpManifestId(dslContext.select(max(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .from(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchOneInto(BigInteger.class));
        metadata.setMaxAsccpId(dslContext.select(max(ASCCP_MANIFEST.ASCCP_ID))
                .from(ASCCP_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxLogId(dslContext.select(max(ASCCP_MANIFEST.LOG_ID))
                .from(ASCCP_MANIFEST)
                .fetchOneInto(BigInteger.class));

        metadata.setMaxBccpManifestId(dslContext.select(max(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                .from(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchOneInto(BigInteger.class));
        metadata.setMaxBccpId(dslContext.select(max(BCCP_MANIFEST.BCCP_ID))
                .from(BCCP_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxLogId(dslContext.select(max(BCCP_MANIFEST.LOG_ID))
                .from(BCCP_MANIFEST)
                .fetchOneInto(BigInteger.class));

        metadata.setMaxDtManifestId(dslContext.select(max(DT_MANIFEST.DT_MANIFEST_ID))
                .from(DT_MANIFEST)
                .where(DT_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchOneInto(BigInteger.class));
        metadata.setMaxDtId(dslContext.select(max(DT_MANIFEST.DT_ID))
                .from(DT_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxLogId(dslContext.select(max(DT_MANIFEST.LOG_ID))
                .from(DT_MANIFEST)
                .fetchOneInto(BigInteger.class));

        metadata.setMaxDtScManifestId(dslContext.select(max(DT_SC_MANIFEST.DT_SC_MANIFEST_ID))
                .from(DT_SC_MANIFEST)
                .where(DT_SC_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchOneInto(BigInteger.class));
        metadata.setMaxDtScId(dslContext.select(max(DT_SC_MANIFEST.DT_SC_ID))
                .from(DT_SC_MANIFEST)
                .fetchOneInto(BigInteger.class));

        metadata.setMaxCodeListManifestId(dslContext.select(max(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID))
                .from(CODE_LIST_MANIFEST)
                .where(CODE_LIST_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchOneInto(BigInteger.class));
        metadata.setMaxCodeListId(dslContext.select(max(CODE_LIST_MANIFEST.CODE_LIST_ID))
                .from(CODE_LIST_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxLogId(dslContext.select(max(CODE_LIST_MANIFEST.LOG_ID))
                .from(CODE_LIST_MANIFEST)
                .fetchOneInto(BigInteger.class));

        metadata.setMaxCodeListValueManifestId(dslContext.select(max(CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_MANIFEST_ID))
                .from(CODE_LIST_VALUE_MANIFEST)
                .where(CODE_LIST_VALUE_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchOneInto(BigInteger.class));
        metadata.setMaxCodeListValueId(dslContext.select(max(CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_ID))
                .from(CODE_LIST_VALUE_MANIFEST)
                .fetchOneInto(BigInteger.class));

        metadata.setMaxAgencyIdListManifestId(dslContext.select(max(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID))
                .from(AGENCY_ID_LIST_MANIFEST)
                .where(AGENCY_ID_LIST_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchOneInto(BigInteger.class));
        metadata.setMaxAgencyIdListId(dslContext.select(max(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID))
                .from(AGENCY_ID_LIST_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxLogId(dslContext.select(max(AGENCY_ID_LIST_MANIFEST.LOG_ID))
                .from(AGENCY_ID_LIST_MANIFEST)
                .fetchOneInto(BigInteger.class));

        metadata.setMaxAgencyIdListValueManifestId(dslContext.select(max(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_MANIFEST_ID))
                .from(AGENCY_ID_LIST_VALUE_MANIFEST)
                .where(AGENCY_ID_LIST_VALUE_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchOneInto(BigInteger.class));
        metadata.setMaxAgencyIdListValueId(dslContext.select(max(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_ID))
                .from(AGENCY_ID_LIST_VALUE_MANIFEST)
                .fetchOneInto(BigInteger.class));

        metadata.setMaxXbtManifestId(dslContext.select(max(XBT_MANIFEST.XBT_MANIFEST_ID))
                .from(XBT_MANIFEST)
                .where(XBT_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchOneInto(BigInteger.class));

        metadata.setMaxNamespaceId(dslContext.select(max(NAMESPACE.NAMESPACE_ID))
                .from(NAMESPACE)
                .where(NAMESPACE.IS_STD_NMSP.eq((byte) 1))
                .fetchOneInto(BigInteger.class));

        metadata.setMaxModuleSetId(dslContext.select(max(MODULE_SET_RELEASE.MODULE_SET_ID))
                .from(MODULE_SET_RELEASE)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxModuleSetReleaseId(dslContext.select(max(MODULE_SET_RELEASE.MODULE_SET_RELEASE_ID))
                .from(MODULE_SET_RELEASE)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxModuleId(dslContext.select(max(MODULE.MODULE_ID))
                .from(MODULE)
                .fetchOneInto(BigInteger.class));

        metadata.setMaxModuleAccManifestId(dslContext.select(max(MODULE_ACC_MANIFEST.MODULE_ACC_MANIFEST_ID))
                .from(MODULE_ACC_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxModuleAsccpManifestId(dslContext.select(max(MODULE_ASCCP_MANIFEST.MODULE_ASCCP_MANIFEST_ID))
                .from(MODULE_ASCCP_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxModuleBccpManifestId(dslContext.select(max(MODULE_BCCP_MANIFEST.MODULE_BCCP_MANIFEST_ID))
                .from(MODULE_BCCP_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxModuleDtManifestId(dslContext.select(max(MODULE_DT_MANIFEST.MODULE_DT_MANIFEST_ID))
                .from(MODULE_DT_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxModuleAgencyIdListManifestId(dslContext.select(max(MODULE_AGENCY_ID_LIST_MANIFEST.MODULE_AGENCY_ID_LIST_MANIFEST_ID))
                .from(MODULE_AGENCY_ID_LIST_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxModuleCodeListManifestId(dslContext.select(max(MODULE_CODE_LIST_MANIFEST.MODULE_CODE_LIST_MANIFEST_ID))
                .from(MODULE_CODE_LIST_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxModuleXbtManifestId(dslContext.select(max(MODULE_XBT_MANIFEST.MODULE_XBT_MANIFEST_ID))
                .from(MODULE_XBT_MANIFEST)
                .fetchOneInto(BigInteger.class));
        metadata.setMaxModuleBlobContentManifestId(dslContext.select(max(MODULE_BLOB_CONTENT_MANIFEST.MODULE_BLOB_CONTENT_MANIFEST_ID))
                .from(MODULE_BLOB_CONTENT_MANIFEST)
                .fetchOneInto(BigInteger.class));

        return metadata;
    }
}
