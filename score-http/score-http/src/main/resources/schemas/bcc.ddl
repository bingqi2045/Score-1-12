CREATE TABLE `bcc`
(
    `bcc_id`                bigint(20) unsigned          NOT NULL AUTO_INCREMENT COMMENT 'A internal, primary database key of an BCC.',
    `guid`                  char(32) CHARACTER SET ascii NOT NULL COMMENT 'A globally unique identifier (GUID).',
    `cardinality_min`       int(11)                      NOT NULL COMMENT 'Minimum cardinality of the TO_BCCP_ID. The valid values are non-negative integer.',
    `cardinality_max`       int(11)                               DEFAULT NULL COMMENT 'Maximum cardinality of the TO_BCCP_ID. The valid values are integer -1 and up. Specifically, -1 means unbounded. 0 means prohibited or not to use.'',',
    `to_bccp_id`            bigint(20) unsigned          NOT NULL COMMENT 'TO_BCCP_ID is a foreign key to an BCCP table record. It is basically pointing to a child data element of the FROM_ACC_ID. \n\nNote that for the BCC history records, this column always points to the BCCP_ID of the current record of a BCCP.'',',
    `from_acc_id`           bigint(20) unsigned          NOT NULL COMMENT 'FROM_ACC_ID is a foreign key pointing to an ACC record. It is basically pointing to a parent data element (type) of the TO_BCCP_ID. \n\nNote that for the BCC history records, this column always points to the ACC_ID of the current record of an ACC.',
    `seq_key`               int(11)                               DEFAULT NULL COMMENT '@deprecated since 2.0.0. This indicates the order of the associations among other siblings. A valid value is positive integer. The SEQ_KEY at the CC side is localized. In other words, if an ACC is based on another ACC, SEQ_KEY of ASCCs or BCCs of the former ACC starts at 1 again.',
    `entity_type`           int(11)                               DEFAULT NULL COMMENT 'This is a code list: 0 = ATTRIBUTE and 1 = ELEMENT. An expression generator may or may not use this information. This column is necessary because some of the BCCs are xsd:attribute and some are xsd:element in the OAGIS 10.x. ',
    `den`                   varchar(200)                 NOT NULL COMMENT 'DEN (dictionary entry name) of the BCC. This column can be derived from QUALIFIER and OBJECT_CLASS_TERM of the FROM_ACC_ID and DEN of the TO_BCCP_ID as QUALIFIER + "_ " + OBJECT_CLASS_TERM + ". " + DEN. ',
    `definition`            text COMMENT 'This is a documentation or description of the BCC. Since BCC is business context independent, this is a business context independent description of the BCC. Since there are definitions also in the BCCP (as referenced by TO_BCCP_ID column) and the BDT under that BCCP, the definition in the BCC is a specific description about the relationship between the ACC (as in FROM_ACC_ID) and the BCCP.',
    `definition_source`     varchar(100)                          DEFAULT NULL COMMENT 'This is typically a URL identifying the source of the DEFINITION column.',
    `created_by`            bigint(20) unsigned          NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the user who creates the entity.\n\nThis column never change between the history and the current record. The history record should have the same value as that of its current record.',
    `owner_user_id`         bigint(20) unsigned          NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    `last_updated_by`       bigint(20) unsigned          NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).',
    `creation_timestamp`    datetime(6)                  NOT NULL COMMENT 'Timestamp when the revision of the BCC was created. \n\nThis never change for a revision.',
    `last_update_timestamp` datetime(6)                  NOT NULL COMMENT 'The timestamp when the record was last updated.\n\nThe value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the change has occurred.',
    `state`                 varchar(20)                           DEFAULT NULL COMMENT 'Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the BCC.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.',
    `is_deprecated`         tinyint(1)                   NOT NULL COMMENT 'Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).',
    `replacement_bcc_id`    bigint(20) unsigned                   DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    `is_nillable`           tinyint(1)                   NOT NULL DEFAULT '0' COMMENT '@deprecated since 2.0.0 in favor of impossibility of nillable association (element reference) in XML schema.\n\nIndicate whether the field can have a NULL This is corresponding to the nillable flag in the XML schema.',
    `default_value`         text COMMENT 'This set the default value at the association level. ',
    `fixed_value`           text COMMENT 'This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.',
    `prev_bcc_id`           bigint(20) unsigned                   DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    `next_bcc_id`           bigint(20) unsigned                   DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.',
    PRIMARY KEY (`bcc_id`),
    KEY `bcc_to_bccp_id_fk` (`to_bccp_id`),
    KEY `bcc_from_acc_id_fk` (`from_acc_id`),
    KEY `bcc_created_by_fk` (`created_by`),
    KEY `bcc_owner_user_id_fk` (`owner_user_id`),
    KEY `bcc_last_updated_by_fk` (`last_updated_by`),
    KEY `bcc_prev_bcc_id_fk` (`prev_bcc_id`),
    KEY `bcc_next_bcc_id_fk` (`next_bcc_id`),
    KEY `bcc_guid_idx` (`guid`),
    KEY `bcc_last_update_timestamp_desc_idx` (`last_update_timestamp`),
    KEY `bcc_replacement_bcc_id_fk` (`replacement_bcc_id`),
    CONSTRAINT `bcc_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `bcc_from_acc_id_fk` FOREIGN KEY (`from_acc_id`) REFERENCES `acc` (`acc_id`),
    CONSTRAINT `bcc_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `bcc_next_bcc_id_fk` FOREIGN KEY (`next_bcc_id`) REFERENCES `bcc` (`bcc_id`),
    CONSTRAINT `bcc_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `bcc_prev_bcc_id_fk` FOREIGN KEY (`prev_bcc_id`) REFERENCES `bcc` (`bcc_id`),
    CONSTRAINT `bcc_replacement_bcc_id_fk` FOREIGN KEY (`replacement_bcc_id`) REFERENCES `bcc` (`bcc_id`),
    CONSTRAINT `bcc_to_bccp_id_fk` FOREIGN KEY (`to_bccp_id`) REFERENCES `bccp` (`bccp_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='A BCC represents a relationship/association between an ACC and a BCCP. It creates a data element for an ACC. ';