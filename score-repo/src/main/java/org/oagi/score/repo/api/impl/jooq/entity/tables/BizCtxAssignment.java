/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row3;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BizCtxAssignmentRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BizCtxAssignment extends TableImpl<BizCtxAssignmentRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.biz_ctx_assignment</code>
     */
    public static final BizCtxAssignment BIZ_CTX_ASSIGNMENT = new BizCtxAssignment();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BizCtxAssignmentRecord> getRecordType() {
        return BizCtxAssignmentRecord.class;
    }

    /**
     * The column <code>oagi.biz_ctx_assignment.biz_ctx_assignment_id</code>.
     * Primary, internal database key.
     */
    public final TableField<BizCtxAssignmentRecord, String> BIZ_CTX_ASSIGNMENT_ID = createField(DSL.name("biz_ctx_assignment_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.biz_ctx_assignment.biz_ctx_id</code>. Foreign key
     * to the biz_ctx table.
     */
    public final TableField<BizCtxAssignmentRecord, String> BIZ_CTX_ID = createField(DSL.name("biz_ctx_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the biz_ctx table.");

    /**
     * The column <code>oagi.biz_ctx_assignment.top_level_asbiep_id</code>. This
     * is a foreign key to the top-level ASBIEP.
     */
    public final TableField<BizCtxAssignmentRecord, ULong> TOP_LEVEL_ASBIEP_ID = createField(DSL.name("top_level_asbiep_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "This is a foreign key to the top-level ASBIEP.");

    private BizCtxAssignment(Name alias, Table<BizCtxAssignmentRecord> aliased) {
        this(alias, aliased, null);
    }

    private BizCtxAssignment(Name alias, Table<BizCtxAssignmentRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.biz_ctx_assignment</code> table reference
     */
    public BizCtxAssignment(String alias) {
        this(DSL.name(alias), BIZ_CTX_ASSIGNMENT);
    }

    /**
     * Create an aliased <code>oagi.biz_ctx_assignment</code> table reference
     */
    public BizCtxAssignment(Name alias) {
        this(alias, BIZ_CTX_ASSIGNMENT);
    }

    /**
     * Create a <code>oagi.biz_ctx_assignment</code> table reference
     */
    public BizCtxAssignment() {
        this(DSL.name("biz_ctx_assignment"), null);
    }

    public <O extends Record> BizCtxAssignment(Table<O> child, ForeignKey<O, BizCtxAssignmentRecord> key) {
        super(child, key, BIZ_CTX_ASSIGNMENT);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<BizCtxAssignmentRecord> getPrimaryKey() {
        return Keys.KEY_BIZ_CTX_ASSIGNMENT_PRIMARY;
    }

    @Override
    public List<ForeignKey<BizCtxAssignmentRecord, ?>> getReferences() {
        return Arrays.asList(Keys.BIZ_CTX_ASSIGNMENT_BIZ_CTX_ID_FK, Keys.BIZ_CTX_ASSIGNMENT_TOP_LEVEL_ASBIEP_ID_FK);
    }

    private transient BizCtx _bizCtx;
    private transient TopLevelAsbiep _topLevelAsbiep;

    /**
     * Get the implicit join path to the <code>oagi.biz_ctx</code> table.
     */
    public BizCtx bizCtx() {
        if (_bizCtx == null)
            _bizCtx = new BizCtx(this, Keys.BIZ_CTX_ASSIGNMENT_BIZ_CTX_ID_FK);

        return _bizCtx;
    }

    /**
     * Get the implicit join path to the <code>oagi.top_level_asbiep</code>
     * table.
     */
    public TopLevelAsbiep topLevelAsbiep() {
        if (_topLevelAsbiep == null)
            _topLevelAsbiep = new TopLevelAsbiep(this, Keys.BIZ_CTX_ASSIGNMENT_TOP_LEVEL_ASBIEP_ID_FK);

        return _topLevelAsbiep;
    }

    @Override
    public BizCtxAssignment as(String alias) {
        return new BizCtxAssignment(DSL.name(alias), this);
    }

    @Override
    public BizCtxAssignment as(Name alias) {
        return new BizCtxAssignment(alias, this);
    }

    @Override
    public BizCtxAssignment as(Table<?> alias) {
        return new BizCtxAssignment(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public BizCtxAssignment rename(String name) {
        return new BizCtxAssignment(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BizCtxAssignment rename(Name name) {
        return new BizCtxAssignment(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public BizCtxAssignment rename(Table<?> name) {
        return new BizCtxAssignment(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, ULong> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super String, ? super String, ? super ULong, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super String, ? super String, ? super ULong, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
