/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Message;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MessageRecord extends UpdatableRecordImpl<MessageRecord> implements Record6<ULong, ULong, ULong, String, Byte, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.message.message_id</code>.
     */
    public void setMessageId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.message.message_id</code>.
     */
    public ULong getMessageId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.message.sender_id</code>. The user who created this record.
     */
    public void setSenderId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.message.sender_id</code>. The user who created this record.
     */
    public ULong getSenderId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.message.recipient_id</code>. The user who is a target to possess this record.
     */
    public void setRecipientId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.message.recipient_id</code>. The user who is a target to possess this record.
     */
    public ULong getRecipientId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.message.body</code>. A body of the message.
     */
    public void setBody(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.message.body</code>. A body of the message.
     */
    public String getBody() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.message.is_read</code>. An indicator whether this record is read or not.
     */
    public void setIsRead(Byte value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.message.is_read</code>. An indicator whether this record is read or not.
     */
    public Byte getIsRead() {
        return (Byte) get(4);
    }

    /**
     * Setter for <code>oagi.message.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.message.creation_timestamp</code>. The timestamp when the record was first created.
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
    public Row6<ULong, ULong, ULong, String, Byte, LocalDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<ULong, ULong, ULong, String, Byte, LocalDateTime> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Message.MESSAGE.MESSAGE_ID;
    }

    @Override
    public Field<ULong> field2() {
        return Message.MESSAGE.SENDER_ID;
    }

    @Override
    public Field<ULong> field3() {
        return Message.MESSAGE.RECIPIENT_ID;
    }

    @Override
    public Field<String> field4() {
        return Message.MESSAGE.BODY;
    }

    @Override
    public Field<Byte> field5() {
        return Message.MESSAGE.IS_READ;
    }

    @Override
    public Field<LocalDateTime> field6() {
        return Message.MESSAGE.CREATION_TIMESTAMP;
    }

    @Override
    public ULong component1() {
        return getMessageId();
    }

    @Override
    public ULong component2() {
        return getSenderId();
    }

    @Override
    public ULong component3() {
        return getRecipientId();
    }

    @Override
    public String component4() {
        return getBody();
    }

    @Override
    public Byte component5() {
        return getIsRead();
    }

    @Override
    public LocalDateTime component6() {
        return getCreationTimestamp();
    }

    @Override
    public ULong value1() {
        return getMessageId();
    }

    @Override
    public ULong value2() {
        return getSenderId();
    }

    @Override
    public ULong value3() {
        return getRecipientId();
    }

    @Override
    public String value4() {
        return getBody();
    }

    @Override
    public Byte value5() {
        return getIsRead();
    }

    @Override
    public LocalDateTime value6() {
        return getCreationTimestamp();
    }

    @Override
    public MessageRecord value1(ULong value) {
        setMessageId(value);
        return this;
    }

    @Override
    public MessageRecord value2(ULong value) {
        setSenderId(value);
        return this;
    }

    @Override
    public MessageRecord value3(ULong value) {
        setRecipientId(value);
        return this;
    }

    @Override
    public MessageRecord value4(String value) {
        setBody(value);
        return this;
    }

    @Override
    public MessageRecord value5(Byte value) {
        setIsRead(value);
        return this;
    }

    @Override
    public MessageRecord value6(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public MessageRecord values(ULong value1, ULong value2, ULong value3, String value4, Byte value5, LocalDateTime value6) {
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
     * Create a detached MessageRecord
     */
    public MessageRecord() {
        super(Message.MESSAGE);
    }

    /**
     * Create a detached, initialised MessageRecord
     */
    public MessageRecord(ULong messageId, ULong senderId, ULong recipientId, String body, Byte isRead, LocalDateTime creationTimestamp) {
        super(Message.MESSAGE);

        setMessageId(messageId);
        setSenderId(senderId);
        setRecipientId(recipientId);
        setBody(body);
        setIsRead(isRead);
        setCreationTimestamp(creationTimestamp);
    }
}
