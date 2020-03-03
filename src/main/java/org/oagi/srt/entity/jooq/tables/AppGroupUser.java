/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Indexes;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.AppGroupUserRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppGroupUser extends TableImpl<AppGroupUserRecord> {

    private static final long serialVersionUID = -582932654;

    /**
     * The reference instance of <code>oagi.app_group_user</code>
     */
    public static final AppGroupUser APP_GROUP_USER = new AppGroupUser();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AppGroupUserRecord> getRecordType() {
        return AppGroupUserRecord.class;
    }

    /**
     * The column <code>oagi.app_group_user.app_group_id</code>.
     */
    public final TableField<AppGroupUserRecord, ULong> APP_GROUP_ID = createField(DSL.name("app_group_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.app_group_user.app_user_id</code>.
     */
    public final TableField<AppGroupUserRecord, ULong> APP_USER_ID = createField(DSL.name("app_user_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * Create a <code>oagi.app_group_user</code> table reference
     */
    public AppGroupUser() {
        this(DSL.name("app_group_user"), null);
    }

    /**
     * Create an aliased <code>oagi.app_group_user</code> table reference
     */
    public AppGroupUser(String alias) {
        this(DSL.name(alias), APP_GROUP_USER);
    }

    /**
     * Create an aliased <code>oagi.app_group_user</code> table reference
     */
    public AppGroupUser(Name alias) {
        this(alias, APP_GROUP_USER);
    }

    private AppGroupUser(Name alias, Table<AppGroupUserRecord> aliased) {
        this(alias, aliased, null);
    }

    private AppGroupUser(Name alias, Table<AppGroupUserRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> AppGroupUser(Table<O> child, ForeignKey<O, AppGroupUserRecord> key) {
        super(child, key, APP_GROUP_USER);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.APP_GROUP_USER_APP_USER_ID);
    }

    @Override
    public UniqueKey<AppGroupUserRecord> getPrimaryKey() {
        return Keys.KEY_APP_GROUP_USER_PRIMARY;
    }

    @Override
    public List<UniqueKey<AppGroupUserRecord>> getKeys() {
        return Arrays.<UniqueKey<AppGroupUserRecord>>asList(Keys.KEY_APP_GROUP_USER_PRIMARY);
    }

    @Override
    public List<ForeignKey<AppGroupUserRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<AppGroupUserRecord, ?>>asList(Keys.APP_GROUP_USER_FK_1, Keys.APP_GROUP_USER_FK_2);
    }

    public AppGroup appGroup() {
        return new AppGroup(this, Keys.APP_GROUP_USER_FK_1);
    }

    public AppUser appUser() {
        return new AppUser(this, Keys.APP_GROUP_USER_FK_2);
    }

    @Override
    public AppGroupUser as(String alias) {
        return new AppGroupUser(DSL.name(alias), this);
    }

    @Override
    public AppGroupUser as(Name alias) {
        return new AppGroupUser(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public AppGroupUser rename(String name) {
        return new AppGroupUser(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AppGroupUser rename(Name name) {
        return new AppGroupUser(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<ULong, ULong> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
