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
import org.jooq.Index;
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
import org.oagi.score.repo.api.impl.jooq.entity.Indexes;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CommentRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Comment extends TableImpl<CommentRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.comment</code>
     */
    public static final Comment COMMENT = new Comment();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CommentRecord> getRecordType() {
        return CommentRecord.class;
    }

    /**
     * The column <code>oagi.comment.comment_id</code>. Primary, internal
     * database key.
     */
    public final TableField<CommentRecord, String> COMMENT_ID = createField(DSL.name("comment_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.comment.reference</code>.
     */
    public final TableField<CommentRecord, String> REFERENCE = createField(DSL.name("reference"), SQLDataType.VARCHAR(100).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>oagi.comment.comment</code>.
     */
    public final TableField<CommentRecord, String> COMMENT_ = createField(DSL.name("comment"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>oagi.comment.is_hidden</code>.
     */
    public final TableField<CommentRecord, Byte> IS_HIDDEN = createField(DSL.name("is_hidden"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>oagi.comment.is_deleted</code>.
     */
    public final TableField<CommentRecord, Byte> IS_DELETED = createField(DSL.name("is_deleted"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>oagi.comment.prev_comment_id</code>.
     */
    public final TableField<CommentRecord, String> PREV_COMMENT_ID = createField(DSL.name("prev_comment_id"), SQLDataType.CHAR(36), this, "");

    /**
     * The column <code>oagi.comment.created_by</code>.
     */
    public final TableField<CommentRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CHAR(36).nullable(false), this, "");

    /**
     * The column <code>oagi.comment.creation_timestamp</code>.
     */
    public final TableField<CommentRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    /**
     * The column <code>oagi.comment.last_update_timestamp</code>.
     */
    public final TableField<CommentRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    private Comment(Name alias, Table<CommentRecord> aliased) {
        this(alias, aliased, null);
    }

    private Comment(Name alias, Table<CommentRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.comment</code> table reference
     */
    public Comment(String alias) {
        this(DSL.name(alias), COMMENT);
    }

    /**
     * Create an aliased <code>oagi.comment</code> table reference
     */
    public Comment(Name alias) {
        this(alias, COMMENT);
    }

    /**
     * Create a <code>oagi.comment</code> table reference
     */
    public Comment() {
        this(DSL.name("comment"), null);
    }

    public <O extends Record> Comment(Table<O> child, ForeignKey<O, CommentRecord> key) {
        super(child, key, COMMENT);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.COMMENT_REFERENCE);
    }

    @Override
    public UniqueKey<CommentRecord> getPrimaryKey() {
        return Keys.KEY_COMMENT_PRIMARY;
    }

    @Override
    public List<ForeignKey<CommentRecord, ?>> getReferences() {
        return Arrays.asList(Keys.COMMENT_PREV_COMMENT_ID_FK, Keys.COMMENT_CREATED_BY_FK);
    }

    private transient Comment _comment;
    private transient AppUser _appUser;

    /**
     * Get the implicit join path to the <code>oagi.comment</code> table.
     */
    public Comment comment() {
        if (_comment == null)
            _comment = new Comment(this, Keys.COMMENT_PREV_COMMENT_ID_FK);

        return _comment;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table.
     */
    public AppUser appUser() {
        if (_appUser == null)
            _appUser = new AppUser(this, Keys.COMMENT_CREATED_BY_FK);

        return _appUser;
    }

    @Override
    public Comment as(String alias) {
        return new Comment(DSL.name(alias), this);
    }

    @Override
    public Comment as(Name alias) {
        return new Comment(alias, this);
    }

    @Override
    public Comment as(Table<?> alias) {
        return new Comment(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Comment rename(String name) {
        return new Comment(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Comment rename(Name name) {
        return new Comment(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Comment rename(Table<?> name) {
        return new Comment(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<String, String, String, Byte, Byte, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function9<? super String, ? super String, ? super String, ? super Byte, ? super Byte, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function9<? super String, ? super String, ? super String, ? super Byte, ? super Byte, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
