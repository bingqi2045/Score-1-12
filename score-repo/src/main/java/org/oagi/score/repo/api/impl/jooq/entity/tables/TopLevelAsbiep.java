/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function9;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row9;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.TopLevelAsbiepRecord;


/**
 * This table indexes the ASBIEP which is a top-level ASBIEP. This table and the
 * owner_top_level_asbiep_id column in all BIE tables allow all related BIEs to
 * be retrieved all at once speeding up the profile BOD transactions.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TopLevelAsbiep extends TableImpl<TopLevelAsbiepRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.top_level_asbiep</code>
     */
    public static final TopLevelAsbiep TOP_LEVEL_ASBIEP = new TopLevelAsbiep();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TopLevelAsbiepRecord> getRecordType() {
        return TopLevelAsbiepRecord.class;
    }

    /**
     * The column <code>oagi.top_level_asbiep.top_level_asbiep_id</code>.
     * Primary, internal database key.
     */
    public final TableField<TopLevelAsbiepRecord, String> TOP_LEVEL_ASBIEP_ID = createField(DSL.name("top_level_asbiep_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.top_level_asbiep.asbiep_id</code>. Foreign key to
     * the ASBIEP table pointing to a record which is a top-level ASBIEP.
     */
    public final TableField<TopLevelAsbiepRecord, String> ASBIEP_ID = createField(DSL.name("asbiep_id"), SQLDataType.CHAR(36), this, "Foreign key to the ASBIEP table pointing to a record which is a top-level ASBIEP.");

    /**
     * The column <code>oagi.top_level_asbiep.owner_user_id</code>. Foreign key
     * to the APP_USER table identifying the user who can update or delete the
     * record.
     */
    public final TableField<TopLevelAsbiepRecord, String> OWNER_USER_ID = createField(DSL.name("owner_user_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table identifying the user who can update or delete the record.");

    /**
     * The column <code>oagi.top_level_asbiep.last_update_timestamp</code>. The
     * timestamp when among all related bie records was last updated.
     */
    public final TableField<TopLevelAsbiepRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(6)", SQLDataType.LOCALDATETIME)), this, "The timestamp when among all related bie records was last updated.");

    /**
     * The column <code>oagi.top_level_asbiep.last_updated_by</code>. A foreign
     * key referring to the last user who has updated any related bie records.
     */
    public final TableField<TopLevelAsbiepRecord, String> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.CHAR(36).nullable(false), this, "A foreign key referring to the last user who has updated any related bie records.");

    /**
     * The column <code>oagi.top_level_asbiep.release_id</code>. Foreign key to
     * the RELEASE table.
     */
    public final TableField<TopLevelAsbiepRecord, String> RELEASE_ID = createField(DSL.name("release_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the RELEASE table.");

    /**
     * The column <code>oagi.top_level_asbiep.version</code>. This column hold a
     * version number assigned by the user. This column is only used by the
     * top-level ASBIEP. No format of version is enforced.
     */
    public final TableField<TopLevelAsbiepRecord, String> VERSION = createField(DSL.name("version"), SQLDataType.VARCHAR(45), this, "This column hold a version number assigned by the user. This column is only used by the top-level ASBIEP. No format of version is enforced.");

    /**
     * The column <code>oagi.top_level_asbiep.status</code>. This is different
     * from the STATE column which is CRUD life cycle of an entity. The use case
     * for this is to allow the user to indicate the usage status of a top-level
     * ASBIEP (a profile BOD). An integration architect can use this column.
     * Example values are ?Prototype?, ?Test?, and ?Production?. Only the
     * top-level ASBIEP can use this field.
     */
    public final TableField<TopLevelAsbiepRecord, String> STATUS = createField(DSL.name("status"), SQLDataType.VARCHAR(45), this, "This is different from the STATE column which is CRUD life cycle of an entity. The use case for this is to allow the user to indicate the usage status of a top-level ASBIEP (a profile BOD). An integration architect can use this column. Example values are ?Prototype?, ?Test?, and ?Production?. Only the top-level ASBIEP can use this field.");

    /**
     * The column <code>oagi.top_level_asbiep.state</code>.
     */
    public final TableField<TopLevelAsbiepRecord, String> STATE = createField(DSL.name("state"), SQLDataType.VARCHAR(20), this, "");

    private TopLevelAsbiep(Name alias, Table<TopLevelAsbiepRecord> aliased) {
        this(alias, aliased, null);
    }

    private TopLevelAsbiep(Name alias, Table<TopLevelAsbiepRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table indexes the ASBIEP which is a top-level ASBIEP. This table and the owner_top_level_asbiep_id column in all BIE tables allow all related BIEs to be retrieved all at once speeding up the profile BOD transactions."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.top_level_asbiep</code> table reference
     */
    public TopLevelAsbiep(String alias) {
        this(DSL.name(alias), TOP_LEVEL_ASBIEP);
    }

    /**
     * Create an aliased <code>oagi.top_level_asbiep</code> table reference
     */
    public TopLevelAsbiep(Name alias) {
        this(alias, TOP_LEVEL_ASBIEP);
    }

    /**
     * Create a <code>oagi.top_level_asbiep</code> table reference
     */
    public TopLevelAsbiep() {
        this(DSL.name("top_level_asbiep"), null);
    }

    public <O extends Record> TopLevelAsbiep(Table<O> child, ForeignKey<O, TopLevelAsbiepRecord> key) {
        super(child, key, TOP_LEVEL_ASBIEP);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<TopLevelAsbiepRecord> getPrimaryKey() {
        return Keys.KEY_TOP_LEVEL_ASBIEP_PRIMARY;
    }

    @Override
    public List<ForeignKey<TopLevelAsbiepRecord, ?>> getReferences() {
        return Arrays.asList(Keys.TOP_LEVEL_ASBIEP_ASBIEP_ID_FK, Keys.TOP_LEVEL_ASBIEP_OWNER_USER_ID_FK, Keys.TOP_LEVEL_ASBIEP_LAST_UPDATED_BY_FK, Keys.TOP_LEVEL_ASBIEP_RELEASE_ID_FK);
    }

    private transient Asbiep _asbiep;
    private transient AppUser _topLevelAsbiepOwnerUserIdFk;
    private transient AppUser _topLevelAsbiepLastUpdatedByFk;
    private transient Release _release;

    /**
     * Get the implicit join path to the <code>oagi.asbiep</code> table.
     */
    public Asbiep asbiep() {
        if (_asbiep == null)
            _asbiep = new Asbiep(this, Keys.TOP_LEVEL_ASBIEP_ASBIEP_ID_FK);

        return _asbiep;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>top_level_asbiep_owner_user_id_fk</code> key.
     */
    public AppUser topLevelAsbiepOwnerUserIdFk() {
        if (_topLevelAsbiepOwnerUserIdFk == null)
            _topLevelAsbiepOwnerUserIdFk = new AppUser(this, Keys.TOP_LEVEL_ASBIEP_OWNER_USER_ID_FK);

        return _topLevelAsbiepOwnerUserIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>top_level_asbiep_last_updated_by_fk</code> key.
     */
    public AppUser topLevelAsbiepLastUpdatedByFk() {
        if (_topLevelAsbiepLastUpdatedByFk == null)
            _topLevelAsbiepLastUpdatedByFk = new AppUser(this, Keys.TOP_LEVEL_ASBIEP_LAST_UPDATED_BY_FK);

        return _topLevelAsbiepLastUpdatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.release</code> table.
     */
    public Release release() {
        if (_release == null)
            _release = new Release(this, Keys.TOP_LEVEL_ASBIEP_RELEASE_ID_FK);

        return _release;
    }

    @Override
    public TopLevelAsbiep as(String alias) {
        return new TopLevelAsbiep(DSL.name(alias), this);
    }

    @Override
    public TopLevelAsbiep as(Name alias) {
        return new TopLevelAsbiep(alias, this);
    }

    @Override
    public TopLevelAsbiep as(Table<?> alias) {
        return new TopLevelAsbiep(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TopLevelAsbiep rename(String name) {
        return new TopLevelAsbiep(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TopLevelAsbiep rename(Name name) {
        return new TopLevelAsbiep(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TopLevelAsbiep rename(Table<?> name) {
        return new TopLevelAsbiep(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<String, String, String, LocalDateTime, String, String, String, String, String> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function9<? super String, ? super String, ? super String, ? super LocalDateTime, ? super String, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function9<? super String, ? super String, ? super String, ? super LocalDateTime, ? super String, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
