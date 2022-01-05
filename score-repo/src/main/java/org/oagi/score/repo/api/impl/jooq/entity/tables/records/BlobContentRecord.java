/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BlobContent;


/**
 * This table stores schemas whose content is only imported as a whole and is
 * represented in Blob.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BlobContentRecord extends UpdatableRecordImpl<BlobContentRecord> implements Record2<ULong, byte[]> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.blob_content.blob_content_id</code>. Primary,
     * internal database key.
     */
    public void setBlobContentId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.blob_content.blob_content_id</code>. Primary,
     * internal database key.
     */
    public ULong getBlobContentId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.blob_content.content</code>. The Blob content of
     * the schema file.
     */
    public void setContent(byte[] value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.blob_content.content</code>. The Blob content of
     * the schema file.
     */
    public byte[] getContent() {
        return (byte[]) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<ULong, byte[]> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<ULong, byte[]> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return BlobContent.BLOB_CONTENT.BLOB_CONTENT_ID;
    }

    @Override
    public Field<byte[]> field2() {
        return BlobContent.BLOB_CONTENT.CONTENT;
    }

    @Override
    public ULong component1() {
        return getBlobContentId();
    }

    @Override
    public byte[] component2() {
        return getContent();
    }

    @Override
    public ULong value1() {
        return getBlobContentId();
    }

    @Override
    public byte[] value2() {
        return getContent();
    }

    @Override
    public BlobContentRecord value1(ULong value) {
        setBlobContentId(value);
        return this;
    }

    @Override
    public BlobContentRecord value2(byte[] value) {
        setContent(value);
        return this;
    }

    @Override
    public BlobContentRecord values(ULong value1, byte[] value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BlobContentRecord
     */
    public BlobContentRecord() {
        super(BlobContent.BLOB_CONTENT);
    }

    /**
     * Create a detached, initialised BlobContentRecord
     */
    public BlobContentRecord(ULong blobContentId, byte[] content) {
        super(BlobContent.BLOB_CONTENT);

        setBlobContentId(blobContentId);
        setContent(content);
    }
}
