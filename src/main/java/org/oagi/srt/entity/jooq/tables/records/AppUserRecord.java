/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.AppUser;


/**
 * This table captures the user information for authentication and authorization
 * purposes.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class AppUserRecord extends UpdatableRecordImpl<AppUserRecord> implements Record6<ULong, String, String, String, String, Byte> {

    private static final long serialVersionUID = 522057442;

    /**
     * Setter for <code>oagi.app_user.app_user_id</code>. Primary key column.
     */
    public void setAppUserId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.app_user.app_user_id</code>. Primary key column.
     */
    public ULong getAppUserId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.app_user.login_id</code>. User Id of the user.
     */
    public void setLoginId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.app_user.login_id</code>. User Id of the user.
     */
    public String getLoginId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.app_user.password</code>. Password to authenticate the user.
     */
    public void setPassword(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.app_user.password</code>. Password to authenticate the user.
     */
    public String getPassword() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.app_user.name</code>. Full name of the user.
     */
    public void setName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.app_user.name</code>. Full name of the user.
     */
    public String getName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.app_user.organization</code>. The company the user represents.
     */
    public void setOrganization(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.app_user.organization</code>. The company the user represents.
     */
    public String getOrganization() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.app_user.is_developer</code>.
     */
    public void setIsDeveloper(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.app_user.is_developer</code>.
     */
    public Byte getIsDeveloper() {
        return (Byte) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<ULong, String, String, String, String, Byte> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<ULong, String, String, String, String, Byte> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return AppUser.APP_USER.APP_USER_ID;
    }

    @Override
    public Field<String> field2() {
        return AppUser.APP_USER.LOGIN_ID;
    }

    @Override
    public Field<String> field3() {
        return AppUser.APP_USER.PASSWORD;
    }

    @Override
    public Field<String> field4() {
        return AppUser.APP_USER.NAME;
    }

    @Override
    public Field<String> field5() {
        return AppUser.APP_USER.ORGANIZATION;
    }

    @Override
    public Field<Byte> field6() {
        return AppUser.APP_USER.IS_DEVELOPER;
    }

    @Override
    public ULong component1() {
        return getAppUserId();
    }

    @Override
    public String component2() {
        return getLoginId();
    }

    @Override
    public String component3() {
        return getPassword();
    }

    @Override
    public String component4() {
        return getName();
    }

    @Override
    public String component5() {
        return getOrganization();
    }

    @Override
    public Byte component6() {
        return getIsDeveloper();
    }

    @Override
    public ULong value1() {
        return getAppUserId();
    }

    @Override
    public String value2() {
        return getLoginId();
    }

    @Override
    public String value3() {
        return getPassword();
    }

    @Override
    public String value4() {
        return getName();
    }

    @Override
    public String value5() {
        return getOrganization();
    }

    @Override
    public Byte value6() {
        return getIsDeveloper();
    }

    @Override
    public AppUserRecord value1(ULong value) {
        setAppUserId(value);
        return this;
    }

    @Override
    public AppUserRecord value2(String value) {
        setLoginId(value);
        return this;
    }

    @Override
    public AppUserRecord value3(String value) {
        setPassword(value);
        return this;
    }

    @Override
    public AppUserRecord value4(String value) {
        setName(value);
        return this;
    }

    @Override
    public AppUserRecord value5(String value) {
        setOrganization(value);
        return this;
    }

    @Override
    public AppUserRecord value6(Byte value) {
        setIsDeveloper(value);
        return this;
    }

    @Override
    public AppUserRecord values(ULong value1, String value2, String value3, String value4, String value5, Byte value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AppUserRecord
     */
    public AppUserRecord() {
        super(AppUser.APP_USER);
    }

    /**
     * Create a detached, initialised AppUserRecord
     */
    public AppUserRecord(ULong appUserId, String loginId, String password, String name, String organization, Byte isDeveloper) {
        super(AppUser.APP_USER);

        set(0, appUserId);
        set(1, loginId);
        set(2, password);
        set(3, name);
        set(4, organization);
        set(5, isDeveloper);
    }
}
