/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function8;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row8;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AppUserRecord;


/**
 * This table captures the user information for authentication and authorization
 * purposes.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppUser extends TableImpl<AppUserRecord> {

    private static final long serialVersionUID = 1L;

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
    public final TableField<AppUserRecord, ULong> APP_USER_ID = createField(DSL.name("app_user_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary key column.");

    /**
     * The column <code>oagi.app_user.login_id</code>. User Id of the user.
     */
    public final TableField<AppUserRecord, String> LOGIN_ID = createField(DSL.name("login_id"), SQLDataType.VARCHAR(45).nullable(false), this, "User Id of the user.");

    /**
     * The column <code>oagi.app_user.password</code>. Password to authenticate
     * the user.
     */
    public final TableField<AppUserRecord, String> PASSWORD = createField(DSL.name("password"), SQLDataType.VARCHAR(100), this, "Password to authenticate the user.");

    /**
     * The column <code>oagi.app_user.name</code>. Full name of the user.
     */
    public final TableField<AppUserRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(100), this, "Full name of the user.");

    /**
     * The column <code>oagi.app_user.organization</code>. The company the user
     * represents.
     */
    public final TableField<AppUserRecord, String> ORGANIZATION = createField(DSL.name("organization"), SQLDataType.VARCHAR(100), this, "The company the user represents.");

    /**
     * The column <code>oagi.app_user.is_developer</code>.
     */
    public final TableField<AppUserRecord, Byte> IS_DEVELOPER = createField(DSL.name("is_developer"), SQLDataType.TINYINT, this, "");

    /**
     * The column <code>oagi.app_user.is_admin</code>. Indicator whether the
     * user has an admin role or not.
     */
    public final TableField<AppUserRecord, Byte> IS_ADMIN = createField(DSL.name("is_admin"), SQLDataType.TINYINT.defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "Indicator whether the user has an admin role or not.");

    /**
     * The column <code>oagi.app_user.is_enabled</code>.
     */
    public final TableField<AppUserRecord, Byte> IS_ENABLED = createField(DSL.name("is_enabled"), SQLDataType.TINYINT.defaultValue(DSL.inline("1", SQLDataType.TINYINT)), this, "");

    private AppUser(Name alias, Table<AppUserRecord> aliased) {
        this(alias, aliased, null);
    }

    private AppUser(Name alias, Table<AppUserRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table captures the user information for authentication and authorization purposes."), TableOptions.table());
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

    /**
     * Create a <code>oagi.app_user</code> table reference
     */
    public AppUser() {
        this(DSL.name("app_user"), null);
    }

    public <O extends Record> AppUser(Table<O> child, ForeignKey<O, AppUserRecord> key) {
        super(child, key, APP_USER);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<AppUserRecord, ULong> getIdentity() {
        return (Identity<AppUserRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<AppUserRecord> getPrimaryKey() {
        return Keys.KEY_APP_USER_PRIMARY;
    }

    @Override
    public List<UniqueKey<AppUserRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.KEY_APP_USER_APP_USER_UK1);
    }

    @Override
    public AppUser as(String alias) {
        return new AppUser(DSL.name(alias), this);
    }

    @Override
    public AppUser as(Name alias) {
        return new AppUser(alias, this);
    }

    @Override
    public AppUser as(Table<?> alias) {
        return new AppUser(alias.getQualifiedName(), this);
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

    /**
     * Rename this table
     */
    @Override
    public AppUser rename(Table<?> name) {
        return new AppUser(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<ULong, String, String, String, String, Byte, Byte, Byte> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function8<? super ULong, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super Byte, ? super Byte, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function8<? super ULong, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super Byte, ? super Byte, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
