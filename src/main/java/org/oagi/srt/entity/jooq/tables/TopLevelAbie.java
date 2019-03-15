/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Indexes;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.TopLevelAbieRecord;


/**
 * This table indexes the ABIE which is a top-level ABIE. This table and the 
 * owner_top_level_abie_id column in all BIE tables allow all related BIEs 
 * to be retrieved all at once speeding up the profile BOD transactions.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TopLevelAbie extends TableImpl<TopLevelAbieRecord> {

    private static final long serialVersionUID = 734708381;

    /**
     * The reference instance of <code>oagi.top_level_abie</code>
     */
    public static final TopLevelAbie TOP_LEVEL_ABIE = new TopLevelAbie();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TopLevelAbieRecord> getRecordType() {
        return TopLevelAbieRecord.class;
    }

    /**
     * The column <code>oagi.top_level_abie.top_level_abie_id</code>. A internal, primary database key of an ACC.
     */
    public final TableField<TopLevelAbieRecord, ULong> TOP_LEVEL_ABIE_ID = createField("top_level_abie_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "A internal, primary database key of an ACC.");

    /**
     * The column <code>oagi.top_level_abie.abie_id</code>. Foreign key to the ABIE table pointing to a record which is a top-level ABIE.
     */
    public final TableField<TopLevelAbieRecord, ULong> ABIE_ID = createField("abie_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "Foreign key to the ABIE table pointing to a record which is a top-level ABIE.");

    /**
     * The column <code>oagi.top_level_abie.owner_user_id</code>.
     */
    public final TableField<TopLevelAbieRecord, ULong> OWNER_USER_ID = createField("owner_user_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.top_level_abie.release_id</code>. Foreign key to the RELEASE table. It identifies the release, for which this module is associated.
     */
    public final TableField<TopLevelAbieRecord, ULong> RELEASE_ID = createField("release_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the RELEASE table. It identifies the release, for which this module is associated.");

    /**
     * The column <code>oagi.top_level_abie.state</code>.
     */
    public final TableField<TopLevelAbieRecord, Integer> STATE = createField("state", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * Create a <code>oagi.top_level_abie</code> table reference
     */
    public TopLevelAbie() {
        this(DSL.name("top_level_abie"), null);
    }

    /**
     * Create an aliased <code>oagi.top_level_abie</code> table reference
     */
    public TopLevelAbie(String alias) {
        this(DSL.name(alias), TOP_LEVEL_ABIE);
    }

    /**
     * Create an aliased <code>oagi.top_level_abie</code> table reference
     */
    public TopLevelAbie(Name alias) {
        this(alias, TOP_LEVEL_ABIE);
    }

    private TopLevelAbie(Name alias, Table<TopLevelAbieRecord> aliased) {
        this(alias, aliased, null);
    }

    private TopLevelAbie(Name alias, Table<TopLevelAbieRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table indexes the ABIE which is a top-level ABIE. This table and the owner_top_level_abie_id column in all BIE tables allow all related BIEs to be retrieved all at once speeding up the profile BOD transactions."));
    }

    public <O extends Record> TopLevelAbie(Table<O> child, ForeignKey<O, TopLevelAbieRecord> key) {
        super(child, key, TOP_LEVEL_ABIE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.TOP_LEVEL_ABIE_PRIMARY, Indexes.TOP_LEVEL_ABIE_TOP_LEVEL_ABIE_ABIE_ID_FK, Indexes.TOP_LEVEL_ABIE_TOP_LEVEL_ABIE_OWNER_USER_ID_FK, Indexes.TOP_LEVEL_ABIE_TOP_LEVEL_ABIE_RELEASE_ID_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<TopLevelAbieRecord, ULong> getIdentity() {
        return Keys.IDENTITY_TOP_LEVEL_ABIE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<TopLevelAbieRecord> getPrimaryKey() {
        return Keys.KEY_TOP_LEVEL_ABIE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<TopLevelAbieRecord>> getKeys() {
        return Arrays.<UniqueKey<TopLevelAbieRecord>>asList(Keys.KEY_TOP_LEVEL_ABIE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<TopLevelAbieRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<TopLevelAbieRecord, ?>>asList(Keys.TOP_LEVEL_ABIE_ABIE_ID_FK, Keys.TOP_LEVEL_ABIE_OWNER_USER_ID_FK, Keys.TOP_LEVEL_ABIE_RELEASE_ID_FK);
    }

    public Abie abie() {
        return new Abie(this, Keys.TOP_LEVEL_ABIE_ABIE_ID_FK);
    }

    public AppUser appUser() {
        return new AppUser(this, Keys.TOP_LEVEL_ABIE_OWNER_USER_ID_FK);
    }

    public Release release() {
        return new Release(this, Keys.TOP_LEVEL_ABIE_RELEASE_ID_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TopLevelAbie as(String alias) {
        return new TopLevelAbie(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TopLevelAbie as(Name alias) {
        return new TopLevelAbie(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TopLevelAbie rename(String name) {
        return new TopLevelAbie(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TopLevelAbie rename(Name name) {
        return new TopLevelAbie(name, null);
    }
}
