package org.oagi.srt.cache;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;

@Transactional(readOnly = true)
public class DatabaseCacheHandler<T> implements InitializingBean {

    private final String tableName;
    private final Class<T> mappedClass;

    private String camelCasePriKeyName;
    private String underscorePriKeyName;

    public DatabaseCacheHandler(String tableName, Class<T> mappedClass) {
        this.tableName = tableName;
        this.mappedClass = mappedClass;

        setPrimaryKeyName(this.tableName + "_id");
    }

    public void setPrimaryKeyName(String primaryKeyName) {
        this.underscorePriKeyName = primaryKeyName;
        this.camelCasePriKeyName = underscoreToCamelCase(primaryKeyName);
    }

    public String underscoreToCamelCase(String string) {
        String str = Arrays.asList(string.split("_")).stream()
                .map(e -> Character.toUpperCase(e.charAt(0)) + e.substring(1).toLowerCase())
                .collect(Collectors.joining(""));
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public String getTableName() {
        return this.tableName;
    }

    public Class<T> getMappedClass() {
        return mappedClass;
    }

    public String getCamelCasePriKeyName() {
        return camelCasePriKeyName;
    }

    public void setCamelCasePriKeyName(String camelCasePriKeyName) {
        this.camelCasePriKeyName = camelCasePriKeyName;
    }

    public String getUnderscorePriKeyName() {
        return underscorePriKeyName;
    }

    public void setUnderscorePriKeyName(String underscorePriKeyName) {
        this.underscorePriKeyName = underscorePriKeyName;
    }

    @Override
    public void afterPropertiesSet() {
    }

    private List<String> loadFields(String tableName) {
        List<String> fields = new ArrayList<>();
        Table<?> table = table(name(tableName));
        for(Field field : table.fields()){
            fields.add(field.getName());
        };
        return fields;
    }

    public String getChecksumQuery() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ").append(this.underscorePriKeyName)
                .append(", sha1(concat_ws(`").append(String.join("`,`", loadFields(this.tableName)))
                .append("`)) `checksum` FROM ").append(this.tableName);
        return query.toString();
    }

    public String getChecksumByIdQuery() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT sha1(concat_ws(`").append(String.join("`,`", loadFields(this.tableName)))
                .append("`)) `checksum` FROM ").append(this.tableName).append(" WHERE ")
                .append(this.underscorePriKeyName).append(" = ");
        return query.toString();
    }

}
