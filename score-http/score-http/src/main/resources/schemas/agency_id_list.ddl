CREATE TABLE `agency_id_list`
(
    `agency_id_list_id`             bigint(20) unsigned          NOT NULL AUTO_INCREMENT COMMENT 'A internal, primary database key.',
    `guid`                          char(32) CHARACTER SET ascii NOT NULL COMMENT 'A globally unique identifier (GUID).',
    `enum_type_guid`                varchar(41)                  NOT NULL COMMENT 'This column stores the GUID of the type containing the enumerated values. In OAGIS, most code lists and agnecy ID lists are defined by an XyzCodeContentType (or XyzAgencyIdentificationContentType) and XyzCodeEnumerationType (or XyzAgencyIdentificationEnumerationContentType). However, some don''t have the enumeration type. When that is the case, this column is null.',
    `name`                          varchar(100)                          DEFAULT NULL COMMENT 'Name of the agency identification list.',
    `list_id`                       varchar(100)                          DEFAULT NULL COMMENT 'This is a business or standard identification assigned to the agency identification list.',
    `agency_id_list_value_id`       bigint(20) unsigned                   DEFAULT NULL COMMENT 'This is the identification of the agency or organization which developed and/or maintains the list. Theoretically, this can be modeled as a self-reference foreign key, but it is not implemented at this point.',
    `version_id`                    varchar(100)                          DEFAULT NULL COMMENT 'Version number of the agency identification list (assigned by the agency).',
    `based_agency_id_list_id`       bigint(20) unsigned                   DEFAULT NULL COMMENT 'This is a foreign key to the AGENCY_ID_LIST table itself. This identifies the agency id list on which this agency id list is based, if any. The derivation may be restriction and/or extension.',
    `definition`                    text COMMENT 'Description of the agency identification list.',
    `definition_source`             varchar(100)                          DEFAULT NULL COMMENT 'This is typically a URL which indicates the source of the agency id list DEFINITION.',
    `remark`                        varchar(225)                          DEFAULT NULL COMMENT 'Usage information about the agency id list.',
    `namespace_id`                  bigint(20) unsigned                   DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user''s component because there is also a namespace assigned at the release level.',
    `created_by`                    bigint(20) unsigned          NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the agency ID list.',
    `last_updated_by`               bigint(20) unsigned          NOT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the agency ID list.',
    `creation_timestamp`            datetime(6)                  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Timestamp when the agency ID list was created.',
    `last_update_timestamp`         datetime(6)                  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Timestamp when the agency ID list was last updated.',
    `state`                         varchar(20)                           DEFAULT NULL COMMENT 'Life cycle state of the agency ID list. Possible values are Editing, Published, or Deleted. Only the agency ID list in published state is available for derivation and for used by the CC and BIE. Once the agency ID list is published, it cannot go back to Editing. A new version would have to be created.',
    `is_deprecated`                 tinyint(1)                            DEFAULT '0' COMMENT 'Indicates whether the agency id list is deprecated and should not be reused (i.e., no new reference to this record should be allowed).',
    `replacement_agency_id_list_id` bigint(20) unsigned                   DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    `owner_user_id`                 bigint(20) unsigned          NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    `prev_agency_id_list_id`        bigint(20) unsigned                   DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    `next_agency_id_list_id`        bigint(20) unsigned                   DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.',
    PRIMARY KEY (`agency_id_list_id`),
    KEY `agency_id_list_agency_id_list_value_id_fk` (`agency_id_list_value_id`),
    KEY `agency_id_list_created_by_fk` (`created_by`),
    KEY `agency_id_list_last_updated_by_fk` (`last_updated_by`),
    KEY `agency_id_list_based_agency_id_list_id_fk` (`based_agency_id_list_id`),
    KEY `agency_id_list_owner_user_id_fk` (`owner_user_id`),
    KEY `agency_id_list_prev_agency_id_list_id_fk` (`prev_agency_id_list_id`),
    KEY `agency_id_list_next_agency_id_list_id_fk` (`next_agency_id_list_id`),
    KEY `agency_id_list_namespace_id_fk` (`namespace_id`),
    KEY `agency_id_list_replacement_agency_id_list_id_fk` (`replacement_agency_id_list_id`),
    CONSTRAINT `agency_id_list_agency_id_list_value_id_fk` FOREIGN KEY (`agency_id_list_value_id`) REFERENCES `agency_id_list_value` (`agency_id_list_value_id`),
    CONSTRAINT `agency_id_list_based_agency_id_list_id_fk` FOREIGN KEY (`based_agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`),
    CONSTRAINT `agency_id_list_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `agency_id_list_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `agency_id_list_namespace_id_fk` FOREIGN KEY (`namespace_id`) REFERENCES `namespace` (`namespace_id`),
    CONSTRAINT `agency_id_list_next_agency_id_list_id_fk` FOREIGN KEY (`next_agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`),
    CONSTRAINT `agency_id_list_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `agency_id_list_prev_agency_id_list_id_fk` FOREIGN KEY (`prev_agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`),
    CONSTRAINT `agency_id_list_replacement_agency_id_list_id_fk` FOREIGN KEY (`replacement_agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='The AGENCY_ID_LIST table stores information about agency identification lists. The list''s values are however kept in the AGENCY_ID_LIST_VALUE.';