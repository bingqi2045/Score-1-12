/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.e2e.impl.api.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.e2e.impl.api.jooq.entity.tables.Exception;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ExceptionRecord extends UpdatableRecordImpl<ExceptionRecord> implements Record6<ULong, String, String, byte[], ULong, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.exception.exception_id</code>. Internal, primary
     * database key.
     */
    public void setExceptionId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.exception.exception_id</code>. Internal, primary
     * database key.
     */
    public ULong getExceptionId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.exception.tag</code>. A tag of the exception for
     * the purpose of the searching facilitation
     */
    public void setTag(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.exception.tag</code>. A tag of the exception for
     * the purpose of the searching facilitation
     */
    public String getTag() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.exception.message</code>. The exception message.
     */
    public void setMessage(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.exception.message</code>. The exception message.
     */
    public String getMessage() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.exception.stacktrace</code>. The serialized
     * stacktrace object.
     */
    public void setStacktrace(byte[] value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.exception.stacktrace</code>. The serialized
     * stacktrace object.
     */
    public byte[] getStacktrace() {
        return (byte[]) get(3);
    }

    /**
     * Setter for <code>oagi.exception.created_by</code>. Foreign key to the
     * APP_USER table. It indicates the user who is working on when the
     * exception occurs.
     */
    public void setCreatedBy(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.exception.created_by</code>. Foreign key to the
     * APP_USER table. It indicates the user who is working on when the
     * exception occurs.
     */
    public ULong getCreatedBy() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.exception.creation_timestamp</code>. Timestamp when
     * the exception was created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.exception.creation_timestamp</code>. Timestamp when
     * the exception was created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(5);
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
    public Row6<ULong, String, String, byte[], ULong, LocalDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<ULong, String, String, byte[], ULong, LocalDateTime> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Exception.EXCEPTION.EXCEPTION_ID;
    }

    @Override
    public Field<String> field2() {
        return Exception.EXCEPTION.TAG;
    }

    @Override
    public Field<String> field3() {
        return Exception.EXCEPTION.MESSAGE;
    }

    @Override
    public Field<byte[]> field4() {
        return Exception.EXCEPTION.STACKTRACE;
    }

    @Override
    public Field<ULong> field5() {
        return Exception.EXCEPTION.CREATED_BY;
    }

    @Override
    public Field<LocalDateTime> field6() {
        return Exception.EXCEPTION.CREATION_TIMESTAMP;
    }

    @Override
    public ULong component1() {
        return getExceptionId();
    }

    @Override
    public String component2() {
        return getTag();
    }

    @Override
    public String component3() {
        return getMessage();
    }

    @Override
    public byte[] component4() {
        return getStacktrace();
    }

    @Override
    public ULong component5() {
        return getCreatedBy();
    }

    @Override
    public LocalDateTime component6() {
        return getCreationTimestamp();
    }

    @Override
    public ULong value1() {
        return getExceptionId();
    }

    @Override
    public String value2() {
        return getTag();
    }

    @Override
    public String value3() {
        return getMessage();
    }

    @Override
    public byte[] value4() {
        return getStacktrace();
    }

    @Override
    public ULong value5() {
        return getCreatedBy();
    }

    @Override
    public LocalDateTime value6() {
        return getCreationTimestamp();
    }

    @Override
    public ExceptionRecord value1(ULong value) {
        setExceptionId(value);
        return this;
    }

    @Override
    public ExceptionRecord value2(String value) {
        setTag(value);
        return this;
    }

    @Override
    public ExceptionRecord value3(String value) {
        setMessage(value);
        return this;
    }

    @Override
    public ExceptionRecord value4(byte[] value) {
        setStacktrace(value);
        return this;
    }

    @Override
    public ExceptionRecord value5(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public ExceptionRecord value6(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public ExceptionRecord values(ULong value1, String value2, String value3, byte[] value4, ULong value5, LocalDateTime value6) {
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
     * Create a detached ExceptionRecord
     */
    public ExceptionRecord() {
        super(Exception.EXCEPTION);
    }

    /**
     * Create a detached, initialised ExceptionRecord
     */
    public ExceptionRecord(ULong exceptionId, String tag, String message, byte[] stacktrace, ULong createdBy, LocalDateTime creationTimestamp) {
        super(Exception.EXCEPTION);

        setExceptionId(exceptionId);
        setTag(tag);
        setMessage(message);
        setStacktrace(stacktrace);
        setCreatedBy(createdBy);
        setCreationTimestamp(creationTimestamp);
        resetChangedOnNotNull();
    }
}
