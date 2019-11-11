/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Indexes;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.AppUserRecord;


/**
 * This table captures the user information for authentication and authorization 
 * purposes.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppUser extends TableImpl<AppUserRecord> {

    private static final long serialVersionUID = 417735843;

    /**
     * The reference instance of <code>oagi.app_user</code>
     */
    public static final AppUser APP_USER = new AppUser();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AppUserRecord> getRecordType() {
        return AppUserRecord.class;
    }

    /**
     * The column <code>oagi.app_user.app_user_id</code>. Primary key column.
     */
    public final TableField<AppUserRecord, ULong> APP_USER_ID = createField(DSL.name("app_user_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary key column.");

    /**
     * The column <code>oagi.app_user.login_id</code>. User Id of the user.
     */
    public final TableField<AppUserRecord, String> LOGIN_ID = createField(DSL.name("login_id"), org.jooq.impl.SQLDataType.VARCHAR(45).nullable(false), this, "User Id of the user.");

    /**
     * The column <code>oagi.app_user.password</code>. Password to authenticate the user.
     */
    public final TableField<AppUserRecord, String> PASSWORD = createField(DSL.name("password"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "Password to authenticate the user.");

    /**
     * The column <code>oagi.app_user.name</code>. Full name of the user.
     */
    public final TableField<AppUserRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "Full name of the user.");

    /**
     * The column <code>oagi.app_user.organization</code>. The company the user represents.
     */
    public final TableField<AppUserRecord, String> ORGANIZATION = createField(DSL.name("organization"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "The company the user represents.");

    /**
     * The column <code>oagi.app_user.is_developer</code>.
     */
    public final TableField<AppUserRecord, Byte> IS_DEVELOPER = createField(DSL.name("is_developer"), org.jooq.impl.SQLDataType.TINYINT, this, "");

    /**
     * Create a <code>oagi.app_user</code> table reference
     */
    public AppUser() {
        this(DSL.name("app_user"), null);
    }

    /**
     * Create an aliased <code>oagi.app_user</code> table reference
     */
    public AppUser(String alias) {
        this(DSL.name(alias), APP_USER);
    }

    /**
     * Create an aliased <code>oagi.app_user</code> table reference
     */
    public AppUser(Name alias) {
        this(alias, APP_USER);
    }

    private AppUser(Name alias, Table<AppUserRecord> aliased) {
        this(alias, aliased, null);
    }

    private AppUser(Name alias, Table<AppUserRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table captures the user information for authentication and authorization purposes."));
    }

    public <O extends Record> AppUser(Table<O> child, ForeignKey<O, AppUserRecord> key) {
        super(child, key, APP_USER);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.APP_USER_APP_USER_UK1, Indexes.APP_USER_PRIMARY);
    }

    @Override
    public Identity<AppUserRecord, ULong> getIdentity() {
        return Keys.IDENTITY_APP_USER;
    }

    @Override
    public UniqueKey<AppUserRecord> getPrimaryKey() {
        return Keys.KEY_APP_USER_PRIMARY;
    }

    @Override
    public List<UniqueKey<AppUserRecord>> getKeys() {
        return Arrays.<UniqueKey<AppUserRecord>>asList(Keys.KEY_APP_USER_PRIMARY, Keys.KEY_APP_USER_APP_USER_UK1);
    }

    @Override
    public AppUser as(String alias) {
        return new AppUser(DSL.name(alias), this);
    }

    @Override
    public AppUser as(Name alias) {
        return new AppUser(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public AppUser rename(String name) {
        return new AppUser(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AppUser rename(Name name) {
        return new AppUser(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<ULong, String, String, String, String, Byte> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
