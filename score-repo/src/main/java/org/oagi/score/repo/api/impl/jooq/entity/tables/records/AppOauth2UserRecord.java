/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AppOauth2User;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppOauth2UserRecord extends UpdatableRecordImpl<AppOauth2UserRecord> implements Record10<String, String, String, String, String, String, String, String, String, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.app_oauth2_user.app_oauth2_user_id</code>. Primary,
     * internal database key.
     */
    public void setAppOauth2UserId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.app_oauth2_user.app_oauth2_user_id</code>. Primary,
     * internal database key.
     */
    public String getAppOauth2UserId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.app_oauth2_user.app_user_id</code>. A reference to
     * the record in `app_user`. If it is not set, this is treated as a pending
     * record.
     */
    public void setAppUserId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.app_oauth2_user.app_user_id</code>. A reference to
     * the record in `app_user`. If it is not set, this is treated as a pending
     * record.
     */
    public String getAppUserId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.app_oauth2_user.oauth2_app_id</code>. A reference
     * to the record in `oauth2_app`.
     */
    public void setOauth2AppId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.app_oauth2_user.oauth2_app_id</code>. A reference
     * to the record in `oauth2_app`.
     */
    public String getOauth2AppId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.app_oauth2_user.sub</code>. `sub` claim defined in
     * OIDC spec. This is a unique identifier of the subject in the provider.
     */
    public void setSub(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.app_oauth2_user.sub</code>. `sub` claim defined in
     * OIDC spec. This is a unique identifier of the subject in the provider.
     */
    public String getSub() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.app_oauth2_user.name</code>. `name` claim defined
     * in OIDC spec.
     */
    public void setName(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.app_oauth2_user.name</code>. `name` claim defined
     * in OIDC spec.
     */
    public String getName() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.app_oauth2_user.email</code>. `email` claim defined
     * in OIDC spec.
     */
    public void setEmail(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.app_oauth2_user.email</code>. `email` claim defined
     * in OIDC spec.
     */
    public String getEmail() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.app_oauth2_user.nickname</code>. `nickname` claim
     * defined in OIDC spec.
     */
    public void setNickname(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.app_oauth2_user.nickname</code>. `nickname` claim
     * defined in OIDC spec.
     */
    public String getNickname() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.app_oauth2_user.preferred_username</code>.
     * `preferred_username` claim defined in OIDC spec.
     */
    public void setPreferredUsername(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.app_oauth2_user.preferred_username</code>.
     * `preferred_username` claim defined in OIDC spec.
     */
    public String getPreferredUsername() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.app_oauth2_user.phone_number</code>. `phone_number`
     * claim defined in OIDC spec.
     */
    public void setPhoneNumber(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.app_oauth2_user.phone_number</code>. `phone_number`
     * claim defined in OIDC spec.
     */
    public String getPhoneNumber() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.app_oauth2_user.creation_timestamp</code>.
     * Timestamp when this record is created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.app_oauth2_user.creation_timestamp</code>.
     * Timestamp when this record is created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(9);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row10<String, String, String, String, String, String, String, String, String, LocalDateTime> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    @Override
    public Row10<String, String, String, String, String, String, String, String, String, LocalDateTime> valuesRow() {
        return (Row10) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return AppOauth2User.APP_OAUTH2_USER.APP_OAUTH2_USER_ID;
    }

    @Override
    public Field<String> field2() {
        return AppOauth2User.APP_OAUTH2_USER.APP_USER_ID;
    }

    @Override
    public Field<String> field3() {
        return AppOauth2User.APP_OAUTH2_USER.OAUTH2_APP_ID;
    }

    @Override
    public Field<String> field4() {
        return AppOauth2User.APP_OAUTH2_USER.SUB;
    }

    @Override
    public Field<String> field5() {
        return AppOauth2User.APP_OAUTH2_USER.NAME;
    }

    @Override
    public Field<String> field6() {
        return AppOauth2User.APP_OAUTH2_USER.EMAIL;
    }

    @Override
    public Field<String> field7() {
        return AppOauth2User.APP_OAUTH2_USER.NICKNAME;
    }

    @Override
    public Field<String> field8() {
        return AppOauth2User.APP_OAUTH2_USER.PREFERRED_USERNAME;
    }

    @Override
    public Field<String> field9() {
        return AppOauth2User.APP_OAUTH2_USER.PHONE_NUMBER;
    }

    @Override
    public Field<LocalDateTime> field10() {
        return AppOauth2User.APP_OAUTH2_USER.CREATION_TIMESTAMP;
    }

    @Override
    public String component1() {
        return getAppOauth2UserId();
    }

    @Override
    public String component2() {
        return getAppUserId();
    }

    @Override
    public String component3() {
        return getOauth2AppId();
    }

    @Override
    public String component4() {
        return getSub();
    }

    @Override
    public String component5() {
        return getName();
    }

    @Override
    public String component6() {
        return getEmail();
    }

    @Override
    public String component7() {
        return getNickname();
    }

    @Override
    public String component8() {
        return getPreferredUsername();
    }

    @Override
    public String component9() {
        return getPhoneNumber();
    }

    @Override
    public LocalDateTime component10() {
        return getCreationTimestamp();
    }

    @Override
    public String value1() {
        return getAppOauth2UserId();
    }

    @Override
    public String value2() {
        return getAppUserId();
    }

    @Override
    public String value3() {
        return getOauth2AppId();
    }

    @Override
    public String value4() {
        return getSub();
    }

    @Override
    public String value5() {
        return getName();
    }

    @Override
    public String value6() {
        return getEmail();
    }

    @Override
    public String value7() {
        return getNickname();
    }

    @Override
    public String value8() {
        return getPreferredUsername();
    }

    @Override
    public String value9() {
        return getPhoneNumber();
    }

    @Override
    public LocalDateTime value10() {
        return getCreationTimestamp();
    }

    @Override
    public AppOauth2UserRecord value1(String value) {
        setAppOauth2UserId(value);
        return this;
    }

    @Override
    public AppOauth2UserRecord value2(String value) {
        setAppUserId(value);
        return this;
    }

    @Override
    public AppOauth2UserRecord value3(String value) {
        setOauth2AppId(value);
        return this;
    }

    @Override
    public AppOauth2UserRecord value4(String value) {
        setSub(value);
        return this;
    }

    @Override
    public AppOauth2UserRecord value5(String value) {
        setName(value);
        return this;
    }

    @Override
    public AppOauth2UserRecord value6(String value) {
        setEmail(value);
        return this;
    }

    @Override
    public AppOauth2UserRecord value7(String value) {
        setNickname(value);
        return this;
    }

    @Override
    public AppOauth2UserRecord value8(String value) {
        setPreferredUsername(value);
        return this;
    }

    @Override
    public AppOauth2UserRecord value9(String value) {
        setPhoneNumber(value);
        return this;
    }

    @Override
    public AppOauth2UserRecord value10(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public AppOauth2UserRecord values(String value1, String value2, String value3, String value4, String value5, String value6, String value7, String value8, String value9, LocalDateTime value10) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AppOauth2UserRecord
     */
    public AppOauth2UserRecord() {
        super(AppOauth2User.APP_OAUTH2_USER);
    }

    /**
     * Create a detached, initialised AppOauth2UserRecord
     */
    public AppOauth2UserRecord(String appOauth2UserId, String appUserId, String oauth2AppId, String sub, String name, String email, String nickname, String preferredUsername, String phoneNumber, LocalDateTime creationTimestamp) {
        super(AppOauth2User.APP_OAUTH2_USER);

        setAppOauth2UserId(appOauth2UserId);
        setAppUserId(appUserId);
        setOauth2AppId(oauth2AppId);
        setSub(sub);
        setName(name);
        setEmail(email);
        setNickname(nickname);
        setPreferredUsername(preferredUsername);
        setPhoneNumber(phoneNumber);
        setCreationTimestamp(creationTimestamp);
    }
}
