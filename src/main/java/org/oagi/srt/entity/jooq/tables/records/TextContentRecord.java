/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.TextContent;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TextContentRecord extends UpdatableRecordImpl<TextContentRecord> implements Record3<ULong, String, String> {

    private static final long serialVersionUID = 1182509174;

    /**
     * Setter for <code>oagi.text_content.text_content_id</code>.
     */
    public void setTextContentId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.text_content.text_content_id</code>.
     */
    public ULong getTextContentId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.text_content.text_content_type</code>.
     */
    public void setTextContentType(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.text_content.text_content_type</code>.
     */
    public String getTextContentType() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.text_content.text_content</code>.
     */
    public void setTextContent(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.text_content.text_content</code>.
     */
    public String getTextContent() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<ULong, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<ULong, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return TextContent.TEXT_CONTENT.TEXT_CONTENT_ID;
    }

    @Override
    public Field<String> field2() {
        return TextContent.TEXT_CONTENT.TEXT_CONTENT_TYPE;
    }

    @Override
    public Field<String> field3() {
        return TextContent.TEXT_CONTENT.TEXT_CONTENT_;
    }

    @Override
    public ULong component1() {
        return getTextContentId();
    }

    @Override
    public String component2() {
        return getTextContentType();
    }

    @Override
    public String component3() {
        return getTextContent();
    }

    @Override
    public ULong value1() {
        return getTextContentId();
    }

    @Override
    public String value2() {
        return getTextContentType();
    }

    @Override
    public String value3() {
        return getTextContent();
    }

    @Override
    public TextContentRecord value1(ULong value) {
        setTextContentId(value);
        return this;
    }

    @Override
    public TextContentRecord value2(String value) {
        setTextContentType(value);
        return this;
    }

    @Override
    public TextContentRecord value3(String value) {
        setTextContent(value);
        return this;
    }

    @Override
    public TextContentRecord values(ULong value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TextContentRecord
     */
    public TextContentRecord() {
        super(TextContent.TEXT_CONTENT);
    }

    /**
     * Create a detached, initialised TextContentRecord
     */
    public TextContentRecord(ULong textContentId, String textContentType, String textContent) {
        super(TextContent.TEXT_CONTENT);

        set(0, textContentId);
        set(1, textContentType);
        set(2, textContent);
    }
}
