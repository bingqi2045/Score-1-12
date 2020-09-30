/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ClientRecord;


/**
 * This table captures a client organization. It is used, for example, to 
 * indicate the customer, for which the BIE was generated.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Client extends TableImpl<ClientRecord> {

    private static final long serialVersionUID = 30961187;

    /**
     * The reference instance of <code>oagi.client</code>
     */
    public static final Client CLIENT = new Client();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ClientRecord> getRecordType() {
        return ClientRecord.class;
    }

    /**
     * The column <code>oagi.client.client_id</code>. Primary, internal database key.
     */
    public final TableField<ClientRecord, ULong> CLIENT_ID = createField(DSL.name("client_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.client.name</code>. Pretty print name of the client.
     */
    public final TableField<ClientRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR(200), this, "Pretty print name of the client.");

    /**
     * Create a <code>oagi.client</code> table reference
     */
    public Client() {
        this(DSL.name("client"), null);
    }

    /**
     * Create an aliased <code>oagi.client</code> table reference
     */
    public Client(String alias) {
        this(DSL.name(alias), CLIENT);
    }

    /**
     * Create an aliased <code>oagi.client</code> table reference
     */
    public Client(Name alias) {
        this(alias, CLIENT);
    }

    private Client(Name alias, Table<ClientRecord> aliased) {
        this(alias, aliased, null);
    }

    private Client(Name alias, Table<ClientRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table captures a client organization. It is used, for example, to indicate the customer, for which the BIE was generated."), TableOptions.table());
    }

    public <O extends Record> Client(Table<O> child, ForeignKey<O, ClientRecord> key) {
        super(child, key, CLIENT);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<ClientRecord, ULong> getIdentity() {
        return Keys.IDENTITY_CLIENT;
    }

    @Override
    public UniqueKey<ClientRecord> getPrimaryKey() {
        return Keys.KEY_CLIENT_PRIMARY;
    }

    @Override
    public List<UniqueKey<ClientRecord>> getKeys() {
        return Arrays.<UniqueKey<ClientRecord>>asList(Keys.KEY_CLIENT_PRIMARY);
    }

    @Override
    public Client as(String alias) {
        return new Client(DSL.name(alias), this);
    }

    @Override
    public Client as(Name alias) {
        return new Client(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Client rename(String name) {
        return new Client(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Client rename(Name name) {
        return new Client(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<ULong, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
