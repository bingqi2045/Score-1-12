/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.BlobContent;


/**
 * This table stores schemas whose content is only imported as a whole and 
 * is represented in Blob.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BlobContentRecord extends UpdatableRecordImpl<BlobContentRecord> implements Record4<ULong, byte[], ULong, ULong> {

    private static final long serialVersionUID = -1001175673;

    /**
     * Setter for <code>oagi.blob_content.blob_content_id</code>. Primary, internal database key.
     */
    public void setBlobContentId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.blob_content.blob_content_id</code>. Primary, internal database key.
     */
    public ULong getBlobContentId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.blob_content.content</code>. The Blob content of the schema file.
     */
    public void setContent(byte... value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.blob_content.content</code>. The Blob content of the schema file.
     */
    public byte[] getContent() {
        return (byte[]) get(1);
    }

    /**
     * Setter for <code>oagi.blob_content.release_id</code>. The release to which this file/content belongs/published.
     */
    public void setReleaseId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.blob_content.release_id</code>. The release to which this file/content belongs/published.
     */
    public ULong getReleaseId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.blob_content.module_id</code>. Foreign key to the module table indicating the physical file the blob content should be output to when generating/serializing the content.
     */
    public void setModuleId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.blob_content.module_id</code>. Foreign key to the module table indicating the physical file the blob content should be output to when generating/serializing the content.
     */
    public ULong getModuleId() {
        return (ULong) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<ULong, byte[], ULong, ULong> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<ULong, byte[], ULong, ULong> valuesRow() {
        return (Row4) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field1() {
        return BlobContent.BLOB_CONTENT.BLOB_CONTENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<byte[]> field2() {
        return BlobContent.BLOB_CONTENT.CONTENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field3() {
        return BlobContent.BLOB_CONTENT.RELEASE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field4() {
        return BlobContent.BLOB_CONTENT.MODULE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component1() {
        return getBlobContentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] component2() {
        return getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component3() {
        return getReleaseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component4() {
        return getModuleId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value1() {
        return getBlobContentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] value2() {
        return getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value3() {
        return getReleaseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value4() {
        return getModuleId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlobContentRecord value1(ULong value) {
        setBlobContentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlobContentRecord value2(byte... value) {
        setContent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlobContentRecord value3(ULong value) {
        setReleaseId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlobContentRecord value4(ULong value) {
        setModuleId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlobContentRecord values(ULong value1, byte[] value2, ULong value3, ULong value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
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
    public BlobContentRecord(ULong blobContentId, byte[] content, ULong releaseId, ULong moduleId) {
        super(BlobContent.BLOB_CONTENT);

        set(0, blobContentId);
        set(1, content);
        set(2, releaseId);
        set(3, moduleId);
    }
}
