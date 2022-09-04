-- ----------------------------------------------------
-- Migration script for Score v2.4.0                 --
--                                                   --
-- Author: Hakju Oh <hakju.oh@nist.gov>              --
--         Dimitrije Milenkovic                      --
--                <dimitrije.milenkovic@nist.gov>    --
-- ----------------------------------------------------

-- Bug fix
-- Delete orphan records; 'Program. Extension. Program. Extension' ASCC
UPDATE `ascc_manifest` SET `seq_key_id` = NULL WHERE `ascc_manifest_id` = 57206;
DELETE FROM `seq_key` WHERE `seq_key_id` = 85921;
DELETE FROM `ascc_manifest` WHERE `ascc_manifest_id` = 57206;
DELETE FROM `ascc` WHERE `ascc_id` = 7311;

-- ----------------
-- Business Terms -
-- ----------------
CREATE TABLE IF NOT EXISTS `business_term`
(
    `business_term_id`      bigint(20) unsigned          NOT NULL AUTO_INCREMENT COMMENT 'A internal, primary database key of an Business term.',
    `guid`                  char(32) CHARACTER SET ascii NOT NULL COMMENT 'A globally unique identifier (GUID).',
    `business_term`         varchar(200)                 NOT NULL COMMENT 'A main name of the business term',
    `definition`            text COMMENT 'Definition of the business term.',
    `created_by`            bigint(20) unsigned          NOT NULL COMMENT 'A foreign key referring to the user who creates the business term. The creator of the business term is also its owner by default.',
    `last_updated_by`       bigint(20) unsigned          NOT NULL COMMENT 'A foreign key referring to the last user who has updated the business term record. This may be the user who is in the same group as the creator.',
    `creation_timestamp`    datetime(6)                  NOT NULL COMMENT 'Timestamp when the business term record was first created.',
    `last_update_timestamp` datetime(6)                  NOT NULL COMMENT 'The timestamp when the business term was last updated.',
    `external_ref_uri`      text COMMENT 'TODO: Definition is missing.',
    `external_ref_id`       varchar(100) DEFAULT NULL COMMENT 'TODO: Definition is missing.',
    `comment`               text COMMENT 'Comment of the business term.',
    PRIMARY KEY (`business_term_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='The Business Term table stores information about the business term, which is usually associated to BIE or CC.';

CREATE TABLE IF NOT EXISTS `ascc_bizterm`
(
    `ascc_bizterm_id`       bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'An internal, primary database key of an Business term.',
    `business_term_id`      bigint(20) unsigned NOT NULL COMMENT 'An internal ID of the associated business term',
    `ascc_id`               bigint(20) unsigned NOT NULL COMMENT 'An internal ID of the associated ASCC',
    `created_by`            bigint(20) unsigned NOT NULL COMMENT 'A foreign key referring to the user who creates the ascc_bizterm record. The creator of the ascc_bizterm is also its owner by default.',
    `last_updated_by`       bigint(20) unsigned NOT NULL COMMENT 'A foreign key referring to the last user who has updated the ascc_bizterm record. This may be the user who is in the same group as the creator.',
    `creation_timestamp`    datetime(6)         NOT NULL COMMENT 'Timestamp when the ascc_bizterm record was first created.',
    `last_update_timestamp` datetime(6)         NOT NULL COMMENT 'The timestamp when the ascc_bizterm was last updated.',
    PRIMARY KEY (`ascc_bizterm_id`),
    KEY `ascc_bizterm_ascc_fk` (`ascc_id`),
    KEY `ascc_bizterm_business_term_fk` (`business_term_id`),
    CONSTRAINT `ascc_bizterm_ascc_fk` FOREIGN KEY (`ascc_id`) REFERENCES `ascc` (`ascc_id`),
    CONSTRAINT `ascc_bizterm_business_term_fk` FOREIGN KEY (`business_term_id`) REFERENCES `business_term` (`business_term_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='The ascc_bizterm table stores information about the aggregation between the business term and ASCC. TODO: Placeholder, definition is missing.';

CREATE TABLE IF NOT EXISTS `asbie_bizterm`
(
    `asbie_bizterm_id`      bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'An internal, primary database key of an asbie_bizterm record.',
    `ascc_bizterm_id`       bigint(20) unsigned NOT NULL COMMENT 'An internal ID of the ascc_business_term record.',
    `asbie_id`              bigint(20) unsigned NOT NULL COMMENT 'An internal ID of the associated ASBIE',
    `primary_indicator`     char(1)  DEFAULT NULL COMMENT 'The indicator shows if the business term is primary for the assigned ASBIE.',
    `type_code`             char(30) DEFAULT NULL COMMENT 'The type code of the assignment.',
    `created_by`            bigint(20) unsigned NOT NULL COMMENT 'A foreign key referring to the user who creates the asbie_bizterm record. The creator of the asbie_bizterm is also its owner by default.',
    `last_updated_by`       bigint(20) unsigned NOT NULL COMMENT 'A foreign key referring to the last user who has updated the asbie_bizterm record. This may be the user who is in the same group as the creator.',
    `creation_timestamp`    datetime(6)         NOT NULL COMMENT 'Timestamp when the asbie_bizterm record was first created.',
    `last_update_timestamp` datetime(6)         NOT NULL COMMENT 'The timestamp when the asbie_bizterm was last updated.',
    PRIMARY KEY (`asbie_bizterm_id`),
    KEY `asbie_bizterm_ascc_bizterm_fk` (`ascc_bizterm_id`),
    KEY `asbie_bizterm_asbie_fk` (`asbie_id`),
    CONSTRAINT `asbie_bizterm_asbie_fk` FOREIGN KEY (`asbie_id`) REFERENCES `asbie` (`asbie_id`),
    CONSTRAINT `asbie_bizterm_ascc_bizterm_fk` FOREIGN KEY (`ascc_bizterm_id`) REFERENCES `ascc_bizterm` (`ascc_bizterm_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='The asbie_bizterm table stores information about the aggregation between the ascc_bizterm and ASBIE. TODO: Placeholder, definition is missing.';

CREATE TABLE IF NOT EXISTS `bcc_bizterm`
(
    `bcc_bizterm_id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'An internal, primary database key of an bcc_bizterm record.',
    `business_term_id`      bigint(20) unsigned NOT NULL COMMENT 'An internal ID of the associated business term',
    `bcc_id`                bigint(20) unsigned NOT NULL COMMENT 'An internal ID of the associated BCC',
    `created_by`            bigint(20) unsigned NOT NULL COMMENT 'A foreign key referring to the user who creates the bcc_bizterm record. The creator of the bcc_bizterm is also its owner by default.',
    `last_updated_by`       bigint(20) unsigned NOT NULL COMMENT 'A foreign key referring to the last user who has updated the bcc_bizterm record. This may be the user who is in the same group as the creator.',
    `creation_timestamp`    datetime(6)         NOT NULL COMMENT 'Timestamp when the bcc_bizterm record was first created.',
    `last_update_timestamp` datetime(6)         NOT NULL COMMENT 'The timestamp when the bcc_bizterm was last updated.',
    PRIMARY KEY (`bcc_bizterm_id`),
    KEY `bcc_bizterm_bcc_fk` (`bcc_id`),
    KEY `bcc_bizterm_business_term_fk` (`business_term_id`),
    CONSTRAINT `bcc_bizterm_bcc_fk` FOREIGN KEY (`bcc_id`) REFERENCES `bcc` (`bcc_id`),
    CONSTRAINT `bcc_bizterm_business_term_fk` FOREIGN KEY (`business_term_id`) REFERENCES `business_term` (`business_term_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='The bcc_bizterm table stores information about the aggregation between the business term and BCC. TODO: Placeholder, definition is missing.';

CREATE TABLE IF NOT EXISTS `bbie_bizterm`
(
    `bbie_bizterm_id`       bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'An internal, primary database key of an bbie_bizterm record.',
    `bcc_bizterm_id`        bigint(20) unsigned NOT NULL COMMENT 'An internal ID of the bbie_bizterm record.',
    `bbie_id`               bigint(20) unsigned NOT NULL COMMENT 'An internal ID of the associated BBIE',
    `primary_indicator`     char(1)  DEFAULT NULL COMMENT 'The indicator shows if the business term is primary for the assigned BBIE.',
    `type_code`             char(30) DEFAULT NULL COMMENT 'The type code of the assignment.',
    `created_by`            bigint(20) unsigned NOT NULL COMMENT 'A foreign key referring to the user who creates the bbie_bizterm record. The creator of the asbie_bizterm is also its owner by default.',
    `last_updated_by`       bigint(20) unsigned NOT NULL COMMENT 'A foreign key referring to the last user who has updated the bbie_bizterm record. This may be the user who is in the same group as the creator.',
    `creation_timestamp`    datetime(6)         NOT NULL COMMENT 'Timestamp when the bbie_bizterm record was first created.',
    `last_update_timestamp` datetime(6)         NOT NULL COMMENT 'The timestamp when the bbie_bizterm was last updated.',
    PRIMARY KEY (`bbie_bizterm_id`),
    KEY `bbie_bizterm_bcc_bizterm_fk` (`bcc_bizterm_id`),
    KEY `asbie_bizterm_asbie_fk` (`bbie_id`),
    CONSTRAINT `bbie_bizterm_bcc_bizterm_fk` FOREIGN KEY (`bcc_bizterm_id`) REFERENCES `bcc_bizterm` (`bcc_bizterm_id`),
    CONSTRAINT `bbie_bizterm_bbie_fk` FOREIGN KEY (`bbie_id`) REFERENCES `bbie` (`bbie_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='The bbie_bizterm table stores information about the aggregation between the bbie_bizterm and BBIE. TODO: Placeholder, definition is missing.';

-- Add `description` field on `module_set_release` table.
ALTER TABLE `module_set_release` ADD COLUMN `description` text COMMENT 'Description or explanation about the module set release.' AFTER `name`;

-- Add `facet_min_length`, `facet_max_length`, and `facet_pattern` fields on `bbie` and `bbie_sc` tables.
ALTER TABLE `bbie`
    ADD COLUMN `facet_min_length` bigint(20) unsigned DEFAULT NULL COMMENT 'Defines the minimum number of units of length.' AFTER `cardinality_max`,
    ADD COLUMN `facet_max_length` bigint(20) unsigned DEFAULT NULL COMMENT 'Defines the minimum number of units of length.' AFTER `facet_min_length`,
    ADD COLUMN `facet_pattern` text COMMENT 'Defines a constraint on the lexical space of a datatype to literals in a specific pattern.' AFTER `facet_max_length`;

ALTER TABLE `bbie_sc`
    ADD COLUMN `facet_min_length` bigint(20) unsigned DEFAULT NULL COMMENT 'Defines the minimum number of units of length.' AFTER `cardinality_max`,
    ADD COLUMN `facet_max_length` bigint(20) unsigned DEFAULT NULL COMMENT 'Defines the minimum number of units of length.' AFTER `facet_min_length`,
    ADD COLUMN `facet_pattern` text COMMENT 'Defines a constraint on the lexical space of a datatype to literals in a specific pattern.' AFTER `facet_max_length`;

-- Add `cc_tag` table
CREATE TABLE `cc_tag`
(
    `cc_tag_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `tag_name`  varchar(100) NOT NULL,
    PRIMARY KEY (`cc_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `cc_tag` (`cc_tag_id`, `tag_name`)
VALUES
    (1, 'BOD'),
    (2, 'Noun'),
    (3, 'Verb');

-- Add `manifest_tag` tables
CREATE TABLE `acc_manifest_tag`
(
    `acc_manifest_id` bigint(20) unsigned NOT NULL,
    `cc_tag_id`         bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`acc_manifest_id`, `cc_tag_id`),
    KEY                 `acc_manifest_tag_cc_tag_id_fk` (`cc_tag_id`),
    CONSTRAINT `acc_manifest_tag_acc_manifest_id_fk` FOREIGN KEY (`acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `acc_manifest_tag_cc_tag_id_fk` FOREIGN KEY (`cc_tag_id`) REFERENCES `cc_tag` (`cc_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `asccp_manifest_tag`
(
    `asccp_manifest_id` bigint(20) unsigned NOT NULL,
    `cc_tag_id`         bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`asccp_manifest_id`, `cc_tag_id`),
    KEY                 `asccp_manifest_tag_cc_tag_id_fk` (`cc_tag_id`),
    CONSTRAINT `asccp_manifest_tag_asccp_manifest_id_fk` FOREIGN KEY (`asccp_manifest_id`) REFERENCES `asccp_manifest` (`asccp_manifest_id`),
    CONSTRAINT `asccp_manifest_tag_cc_tag_id_fk` FOREIGN KEY (`cc_tag_id`) REFERENCES `cc_tag` (`cc_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bccp_manifest_tag`
(
    `bccp_manifest_id` bigint(20) unsigned NOT NULL,
    `cc_tag_id`         bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`bccp_manifest_id`, `cc_tag_id`),
    KEY                 `bccp_manifest_tag_cc_tag_id_fk` (`cc_tag_id`),
    CONSTRAINT `bccp_manifest_tag_bccp_manifest_id_fk` FOREIGN KEY (`bccp_manifest_id`) REFERENCES `bccp_manifest` (`bccp_manifest_id`),
    CONSTRAINT `bccp_manifest_tag_cc_tag_id_fk` FOREIGN KEY (`cc_tag_id`) REFERENCES `cc_tag` (`cc_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `dt_manifest_tag`
(
    `dt_manifest_id` bigint(20) unsigned NOT NULL,
    `cc_tag_id`         bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`dt_manifest_id`, `cc_tag_id`),
    KEY                 `dt_manifest_tag_cc_tag_id_fk` (`cc_tag_id`),
    CONSTRAINT `dt_manifest_tag_dt_manifest_id_fk` FOREIGN KEY (`dt_manifest_id`) REFERENCES `dt_manifest` (`dt_manifest_id`),
    CONSTRAINT `dt_manifest_tag_cc_tag_id_fk` FOREIGN KEY (`cc_tag_id`) REFERENCES `cc_tag` (`cc_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Add 'BOD'
INSERT INTO `asccp_manifest_tag` (`asccp_manifest_id`, `cc_tag_id`)
SELECT asccp_manifest.asccp_manifest_id, (SELECT `cc_tag`.`cc_tag_id` FROM `cc_tag` WHERE `cc_tag`.`tag_name` = 'BOD')
FROM asccp_manifest
         JOIN acc_manifest on asccp_manifest.role_of_acc_manifest_id = acc_manifest.acc_manifest_id
         JOIN asccp ON asccp_manifest.asccp_id = asccp.asccp_id
WHERE acc_manifest.based_acc_manifest_id IN (SELECT acc_manifest_id
                                             FROM acc_manifest
                                                      JOIN acc on acc_manifest.acc_id = acc.acc_id
                                             WHERE acc.object_class_term = 'Business Object Document');

-- Add 'Verb'
INSERT INTO `asccp_manifest_tag` (`asccp_manifest_id`, `cc_tag_id`)
SELECT asccp_manifest.asccp_manifest_id,
       (SELECT `cc_tag`.`cc_tag_id` FROM `cc_tag` WHERE `cc_tag`.`tag_name` = 'Verb')
FROM asccp_manifest
         JOIN acc_manifest on asccp_manifest.role_of_acc_manifest_id = acc_manifest.acc_manifest_id
         JOIN asccp ON asccp_manifest.asccp_id = asccp.asccp_id
WHERE acc_manifest.based_acc_manifest_id IN (SELECT acc_manifest_id
                                             FROM acc_manifest
                                                      JOIN acc on acc_manifest.acc_id = acc.acc_id
                                             WHERE acc.object_class_term LIKE '%Verb');

INSERT INTO `acc_manifest_tag` (`acc_manifest_id`, `cc_tag_id`)
SELECT distinct acc_manifest.acc_manifest_id,
       (SELECT `cc_tag`.`cc_tag_id` FROM `cc_tag` WHERE `cc_tag`.`tag_name` = 'Verb')
FROM asccp_manifest
         JOIN asccp_manifest_tag on asccp_manifest.asccp_manifest_id = asccp_manifest_tag.asccp_manifest_id
         JOIN cc_tag ON asccp_manifest_tag.cc_tag_id = cc_tag.cc_tag_id
         JOIN acc_manifest on asccp_manifest.role_of_acc_manifest_id = acc_manifest.acc_manifest_id
WHERE cc_tag.tag_name = 'Verb';

INSERT INTO `acc_manifest_tag` (`acc_manifest_id`, `cc_tag_id`)
SELECT distinct acc_manifest.based_acc_manifest_id,
                (SELECT `cc_tag`.`cc_tag_id` FROM `cc_tag` WHERE `cc_tag`.`tag_name` = 'Verb')
FROM asccp_manifest
         JOIN asccp_manifest_tag on asccp_manifest.asccp_manifest_id = asccp_manifest_tag.asccp_manifest_id
         JOIN cc_tag ON asccp_manifest_tag.cc_tag_id = cc_tag.cc_tag_id
         JOIN acc_manifest on asccp_manifest.role_of_acc_manifest_id = acc_manifest.acc_manifest_id
WHERE cc_tag.tag_name = 'Verb';

INSERT INTO `acc_manifest_tag` (`acc_manifest_id`, `cc_tag_id`)
SELECT distinct base.based_acc_manifest_id,
                (SELECT `cc_tag`.`cc_tag_id` FROM `cc_tag` WHERE `cc_tag`.`tag_name` = 'Verb')
FROM asccp_manifest
         JOIN asccp_manifest_tag on asccp_manifest.asccp_manifest_id = asccp_manifest_tag.asccp_manifest_id
         JOIN cc_tag ON asccp_manifest_tag.cc_tag_id = cc_tag.cc_tag_id
         JOIN acc_manifest on asccp_manifest.role_of_acc_manifest_id = acc_manifest.acc_manifest_id
         JOIN acc_manifest AS base on acc_manifest.based_acc_manifest_id = base.acc_manifest_id
WHERE cc_tag.tag_name = 'Verb';

-- Add 'Noun'
INSERT INTO `asccp_manifest_tag` (`asccp_manifest_id`, `cc_tag_id`)
SELECT distinct desc_asccp_manifest.asccp_manifest_id,
                (SELECT `cc_tag`.`cc_tag_id` FROM `cc_tag` WHERE `cc_tag`.`tag_name` = 'Noun')
FROM asccp_manifest
         JOIN asccp_manifest_tag ON asccp_manifest.asccp_manifest_id = asccp_manifest_tag.asccp_manifest_id
         JOIN cc_tag ON asccp_manifest_tag.cc_tag_id = cc_tag.cc_tag_id
         JOIN acc_manifest ON asccp_manifest.role_of_acc_manifest_id = acc_manifest.acc_manifest_id
         JOIN ascc_manifest ON acc_manifest.acc_manifest_id = ascc_manifest.from_acc_manifest_id
         JOIN asccp_manifest AS data_area_asccp_manifest
              ON ascc_manifest.to_asccp_manifest_id = data_area_asccp_manifest.asccp_manifest_id
         JOIN acc_manifest AS data_area_acc_manifest
              ON data_area_asccp_manifest.role_of_acc_manifest_id = data_area_acc_manifest.acc_manifest_id
         JOIN ascc_manifest AS data_area_ascc_manifest
              ON data_area_acc_manifest.acc_manifest_id = data_area_ascc_manifest.from_acc_manifest_id
         JOIN asccp_manifest AS desc_asccp_manifest
              ON data_area_ascc_manifest.to_asccp_manifest_id = desc_asccp_manifest.asccp_manifest_id
         JOIN asccp ON desc_asccp_manifest.asccp_id = asccp.asccp_id
WHERE cc_tag.tag_name = 'BOD'
  AND asccp.type != 'Verb';

-- Add `is_default` column on `cdt_sc_awd_pri_xps_type_map` table.
ALTER TABLE `cdt_sc_awd_pri_xps_type_map` ADD COLUMN `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indicating a default value domain mapping.';

UPDATE `cdt_sc_awd_pri_xps_type_map` SET `is_default` = 1 WHERE `cdt_sc_awd_pri_xps_type_map_id` IN (3,6,9,12,15,18,21,24,25,28,31,34,37,40,43,46,49,52,55,58,61,62,71,74,75,84,87,90,93,96,99,102,105,108,110,117,120,127,132,135,142,145,150,157,162,167,172,177,182,187,190,197,200,207,212,215,220,227,230,237,240,245,252,255,262,267,270,277,280,284,293,296,299,302,305,308,311,314,317,320,323,326,329,332,335,338,341,344,347,348,357,360,363,366,369,372,375,378,379,388,390,392,402,406,409,412,415,418,419,421,425,427,430,433,437,438,445,457,460,463,464,474,486,489,492,494,498,501,504,507,509,513,516,518,521,525,527,531,534,537,539,543,545,549,551,555,558,561,563,567,570,572,576,579,581,585,588);

-- Add `update_uuid` procedure and `uuid_v4s` function
DELIMITER //
DROP FUNCTION IF EXISTS `uuid_v4s`//
CREATE FUNCTION uuid_v4s()
    RETURNS CHAR(36)
BEGIN
    -- 1th and 2nd block are made of 6 random bytes
    SET @h1 = HEX(RANDOM_BYTES(4));
    SET @h2 = HEX(RANDOM_BYTES(2));

    -- 3th block will start with a 4 indicating the version, remaining is random
    SET @h3 = SUBSTR(HEX(RANDOM_BYTES(2)), 2, 3);

    -- 4th block first nibble can only be 8, 9 A or B, remaining is random
    SET @h4 = CONCAT(HEX(FLOOR(ASCII(RANDOM_BYTES(1)) / 64)+8),
                     SUBSTR(HEX(RANDOM_BYTES(2)), 2, 3));

    -- 5th block is made of 6 random bytes
    SET @h5 = HEX(RANDOM_BYTES(6));

    -- Build the complete UUID
    RETURN LOWER(CONCAT(
            @h1, '-', @h2, '-4', @h3, '-', @h4, '-', @h5
        ));
END//

DROP PROCEDURE IF EXISTS `update_uuid`//
CREATE PROCEDURE `update_uuid`(IN table_name VARCHAR(100))
BEGIN
    SET @sql = CONCAT('UPDATE `', table_name, '` SET `', table_name, '_uuid` = uuid_v4s() WHERE `', table_name, '_uuid` IS NULL');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END//
DELIMITER ;

-- -------------------------------
-- Change 'app_user_id' TO UUID --
-- -------------------------------
ALTER TABLE `app_user` ADD COLUMN `app_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `app_user_id`;
CALL update_uuid('app_user');

UPDATE `app_user` SET `app_user_uuid` = '3291889a-0dad-4262-b2f1-4ec4754c41cd' WHERE `login_id` = 'sysadm';

ALTER TABLE `abie`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who creates the ABIE. The creator of the ABIE is also its owner by default. ABIEs created as children of another ABIE have the same CREATED_BY as its parent.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the last user who has updated the ABIE record. This may be the user who is in the same group as the creator.' AFTER `last_updated_by`;

UPDATE `app_user`, `abie` SET `abie`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `abie`.`created_by`;
UPDATE `app_user`, `abie` SET `abie`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `abie`.`last_updated_by`;

ALTER TABLE `acc`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the user who creates the entity.\n\nThis column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).' AFTER `last_updated_by`;

UPDATE `app_user`, `acc` SET `acc`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `acc`.`created_by`;
UPDATE `app_user`, `acc` SET `acc`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `acc`.`owner_user_id`;
UPDATE `app_user`, `acc` SET `acc`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `acc`.`last_updated_by`;

ALTER TABLE `agency_id_list`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the agency ID list.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the agency ID list.' AFTER `last_updated_by`;

UPDATE `app_user`, `agency_id_list` SET `agency_id_list`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `agency_id_list`.`created_by`;
UPDATE `app_user`, `agency_id_list` SET `agency_id_list`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `agency_id_list`.`owner_user_id`;
UPDATE `app_user`, `agency_id_list` SET `agency_id_list`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `agency_id_list`.`last_updated_by`;

ALTER TABLE `agency_id_list_value`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the agency ID list value.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the agency ID list value.' AFTER `last_updated_by`;

UPDATE `app_user`, `agency_id_list_value` SET `agency_id_list_value`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `agency_id_list_value`.`created_by`;
UPDATE `app_user`, `agency_id_list_value` SET `agency_id_list_value`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `agency_id_list_value`.`owner_user_id`;
UPDATE `app_user`, `agency_id_list_value` SET `agency_id_list_value`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `agency_id_list_value`.`last_updated_by`;

ALTER TABLE `app_oauth2_user`
    ADD COLUMN `app_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A reference to the record in `app_user`. If it is not set, this is treated as a pending record.' AFTER `app_user_id`;

UPDATE `app_user`, `app_oauth2_user` SET `app_oauth2_user`.`app_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `app_oauth2_user`.`app_user_id`;

ALTER TABLE `asbie`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who creates the ASBIE. The creator of the ASBIE is also its owner by default. ASBIEs created as children of another ABIE have the same CREATED_BY.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who has last updated the ASBIE record.' AFTER `last_updated_by`;

UPDATE `app_user`, `asbie` SET `asbie`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `asbie`.`created_by`;
UPDATE `app_user`, `asbie` SET `asbie`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `asbie`.`last_updated_by`;

ALTER TABLE `asbie_bizterm`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who creates the ASBIE_BIZTERM record. The creator of the ASBIE_BIZTERM is also its owner by default.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the last user who has updated the ASBIE_BIZTERM record. This may be the user who is in the same group as the creator.' AFTER `last_updated_by`;

UPDATE `app_user`, `asbie_bizterm` SET `asbie_bizterm`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `asbie_bizterm`.`created_by`;
UPDATE `app_user`, `asbie_bizterm` SET `asbie_bizterm`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `asbie_bizterm`.`last_updated_by`;

ALTER TABLE `asbiep`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who creates the ASBIEP. The creator of the ASBIEP is also its owner by default. ASBIEPs created as children of another ABIE have the same CREATED_BY.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the last user who has updated the ASBIEP record.' AFTER `last_updated_by`;

UPDATE `app_user`, `asbiep` SET `asbiep`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `asbiep`.`created_by`;
UPDATE `app_user`, `asbiep` SET `asbiep`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `asbiep`.`last_updated_by`;

ALTER TABLE `ascc`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key to the APP_USER table referring to the user who creates the entity.\n\nThis column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).' AFTER `last_updated_by`;

UPDATE `app_user`, `ascc` SET `ascc`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `ascc`.`created_by`;
UPDATE `app_user`, `ascc` SET `ascc`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `ascc`.`owner_user_id`;
UPDATE `app_user`, `ascc` SET `ascc`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `ascc`.`last_updated_by`;

ALTER TABLE `ascc_bizterm`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who creates the ASCC_BIZTERM record. The creator of the ASCC_BIZTERM is also its owner by default.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the last user who has updated the ASCC_BIZTERM record. This may be the user who is in the same group as the creator.' AFTER `last_updated_by`;

UPDATE `app_user`, `ascc_bizterm` SET `ascc_bizterm`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `ascc_bizterm`.`created_by`;
UPDATE `app_user`, `ascc_bizterm` SET `ascc_bizterm`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `ascc_bizterm`.`last_updated_by`;

ALTER TABLE `asccp`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the user who creates the entity. \n\nThis column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).' AFTER `last_updated_by`;

UPDATE `app_user`, `asccp` SET `asccp`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `asccp`.`created_by`;
UPDATE `app_user`, `asccp` SET `asccp`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `asccp`.`owner_user_id`;
UPDATE `app_user`, `asccp` SET `asccp`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `asccp`.`last_updated_by`;

ALTER TABLE `bbie`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who creates the BBIE. The creator of the BBIE is also its owner by default. BBIEs created as children of another ABIE have the same CREATED_BY.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who has last updated the BBIE record.' AFTER `last_updated_by`;

UPDATE `app_user`, `bbie` SET `bbie`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bbie`.`created_by`;
UPDATE `app_user`, `bbie` SET `bbie`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bbie`.`last_updated_by`;

ALTER TABLE `bbie_bizterm`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who creates the BBIE_BIZTERM record. The creator of the ASBIE_BIZTERM is also its owner by default.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the last user who has updated the BBIE_BIZTERM record. This may be the user who is in the same group as the creator.' AFTER `last_updated_by`;

UPDATE `app_user`, `bbie_bizterm` SET `bbie_bizterm`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bbie_bizterm`.`created_by`;
UPDATE `app_user`, `bbie_bizterm` SET `bbie_bizterm`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bbie_bizterm`.`last_updated_by`;

ALTER TABLE `bbie_sc`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who creates the BBIE_SC. The creator of the BBIE_SC is also its owner by default.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who has last updated the BBIE_SC record.' AFTER `last_updated_by`;

UPDATE `app_user`, `bbie_sc` SET `bbie_sc`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bbie_sc`.`created_by`;
UPDATE `app_user`, `bbie_sc` SET `bbie_sc`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bbie_sc`.`last_updated_by`;

ALTER TABLE `bbiep`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who creates the BBIEP. The creator of the BBIEP is also its owner by default. BBIEPs created as children of another ABIE have the same CREATED_BY'',' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the last user who has updated the BBIEP record.' AFTER `last_updated_by`;

UPDATE `app_user`, `bbiep` SET `bbiep`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bbiep`.`created_by`;
UPDATE `app_user`, `bbiep` SET `bbiep`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bbiep`.`last_updated_by`;

ALTER TABLE `bcc`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the user who creates the entity.\n\nThis column never change between the history and the current record. The history record should have the same value as that of its current record.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).' AFTER `last_updated_by`;

UPDATE `app_user`, `bcc` SET `bcc`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bcc`.`created_by`;
UPDATE `app_user`, `bcc` SET `bcc`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bcc`.`owner_user_id`;
UPDATE `app_user`, `bcc` SET `bcc`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bcc`.`last_updated_by`;

ALTER TABLE `bcc_bizterm`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who creates the BCC_BIZTERM record. The creator of the BCC_BIZTERM is also its owner by default.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the last user who has updated the BCC_BIZTERM record. This may be the user who is in the same group as the creator.' AFTER `last_updated_by`;

UPDATE `app_user`, `bcc_bizterm` SET `bcc_bizterm`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bcc_bizterm`.`created_by`;
UPDATE `app_user`, `bcc_bizterm` SET `bcc_bizterm`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bcc_bizterm`.`last_updated_by`;

ALTER TABLE `bccp`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the user who creates the entity.\n\nThis column never change between the history and the current record. The history record should have the same value as that of its current record.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).' AFTER `last_updated_by`;

UPDATE `app_user`, `bccp` SET `bccp`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bccp`.`created_by`;
UPDATE `app_user`, `bccp` SET `bccp`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bccp`.`owner_user_id`;
UPDATE `app_user`, `bccp` SET `bccp`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `bccp`.`last_updated_by`;

ALTER TABLE `biz_ctx`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the user who creates the entity.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table  referring to the last user who has updated the business context.' AFTER `last_updated_by`;

UPDATE `app_user`, `biz_ctx` SET `biz_ctx`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `biz_ctx`.`created_by`;
UPDATE `app_user`, `biz_ctx` SET `biz_ctx`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `biz_ctx`.`last_updated_by`;

ALTER TABLE `business_term`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the user who creates the business term. The creator of the business term is also its owner by default.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the last user who has updated the business term record. This may be the user who is in the same group as the creator.' AFTER `last_updated_by`;

UPDATE `app_user`, `business_term` SET `business_term`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `business_term`.`created_by`;
UPDATE `app_user`, `business_term` SET `business_term`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `business_term`.`last_updated_by`;

ALTER TABLE `code_list`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the code list.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the code list.' AFTER `last_updated_by`;

UPDATE `app_user`, `code_list` SET `code_list`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `code_list`.`created_by`;
UPDATE `app_user`, `code_list` SET `code_list`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `code_list`.`owner_user_id`;
UPDATE `app_user`, `code_list` SET `code_list`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `code_list`.`last_updated_by`;

ALTER TABLE `code_list_value`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the code list value.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the code list value.' AFTER `last_updated_by`;

UPDATE `app_user`, `code_list_value` SET `code_list_value`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `code_list_value`.`created_by`;
UPDATE `app_user`, `code_list_value` SET `code_list_value`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `code_list_value`.`owner_user_id`;
UPDATE `app_user`, `code_list_value` SET `code_list_value`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `code_list_value`.`last_updated_by`;

ALTER TABLE `comment`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `created_by`;

UPDATE `app_user`, `comment` SET `comment`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `comment`.`created_by`;

ALTER TABLE `ctx_category`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the context category.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the context category.' AFTER `last_updated_by`;

UPDATE `app_user`, `ctx_category` SET `ctx_category`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `ctx_category`.`created_by`;
UPDATE `app_user`, `ctx_category` SET `ctx_category`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `ctx_category`.`last_updated_by`;

ALTER TABLE `ctx_scheme`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this context scheme.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the context scheme.' AFTER `last_updated_by`;

UPDATE `app_user`, `ctx_scheme` SET `ctx_scheme`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `ctx_scheme`.`created_by`;
UPDATE `app_user`, `ctx_scheme` SET `ctx_scheme`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `ctx_scheme`.`last_updated_by`;

ALTER TABLE `dt`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this DT.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).' AFTER `last_updated_by`;

UPDATE `app_user`, `dt` SET `dt`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `dt`.`created_by`;
UPDATE `app_user`, `dt` SET `dt`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `dt`.`owner_user_id`;
UPDATE `app_user`, `dt` SET `dt`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `dt`.`last_updated_by`;

ALTER TABLE `dt_sc`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the DT_SC.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the DT_SC.' AFTER `last_updated_by`;

UPDATE `app_user`, `dt_sc` SET `dt_sc`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `dt_sc`.`created_by`;
UPDATE `app_user`, `dt_sc` SET `dt_sc`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `dt_sc`.`owner_user_id`;
UPDATE `app_user`, `dt_sc` SET `dt_sc`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `dt_sc`.`last_updated_by`;

ALTER TABLE `exception`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who is working on when the exception occurs.' AFTER `created_by`;

UPDATE `app_user`, `exception` SET `exception`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `exception`.`created_by`;

ALTER TABLE `log`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `created_by`;

UPDATE `app_user`, `log` SET `log`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `log`.`created_by`;

ALTER TABLE `message`
    ADD COLUMN `sender_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'The user who created this record.' AFTER `sender_id`,
    ADD COLUMN `recipient_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'The user who is a target to possess this record.' AFTER `recipient_id`;

UPDATE `app_user`, `message` SET `message`.`sender_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `message`.`sender_id`;
UPDATE `app_user`, `message` SET `message`.`recipient_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `message`.`recipient_id`;

ALTER TABLE `module`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this MODULE.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who can update or delete the record.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).' AFTER `last_updated_by`;

UPDATE `app_user`, `module` SET `module`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module`.`created_by`;
UPDATE `app_user`, `module` SET `module`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module`.`owner_user_id`;
UPDATE `app_user`, `module` SET `module`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module`.`last_updated_by`;

ALTER TABLE `module_acc_manifest`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `module_acc_manifest` SET `module_acc_manifest`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_acc_manifest`.`created_by`;
UPDATE `app_user`, `module_acc_manifest` SET `module_acc_manifest`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_acc_manifest`.`last_updated_by`;

ALTER TABLE `module_agency_id_list_manifest`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `module_agency_id_list_manifest` SET `module_agency_id_list_manifest`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_agency_id_list_manifest`.`created_by`;
UPDATE `app_user`, `module_agency_id_list_manifest` SET `module_agency_id_list_manifest`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_agency_id_list_manifest`.`last_updated_by`;

ALTER TABLE `module_asccp_manifest`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `module_asccp_manifest` SET `module_asccp_manifest`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_asccp_manifest`.`created_by`;
UPDATE `app_user`, `module_asccp_manifest` SET `module_asccp_manifest`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_asccp_manifest`.`last_updated_by`;

ALTER TABLE `module_bccp_manifest`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `module_bccp_manifest` SET `module_bccp_manifest`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_bccp_manifest`.`created_by`;
UPDATE `app_user`, `module_bccp_manifest` SET `module_bccp_manifest`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_bccp_manifest`.`last_updated_by`;

ALTER TABLE `module_blob_content_manifest`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `module_blob_content_manifest` SET `module_blob_content_manifest`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_blob_content_manifest`.`created_by`;
UPDATE `app_user`, `module_blob_content_manifest` SET `module_blob_content_manifest`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_blob_content_manifest`.`last_updated_by`;

ALTER TABLE `module_code_list_manifest`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `module_code_list_manifest` SET `module_code_list_manifest`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_code_list_manifest`.`created_by`;
UPDATE `app_user`, `module_code_list_manifest` SET `module_code_list_manifest`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_code_list_manifest`.`last_updated_by`;

ALTER TABLE `module_dt_manifest`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `module_dt_manifest` SET `module_dt_manifest`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_dt_manifest`.`created_by`;
UPDATE `app_user`, `module_dt_manifest` SET `module_dt_manifest`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_dt_manifest`.`last_updated_by`;

ALTER TABLE `module_set`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this MODULE_SET.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `module_set` SET `module_set`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_set`.`created_by`;
UPDATE `app_user`, `module_set` SET `module_set`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_set`.`last_updated_by`;

ALTER TABLE `module_set_release`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this MODULE_SET_RELEASE.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `module_set_release` SET `module_set_release`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_set_release`.`created_by`;
UPDATE `app_user`, `module_set_release` SET `module_set_release`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_set_release`.`last_updated_by`;

ALTER TABLE `module_xbt_manifest`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `module_xbt_manifest` SET `module_xbt_manifest`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_xbt_manifest`.`created_by`;
UPDATE `app_user`, `module_xbt_manifest` SET `module_xbt_manifest`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `module_xbt_manifest`.`last_updated_by`;

ALTER TABLE `namespace`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table identifying user who created the namespace.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who can update or delete the record.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who last updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `namespace` SET `namespace`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `namespace`.`created_by`;
UPDATE `app_user`, `namespace` SET `namespace`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `namespace`.`owner_user_id`;
UPDATE `app_user`, `namespace` SET `namespace`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `namespace`.`last_updated_by`;

ALTER TABLE `release`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table identifying user who created the namespace.' AFTER `created_by`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who last updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `release` SET `release`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `release`.`created_by`;
UPDATE `app_user`, `release` SET `release`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `release`.`last_updated_by`;

ALTER TABLE `top_level_asbiep`
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who can update or delete the record.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key referring to the last user who has updated any related bie records.' AFTER `last_updated_by`;

UPDATE `app_user`, `top_level_asbiep` SET `top_level_asbiep`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `top_level_asbiep`.`owner_user_id`;
UPDATE `app_user`, `top_level_asbiep` SET `top_level_asbiep`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `top_level_asbiep`.`last_updated_by`;

ALTER TABLE `xbt`
    ADD COLUMN `created_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table identifying user who created the xbt.' AFTER `created_by`,
    ADD COLUMN `owner_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who can update or delete the record.' AFTER `owner_user_id`,
    ADD COLUMN `last_updated_by_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who last updated the record.' AFTER `last_updated_by`;

UPDATE `app_user`, `xbt` SET `xbt`.`created_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `xbt`.`created_by`;
UPDATE `app_user`, `xbt` SET `xbt`.`owner_user_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `xbt`.`owner_user_id`;
UPDATE `app_user`, `xbt` SET `xbt`.`last_updated_by_uuid` = `app_user`.`app_user_uuid`
WHERE `app_user`.`app_user_id` = `xbt`.`last_updated_by`;

-- Drop old `app_user_id` columns
ALTER TABLE `abie` DROP FOREIGN KEY `abie_created_by_fk`;
ALTER TABLE `abie` DROP COLUMN `created_by`;
ALTER TABLE `abie` DROP FOREIGN KEY `abie_last_updated_by_fk`;
ALTER TABLE `abie` DROP COLUMN `last_updated_by`;

ALTER TABLE `acc` DROP FOREIGN KEY `acc_created_by_fk`;
ALTER TABLE `acc` DROP COLUMN `created_by`;
ALTER TABLE `acc` DROP FOREIGN KEY `acc_last_updated_by_fk`;
ALTER TABLE `acc` DROP COLUMN `last_updated_by`;
ALTER TABLE `acc` DROP FOREIGN KEY `acc_owner_user_id_fk`;
ALTER TABLE `acc` DROP COLUMN `owner_user_id`;

ALTER TABLE `agency_id_list` DROP FOREIGN KEY `agency_id_list_created_by_fk`;
ALTER TABLE `agency_id_list` DROP COLUMN `created_by`;
ALTER TABLE `agency_id_list` DROP FOREIGN KEY `agency_id_list_last_updated_by_fk`;
ALTER TABLE `agency_id_list` DROP COLUMN `last_updated_by`;
ALTER TABLE `agency_id_list` DROP FOREIGN KEY `agency_id_list_owner_user_id_fk`;
ALTER TABLE `agency_id_list` DROP COLUMN `owner_user_id`;

ALTER TABLE `agency_id_list_value` DROP FOREIGN KEY `agency_id_list_value_created_by_fk`;
ALTER TABLE `agency_id_list_value` DROP COLUMN `created_by`;
ALTER TABLE `agency_id_list_value` DROP FOREIGN KEY `agency_id_list_value_last_updated_by_fk`;
ALTER TABLE `agency_id_list_value` DROP COLUMN `last_updated_by`;
ALTER TABLE `agency_id_list_value` DROP FOREIGN KEY `agency_id_list_value_owner_user_id_fk`;
ALTER TABLE `agency_id_list_value` DROP COLUMN `owner_user_id`;

ALTER TABLE `app_oauth2_user` DROP FOREIGN KEY `app_oauth2_user_app_user_id_fk`;
ALTER TABLE `app_oauth2_user` DROP COLUMN `app_user_id`;

ALTER TABLE `asbie` DROP FOREIGN KEY `asbie_created_by_fk`;
ALTER TABLE `asbie` DROP COLUMN `created_by`;
ALTER TABLE `asbie` DROP FOREIGN KEY `asbie_last_updated_by_fk`;
ALTER TABLE `asbie` DROP COLUMN `last_updated_by`;

ALTER TABLE `asbie_bizterm` DROP COLUMN `created_by`;
ALTER TABLE `asbie_bizterm` DROP COLUMN `last_updated_by`;

ALTER TABLE `asbiep` DROP FOREIGN KEY `asbiep_created_by_fk`;
ALTER TABLE `asbiep` DROP COLUMN `created_by`;
ALTER TABLE `asbiep` DROP FOREIGN KEY `asbiep_last_updated_by_fk`;
ALTER TABLE `asbiep` DROP COLUMN `last_updated_by`;

ALTER TABLE `ascc` DROP FOREIGN KEY `ascc_created_by_fk`;
ALTER TABLE `ascc` DROP COLUMN `created_by`;
ALTER TABLE `ascc` DROP FOREIGN KEY `ascc_last_updated_by_fk`;
ALTER TABLE `ascc` DROP COLUMN `last_updated_by`;
ALTER TABLE `ascc` DROP FOREIGN KEY `ascc_owner_user_id_fk`;
ALTER TABLE `ascc` DROP COLUMN `owner_user_id`;

ALTER TABLE `ascc_bizterm` DROP COLUMN `created_by`;
ALTER TABLE `ascc_bizterm` DROP COLUMN `last_updated_by`;

ALTER TABLE `asccp` DROP FOREIGN KEY `asccp_created_by_fk`;
ALTER TABLE `asccp` DROP COLUMN `created_by`;
ALTER TABLE `asccp` DROP FOREIGN KEY `asccp_last_updated_by_fk`;
ALTER TABLE `asccp` DROP COLUMN `last_updated_by`;
ALTER TABLE `asccp` DROP FOREIGN KEY `asccp_owner_user_id_fk`;
ALTER TABLE `asccp` DROP COLUMN `owner_user_id`;

ALTER TABLE `bbie` DROP FOREIGN KEY `bbie_created_by_fk`;
ALTER TABLE `bbie` DROP COLUMN `created_by`;
ALTER TABLE `bbie` DROP FOREIGN KEY `bbie_last_updated_by_fk`;
ALTER TABLE `bbie` DROP COLUMN `last_updated_by`;

ALTER TABLE `bbie_bizterm` DROP COLUMN `created_by`;
ALTER TABLE `bbie_bizterm` DROP COLUMN `last_updated_by`;

ALTER TABLE `bbie_sc` DROP FOREIGN KEY `bbie_sc_created_by_fk`;
ALTER TABLE `bbie_sc` DROP COLUMN `created_by`;
ALTER TABLE `bbie_sc` DROP FOREIGN KEY `bbie_sc_last_updated_by_fk`;
ALTER TABLE `bbie_sc` DROP COLUMN `last_updated_by`;

ALTER TABLE `bbiep` DROP FOREIGN KEY `bbiep_created_by_fk`;
ALTER TABLE `bbiep` DROP COLUMN `created_by`;
ALTER TABLE `bbiep` DROP FOREIGN KEY `bbiep_last_updated_by_fk`;
ALTER TABLE `bbiep` DROP COLUMN `last_updated_by`;

ALTER TABLE `bcc` DROP FOREIGN KEY `bcc_created_by_fk`;
ALTER TABLE `bcc` DROP COLUMN `created_by`;
ALTER TABLE `bcc` DROP FOREIGN KEY `bcc_last_updated_by_fk`;
ALTER TABLE `bcc` DROP COLUMN `last_updated_by`;
ALTER TABLE `bcc` DROP FOREIGN KEY `bcc_owner_user_id_fk`;
ALTER TABLE `bcc` DROP COLUMN `owner_user_id`;

ALTER TABLE `bcc_bizterm` DROP COLUMN `created_by`;
ALTER TABLE `bcc_bizterm` DROP COLUMN `last_updated_by`;

ALTER TABLE `bccp` DROP FOREIGN KEY `bccp_created_by_fk`;
ALTER TABLE `bccp` DROP COLUMN `created_by`;
ALTER TABLE `bccp` DROP FOREIGN KEY `bccp_last_updated_by_fk`;
ALTER TABLE `bccp` DROP COLUMN `last_updated_by`;
ALTER TABLE `bccp` DROP FOREIGN KEY `bccp_owner_user_id_fk`;
ALTER TABLE `bccp` DROP COLUMN `owner_user_id`;

ALTER TABLE `biz_ctx` DROP FOREIGN KEY `biz_ctx_created_by_fk`;
ALTER TABLE `biz_ctx` DROP COLUMN `created_by`;
ALTER TABLE `biz_ctx` DROP FOREIGN KEY `biz_ctx_last_updated_by_fk`;
ALTER TABLE `biz_ctx` DROP COLUMN `last_updated_by`;

ALTER TABLE `business_term` DROP COLUMN `created_by`;
ALTER TABLE `business_term` DROP COLUMN `last_updated_by`;

ALTER TABLE `code_list` DROP FOREIGN KEY `code_list_created_by_fk`;
ALTER TABLE `code_list` DROP COLUMN `created_by`;
ALTER TABLE `code_list` DROP FOREIGN KEY `code_list_last_updated_by_fk`;
ALTER TABLE `code_list` DROP COLUMN `last_updated_by`;
ALTER TABLE `code_list` DROP FOREIGN KEY `code_list_owner_user_id_fk`;
ALTER TABLE `code_list` DROP COLUMN `owner_user_id`;

ALTER TABLE `code_list_value` DROP FOREIGN KEY `code_list_value_created_by_fk`;
ALTER TABLE `code_list_value` DROP COLUMN `created_by`;
ALTER TABLE `code_list_value` DROP FOREIGN KEY `code_list_value_last_updated_by_fk`;
ALTER TABLE `code_list_value` DROP COLUMN `last_updated_by`;
ALTER TABLE `code_list_value` DROP FOREIGN KEY `code_list_value_owner_user_id_fk`;
ALTER TABLE `code_list_value` DROP COLUMN `owner_user_id`;

ALTER TABLE `comment` DROP FOREIGN KEY `comment_created_by_fk`;
ALTER TABLE `comment` DROP COLUMN `created_by`;

ALTER TABLE `ctx_category` DROP FOREIGN KEY `ctx_category_created_by_fk`;
ALTER TABLE `ctx_category` DROP COLUMN `created_by`;
ALTER TABLE `ctx_category` DROP FOREIGN KEY `ctx_category_last_updated_by_fk`;
ALTER TABLE `ctx_category` DROP COLUMN `last_updated_by`;

ALTER TABLE `ctx_scheme` DROP FOREIGN KEY `ctx_scheme_created_by_fk`;
ALTER TABLE `ctx_scheme` DROP COLUMN `created_by`;
ALTER TABLE `ctx_scheme` DROP FOREIGN KEY `ctx_scheme_last_updated_by_fk`;
ALTER TABLE `ctx_scheme` DROP COLUMN `last_updated_by`;

ALTER TABLE `dt` DROP FOREIGN KEY `dt_created_by_fk`;
ALTER TABLE `dt` DROP COLUMN `created_by`;
ALTER TABLE `dt` DROP FOREIGN KEY `dt_last_updated_by_fk`;
ALTER TABLE `dt` DROP COLUMN `last_updated_by`;
ALTER TABLE `dt` DROP FOREIGN KEY `dt_owner_user_id_fk`;
ALTER TABLE `dt` DROP COLUMN `owner_user_id`;

ALTER TABLE `dt_sc` DROP FOREIGN KEY `dt_sc_created_by_fk`;
ALTER TABLE `dt_sc` DROP COLUMN `created_by`;
ALTER TABLE `dt_sc` DROP FOREIGN KEY `dt_sc_last_updated_by_fk`;
ALTER TABLE `dt_sc` DROP COLUMN `last_updated_by`;
ALTER TABLE `dt_sc` DROP FOREIGN KEY `dt_sc_owner_user_id_fk`;
ALTER TABLE `dt_sc` DROP COLUMN `owner_user_id`;

ALTER TABLE `exception` DROP FOREIGN KEY `exception_created_by_fk`;
ALTER TABLE `exception` DROP COLUMN `created_by`;

ALTER TABLE `log` DROP FOREIGN KEY `log_created_by_fk`;
ALTER TABLE `log` DROP COLUMN `created_by`;

ALTER TABLE `message` DROP FOREIGN KEY `message_sender_id_fk`;
ALTER TABLE `message` DROP COLUMN `sender_id`;
ALTER TABLE `message` DROP FOREIGN KEY `message_recipient_id_fk`;
ALTER TABLE `message` DROP COLUMN `recipient_id`;

ALTER TABLE `module` DROP FOREIGN KEY `module_created_by_fk`;
ALTER TABLE `module` DROP COLUMN `created_by`;
ALTER TABLE `module` DROP FOREIGN KEY `module_last_updated_by_fk`;
ALTER TABLE `module` DROP COLUMN `last_updated_by`;
ALTER TABLE `module` DROP FOREIGN KEY `module_owner_user_id_fk`;
ALTER TABLE `module` DROP COLUMN `owner_user_id`;

ALTER TABLE `module_acc_manifest` DROP FOREIGN KEY `module_acc_manifest_created_by_fk`;
ALTER TABLE `module_acc_manifest` DROP COLUMN `created_by`;
ALTER TABLE `module_acc_manifest` DROP FOREIGN KEY `module_acc_manifest_last_updated_by_fk`;
ALTER TABLE `module_acc_manifest` DROP COLUMN `last_updated_by`;

ALTER TABLE `module_agency_id_list_manifest` DROP FOREIGN KEY `module_agency_id_list_manifest_created_by_fk`;
ALTER TABLE `module_agency_id_list_manifest` DROP COLUMN `created_by`;
ALTER TABLE `module_agency_id_list_manifest` DROP FOREIGN KEY `module_agency_id_list_manifest_last_updated_by_fk`;
ALTER TABLE `module_agency_id_list_manifest` DROP COLUMN `last_updated_by`;

ALTER TABLE `module_asccp_manifest` DROP FOREIGN KEY `module_asccp_manifest_created_by_fk`;
ALTER TABLE `module_asccp_manifest` DROP COLUMN `created_by`;
ALTER TABLE `module_asccp_manifest` DROP FOREIGN KEY `module_asccp_manifest_last_updated_by_fk`;
ALTER TABLE `module_asccp_manifest` DROP COLUMN `last_updated_by`;

ALTER TABLE `module_bccp_manifest` DROP FOREIGN KEY `module_bccp_manifest_created_by_fk`;
ALTER TABLE `module_bccp_manifest` DROP COLUMN `created_by`;
ALTER TABLE `module_bccp_manifest` DROP FOREIGN KEY `module_bccp_manifest_last_updated_by_fk`;
ALTER TABLE `module_bccp_manifest` DROP COLUMN `last_updated_by`;

ALTER TABLE `module_blob_content_manifest` DROP FOREIGN KEY `module_blob_content_manifest_created_by_fk`;
ALTER TABLE `module_blob_content_manifest` DROP COLUMN `created_by`;
ALTER TABLE `module_blob_content_manifest` DROP FOREIGN KEY `module_blob_content_manifest_last_updated_by_fk`;
ALTER TABLE `module_blob_content_manifest` DROP COLUMN `last_updated_by`;

ALTER TABLE `module_code_list_manifest` DROP FOREIGN KEY `module_code_list_manifest_created_by_fk`;
ALTER TABLE `module_code_list_manifest` DROP COLUMN `created_by`;
ALTER TABLE `module_code_list_manifest` DROP FOREIGN KEY `module_code_list_manifest_last_updated_by_fk`;
ALTER TABLE `module_code_list_manifest` DROP COLUMN `last_updated_by`;

ALTER TABLE `module_dt_manifest` DROP FOREIGN KEY `module_dt_manifest_created_by_fk`;
ALTER TABLE `module_dt_manifest` DROP COLUMN `created_by`;
ALTER TABLE `module_dt_manifest` DROP FOREIGN KEY `module_dt_manifest_last_updated_by_fk`;
ALTER TABLE `module_dt_manifest` DROP COLUMN `last_updated_by`;

ALTER TABLE `module_set` DROP FOREIGN KEY `module_set_created_by_fk`;
ALTER TABLE `module_set` DROP COLUMN `created_by`;
ALTER TABLE `module_set` DROP FOREIGN KEY `module_set_last_updated_by_fk`;
ALTER TABLE `module_set` DROP COLUMN `last_updated_by`;

ALTER TABLE `module_set_release` DROP FOREIGN KEY `module_set_release_assignment_created_by_fk`;
ALTER TABLE `module_set_release` DROP COLUMN `created_by`;
ALTER TABLE `module_set_release` DROP FOREIGN KEY `module_set_release_assignment_last_updated_by_fk`;
ALTER TABLE `module_set_release` DROP COLUMN `last_updated_by`;

ALTER TABLE `module_xbt_manifest` DROP FOREIGN KEY `module_xbt_manifest_created_by_fk`;
ALTER TABLE `module_xbt_manifest` DROP COLUMN `created_by`;
ALTER TABLE `module_xbt_manifest` DROP FOREIGN KEY `module_xbt_manifest_last_updated_by_fk`;
ALTER TABLE `module_xbt_manifest` DROP COLUMN `last_updated_by`;

ALTER TABLE `namespace` DROP FOREIGN KEY `namespace_created_by_fk`;
ALTER TABLE `namespace` DROP COLUMN `created_by`;
ALTER TABLE `namespace` DROP FOREIGN KEY `namespace_last_updated_by_fk`;
ALTER TABLE `namespace` DROP COLUMN `last_updated_by`;
ALTER TABLE `namespace` DROP FOREIGN KEY `namespace_owner_user_id_fk`;
ALTER TABLE `namespace` DROP COLUMN `owner_user_id`;

ALTER TABLE `release` DROP FOREIGN KEY `release_created_by_fk`;
ALTER TABLE `release` DROP COLUMN `created_by`;
ALTER TABLE `release` DROP FOREIGN KEY `release_last_updated_by_fk`;
ALTER TABLE `release` DROP COLUMN `last_updated_by`;

ALTER TABLE `top_level_asbiep` DROP FOREIGN KEY `top_level_asbiep_owner_user_id_fk`;
ALTER TABLE `top_level_asbiep` DROP COLUMN `owner_user_id`;
ALTER TABLE `top_level_asbiep` DROP FOREIGN KEY `top_level_asbiep_last_updated_by_fk`;
ALTER TABLE `top_level_asbiep` DROP COLUMN `last_updated_by`;

ALTER TABLE `xbt` DROP FOREIGN KEY `xbt_created_by_fk`;
ALTER TABLE `xbt` DROP COLUMN `created_by`;
ALTER TABLE `xbt` DROP FOREIGN KEY `xbt_last_updated_by_fk`;
ALTER TABLE `xbt` DROP COLUMN `last_updated_by`;
ALTER TABLE `xbt` DROP FOREIGN KEY `xbt_owner_user_id_fk`;
ALTER TABLE `xbt` DROP COLUMN `owner_user_id`;

ALTER TABLE `app_user` DROP COLUMN `app_user_id`;

-- Rename `app_user_uuid` TO `app_user_id`
ALTER TABLE `app_user` CHANGE `app_user_uuid` `app_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';

ALTER TABLE `abie`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who creates the ABIE. The creator of the ABIE is also its owner by default. ABIEs created as children of another ABIE have the same CREATED_BY as its parent.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the last user who has updated the ABIE record. This may be the user who is in the same group as the creator.';

ALTER TABLE `acc`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the user who creates the entity.\n\nThis column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).';

ALTER TABLE `agency_id_list`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the agency ID list.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the agency ID list.';

ALTER TABLE `agency_id_list_value`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the agency ID list value.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the agency ID list value.';

ALTER TABLE `app_oauth2_user`
    CHANGE `app_user_uuid` `app_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A reference to the record in `app_user`. If it is not set, this is treated as a pending record.';

ALTER TABLE `asbie`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who creates the ASBIE. The creator of the ASBIE is also its owner by default. ASBIEs created as children of another ABIE have the same CREATED_BY.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who has last updated the ASBIE record.';

ALTER TABLE `asbie_bizterm`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who creates the ASBIE_BIZTERM record. The creator of the ASBIE_BIZTERM is also its owner by default.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the last user who has updated the ASBIE_BIZTERM record. This may be the user who is in the same group as the creator.';

ALTER TABLE `asbiep`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who creates the ASBIEP. The creator of the ASBIEP is also its owner by default. ASBIEPs created as children of another ABIE have the same CREATED_BY.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the last user who has updated the ASBIEP record.';

ALTER TABLE `ascc`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key to the APP_USER table referring to the user who creates the entity.\n\nThis column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).';

ALTER TABLE `ascc_bizterm`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who creates the ASCC_BIZTERM record. The creator of the ASCC_BIZTERM is also its owner by default.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the last user who has updated the ASCC_BIZTERM record. This may be the user who is in the same group as the creator.';

ALTER TABLE `asccp`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the user who creates the entity. \n\nThis column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).';

ALTER TABLE `bbie`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who creates the BBIE. The creator of the BBIE is also its owner by default. BBIEs created as children of another ABIE have the same CREATED_BY.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who has last updated the BBIE record.';

ALTER TABLE `bbie_bizterm`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who creates the BBIE_BIZTERM record. The creator of the ASBIE_BIZTERM is also its owner by default.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the last user who has updated the BBIE_BIZTERM record. This may be the user who is in the same group as the creator.';

ALTER TABLE `bbie_sc`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who creates the BBIE_SC. The creator of the BBIE_SC is also its owner by default.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who has last updated the BBIE_SC record.';

ALTER TABLE `bbiep`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who creates the BBIEP. The creator of the BBIEP is also its owner by default. BBIEPs created as children of another ABIE have the same CREATED_BY'',',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the last user who has updated the BBIEP record.';

ALTER TABLE `bcc`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the user who creates the entity.\n\nThis column never change between the history and the current record. The history record should have the same value as that of its current record.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).';

ALTER TABLE `bcc_bizterm`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who creates the BCC_BIZTERM record. The creator of the BCC_BIZTERM is also its owner by default.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the last user who has updated the BCC_BIZTERM record. This may be the user who is in the same group as the creator.';

ALTER TABLE `bccp`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the user who creates the entity.\n\nThis column never change between the history and the current record. The history record should have the same value as that of its current record.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).';

ALTER TABLE `biz_ctx`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the user who creates the entity.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table  referring to the last user who has updated the business context.';

ALTER TABLE `business_term`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the user who creates the business term. The creator of the business term is also its owner by default.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the last user who has updated the business term record. This may be the user who is in the same group as the creator.';

ALTER TABLE `code_list`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the code list.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the code list.';

ALTER TABLE `code_list_value`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the code list value.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the code list value.';

ALTER TABLE `comment`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL;

ALTER TABLE `ctx_category`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the context category.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the context category.';

ALTER TABLE `ctx_scheme`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this context scheme.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the context scheme.';

ALTER TABLE `dt`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this DT.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).';

ALTER TABLE `dt_sc`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the DT_SC.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the DT_SC.';

ALTER TABLE `exception`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who is working on when the exception occurs.';

ALTER TABLE `log`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL;

ALTER TABLE `message`
    CHANGE `sender_uuid` `sender_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'The user who created this record.',
    CHANGE `recipient_uuid` `recipient_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'The user who is a target to possess this record.';

ALTER TABLE `module`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this MODULE.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who can update or delete the record.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).';

ALTER TABLE `module_acc_manifest`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.';

ALTER TABLE `module_agency_id_list_manifest`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.';

ALTER TABLE `module_asccp_manifest`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.';

ALTER TABLE `module_bccp_manifest`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.';

ALTER TABLE `module_blob_content_manifest`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.';

ALTER TABLE `module_code_list_manifest`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.';

ALTER TABLE `module_dt_manifest`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.';

ALTER TABLE `module_set`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this MODULE_SET.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.';

ALTER TABLE `module_set_release`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this MODULE_SET_RELEASE.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.';

ALTER TABLE `module_xbt_manifest`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.';

ALTER TABLE `namespace`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table identifying user who created the namespace.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who can update or delete the record.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who last updated the record.';

ALTER TABLE `release`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table identifying user who created the namespace.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who last updated the record.';

ALTER TABLE `top_level_asbiep`
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who can update or delete the record.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key referring to the last user who has updated any related bie records.';

ALTER TABLE `xbt`
    CHANGE `created_by_uuid` `created_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table identifying user who created the xbt.',
    CHANGE `owner_user_uuid` `owner_user_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who can update or delete the record.',
    CHANGE `last_updated_by_uuid` `last_updated_by` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the APP_USER table identifying the user who last updated the record.';

-- Add foreign key constraints
ALTER TABLE `app_user` ADD PRIMARY KEY (`app_user_id`);
ALTER TABLE `abie`
    ADD CONSTRAINT `abie_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `abie_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `acc`
    ADD CONSTRAINT `acc_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `acc_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `acc_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `agency_id_list`
    ADD CONSTRAINT `agency_id_list_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `agency_id_list_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `agency_id_list_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `agency_id_list_value`
    ADD CONSTRAINT `agency_id_list_value_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `agency_id_list_value_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `agency_id_list_value_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `app_oauth2_user`
    ADD CONSTRAINT `app_oauth2_user_app_user_id_fk` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `asbie`
    ADD CONSTRAINT `asbie_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `asbie_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `asbie_bizterm`
    ADD CONSTRAINT `asbie_bizterm_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `asbie_bizterm_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `asbiep`
    ADD CONSTRAINT `asbiep_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `asbiep_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `ascc`
    ADD CONSTRAINT `ascc_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `ascc_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `ascc_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `ascc_bizterm`
    ADD CONSTRAINT `ascc_bizterm_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `ascc_bizterm_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `asccp`
    ADD CONSTRAINT `asccp_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `asccp_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `asccp_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `bbie`
    ADD CONSTRAINT `bbie_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `bbie_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `bbie_bizterm`
    ADD CONSTRAINT `bbie_bizterm_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `bbie_bizterm_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `bbie_sc`
    ADD CONSTRAINT `bbie_sc_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `bbie_sc_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `bbiep`
    ADD CONSTRAINT `bbiep_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `bbiep_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `bcc`
    ADD CONSTRAINT `bcc_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `bcc_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `bcc_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `bcc_bizterm`
    ADD CONSTRAINT `bcc_bizterm_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `bcc_bizterm_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `bccp`
    ADD CONSTRAINT `bccp_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `bccp_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `bccp_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `biz_ctx`
    ADD CONSTRAINT `biz_ctx_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `biz_ctx_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `business_term`
    ADD CONSTRAINT `business_term_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `business_term_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `code_list`
    ADD CONSTRAINT `code_list_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `code_list_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `code_list_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `code_list_value`
    ADD CONSTRAINT `code_list_value_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `code_list_value_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `code_list_value_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `comment`
    ADD CONSTRAINT `comment_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `ctx_category`
    ADD CONSTRAINT `ctx_category_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `ctx_category_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `ctx_scheme`
    ADD CONSTRAINT `ctx_scheme_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `ctx_scheme_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `dt`
    ADD CONSTRAINT `dt_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `dt_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `dt_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `dt_sc`
    ADD CONSTRAINT `dt_sc_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `dt_sc_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `dt_sc_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `exception`
    ADD CONSTRAINT `exception_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `log`
    ADD CONSTRAINT `log_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `message`
    ADD CONSTRAINT `message_sender_id_fk` FOREIGN KEY (`sender_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `message_recipient_id_fk` FOREIGN KEY (`recipient_id`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `module`
    ADD CONSTRAINT `module_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `module_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `module_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `module_acc_manifest`
    ADD CONSTRAINT `module_acc_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `module_acc_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `module_agency_id_list_manifest`
    ADD CONSTRAINT `module_agency_id_list_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `module_agency_id_list_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `module_asccp_manifest`
    ADD CONSTRAINT `module_asccp_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `module_asccp_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `module_bccp_manifest`
    ADD CONSTRAINT `module_bccp_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `module_bccp_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `module_blob_content_manifest`
    ADD CONSTRAINT `module_blob_content_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `module_blob_content_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `module_code_list_manifest`
    ADD CONSTRAINT `module_code_list_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `module_code_list_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `module_dt_manifest`
    ADD CONSTRAINT `module_dt_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `module_dt_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `module_set`
    ADD CONSTRAINT `module_set_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `module_set_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `module_set_release`
    ADD CONSTRAINT `module_set_release_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `module_set_release_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `module_xbt_manifest`
    ADD CONSTRAINT `module_xbt_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `module_xbt_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `namespace`
    ADD CONSTRAINT `namespace_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `namespace_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `namespace_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `release`
    ADD CONSTRAINT `release_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `release_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `top_level_asbiep`
    ADD CONSTRAINT `top_level_asbiep_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `top_level_asbiep_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

ALTER TABLE `xbt`
    ADD CONSTRAINT `xbt_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `xbt_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `xbt_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

-- --------------------------------
-- Change 'namespace_id' TO UUID --
-- --------------------------------
ALTER TABLE `namespace` ADD COLUMN `namespace_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `namespace_id`;
CALL update_uuid('namespace');

ALTER TABLE `acc` ADD COLUMN `namespace_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.' AFTER `namespace_id`;
UPDATE `namespace`, `acc` SET `acc`.`namespace_uuid` = `namespace`.`namespace_uuid`
WHERE `namespace`.`namespace_id` = `acc`.`namespace_id`;

ALTER TABLE `agency_id_list` ADD COLUMN `namespace_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.' AFTER `namespace_id`;
UPDATE `namespace`, `agency_id_list` SET `agency_id_list`.`namespace_uuid` = `namespace`.`namespace_uuid`
WHERE `namespace`.`namespace_id` = `agency_id_list`.`namespace_id`;

ALTER TABLE `asccp` ADD COLUMN `namespace_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.' AFTER `namespace_id`;
UPDATE `namespace`, `asccp` SET `asccp`.`namespace_uuid` = `namespace`.`namespace_uuid`
WHERE `namespace`.`namespace_id` = `asccp`.`namespace_id`;

ALTER TABLE `bccp` ADD COLUMN `namespace_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.' AFTER `namespace_id`;
UPDATE `namespace`, `bccp` SET `bccp`.`namespace_uuid` = `namespace`.`namespace_uuid`
WHERE `namespace`.`namespace_id` = `bccp`.`namespace_id`;

ALTER TABLE `code_list` ADD COLUMN `namespace_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.' AFTER `namespace_id`;
UPDATE `namespace`, `code_list` SET `code_list`.`namespace_uuid` = `namespace`.`namespace_uuid`
WHERE `namespace`.`namespace_id` = `code_list`.`namespace_id`;

ALTER TABLE `dt` ADD COLUMN `namespace_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.' AFTER `namespace_id`;
UPDATE `namespace`, `dt` SET `dt`.`namespace_uuid` = `namespace`.`namespace_uuid`
WHERE `namespace`.`namespace_id` = `dt`.`namespace_id`;

ALTER TABLE `module` ADD COLUMN `namespace_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.' AFTER `namespace_id`;
UPDATE `namespace`, `module` SET `module`.`namespace_uuid` = `namespace`.`namespace_uuid`
WHERE `namespace`.`namespace_id` = `module`.`namespace_id`;

ALTER TABLE `release` ADD COLUMN `namespace_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.' AFTER `namespace_id`;
UPDATE `namespace`, `release` SET `release`.`namespace_uuid` = `namespace`.`namespace_uuid`
WHERE `namespace`.`namespace_id` = `release`.`namespace_id`;

-- Drop old `namespace_id` columns
ALTER TABLE `acc` DROP FOREIGN KEY `acc_namespace_id_fk`;
ALTER TABLE `acc` DROP COLUMN `namespace_id`;

ALTER TABLE `agency_id_list` DROP FOREIGN KEY `agency_id_list_namespace_id_fk`;
ALTER TABLE `agency_id_list` DROP COLUMN `namespace_id`;

ALTER TABLE `asccp` DROP FOREIGN KEY `asccp_namespace_id_fk`;
ALTER TABLE `asccp` DROP COLUMN `namespace_id`;

ALTER TABLE `bccp` DROP FOREIGN KEY `bccp_namespace_id_fk`;
ALTER TABLE `bccp` DROP COLUMN `namespace_id`;

ALTER TABLE `code_list` DROP FOREIGN KEY `code_list_namespace_id_fk`;
ALTER TABLE `code_list` DROP COLUMN `namespace_id`;

ALTER TABLE `dt` DROP FOREIGN KEY `dt_namespace_id_fk`;
ALTER TABLE `dt` DROP COLUMN `namespace_id`;

ALTER TABLE `module` DROP FOREIGN KEY `module_namespace_id_fk`;
ALTER TABLE `module` DROP COLUMN `namespace_id`;

ALTER TABLE `release` DROP FOREIGN KEY `release_namespace_id_fk`;
ALTER TABLE `release` DROP COLUMN `namespace_id`;

ALTER TABLE `namespace` DROP COLUMN `namespace_id`;

-- Rename `namespace_uuid` TO `namespace_id`
ALTER TABLE `namespace` CHANGE `namespace_uuid` `namespace_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `acc` CHANGE `namespace_uuid` `namespace_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.';
ALTER TABLE `agency_id_list` CHANGE `namespace_uuid` `namespace_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.';
ALTER TABLE `asccp` CHANGE `namespace_uuid` `namespace_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.';
ALTER TABLE `bccp` CHANGE `namespace_uuid` `namespace_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.';
ALTER TABLE `code_list` CHANGE `namespace_uuid` `namespace_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.';
ALTER TABLE `dt` CHANGE `namespace_uuid` `namespace_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.';
ALTER TABLE `module` CHANGE `namespace_uuid` `namespace_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.';
ALTER TABLE `release` CHANGE `namespace_uuid` `namespace_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table.';

-- Add foreign key constraints
ALTER TABLE `namespace` ADD PRIMARY KEY (`namespace_id`);
ALTER TABLE `acc` ADD CONSTRAINT `acc_namespace_id_fk` FOREIGN KEY (`namespace_id`) REFERENCES `namespace` (`namespace_id`);
ALTER TABLE `agency_id_list` ADD CONSTRAINT `agency_id_list_namespace_id_fk` FOREIGN KEY (`namespace_id`) REFERENCES `namespace` (`namespace_id`);
ALTER TABLE `asccp` ADD CONSTRAINT `asccp_namespace_id_fk` FOREIGN KEY (`namespace_id`) REFERENCES `namespace` (`namespace_id`);
ALTER TABLE `bccp` ADD CONSTRAINT `bccp_namespace_id_fk` FOREIGN KEY (`namespace_id`) REFERENCES `namespace` (`namespace_id`);
ALTER TABLE `code_list` ADD CONSTRAINT `code_list_namespace_id_fk` FOREIGN KEY (`namespace_id`) REFERENCES `namespace` (`namespace_id`);
ALTER TABLE `dt` ADD CONSTRAINT `dt_namespace_id_fk` FOREIGN KEY (`namespace_id`) REFERENCES `namespace` (`namespace_id`);
ALTER TABLE `module` ADD CONSTRAINT `module_namespace_id_fk` FOREIGN KEY (`namespace_id`) REFERENCES `namespace` (`namespace_id`);
ALTER TABLE `release` ADD CONSTRAINT `release_namespace_id_fk` FOREIGN KEY (`namespace_id`) REFERENCES `namespace` (`namespace_id`);

-- --------------------------------
-- Change `release_id` TO UUID --
-- --------------------------------
ALTER TABLE `release` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `release_id`;
CALL update_uuid('release');

-- Static UUID for 'Working' release.
UPDATE `release` SET `release_uuid` = 'c6dcd936-4f4c-4c2c-97d0-b0eb62d157e7' WHERE `release_num` = 'Working';

ALTER TABLE `acc_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `acc_manifest` SET `acc_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `acc_manifest`.`release_id`;

ALTER TABLE `agency_id_list_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `agency_id_list_manifest` SET `agency_id_list_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `agency_id_list_manifest`.`release_id`;

ALTER TABLE `agency_id_list_value_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `agency_id_list_value_manifest` SET `agency_id_list_value_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `agency_id_list_value_manifest`.`release_id`;

ALTER TABLE `ascc_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `ascc_manifest` SET `ascc_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `ascc_manifest`.`release_id`;

ALTER TABLE `asccp_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `asccp_manifest` SET `asccp_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `asccp_manifest`.`release_id`;

ALTER TABLE `bcc_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `bcc_manifest` SET `bcc_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `bcc_manifest`.`release_id`;

ALTER TABLE `bccp_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `bccp_manifest` SET `bccp_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `bccp_manifest`.`release_id`;

ALTER TABLE `blob_content_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `blob_content_manifest` SET `blob_content_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `blob_content_manifest`.`release_id`;

ALTER TABLE `code_list_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `code_list_manifest` SET `code_list_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `code_list_manifest`.`release_id`;

ALTER TABLE `code_list_value_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `code_list_value_manifest` SET `code_list_value_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `code_list_value_manifest`.`release_id`;

ALTER TABLE `dt_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `dt_manifest` SET `dt_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `dt_manifest`.`release_id`;

ALTER TABLE `dt_sc_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `dt_sc_manifest` SET `dt_sc_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `dt_sc_manifest`.`release_id`;

ALTER TABLE `module_set_release` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `module_set_release` SET `module_set_release`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `module_set_release`.`release_id`;

ALTER TABLE `top_level_asbiep` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `top_level_asbiep` SET `top_level_asbiep`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `top_level_asbiep`.`release_id`;

ALTER TABLE `xbt_manifest` ADD COLUMN `release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.' AFTER `release_id`;
UPDATE `release`, `xbt_manifest` SET `xbt_manifest`.`release_uuid` = `release`.`release_uuid`
WHERE `release`.`release_id` = `xbt_manifest`.`release_id`;

-- Drop old `release_id` columns
ALTER TABLE `acc_manifest` DROP FOREIGN KEY `acc_manifest_release_id_fk`;
ALTER TABLE `acc_manifest` DROP COLUMN `release_id`;

ALTER TABLE `agency_id_list_manifest` DROP FOREIGN KEY `agency_id_list_manifest_release_id_fk`;
ALTER TABLE `agency_id_list_manifest` DROP COLUMN `release_id`;

ALTER TABLE `agency_id_list_value_manifest` DROP FOREIGN KEY `agency_id_list_value_manifest_release_id_fk`;
ALTER TABLE `agency_id_list_value_manifest` DROP COLUMN `release_id`;

ALTER TABLE `ascc_manifest` DROP FOREIGN KEY `ascc_manifest_release_id_fk`;
ALTER TABLE `ascc_manifest` DROP COLUMN `release_id`;

ALTER TABLE `asccp_manifest` DROP FOREIGN KEY `asccp_manifest_release_id_fk`;
ALTER TABLE `asccp_manifest` DROP COLUMN `release_id`;

ALTER TABLE `bcc_manifest` DROP FOREIGN KEY `bcc_manifest_release_id_fk`;
ALTER TABLE `bcc_manifest` DROP COLUMN `release_id`;

ALTER TABLE `bccp_manifest` DROP FOREIGN KEY `bccp_manifest_release_id_fk`;
ALTER TABLE `bccp_manifest` DROP COLUMN `release_id`;

ALTER TABLE `blob_content_manifest` DROP FOREIGN KEY `blob_content_manifest_release_id_fk`;
ALTER TABLE `blob_content_manifest` DROP COLUMN `release_id`;

ALTER TABLE `code_list_manifest` DROP FOREIGN KEY `code_list_manifest_release_id_fk`;
ALTER TABLE `code_list_manifest` DROP COLUMN `release_id`;

ALTER TABLE `code_list_value_manifest` DROP FOREIGN KEY `code_list_value_manifest_release_id_fk`;
ALTER TABLE `code_list_value_manifest` DROP COLUMN `release_id`;

ALTER TABLE `dt_manifest` DROP FOREIGN KEY `dt_manifest_release_id_fk`;
ALTER TABLE `dt_manifest` DROP COLUMN `release_id`;

ALTER TABLE `dt_sc_manifest` DROP FOREIGN KEY `dt_sc_manifest_release_id_fk`;
ALTER TABLE `dt_sc_manifest` DROP COLUMN `release_id`;

ALTER TABLE `module_set_release` DROP FOREIGN KEY `module_set_release_release_id_fk`;
ALTER TABLE `module_set_release` DROP COLUMN `release_id`;

ALTER TABLE `top_level_asbiep` DROP FOREIGN KEY `top_level_asbiep_release_id_fk`;
ALTER TABLE `top_level_asbiep` DROP COLUMN `release_id`;

ALTER TABLE `xbt_manifest` DROP FOREIGN KEY `xbt_manifest_release_id_fk`;
ALTER TABLE `xbt_manifest` DROP COLUMN `release_id`;

ALTER TABLE `release` DROP COLUMN `release_id`;

-- Rename `release_uuid` TO `release_id`
ALTER TABLE `release` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `acc_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `agency_id_list_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `agency_id_list_value_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `ascc_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `asccp_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `bcc_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `bccp_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `blob_content_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `code_list_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `code_list_value_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `dt_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `dt_sc_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `module_set_release` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `top_level_asbiep` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';
ALTER TABLE `xbt_manifest` CHANGE `release_uuid` `release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the RELEASE table.';

-- Add foreign key constraints
ALTER TABLE `release` ADD PRIMARY KEY (`release_id`);
ALTER TABLE `acc_manifest` ADD CONSTRAINT `acc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `agency_id_list_manifest` ADD CONSTRAINT `agency_id_list_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `agency_id_list_value_manifest` ADD CONSTRAINT `agency_id_list_value_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `ascc_manifest` ADD CONSTRAINT `ascc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `asccp_manifest` ADD CONSTRAINT `asccp_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `bcc_manifest` ADD CONSTRAINT `bcc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `bccp_manifest` ADD CONSTRAINT `bccp_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `blob_content_manifest` ADD CONSTRAINT `blob_content_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `code_list_manifest` ADD CONSTRAINT `code_list_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `code_list_value_manifest` ADD CONSTRAINT `code_list_value_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `dt_manifest` ADD CONSTRAINT `dt_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `dt_sc_manifest` ADD CONSTRAINT `dt_sc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `module_set_release` ADD CONSTRAINT `module_set_release_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `top_level_asbiep` ADD CONSTRAINT `top_level_asbiep_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);
ALTER TABLE `xbt_manifest` ADD CONSTRAINT `xbt_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`);

-- --------------------------
-- Change 'log_id' TO UUID --
-- --------------------------
ALTER TABLE `log` ADD COLUMN `log_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `log_id`;
CALL update_uuid('log');

ALTER TABLE `log`
    ADD COLUMN `prev_log_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `prev_log_id`,
    ADD COLUMN `next_log_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `next_log_id`;

UPDATE `log` AS tmp1, `log` AS tmp2 SET tmp1.`prev_log_uuid` = tmp2.`log_uuid`
WHERE tmp2.`log_id` = tmp1.`prev_log_id`;
UPDATE `log` AS tmp1, `log` AS tmp2 SET tmp1.`next_log_uuid` = tmp2.`log_uuid`
WHERE tmp2.`log_id` = tmp1.`next_log_id`;

ALTER TABLE `acc_manifest` ADD COLUMN `log_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.' AFTER `log_id`;
UPDATE `log`, `acc_manifest` SET `acc_manifest`.`log_uuid` = `log`.`log_uuid`
WHERE `log`.`log_id` = `acc_manifest`.`log_id`;

ALTER TABLE `agency_id_list_manifest` ADD COLUMN `log_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.' AFTER `log_id`;
UPDATE `log`, `agency_id_list_manifest` SET `agency_id_list_manifest`.`log_uuid` = `log`.`log_uuid`
WHERE `log`.`log_id` = `agency_id_list_manifest`.`log_id`;

ALTER TABLE `asccp_manifest` ADD COLUMN `log_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.' AFTER `log_id`;
UPDATE `log`, `asccp_manifest` SET `asccp_manifest`.`log_uuid` = `log`.`log_uuid`
WHERE `log`.`log_id` = `asccp_manifest`.`log_id`;

ALTER TABLE `bccp_manifest` ADD COLUMN `log_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.' AFTER `log_id`;
UPDATE `log`, `bccp_manifest` SET `bccp_manifest`.`log_uuid` = `log`.`log_uuid`
WHERE `log`.`log_id` = `bccp_manifest`.`log_id`;

ALTER TABLE `code_list_manifest` ADD COLUMN `log_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.' AFTER `log_id`;
UPDATE `log`, `code_list_manifest` SET `code_list_manifest`.`log_uuid` = `log`.`log_uuid`
WHERE `log`.`log_id` = `code_list_manifest`.`log_id`;

ALTER TABLE `dt_manifest` ADD COLUMN `log_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.' AFTER `log_id`;
UPDATE `log`, `dt_manifest` SET `dt_manifest`.`log_uuid` = `log`.`log_uuid`
WHERE `log`.`log_id` = `dt_manifest`.`log_id`;

ALTER TABLE `xbt_manifest` ADD COLUMN `log_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.' AFTER `log_id`;
UPDATE `log`, `xbt_manifest` SET `xbt_manifest`.`log_uuid` = `log`.`log_uuid`
WHERE `log`.`log_id` = `xbt_manifest`.`log_id`;

-- Drop old `log_id` columns
ALTER TABLE `acc_manifest` DROP FOREIGN KEY `acc_manifest_log_id_fk`;
ALTER TABLE `acc_manifest` DROP COLUMN `log_id`;

ALTER TABLE `agency_id_list_manifest` DROP FOREIGN KEY `agency_id_list_manifest_log_id_fk`;
ALTER TABLE `agency_id_list_manifest` DROP COLUMN `log_id`;

ALTER TABLE `asccp_manifest` DROP FOREIGN KEY `asccp_manifest_log_id_fk`;
ALTER TABLE `asccp_manifest` DROP COLUMN `log_id`;

ALTER TABLE `bccp_manifest` DROP FOREIGN KEY `bccp_manifest_log_id_fk`;
ALTER TABLE `bccp_manifest` DROP COLUMN `log_id`;

ALTER TABLE `code_list_manifest` DROP FOREIGN KEY `code_list_manifest_log_id_fk`;
ALTER TABLE `code_list_manifest` DROP COLUMN `log_id`;

ALTER TABLE `dt_manifest` DROP FOREIGN KEY `dt_manifest_log_id_fk`;
ALTER TABLE `dt_manifest` DROP COLUMN `log_id`;

ALTER TABLE `xbt_manifest` DROP FOREIGN KEY `xbt_manifest_log_id_fk`;
ALTER TABLE `xbt_manifest` DROP COLUMN `log_id`;

ALTER TABLE `log` DROP FOREIGN KEY `log_prev_log_id_fk`;
ALTER TABLE `log` DROP COLUMN `prev_log_id`;

ALTER TABLE `log` DROP FOREIGN KEY `log_next_log_id_fk`;
ALTER TABLE `log` DROP COLUMN `next_log_id`;

ALTER TABLE `log` DROP COLUMN `log_id`;

-- Rename `log_uuid` TO `log_id`
ALTER TABLE `log` CHANGE `log_uuid` `log_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `log`
    CHANGE `prev_log_uuid` `prev_log_id` char(36) CHARACTER SET ascii DEFAULT NULL,
    CHANGE `next_log_uuid` `next_log_id` char(36) CHARACTER SET ascii DEFAULT NULL;
ALTER TABLE `acc_manifest` CHANGE `log_uuid` `log_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.';
ALTER TABLE `agency_id_list_manifest` CHANGE `log_uuid` `log_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.';
ALTER TABLE `asccp_manifest` CHANGE `log_uuid` `log_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.';
ALTER TABLE `bccp_manifest` CHANGE `log_uuid` `log_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.';
ALTER TABLE `code_list_manifest` CHANGE `log_uuid` `log_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.';
ALTER TABLE `dt_manifest` CHANGE `log_uuid` `log_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.';
ALTER TABLE `xbt_manifest` CHANGE `log_uuid` `log_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.';

-- Add foreign key constraints
ALTER TABLE `log` ADD PRIMARY KEY (`log_id`);
ALTER TABLE `log`
    ADD CONSTRAINT `log_prev_log_id_fk` FOREIGN KEY (`prev_log_id`) REFERENCES `log` (`log_id`),
    ADD CONSTRAINT `log_next_log_id_fk` FOREIGN KEY (`next_log_id`) REFERENCES `log` (`log_id`);
ALTER TABLE `acc_manifest` ADD CONSTRAINT `acc_manifest_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `log` (`log_id`);
ALTER TABLE `agency_id_list_manifest` ADD CONSTRAINT `agency_id_list_manifest_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `log` (`log_id`);
ALTER TABLE `asccp_manifest` ADD CONSTRAINT `asccp_manifest_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `log` (`log_id`);
ALTER TABLE `bccp_manifest` ADD CONSTRAINT `bccp_manifest_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `log` (`log_id`);
ALTER TABLE `code_list_manifest` ADD CONSTRAINT `code_list_manifest_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `log` (`log_id`);
ALTER TABLE `dt_manifest` ADD CONSTRAINT `dt_manifest_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `log` (`log_id`);
ALTER TABLE `xbt_manifest` ADD CONSTRAINT `xbt_manifest_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `log` (`log_id`);

-- ---------------------------------------------------------------------------
-- Change `ctx_category_id`, `ctx_scheme_id`, `ctx_scheme_value_id`,        --
--        `biz_ctx_id`, `biz_ctx_value_id`, `biz_ctx_assignment_id` TO UUID --
-- ---------------------------------------------------------------------------
ALTER TABLE `ctx_category` ADD COLUMN `ctx_category_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `ctx_category_id`;
CALL update_uuid('ctx_category');

ALTER TABLE `ctx_scheme` ADD COLUMN `ctx_scheme_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `ctx_scheme_id`;
CALL update_uuid('ctx_scheme');

ALTER TABLE `ctx_scheme_value` ADD COLUMN `ctx_scheme_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `ctx_scheme_value_id`;
CALL update_uuid('ctx_scheme_value');

ALTER TABLE `biz_ctx` ADD COLUMN `biz_ctx_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `biz_ctx_id`;
CALL update_uuid('biz_ctx');

ALTER TABLE `biz_ctx_value` ADD COLUMN `biz_ctx_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `biz_ctx_value_id`;
CALL update_uuid('biz_ctx_value');

ALTER TABLE `biz_ctx_assignment` ADD COLUMN `biz_ctx_assignment_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `biz_ctx_assignment_id`;
CALL update_uuid('biz_ctx_assignment');

ALTER TABLE `ctx_scheme` ADD COLUMN `ctx_category_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This the foreign key to the CTX_CATEGORY table. It identifies the context category associated with this context scheme.' AFTER `ctx_category_id`;
UPDATE `ctx_category`, `ctx_scheme` SET `ctx_scheme`.`ctx_category_uuid` = `ctx_category`.`ctx_category_uuid`
WHERE `ctx_category`.`ctx_category_id` = `ctx_scheme`.`ctx_category_id`;

ALTER TABLE `ctx_scheme_value` ADD COLUMN `owner_ctx_scheme_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CTX_SCHEME table. It identifies the context scheme, to which this scheme value belongs.' AFTER `owner_ctx_scheme_id`;
UPDATE `ctx_scheme`, `ctx_scheme_value` SET `ctx_scheme_value`.`owner_ctx_scheme_uuid` = `ctx_scheme`.`ctx_scheme_uuid`
WHERE `ctx_scheme`.`ctx_scheme_id` = `ctx_scheme_value`.`owner_ctx_scheme_id`;

ALTER TABLE `biz_ctx_value` ADD COLUMN `ctx_scheme_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CTX_SCHEME_VALUE table.' AFTER `ctx_scheme_value_id`;
UPDATE `ctx_scheme_value`, `biz_ctx_value` SET `biz_ctx_value`.`ctx_scheme_value_uuid` = `ctx_scheme_value`.`ctx_scheme_value_uuid`
WHERE `ctx_scheme_value`.`ctx_scheme_value_id` = `biz_ctx_value`.`ctx_scheme_value_id`;

ALTER TABLE `biz_ctx_value` ADD COLUMN `biz_ctx_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the biz_ctx table.' AFTER `biz_ctx_id`;
UPDATE `biz_ctx`, `biz_ctx_value` SET `biz_ctx_value`.`biz_ctx_uuid` = `biz_ctx`.`biz_ctx_uuid`
WHERE `biz_ctx`.`biz_ctx_id` = `biz_ctx_value`.`biz_ctx_id`;

ALTER TABLE `biz_ctx_assignment` ADD COLUMN `biz_ctx_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the biz_ctx table.' AFTER `biz_ctx_id`;
UPDATE `biz_ctx`, `biz_ctx_assignment` SET `biz_ctx_assignment`.`biz_ctx_uuid` = `biz_ctx`.`biz_ctx_uuid`
WHERE `biz_ctx`.`biz_ctx_id` = `biz_ctx_assignment`.`biz_ctx_id`;

ALTER TABLE `abie` ADD COLUMN `biz_ctx_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT '(Deprecated) A foreign key to the BIZ_CTX table. This column stores the business context assigned to the ABIE.' AFTER `biz_ctx_id`;
UPDATE `biz_ctx`, `abie` SET `abie`.`biz_ctx_uuid` = `biz_ctx`.`biz_ctx_uuid`
WHERE `biz_ctx`.`biz_ctx_id` = `abie`.`biz_ctx_id`;

-- Drop old `ctx_category_id`, `ctx_scheme_id`, `ctx_scheme_value_id`,
-- `biz_ctx_id`, `biz_ctx_value_id`, and `biz_ctx_assignment_id`  columns
ALTER TABLE `biz_ctx_assignment` DROP FOREIGN KEY `biz_ctx_assignment_biz_ctx_id_fk`;
ALTER TABLE `biz_ctx_assignment` DROP KEY `biz_ctx_assignment_uk`;
ALTER TABLE `biz_ctx_assignment` DROP COLUMN `biz_ctx_id`;

ALTER TABLE `biz_ctx_value` DROP FOREIGN KEY `biz_ctx_value_biz_ctx_id_fk`;
ALTER TABLE `biz_ctx_value` DROP COLUMN `biz_ctx_id`;

ALTER TABLE `abie` DROP FOREIGN KEY `abie_biz_ctx_id_fk`;
ALTER TABLE `abie` DROP COLUMN `biz_ctx_id`;

ALTER TABLE `biz_ctx_value` DROP FOREIGN KEY `biz_ctx_value_ctx_scheme_value_id_fk`;
ALTER TABLE `biz_ctx_value` DROP COLUMN `ctx_scheme_value_id`;

ALTER TABLE `ctx_scheme_value` DROP FOREIGN KEY `ctx_scheme_value_owner_ctx_scheme_id_fk`;
ALTER TABLE `ctx_scheme_value` DROP COLUMN `owner_ctx_scheme_id`;

ALTER TABLE `ctx_scheme` DROP FOREIGN KEY `ctx_scheme_ctx_category_id_fk`;
ALTER TABLE `ctx_scheme` DROP COLUMN `ctx_category_id`;

ALTER TABLE `ctx_category` DROP COLUMN `ctx_category_id`;
ALTER TABLE `ctx_scheme` DROP COLUMN `ctx_scheme_id`;
ALTER TABLE `ctx_scheme_value` DROP COLUMN `ctx_scheme_value_id`;
ALTER TABLE `biz_ctx` DROP COLUMN `biz_ctx_id`;
ALTER TABLE `biz_ctx_value` DROP COLUMN `biz_ctx_value_id`;
ALTER TABLE `biz_ctx_assignment` DROP COLUMN `biz_ctx_assignment_id`;

-- Rename `*_uuid` TO `*_id`
ALTER TABLE `ctx_category` CHANGE `ctx_category_uuid` `ctx_category_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `ctx_scheme` CHANGE `ctx_scheme_uuid` `ctx_scheme_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `ctx_scheme_value` CHANGE `ctx_scheme_value_uuid` `ctx_scheme_value_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `biz_ctx` CHANGE `biz_ctx_uuid` `biz_ctx_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `biz_ctx_value` CHANGE `biz_ctx_value_uuid` `biz_ctx_value_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `biz_ctx_assignment` CHANGE `biz_ctx_assignment_uuid` `biz_ctx_assignment_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';

ALTER TABLE `ctx_scheme` CHANGE `ctx_category_uuid` `ctx_category_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This the foreign key to the CTX_CATEGORY table. It identifies the context category associated with this context scheme.';
ALTER TABLE `ctx_scheme_value` CHANGE `owner_ctx_scheme_uuid` `owner_ctx_scheme_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the CTX_SCHEME table. It identifies the context scheme, to which this scheme value belongs.';
ALTER TABLE `biz_ctx_value` CHANGE `ctx_scheme_value_uuid` `ctx_scheme_value_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the CTX_SCHEME_VALUE table.';
ALTER TABLE `biz_ctx_value` CHANGE `biz_ctx_uuid` `biz_ctx_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the biz_ctx table.';
ALTER TABLE `biz_ctx_assignment` CHANGE `biz_ctx_uuid` `biz_ctx_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the biz_ctx table.';
ALTER TABLE `abie` CHANGE `biz_ctx_uuid` `biz_ctx_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT '(Deprecated) A foreign key to the BIZ_CTX table. This column stores the business context assigned to the ABIE.';

-- Add foreign key constraints
ALTER TABLE `ctx_category` ADD PRIMARY KEY (`ctx_category_id`);
ALTER TABLE `ctx_scheme` ADD PRIMARY KEY (`ctx_scheme_id`);
ALTER TABLE `ctx_scheme_value` ADD PRIMARY KEY (`ctx_scheme_value_id`);
ALTER TABLE `biz_ctx` ADD PRIMARY KEY (`biz_ctx_id`);
ALTER TABLE `biz_ctx_value` ADD PRIMARY KEY (`biz_ctx_value_id`);
ALTER TABLE `biz_ctx_assignment` ADD PRIMARY KEY (`biz_ctx_assignment_id`);

ALTER TABLE `ctx_scheme` ADD CONSTRAINT `ctx_scheme_ctx_category_id_fk` FOREIGN KEY (`ctx_category_id`) REFERENCES `ctx_category` (`ctx_category_id`);
ALTER TABLE `ctx_scheme_value` ADD CONSTRAINT `ctx_scheme_value_owner_ctx_scheme_id_fk` FOREIGN KEY (`owner_ctx_scheme_id`) REFERENCES `ctx_scheme` (`ctx_scheme_id`);
ALTER TABLE `biz_ctx_value` ADD CONSTRAINT `biz_ctx_value_ctx_scheme_value_id_fk` FOREIGN KEY (`ctx_scheme_value_id`) REFERENCES `ctx_scheme_value` (`ctx_scheme_value_id`);
ALTER TABLE `biz_ctx_value` ADD CONSTRAINT `biz_ctx_value_biz_ctx_id_fk` FOREIGN KEY (`biz_ctx_id`) REFERENCES `biz_ctx` (`biz_ctx_id`);
ALTER TABLE `biz_ctx_assignment` ADD CONSTRAINT `biz_ctx_assignment_biz_ctx_id_fk` FOREIGN KEY (`biz_ctx_id`) REFERENCES `biz_ctx` (`biz_ctx_id`);
ALTER TABLE `biz_ctx_assignment` ADD UNIQUE KEY `biz_ctx_assignment_uk` (`biz_ctx_id`, `top_level_asbiep_id`);
ALTER TABLE `abie` ADD CONSTRAINT `abie_biz_ctx_id_fk` FOREIGN KEY (`biz_ctx_id`) REFERENCES `biz_ctx` (`biz_ctx_id`);

-- --------------------------------------------------------------------------------------------------
-- Change `module_id`, `module_acc_manifest_id`, `module_agency_id_list_manifest_id`,              --
--        `module_asccp_manifest_id`, `module_bccp_manifest_id`, `module_blob_content_manifest_id` --
--        `module_code_list_manifest_id`, `module_dt_manifest_id`, `module_set_id`                 --
--        `module_set_release_id`, `module_xbt_manifest_id` TO UUID                                --
-- --------------------------------------------------------------------------------------------------
ALTER TABLE `module` ADD COLUMN `module_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `module_id`;
CALL update_uuid('module');

ALTER TABLE `module_acc_manifest` ADD COLUMN `module_acc_manifest_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `module_acc_manifest_id`;
CALL update_uuid('module_acc_manifest');

ALTER TABLE `module_agency_id_list_manifest` ADD COLUMN `module_agency_id_list_manifest_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `module_agency_id_list_manifest_id`;
CALL update_uuid('module_agency_id_list_manifest');

ALTER TABLE `module_asccp_manifest` ADD COLUMN `module_asccp_manifest_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `module_asccp_manifest_id`;
CALL update_uuid('module_asccp_manifest');

ALTER TABLE `module_bccp_manifest` ADD COLUMN `module_bccp_manifest_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `module_bccp_manifest_id`;
CALL update_uuid('module_bccp_manifest');

ALTER TABLE `module_blob_content_manifest` ADD COLUMN `module_blob_content_manifest_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `module_blob_content_manifest_id`;
CALL update_uuid('module_blob_content_manifest');

ALTER TABLE `module_code_list_manifest` ADD COLUMN `module_code_list_manifest_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `module_code_list_manifest_id`;
CALL update_uuid('module_code_list_manifest');

ALTER TABLE `module_dt_manifest` ADD COLUMN `module_dt_manifest_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `module_dt_manifest_id`;
CALL update_uuid('module_dt_manifest');

ALTER TABLE `module_set` ADD COLUMN `module_set_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `module_set_id`;
CALL update_uuid('module_set');

ALTER TABLE `module_set_release` ADD COLUMN `module_set_release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `module_set_release_id`;
CALL update_uuid('module_set_release');

ALTER TABLE `module_xbt_manifest` ADD COLUMN `module_xbt_manifest_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `module_xbt_manifest_id`;
CALL update_uuid('module_xbt_manifest');

ALTER TABLE `module` ADD COLUMN `module_set_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This indicates a module set.' AFTER `module_set_id`;
UPDATE `module`, `module_set` SET `module`.`module_set_uuid` = `module_set`.`module_set_uuid`
WHERE `module`.`module_set_id` = `module_set`.`module_set_id`;

ALTER TABLE `module` ADD COLUMN `parent_module_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This indicates a parent module id. root module will be NULL.' AFTER `parent_module_id`;
UPDATE `module`, `module` AS parent_module SET parent_module.`parent_module_uuid` = `module`.`module_uuid`
WHERE `module`.`module_id` = parent_module.`parent_module_id`;

ALTER TABLE `module_acc_manifest` ADD COLUMN `module_set_release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key of the module set release record.' AFTER `module_set_release_id`;
UPDATE `module_acc_manifest`, `module_set_release` SET `module_acc_manifest`.`module_set_release_uuid` = `module_set_release`.`module_set_release_uuid`
WHERE `module_acc_manifest`.`module_set_release_id` = `module_set_release`.`module_set_release_id`;
ALTER TABLE `module_acc_manifest` ADD COLUMN `module_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_id`;
UPDATE `module_acc_manifest`, `module` SET `module_acc_manifest`.`module_uuid` = `module`.`module_uuid`
WHERE `module_acc_manifest`.`module_id` = `module`.`module_id`;

ALTER TABLE `module_agency_id_list_manifest` ADD COLUMN `module_set_release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key of the module set release record.' AFTER `module_set_release_id`;
UPDATE `module_agency_id_list_manifest`, `module_set_release` SET `module_agency_id_list_manifest`.`module_set_release_uuid` = `module_set_release`.`module_set_release_uuid`
WHERE `module_agency_id_list_manifest`.`module_set_release_id` = `module_set_release`.`module_set_release_id`;
ALTER TABLE `module_agency_id_list_manifest` ADD COLUMN `module_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_id`;
UPDATE `module_agency_id_list_manifest`, `module` SET `module_agency_id_list_manifest`.`module_uuid` = `module`.`module_uuid`
WHERE `module_agency_id_list_manifest`.`module_id` = `module`.`module_id`;

ALTER TABLE `module_asccp_manifest` ADD COLUMN `module_set_release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key of the module set release record.' AFTER `module_set_release_id`;
UPDATE `module_asccp_manifest`, `module_set_release` SET `module_asccp_manifest`.`module_set_release_uuid` = `module_set_release`.`module_set_release_uuid`
WHERE `module_asccp_manifest`.`module_set_release_id` = `module_set_release`.`module_set_release_id`;
ALTER TABLE `module_asccp_manifest` ADD COLUMN `module_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_id`;
UPDATE `module_asccp_manifest`, `module` SET `module_asccp_manifest`.`module_uuid` = `module`.`module_uuid`
WHERE `module_asccp_manifest`.`module_id` = `module`.`module_id`;

ALTER TABLE `module_bccp_manifest` ADD COLUMN `module_set_release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key of the module set release record.' AFTER `module_set_release_id`;
UPDATE `module_bccp_manifest`, `module_set_release` SET `module_bccp_manifest`.`module_set_release_uuid` = `module_set_release`.`module_set_release_uuid`
WHERE `module_bccp_manifest`.`module_set_release_id` = `module_set_release`.`module_set_release_id`;
ALTER TABLE `module_bccp_manifest` ADD COLUMN `module_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_id`;
UPDATE `module_bccp_manifest`, `module` SET `module_bccp_manifest`.`module_uuid` = `module`.`module_uuid`
WHERE `module_bccp_manifest`.`module_id` = `module`.`module_id`;

ALTER TABLE `module_blob_content_manifest` ADD COLUMN `module_set_release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key of the module set release record.' AFTER `module_set_release_id`;
UPDATE `module_blob_content_manifest`, `module_set_release` SET `module_blob_content_manifest`.`module_set_release_uuid` = `module_set_release`.`module_set_release_uuid`
WHERE `module_blob_content_manifest`.`module_set_release_id` = `module_set_release`.`module_set_release_id`;
ALTER TABLE `module_blob_content_manifest` ADD COLUMN `module_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_id`;
UPDATE `module_blob_content_manifest`, `module` SET `module_blob_content_manifest`.`module_uuid` = `module`.`module_uuid`
WHERE `module_blob_content_manifest`.`module_id` = `module`.`module_id`;

ALTER TABLE `module_code_list_manifest` ADD COLUMN `module_set_release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key of the module set release record.' AFTER `module_set_release_id`;
UPDATE `module_code_list_manifest`, `module_set_release` SET `module_code_list_manifest`.`module_set_release_uuid` = `module_set_release`.`module_set_release_uuid`
WHERE `module_code_list_manifest`.`module_set_release_id` = `module_set_release`.`module_set_release_id`;
ALTER TABLE `module_code_list_manifest` ADD COLUMN `module_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_id`;
UPDATE `module_code_list_manifest`, `module` SET `module_code_list_manifest`.`module_uuid` = `module`.`module_uuid`
WHERE `module_code_list_manifest`.`module_id` = `module`.`module_id`;

ALTER TABLE `module_dt_manifest` ADD COLUMN `module_set_release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key of the module set release record.' AFTER `module_set_release_id`;
UPDATE `module_dt_manifest`, `module_set_release` SET `module_dt_manifest`.`module_set_release_uuid` = `module_set_release`.`module_set_release_uuid`
WHERE `module_dt_manifest`.`module_set_release_id` = `module_set_release`.`module_set_release_id`;
ALTER TABLE `module_dt_manifest` ADD COLUMN `module_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_id`;
UPDATE `module_dt_manifest`, `module` SET `module_dt_manifest`.`module_uuid` = `module`.`module_uuid`
WHERE `module_dt_manifest`.`module_id` = `module`.`module_id`;

ALTER TABLE `module_set_release` ADD COLUMN `module_set_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key of the module set.' AFTER `module_set_id`;
UPDATE `module_set_release`, `module_set` SET `module_set_release`.`module_set_uuid` = `module_set`.`module_set_uuid`
WHERE `module_set_release`.`module_set_id` = `module_set`.`module_set_id`;

ALTER TABLE `module_xbt_manifest` ADD COLUMN `module_set_release_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key of the module set release record.' AFTER `module_set_release_id`;
UPDATE `module_xbt_manifest`, `module_set_release` SET `module_xbt_manifest`.`module_set_release_uuid` = `module_set_release`.`module_set_release_uuid`
WHERE `module_xbt_manifest`.`module_set_release_id` = `module_set_release`.`module_set_release_id`;
ALTER TABLE `module_xbt_manifest` ADD COLUMN `module_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_id`;
UPDATE `module_xbt_manifest`, `module` SET `module_xbt_manifest`.`module_uuid` = `module`.`module_uuid`
WHERE `module_xbt_manifest`.`module_id` = `module`.`module_id`;

-- Drop old `module_id`, `module_acc_manifest_id`, `module_agency_id_list_manifest_id`,
-- `module_asccp_manifest_id`, `module_bccp_manifest_id`, `module_blob_content_manifest_id`
-- `module_code_list_manifest_id`, `module_dt_manifest_id`, `module_set_id`
-- `module_set_release_id`, `module_xbt_manifest_id` columns
ALTER TABLE `module` DROP FOREIGN KEY `module_module_set_id_fk`;
ALTER TABLE `module` DROP COLUMN `module_set_id`;
ALTER TABLE `module_set_release` DROP FOREIGN KEY `module_set_release_module_set_id_fk`;
ALTER TABLE `module_set_release` DROP COLUMN `module_set_id`;
ALTER TABLE `module` DROP FOREIGN KEY `module_parent_module_id_fk`;
ALTER TABLE `module` DROP COLUMN `parent_module_id`;

ALTER TABLE `module_acc_manifest` DROP FOREIGN KEY `module_acc_manifest_module_set_release_id_fk`;
ALTER TABLE `module_acc_manifest` DROP COLUMN `module_set_release_id`;
ALTER TABLE `module_agency_id_list_manifest` DROP FOREIGN KEY `module_agency_id_list_manifest_module_set_release_id_fk`;
ALTER TABLE `module_agency_id_list_manifest` DROP COLUMN `module_set_release_id`;
ALTER TABLE `module_asccp_manifest` DROP FOREIGN KEY `module_asccp_manifest_module_set_release_id_fk`;
ALTER TABLE `module_asccp_manifest` DROP COLUMN `module_set_release_id`;
ALTER TABLE `module_bccp_manifest` DROP FOREIGN KEY `module_bccp_manifest_module_set_release_id_fk`;
ALTER TABLE `module_bccp_manifest` DROP COLUMN `module_set_release_id`;
ALTER TABLE `module_blob_content_manifest` DROP FOREIGN KEY `module_blob_content_manifest_module_set_release_id_fk`;
ALTER TABLE `module_blob_content_manifest` DROP COLUMN `module_set_release_id`;
ALTER TABLE `module_code_list_manifest` DROP FOREIGN KEY `module_code_list_manifest_module_set_release_id_fk`;
ALTER TABLE `module_code_list_manifest` DROP COLUMN `module_set_release_id`;
ALTER TABLE `module_dt_manifest` DROP FOREIGN KEY `module_dt_manifest_module_set_release_id_fk`;
ALTER TABLE `module_dt_manifest` DROP COLUMN `module_set_release_id`;
ALTER TABLE `module_xbt_manifest` DROP FOREIGN KEY `module_xbt_manifest_module_set_release_id_fk`;
ALTER TABLE `module_xbt_manifest` DROP COLUMN `module_set_release_id`;
ALTER TABLE `module_acc_manifest` DROP FOREIGN KEY `module_acc_manifest_module_id_fk`;
ALTER TABLE `module_acc_manifest` DROP COLUMN `module_id`;
ALTER TABLE `module_agency_id_list_manifest` DROP FOREIGN KEY `module_agency_id_list_manifest_module_id_fk`;
ALTER TABLE `module_agency_id_list_manifest` DROP COLUMN `module_id`;
ALTER TABLE `module_asccp_manifest` DROP FOREIGN KEY `module_asccp_manifest_module_id_fk`;
ALTER TABLE `module_asccp_manifest` DROP COLUMN `module_id`;
ALTER TABLE `module_bccp_manifest` DROP FOREIGN KEY `module_bccp_manifest_module_id_fk`;
ALTER TABLE `module_bccp_manifest` DROP COLUMN `module_id`;
ALTER TABLE `module_blob_content_manifest` DROP FOREIGN KEY `module_blob_content_manifest_module_id_fk`;
ALTER TABLE `module_blob_content_manifest` DROP COLUMN `module_id`;
ALTER TABLE `module_code_list_manifest` DROP FOREIGN KEY `module_code_list_manifest_module_id_fk`;
ALTER TABLE `module_code_list_manifest` DROP COLUMN `module_id`;
ALTER TABLE `module_dt_manifest` DROP FOREIGN KEY `module_dt_manifest_module_id_fk`;
ALTER TABLE `module_dt_manifest` DROP COLUMN `module_id`;
ALTER TABLE `module_xbt_manifest` DROP FOREIGN KEY `module_xbt_manifest_module_id_fk`;
ALTER TABLE `module_xbt_manifest` DROP COLUMN `module_id`;

ALTER TABLE `module` DROP COLUMN `module_id`;
ALTER TABLE `module_acc_manifest` DROP COLUMN `module_acc_manifest_id`;
ALTER TABLE `module_agency_id_list_manifest` DROP COLUMN `module_agency_id_list_manifest_id`;
ALTER TABLE `module_asccp_manifest` DROP COLUMN `module_asccp_manifest_id`;
ALTER TABLE `module_bccp_manifest` DROP COLUMN `module_bccp_manifest_id`;
ALTER TABLE `module_blob_content_manifest` DROP COLUMN `module_blob_content_manifest_id`;
ALTER TABLE `module_code_list_manifest` DROP COLUMN `module_code_list_manifest_id`;
ALTER TABLE `module_dt_manifest` DROP COLUMN `module_dt_manifest_id`;
ALTER TABLE `module_set` DROP COLUMN `module_set_id`;
ALTER TABLE `module_set_release` DROP COLUMN `module_set_release_id`;
ALTER TABLE `module_xbt_manifest` DROP COLUMN `module_xbt_manifest_id`;

-- Rename `*_uuid` TO `*_id`
ALTER TABLE `module` CHANGE `module_uuid` `module_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `module_acc_manifest` CHANGE `module_acc_manifest_uuid` `module_acc_manifest_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `module_agency_id_list_manifest` CHANGE `module_agency_id_list_manifest_uuid` `module_agency_id_list_manifest_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `module_asccp_manifest` CHANGE `module_asccp_manifest_uuid` `module_asccp_manifest_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `module_bccp_manifest` CHANGE `module_bccp_manifest_uuid` `module_bccp_manifest_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `module_blob_content_manifest` CHANGE `module_blob_content_manifest_uuid` `module_blob_content_manifest_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `module_code_list_manifest` CHANGE `module_code_list_manifest_uuid` `module_code_list_manifest_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `module_dt_manifest` CHANGE `module_dt_manifest_uuid` `module_dt_manifest_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `module_set` CHANGE `module_set_uuid` `module_set_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `module_set_release` CHANGE `module_set_release_uuid` `module_set_release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `module_xbt_manifest` CHANGE `module_xbt_manifest_uuid` `module_xbt_manifest_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';

ALTER TABLE `module` CHANGE `module_set_uuid` `module_set_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This indicates a module set.';
ALTER TABLE `module` CHANGE `parent_module_uuid` `parent_module_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This indicates a parent module id. root module will be NULL.';
ALTER TABLE `module_acc_manifest` CHANGE `module_set_release_uuid` `module_set_release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key of the module set release record.';
ALTER TABLE `module_acc_manifest` CHANGE `module_uuid` `module_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This indicates a module.';
ALTER TABLE `module_agency_id_list_manifest` CHANGE `module_set_release_uuid` `module_set_release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key of the module set release record.';
ALTER TABLE `module_agency_id_list_manifest` CHANGE `module_uuid` `module_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This indicates a module.';
ALTER TABLE `module_asccp_manifest` CHANGE `module_set_release_uuid` `module_set_release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key of the module set release record.';
ALTER TABLE `module_asccp_manifest` CHANGE `module_uuid` `module_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This indicates a module.';
ALTER TABLE `module_bccp_manifest` CHANGE `module_set_release_uuid` `module_set_release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key of the module set release record.';
ALTER TABLE `module_bccp_manifest` CHANGE `module_uuid` `module_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This indicates a module.';
ALTER TABLE `module_blob_content_manifest` CHANGE `module_set_release_uuid` `module_set_release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key of the module set release record.';
ALTER TABLE `module_blob_content_manifest` CHANGE `module_uuid` `module_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This indicates a module.';
ALTER TABLE `module_code_list_manifest` CHANGE `module_set_release_uuid` `module_set_release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key of the module set release record.';
ALTER TABLE `module_code_list_manifest` CHANGE `module_uuid` `module_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This indicates a module.';
ALTER TABLE `module_dt_manifest` CHANGE `module_set_release_uuid` `module_set_release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key of the module set release record.';
ALTER TABLE `module_dt_manifest` CHANGE `module_uuid` `module_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This indicates a module.';
ALTER TABLE `module_set_release` CHANGE `module_set_uuid` `module_set_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key of the module set.';
ALTER TABLE `module_xbt_manifest` CHANGE `module_set_release_uuid` `module_set_release_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key of the module set release record.';
ALTER TABLE `module_xbt_manifest` CHANGE `module_uuid` `module_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This indicates a module.';

-- Add foreign key constraints
ALTER TABLE `module` ADD PRIMARY KEY (`module_id`);
ALTER TABLE `module_acc_manifest` ADD PRIMARY KEY (`module_acc_manifest_id`);
ALTER TABLE `module_agency_id_list_manifest` ADD PRIMARY KEY (`module_agency_id_list_manifest_id`);
ALTER TABLE `module_asccp_manifest` ADD PRIMARY KEY (`module_asccp_manifest_id`);
ALTER TABLE `module_bccp_manifest` ADD PRIMARY KEY (`module_bccp_manifest_id`);
ALTER TABLE `module_blob_content_manifest` ADD PRIMARY KEY (`module_blob_content_manifest_id`);
ALTER TABLE `module_code_list_manifest` ADD PRIMARY KEY (`module_code_list_manifest_id`);
ALTER TABLE `module_dt_manifest` ADD PRIMARY KEY (`module_dt_manifest_id`);
ALTER TABLE `module_set` ADD PRIMARY KEY (`module_set_id`);
ALTER TABLE `module_set_release` ADD PRIMARY KEY (`module_set_release_id`);
ALTER TABLE `module_xbt_manifest` ADD PRIMARY KEY (`module_xbt_manifest_id`);

ALTER TABLE `module` ADD CONSTRAINT `module_module_set_id_fk` FOREIGN KEY (`module_set_id`) REFERENCES `module_set` (`module_set_id`);
ALTER TABLE `module` ADD CONSTRAINT `module_parent_module_id_fk` FOREIGN KEY (`parent_module_id`) REFERENCES `module` (`module_id`);
ALTER TABLE `module_set_release` ADD CONSTRAINT `module_set_release_module_set_id_fk` FOREIGN KEY (`module_set_id`) REFERENCES `module_set` (`module_set_id`);
ALTER TABLE `module_acc_manifest`
    ADD CONSTRAINT `module_acc_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`),
    ADD CONSTRAINT `module_acc_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
ALTER TABLE `module_agency_id_list_manifest`
    ADD CONSTRAINT `module_agency_id_list_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`),
    ADD CONSTRAINT `module_agency_id_list_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
ALTER TABLE `module_asccp_manifest`
    ADD CONSTRAINT `module_asccp_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`),
    ADD CONSTRAINT `module_asccp_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
ALTER TABLE `module_bccp_manifest`
    ADD CONSTRAINT `module_bccp_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`),
    ADD CONSTRAINT `module_bccp_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
ALTER TABLE `module_blob_content_manifest`
    ADD CONSTRAINT `module_blob_content_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`),
    ADD CONSTRAINT `module_blob_content_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
ALTER TABLE `module_code_list_manifest`
    ADD CONSTRAINT `module_code_list_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`),
    ADD CONSTRAINT `module_code_list_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
ALTER TABLE `module_dt_manifest`
    ADD CONSTRAINT `module_dt_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`),
    ADD CONSTRAINT `module_dt_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
ALTER TABLE `module_xbt_manifest`
    ADD CONSTRAINT `module_xbt_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`),
    ADD CONSTRAINT `module_xbt_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);

-- ----------------------------------------------------------
-- Change `acc_id`, `ascc_id`, `bcc_id`, `blob_content_id` --
--        `asccp_id`, `bccp_id`, `dt_id`, `dt_sc_id`,      --
--        `code_list_id`, `agency_id_list_id` TO UUID      --
-- ----------------------------------------------------------
ALTER TABLE `acc` ADD COLUMN `acc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `acc_id`;
CALL update_uuid('acc');

ALTER TABLE `ascc` ADD COLUMN `ascc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `ascc_id`;
CALL update_uuid('ascc');

ALTER TABLE `bcc` ADD COLUMN `bcc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `bcc_id`;
CALL update_uuid('bcc');

ALTER TABLE `asccp` ADD COLUMN `asccp_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `asccp_id`;
CALL update_uuid('asccp');

ALTER TABLE `bccp` ADD COLUMN `bccp_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `bccp_id`;
CALL update_uuid('bccp');

ALTER TABLE `blob_content` ADD COLUMN `blob_content_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `blob_content_id`;
CALL update_uuid('blob_content');

ALTER TABLE `dt` ADD COLUMN `dt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `dt_id`;
CALL update_uuid('dt');

ALTER TABLE `dt_sc` ADD COLUMN `dt_sc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `dt_sc_id`;
CALL update_uuid('dt_sc');

ALTER TABLE `code_list` ADD COLUMN `code_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `code_list_id`;
CALL update_uuid('code_list');

ALTER TABLE `code_list_value` ADD COLUMN `code_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `code_list_value_id`;
CALL update_uuid('code_list_value');

ALTER TABLE `agency_id_list` ADD COLUMN `agency_id_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `agency_id_list_id`;
CALL update_uuid('agency_id_list');

ALTER TABLE `agency_id_list_value` ADD COLUMN `agency_id_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `agency_id_list_value_id`;
CALL update_uuid('agency_id_list_value');

ALTER TABLE `acc`
    ADD COLUMN `based_acc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'BASED_ACC_ID is a foreign key to the ACC table itself. It represents the ACC that is qualified by this ACC. In general CCS sense, a qualification can be a content extension or restriction, but the current scope supports only extension.' AFTER `based_acc_id`,
    ADD COLUMN `replacement_acc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.' AFTER `replacement_acc_id`,
    ADD COLUMN `prev_acc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.' AFTER `prev_acc_id`,
    ADD COLUMN `next_acc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.' AFTER `next_acc_id`;
UPDATE `acc` AS tmp, `acc` SET tmp.`based_acc_uuid` = `acc`.`acc_uuid`
WHERE tmp.`based_acc_id` = `acc`.`acc_id`;
UPDATE `acc` AS tmp, `acc` SET tmp.`replacement_acc_uuid` = `acc`.`acc_uuid`
WHERE tmp.`replacement_acc_id` = `acc`.`acc_id`;
UPDATE `acc` AS tmp, `acc` SET tmp.`prev_acc_uuid` = `acc`.`acc_uuid`
WHERE tmp.`prev_acc_id` = `acc`.`acc_id`;
UPDATE `acc` AS tmp, `acc` SET tmp.`next_acc_uuid` = `acc`.`acc_uuid`
WHERE tmp.`next_acc_id` = `acc`.`acc_id`;

ALTER TABLE `acc_manifest` ADD COLUMN `acc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `acc_id`;
UPDATE `acc_manifest`, `acc` SET `acc_manifest`.`acc_uuid` = `acc`.`acc_uuid`
WHERE `acc_manifest`.`acc_id` = `acc`.`acc_id`;

ALTER TABLE `agency_id_list`
    ADD COLUMN `agency_id_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is the identification of the agency or organization which developed and/or maintains the list. Theoretically, this can be modeled as a self-reference foreign key, but it is not implemented at this point.' AFTER `agency_id_list_value_id`,
    ADD COLUMN `based_agency_id_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the AGENCY_ID_LIST table itself. This identifies the agency id list on which this agency id list is based, if any. The derivation may be restriction and/or extension.' AFTER `based_agency_id_list_id`,
    ADD COLUMN `replacement_agency_id_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.' AFTER `replacement_agency_id_list_id`,
    ADD COLUMN `prev_agency_id_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.' AFTER `prev_agency_id_list_id`,
    ADD COLUMN `next_agency_id_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.' AFTER `next_agency_id_list_id`;
UPDATE `agency_id_list`, `agency_id_list_value` SET `agency_id_list`.`agency_id_list_value_uuid` = `agency_id_list_value`.`agency_id_list_value_uuid`
WHERE `agency_id_list`.`agency_id_list_value_id` = `agency_id_list_value`.`agency_id_list_value_id`;
UPDATE `agency_id_list` AS tmp, `agency_id_list` SET tmp.`based_agency_id_list_uuid` = `agency_id_list`.`agency_id_list_uuid`
WHERE tmp.`based_agency_id_list_id` = `agency_id_list`.`agency_id_list_id`;
UPDATE `agency_id_list` AS tmp, `agency_id_list` SET tmp.`replacement_agency_id_list_uuid` = `agency_id_list`.`agency_id_list_uuid`
WHERE tmp.`replacement_agency_id_list_id` = `agency_id_list`.`agency_id_list_id`;
UPDATE `agency_id_list` AS tmp, `agency_id_list` SET tmp.`prev_agency_id_list_uuid` = `agency_id_list`.`agency_id_list_uuid`
WHERE tmp.`prev_agency_id_list_id` = `agency_id_list`.`agency_id_list_id`;
UPDATE `agency_id_list` AS tmp, `agency_id_list` SET tmp.`next_agency_id_list_uuid` = `agency_id_list`.`agency_id_list_uuid`
WHERE tmp.`next_agency_id_list_id` = `agency_id_list`.`agency_id_list_id`;

ALTER TABLE `agency_id_list_manifest` ADD COLUMN `agency_id_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `agency_id_list_id`;
UPDATE `agency_id_list_manifest`, `agency_id_list` SET `agency_id_list_manifest`.`agency_id_list_uuid` = `agency_id_list`.`agency_id_list_uuid`
WHERE `agency_id_list_manifest`.`agency_id_list_id` = `agency_id_list`.`agency_id_list_id`;

ALTER TABLE `agency_id_list_value`
    ADD COLUMN `owner_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the agency identification list in the AGENCY_ID_LIST table this value belongs to.' AFTER `owner_list_id`,
    ADD COLUMN `based_agency_id_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the AGENCY_ID_LIST_VALUE table itself. This column is used when the AGENCY_ID_LIST_VALUE is derived from the based AGENCY_ID_LIST_VALUE.' AFTER `based_agency_id_list_value_id`,
    ADD COLUMN `replacement_agency_id_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.' AFTER `replacement_agency_id_list_value_id`,
    ADD COLUMN `prev_agency_id_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.' AFTER `prev_agency_id_list_value_id`,
    ADD COLUMN `next_agency_id_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.' AFTER `next_agency_id_list_value_id`;
UPDATE `agency_id_list_value`, `agency_id_list` SET `agency_id_list_value`.`owner_list_uuid` = `agency_id_list`.`agency_id_list_uuid`
WHERE `agency_id_list_value`.`owner_list_id` = `agency_id_list`.`agency_id_list_id`;
UPDATE `agency_id_list_value` AS tmp, `agency_id_list_value` SET tmp.`based_agency_id_list_value_uuid` = `agency_id_list_value`.`agency_id_list_value_uuid`
WHERE tmp.`based_agency_id_list_value_id` = `agency_id_list_value`.`agency_id_list_value_id`;
UPDATE `agency_id_list_value` AS tmp, `agency_id_list_value` SET tmp.`replacement_agency_id_list_value_uuid` = `agency_id_list_value`.`agency_id_list_value_uuid`
WHERE tmp.`replacement_agency_id_list_value_id` = `agency_id_list_value`.`agency_id_list_value_id`;
UPDATE `agency_id_list_value` AS tmp, `agency_id_list_value` SET tmp.`prev_agency_id_list_value_uuid` = `agency_id_list_value`.`agency_id_list_value_uuid`
WHERE tmp.`prev_agency_id_list_value_id` = `agency_id_list_value`.`agency_id_list_value_id`;
UPDATE `agency_id_list_value` AS tmp, `agency_id_list_value` SET tmp.`next_agency_id_list_value_uuid` = `agency_id_list_value`.`agency_id_list_value_uuid`
WHERE tmp.`next_agency_id_list_value_id` = `agency_id_list_value`.`agency_id_list_value_id`;

ALTER TABLE `agency_id_list_value_manifest` ADD COLUMN `agency_id_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `agency_id_list_value_id`;
UPDATE `agency_id_list_value_manifest`, `agency_id_list_value` SET `agency_id_list_value_manifest`.`agency_id_list_value_uuid` = `agency_id_list_value`.`agency_id_list_value_uuid`
WHERE `agency_id_list_value_manifest`.`agency_id_list_value_id` = `agency_id_list_value`.`agency_id_list_value_id`;

ALTER TABLE `ascc`
    ADD COLUMN `from_acc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'FROM_ACC_ID is a foreign key pointing to an ACC record. It is basically pointing to a parent data element (type) of the TO_ASCCP_ID.' AFTER `from_acc_id`,
    ADD COLUMN `to_asccp_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'TO_ASCCP_ID is a foreign key to an ASCCP table record. It is basically pointing to a child data element of the FROM_ACC_ID.' AFTER `to_asccp_id`,
    ADD COLUMN `replacement_ascc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.' AFTER `replacement_ascc_id`,
    ADD COLUMN `prev_ascc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.' AFTER `prev_ascc_id`,
    ADD COLUMN `next_ascc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.' AFTER `next_ascc_id`;
UPDATE `acc`, `ascc` SET `ascc`.`from_acc_uuid` = `acc`.`acc_uuid`
WHERE `ascc`.`from_acc_id` = `acc`.`acc_id`;
UPDATE `asccp`, `ascc` SET `ascc`.`to_asccp_uuid` = `asccp`.`asccp_uuid`
WHERE `ascc`.`to_asccp_id` = `asccp`.`asccp_id`;
UPDATE `ascc` AS tmp, `ascc` SET tmp.`replacement_ascc_uuid` = `ascc`.`ascc_uuid`
WHERE tmp.`replacement_ascc_id` = `ascc`.`ascc_id`;
UPDATE `ascc` AS tmp, `ascc` SET tmp.`prev_ascc_uuid` = `ascc`.`ascc_uuid`
WHERE tmp.`prev_ascc_id` = `ascc`.`ascc_id`;
UPDATE `ascc` AS tmp, `ascc` SET tmp.`next_ascc_uuid` = `ascc`.`ascc_uuid`
WHERE tmp.`next_ascc_id` = `ascc`.`ascc_id`;

ALTER TABLE `ascc_bizterm` ADD COLUMN `ascc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'An internal ID of the associated ASCC' AFTER `ascc_id`;
UPDATE `ascc_bizterm`, `ascc` SET `ascc_bizterm`.`ascc_uuid` = `ascc`.`ascc_uuid`
WHERE `ascc_bizterm`.`ascc_id` = `ascc`.`ascc_id`;

ALTER TABLE `ascc_manifest` ADD COLUMN `ascc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `ascc_id`;
UPDATE `ascc_manifest`, `ascc` SET `ascc_manifest`.`ascc_uuid` = `ascc`.`ascc_uuid`
WHERE `ascc_manifest`.`ascc_id` = `ascc`.`ascc_id`;

ALTER TABLE `asccp`
    ADD COLUMN `role_of_acc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'The ACC from which this ASCCP is created (ASCCP applies role to the ACC).' AFTER `role_of_acc_id`,
    ADD COLUMN `replacement_asccp_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.' AFTER `replacement_asccp_id`,
    ADD COLUMN `prev_asccp_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.' AFTER `prev_asccp_id`,
    ADD COLUMN `next_asccp_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.' AFTER `next_asccp_id`;
UPDATE `asccp`, `acc` SET `asccp`.`role_of_acc_uuid` = `acc`.`acc_uuid`
WHERE `asccp`.`role_of_acc_id` = `acc`.`acc_id`;
UPDATE `asccp` AS tmp, `asccp` SET tmp.`replacement_asccp_uuid` = `asccp`.`asccp_uuid`
WHERE tmp.`replacement_asccp_id` = `asccp`.`asccp_id`;
UPDATE `asccp` AS tmp, `asccp` SET tmp.`prev_asccp_uuid` = `asccp`.`asccp_uuid`
WHERE tmp.`prev_asccp_id` = `asccp`.`asccp_id`;
UPDATE `asccp` AS tmp, `asccp` SET tmp.`next_asccp_uuid` = `asccp`.`asccp_uuid`
WHERE tmp.`next_asccp_id` = `asccp`.`asccp_id`;

ALTER TABLE `asccp_manifest` ADD COLUMN `asccp_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `asccp_id`;
UPDATE `asccp_manifest`, `asccp` SET `asccp_manifest`.`asccp_uuid` = `asccp`.`asccp_uuid`
WHERE `asccp_manifest`.`asccp_id` = `asccp`.`asccp_id`;

ALTER TABLE `bbie`
    ADD COLUMN `code_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the CODE_LIST table. If a code list is assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this association), then this column stores the assigned code list. It should be noted that one of the possible primitives assignable to the BDT_PRI_RESTRI_ID column may also be a code list. So this column is typically used when the user wants to assign another code list different from the one permissible by the CC model.' AFTER `code_list_id`,
    ADD COLUMN `agency_id_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the AGENCY_ID_LIST table. It is used in the case that the BDT content can be restricted to an agency identification.' AFTER `agency_id_list_id`;
UPDATE `bbie`, `code_list` SET `bbie`.`code_list_uuid` = `code_list`.`code_list_uuid`
WHERE `bbie`.`code_list_id` = `code_list`.`code_list_id`;
UPDATE `bbie`, `agency_id_list` SET `bbie`.`agency_id_list_uuid` = `agency_id_list`.`agency_id_list_uuid`
WHERE `bbie`.`agency_id_list_id` = `agency_id_list`.`agency_id_list_id`;

ALTER TABLE `bbie_sc`
    ADD COLUMN `code_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the CODE_LIST table. If a code list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned code list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be a code list. So this column is typically used when the user wants to assign another code list different from the one permissible by the CC model.\n\nThis column is, the DT_SC_PRI_RESTRI_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.' AFTER `code_list_id`,
    ADD COLUMN `agency_id_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the AGENCY_ID_LIST table. If a agency ID list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned Agency ID list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be an Agency ID list. So this column is typically used only when the user wants to assign another Agency ID list different from the one permissible by the CC model.\n\nThis column, the DT_SC_PRI_RESTRI_ID column, and CODE_LIST_ID column cannot have a value at the same time.' AFTER `agency_id_list_id`;
UPDATE `bbie_sc`, `code_list` SET `bbie_sc`.`code_list_uuid` = `code_list`.`code_list_uuid`
WHERE `bbie_sc`.`code_list_id` = `code_list`.`code_list_id`;
UPDATE `bbie_sc`, `agency_id_list` SET `bbie_sc`.`agency_id_list_uuid` = `agency_id_list`.`agency_id_list_uuid`
WHERE `bbie_sc`.`agency_id_list_id` = `agency_id_list`.`agency_id_list_id`;

ALTER TABLE `bcc`
    ADD COLUMN `from_acc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'FROM_ACC_ID is a foreign key pointing to an ACC record. It is basically pointing to a parent data element (type) of the TO_BCCP_ID. \n\nNote that for the BCC history records, this column always points to the ACC_ID of the current record of an ACC.' AFTER `from_acc_id`,
    ADD COLUMN `to_bccp_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'TO_BCCP_ID is a foreign key to an BCCP table record. It is basically pointing to a child data element of the FROM_ACC_ID. \n\nNote that for the BCC history records, this column always points to the BCCP_ID of the current record of a BCCP.' AFTER `to_bccp_id`,
    ADD COLUMN `replacement_bcc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.' AFTER `replacement_bcc_id`,
    ADD COLUMN `prev_bcc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.' AFTER `prev_bcc_id`,
    ADD COLUMN `next_bcc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.' AFTER `next_bcc_id`;
UPDATE `acc`, `bcc` SET `bcc`.`from_acc_uuid` = `acc`.`acc_uuid`
WHERE `bcc`.`from_acc_id` = `acc`.`acc_id`;
UPDATE `bccp`, `bcc` SET `bcc`.`to_bccp_uuid` = `bccp`.`bccp_uuid`
WHERE `bcc`.`to_bccp_id` = `bccp`.`bccp_id`;
UPDATE `bcc` AS tmp, `bcc` SET tmp.`replacement_bcc_uuid` = `bcc`.`bcc_uuid`
WHERE tmp.`replacement_bcc_id` = `bcc`.`bcc_id`;
UPDATE `bcc` AS tmp, `bcc` SET tmp.`prev_bcc_uuid` = `bcc`.`bcc_uuid`
WHERE tmp.`prev_bcc_id` = `bcc`.`bcc_id`;
UPDATE `bcc` AS tmp, `bcc` SET tmp.`next_bcc_uuid` = `bcc`.`bcc_uuid`
WHERE tmp.`next_bcc_id` = `bcc`.`bcc_id`;

ALTER TABLE `bcc_bizterm` ADD COLUMN `bcc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'An internal ID of the associated BCC' AFTER `bcc_id`;
UPDATE `bcc_bizterm`, `bcc` SET `bcc_bizterm`.`bcc_uuid` = `bcc`.`bcc_uuid`
WHERE `bcc_bizterm`.`bcc_id` = `bcc`.`bcc_id`;

ALTER TABLE `bcc_manifest` ADD COLUMN `bcc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `bcc_id`;
UPDATE `bcc_manifest`, `bcc` SET `bcc_manifest`.`bcc_uuid` = `bcc`.`bcc_uuid`
WHERE `bcc_manifest`.`bcc_id` = `bcc`.`bcc_id`;

ALTER TABLE `bccp`
    ADD COLUMN `bdt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key pointing to the DT table indicating the data typye or data format of the BCCP. Only DT_ID which DT_Type is BDT can be used.' AFTER `bdt_id`,
    ADD COLUMN `replacement_bccp_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.' AFTER `replacement_bccp_id`,
    ADD COLUMN `prev_bccp_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.' AFTER `prev_bccp_id`,
    ADD COLUMN `next_bccp_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.' AFTER `next_bccp_id`;
UPDATE `bccp`, `dt` SET `bccp`.`bdt_uuid` = `dt`.`dt_uuid`
WHERE `bccp`.`bdt_id` = `dt`.`dt_id`;
UPDATE `bccp` AS tmp, `bccp` SET tmp.`replacement_bccp_uuid` = `bccp`.`bccp_uuid`
WHERE tmp.`replacement_bccp_id` = `bccp`.`bccp_id`;
UPDATE `bccp` AS tmp, `bccp` SET tmp.`prev_bccp_uuid` = `bccp`.`bccp_uuid`
WHERE tmp.`prev_bccp_id` = `bccp`.`bccp_id`;
UPDATE `bccp` AS tmp, `bccp` SET tmp.`next_bccp_uuid` = `bccp`.`bccp_uuid`
WHERE tmp.`next_bccp_id` = `bccp`.`bccp_id`;

ALTER TABLE `bccp_manifest` ADD COLUMN `bccp_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `bccp_id`;
UPDATE `bccp_manifest`, `bccp` SET `bccp_manifest`.`bccp_uuid` = `bccp`.`bccp_uuid`
WHERE `bccp_manifest`.`bccp_id` = `bccp`.`bccp_id`;

ALTER TABLE `bdt_pri_restri`
    ADD COLUMN `bdt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the DT table. It shall point to only DT that is a BDT (not a CDT).' AFTER `bdt_id`,
    ADD COLUMN `code_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CODE_LIST table.' AFTER `code_list_id`,
    ADD COLUMN `agency_id_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the AGENCY_ID_LIST table. It is used in the case that the BDT content can be restricted to an agency identification.' AFTER `agency_id_list_id`;
UPDATE `bdt_pri_restri`, `dt` SET `bdt_pri_restri`.`bdt_uuid` = `dt`.`dt_uuid`
WHERE `bdt_pri_restri`.`bdt_id` = `dt`.`dt_id`;
UPDATE `bdt_pri_restri`, `code_list` SET `bdt_pri_restri`.`code_list_uuid` = `code_list`.`code_list_uuid`
WHERE `bdt_pri_restri`.`code_list_id` = `code_list`.`code_list_id`;
UPDATE `bdt_pri_restri`, `agency_id_list` SET `bdt_pri_restri`.`agency_id_list_uuid` = `agency_id_list`.`agency_id_list_uuid`
WHERE `bdt_pri_restri`.`agency_id_list_id` = `agency_id_list`.`agency_id_list_id`;

ALTER TABLE `bdt_sc_pri_restri`
    ADD COLUMN `bdt_sc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the DT table. It shall point to only DT that is a BDT (not a CDT).' AFTER `bdt_sc_id`,
    ADD COLUMN `code_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CODE_LIST table.' AFTER `code_list_id`,
    ADD COLUMN `agency_id_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the AGENCY_ID_LIST table. It is used in the case that the BDT content can be restricted to an agency identification.' AFTER `agency_id_list_id`;
UPDATE `bdt_sc_pri_restri`, `dt_sc` SET `bdt_sc_pri_restri`.`bdt_sc_uuid` = `dt_sc`.`dt_sc_uuid`
WHERE `bdt_sc_pri_restri`.`bdt_sc_id` = `dt_sc`.`dt_sc_id`;
UPDATE `bdt_sc_pri_restri`, `code_list` SET `bdt_sc_pri_restri`.`code_list_uuid` = `code_list`.`code_list_uuid`
WHERE `bdt_sc_pri_restri`.`code_list_id` = `code_list`.`code_list_id`;
UPDATE `bdt_sc_pri_restri`, `agency_id_list` SET `bdt_sc_pri_restri`.`agency_id_list_uuid` = `agency_id_list`.`agency_id_list_uuid`
WHERE `bdt_sc_pri_restri`.`agency_id_list_id` = `agency_id_list`.`agency_id_list_id`;

ALTER TABLE `bie_user_ext_revision`
    ADD COLUMN `ext_acc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This points to an extension ACC on which the ABIE indicated by the EXT_ABIE_ID column is based. E.g. It may point to an ApplicationAreaExtension ACC, AllExtension ACC, ActualLedgerExtension ACC, etc. It should be noted that an ACC record pointed to must have the OAGIS_COMPONENT_TYPE = 2 (Extension).' AFTER `ext_acc_id`,
    ADD COLUMN `user_ext_acc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This column points to the specific revision of a User Extension ACC (this is an ACC whose OAGIS_COMPONENT_TYPE = 4) currently used by the ABIE as indicated by the EXT_ABIE_ID or the by the TOP_LEVEL_ABIE_ID (in case of the AllExtension).' AFTER `user_ext_acc_id`;
UPDATE `bie_user_ext_revision`, `acc` SET `bie_user_ext_revision`.`ext_acc_uuid` = `acc`.`acc_uuid`
WHERE `bie_user_ext_revision`.`ext_acc_id` = `acc`.`acc_id`;
UPDATE `bie_user_ext_revision`, `acc` SET `bie_user_ext_revision`.`user_ext_acc_uuid` = `acc`.`acc_uuid`
WHERE `bie_user_ext_revision`.`user_ext_acc_id` = `acc`.`acc_id`;

ALTER TABLE `blob_content_manifest` ADD COLUMN `blob_content_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `blob_content_id`;
UPDATE `blob_content_manifest`, `blob_content` SET `blob_content_manifest`.`blob_content_uuid` = `blob_content`.`blob_content_uuid`
WHERE `blob_content_manifest`.`blob_content_id` = `blob_content`.`blob_content_id`;

ALTER TABLE `cdt_awd_pri`
    ADD COLUMN `cdt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key pointing to a CDT in the DT table.' AFTER `cdt_id`;
UPDATE `cdt_awd_pri`, `dt` SET `cdt_awd_pri`.`cdt_uuid` = `dt`.`dt_uuid`
WHERE `cdt_awd_pri`.`cdt_id` = `dt`.`dt_id`;

ALTER TABLE `cdt_ref_spec`
    ADD COLUMN `cdt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `cdt_id`;
UPDATE `cdt_ref_spec`, `dt` SET `cdt_ref_spec`.`cdt_uuid` = `dt`.`dt_uuid`
WHERE `cdt_ref_spec`.`cdt_id` = `dt`.`dt_id`;

ALTER TABLE `cdt_sc_awd_pri`
    ADD COLUMN `cdt_sc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key pointing to the supplementary component (SC).' AFTER `cdt_sc_id`;
UPDATE `cdt_sc_awd_pri`, `dt_sc` SET `cdt_sc_awd_pri`.`cdt_sc_uuid` = `dt_sc`.`dt_sc_uuid`
WHERE `cdt_sc_awd_pri`.`cdt_sc_id` = `dt_sc`.`dt_sc_id`;

ALTER TABLE `cdt_sc_ref_spec`
    ADD COLUMN `cdt_sc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `cdt_sc_id`;
UPDATE `cdt_sc_ref_spec`, `dt_sc` SET `cdt_sc_ref_spec`.`cdt_sc_uuid` = `dt_sc`.`dt_sc_uuid`
WHERE `cdt_sc_ref_spec`.`cdt_sc_id` = `dt_sc`.`dt_sc_id`;

ALTER TABLE `code_list`
    ADD COLUMN `agency_id_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the AGENCY_ID_LIST_VALUE table. It indicates the organization which maintains the code list.' AFTER `agency_id_list_value_id`,
    ADD COLUMN `based_code_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the CODE_LIST table itself. This identifies the code list on which this code list is based, if any. The derivation may be restriction and/or extension.' AFTER `based_code_list_id`,
    ADD COLUMN `replacement_code_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.' AFTER `replacement_code_list_id`,
    ADD COLUMN `prev_code_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.' AFTER `prev_code_list_id`,
    ADD COLUMN `next_code_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.' AFTER `next_code_list_id`;
UPDATE `code_list`, `agency_id_list_value` SET `code_list`.`agency_id_list_value_uuid` = `agency_id_list_value`.`agency_id_list_value_uuid`
WHERE `code_list`.`agency_id_list_value_id` = `agency_id_list_value`.`agency_id_list_value_id`;
UPDATE `code_list` AS tmp, `code_list` SET tmp.`based_code_list_uuid` = `code_list`.`code_list_uuid`
WHERE tmp.`based_code_list_id` = `code_list`.`code_list_id`;
UPDATE `code_list` AS tmp, `code_list` SET tmp.`replacement_code_list_uuid` = `code_list`.`code_list_uuid`
WHERE tmp.`replacement_code_list_id` = `code_list`.`code_list_id`;
UPDATE `code_list` AS tmp, `code_list` SET tmp.`prev_code_list_uuid` = `code_list`.`code_list_uuid`
WHERE tmp.`prev_code_list_id` = `code_list`.`code_list_id`;
UPDATE `code_list` AS tmp, `code_list` SET tmp.`next_code_list_uuid` = `code_list`.`code_list_uuid`
WHERE tmp.`next_code_list_id` = `code_list`.`code_list_id`;

ALTER TABLE `code_list_manifest` ADD COLUMN `code_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `code_list_id`;
UPDATE `code_list_manifest`, `code_list` SET `code_list_manifest`.`code_list_uuid` = `code_list`.`code_list_uuid`
WHERE `code_list_manifest`.`code_list_id` = `code_list`.`code_list_id`;

ALTER TABLE `code_list_value`
    ADD COLUMN `code_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CODE_LIST table. It indicates the code list this code value belonging to.' AFTER `code_list_id`,
    ADD COLUMN `based_code_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CODE_LIST_VALUE table itself. This column is used when the CODE_LIST is derived from the based CODE_LIST.' AFTER `based_code_list_value_id`,
    ADD COLUMN `replacement_code_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.' AFTER `replacement_code_list_value_id`,
    ADD COLUMN `prev_code_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.' AFTER `prev_code_list_value_id`,
    ADD COLUMN `next_code_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.' AFTER `next_code_list_value_id`;
UPDATE `code_list_value`, `code_list` SET `code_list_value`.`code_list_uuid` = `code_list`.`code_list_uuid`
WHERE `code_list_value`.`code_list_id` = `code_list`.`code_list_id`;
UPDATE `code_list_value` AS tmp, `code_list_value` SET tmp.`based_code_list_value_uuid` = `code_list_value`.`code_list_value_uuid`
WHERE tmp.`based_code_list_value_id` = `code_list_value`.`code_list_value_id`;
UPDATE `code_list_value` AS tmp, `code_list_value` SET tmp.`replacement_code_list_value_uuid` = `code_list_value`.`code_list_value_uuid`
WHERE tmp.`replacement_code_list_value_id` = `code_list_value`.`code_list_value_id`;
UPDATE `code_list_value` AS tmp, `code_list_value` SET tmp.`prev_code_list_value_uuid` = `code_list_value`.`code_list_value_uuid`
WHERE tmp.`prev_code_list_value_id` = `code_list_value`.`code_list_value_id`;
UPDATE `code_list_value` AS tmp, `code_list_value` SET tmp.`next_code_list_value_uuid` = `code_list_value`.`code_list_value_uuid`
WHERE tmp.`next_code_list_value_id` = `code_list_value`.`code_list_value_id`;

ALTER TABLE `code_list_value_manifest` ADD COLUMN `code_list_value_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `code_list_value_id`;
UPDATE `code_list_value_manifest`, `code_list_value` SET `code_list_value_manifest`.`code_list_value_uuid` = `code_list_value`.`code_list_value_uuid`
WHERE `code_list_value_manifest`.`code_list_value_id` = `code_list_value`.`code_list_value_id`;

ALTER TABLE `ctx_scheme`
    ADD COLUMN `code_list_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is the foreign key to the CODE_LIST table. It identifies the code list associated with this context scheme.' AFTER `code_list_id`;
UPDATE `ctx_scheme`, `code_list` SET `ctx_scheme`.`code_list_uuid` = `code_list`.`code_list_uuid`
WHERE `ctx_scheme`.`code_list_id` = `code_list`.`code_list_id`;

ALTER TABLE `dt`
    ADD COLUMN `based_dt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key pointing to the DT table itself. This column must be blank when the DT_TYPE is CDT. This column must not be blank when the DT_TYPE is BDT.' AFTER `based_dt_id`,
    ADD COLUMN `replacement_dt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.' AFTER `replacement_dt_id`,
    ADD COLUMN `prev_dt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.' AFTER `prev_dt_id`,
    ADD COLUMN `next_dt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.' AFTER `next_dt_id`;
UPDATE `dt` AS tmp, `dt` SET tmp.`based_dt_uuid` = `dt`.`dt_uuid`
WHERE tmp.`based_dt_id` = `dt`.`dt_id`;
UPDATE `dt` AS tmp, `dt` SET tmp.`replacement_dt_uuid` = `dt`.`dt_uuid`
WHERE tmp.`replacement_dt_id` = `dt`.`dt_id`;
UPDATE `dt` AS tmp, `dt` SET tmp.`prev_dt_uuid` = `dt`.`dt_uuid`
WHERE tmp.`prev_dt_id` = `dt`.`dt_id`;
UPDATE `dt` AS tmp, `dt` SET tmp.`next_dt_uuid` = `dt`.`dt_uuid`
WHERE tmp.`next_dt_id` = `dt`.`dt_id`;

ALTER TABLE `dt_manifest` ADD COLUMN `dt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `dt_id`;
UPDATE `dt_manifest`, `dt` SET `dt_manifest`.`dt_uuid` = `dt`.`dt_uuid`
WHERE `dt_manifest`.`dt_id` = `dt`.`dt_id`;

ALTER TABLE `dt_sc`
    ADD COLUMN `owner_dt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreigned key to the DT table indicating the data type, to which this supplementary component belongs.' AFTER `owner_dt_id`,
    ADD COLUMN `based_dt_sc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the DT_SC table itself. This column is used when the SC is derived from the based DT.' AFTER `based_dt_sc_id`,
    ADD COLUMN `replacement_dt_sc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.' AFTER `replacement_dt_sc_id`,
    ADD COLUMN `prev_dt_sc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.' AFTER `prev_dt_sc_id`,
    ADD COLUMN `next_dt_sc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.' AFTER `next_dt_sc_id`;
UPDATE `dt_sc`, `dt` SET `dt_sc`.`owner_dt_uuid` = `dt`.`dt_uuid`
WHERE `dt_sc`.`owner_dt_id` = `dt`.`dt_id`;
UPDATE `dt_sc` AS tmp, `dt_sc` SET tmp.`based_dt_sc_uuid` = `dt_sc`.`dt_sc_uuid`
WHERE tmp.`based_dt_sc_id` = `dt_sc`.`dt_sc_id`;
UPDATE `dt_sc` AS tmp, `dt_sc` SET tmp.`replacement_dt_sc_uuid` = `dt_sc`.`dt_sc_uuid`
WHERE tmp.`replacement_dt_sc_id` = `dt_sc`.`dt_sc_id`;
UPDATE `dt_sc` AS tmp, `dt_sc` SET tmp.`prev_dt_sc_uuid` = `dt_sc`.`dt_sc_uuid`
WHERE tmp.`prev_dt_sc_id` = `dt_sc`.`dt_sc_id`;
UPDATE `dt_sc` AS tmp, `dt_sc` SET tmp.`next_dt_sc_uuid` = `dt_sc`.`dt_sc_uuid`
WHERE tmp.`next_dt_sc_id` = `dt_sc`.`dt_sc_id`;

ALTER TABLE `dt_sc_manifest` ADD COLUMN `dt_sc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `dt_sc_id`;
UPDATE `dt_sc_manifest`, `dt_sc` SET `dt_sc_manifest`.`dt_sc_uuid` = `dt_sc`.`dt_sc_uuid`
WHERE `dt_sc_manifest`.`dt_sc_id` = `dt_sc`.`dt_sc_id`;

ALTER TABLE `dt_usage_rule`
    ADD COLUMN `target_dt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the DT_ID for assigning a usage rule to the corresponding DT content component.' AFTER `target_dt_id`,
    ADD COLUMN `target_dt_sc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the DT_SC_ID for assigning a usage rule to the corresponding DT_SC.' AFTER `target_dt_sc_id`;
UPDATE `dt_usage_rule`, `dt` SET `dt_usage_rule`.`target_dt_uuid` = `dt`.`dt_uuid`
WHERE `dt_usage_rule`.`target_dt_id` = `dt`.`dt_id`;
UPDATE `dt_usage_rule`, `dt_sc` SET `dt_usage_rule`.`target_dt_sc_uuid` = `dt_sc`.`dt_sc_uuid`
WHERE `dt_usage_rule`.`target_dt_sc_id` = `dt_sc`.`dt_sc_id`;

-- Drop old columns
ALTER TABLE `acc`
    DROP FOREIGN KEY `acc_based_acc_id_fk`,
    DROP FOREIGN KEY `acc_replacement_acc_id_fk`,
    DROP FOREIGN KEY `acc_prev_acc_id_fk`,
    DROP FOREIGN KEY `acc_next_acc_id_fk`;
ALTER TABLE `acc`
    DROP COLUMN `based_acc_id`,
    DROP COLUMN `replacement_acc_id`,
    DROP COLUMN `prev_acc_id`,
    DROP COLUMN `next_acc_id`;

ALTER TABLE `acc_manifest` DROP FOREIGN KEY `acc_manifest_acc_id_fk`;
ALTER TABLE `acc_manifest` DROP COLUMN `acc_id`;

ALTER TABLE `agency_id_list`
    DROP FOREIGN KEY `agency_id_list_agency_id_list_value_id_fk`,
    DROP FOREIGN KEY `agency_id_list_based_agency_id_list_id_fk`,
    DROP FOREIGN KEY `agency_id_list_replacement_agency_id_list_id_fk`,
    DROP FOREIGN KEY `agency_id_list_prev_agency_id_list_id_fk`,
    DROP FOREIGN KEY `agency_id_list_next_agency_id_list_id_fk`;
ALTER TABLE `agency_id_list`
    DROP COLUMN `agency_id_list_value_id`,
    DROP COLUMN `based_agency_id_list_id`,
    DROP COLUMN `replacement_agency_id_list_id`,
    DROP COLUMN `prev_agency_id_list_id`,
    DROP COLUMN `next_agency_id_list_id`;

ALTER TABLE `agency_id_list_manifest` DROP FOREIGN KEY `agency_id_list_manifest_agency_id_list_id_fk`;
ALTER TABLE `agency_id_list_manifest` DROP COLUMN `agency_id_list_id`;

ALTER TABLE `agency_id_list_value`
    DROP FOREIGN KEY `agency_id_list_value_owner_list_id_fk`,
    DROP FOREIGN KEY `agency_id_list_value_based_agency_id_list_value_id_fk`,
    DROP FOREIGN KEY `agency_id_list_value_replacement_agency_id_list_value_id_fk`,
    DROP FOREIGN KEY `agency_id_list_value_prev_agency_id_list_value_id_fk`,
    DROP FOREIGN KEY `agency_id_list_value_next_agency_id_list_value_id_fk`;
ALTER TABLE `agency_id_list_value`
    DROP COLUMN `owner_list_id`,
    DROP COLUMN `based_agency_id_list_value_id`,
    DROP COLUMN `replacement_agency_id_list_value_id`,
    DROP COLUMN `prev_agency_id_list_value_id`,
    DROP COLUMN `next_agency_id_list_value_id`;

ALTER TABLE `agency_id_list_value_manifest` DROP FOREIGN KEY `agency_id_list_value_manifest_agency_id_list_value_id_fk`;
ALTER TABLE `agency_id_list_value_manifest` DROP COLUMN `agency_id_list_value_id`;

ALTER TABLE `ascc`
    DROP FOREIGN KEY `ascc_from_acc_id_fk`,
    DROP FOREIGN KEY `ascc_to_asccp_id_fk`,
    DROP FOREIGN KEY `ascc_replacement_ascc_id_fk`,
    DROP FOREIGN KEY `ascc_prev_ascc_id_fk`,
    DROP FOREIGN KEY `ascc_next_ascc_id_fk`;
ALTER TABLE `ascc`
    DROP COLUMN `from_acc_id`,
    DROP COLUMN `to_asccp_id`,
    DROP COLUMN `replacement_ascc_id`,
    DROP COLUMN `prev_ascc_id`,
    DROP COLUMN `next_ascc_id`;

ALTER TABLE `ascc_bizterm` DROP FOREIGN KEY `ascc_bizterm_ascc_fk`;
ALTER TABLE `ascc_bizterm` DROP COLUMN `ascc_id`;

ALTER TABLE `ascc_manifest` DROP FOREIGN KEY `ascc_manifest_ascc_id_fk`;
ALTER TABLE `ascc_manifest` DROP COLUMN `ascc_id`;

ALTER TABLE `asccp`
    DROP FOREIGN KEY `asccp_role_of_acc_id_fk`,
    DROP FOREIGN KEY `asccp_replacement_asccp_id_fk`,
    DROP FOREIGN KEY `asccp_prev_asccp_id_fk`,
    DROP FOREIGN KEY `asccp_next_asccp_id_fk`;
ALTER TABLE `asccp`
    DROP COLUMN `role_of_acc_id`,
    DROP COLUMN `replacement_asccp_id`,
    DROP COLUMN `prev_asccp_id`,
    DROP COLUMN `next_asccp_id`;

ALTER TABLE `asccp_manifest` DROP FOREIGN KEY `asccp_manifest_asccp_id_fk`;
ALTER TABLE `asccp_manifest` DROP COLUMN `asccp_id`;

ALTER TABLE `bbie`
    DROP FOREIGN KEY `bbie_code_list_id_fk`,
    DROP FOREIGN KEY `bbie_agency_id_list_id_fk`;
ALTER TABLE `bbie`
    DROP COLUMN `code_list_id`,
    DROP COLUMN `agency_id_list_id`;

ALTER TABLE `bbie_sc`
    DROP FOREIGN KEY `bbie_sc_code_list_id_fk`,
    DROP FOREIGN KEY `bbie_sc_agency_id_list_id_fk`;
ALTER TABLE `bbie_sc`
    DROP COLUMN `code_list_id`,
    DROP COLUMN `agency_id_list_id`;

ALTER TABLE `bcc`
    DROP FOREIGN KEY `bcc_from_acc_id_fk`,
    DROP FOREIGN KEY `bcc_to_bccp_id_fk`,
    DROP FOREIGN KEY `bcc_replacement_bcc_id_fk`,
    DROP FOREIGN KEY `bcc_prev_bcc_id_fk`,
    DROP FOREIGN KEY `bcc_next_bcc_id_fk`;
ALTER TABLE `bcc`
    DROP COLUMN `from_acc_id`,
    DROP COLUMN `to_bccp_id`,
    DROP COLUMN `replacement_bcc_id`,
    DROP COLUMN `prev_bcc_id`,
    DROP COLUMN `next_bcc_id`;

ALTER TABLE `bcc_bizterm` DROP FOREIGN KEY `bcc_bizterm_bcc_fk`;
ALTER TABLE `bcc_bizterm` DROP COLUMN `bcc_id`;

ALTER TABLE `bcc_manifest` DROP FOREIGN KEY `bcc_manifest_bcc_id_fk`;
ALTER TABLE `bcc_manifest` DROP COLUMN `bcc_id`;

ALTER TABLE `bccp`
    DROP FOREIGN KEY `bccp_bdt_id_fk`,
    DROP FOREIGN KEY `bccp_replacement_bccp_id_fk`,
    DROP FOREIGN KEY `bccp_prev_bccp_id_fk`,
    DROP FOREIGN KEY `bccp_next_bccp_id_fk`;
ALTER TABLE `bccp`
    DROP COLUMN `bdt_id`,
    DROP COLUMN `replacement_bccp_id`,
    DROP COLUMN `prev_bccp_id`,
    DROP COLUMN `next_bccp_id`;

ALTER TABLE `bccp_manifest` DROP FOREIGN KEY `bccp_manifest_bccp_id_fk`;
ALTER TABLE `bccp_manifest` DROP COLUMN `bccp_id`;

ALTER TABLE `bdt_pri_restri`
    DROP FOREIGN KEY `bdt_pri_restri_bdt_id_fk`,
    DROP FOREIGN KEY `bdt_pri_restri_code_list_id_fk`,
    DROP FOREIGN KEY `bdt_pri_restri_agency_id_list_id_fk`;
ALTER TABLE `bdt_pri_restri`
    DROP COLUMN `bdt_id`,
    DROP COLUMN `code_list_id`,
    DROP COLUMN `agency_id_list_id`;

ALTER TABLE `bdt_sc_pri_restri`
    DROP FOREIGN KEY `bdt_sc_pri_restri_bdt_sc_id_fk`,
    DROP FOREIGN KEY `bdt_sc_pri_restri_code_list_id_fk`,
    DROP FOREIGN KEY `bdt_sc_pri_restri_agency_id_list_id_fk`;
ALTER TABLE `bdt_sc_pri_restri`
    DROP COLUMN `bdt_sc_id`,
    DROP COLUMN `code_list_id`,
    DROP COLUMN `agency_id_list_id`;

ALTER TABLE `bie_user_ext_revision`
    DROP FOREIGN KEY `bie_user_ext_revision_ext_acc_id_fk`,
    DROP FOREIGN KEY `bie_user_ext_revision_user_ext_acc_id_fk`;
ALTER TABLE `bie_user_ext_revision`
    DROP COLUMN `ext_acc_id`,
    DROP COLUMN `user_ext_acc_id`;

ALTER TABLE `blob_content_manifest` DROP FOREIGN KEY `blob_content_manifest_blob_content_id_fk`;
ALTER TABLE `blob_content_manifest` DROP COLUMN `blob_content_id`;

ALTER TABLE `cdt_awd_pri` DROP FOREIGN KEY `cdt_awd_pri_cdt_id_fk`;
ALTER TABLE `cdt_awd_pri` DROP COLUMN `cdt_id`;

ALTER TABLE `cdt_ref_spec` DROP FOREIGN KEY `cdt_ref_spec_cdt_id_fk`;
ALTER TABLE `cdt_ref_spec` DROP COLUMN `cdt_id`;

ALTER TABLE `cdt_sc_awd_pri` DROP FOREIGN KEY `cdt_sc_awd_pri_cdt_sc_id_fk`;
ALTER TABLE `cdt_sc_awd_pri` DROP COLUMN `cdt_sc_id`;

ALTER TABLE `cdt_sc_ref_spec` DROP FOREIGN KEY `cdt_sc_ref_spec_cdt_sc_id_fk`;
ALTER TABLE `cdt_sc_ref_spec` DROP COLUMN `cdt_sc_id`;

ALTER TABLE `code_list`
    DROP FOREIGN KEY `code_list_agency_id_list_value_id_fk`,
    DROP FOREIGN KEY `code_list_based_code_list_id_fk`,
    DROP FOREIGN KEY `code_list_replacement_code_list_id_fk`,
    DROP FOREIGN KEY `code_list_prev_code_list_id_fk`,
    DROP FOREIGN KEY `code_list_next_code_list_id_fk`;
ALTER TABLE `code_list`
    DROP COLUMN `agency_id_list_value_id`,
    DROP COLUMN `based_code_list_id`,
    DROP COLUMN `replacement_code_list_id`,
    DROP COLUMN `prev_code_list_id`,
    DROP COLUMN `next_code_list_id`;

ALTER TABLE `code_list_manifest` DROP FOREIGN KEY `code_list_manifest_code_list_id_fk`;
ALTER TABLE `code_list_manifest` DROP COLUMN `code_list_id`;

ALTER TABLE `code_list_value`
    DROP FOREIGN KEY `code_list_value_code_list_id_fk`,
    DROP FOREIGN KEY `code_list_value_based_code_list_value_id_fk`,
    DROP FOREIGN KEY `code_list_value_replacement_code_list_value_id_fk`,
    DROP FOREIGN KEY `code_list_value_prev_code_list_value_id_fk`,
    DROP FOREIGN KEY `code_list_value_next_code_list_value_id_fk`;
ALTER TABLE `code_list_value`
    DROP COLUMN `code_list_id`,
    DROP COLUMN `based_code_list_value_id`,
    DROP COLUMN `replacement_code_list_value_id`,
    DROP COLUMN `prev_code_list_value_id`,
    DROP COLUMN `next_code_list_value_id`;

ALTER TABLE `code_list_value_manifest` DROP FOREIGN KEY `code_list_value_manifest_code_list_value_id_fk`;
ALTER TABLE `code_list_value_manifest` DROP COLUMN `code_list_value_id`;

ALTER TABLE `ctx_scheme` DROP FOREIGN KEY `ctx_scheme_code_list_id_fk`;
ALTER TABLE `ctx_scheme` DROP COLUMN `code_list_id`;

ALTER TABLE `dt`
    DROP FOREIGN KEY `dt_based_dt_id_fk`,
    DROP FOREIGN KEY `dt_replacement_dt_id_fk`,
    DROP FOREIGN KEY `dt_prev_dt_id_fk`,
    DROP FOREIGN KEY `dt_next_dt_id_fk`;
ALTER TABLE `dt`
    DROP COLUMN `based_dt_id`,
    DROP COLUMN `replacement_dt_id`,
    DROP COLUMN `prev_dt_id`,
    DROP COLUMN `next_dt_id`;

ALTER TABLE `dt_manifest` DROP FOREIGN KEY `dt_manifest_dt_id_fk`;
ALTER TABLE `dt_manifest` DROP COLUMN `dt_id`;

ALTER TABLE `dt_sc`
    DROP FOREIGN KEY `dt_sc_owner_dt_id_fk`,
    DROP FOREIGN KEY `dt_sc_based_dt_sc_id_fk`,
    DROP FOREIGN KEY `dt_sc_replacement_dt_sc_id_fk`,
    DROP FOREIGN KEY `dt_sc_prev_dt_sc_id_fk`,
    DROP FOREIGN KEY `dt_sc_next_dt_sc_id_fk`;
ALTER TABLE `dt_sc`
    DROP COLUMN `owner_dt_id`,
    DROP COLUMN `based_dt_sc_id`,
    DROP COLUMN `replacement_dt_sc_id`,
    DROP COLUMN `prev_dt_sc_id`,
    DROP COLUMN `next_dt_sc_id`;

ALTER TABLE `dt_sc_manifest` DROP FOREIGN KEY `dt_sc_manifest_dt_sc_id_fk`;
ALTER TABLE `dt_sc_manifest` DROP COLUMN `dt_sc_id`;

ALTER TABLE `dt_usage_rule`
    DROP FOREIGN KEY `dt_usage_rule_target_dt_id_fk`,
    DROP FOREIGN KEY `dt_usage_rule_target_dt_sc_id_fk`;
ALTER TABLE `dt_usage_rule`
    DROP COLUMN `target_dt_id`,
    DROP COLUMN `target_dt_sc_id`;

ALTER TABLE `acc` DROP COLUMN `acc_id`;
ALTER TABLE `ascc` DROP COLUMN `ascc_id`;
ALTER TABLE `bcc` DROP COLUMN `bcc_id`;
ALTER TABLE `asccp` DROP COLUMN `asccp_id`;
ALTER TABLE `bccp` DROP COLUMN `bccp_id`;
ALTER TABLE `blob_content` DROP COLUMN `blob_content_id`;
ALTER TABLE `dt` DROP COLUMN `dt_id`;
ALTER TABLE `dt_sc` DROP COLUMN `dt_sc_id`;
ALTER TABLE `code_list` DROP COLUMN `code_list_id`;
ALTER TABLE `code_list_value` DROP COLUMN `code_list_value_id`;
ALTER TABLE `agency_id_list` DROP COLUMN `agency_id_list_id`;
ALTER TABLE `agency_id_list_value` DROP COLUMN `agency_id_list_value_id`;

-- Rename `*_uuid` TO `*_id`
ALTER TABLE `acc` CHANGE `acc_uuid` `acc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `ascc` CHANGE `ascc_uuid` `ascc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bcc` CHANGE `bcc_uuid` `bcc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `asccp` CHANGE `asccp_uuid` `asccp_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bccp` CHANGE `bccp_uuid` `bccp_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `blob_content` CHANGE `blob_content_uuid` `blob_content_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `dt` CHANGE `dt_uuid` `dt_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `dt_sc` CHANGE `dt_sc_uuid` `dt_sc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `code_list` CHANGE `code_list_uuid` `code_list_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `code_list_value` CHANGE `code_list_value_uuid` `code_list_value_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `agency_id_list` CHANGE `agency_id_list_uuid` `agency_id_list_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `agency_id_list_value` CHANGE `agency_id_list_value_uuid` `agency_id_list_value_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';

ALTER TABLE `acc`
    CHANGE `based_acc_uuid` `based_acc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'BASED_ACC_ID is a foreign key to the ACC table itself. It represents the ACC that is qualified by this ACC. In general CCS sense, a qualification can be a content extension or restriction, but the current scope supports only extension.',
    CHANGE `replacement_acc_uuid` `replacement_acc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    CHANGE `prev_acc_uuid` `prev_acc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    CHANGE `next_acc_uuid` `next_acc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.';
ALTER TABLE `acc_manifest` CHANGE `acc_uuid` `acc_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `agency_id_list`
    CHANGE `agency_id_list_value_uuid` `agency_id_list_value_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is the identification of the agency or organization which developed and/or maintains the list. Theoretically, this can be modeled as a self-reference foreign key, but it is not implemented at this point.',
    CHANGE `based_agency_id_list_uuid` `based_agency_id_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the AGENCY_ID_LIST table itself. This identifies the agency id list on which this agency id list is based, if any. The derivation may be restriction and/or extension.',
    CHANGE `replacement_agency_id_list_uuid` `replacement_agency_id_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    CHANGE `prev_agency_id_list_uuid` `prev_agency_id_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    CHANGE `next_agency_id_list_uuid` `next_agency_id_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.';
ALTER TABLE `agency_id_list_manifest` CHANGE `agency_id_list_uuid` `agency_id_list_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `agency_id_list_value`
    CHANGE `owner_list_uuid` `owner_list_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the agency identification list in the AGENCY_ID_LIST table this value belongs to.',
    CHANGE `based_agency_id_list_value_uuid` `based_agency_id_list_value_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the AGENCY_ID_LIST_VALUE table itself. This column is used when the AGENCY_ID_LIST_VALUE is derived from the based AGENCY_ID_LIST_VALUE.',
    CHANGE `replacement_agency_id_list_value_uuid` `replacement_agency_id_list_value_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    CHANGE `prev_agency_id_list_value_uuid` `prev_agency_id_list_value_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    CHANGE `next_agency_id_list_value_uuid` `next_agency_id_list_value_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.';
ALTER TABLE `agency_id_list_value_manifest` CHANGE `agency_id_list_value_uuid` `agency_id_list_value_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `ascc`
    CHANGE `from_acc_uuid` `from_acc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'FROM_ACC_ID is a foreign key pointing to an ACC record. It is basically pointing to a parent data element (type) of the TO_ASCCP_ID.',
    CHANGE `to_asccp_uuid` `to_asccp_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'TO_ASCCP_ID is a foreign key to an ASCCP table record. It is basically pointing to a child data element of the FROM_ACC_ID.',
    CHANGE `replacement_ascc_uuid` `replacement_ascc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    CHANGE `prev_ascc_uuid` `prev_ascc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    CHANGE `next_ascc_uuid` `next_ascc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.';
ALTER TABLE `ascc_bizterm` CHANGE `ascc_uuid` `ascc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'An internal ID of the associated ASCC';
ALTER TABLE `ascc_manifest` CHANGE `ascc_uuid` `ascc_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `asccp`
    CHANGE `role_of_acc_uuid` `role_of_acc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'The ACC from which this ASCCP is created (ASCCP applies role to the ACC).',
    CHANGE `replacement_asccp_uuid` `replacement_asccp_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    CHANGE `prev_asccp_uuid` `prev_asccp_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    CHANGE `next_asccp_uuid` `next_asccp_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.';
ALTER TABLE `asccp_manifest` CHANGE `asccp_uuid` `asccp_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `bbie`
    CHANGE `code_list_uuid` `code_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the CODE_LIST table. If a code list is assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this association), then this column stores the assigned code list. It should be noted that one of the possible primitives assignable to the BDT_PRI_RESTRI_ID column may also be a code list. So this column is typically used when the user wants to assign another code list different from the one permissible by the CC model.',
    CHANGE `agency_id_list_uuid` `agency_id_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the AGENCY_ID_LIST table. It is used in the case that the BDT content can be restricted to an agency identification.';
ALTER TABLE `bbie_sc`
    CHANGE `code_list_uuid` `code_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the CODE_LIST table. If a code list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned code list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be a code list. So this column is typically used when the user wants to assign another code list different from the one permissible by the CC model.\n\nThis column is, the DT_SC_PRI_RESTRI_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.',
    CHANGE `agency_id_list_uuid` `agency_id_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the AGENCY_ID_LIST table. If a agency ID list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned Agency ID list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be an Agency ID list. So this column is typically used only when the user wants to assign another Agency ID list different from the one permissible by the CC model.\n\nThis column, the DT_SC_PRI_RESTRI_ID column, and CODE_LIST_ID column cannot have a value at the same time.';
ALTER TABLE `bcc`
    CHANGE `from_acc_uuid` `from_acc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'FROM_ACC_ID is a foreign key pointing to an ACC record. It is basically pointing to a parent data element (type) of the TO_BCCP_ID. \n\nNote that for the BCC history records, this column always points to the ACC_ID of the current record of an ACC.',
    CHANGE `to_bccp_uuid` `to_bccp_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'TO_BCCP_ID is a foreign key to an BCCP table record. It is basically pointing to a child data element of the FROM_ACC_ID. \n\nNote that for the BCC history records, this column always points to the BCCP_ID of the current record of a BCCP.',
    CHANGE `replacement_bcc_uuid` `replacement_bcc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    CHANGE `prev_bcc_uuid` `prev_bcc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    CHANGE `next_bcc_uuid` `next_bcc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.';
ALTER TABLE `bcc_bizterm` CHANGE `bcc_uuid` `bcc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'An internal ID of the associated BCC';
ALTER TABLE `bcc_manifest` CHANGE `bcc_uuid` `bcc_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `bccp`
    CHANGE `bdt_uuid` `bdt_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key pointing to the DT table indicating the data typye or data format of the BCCP. Only DT_ID which DT_Type is BDT can be used.',
    CHANGE `replacement_bccp_uuid` `replacement_bccp_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    CHANGE `prev_bccp_uuid` `prev_bccp_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    CHANGE `next_bccp_uuid` `next_bccp_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.';
ALTER TABLE `bccp_manifest` CHANGE `bccp_uuid` `bccp_id` char(36) CHARACTER SET ascii DEFAULT NULL;
ALTER TABLE `bdt_pri_restri`
    CHANGE `bdt_uuid` `bdt_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the DT table. It shall point to only DT that is a BDT (not a CDT).',
    CHANGE `code_list_uuid` `code_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CODE_LIST table.',
    CHANGE `agency_id_list_uuid` `agency_id_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the AGENCY_ID_LIST table. It is used in the case that the BDT content can be restricted to an agency identification.';
ALTER TABLE `bdt_sc_pri_restri`
    CHANGE `bdt_sc_uuid` `bdt_sc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the DT table. It shall point to only DT that is a BDT (not a CDT).',
    CHANGE `code_list_uuid` `code_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CODE_LIST table.',
    CHANGE `agency_id_list_uuid` `agency_id_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the AGENCY_ID_LIST table. It is used in the case that the BDT content can be restricted to an agency identification.';
ALTER TABLE `bie_user_ext_revision`
    CHANGE `ext_acc_uuid` `ext_acc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This points to an extension ACC on which the ABIE indicated by the EXT_ABIE_ID column is based. E.g. It may point to an ApplicationAreaExtension ACC, AllExtension ACC, ActualLedgerExtension ACC, etc. It should be noted that an ACC record pointed to must have the OAGIS_COMPONENT_TYPE = 2 (Extension).',
    CHANGE `user_ext_acc_uuid` `user_ext_acc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This column points to the specific revision of a User Extension ACC (this is an ACC whose OAGIS_COMPONENT_TYPE = 4) currently used by the ABIE as indicated by the EXT_ABIE_ID or the by the TOP_LEVEL_ABIE_ID (in case of the AllExtension).';
ALTER TABLE `blob_content_manifest` CHANGE `blob_content_uuid` `blob_content_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `cdt_awd_pri`
    CHANGE `cdt_uuid` `cdt_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key pointing to a CDT in the DT table.';
ALTER TABLE `cdt_ref_spec`
    CHANGE `cdt_uuid` `cdt_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `cdt_sc_awd_pri`
    CHANGE `cdt_sc_uuid` `cdt_sc_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key pointing to the supplementary component (SC).';
ALTER TABLE `cdt_sc_ref_spec`
    CHANGE `cdt_sc_uuid` `cdt_sc_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `code_list`
    CHANGE `agency_id_list_value_uuid` `agency_id_list_value_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the AGENCY_ID_LIST_VALUE table. It indicates the organization which maintains the code list.',
    CHANGE `based_code_list_uuid` `based_code_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the CODE_LIST table itself. This identifies the code list on which this code list is based, if any. The derivation may be restriction and/or extension.',
    CHANGE `replacement_code_list_uuid` `replacement_code_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    CHANGE `prev_code_list_uuid` `prev_code_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    CHANGE `next_code_list_uuid` `next_code_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.';
ALTER TABLE `code_list_manifest` CHANGE `code_list_uuid` `code_list_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `code_list_value`
    CHANGE `code_list_uuid` `code_list_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the CODE_LIST table. It indicates the code list this code value belonging to.',
    CHANGE `based_code_list_value_uuid` `based_code_list_value_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CODE_LIST_VALUE table itself. This column is used when the CODE_LIST is derived from the based CODE_LIST.',
    CHANGE `replacement_code_list_value_uuid` `replacement_code_list_value_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    CHANGE `prev_code_list_value_uuid` `prev_code_list_value_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    CHANGE `next_code_list_value_uuid` `next_code_list_value_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.';
ALTER TABLE `code_list_value_manifest` CHANGE `code_list_value_uuid` `code_list_value_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `ctx_scheme`
    CHANGE `code_list_uuid` `code_list_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is the foreign key to the CODE_LIST table. It identifies the code list associated with this context scheme.';
ALTER TABLE `dt`
    CHANGE `based_dt_uuid` `based_dt_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key pointing to the DT table itself. This column must be blank when the DT_TYPE is CDT. This column must not be blank when the DT_TYPE is BDT.',
    CHANGE `replacement_dt_uuid` `replacement_dt_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    CHANGE `prev_dt_uuid` `prev_dt_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    CHANGE `next_dt_uuid` `next_dt_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.';
ALTER TABLE `dt_manifest` CHANGE `dt_uuid` `dt_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `dt_sc`
    CHANGE `owner_dt_uuid` `owner_dt_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the DT table indicating the data type, to which this supplementary component belongs.',
    CHANGE `based_dt_sc_uuid` `based_dt_sc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the DT_SC table itself. This column is used when the SC is derived from the based DT.',
    CHANGE `replacement_dt_sc_uuid` `replacement_dt_sc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This refers to a replacement if the record is deprecated.',
    CHANGE `prev_dt_sc_uuid` `prev_dt_sc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    CHANGE `next_dt_sc_uuid` `next_dt_sc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.';
ALTER TABLE `dt_sc_manifest` CHANGE `dt_sc_uuid` `dt_sc_id` char(36) CHARACTER SET ascii NOT NULL;
ALTER TABLE `dt_usage_rule`
    CHANGE `target_dt_uuid` `target_dt_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the DT_ID for assigning a usage rule to the corresponding DT content component.',
    CHANGE `target_dt_sc_uuid` `target_dt_sc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the DT_SC_ID for assigning a usage rule to the corresponding DT_SC.';

-- Add foreign key constraints
ALTER TABLE `acc` ADD PRIMARY KEY (`acc_id`);
ALTER TABLE `ascc` ADD PRIMARY KEY (`ascc_id`);
ALTER TABLE `bcc` ADD PRIMARY KEY (`bcc_id`);
ALTER TABLE `asccp` ADD PRIMARY KEY (`asccp_id`);
ALTER TABLE `bccp` ADD PRIMARY KEY (`bccp_id`);
ALTER TABLE `blob_content` ADD PRIMARY KEY (`blob_content_id`);
ALTER TABLE `dt` ADD PRIMARY KEY (`dt_id`);
ALTER TABLE `dt_sc` ADD PRIMARY KEY (`dt_sc_id`);
ALTER TABLE `code_list` ADD PRIMARY KEY (`code_list_id`);
ALTER TABLE `code_list_value` ADD PRIMARY KEY (`code_list_value_id`);
ALTER TABLE `agency_id_list` ADD PRIMARY KEY (`agency_id_list_id`);
ALTER TABLE `agency_id_list_value` ADD PRIMARY KEY (`agency_id_list_value_id`);

ALTER TABLE `acc`
    ADD CONSTRAINT `acc_based_acc_id_fk` FOREIGN KEY (`based_acc_id`) REFERENCES `acc` (`acc_id`),
    ADD CONSTRAINT `acc_replacement_acc_id_fk` FOREIGN KEY (`replacement_acc_id`) REFERENCES `acc` (`acc_id`),
    ADD CONSTRAINT `acc_prev_acc_id_fk` FOREIGN KEY (`prev_acc_id`) REFERENCES `acc` (`acc_id`),
    ADD CONSTRAINT `acc_next_acc_id_fk` FOREIGN KEY (`next_acc_id`) REFERENCES `acc` (`acc_id`);
ALTER TABLE `acc_manifest` ADD CONSTRAINT `acc_manifest_acc_id_fk` FOREIGN KEY (`acc_id`) REFERENCES `acc` (`acc_id`);
ALTER TABLE `agency_id_list`
    ADD CONSTRAINT `agency_id_list_agency_id_list_value_id_fk` FOREIGN KEY (`agency_id_list_value_id`) REFERENCES `agency_id_list_value` (`agency_id_list_value_id`),
    ADD CONSTRAINT `agency_id_list_based_agency_id_list_id_fk` FOREIGN KEY (`based_agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`),
    ADD CONSTRAINT `agency_id_list_replacement_agency_id_list_id_fk` FOREIGN KEY (`replacement_agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`),
    ADD CONSTRAINT `agency_id_list_prev_agency_id_list_id_fk` FOREIGN KEY (`prev_agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`),
    ADD CONSTRAINT `agency_id_list_next_agency_id_list_id_fk` FOREIGN KEY (`next_agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`);
ALTER TABLE `agency_id_list_manifest` ADD CONSTRAINT `agency_id_list_manifest_agency_id_list_id_fk` FOREIGN KEY (`agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`);
ALTER TABLE `agency_id_list_value`
    ADD CONSTRAINT `agency_id_list_value_owner_list_id_fk` FOREIGN KEY (`owner_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`),
    ADD CONSTRAINT `agency_id_list_value_based_agency_id_list_value_id_fk` FOREIGN KEY (`based_agency_id_list_value_id`) REFERENCES `agency_id_list_value` (`agency_id_list_value_id`),
    ADD CONSTRAINT `agency_id_list_value_replacement_agency_id_list_value_id_fk` FOREIGN KEY (`replacement_agency_id_list_value_id`) REFERENCES `agency_id_list_value` (`agency_id_list_value_id`),
    ADD CONSTRAINT `agency_id_list_value_prev_agency_id_list_value_id_fk` FOREIGN KEY (`prev_agency_id_list_value_id`) REFERENCES `agency_id_list_value` (`agency_id_list_value_id`),
    ADD CONSTRAINT `agency_id_list_value_next_agency_id_list_value_id_fk` FOREIGN KEY (`next_agency_id_list_value_id`) REFERENCES `agency_id_list_value` (`agency_id_list_value_id`);
ALTER TABLE `agency_id_list_value_manifest` ADD CONSTRAINT `agency_id_list_value_manifest_agency_id_list_value_id_fk` FOREIGN KEY (`agency_id_list_value_id`) REFERENCES `agency_id_list_value` (`agency_id_list_value_id`);
ALTER TABLE `ascc`
    ADD CONSTRAINT `ascc_from_acc_id_fk` FOREIGN KEY (`from_acc_id`) REFERENCES `acc` (`acc_id`),
    ADD CONSTRAINT `ascc_to_asccp_id_fk` FOREIGN KEY (`to_asccp_id`) REFERENCES `asccp` (`asccp_id`),
    ADD CONSTRAINT `ascc_replacement_ascc_id_fk` FOREIGN KEY (`replacement_ascc_id`) REFERENCES `ascc` (`ascc_id`),
    ADD CONSTRAINT `ascc_prev_ascc_id_fk` FOREIGN KEY (`prev_ascc_id`) REFERENCES `ascc` (`ascc_id`),
    ADD CONSTRAINT `ascc_next_ascc_id_fk` FOREIGN KEY (`next_ascc_id`) REFERENCES `ascc` (`ascc_id`);
ALTER TABLE `ascc_bizterm` ADD CONSTRAINT `ascc_bizterm_ascc_id_fk` FOREIGN KEY (`ascc_id`) REFERENCES `ascc` (`ascc_id`);
ALTER TABLE `ascc_manifest` ADD CONSTRAINT `ascc_manifest_ascc_id_fk` FOREIGN KEY (`ascc_id`) REFERENCES `ascc` (`ascc_id`);
ALTER TABLE `asccp`
    ADD CONSTRAINT `asccp_role_of_acc_id_fk` FOREIGN KEY (`role_of_acc_id`) REFERENCES `acc` (`acc_id`),
    ADD CONSTRAINT `asccp_replacement_asccp_id_fk` FOREIGN KEY (`replacement_asccp_id`) REFERENCES `asccp` (`asccp_id`),
    ADD CONSTRAINT `asccp_prev_asccp_id_fk` FOREIGN KEY (`prev_asccp_id`) REFERENCES `asccp` (`asccp_id`),
    ADD CONSTRAINT `asccp_next_asccp_id_fk` FOREIGN KEY (`next_asccp_id`) REFERENCES `asccp` (`asccp_id`);
ALTER TABLE `asccp_manifest` ADD CONSTRAINT `asccp_manifest_asccp_id_fk` FOREIGN KEY (`asccp_id`) REFERENCES `asccp` (`asccp_id`);
ALTER TABLE `bbie`
    ADD CONSTRAINT `bbie_code_list_id_fk` FOREIGN KEY (`code_list_id`) REFERENCES `code_list` (`code_list_id`),
    ADD CONSTRAINT `bbie_agency_id_list_id_fk` FOREIGN KEY (`agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`);
ALTER TABLE `bbie_sc`
    ADD CONSTRAINT `bbie_sc_code_list_id_fk` FOREIGN KEY (`code_list_id`) REFERENCES `code_list` (`code_list_id`),
    ADD CONSTRAINT `bbie_sc_agency_id_list_id_fk` FOREIGN KEY (`agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`);
ALTER TABLE `bcc`
    ADD CONSTRAINT `bcc_from_acc_id_fk` FOREIGN KEY (`from_acc_id`) REFERENCES `acc` (`acc_id`),
    ADD CONSTRAINT `bcc_to_bccp_id_fk` FOREIGN KEY (`to_bccp_id`) REFERENCES `bccp` (`bccp_id`),
    ADD CONSTRAINT `bcc_replacement_bcc_id_fk` FOREIGN KEY (`replacement_bcc_id`) REFERENCES `bcc` (`bcc_id`),
    ADD CONSTRAINT `bcc_prev_bcc_id_fk` FOREIGN KEY (`prev_bcc_id`) REFERENCES `bcc` (`bcc_id`),
    ADD CONSTRAINT `bcc_next_bcc_id_fk` FOREIGN KEY (`next_bcc_id`) REFERENCES `bcc` (`bcc_id`);
ALTER TABLE `bcc_bizterm` ADD CONSTRAINT `bcc_bizterm_bcc_id_fk` FOREIGN KEY (`bcc_id`) REFERENCES `bcc` (`bcc_id`);
ALTER TABLE `bcc_manifest` ADD CONSTRAINT `bcc_manifest_bcc_id_fk` FOREIGN KEY (`bcc_id`) REFERENCES `bcc` (`bcc_id`);
ALTER TABLE `bccp`
    ADD CONSTRAINT `bccp_bdt_id_fk` FOREIGN KEY (`bdt_id`) REFERENCES `dt` (`dt_id`),
    ADD CONSTRAINT `bccp_replacement_bccp_id_fk` FOREIGN KEY (`replacement_bccp_id`) REFERENCES `bccp` (`bccp_id`),
    ADD CONSTRAINT `bccp_prev_bccp_id_fk` FOREIGN KEY (`prev_bccp_id`) REFERENCES `bccp` (`bccp_id`),
    ADD CONSTRAINT `bccp_next_bccp_id_fk` FOREIGN KEY (`next_bccp_id`) REFERENCES `bccp` (`bccp_id`);
ALTER TABLE `bccp_manifest` ADD CONSTRAINT `bccp_manifest_bccp_id_fk` FOREIGN KEY (`bccp_id`) REFERENCES `bccp` (`bccp_id`);
ALTER TABLE `bdt_pri_restri`
    ADD CONSTRAINT `bdt_pri_restri_bdt_id_fk` FOREIGN KEY (`bdt_id`) REFERENCES `dt` (`dt_id`),
    ADD CONSTRAINT `bdt_pri_restri_code_list_id_fk` FOREIGN KEY (`code_list_id`) REFERENCES `code_list` (`code_list_id`),
    ADD CONSTRAINT `bdt_pri_restri_agency_id_list_id_fk` FOREIGN KEY (`agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`);
ALTER TABLE `bdt_sc_pri_restri`
    ADD CONSTRAINT `bdt_sc_pri_restri_bdt_sc_id_fk` FOREIGN KEY (`bdt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`),
    ADD CONSTRAINT `bdt_sc_pri_restri_code_list_id_fk` FOREIGN KEY (`code_list_id`) REFERENCES `code_list` (`code_list_id`),
    ADD CONSTRAINT `bdt_sc_pri_restri_agency_id_list_id_fk` FOREIGN KEY (`agency_id_list_id`) REFERENCES `agency_id_list` (`agency_id_list_id`);
ALTER TABLE `bie_user_ext_revision`
    ADD CONSTRAINT `bie_user_ext_revision_ext_acc_id_fk` FOREIGN KEY (`ext_acc_id`) REFERENCES `acc` (`acc_id`),
    ADD CONSTRAINT `bie_user_ext_revision_user_ext_acc_id_fk` FOREIGN KEY (`user_ext_acc_id`) REFERENCES `acc` (`acc_id`);
ALTER TABLE `blob_content_manifest` ADD CONSTRAINT `blob_content_manifest_blob_content_id_fk` FOREIGN KEY (`blob_content_id`) REFERENCES `blob_content` (`blob_content_id`);
ALTER TABLE `cdt_awd_pri`
    ADD CONSTRAINT `cdt_awd_pri_cdt_id_fk` FOREIGN KEY (`cdt_id`) REFERENCES `dt` (`dt_id`);
ALTER TABLE `cdt_ref_spec`
    ADD CONSTRAINT `cdt_ref_spec_cdt_id_fk` FOREIGN KEY (`cdt_id`) REFERENCES `dt` (`dt_id`);
ALTER TABLE `cdt_sc_awd_pri`
    ADD CONSTRAINT `cdt_sc_awd_pri_cdt_sc_id_fk` FOREIGN KEY (`cdt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`);
ALTER TABLE `cdt_sc_ref_spec`
    ADD CONSTRAINT `cdt_sc_ref_spec_cdt_sc_id_fk` FOREIGN KEY (`cdt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`);
ALTER TABLE `code_list`
    ADD CONSTRAINT `code_list_agency_id_list_value_id_fk` FOREIGN KEY (`agency_id_list_value_id`) REFERENCES `agency_id_list_value` (`agency_id_list_value_id`),
    ADD CONSTRAINT `code_list_based_code_list_id_fk` FOREIGN KEY (`based_code_list_id`) REFERENCES `code_list` (`code_list_id`),
    ADD CONSTRAINT `code_list_replacement_code_list_id_fk` FOREIGN KEY (`replacement_code_list_id`) REFERENCES `code_list` (`code_list_id`),
    ADD CONSTRAINT `code_list_prev_code_list_id_fk` FOREIGN KEY (`prev_code_list_id`) REFERENCES `code_list` (`code_list_id`),
    ADD CONSTRAINT `code_list_next_code_list_id_fk` FOREIGN KEY (`next_code_list_id`) REFERENCES `code_list` (`code_list_id`);
ALTER TABLE `code_list_manifest` ADD CONSTRAINT `code_list_manifest_code_list_id_fk` FOREIGN KEY (`code_list_id`) REFERENCES `code_list` (`code_list_id`);
ALTER TABLE `code_list_value`
    ADD CONSTRAINT `code_list_value_code_list_id_fk` FOREIGN KEY (`code_list_id`) REFERENCES `code_list` (`code_list_id`),
    ADD CONSTRAINT `code_list_value_based_code_list_value_id_fk` FOREIGN KEY (`based_code_list_value_id`) REFERENCES `code_list_value` (`code_list_value_id`),
    ADD CONSTRAINT `code_list_value_replacement_code_list_value_id_fk` FOREIGN KEY (`replacement_code_list_value_id`) REFERENCES `code_list_value` (`code_list_value_id`),
    ADD CONSTRAINT `code_list_value_prev_code_list_value_id_fk` FOREIGN KEY (`prev_code_list_value_id`) REFERENCES `code_list_value` (`code_list_value_id`),
    ADD CONSTRAINT `code_list_value_next_code_list_value_id_fk` FOREIGN KEY (`next_code_list_value_id`) REFERENCES `code_list_value` (`code_list_value_id`);
ALTER TABLE `code_list_value_manifest` ADD CONSTRAINT `code_list_value_manifest_code_list_value_id_fk` FOREIGN KEY (`code_list_value_id`) REFERENCES `code_list_value` (`code_list_value_id`);
ALTER TABLE `ctx_scheme`
    ADD CONSTRAINT `ctx_scheme_code_list_id_fk` FOREIGN KEY (`code_list_id`) REFERENCES `code_list` (`code_list_id`);
ALTER TABLE `dt`
    ADD CONSTRAINT `dt_based_dt_id_fk` FOREIGN KEY (`based_dt_id`) REFERENCES `dt` (`dt_id`),
    ADD CONSTRAINT `dt_replacement_dt_id_fk` FOREIGN KEY (`replacement_dt_id`) REFERENCES `dt` (`dt_id`),
    ADD CONSTRAINT `dt_prev_dt_id_fk` FOREIGN KEY (`prev_dt_id`) REFERENCES `dt` (`dt_id`),
    ADD CONSTRAINT `dt_next_dt_id_fk` FOREIGN KEY (`next_dt_id`) REFERENCES `dt` (`dt_id`);
ALTER TABLE `dt_manifest` ADD CONSTRAINT `dt_manifest_dt_id_fk` FOREIGN KEY (`dt_id`) REFERENCES `dt` (`dt_id`);
ALTER TABLE `dt_sc`
    ADD CONSTRAINT `dt_sc_owner_dt_id_fk` FOREIGN KEY (`owner_dt_id`) REFERENCES `dt` (`dt_id`),
    ADD CONSTRAINT `dt_sc_based_dt_sc_id_fk` FOREIGN KEY (`based_dt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`),
    ADD CONSTRAINT `dt_sc_replacement_dt_sc_id_fk` FOREIGN KEY (`replacement_dt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`),
    ADD CONSTRAINT `dt_sc_prev_dt_sc_id_fk` FOREIGN KEY (`prev_dt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`),
    ADD CONSTRAINT `dt_sc_next_dt_sc_id_fk` FOREIGN KEY (`next_dt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`);
ALTER TABLE `dt_sc_manifest` ADD CONSTRAINT `dt_sc_manifest_dt_sc_id_fk` FOREIGN KEY (`dt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`);
ALTER TABLE `dt_usage_rule`
    ADD CONSTRAINT `dt_usage_rule_target_dt_id_fk` FOREIGN KEY (`target_dt_id`) REFERENCES `dt` (`dt_id`),
    ADD CONSTRAINT `dt_usage_rule_target_dt_sc_id_fk` FOREIGN KEY (`target_dt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`);

-- --------------------------
-- Change `xbt_id` TO UUID --
-- --------------------------
ALTER TABLE `xbt` ADD COLUMN `xbt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `xbt_id`;
CALL update_uuid('xbt');

ALTER TABLE `xbt` ADD COLUMN `subtype_of_xbt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the XBT table itself. It indicates a super type of this XSD built-in type.' AFTER `subtype_of_xbt_id`;

UPDATE `xbt`, `xbt` AS sub_xbt SET `xbt`.`subtype_of_xbt_uuid` = sub_xbt.`xbt_uuid`
WHERE `xbt`.`subtype_of_xbt_id` = sub_xbt.`xbt_id`;

ALTER TABLE `xbt_manifest` ADD COLUMN `xbt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the XBT table.' AFTER `xbt_id`;

UPDATE `xbt`, `xbt_manifest` SET `xbt_manifest`.`xbt_uuid` = `xbt`.`xbt_uuid`
WHERE `xbt`.`xbt_id` = `xbt_manifest`.`xbt_id`;

ALTER TABLE `cdt_awd_pri_xps_type_map` ADD COLUMN `xbt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the XBT table.' AFTER `xbt_id`;

UPDATE `xbt`, `cdt_awd_pri_xps_type_map` SET `cdt_awd_pri_xps_type_map`.`xbt_uuid` = `xbt`.`xbt_uuid`
WHERE `xbt`.`xbt_id` = `cdt_awd_pri_xps_type_map`.`xbt_id`;

ALTER TABLE `cdt_sc_awd_pri_xps_type_map` ADD COLUMN `xbt_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the XBT table.' AFTER `xbt_id`;

UPDATE `xbt`, `cdt_sc_awd_pri_xps_type_map` SET `cdt_sc_awd_pri_xps_type_map`.`xbt_uuid` = `xbt`.`xbt_uuid`
WHERE `xbt`.`xbt_id` = `cdt_sc_awd_pri_xps_type_map`.`xbt_id`;

-- Drop old `xbt_id` columns
ALTER TABLE `cdt_awd_pri_xps_type_map` DROP FOREIGN KEY `cdt_awd_pri_xps_type_map_xbt_id_fk`;
ALTER TABLE `cdt_awd_pri_xps_type_map` DROP COLUMN `xbt_id`;

ALTER TABLE `cdt_sc_awd_pri_xps_type_map` DROP FOREIGN KEY `cdt_sc_awd_pri_xps_type_map_xbt_id_fk`;
ALTER TABLE `cdt_sc_awd_pri_xps_type_map` DROP COLUMN `xbt_id`;

ALTER TABLE `xbt_manifest` DROP FOREIGN KEY `xbt_manifest_xbt_id_fk`;
ALTER TABLE `xbt_manifest` DROP COLUMN `xbt_id`;

ALTER TABLE `xbt` DROP FOREIGN KEY `xbt_subtype_of_xbt_id_fk`;
ALTER TABLE `xbt` DROP COLUMN `subtype_of_xbt_id`;

ALTER TABLE `xbt` DROP COLUMN `xbt_id`;

-- Rename `xbt_uuid` TO `xbt_id`
ALTER TABLE `xbt` CHANGE `xbt_uuid` `xbt_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `xbt` CHANGE `subtype_of_xbt_uuid` `subtype_of_xbt_id` char(36) CHARACTER SET ascii COMMENT 'Foreign key to the XBT table itself. It indicates a super type of this XSD built-in type.';
ALTER TABLE `xbt_manifest` CHANGE `xbt_uuid` `xbt_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the XBT table.';
ALTER TABLE `cdt_awd_pri_xps_type_map` CHANGE `xbt_uuid` `xbt_id` char(36) CHARACTER SET ascii COMMENT 'Foreign key to the XBT table.';
ALTER TABLE `cdt_sc_awd_pri_xps_type_map` CHANGE `xbt_uuid` `xbt_id` char(36) CHARACTER SET ascii COMMENT 'Foreign key to the XBT table.';

-- Add foreign key constraints
ALTER TABLE `xbt` ADD PRIMARY KEY (`xbt_id`);
ALTER TABLE `xbt` ADD CONSTRAINT `xbt_subtype_of_xbt_id_fk` FOREIGN KEY (`subtype_of_xbt_id`) REFERENCES `xbt` (`xbt_id`);
ALTER TABLE `xbt_manifest` ADD CONSTRAINT `xbt_manifest_xbt_id_fk` FOREIGN KEY (`xbt_id`) REFERENCES `xbt` (`xbt_id`);
ALTER TABLE `cdt_awd_pri_xps_type_map` ADD CONSTRAINT `cdt_awd_pri_xps_type_map_xbt_id_fk` FOREIGN KEY (`xbt_id`) REFERENCES `xbt` (`xbt_id`);
ALTER TABLE `cdt_sc_awd_pri_xps_type_map` ADD CONSTRAINT `cdt_sc_awd_pri_xps_type_map_xbt_id_fk` FOREIGN KEY (`xbt_id`) REFERENCES `xbt` (`xbt_id`);

-- ------------------------------
-- Change `cdt_pri_id` TO UUID --
-- ------------------------------
ALTER TABLE `cdt_pri` ADD COLUMN `cdt_pri_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `cdt_pri_id`;
CALL update_uuid('cdt_pri');

ALTER TABLE `cdt_awd_pri` ADD COLUMN `cdt_pri_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CDT_PRI table.' AFTER `cdt_pri_id`;

UPDATE `cdt_pri`, `cdt_awd_pri` SET `cdt_awd_pri`.`cdt_pri_uuid` = `cdt_pri`.`cdt_pri_uuid`
WHERE `cdt_pri`.`cdt_pri_id` = `cdt_awd_pri`.`cdt_pri_id`;

ALTER TABLE `cdt_sc_awd_pri` ADD COLUMN `cdt_pri_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CDT_PRI table.' AFTER `cdt_pri_id`;

UPDATE `cdt_pri`, `cdt_sc_awd_pri` SET `cdt_sc_awd_pri`.`cdt_pri_uuid` = `cdt_pri`.`cdt_pri_uuid`
WHERE `cdt_pri`.`cdt_pri_id` = `cdt_sc_awd_pri`.`cdt_pri_id`;

-- Drop old `cdt_pri_id` columns
ALTER TABLE `cdt_awd_pri` DROP FOREIGN KEY `cdt_awd_pri_cdt_pri_id_fk`;
ALTER TABLE `cdt_awd_pri` DROP COLUMN `cdt_pri_id`;

ALTER TABLE `cdt_sc_awd_pri` DROP FOREIGN KEY `cdt_sc_awd_pri_cdt_pri_id_fk`;
ALTER TABLE `cdt_sc_awd_pri` DROP COLUMN `cdt_pri_id`;

ALTER TABLE `cdt_pri` DROP COLUMN `cdt_pri_id`;

-- Rename `cdt_pri_uuid` TO `cdt_pri_id`
ALTER TABLE `cdt_pri` CHANGE `cdt_pri_uuid` `cdt_pri_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `cdt_awd_pri` CHANGE `cdt_pri_uuid` `cdt_pri_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the CDT_PRI table.';
ALTER TABLE `cdt_sc_awd_pri` CHANGE `cdt_pri_uuid` `cdt_pri_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the CDT_PRI table.';

-- Add foreign key constraints
ALTER TABLE `cdt_pri` ADD PRIMARY KEY (`cdt_pri_id`);
ALTER TABLE `cdt_awd_pri` ADD CONSTRAINT `cdt_awd_pri_cdt_pri_id_fk` FOREIGN KEY (`cdt_pri_id`) REFERENCES `cdt_pri` (`cdt_pri_id`);
ALTER TABLE `cdt_sc_awd_pri` ADD CONSTRAINT `cdt_sc_awd_pri_cdt_pri_id_fk` FOREIGN KEY (`cdt_pri_id`) REFERENCES `cdt_pri` (`cdt_pri_id`);

-- ------------------------------------------------------------------------------------
-- Change `cdt_awd_pri_id`, `cdt_awd_pri_xps_type_map_id`,
-- `cdt_sc_awd_pri_id`, AND `cdt_sc_awd_pri_xps_type_map_id` TO UUID --
-- ------------------------------------------------------------------------------------
ALTER TABLE `cdt_awd_pri` ADD COLUMN `cdt_awd_pri_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `cdt_awd_pri_id`;
CALL update_uuid('cdt_awd_pri');

ALTER TABLE `cdt_sc_awd_pri` ADD COLUMN `cdt_sc_awd_pri_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `cdt_sc_awd_pri_id`;
CALL update_uuid('cdt_sc_awd_pri');

ALTER TABLE `cdt_awd_pri_xps_type_map` ADD COLUMN `cdt_awd_pri_xps_type_map_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `cdt_awd_pri_xps_type_map_id`;
CALL update_uuid('cdt_awd_pri_xps_type_map');

ALTER TABLE `cdt_sc_awd_pri_xps_type_map` ADD COLUMN `cdt_sc_awd_pri_xps_type_map_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `cdt_sc_awd_pri_xps_type_map_id`;
CALL update_uuid('cdt_sc_awd_pri_xps_type_map');

ALTER TABLE `cdt_awd_pri_xps_type_map` ADD COLUMN `cdt_awd_pri_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CDT_AWD_PRI table.' AFTER `cdt_awd_pri_id`;

UPDATE `cdt_awd_pri`, `cdt_awd_pri_xps_type_map` SET `cdt_awd_pri_xps_type_map`.`cdt_awd_pri_uuid` = `cdt_awd_pri`.`cdt_awd_pri_uuid`
WHERE `cdt_awd_pri`.`cdt_awd_pri_id` = `cdt_awd_pri_xps_type_map`.`cdt_awd_pri_id`;

ALTER TABLE `cdt_sc_awd_pri_xps_type_map` ADD COLUMN `cdt_sc_awd_pri_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CDT_SC_AWD_PRI table.' AFTER `cdt_sc_awd_pri_id`;

UPDATE `cdt_sc_awd_pri`, `cdt_sc_awd_pri_xps_type_map` SET `cdt_sc_awd_pri_xps_type_map`.`cdt_sc_awd_pri_uuid` = `cdt_sc_awd_pri`.`cdt_sc_awd_pri_uuid`
WHERE `cdt_sc_awd_pri`.`cdt_sc_awd_pri_id` = `cdt_sc_awd_pri_xps_type_map`.`cdt_sc_awd_pri_id`;

ALTER TABLE `bdt_pri_restri` ADD COLUMN `cdt_awd_pri_xps_type_map_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CDT_AWD_PRI_XPS_TYPE_MAP table.' AFTER `cdt_awd_pri_xps_type_map_id`;

UPDATE `bdt_pri_restri`, `cdt_awd_pri_xps_type_map` SET `bdt_pri_restri`.`cdt_awd_pri_xps_type_map_uuid` = `cdt_awd_pri_xps_type_map`.`cdt_awd_pri_xps_type_map_uuid`
WHERE `bdt_pri_restri`.`cdt_awd_pri_xps_type_map_id` = `cdt_awd_pri_xps_type_map`.`cdt_awd_pri_xps_type_map_id`;

ALTER TABLE `bdt_sc_pri_restri` ADD COLUMN `cdt_sc_awd_pri_xps_type_map_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the CDT_SC_AWD_PRI_XPS_TYPE_MAP table.' AFTER `cdt_sc_awd_pri_xps_type_map_id`;

UPDATE `bdt_sc_pri_restri`, `cdt_sc_awd_pri_xps_type_map` SET `bdt_sc_pri_restri`.`cdt_sc_awd_pri_xps_type_map_uuid` = `cdt_sc_awd_pri_xps_type_map`.`cdt_sc_awd_pri_xps_type_map_uuid`
WHERE `bdt_sc_pri_restri`.`cdt_sc_awd_pri_xps_type_map_id` = `cdt_sc_awd_pri_xps_type_map`.`cdt_sc_awd_pri_xps_type_map_id`;

-- Drop old `cdt_awd_pri_id`, `cdt_awd_pri_xps_type_map_id`,
-- `cdt_sc_awd_pri_id`, AND `cdt_sc_awd_pri_xps_type_map_id` columns
ALTER TABLE `bdt_pri_restri` DROP FOREIGN KEY `bdt_pri_restri_cdt_awd_pri_xps_type_map_id_fk`;
ALTER TABLE `bdt_pri_restri` DROP COLUMN `cdt_awd_pri_xps_type_map_id`;

ALTER TABLE `bdt_sc_pri_restri` DROP FOREIGN KEY `bdt_sc_pri_restri_cdt_sc_awd_pri_xps_type_map_id_fk`;
ALTER TABLE `bdt_sc_pri_restri` DROP COLUMN `cdt_sc_awd_pri_xps_type_map_id`;

ALTER TABLE `cdt_awd_pri_xps_type_map` DROP FOREIGN KEY `cdt_awd_pri_xps_type_map_cdt_awd_pri_id_fk`;
ALTER TABLE `cdt_awd_pri_xps_type_map` DROP COLUMN `cdt_awd_pri_id`;

ALTER TABLE `cdt_sc_awd_pri_xps_type_map` DROP FOREIGN KEY `cdt_sc_awd_pri_xps_type_map_cdt_sc_awd_pri_id_fk`;
ALTER TABLE `cdt_sc_awd_pri_xps_type_map` DROP COLUMN `cdt_sc_awd_pri_id`;

ALTER TABLE `cdt_awd_pri` DROP COLUMN `cdt_awd_pri_id`;
ALTER TABLE `cdt_sc_awd_pri` DROP COLUMN `cdt_sc_awd_pri_id`;

ALTER TABLE `cdt_awd_pri_xps_type_map` DROP COLUMN `cdt_awd_pri_xps_type_map_id`;
ALTER TABLE `cdt_sc_awd_pri_xps_type_map` DROP COLUMN `cdt_sc_awd_pri_xps_type_map_id`;

-- Rename `cdt_awd_pri_uuid` TO `cdt_awd_pri_id`
ALTER TABLE `cdt_awd_pri` CHANGE `cdt_awd_pri_uuid` `cdt_awd_pri_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `cdt_awd_pri_xps_type_map` CHANGE `cdt_awd_pri_uuid` `cdt_awd_pri_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the CDT_AWD_PRI table.';

-- Rename `cdt_sc_awd_pri_uuid` TO `cdt_sc_awd_pri_id`
ALTER TABLE `cdt_sc_awd_pri` CHANGE `cdt_sc_awd_pri_uuid` `cdt_sc_awd_pri_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `cdt_sc_awd_pri_xps_type_map` CHANGE `cdt_sc_awd_pri_uuid` `cdt_sc_awd_pri_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Foreign key to the CDT_SC_AWD_PRI table.';

-- Rename `cdt_awd_pri_xps_type_map_uuid` TO `cdt_awd_pri_xps_type_map_id`
ALTER TABLE `cdt_awd_pri_xps_type_map` CHANGE `cdt_awd_pri_xps_type_map_uuid` `cdt_awd_pri_xps_type_map_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bdt_pri_restri` CHANGE `cdt_awd_pri_xps_type_map_uuid` `cdt_awd_pri_xps_type_map_id` char(36) CHARACTER SET ascii COMMENT 'Foreign key to the CDT_AWD_PRI_XPS_TYPE_MAP table.';

-- Rename `cdt_sc_awd_pri_xps_type_map_uuid` TO `cdt_sc_awd_pri_xps_type_map_id`
ALTER TABLE `cdt_sc_awd_pri_xps_type_map` CHANGE `cdt_sc_awd_pri_xps_type_map_uuid` `cdt_sc_awd_pri_xps_type_map_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bdt_sc_pri_restri` CHANGE `cdt_sc_awd_pri_xps_type_map_uuid` `cdt_sc_awd_pri_xps_type_map_id` char(36) CHARACTER SET ascii COMMENT 'Foreign key to the CDT_SC_AWD_PRI_XPS_TYPE_MAP table.';

-- Add foreign key constraints
ALTER TABLE `cdt_awd_pri` ADD PRIMARY KEY (`cdt_awd_pri_id`);
ALTER TABLE `cdt_awd_pri_xps_type_map` ADD CONSTRAINT `cdt_awd_pri_xps_type_map_cdt_awd_pri_id_fk` FOREIGN KEY (`cdt_awd_pri_id`) REFERENCES `cdt_awd_pri` (`cdt_awd_pri_id`);
ALTER TABLE `cdt_sc_awd_pri` ADD PRIMARY KEY (`cdt_sc_awd_pri_id`);
ALTER TABLE `cdt_sc_awd_pri_xps_type_map` ADD CONSTRAINT `cdt_sc_awd_pri_xps_type_map_cdt_sc_awd_pri_id_fk` FOREIGN KEY (`cdt_sc_awd_pri_id`) REFERENCES `cdt_sc_awd_pri` (`cdt_sc_awd_pri_id`);

ALTER TABLE `cdt_awd_pri_xps_type_map` ADD PRIMARY KEY (`cdt_awd_pri_xps_type_map_id`);
ALTER TABLE `bdt_pri_restri` ADD CONSTRAINT `bdt_pri_restri_cdt_awd_pri_xps_type_map_id_fk` FOREIGN KEY (`cdt_awd_pri_xps_type_map_id`) REFERENCES `cdt_awd_pri_xps_type_map` (`cdt_awd_pri_xps_type_map_id`);
ALTER TABLE `cdt_sc_awd_pri_xps_type_map` ADD PRIMARY KEY (`cdt_sc_awd_pri_xps_type_map_id`);
ALTER TABLE `bdt_sc_pri_restri` ADD CONSTRAINT `bdt_sc_pri_restri_cdt_sc_awd_pri_xps_type_map_id_fk` FOREIGN KEY (`cdt_sc_awd_pri_xps_type_map_id`) REFERENCES `cdt_sc_awd_pri_xps_type_map` (`cdt_sc_awd_pri_xps_type_map_id`);

-- Add xbt primitives
INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('0d6f0c93-0222-47aa-952c-1c10c9e69883', '3395a2f88a397a2e85345fee3ec1746e', 'non positive integer', 'xsd:nonPositiveInteger', '{\"type\":\"number\", \"multipleOf\":1, \"maximum\":0, \"exclusiveMaximum\":false}', '{\"type\":\"integer\", \"maximum\":0, \"exclusiveMaximum\":false}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'non positive integer' AND subtype.`name` = 'integer';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('1dce4776-e435-41cc-9941-372c6ab0754c', 'f9044b20770e252037c894464e8e18df', 'negative integer', 'xsd:negativeInteger', '{\"type\":\"number\", \"multipleOf\":1, \"maximum\":0, \"exclusiveMaximum\":true}', '{\"type\":\"integer\", \"maximum\":0, \"exclusiveMaximum\":true}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'negative integer' AND subtype.`name` = 'non positive integer';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('010e4563-02d8-4715-acb6-5d7227988ca7', 'd484e5046be205ffe7ef0a84651ab595', 'long', 'xsd:long', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":-9223372036854775808, \"maximum\":9223372036854775807}', '{\"type\":\"integer\", \"minimum\":-9223372036854775808, \"maximum\":9223372036854775807}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'long' AND subtype.`name` = 'integer';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('c22e47f7-66db-433a-a1b4-9b1a151983b3', '8278dee5e8106cc9e2e02b4f6ee4c85f', 'int', 'xsd:int', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":-2147483648, \"maximum\":2147483647}', '{\"type\":\"integer\", \"minimum\":-2147483648, \"maximum\":2147483647}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'int' AND subtype.`name` = 'long';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('9895d011-6625-41f8-a01c-29692ae323e9', 'c776d8ccc5050987b0af9873a2ef95ab', 'short', 'xsd:short', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":-32768, \"maximum\":32767}', '{\"type\":\"integer\", \"minimum\":-32768, \"maximum\":32767}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'short' AND subtype.`name` = 'int';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', '9aa5f0beeb9d08109e0f6834790ab9a4', 'byte', 'xsd:byte', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":-128, \"maximum\":127}', '{\"type\":\"integer\", \"minimum\":-128, \"maximum\":127}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'byte' AND subtype.`name` = 'short';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('5dcfb77e-2869-4f77-9462-250ce7b7c492', 'a54e126ffd199e8df4bb44bc903926e8', 'unsigned long', 'xsd:unsignedLong', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":0, \"maximum\":18446744073709551615}', '{\"type\":\"integer\", \"minimum\":0, \"maximum\":18446744073709551615}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'unsigned long' AND subtype.`name` = 'non negative integer';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('65ead9cc-35eb-47fc-9006-078df7cfa32a', '12c448fbbe86ca3b7e93a7df771e0b94', 'unsigned int', 'xsd:unsignedInt', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":0, \"maximum\":4294967295}', '{\"type\":\"integer\", \"minimum\":0, \"maximum\":4294967295}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'unsigned int' AND subtype.`name` = 'unsigned long';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('2877cf6b-357b-4e0a-95f5-1e91413638d2', 'a422d1c18d382571e92ab3b0f85c37f7', 'unsigned short', 'xsd:unsignedShort', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":0, \"maximum\":65535}', '{\"type\":\"integer\", \"minimum\":0, \"maximum\":65535}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'unsigned short' AND subtype.`name` = 'unsigned int';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('8c093c3b-1216-401d-bda9-bdb450e02bad', '8451efe9c107d51a33ea623668afaa4a', 'unsigned byte', 'xsd:unsignedByte', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":0, \"maximum\":255}', '{\"type\":\"integer\", \"minimum\":0, \"maximum\":255}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'unsigned byte' AND subtype.`name` = 'unsigned short';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('7fd4451b-6ad1-4930-8570-34c67cb66081', '4ee4e35beded482a401d14879671cf2b', 'name', 'xsd:Name', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'name' AND subtype.`name` = 'token';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('d5af9325-d1c8-4bd0-8583-8ac06b4956d2', '954467cfde776fc6d45ed393de47ecff', 'non-colonized name', 'xsd:NCName', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'non-colonized name' AND subtype.`name` = 'name';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('98a97e1c-5d7c-429d-851e-2e0516015b7b', 'aafa09d5c14648f3fbd90808944d9f75', 'name token', 'xsd:NMTOKEN', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'name token' AND subtype.`name` = 'token';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('de57e550-fcaf-481d-b8a3-6b301ee48988', 'd99546b1bc2acfd6e87c1ea1487f5a59', 'name tokens', 'xsd:NMTOKENS', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'name tokens' AND subtype.`name` = 'name token';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', '864c5d84696bdd17f7b36f61b2d42a0e', 'identifier', 'xsd:ID', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'identifier' AND subtype.`name` = 'non-colonized name';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('fddb814a-96b9-4392-bd07-2af08233e5f1', 'b20c9e033be4afc6ef1b11659afc4103', 'identifier reference', 'xsd:IDREF', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'identifier reference' AND subtype.`name` = 'non-colonized name';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('a08258f8-0ee9-44a4-b937-323ef1bb907d', '6112fe6c7fe8978d26dfc39f45f3c04b', 'identifier references', 'xsd:IDREFS', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'identifier references' AND subtype.`name` = 'identifier reference';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 'ec21905187690f7af17686e7214013d2', 'entity', 'xsd:ENTITY', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'entity' AND subtype.`name` = 'non-colonized name';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('06f9b3ea-7699-4355-a02d-4826c47a78a9', '31cbacbd62c00094596914a66bd2b175', 'entities', 'xsd:ENTITIES', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'entities' AND subtype.`name` = 'entity';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('1cdcbd74-1896-4f8c-b2ab-c68e47976e18', '13e5d1379633033e1d1f8671c62ffb61', 'qualified name', 'xsd:QName', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'qualified name' AND subtype.`name` = 'any simple type';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('18bc4b23-48da-426c-9986-2b4a7bc38f39', '169bb1ded95c8edc188262c21affb79c', 'notation', 'xsd:NOTATION', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'notation' AND subtype.`name` = 'any simple type';

INSERT INTO `log` (`log_id`, `hash`, `revision_num`, `revision_tracking_num`, `log_action`, `reference`, `snapshot`, `prev_log_id`, `next_log_id`, `created_by`, `creation_timestamp`)
VALUES
    (27001, 'c2ddce6fcb8bbc558a24bd31a04bac4b', 1, 1, 'Added', 'd484e5046be205ffe7ef0a84651ab595', '{\"guid\": \"d484e5046be205ffe7ef0a84651ab595\", \"name\": \"long\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"d484e5046be205ffe7ef0a84651ab595\", \"name\": \"long\", \"state\": 3, \"xbt_id\": \"010e4563-02d8-4715-acb6-5d7227988ca7\", \"created_by\": 1, \"builtIn_type\": \"xsd:long\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-9223372036854775808, \\\"maximum\\\":9223372036854775807}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-9223372036854775808, \\\"maximum\\\":9223372036854775807}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"2cfd15d4-3678-4e65-be38-92629ed0ae45\", \"creation_timestamp\": \"2022-08-12T20:00:51.276113\", \"last_update_timestamp\": \"2022-08-12T20:00:51.276113\"}, \"xbtManifest\": {\"xbt_id\": \"010e4563-02d8-4715-acb6-5d7227988ca7\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3156}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:long\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-9223372036854775808, \\\"maximum\\\":9223372036854775807}\", \"subTypeOfXbt\": {\"guid\": \"f3bc6f3ca44b47e9a9a66997fc5d3c2b\", \"name\": \"integer\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-9223372036854775808, \\\"maximum\\\":9223372036854775807}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.276113'),
    (27002, 'e9ed5dcee2a7cb3c3cb86460ff6ede7e', 1, 1, 'Added', '31cbacbd62c00094596914a66bd2b175', '{\"guid\": \"31cbacbd62c00094596914a66bd2b175\", \"name\": \"entities\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"31cbacbd62c00094596914a66bd2b175\", \"name\": \"entities\", \"state\": 3, \"xbt_id\": \"06f9b3ea-7699-4355-a02d-4826c47a78a9\", \"created_by\": 1, \"builtIn_type\": \"xsd:ENTITIES\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"126c78b2-0fb7-4dc4-9cc0-881aa205f7f3\", \"creation_timestamp\": \"2022-08-12T20:00:51.331423\", \"last_update_timestamp\": \"2022-08-12T20:00:51.331423\"}, \"xbtManifest\": {\"xbt_id\": \"06f9b3ea-7699-4355-a02d-4826c47a78a9\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3167}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:ENTITIES\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"ec21905187690f7af17686e7214013d2\", \"name\": \"entity\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.331423'),
    (27003, '13460d8ef61ae7a07a052dcfe7b40980', 1, 1, 'Added', '3395a2f88a397a2e85345fee3ec1746e', '{\"guid\": \"3395a2f88a397a2e85345fee3ec1746e\", \"name\": \"non positive integer\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"3395a2f88a397a2e85345fee3ec1746e\", \"name\": \"non positive integer\", \"state\": 3, \"xbt_id\": \"0d6f0c93-0222-47aa-952c-1c10c9e69883\", \"created_by\": 1, \"builtIn_type\": \"xsd:nonPositiveInteger\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":false}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":false}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"2cfd15d4-3678-4e65-be38-92629ed0ae45\", \"creation_timestamp\": \"2022-08-12T20:00:51.268162\", \"last_update_timestamp\": \"2022-08-12T20:00:51.268162\"}, \"xbtManifest\": {\"xbt_id\": \"0d6f0c93-0222-47aa-952c-1c10c9e69883\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3178}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:nonPositiveInteger\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":false}\", \"subTypeOfXbt\": {\"guid\": \"f3bc6f3ca44b47e9a9a66997fc5d3c2b\", \"name\": \"integer\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":false}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.268162'),
    (27004, '78915018bd18a7f21a9a3d78985337c4', 1, 1, 'Added', 'ec21905187690f7af17686e7214013d2', '{\"guid\": \"ec21905187690f7af17686e7214013d2\", \"name\": \"entity\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"ec21905187690f7af17686e7214013d2\", \"name\": \"entity\", \"state\": 3, \"xbt_id\": \"126c78b2-0fb7-4dc4-9cc0-881aa205f7f3\", \"created_by\": 1, \"builtIn_type\": \"xsd:ENTITY\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"d5af9325-d1c8-4bd0-8583-8ac06b4956d2\", \"creation_timestamp\": \"2022-08-12T20:00:51.328373\", \"last_update_timestamp\": \"2022-08-12T20:00:51.328373\"}, \"xbtManifest\": {\"xbt_id\": \"126c78b2-0fb7-4dc4-9cc0-881aa205f7f3\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3189}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:ENTITY\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"954467cfde776fc6d45ed393de47ecff\", \"name\": \"non-colonized name\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.328373'),
    (27005, '96d30429ae122a86d9aa1c57beed80d2', 1, 1, 'Added', 'f9044b20770e252037c894464e8e18df', '{\"guid\": \"f9044b20770e252037c894464e8e18df\", \"name\": \"negative integer\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"f9044b20770e252037c894464e8e18df\", \"name\": \"negative integer\", \"state\": 3, \"xbt_id\": \"1dce4776-e435-41cc-9941-372c6ab0754c\", \"created_by\": 1, \"builtIn_type\": \"xsd:negativeInteger\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":true}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":true}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"0d6f0c93-0222-47aa-952c-1c10c9e69883\", \"creation_timestamp\": \"2022-08-12T20:00:51.272934\", \"last_update_timestamp\": \"2022-08-12T20:00:51.272934\"}, \"xbtManifest\": {\"xbt_id\": \"1dce4776-e435-41cc-9941-372c6ab0754c\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3200}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:negativeInteger\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":true}\", \"subTypeOfXbt\": {\"guid\": \"3395a2f88a397a2e85345fee3ec1746e\", \"name\": \"non positive integer\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":true}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.272934'),
    (27006, '0434b935ee0c9017efb00012d3a1404a', 1, 1, 'Added', 'a422d1c18d382571e92ab3b0f85c37f7', '{\"guid\": \"a422d1c18d382571e92ab3b0f85c37f7\", \"name\": \"unsigned short\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"a422d1c18d382571e92ab3b0f85c37f7\", \"name\": \"unsigned short\", \"state\": 3, \"xbt_id\": \"2877cf6b-357b-4e0a-95f5-1e91413638d2\", \"created_by\": 1, \"builtIn_type\": \"xsd:unsignedShort\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":65535}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":65535}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"65ead9cc-35eb-47fc-9006-078df7cfa32a\", \"creation_timestamp\": \"2022-08-12T20:00:51.298955\", \"last_update_timestamp\": \"2022-08-12T20:00:51.298955\"}, \"xbtManifest\": {\"xbt_id\": \"2877cf6b-357b-4e0a-95f5-1e91413638d2\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3211}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:unsignedShort\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":65535}\", \"subTypeOfXbt\": {\"guid\": \"12c448fbbe86ca3b7e93a7df771e0b94\", \"name\": \"unsigned int\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":65535}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.298955'),
    (27007, 'eade834ba5d0469dbad2261eaa29be8b', 1, 1, 'Added', 'a54e126ffd199e8df4bb44bc903926e8', '{\"guid\": \"a54e126ffd199e8df4bb44bc903926e8\", \"name\": \"unsigned long\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"a54e126ffd199e8df4bb44bc903926e8\", \"name\": \"unsigned long\", \"state\": 3, \"xbt_id\": \"5dcfb77e-2869-4f77-9462-250ce7b7c492\", \"created_by\": 1, \"builtIn_type\": \"xsd:unsignedLong\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":18446744073709551615}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":18446744073709551615}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"1df3a62d-53e1-46a0-b31c-bd0d6c50ff7c\", \"creation_timestamp\": \"2022-08-12T20:00:51.290947\", \"last_update_timestamp\": \"2022-08-12T20:00:51.290947\"}, \"xbtManifest\": {\"xbt_id\": \"5dcfb77e-2869-4f77-9462-250ce7b7c492\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3222}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:unsignedLong\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":18446744073709551615}\", \"subTypeOfXbt\": {\"guid\": \"da82a62e6bfd4db88710442a67356ff9\", \"name\": \"non negative integer\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":18446744073709551615}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.290947'),
    (27008, '5c54836a2785f25be328eafa08507423', 1, 1, 'Added', '12c448fbbe86ca3b7e93a7df771e0b94', '{\"guid\": \"12c448fbbe86ca3b7e93a7df771e0b94\", \"name\": \"unsigned int\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"12c448fbbe86ca3b7e93a7df771e0b94\", \"name\": \"unsigned int\", \"state\": 3, \"xbt_id\": \"65ead9cc-35eb-47fc-9006-078df7cfa32a\", \"created_by\": 1, \"builtIn_type\": \"xsd:unsignedInt\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":4294967295}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":4294967295}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"5dcfb77e-2869-4f77-9462-250ce7b7c492\", \"creation_timestamp\": \"2022-08-12T20:00:51.294112\", \"last_update_timestamp\": \"2022-08-12T20:00:51.294112\"}, \"xbtManifest\": {\"xbt_id\": \"65ead9cc-35eb-47fc-9006-078df7cfa32a\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3233}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:unsignedInt\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":4294967295}\", \"subTypeOfXbt\": {\"guid\": \"a54e126ffd199e8df4bb44bc903926e8\", \"name\": \"unsigned long\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":4294967295}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.294112'),
    (27009, '7de6bddd104817bae8ec945f823f627c', 1, 1, 'Added', '4ee4e35beded482a401d14879671cf2b', '{\"guid\": \"4ee4e35beded482a401d14879671cf2b\", \"name\": \"name\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"4ee4e35beded482a401d14879671cf2b\", \"name\": \"name\", \"state\": 3, \"xbt_id\": \"7fd4451b-6ad1-4930-8570-34c67cb66081\", \"created_by\": 1, \"builtIn_type\": \"xsd:Name\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"f4fb8c41-6374-4f7e-a340-7ba1b871e778\", \"creation_timestamp\": \"2022-08-12T20:00:51.30583\", \"last_update_timestamp\": \"2022-08-12T20:00:51.30583\"}, \"xbtManifest\": {\"xbt_id\": \"7fd4451b-6ad1-4930-8570-34c67cb66081\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3244}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:Name\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"0963dd2d22084b4893ff69ff303e57d9\", \"name\": \"token\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.305830'),
    (27010, 'f65bd7e40f0f37b4869f1c721abb126a', 1, 1, 'Added', '8451efe9c107d51a33ea623668afaa4a', '{\"guid\": \"8451efe9c107d51a33ea623668afaa4a\", \"name\": \"unsigned byte\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"8451efe9c107d51a33ea623668afaa4a\", \"name\": \"unsigned byte\", \"state\": 3, \"xbt_id\": \"8c093c3b-1216-401d-bda9-bdb450e02bad\", \"created_by\": 1, \"builtIn_type\": \"xsd:unsignedByte\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":255}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":255}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"2877cf6b-357b-4e0a-95f5-1e91413638d2\", \"creation_timestamp\": \"2022-08-12T20:00:51.302249\", \"last_update_timestamp\": \"2022-08-12T20:00:51.302249\"}, \"xbtManifest\": {\"xbt_id\": \"8c093c3b-1216-401d-bda9-bdb450e02bad\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3255}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:unsignedByte\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":255}\", \"subTypeOfXbt\": {\"guid\": \"a422d1c18d382571e92ab3b0f85c37f7\", \"name\": \"unsigned short\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":255}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.302249'),
    (27011, '22520c48f6f324a509147f90903809c5', 1, 1, 'Added', 'c776d8ccc5050987b0af9873a2ef95ab', '{\"guid\": \"c776d8ccc5050987b0af9873a2ef95ab\", \"name\": \"short\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"c776d8ccc5050987b0af9873a2ef95ab\", \"name\": \"short\", \"state\": 3, \"xbt_id\": \"9895d011-6625-41f8-a01c-29692ae323e9\", \"created_by\": 1, \"builtIn_type\": \"xsd:short\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-32768, \\\"maximum\\\":32767}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-32768, \\\"maximum\\\":32767}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"c22e47f7-66db-433a-a1b4-9b1a151983b3\", \"creation_timestamp\": \"2022-08-12T20:00:51.282907\", \"last_update_timestamp\": \"2022-08-12T20:00:51.282907\"}, \"xbtManifest\": {\"xbt_id\": \"9895d011-6625-41f8-a01c-29692ae323e9\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3266}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:short\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-32768, \\\"maximum\\\":32767}\", \"subTypeOfXbt\": {\"guid\": \"8278dee5e8106cc9e2e02b4f6ee4c85f\", \"name\": \"int\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-32768, \\\"maximum\\\":32767}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.282907'),
    (27012, 'e6501e1d8d2eeb6f8f9f707bf635a123', 1, 1, 'Added', 'aafa09d5c14648f3fbd90808944d9f75', '{\"guid\": \"aafa09d5c14648f3fbd90808944d9f75\", \"name\": \"name token\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"aafa09d5c14648f3fbd90808944d9f75\", \"name\": \"name token\", \"state\": 3, \"xbt_id\": \"98a97e1c-5d7c-429d-851e-2e0516015b7b\", \"created_by\": 1, \"builtIn_type\": \"xsd:NMTOKEN\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"f4fb8c41-6374-4f7e-a340-7ba1b871e778\", \"creation_timestamp\": \"2022-08-12T20:00:51.311743\", \"last_update_timestamp\": \"2022-08-12T20:00:51.311743\"}, \"xbtManifest\": {\"xbt_id\": \"98a97e1c-5d7c-429d-851e-2e0516015b7b\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3277}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:NMTOKEN\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"0963dd2d22084b4893ff69ff303e57d9\", \"name\": \"token\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.311743'),
    (27013, 'ab0c78811f66b17f8b50b7a7068ceb39', 1, 1, 'Added', '6112fe6c7fe8978d26dfc39f45f3c04b', '{\"guid\": \"6112fe6c7fe8978d26dfc39f45f3c04b\", \"name\": \"identifier references\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"6112fe6c7fe8978d26dfc39f45f3c04b\", \"name\": \"identifier references\", \"state\": 3, \"xbt_id\": \"a08258f8-0ee9-44a4-b937-323ef1bb907d\", \"created_by\": 1, \"builtIn_type\": \"xsd:IDREFS\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"fddb814a-96b9-4392-bd07-2af08233e5f1\", \"creation_timestamp\": \"2022-08-12T20:00:51.325116\", \"last_update_timestamp\": \"2022-08-12T20:00:51.325116\"}, \"xbtManifest\": {\"xbt_id\": \"a08258f8-0ee9-44a4-b937-323ef1bb907d\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3288}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:IDREFS\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"b20c9e033be4afc6ef1b11659afc4103\", \"name\": \"identifier reference\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.325116'),
    (27014, '490331d583e0b9ae5b39def271f7fef3', 1, 1, 'Added', '864c5d84696bdd17f7b36f61b2d42a0e', '{\"guid\": \"864c5d84696bdd17f7b36f61b2d42a0e\", \"name\": \"identifier\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"864c5d84696bdd17f7b36f61b2d42a0e\", \"name\": \"identifier\", \"state\": 3, \"xbt_id\": \"a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b\", \"created_by\": 1, \"builtIn_type\": \"xsd:ID\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"d5af9325-d1c8-4bd0-8583-8ac06b4956d2\", \"creation_timestamp\": \"2022-08-12T20:00:51.319093\", \"last_update_timestamp\": \"2022-08-12T20:00:51.319093\"}, \"xbtManifest\": {\"xbt_id\": \"a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3299}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:ID\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"954467cfde776fc6d45ed393de47ecff\", \"name\": \"non-colonized name\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.319093'),
    (27015, '9d856cd06d4dfbeb5995a51df92a0f1b', 1, 1, 'Added', '8278dee5e8106cc9e2e02b4f6ee4c85f', '{\"guid\": \"8278dee5e8106cc9e2e02b4f6ee4c85f\", \"name\": \"int\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"8278dee5e8106cc9e2e02b4f6ee4c85f\", \"name\": \"int\", \"state\": 3, \"xbt_id\": \"c22e47f7-66db-433a-a1b4-9b1a151983b3\", \"created_by\": 1, \"builtIn_type\": \"xsd:int\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-2147483648, \\\"maximum\\\":2147483647}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-2147483648, \\\"maximum\\\":2147483647}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"010e4563-02d8-4715-acb6-5d7227988ca7\", \"creation_timestamp\": \"2022-08-12T20:00:51.279799\", \"last_update_timestamp\": \"2022-08-12T20:00:51.279799\"}, \"xbtManifest\": {\"xbt_id\": \"c22e47f7-66db-433a-a1b4-9b1a151983b3\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3310}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:int\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-2147483648, \\\"maximum\\\":2147483647}\", \"subTypeOfXbt\": {\"guid\": \"d484e5046be205ffe7ef0a84651ab595\", \"name\": \"long\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-2147483648, \\\"maximum\\\":2147483647}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.279799'),
    (27016, '32166e9cf169c36d648443cbc046a486', 1, 1, 'Added', '954467cfde776fc6d45ed393de47ecff', '{\"guid\": \"954467cfde776fc6d45ed393de47ecff\", \"name\": \"non-colonized name\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"954467cfde776fc6d45ed393de47ecff\", \"name\": \"non-colonized name\", \"state\": 3, \"xbt_id\": \"d5af9325-d1c8-4bd0-8583-8ac06b4956d2\", \"created_by\": 1, \"builtIn_type\": \"xsd:NCName\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"7fd4451b-6ad1-4930-8570-34c67cb66081\", \"creation_timestamp\": \"2022-08-12T20:00:51.309084\", \"last_update_timestamp\": \"2022-08-12T20:00:51.309084\"}, \"xbtManifest\": {\"xbt_id\": \"d5af9325-d1c8-4bd0-8583-8ac06b4956d2\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3321}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:NCName\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"4ee4e35beded482a401d14879671cf2b\", \"name\": \"name\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.309084'),
    (27017, '740a949857d90afbfc48384e77ab129b', 1, 1, 'Added', 'd99546b1bc2acfd6e87c1ea1487f5a59', '{\"guid\": \"d99546b1bc2acfd6e87c1ea1487f5a59\", \"name\": \"name tokens\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"d99546b1bc2acfd6e87c1ea1487f5a59\", \"name\": \"name tokens\", \"state\": 3, \"xbt_id\": \"de57e550-fcaf-481d-b8a3-6b301ee48988\", \"created_by\": 1, \"builtIn_type\": \"xsd:NMTOKENS\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"98a97e1c-5d7c-429d-851e-2e0516015b7b\", \"creation_timestamp\": \"2022-08-12T20:00:51.315503\", \"last_update_timestamp\": \"2022-08-12T20:00:51.315503\"}, \"xbtManifest\": {\"xbt_id\": \"de57e550-fcaf-481d-b8a3-6b301ee48988\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3332}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:NMTOKENS\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"aafa09d5c14648f3fbd90808944d9f75\", \"name\": \"name token\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.315503'),
    (27018, 'a090a4401b6131a332b36983af0ba019', 1, 1, 'Added', '9aa5f0beeb9d08109e0f6834790ab9a4', '{\"guid\": \"9aa5f0beeb9d08109e0f6834790ab9a4\", \"name\": \"byte\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"9aa5f0beeb9d08109e0f6834790ab9a4\", \"name\": \"byte\", \"state\": 3, \"xbt_id\": \"eba1be95-9fc1-4cd8-88c9-75e3c61fcddb\", \"created_by\": 1, \"builtIn_type\": \"xsd:byte\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-128, \\\"maximum\\\":127}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-128, \\\"maximum\\\":127}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"9895d011-6625-41f8-a01c-29692ae323e9\", \"creation_timestamp\": \"2022-08-12T20:00:51.286613\", \"last_update_timestamp\": \"2022-08-12T20:00:51.286613\"}, \"xbtManifest\": {\"xbt_id\": \"eba1be95-9fc1-4cd8-88c9-75e3c61fcddb\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3343}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:byte\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-128, \\\"maximum\\\":127}\", \"subTypeOfXbt\": {\"guid\": \"c776d8ccc5050987b0af9873a2ef95ab\", \"name\": \"short\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-128, \\\"maximum\\\":127}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.286613'),
    (27019, '6dbba707cffde0d9ea0be31bed0739f5', 1, 1, 'Added', 'b20c9e033be4afc6ef1b11659afc4103', '{\"guid\": \"b20c9e033be4afc6ef1b11659afc4103\", \"name\": \"identifier reference\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"b20c9e033be4afc6ef1b11659afc4103\", \"name\": \"identifier reference\", \"state\": 3, \"xbt_id\": \"fddb814a-96b9-4392-bd07-2af08233e5f1\", \"created_by\": 1, \"builtIn_type\": \"xsd:IDREF\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"d5af9325-d1c8-4bd0-8583-8ac06b4956d2\", \"creation_timestamp\": \"2022-08-12T20:00:51.322015\", \"last_update_timestamp\": \"2022-08-12T20:00:51.322015\"}, \"xbtManifest\": {\"xbt_id\": \"fddb814a-96b9-4392-bd07-2af08233e5f1\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3354}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:IDREF\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"954467cfde776fc6d45ed393de47ecff\", \"name\": \"non-colonized name\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-12 20:00:51.322015'),
    (27020, '97b81c75beb9735080f8136d0f13d3ba', 1, 1, 'Added', '169bb1ded95c8edc188262c21affb79c', '{\"guid\": \"169bb1ded95c8edc188262c21affb79c\", \"name\": \"notation\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"169bb1ded95c8edc188262c21affb79c\", \"name\": \"notation\", \"state\": 3, \"xbt_id\": \"1cdcbd74-1896-4f8c-b2ab-c68e47976e18\", \"created_by\": 1, \"builtIn_type\": \"xsd:NOTATION\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"a5d1f255-ff15-4c38-a5f5-6b3660a80a88\", \"creation_timestamp\": \"2022-08-13 04:15:46.126781\", \"last_update_timestamp\": \"2022-08-13 04:15:46.126781\"}, \"xbtManifest\": {\"xbt_id\": \"18bc4b23-48da-426c-9986-2b4a7bc38f39\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3376}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:NOTATION\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"7114441198054ad78d2fcf9bcdab2cbf\", \"name\": \"any simple type\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-13 04:30:38.195407'),
    (27021, 'f742e9879e085dd8bd1dd7e790509b3c', 1, 1, 'Added', '13e5d1379633033e1d1f8671c62ffb61', '{\"guid\": \"13e5d1379633033e1d1f8671c62ffb61\", \"name\": \"qualified name\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"13e5d1379633033e1d1f8671c62ffb61\", \"name\": \"qualified name\", \"state\": 3, \"xbt_id\": \"18bc4b23-48da-426c-9986-2b4a7bc38f39\", \"created_by\": 1, \"builtIn_type\": \"xsd:QName\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"a5d1f255-ff15-4c38-a5f5-6b3660a80a88\", \"creation_timestamp\": \"2022-08-13 04:15:46.133324\", \"last_update_timestamp\": \"2022-08-13 04:15:46.133324\"}, \"xbtManifest\": {\"xbt_id\": \"1cdcbd74-1896-4f8c-b2ab-c68e47976e18\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3365}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:QName\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"7114441198054ad78d2fcf9bcdab2cbf\", \"name\": \"any simple type\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'sysadm'), '2022-08-13 04:30:57.641335');

SET FOREIGN_KEY_CHECKS = 0;
INSERT INTO `xbt_manifest` (`xbt_manifest_id`, `release_id`, `xbt_id`, `conflict`, `log_id`, `prev_xbt_manifest_id`, `next_xbt_manifest_id`)
VALUES
    (3156, 1, '010e4563-02d8-4715-acb6-5d7227988ca7', 0, 27001, NULL, 3158),
    (3157, 2, '010e4563-02d8-4715-acb6-5d7227988ca7', 0, 27001, 3166, NULL),
    (3158, 3, '010e4563-02d8-4715-acb6-5d7227988ca7', 0, 27001, 3156, 3159),
    (3159, 4, '010e4563-02d8-4715-acb6-5d7227988ca7', 0, 27001, 3158, 3160),
    (3160, 5, '010e4563-02d8-4715-acb6-5d7227988ca7', 0, 27001, 3159, 3161),
    (3161, 7, '010e4563-02d8-4715-acb6-5d7227988ca7', 0, 27001, 3160, 3162),
    (3162, 8, '010e4563-02d8-4715-acb6-5d7227988ca7', 0, 27001, 3161, 3163),
    (3163, 9, '010e4563-02d8-4715-acb6-5d7227988ca7', 0, 27001, 3162, 3164),
    (3164, 10, '010e4563-02d8-4715-acb6-5d7227988ca7', 0, 27001, 3163, 3165),
    (3165, 11, '010e4563-02d8-4715-acb6-5d7227988ca7', 0, 27001, 3164, 3166),
    (3166, 12, '010e4563-02d8-4715-acb6-5d7227988ca7', 0, 27001, 3165, 3157),
    (3167, 1, '06f9b3ea-7699-4355-a02d-4826c47a78a9', 0, 27002, NULL, 3169),
    (3168, 2, '06f9b3ea-7699-4355-a02d-4826c47a78a9', 0, 27002, 3177, NULL),
    (3169, 3, '06f9b3ea-7699-4355-a02d-4826c47a78a9', 0, 27002, 3167, 3170),
    (3170, 4, '06f9b3ea-7699-4355-a02d-4826c47a78a9', 0, 27002, 3169, 3171),
    (3171, 5, '06f9b3ea-7699-4355-a02d-4826c47a78a9', 0, 27002, 3170, 3172),
    (3172, 7, '06f9b3ea-7699-4355-a02d-4826c47a78a9', 0, 27002, 3171, 3173),
    (3173, 8, '06f9b3ea-7699-4355-a02d-4826c47a78a9', 0, 27002, 3172, 3174),
    (3174, 9, '06f9b3ea-7699-4355-a02d-4826c47a78a9', 0, 27002, 3173, 3175),
    (3175, 10, '06f9b3ea-7699-4355-a02d-4826c47a78a9', 0, 27002, 3174, 3176),
    (3176, 11, '06f9b3ea-7699-4355-a02d-4826c47a78a9', 0, 27002, 3175, 3177),
    (3177, 12, '06f9b3ea-7699-4355-a02d-4826c47a78a9', 0, 27002, 3176, 3168),
    (3178, 1, '0d6f0c93-0222-47aa-952c-1c10c9e69883', 0, 27003, NULL, 3180),
    (3179, 2, '0d6f0c93-0222-47aa-952c-1c10c9e69883', 0, 27003, 3188, NULL),
    (3180, 3, '0d6f0c93-0222-47aa-952c-1c10c9e69883', 0, 27003, 3178, 3181),
    (3181, 4, '0d6f0c93-0222-47aa-952c-1c10c9e69883', 0, 27003, 3180, 3182),
    (3182, 5, '0d6f0c93-0222-47aa-952c-1c10c9e69883', 0, 27003, 3181, 3183),
    (3183, 7, '0d6f0c93-0222-47aa-952c-1c10c9e69883', 0, 27003, 3182, 3184),
    (3184, 8, '0d6f0c93-0222-47aa-952c-1c10c9e69883', 0, 27003, 3183, 3185),
    (3185, 9, '0d6f0c93-0222-47aa-952c-1c10c9e69883', 0, 27003, 3184, 3186),
    (3186, 10, '0d6f0c93-0222-47aa-952c-1c10c9e69883', 0, 27003, 3185, 3187),
    (3187, 11, '0d6f0c93-0222-47aa-952c-1c10c9e69883', 0, 27003, 3186, 3188),
    (3188, 12, '0d6f0c93-0222-47aa-952c-1c10c9e69883', 0, 27003, 3187, 3179),
    (3189, 1, '126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 0, 27004, NULL, 3191),
    (3190, 2, '126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 0, 27004, 3199, NULL),
    (3191, 3, '126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 0, 27004, 3189, 3192),
    (3192, 4, '126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 0, 27004, 3191, 3193),
    (3193, 5, '126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 0, 27004, 3192, 3194),
    (3194, 7, '126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 0, 27004, 3193, 3195),
    (3195, 8, '126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 0, 27004, 3194, 3196),
    (3196, 9, '126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 0, 27004, 3195, 3197),
    (3197, 10, '126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 0, 27004, 3196, 3198),
    (3198, 11, '126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 0, 27004, 3197, 3199),
    (3199, 12, '126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 0, 27004, 3198, 3190),
    (3200, 1, '1dce4776-e435-41cc-9941-372c6ab0754c', 0, 27005, NULL, 3202),
    (3201, 2, '1dce4776-e435-41cc-9941-372c6ab0754c', 0, 27005, 3210, NULL),
    (3202, 3, '1dce4776-e435-41cc-9941-372c6ab0754c', 0, 27005, 3200, 3203),
    (3203, 4, '1dce4776-e435-41cc-9941-372c6ab0754c', 0, 27005, 3202, 3204),
    (3204, 5, '1dce4776-e435-41cc-9941-372c6ab0754c', 0, 27005, 3203, 3205),
    (3205, 7, '1dce4776-e435-41cc-9941-372c6ab0754c', 0, 27005, 3204, 3206),
    (3206, 8, '1dce4776-e435-41cc-9941-372c6ab0754c', 0, 27005, 3205, 3207),
    (3207, 9, '1dce4776-e435-41cc-9941-372c6ab0754c', 0, 27005, 3206, 3208),
    (3208, 10, '1dce4776-e435-41cc-9941-372c6ab0754c', 0, 27005, 3207, 3209),
    (3209, 11, '1dce4776-e435-41cc-9941-372c6ab0754c', 0, 27005, 3208, 3210),
    (3210, 12, '1dce4776-e435-41cc-9941-372c6ab0754c', 0, 27005, 3209, 3201),
    (3211, 1, '2877cf6b-357b-4e0a-95f5-1e91413638d2', 0, 27006, NULL, 3213),
    (3212, 2, '2877cf6b-357b-4e0a-95f5-1e91413638d2', 0, 27006, 3221, NULL),
    (3213, 3, '2877cf6b-357b-4e0a-95f5-1e91413638d2', 0, 27006, 3211, 3214),
    (3214, 4, '2877cf6b-357b-4e0a-95f5-1e91413638d2', 0, 27006, 3213, 3215),
    (3215, 5, '2877cf6b-357b-4e0a-95f5-1e91413638d2', 0, 27006, 3214, 3216),
    (3216, 7, '2877cf6b-357b-4e0a-95f5-1e91413638d2', 0, 27006, 3215, 3217),
    (3217, 8, '2877cf6b-357b-4e0a-95f5-1e91413638d2', 0, 27006, 3216, 3218),
    (3218, 9, '2877cf6b-357b-4e0a-95f5-1e91413638d2', 0, 27006, 3217, 3219),
    (3219, 10, '2877cf6b-357b-4e0a-95f5-1e91413638d2', 0, 27006, 3218, 3220),
    (3220, 11, '2877cf6b-357b-4e0a-95f5-1e91413638d2', 0, 27006, 3219, 3221),
    (3221, 12, '2877cf6b-357b-4e0a-95f5-1e91413638d2', 0, 27006, 3220, 3212),
    (3222, 1, '5dcfb77e-2869-4f77-9462-250ce7b7c492', 0, 27007, NULL, 3224),
    (3223, 2, '5dcfb77e-2869-4f77-9462-250ce7b7c492', 0, 27007, 3232, NULL),
    (3224, 3, '5dcfb77e-2869-4f77-9462-250ce7b7c492', 0, 27007, 3222, 3225),
    (3225, 4, '5dcfb77e-2869-4f77-9462-250ce7b7c492', 0, 27007, 3224, 3226),
    (3226, 5, '5dcfb77e-2869-4f77-9462-250ce7b7c492', 0, 27007, 3225, 3227),
    (3227, 7, '5dcfb77e-2869-4f77-9462-250ce7b7c492', 0, 27007, 3226, 3228),
    (3228, 8, '5dcfb77e-2869-4f77-9462-250ce7b7c492', 0, 27007, 3227, 3229),
    (3229, 9, '5dcfb77e-2869-4f77-9462-250ce7b7c492', 0, 27007, 3228, 3230),
    (3230, 10, '5dcfb77e-2869-4f77-9462-250ce7b7c492', 0, 27007, 3229, 3231),
    (3231, 11, '5dcfb77e-2869-4f77-9462-250ce7b7c492', 0, 27007, 3230, 3232),
    (3232, 12, '5dcfb77e-2869-4f77-9462-250ce7b7c492', 0, 27007, 3231, 3223),
    (3233, 1, '65ead9cc-35eb-47fc-9006-078df7cfa32a', 0, 27008, NULL, 3235),
    (3234, 2, '65ead9cc-35eb-47fc-9006-078df7cfa32a', 0, 27008, 3243, NULL),
    (3235, 3, '65ead9cc-35eb-47fc-9006-078df7cfa32a', 0, 27008, 3233, 3236),
    (3236, 4, '65ead9cc-35eb-47fc-9006-078df7cfa32a', 0, 27008, 3235, 3237),
    (3237, 5, '65ead9cc-35eb-47fc-9006-078df7cfa32a', 0, 27008, 3236, 3238),
    (3238, 7, '65ead9cc-35eb-47fc-9006-078df7cfa32a', 0, 27008, 3237, 3239),
    (3239, 8, '65ead9cc-35eb-47fc-9006-078df7cfa32a', 0, 27008, 3238, 3240),
    (3240, 9, '65ead9cc-35eb-47fc-9006-078df7cfa32a', 0, 27008, 3239, 3241),
    (3241, 10, '65ead9cc-35eb-47fc-9006-078df7cfa32a', 0, 27008, 3240, 3242),
    (3242, 11, '65ead9cc-35eb-47fc-9006-078df7cfa32a', 0, 27008, 3241, 3243),
    (3243, 12, '65ead9cc-35eb-47fc-9006-078df7cfa32a', 0, 27008, 3242, 3234),
    (3244, 1, '7fd4451b-6ad1-4930-8570-34c67cb66081', 0, 27009, NULL, 3246),
    (3245, 2, '7fd4451b-6ad1-4930-8570-34c67cb66081', 0, 27009, 3254, NULL),
    (3246, 3, '7fd4451b-6ad1-4930-8570-34c67cb66081', 0, 27009, 3244, 3247),
    (3247, 4, '7fd4451b-6ad1-4930-8570-34c67cb66081', 0, 27009, 3246, 3248),
    (3248, 5, '7fd4451b-6ad1-4930-8570-34c67cb66081', 0, 27009, 3247, 3249),
    (3249, 7, '7fd4451b-6ad1-4930-8570-34c67cb66081', 0, 27009, 3248, 3250),
    (3250, 8, '7fd4451b-6ad1-4930-8570-34c67cb66081', 0, 27009, 3249, 3251),
    (3251, 9, '7fd4451b-6ad1-4930-8570-34c67cb66081', 0, 27009, 3250, 3252),
    (3252, 10, '7fd4451b-6ad1-4930-8570-34c67cb66081', 0, 27009, 3251, 3253),
    (3253, 11, '7fd4451b-6ad1-4930-8570-34c67cb66081', 0, 27009, 3252, 3254),
    (3254, 12, '7fd4451b-6ad1-4930-8570-34c67cb66081', 0, 27009, 3253, 3245),
    (3255, 1, '8c093c3b-1216-401d-bda9-bdb450e02bad', 0, 27010, NULL, 3257),
    (3256, 2, '8c093c3b-1216-401d-bda9-bdb450e02bad', 0, 27010, 3265, NULL),
    (3257, 3, '8c093c3b-1216-401d-bda9-bdb450e02bad', 0, 27010, 3255, 3258),
    (3258, 4, '8c093c3b-1216-401d-bda9-bdb450e02bad', 0, 27010, 3257, 3259),
    (3259, 5, '8c093c3b-1216-401d-bda9-bdb450e02bad', 0, 27010, 3258, 3260),
    (3260, 7, '8c093c3b-1216-401d-bda9-bdb450e02bad', 0, 27010, 3259, 3261),
    (3261, 8, '8c093c3b-1216-401d-bda9-bdb450e02bad', 0, 27010, 3260, 3262),
    (3262, 9, '8c093c3b-1216-401d-bda9-bdb450e02bad', 0, 27010, 3261, 3263),
    (3263, 10, '8c093c3b-1216-401d-bda9-bdb450e02bad', 0, 27010, 3262, 3264),
    (3264, 11, '8c093c3b-1216-401d-bda9-bdb450e02bad', 0, 27010, 3263, 3265),
    (3265, 12, '8c093c3b-1216-401d-bda9-bdb450e02bad', 0, 27010, 3264, 3256),
    (3266, 1, '9895d011-6625-41f8-a01c-29692ae323e9', 0, 27011, NULL, 3268),
    (3267, 2, '9895d011-6625-41f8-a01c-29692ae323e9', 0, 27011, 3276, NULL),
    (3268, 3, '9895d011-6625-41f8-a01c-29692ae323e9', 0, 27011, 3266, 3269),
    (3269, 4, '9895d011-6625-41f8-a01c-29692ae323e9', 0, 27011, 3268, 3270),
    (3270, 5, '9895d011-6625-41f8-a01c-29692ae323e9', 0, 27011, 3269, 3271),
    (3271, 7, '9895d011-6625-41f8-a01c-29692ae323e9', 0, 27011, 3270, 3272),
    (3272, 8, '9895d011-6625-41f8-a01c-29692ae323e9', 0, 27011, 3271, 3273),
    (3273, 9, '9895d011-6625-41f8-a01c-29692ae323e9', 0, 27011, 3272, 3274),
    (3274, 10, '9895d011-6625-41f8-a01c-29692ae323e9', 0, 27011, 3273, 3275),
    (3275, 11, '9895d011-6625-41f8-a01c-29692ae323e9', 0, 27011, 3274, 3276),
    (3276, 12, '9895d011-6625-41f8-a01c-29692ae323e9', 0, 27011, 3275, 3267),
    (3277, 1, '98a97e1c-5d7c-429d-851e-2e0516015b7b', 0, 27012, NULL, 3279),
    (3278, 2, '98a97e1c-5d7c-429d-851e-2e0516015b7b', 0, 27012, 3287, NULL),
    (3279, 3, '98a97e1c-5d7c-429d-851e-2e0516015b7b', 0, 27012, 3277, 3280),
    (3280, 4, '98a97e1c-5d7c-429d-851e-2e0516015b7b', 0, 27012, 3279, 3281),
    (3281, 5, '98a97e1c-5d7c-429d-851e-2e0516015b7b', 0, 27012, 3280, 3282),
    (3282, 7, '98a97e1c-5d7c-429d-851e-2e0516015b7b', 0, 27012, 3281, 3283),
    (3283, 8, '98a97e1c-5d7c-429d-851e-2e0516015b7b', 0, 27012, 3282, 3284),
    (3284, 9, '98a97e1c-5d7c-429d-851e-2e0516015b7b', 0, 27012, 3283, 3285),
    (3285, 10, '98a97e1c-5d7c-429d-851e-2e0516015b7b', 0, 27012, 3284, 3286),
    (3286, 11, '98a97e1c-5d7c-429d-851e-2e0516015b7b', 0, 27012, 3285, 3287),
    (3287, 12, '98a97e1c-5d7c-429d-851e-2e0516015b7b', 0, 27012, 3286, 3278),
    (3288, 1, 'a08258f8-0ee9-44a4-b937-323ef1bb907d', 0, 27013, NULL, 3290),
    (3289, 2, 'a08258f8-0ee9-44a4-b937-323ef1bb907d', 0, 27013, 3298, NULL),
    (3290, 3, 'a08258f8-0ee9-44a4-b937-323ef1bb907d', 0, 27013, 3288, 3291),
    (3291, 4, 'a08258f8-0ee9-44a4-b937-323ef1bb907d', 0, 27013, 3290, 3292),
    (3292, 5, 'a08258f8-0ee9-44a4-b937-323ef1bb907d', 0, 27013, 3291, 3293),
    (3293, 7, 'a08258f8-0ee9-44a4-b937-323ef1bb907d', 0, 27013, 3292, 3294),
    (3294, 8, 'a08258f8-0ee9-44a4-b937-323ef1bb907d', 0, 27013, 3293, 3295),
    (3295, 9, 'a08258f8-0ee9-44a4-b937-323ef1bb907d', 0, 27013, 3294, 3296),
    (3296, 10, 'a08258f8-0ee9-44a4-b937-323ef1bb907d', 0, 27013, 3295, 3297),
    (3297, 11, 'a08258f8-0ee9-44a4-b937-323ef1bb907d', 0, 27013, 3296, 3298),
    (3298, 12, 'a08258f8-0ee9-44a4-b937-323ef1bb907d', 0, 27013, 3297, 3289),
    (3299, 1, 'a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', 0, 27014, NULL, 3301),
    (3300, 2, 'a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', 0, 27014, 3309, NULL),
    (3301, 3, 'a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', 0, 27014, 3299, 3302),
    (3302, 4, 'a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', 0, 27014, 3301, 3303),
    (3303, 5, 'a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', 0, 27014, 3302, 3304),
    (3304, 7, 'a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', 0, 27014, 3303, 3305),
    (3305, 8, 'a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', 0, 27014, 3304, 3306),
    (3306, 9, 'a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', 0, 27014, 3305, 3307),
    (3307, 10, 'a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', 0, 27014, 3306, 3308),
    (3308, 11, 'a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', 0, 27014, 3307, 3309),
    (3309, 12, 'a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', 0, 27014, 3308, 3300),
    (3310, 1, 'c22e47f7-66db-433a-a1b4-9b1a151983b3', 0, 27015, NULL, 3312),
    (3311, 2, 'c22e47f7-66db-433a-a1b4-9b1a151983b3', 0, 27015, 3320, NULL),
    (3312, 3, 'c22e47f7-66db-433a-a1b4-9b1a151983b3', 0, 27015, 3310, 3313),
    (3313, 4, 'c22e47f7-66db-433a-a1b4-9b1a151983b3', 0, 27015, 3312, 3314),
    (3314, 5, 'c22e47f7-66db-433a-a1b4-9b1a151983b3', 0, 27015, 3313, 3315),
    (3315, 7, 'c22e47f7-66db-433a-a1b4-9b1a151983b3', 0, 27015, 3314, 3316),
    (3316, 8, 'c22e47f7-66db-433a-a1b4-9b1a151983b3', 0, 27015, 3315, 3317),
    (3317, 9, 'c22e47f7-66db-433a-a1b4-9b1a151983b3', 0, 27015, 3316, 3318),
    (3318, 10, 'c22e47f7-66db-433a-a1b4-9b1a151983b3', 0, 27015, 3317, 3319),
    (3319, 11, 'c22e47f7-66db-433a-a1b4-9b1a151983b3', 0, 27015, 3318, 3320),
    (3320, 12, 'c22e47f7-66db-433a-a1b4-9b1a151983b3', 0, 27015, 3319, 3311),
    (3321, 1, 'd5af9325-d1c8-4bd0-8583-8ac06b4956d2', 0, 27016, NULL, 3323),
    (3322, 2, 'd5af9325-d1c8-4bd0-8583-8ac06b4956d2', 0, 27016, 3331, NULL),
    (3323, 3, 'd5af9325-d1c8-4bd0-8583-8ac06b4956d2', 0, 27016, 3321, 3324),
    (3324, 4, 'd5af9325-d1c8-4bd0-8583-8ac06b4956d2', 0, 27016, 3323, 3325),
    (3325, 5, 'd5af9325-d1c8-4bd0-8583-8ac06b4956d2', 0, 27016, 3324, 3326),
    (3326, 7, 'd5af9325-d1c8-4bd0-8583-8ac06b4956d2', 0, 27016, 3325, 3327),
    (3327, 8, 'd5af9325-d1c8-4bd0-8583-8ac06b4956d2', 0, 27016, 3326, 3328),
    (3328, 9, 'd5af9325-d1c8-4bd0-8583-8ac06b4956d2', 0, 27016, 3327, 3329),
    (3329, 10, 'd5af9325-d1c8-4bd0-8583-8ac06b4956d2', 0, 27016, 3328, 3330),
    (3330, 11, 'd5af9325-d1c8-4bd0-8583-8ac06b4956d2', 0, 27016, 3329, 3331),
    (3331, 12, 'd5af9325-d1c8-4bd0-8583-8ac06b4956d2', 0, 27016, 3330, 3322),
    (3332, 1, 'de57e550-fcaf-481d-b8a3-6b301ee48988', 0, 27017, NULL, 3334),
    (3333, 2, 'de57e550-fcaf-481d-b8a3-6b301ee48988', 0, 27017, 3342, NULL),
    (3334, 3, 'de57e550-fcaf-481d-b8a3-6b301ee48988', 0, 27017, 3332, 3335),
    (3335, 4, 'de57e550-fcaf-481d-b8a3-6b301ee48988', 0, 27017, 3334, 3336),
    (3336, 5, 'de57e550-fcaf-481d-b8a3-6b301ee48988', 0, 27017, 3335, 3337),
    (3337, 7, 'de57e550-fcaf-481d-b8a3-6b301ee48988', 0, 27017, 3336, 3338),
    (3338, 8, 'de57e550-fcaf-481d-b8a3-6b301ee48988', 0, 27017, 3337, 3339),
    (3339, 9, 'de57e550-fcaf-481d-b8a3-6b301ee48988', 0, 27017, 3338, 3340),
    (3340, 10, 'de57e550-fcaf-481d-b8a3-6b301ee48988', 0, 27017, 3339, 3341),
    (3341, 11, 'de57e550-fcaf-481d-b8a3-6b301ee48988', 0, 27017, 3340, 3342),
    (3342, 12, 'de57e550-fcaf-481d-b8a3-6b301ee48988', 0, 27017, 3341, 3333),
    (3343, 1, 'eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', 0, 27018, NULL, 3345),
    (3344, 2, 'eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', 0, 27018, 3353, NULL),
    (3345, 3, 'eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', 0, 27018, 3343, 3346),
    (3346, 4, 'eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', 0, 27018, 3345, 3347),
    (3347, 5, 'eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', 0, 27018, 3346, 3348),
    (3348, 7, 'eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', 0, 27018, 3347, 3349),
    (3349, 8, 'eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', 0, 27018, 3348, 3350),
    (3350, 9, 'eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', 0, 27018, 3349, 3351),
    (3351, 10, 'eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', 0, 27018, 3350, 3352),
    (3352, 11, 'eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', 0, 27018, 3351, 3353),
    (3353, 12, 'eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', 0, 27018, 3352, 3344),
    (3354, 1, 'fddb814a-96b9-4392-bd07-2af08233e5f1', 0, 27019, NULL, 3356),
    (3355, 2, 'fddb814a-96b9-4392-bd07-2af08233e5f1', 0, 27019, 3364, NULL),
    (3356, 3, 'fddb814a-96b9-4392-bd07-2af08233e5f1', 0, 27019, 3354, 3357),
    (3357, 4, 'fddb814a-96b9-4392-bd07-2af08233e5f1', 0, 27019, 3356, 3358),
    (3358, 5, 'fddb814a-96b9-4392-bd07-2af08233e5f1', 0, 27019, 3357, 3359),
    (3359, 7, 'fddb814a-96b9-4392-bd07-2af08233e5f1', 0, 27019, 3358, 3360),
    (3360, 8, 'fddb814a-96b9-4392-bd07-2af08233e5f1', 0, 27019, 3359, 3361),
    (3361, 9, 'fddb814a-96b9-4392-bd07-2af08233e5f1', 0, 27019, 3360, 3362),
    (3362, 10, 'fddb814a-96b9-4392-bd07-2af08233e5f1', 0, 27019, 3361, 3363),
    (3363, 11, 'fddb814a-96b9-4392-bd07-2af08233e5f1', 0, 27019, 3362, 3364),
    (3364, 12, 'fddb814a-96b9-4392-bd07-2af08233e5f1', 0, 27019, 3363, 3355),
    (3365, 1, '18bc4b23-48da-426c-9986-2b4a7bc38f39', 0, 27020, NULL, 3367),
    (3366, 2, '18bc4b23-48da-426c-9986-2b4a7bc38f39', 0, 27020, 3375, NULL),
    (3367, 3, '18bc4b23-48da-426c-9986-2b4a7bc38f39', 0, 27020, 3365, 3368),
    (3368, 4, '18bc4b23-48da-426c-9986-2b4a7bc38f39', 0, 27020, 3367, 3369),
    (3369, 5, '18bc4b23-48da-426c-9986-2b4a7bc38f39', 0, 27020, 3368, 3370),
    (3370, 7, '18bc4b23-48da-426c-9986-2b4a7bc38f39', 0, 27020, 3369, 3371),
    (3371, 8, '18bc4b23-48da-426c-9986-2b4a7bc38f39', 0, 27020, 3370, 3372),
    (3372, 9, '18bc4b23-48da-426c-9986-2b4a7bc38f39', 0, 27020, 3371, 3373),
    (3373, 10, '18bc4b23-48da-426c-9986-2b4a7bc38f39', 0, 27020, 3372, 3374),
    (3374, 11, '18bc4b23-48da-426c-9986-2b4a7bc38f39', 0, 27020, 3373, 3375),
    (3375, 12, '18bc4b23-48da-426c-9986-2b4a7bc38f39', 0, 27020, 3374, 3366),
    (3376, 1, '1cdcbd74-1896-4f8c-b2ab-c68e47976e18', 0, 27021, NULL, 3378),
    (3377, 2, '1cdcbd74-1896-4f8c-b2ab-c68e47976e18', 0, 27021, 3386, NULL),
    (3378, 3, '1cdcbd74-1896-4f8c-b2ab-c68e47976e18', 0, 27021, 3376, 3379),
    (3379, 4, '1cdcbd74-1896-4f8c-b2ab-c68e47976e18', 0, 27021, 3378, 3380),
    (3380, 5, '1cdcbd74-1896-4f8c-b2ab-c68e47976e18', 0, 27021, 3379, 3381),
    (3381, 7, '1cdcbd74-1896-4f8c-b2ab-c68e47976e18', 0, 27021, 3380, 3382),
    (3382, 8, '1cdcbd74-1896-4f8c-b2ab-c68e47976e18', 0, 27021, 3381, 3383),
    (3383, 9, '1cdcbd74-1896-4f8c-b2ab-c68e47976e18', 0, 27021, 3382, 3384),
    (3384, 10, '1cdcbd74-1896-4f8c-b2ab-c68e47976e18', 0, 27021, 3383, 3385),
    (3385, 11, '1cdcbd74-1896-4f8c-b2ab-c68e47976e18', 0, 27021, 3384, 3386),
    (3386, 12, '1cdcbd74-1896-4f8c-b2ab-c68e47976e18', 0, 27021, 3385, 3377);
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `cdt_awd_pri_xps_type_map` (`cdt_awd_pri_xps_type_map_id`, `cdt_awd_pri_id`, `xbt_id`, `is_default`)
SELECT uuid_v4s(), `cdt_awd_pri`.`cdt_awd_pri_id`, `xbt`.`xbt_id`, 0
FROM `xbt`, `cdt_awd_pri`
JOIN `cdt_pri` ON `cdt_awd_pri`.`cdt_pri_id` = `cdt_pri`.`cdt_pri_id`
WHERE `cdt_pri`.`name` = 'Integer'
  AND `xbt`.`name` IN ('non positive integer', 'negative integer', 'long', 'int', 'short', 'byte', 'unsigned long', 'unsigned int', 'unsigned short', 'unsigned byte');

INSERT INTO `cdt_awd_pri_xps_type_map` (`cdt_awd_pri_xps_type_map_id`, `cdt_awd_pri_id`, `xbt_id`, `is_default`)
SELECT uuid_v4s(), `cdt_awd_pri`.`cdt_awd_pri_id`, `xbt`.`xbt_id`, 0
FROM `xbt`, `cdt_awd_pri`
JOIN `cdt_pri` ON `cdt_awd_pri`.`cdt_pri_id` = `cdt_pri`.`cdt_pri_id`
WHERE `cdt_pri`.`name` = 'Token'
  AND `xbt`.`name` IN ('name', 'non-colonized name', 'name token', 'name tokens', 'identifier', 'identifier reference', 'identifier references', 'entity', 'entities');

INSERT INTO `cdt_awd_pri_xps_type_map` (`cdt_awd_pri_xps_type_map_id`, `cdt_awd_pri_id`, `xbt_id`, `is_default`)
SELECT uuid_v4s(), `cdt_awd_pri`.`cdt_awd_pri_id`, `xbt`.`xbt_id`, 0
FROM `xbt`, `cdt_awd_pri`
JOIN `cdt_pri` ON `cdt_awd_pri`.`cdt_pri_id` = `cdt_pri`.`cdt_pri_id`
WHERE `cdt_pri`.`name` = 'String'
  AND `xbt`.`name` IN ('qualified name', 'notation');

INSERT INTO `cdt_sc_awd_pri_xps_type_map` (`cdt_sc_awd_pri_xps_type_map_id`, `cdt_sc_awd_pri_id`, `xbt_id`)
SELECT uuid_v4s(), `cdt_sc_awd_pri`.`cdt_sc_awd_pri_id`, `xbt`.`xbt_id`
FROM `xbt`, `cdt_sc_awd_pri`
JOIN `cdt_pri` ON `cdt_sc_awd_pri`.`cdt_pri_id` = `cdt_pri`.`cdt_pri_id`
WHERE `cdt_pri`.`name` = 'Integer'
  AND `xbt`.`name` IN ('non positive integer', 'negative integer', 'long', 'int', 'short', 'byte', 'unsigned long', 'unsigned int', 'unsigned short', 'unsigned byte');

INSERT INTO `cdt_sc_awd_pri_xps_type_map` (`cdt_sc_awd_pri_xps_type_map_id`, `cdt_sc_awd_pri_id`, `xbt_id`)
SELECT uuid_v4s(), `cdt_sc_awd_pri`.`cdt_sc_awd_pri_id`, `xbt`.`xbt_id`
FROM `xbt`, `cdt_sc_awd_pri`
JOIN `cdt_pri` ON `cdt_sc_awd_pri`.`cdt_pri_id` = `cdt_pri`.`cdt_pri_id`
WHERE `cdt_pri`.`name` = 'Token'
  AND `xbt`.`name` IN ('name', 'non-colonized name', 'name token', 'name tokens', 'identifier', 'identifier reference', 'identifier references', 'entity', 'entities');

INSERT INTO `cdt_sc_awd_pri_xps_type_map` (`cdt_sc_awd_pri_xps_type_map_id`, `cdt_sc_awd_pri_id`, `xbt_id`)
SELECT uuid_v4s(), `cdt_sc_awd_pri`.`cdt_sc_awd_pri_id`, `xbt`.`xbt_id`
FROM `xbt`, `cdt_sc_awd_pri`
JOIN `cdt_pri` ON `cdt_sc_awd_pri`.`cdt_pri_id` = `cdt_pri`.`cdt_pri_id`
WHERE `cdt_pri`.`name` = 'String'
  AND `xbt`.`name` IN ('qualified name', 'notation');

-- ------------------------------------------------
-- Change `abie_id`, `asbie_id`, `bbie_id`,      --
--        `asbiep_id`, `bbiep_id`, `bbie_sc_id`, --
--        and `top_level_asbiep_id` TO UUID      --
-- ------------------------------------------------
ALTER TABLE `abie` ADD COLUMN `abie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `abie_id`;
CALL update_uuid('abie');

ALTER TABLE `asbie` ADD COLUMN `asbie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `asbie_id`;
CALL update_uuid('asbie');

ALTER TABLE `bbie` ADD COLUMN `bbie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `bbie_id`;
CALL update_uuid('bbie');

ALTER TABLE `asbiep` ADD COLUMN `asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `asbiep_id`;
CALL update_uuid('asbiep');

ALTER TABLE `bbiep` ADD COLUMN `bbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `bbiep_id`;
CALL update_uuid('bbiep');

ALTER TABLE `bbie_sc` ADD COLUMN `bbie_sc_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `bbie_sc_id`;
CALL update_uuid('bbie_sc');

ALTER TABLE `top_level_asbiep` ADD COLUMN `top_level_asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `top_level_asbiep_id`;
CALL update_uuid('top_level_asbiep');

ALTER TABLE `abie` ADD COLUMN `owner_top_level_asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.' AFTER `owner_top_level_asbiep_id`;
UPDATE `abie`, `top_level_asbiep` SET `abie`.`owner_top_level_asbiep_uuid` = `top_level_asbiep`.`top_level_asbiep_uuid`
WHERE `abie`.`owner_top_level_asbiep_id` = `top_level_asbiep`.`top_level_asbiep_id`;

ALTER TABLE `asbie`
    ADD COLUMN `from_abie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointing to the ABIE table. FROM_ABIE_ID is basically  a parent data element (type) of the TO_ASBIEP_ID. FROM_ABIE_ID must be based on the FROM_ACC_ID in the BASED_ASCC_ID except when the FROM_ACC_ID refers to an SEMANTIC_GROUP ACC or USER_EXTENSION_GROUP ACC.' AFTER `from_abie_id`,
    ADD COLUMN `to_asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key to the ASBIEP table. TO_ASBIEP_ID is basically a child data element of the FROM_ABIE_ID. The TO_ASBIEP_ID must be based on the TO_ASCCP_ID in the BASED_ASCC_ID. the ASBIEP is reused with the OWNER_TOP_LEVEL_ASBIEP is different after joining ASBIE and ASBIEP tables' AFTER `to_asbiep_id`,
    ADD COLUMN `owner_top_level_asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.' AFTER `owner_top_level_asbiep_id`;
UPDATE `asbie`, `abie` SET `asbie`.`from_abie_uuid` = `abie`.`abie_uuid`
WHERE `asbie`.`from_abie_id` = `abie`.`abie_id`;
UPDATE `asbie`, `asbiep` SET `asbie`.`to_asbiep_uuid` = `asbiep`.`asbiep_uuid`
WHERE `asbie`.`to_asbiep_id` = `asbiep`.`asbiep_id`;
UPDATE `asbie`, `top_level_asbiep` SET `asbie`.`owner_top_level_asbiep_uuid` = `top_level_asbiep`.`top_level_asbiep_uuid`
WHERE `asbie`.`owner_top_level_asbiep_id` = `top_level_asbiep`.`top_level_asbiep_id`;

ALTER TABLE `asbie_bizterm` ADD COLUMN `asbie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'An internal ID of the associated ASBIE' AFTER `asbie_id`;
UPDATE `asbie_bizterm`, `asbie` SET `asbie_bizterm`.`asbie_uuid` = `asbie`.`asbie_uuid`
WHERE `asbie_bizterm`.`asbie_id` = `asbie`.`asbie_id`;

ALTER TABLE `asbiep`
    ADD COLUMN `role_of_abie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointing to the ABIE record. It is the ABIE, which the property term in the based ASCCP qualifies. Note that the ABIE has to be derived from the ACC used by the based ASCCP.' AFTER `role_of_abie_id`,
    ADD COLUMN `owner_top_level_asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.' AFTER `owner_top_level_asbiep_id`;
UPDATE `asbiep`, `abie` SET `asbiep`.`role_of_abie_uuid` = `abie`.`abie_uuid`
WHERE `asbiep`.`role_of_abie_id` = `abie`.`abie_id`;
UPDATE `asbiep`, `top_level_asbiep` SET `asbiep`.`owner_top_level_asbiep_uuid` = `top_level_asbiep`.`top_level_asbiep_uuid`
WHERE `asbiep`.`owner_top_level_asbiep_id` = `top_level_asbiep`.`top_level_asbiep_id`;

ALTER TABLE `bbie`
    ADD COLUMN `from_abie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key pointing to the ABIE table. FROM_ABIE_ID is basically  a parent data element (type) of the TO_ASBIEP_ID. FROM_ABIE_ID must be based on the FROM_ACC_ID in the BASED_ASCC_ID except when the FROM_ACC_ID refers to an SEMANTIC_GROUP ACC or USER_EXTENSION_GROUP ACC.FROM_ABIE_ID must be based on the FROM_ACC_ID in the BASED_BCC_ID.' AFTER `from_abie_id`,
    ADD COLUMN `to_bbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'TO_BBIEP_ID is a foreign key to the BBIEP table. TO_BBIEP_ID basically refers to a child data element of the FROM_ABIE_ID. TO_BBIEP_ID must be based on the TO_BCCP_ID in the based BCC.' AFTER `to_bbiep_id`,
    ADD COLUMN `owner_top_level_asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.' AFTER `owner_top_level_asbiep_id`;
UPDATE `bbie`, `abie` SET `bbie`.`from_abie_uuid` = `abie`.`abie_uuid`
WHERE `bbie`.`from_abie_id` = `abie`.`abie_id`;
UPDATE `bbie`, `bbiep` SET `bbie`.`to_bbiep_uuid` = `bbiep`.`bbiep_uuid`
WHERE `bbie`.`to_bbiep_id` = `bbiep`.`bbiep_id`;
UPDATE `bbie`, `top_level_asbiep` SET `bbie`.`owner_top_level_asbiep_uuid` = `top_level_asbiep`.`top_level_asbiep_uuid`
WHERE `bbie`.`owner_top_level_asbiep_id` = `top_level_asbiep`.`top_level_asbiep_id`;

ALTER TABLE `bbie_bizterm` ADD COLUMN `bbie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'An internal ID of the associated BBIE' AFTER `bbie_id`;
UPDATE `bbie_bizterm`, `bbie` SET `bbie_bizterm`.`bbie_uuid` = `bbie`.`bbie_uuid`
WHERE `bbie_bizterm`.`bbie_id` = `bbie`.`bbie_id`;

ALTER TABLE `bbie_sc`
    ADD COLUMN `bbie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'The BBIE this BBIE_SC applies to.' AFTER `bbie_id`,
    ADD COLUMN `owner_top_level_asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.' AFTER `owner_top_level_asbiep_id`;
UPDATE `bbie_sc`, `bbie` SET `bbie_sc`.`bbie_uuid` = `bbie`.`bbie_uuid`
WHERE `bbie_sc`.`bbie_id` = `bbie`.`bbie_id`;
UPDATE `bbie_sc`, `top_level_asbiep` SET `bbie_sc`.`owner_top_level_asbiep_uuid` = `top_level_asbiep`.`top_level_asbiep_uuid`
WHERE `bbie_sc`.`owner_top_level_asbiep_id` = `top_level_asbiep`.`top_level_asbiep_id`;

ALTER TABLE `bbiep` ADD COLUMN `owner_top_level_asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.' AFTER `owner_top_level_asbiep_id`;
UPDATE `bbiep`, `top_level_asbiep` SET `bbiep`.`owner_top_level_asbiep_uuid` = `top_level_asbiep`.`top_level_asbiep_uuid`
WHERE `bbiep`.`owner_top_level_asbiep_id` = `top_level_asbiep`.`top_level_asbiep_id`;

ALTER TABLE `bie_usage_rule`
    ADD COLUMN `target_abie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the ABIE table indicating the ABIE, to which the usage rule is applied.' AFTER `target_abie_id`,
    ADD COLUMN `target_asbie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the ASBIE table indicating the ASBIE, to which the usage rule is applied.' AFTER `target_asbie_id`,
    ADD COLUMN `target_asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the ASBIEP table indicating the ASBIEP, to which the usage rule is applied.' AFTER `target_asbiep_id`,
    ADD COLUMN `target_bbie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the BBIE table indicating the BBIE, to which the usage rule is applied.' AFTER `target_bbie_id`,
    ADD COLUMN `target_bbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the BBIEP table indicating the ABIEP, to which the usage rule is applied.' AFTER `target_bbiep_id`;
UPDATE `bie_usage_rule`, `abie` SET `bie_usage_rule`.`target_abie_uuid` = `abie`.`abie_uuid`
WHERE `bie_usage_rule`.`target_abie_id` = `abie`.`abie_id`;
UPDATE `bie_usage_rule`, `asbie` SET `bie_usage_rule`.`target_asbie_uuid` = `asbie`.`asbie_uuid`
WHERE `bie_usage_rule`.`target_asbie_id` = `asbie`.`asbie_id`;
UPDATE `bie_usage_rule`, `asbiep` SET `bie_usage_rule`.`target_asbiep_uuid` = `asbiep`.`asbiep_uuid`
WHERE `bie_usage_rule`.`target_asbiep_id` = `asbiep`.`asbiep_id`;
UPDATE `bie_usage_rule`, `bbie` SET `bie_usage_rule`.`target_bbie_uuid` = `bbie`.`bbie_uuid`
WHERE `bie_usage_rule`.`target_bbie_id` = `bbie`.`bbie_id`;
UPDATE `bie_usage_rule`, `bbiep` SET `bie_usage_rule`.`target_bbiep_uuid` = `bbiep`.`bbiep_uuid`
WHERE `bie_usage_rule`.`target_bbiep_id` = `bbiep`.`bbiep_id`;

ALTER TABLE `bie_user_ext_revision`
    ADD COLUMN `ext_abie_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This points to an ABIE record corresponding to the EXTENSION_ACC_ID record. For example, this column can point to the ApplicationAreaExtension ABIE which is based on the ApplicationAreaExtension ACC (referred to by the EXT_ACC_ID column). This column can be NULL only when the extension is the AllExtension because there is no corresponding ABIE for the AllExtension ACC.' AFTER `ext_abie_id`,
    ADD COLUMN `top_level_asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.' AFTER `top_level_asbiep_id`;
UPDATE `bie_user_ext_revision`, `abie` SET `bie_user_ext_revision`.`ext_abie_uuid` = `abie`.`abie_uuid`
WHERE `bie_user_ext_revision`.`ext_abie_uuid` = `abie`.`abie_id`;
UPDATE `bie_user_ext_revision`, `top_level_asbiep` SET `bie_user_ext_revision`.`top_level_asbiep_uuid` = `top_level_asbiep`.`top_level_asbiep_uuid`
WHERE `bie_user_ext_revision`.`top_level_asbiep_id` = `top_level_asbiep`.`top_level_asbiep_id`;

ALTER TABLE `biz_ctx_assignment` ADD COLUMN `top_level_asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.' AFTER `top_level_asbiep_id`;
UPDATE `biz_ctx_assignment`, `top_level_asbiep` SET `biz_ctx_assignment`.`top_level_asbiep_uuid` = `top_level_asbiep`.`top_level_asbiep_uuid`
WHERE `biz_ctx_assignment`.`top_level_asbiep_id` = `top_level_asbiep`.`top_level_asbiep_id`;

ALTER TABLE `top_level_asbiep` ADD COLUMN `asbiep_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the ASBIEP table pointing to a record which is a top-level ASBIEP.' AFTER `asbiep_id`;
UPDATE `top_level_asbiep`, `asbiep` SET `top_level_asbiep`.`asbiep_uuid` = `asbiep`.`asbiep_uuid`
WHERE `top_level_asbiep`.`asbiep_id` = `asbiep`.`asbiep_id`;

-- Drop old `xbt_id` columns
ALTER TABLE `abie` DROP FOREIGN KEY `abie_owner_top_level_asbiep_id_fk`;
ALTER TABLE `abie` DROP COLUMN `owner_top_level_asbiep_id`;

ALTER TABLE `asbie`
    DROP FOREIGN KEY `asbie_from_abie_id_fk`,
    DROP FOREIGN KEY `asbie_to_asbiep_id_fk`,
    DROP FOREIGN KEY `asbie_owner_top_level_asbiep_id_fk`;
ALTER TABLE `asbie`
    DROP COLUMN `from_abie_id`,
    DROP COLUMN `to_asbiep_id`,
    DROP COLUMN `owner_top_level_asbiep_id`;

ALTER TABLE `asbie_bizterm` DROP FOREIGN KEY `asbie_bizterm_asbie_fk`;
ALTER TABLE `asbie_bizterm` DROP COLUMN `asbie_id`;

ALTER TABLE `asbiep`
    DROP FOREIGN KEY `asbiep_role_of_abie_id_fk`,
    DROP FOREIGN KEY `asbiep_owner_top_level_asbiep_id_fk`;
ALTER TABLE `asbiep`
    DROP COLUMN `role_of_abie_id`,
    DROP COLUMN `owner_top_level_asbiep_id`;

ALTER TABLE `bbie`
    DROP FOREIGN KEY `bbie_from_abie_id_fk`,
    DROP FOREIGN KEY `bbie_to_bbiep_id_fk`,
    DROP FOREIGN KEY `bbie_owner_top_level_asbiep_id_fk`;
ALTER TABLE `bbie`
    DROP COLUMN `from_abie_id`,
    DROP COLUMN `to_bbiep_id`,
    DROP COLUMN `owner_top_level_asbiep_id`;

ALTER TABLE `bbie_bizterm` DROP FOREIGN KEY `bbie_bizterm_bbie_fk`;
ALTER TABLE `bbie_bizterm` DROP COLUMN `bbie_id`;

ALTER TABLE `bbie_sc`
    DROP FOREIGN KEY `bbie_sc_bbie_id_fk`,
    DROP FOREIGN KEY `bbie_sc_owner_top_level_asbiep_id_fk`;
ALTER TABLE `bbie_sc`
    DROP COLUMN `bbie_id`,
    DROP COLUMN `owner_top_level_asbiep_id`;

ALTER TABLE `bbiep` DROP FOREIGN KEY `bbiep_owner_top_level_asbiep_id_fk`;
ALTER TABLE `bbiep` DROP COLUMN `owner_top_level_asbiep_id`;

ALTER TABLE `bie_usage_rule`
    DROP FOREIGN KEY `bie_usage_rule_target_abie_id_fk`,
    DROP FOREIGN KEY `bie_usage_rule_target_asbie_id_fk`,
    DROP FOREIGN KEY `bie_usage_rule_target_asbiep_id_fk`,
    DROP FOREIGN KEY `bie_usage_rule_target_bbie_id_fk`,
    DROP FOREIGN KEY `bie_usage_rule_target_bbiep_id_fk`;
ALTER TABLE `bie_usage_rule`
    DROP COLUMN `target_abie_id`,
    DROP COLUMN `target_asbie_id`,
    DROP COLUMN `target_asbiep_id`,
    DROP COLUMN `target_bbie_id`,
    DROP COLUMN `target_bbiep_id`;

ALTER TABLE `bie_user_ext_revision`
    DROP FOREIGN KEY `bie_user_ext_revision_ext_abie_id_fk`,
    DROP FOREIGN KEY `bie_user_ext_revision_top_level_asbiep_id_fk`;
ALTER TABLE `bie_user_ext_revision`
    DROP COLUMN `ext_abie_id`,
    DROP COLUMN `top_level_asbiep_id`;

ALTER TABLE `biz_ctx_assignment`
    DROP FOREIGN KEY `biz_ctx_assignment_top_level_asbiep_id_fk`,
    DROP FOREIGN KEY `biz_ctx_assignment_biz_ctx_id_fk`;
ALTER TABLE `biz_ctx_assignment` DROP KEY `biz_ctx_assignment_uk`;
ALTER TABLE `biz_ctx_assignment` DROP COLUMN `top_level_asbiep_id`;

ALTER TABLE `top_level_asbiep` DROP FOREIGN KEY `top_level_asbiep_asbiep_id_fk`;
ALTER TABLE `top_level_asbiep` DROP COLUMN `asbiep_id`;

ALTER TABLE `abie` DROP COLUMN `abie_id`;
ALTER TABLE `asbie` DROP COLUMN `asbie_id`;
ALTER TABLE `bbie` DROP COLUMN `bbie_id`;
ALTER TABLE `asbiep` DROP COLUMN `asbiep_id`;
ALTER TABLE `bbiep` DROP COLUMN `bbiep_id`;
ALTER TABLE `bbie_sc` DROP COLUMN `bbie_sc_id`;
ALTER TABLE `top_level_asbiep` DROP COLUMN `top_level_asbiep_id`;

-- Rename `*_uuid` TO `*_id`
ALTER TABLE `abie` CHANGE `abie_uuid` `abie_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `asbie` CHANGE `asbie_uuid` `asbie_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bbie` CHANGE `bbie_uuid` `bbie_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `asbiep` CHANGE `asbiep_uuid` `asbiep_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bbiep` CHANGE `bbiep_uuid` `bbiep_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bbie_sc` CHANGE `bbie_sc_uuid` `bbie_sc_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `top_level_asbiep` CHANGE `top_level_asbiep_uuid` `top_level_asbiep_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';

ALTER TABLE `abie` CHANGE `owner_top_level_asbiep_uuid` `owner_top_level_asbiep_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.';

ALTER TABLE `asbie`
    CHANGE `from_abie_uuid` `from_abie_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key pointing to the ABIE table. FROM_ABIE_ID is basically  a parent data element (type) of the TO_ASBIEP_ID. FROM_ABIE_ID must be based on the FROM_ACC_ID in the BASED_ASCC_ID except when the FROM_ACC_ID refers to an SEMANTIC_GROUP ACC or USER_EXTENSION_GROUP ACC.',
    CHANGE `to_asbiep_uuid` `to_asbiep_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A foreign key to the ASBIEP table. TO_ASBIEP_ID is basically a child data element of the FROM_ABIE_ID. The TO_ASBIEP_ID must be based on the TO_ASCCP_ID in the BASED_ASCC_ID. the ASBIEP is reused with the OWNER_TOP_LEVEL_ASBIEP is different after joining ASBIE and ASBIEP tables',
    CHANGE `owner_top_level_asbiep_uuid` `owner_top_level_asbiep_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.';

ALTER TABLE `asbie_bizterm` CHANGE `asbie_uuid` `asbie_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'An internal ID of the associated ASBIE';

ALTER TABLE `asbiep`
    CHANGE `role_of_abie_uuid` `role_of_abie_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key pointing to the ABIE record. It is the ABIE, which the property term in the based ASCCP qualifies. Note that the ABIE has to be derived from the ACC used by the based ASCCP.',
    CHANGE `owner_top_level_asbiep_uuid` `owner_top_level_asbiep_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.';

ALTER TABLE `bbie`
    CHANGE `from_abie_uuid` `from_abie_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'A foreign key pointing to the ABIE table. FROM_ABIE_ID is basically  a parent data element (type) of the TO_ASBIEP_ID. FROM_ABIE_ID must be based on the FROM_ACC_ID in the BASED_ASCC_ID except when the FROM_ACC_ID refers to an SEMANTIC_GROUP ACC or USER_EXTENSION_GROUP ACC.FROM_ABIE_ID must be based on the FROM_ACC_ID in the BASED_BCC_ID.',
    CHANGE `to_bbiep_uuid` `to_bbiep_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'TO_BBIEP_ID is a foreign key to the BBIEP table. TO_BBIEP_ID basically refers to a child data element of the FROM_ABIE_ID. TO_BBIEP_ID must be based on the TO_BCCP_ID in the based BCC.',
    CHANGE `owner_top_level_asbiep_uuid` `owner_top_level_asbiep_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.';

ALTER TABLE `bbie_bizterm` CHANGE `bbie_uuid` `bbie_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'An internal ID of the associated BBIE';

ALTER TABLE `bbie_sc`
    CHANGE `bbie_uuid` `bbie_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'The BBIE this BBIE_SC applies to.',
    CHANGE `owner_top_level_asbiep_uuid` `owner_top_level_asbiep_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.';

ALTER TABLE `bbiep` CHANGE `owner_top_level_asbiep_uuid` `owner_top_level_asbiep_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.';

ALTER TABLE `bie_usage_rule`
    CHANGE `target_abie_uuid` `target_abie_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the ABIE table indicating the ABIE, to which the usage rule is applied.',
    CHANGE `target_asbie_uuid` `target_asbie_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the ASBIE table indicating the ASBIE, to which the usage rule is applied.',
    CHANGE `target_asbiep_uuid` `target_asbiep_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the ASBIEP table indicating the ASBIEP, to which the usage rule is applied.',
    CHANGE `target_bbie_uuid` `target_bbie_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the BBIE table indicating the BBIE, to which the usage rule is applied.',
    CHANGE `target_bbiep_uuid` `target_bbiep_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the BBIEP table indicating the ABIEP, to which the usage rule is applied.';

ALTER TABLE `bie_user_ext_revision`
    CHANGE `ext_abie_uuid` `ext_abie_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This points to an ABIE record corresponding to the EXTENSION_ACC_ID record. For example, this column can point to the ApplicationAreaExtension ABIE which is based on the ApplicationAreaExtension ACC (referred to by the EXT_ACC_ID column). This column can be NULL only when the extension is the AllExtension because there is no corresponding ABIE for the AllExtension ACC.',
    CHANGE `top_level_asbiep_uuid` `top_level_asbiep_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.';

ALTER TABLE `biz_ctx_assignment` CHANGE `top_level_asbiep_uuid` `top_level_asbiep_id` char(36) CHARACTER SET ascii NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.';

ALTER TABLE `top_level_asbiep` CHANGE `asbiep_uuid` `asbiep_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the ASBIEP table pointing to a record which is a top-level ASBIEP.';

-- Add foreign key constraints
ALTER TABLE `abie` ADD PRIMARY KEY (`abie_id`);
ALTER TABLE `asbie` ADD PRIMARY KEY (`asbie_id`);
ALTER TABLE `bbie` ADD PRIMARY KEY (`bbie_id`);
ALTER TABLE `asbiep` ADD PRIMARY KEY (`asbiep_id`);
ALTER TABLE `bbiep` ADD PRIMARY KEY (`bbiep_id`);
ALTER TABLE `bbie_sc` ADD PRIMARY KEY (`bbie_sc_id`);
ALTER TABLE `top_level_asbiep` ADD PRIMARY KEY (`top_level_asbiep_id`);

ALTER TABLE `abie` ADD CONSTRAINT `abie_owner_top_level_asbiep_id_fk` FOREIGN KEY (`owner_top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

ALTER TABLE `asbie`
    ADD CONSTRAINT `asbie_from_abie_id_fk` FOREIGN KEY (`from_abie_id`) REFERENCES `abie` (`abie_id`),
    ADD CONSTRAINT `asbie_to_asbiep_id_fk` FOREIGN KEY (`to_asbiep_id`) REFERENCES `asbiep` (`asbiep_id`),
    ADD CONSTRAINT `asbie_owner_top_level_asbiep_id_fk` FOREIGN KEY (`owner_top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

ALTER TABLE `asbie_bizterm` ADD CONSTRAINT `asbie_bizterm_asbie_id_fk` FOREIGN KEY (`asbie_id`) REFERENCES `asbie` (`asbie_id`);

ALTER TABLE `asbiep`
    ADD CONSTRAINT `asbiep_role_of_abie_id_fk` FOREIGN KEY (`role_of_abie_id`) REFERENCES `abie` (`abie_id`),
    ADD CONSTRAINT `asbiep_owner_top_level_asbiep_id_fk` FOREIGN KEY (`owner_top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

ALTER TABLE `bbie`
    ADD CONSTRAINT `bbie_from_abie_id_fk` FOREIGN KEY (`from_abie_id`) REFERENCES `abie` (`abie_id`),
    ADD CONSTRAINT `bbie_to_bbiep_id_fk` FOREIGN KEY (`to_bbiep_id`) REFERENCES `bbiep` (`bbiep_id`),
    ADD CONSTRAINT `bbie_owner_top_level_asbiep_id_fk` FOREIGN KEY (`owner_top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

ALTER TABLE `bbie_bizterm` ADD CONSTRAINT `bbie_bizterm_bbie_id_fk` FOREIGN KEY (`bbie_id`) REFERENCES `bbie` (`bbie_id`);

ALTER TABLE `bbie_sc`
    ADD CONSTRAINT `bbie_sc_bbie_id_fk` FOREIGN KEY (`bbie_id`) REFERENCES `bbie` (`bbie_id`),
    ADD CONSTRAINT `bbie_sc_owner_top_level_asbiep_id_fk` FOREIGN KEY (`owner_top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

ALTER TABLE `bbiep` ADD CONSTRAINT `bbiep_owner_top_level_asbiep_id_fk` FOREIGN KEY (`owner_top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

ALTER TABLE `bie_usage_rule`
    ADD CONSTRAINT `bie_usage_rule_target_abie_id_fk` FOREIGN KEY (`target_abie_id`) REFERENCES `abie` (`abie_id`),
    ADD CONSTRAINT `bie_usage_rule_target_asbie_id_fk` FOREIGN KEY (`target_asbie_id`) REFERENCES `asbie` (`asbie_id`),
    ADD CONSTRAINT `bie_usage_rule_target_asbiep_id_fk` FOREIGN KEY (`target_asbiep_id`) REFERENCES `asbiep` (`asbiep_id`),
    ADD CONSTRAINT `bie_usage_rule_target_bbie_id_fk` FOREIGN KEY (`target_bbie_id`) REFERENCES `bbie` (`bbie_id`),
    ADD CONSTRAINT `bie_usage_rule_target_bbiep_id_fk` FOREIGN KEY (`target_bbiep_id`) REFERENCES `bbiep` (`bbiep_id`);

ALTER TABLE `bie_user_ext_revision`
    ADD CONSTRAINT `bie_user_ext_revision_ext_abie_id_fk` FOREIGN KEY (`ext_abie_id`) REFERENCES `abie` (`abie_id`),
    ADD CONSTRAINT `bie_user_ext_revision_top_level_asbiep_id_fk` FOREIGN KEY (`top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

ALTER TABLE `biz_ctx_assignment`
    ADD CONSTRAINT `biz_ctx_assignment_top_level_asbiep_id_fk` FOREIGN KEY (`top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);
ALTER TABLE `biz_ctx_assignment` ADD UNIQUE KEY `biz_ctx_assignment_uk` (`biz_ctx_id`, `top_level_asbiep_id`);

ALTER TABLE `top_level_asbiep` ADD CONSTRAINT `top_level_asbiep_asbiep_id_fk` FOREIGN KEY (`asbiep_id`) REFERENCES `asbiep` (`asbiep_id`);

-- ------------------------------------------------------------------------------------
-- Change `app_oauth2_user_id`, `asbie_bizterm_id`, `ascc_bizterm_id`,               --
-- `bbie_bizterm_id`, `bcc_bizterm_id`, `bdt_pri_restri_id`, `bdt_sc_pri_restri_id`, --
-- `bie_usage_rule_id`, `bie_user_ext_revision_id`, `business_term_id`,              --
-- `cc_tag_id`, `cdt_ref_spec_id`, `cdt_sc_ref_spec_id`, `comment_id`,               --
-- `dt_usage_rule_id`, `exception_id`, `message_id`, `oauth2_app_id`,                --
-- `oauth2_app_scope_id`, `ref_spec_id`, `usage_rule_id`, `usage_rule_expression_id` --
--                                                                          TO UUID  --
-- ------------------------------------------------------------------------------------
ALTER TABLE `app_oauth2_user` ADD COLUMN `app_oauth2_user_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `app_oauth2_user_id`;
CALL update_uuid('app_oauth2_user');

ALTER TABLE `asbie_bizterm` ADD COLUMN `asbie_bizterm_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `asbie_bizterm_id`;
CALL update_uuid('asbie_bizterm');

ALTER TABLE `ascc_bizterm` ADD COLUMN `ascc_bizterm_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `ascc_bizterm_id`;
CALL update_uuid('ascc_bizterm');

ALTER TABLE `bbie_bizterm` ADD COLUMN `bbie_bizterm_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `bbie_bizterm_id`;
CALL update_uuid('bbie_bizterm');

ALTER TABLE `bcc_bizterm` ADD COLUMN `bcc_bizterm_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `bcc_bizterm_id`;
CALL update_uuid('bcc_bizterm');

ALTER TABLE `bdt_pri_restri` ADD COLUMN `bdt_pri_restri_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `bdt_pri_restri_id`;
CALL update_uuid('bdt_pri_restri');

ALTER TABLE `bdt_sc_pri_restri` ADD COLUMN `bdt_sc_pri_restri_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `bdt_sc_pri_restri_id`;
CALL update_uuid('bdt_sc_pri_restri');

ALTER TABLE `bie_usage_rule` ADD COLUMN `bie_usage_rule_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `bie_usage_rule_id`;
CALL update_uuid('bie_usage_rule');

ALTER TABLE `bie_user_ext_revision` ADD COLUMN `bie_user_ext_revision_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `bie_user_ext_revision_id`;
CALL update_uuid('bie_user_ext_revision');

ALTER TABLE `business_term` ADD COLUMN `business_term_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `business_term_id`;
CALL update_uuid('business_term');

ALTER TABLE `cc_tag` ADD COLUMN `cc_tag_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `cc_tag_id`;
CALL update_uuid('cc_tag');

ALTER TABLE `cdt_ref_spec` ADD COLUMN `cdt_ref_spec_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `cdt_ref_spec_id`;
CALL update_uuid('cdt_ref_spec');

ALTER TABLE `cdt_sc_ref_spec` ADD COLUMN `cdt_sc_ref_spec_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `cdt_sc_ref_spec_id`;
CALL update_uuid('cdt_sc_ref_spec');

ALTER TABLE `comment` ADD COLUMN `comment_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `comment_id`;
CALL update_uuid('comment');

ALTER TABLE `dt_usage_rule` ADD COLUMN `dt_usage_rule_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `dt_usage_rule_id`;
CALL update_uuid('dt_usage_rule');

ALTER TABLE `exception` ADD COLUMN `exception_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `exception_id`;
CALL update_uuid('exception');

ALTER TABLE `message` ADD COLUMN `message_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `message_id`;
CALL update_uuid('message');

ALTER TABLE `oauth2_app` ADD COLUMN `oauth2_app_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `oauth2_app_id`;
CALL update_uuid('oauth2_app');

ALTER TABLE `oauth2_app_scope` ADD COLUMN `oauth2_app_scope_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `oauth2_app_scope_id`;
CALL update_uuid('oauth2_app_scope');

ALTER TABLE `ref_spec` ADD COLUMN `ref_spec_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `ref_spec_id`;
CALL update_uuid('ref_spec');

UPDATE `ref_spec` SET `ref_spec_uuid` = '7294fd33-8315-467b-9416-783c32551a15' WHERE `spec` = 'CCTS DT v3.1';

ALTER TABLE `usage_rule` ADD COLUMN `usage_rule_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `usage_rule_id`;
CALL update_uuid('usage_rule');

ALTER TABLE `usage_rule_expression` ADD COLUMN `usage_rule_expression_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.' AFTER `usage_rule_expression_id`;
CALL update_uuid('usage_rule_expression');

ALTER TABLE `acc_manifest_tag` ADD COLUMN `cc_tag_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `cc_tag_id`;
UPDATE `acc_manifest_tag`, `cc_tag` SET `acc_manifest_tag`.`cc_tag_uuid` = `cc_tag`.`cc_tag_uuid`
WHERE `acc_manifest_tag`.`cc_tag_id` = `cc_tag`.`cc_tag_id`;

ALTER TABLE `app_oauth2_user` ADD COLUMN `oauth2_app_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A reference to the record in `oauth2_app`.' AFTER `oauth2_app_id`;
UPDATE `app_oauth2_user`, `oauth2_app` SET `app_oauth2_user`.`oauth2_app_uuid` = `oauth2_app`.`oauth2_app_uuid`
WHERE `app_oauth2_user`.`oauth2_app_id` = `oauth2_app`.`oauth2_app_id`;

ALTER TABLE `asbie_bizterm` ADD COLUMN `ascc_bizterm_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'An internal ID of the ascc_business_term record.' AFTER `ascc_bizterm_id`;
UPDATE `asbie_bizterm`, `ascc_bizterm` SET `asbie_bizterm`.`ascc_bizterm_uuid` = `ascc_bizterm`.`ascc_bizterm_uuid`
WHERE `asbie_bizterm`.`ascc_bizterm_id` = `ascc_bizterm`.`ascc_bizterm_id`;

ALTER TABLE `ascc_bizterm` ADD COLUMN `business_term_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'An internal ID of the associated business term.' AFTER `business_term_id`;
UPDATE `ascc_bizterm`, `business_term` SET `ascc_bizterm`.`business_term_uuid` = `business_term`.`business_term_uuid`
WHERE `ascc_bizterm`.`business_term_id` = `business_term`.`business_term_id`;

ALTER TABLE `asccp_manifest_tag` ADD COLUMN `cc_tag_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `cc_tag_id`;
UPDATE `asccp_manifest_tag`, `cc_tag` SET `asccp_manifest_tag`.`cc_tag_uuid` = `cc_tag`.`cc_tag_uuid`
WHERE `asccp_manifest_tag`.`cc_tag_id` = `cc_tag`.`cc_tag_id`;

ALTER TABLE `bbie` ADD COLUMN `bdt_pri_restri_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is the foreign key to the BDT_PRI_RESTRI table. It indicates the primitive assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this specific association). This is assigned by the user who authors the BIE. The assignment would override the default from the CC side.' AFTER `bdt_pri_restri_id`;
UPDATE `bbie`, `bdt_pri_restri` SET `bbie`.`bdt_pri_restri_uuid` = `bdt_pri_restri`.`bdt_pri_restri_uuid`
WHERE `bbie`.`bdt_pri_restri_id` = `bdt_pri_restri`.`bdt_pri_restri_id`;

ALTER TABLE `bbie_bizterm` ADD COLUMN `bcc_bizterm_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'An internal ID of the bcc_business_term record.' AFTER `bcc_bizterm_id`;
UPDATE `bbie_bizterm`, `bcc_bizterm` SET `bbie_bizterm`.`bcc_bizterm_uuid` = `bcc_bizterm`.`bcc_bizterm_uuid`
WHERE `bbie_bizterm`.`bcc_bizterm_id` = `bcc_bizterm`.`bcc_bizterm_id`;

ALTER TABLE `bbie_sc` ADD COLUMN `dt_sc_pri_restri_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This must be one of the allowed primitive/code list as specified in the corresponding SC of the based BCC of the BBIE (referred to by the BBIE_ID column).\n\nIt is the foreign key to the BDT_SC_PRI_RESTRI table. It indicates the primitive assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this specific association). This is assigned by the user who authors the BIE. The assignment would override the default from the CC side.\n\nThis column, the CODE_LIST_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.' AFTER `dt_sc_pri_restri_id`;
UPDATE `bbie_sc`, `bdt_sc_pri_restri` SET `bbie_sc`.`dt_sc_pri_restri_uuid` = `bdt_sc_pri_restri`.`bdt_sc_pri_restri_uuid`
WHERE `bbie_sc`.`dt_sc_pri_restri_id` = `bdt_sc_pri_restri`.`bdt_sc_pri_restri_id`;

ALTER TABLE `bcc_bizterm` ADD COLUMN `business_term_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'An internal ID of the associated business term.' AFTER `business_term_id`;
UPDATE `bcc_bizterm`, `business_term` SET `bcc_bizterm`.`business_term_uuid` = `business_term`.`business_term_uuid`
WHERE `bcc_bizterm`.`business_term_id` = `business_term`.`business_term_id`;

ALTER TABLE `bccp_manifest_tag` ADD COLUMN `cc_tag_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `cc_tag_id`;
UPDATE `bccp_manifest_tag`, `cc_tag` SET `bccp_manifest_tag`.`cc_tag_uuid` = `cc_tag`.`cc_tag_uuid`
WHERE `bccp_manifest_tag`.`cc_tag_id` = `cc_tag`.`cc_tag_id`;

ALTER TABLE `bie_usage_rule` ADD COLUMN `assigned_usage_rule_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the USAGE_RULE table indicating the usage rule assigned to a BIE.' AFTER `assigned_usage_rule_id`;
UPDATE `bie_usage_rule`, `usage_rule` SET `bie_usage_rule`.`assigned_usage_rule_uuid` = `usage_rule`.`usage_rule_uuid`
WHERE `bie_usage_rule`.`assigned_usage_rule_id` = `usage_rule`.`usage_rule_id`;

ALTER TABLE `cdt_ref_spec` ADD COLUMN `ref_spec_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `ref_spec_id`;
UPDATE `cdt_ref_spec`, `ref_spec` SET `cdt_ref_spec`.`ref_spec_uuid` = `ref_spec`.`ref_spec_uuid`
WHERE `cdt_ref_spec`.`ref_spec_id` = `ref_spec`.`ref_spec_id`;

ALTER TABLE `cdt_sc_ref_spec` ADD COLUMN `ref_spec_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `ref_spec_id`;
UPDATE `cdt_sc_ref_spec`, `ref_spec` SET `cdt_sc_ref_spec`.`ref_spec_uuid` = `ref_spec`.`ref_spec_uuid`
WHERE `cdt_sc_ref_spec`.`ref_spec_id` = `ref_spec`.`ref_spec_id`;

ALTER TABLE `comment` ADD COLUMN `prev_comment_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `prev_comment_id`;
UPDATE `comment`, `comment` as tmp SET `comment`.`prev_comment_uuid` = tmp.`comment_uuid`
WHERE `comment`.`prev_comment_id` = tmp.`comment_id`;

ALTER TABLE `dt_manifest_tag` ADD COLUMN `cc_tag_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `cc_tag_id`;
UPDATE `dt_manifest_tag`, `cc_tag` SET `dt_manifest_tag`.`cc_tag_uuid` = `cc_tag`.`cc_tag_uuid`
WHERE `dt_manifest_tag`.`cc_tag_id` = `cc_tag`.`cc_tag_id`;

ALTER TABLE `dt_usage_rule` ADD COLUMN `assigned_usage_rule_uuid` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the USAGE_RULE table indicating the usage rule assigned to a BIE.' AFTER `assigned_usage_rule_id`;
UPDATE `dt_usage_rule`, `usage_rule` SET `dt_usage_rule`.`assigned_usage_rule_uuid` = `usage_rule`.`usage_rule_uuid`
WHERE `dt_usage_rule`.`assigned_usage_rule_id` = `usage_rule`.`usage_rule_id`;

ALTER TABLE `oauth2_app_scope` ADD COLUMN `oauth2_app_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `oauth2_app_id`;
UPDATE `oauth2_app_scope`, `oauth2_app` SET `oauth2_app_scope`.`oauth2_app_uuid` = `oauth2_app`.`oauth2_app_uuid`
WHERE `oauth2_app_scope`.`oauth2_app_id` = `oauth2_app`.`oauth2_app_id`;

ALTER TABLE `usage_rule_expression` ADD COLUMN `represented_usage_rule_uuid` char(36) CHARACTER SET ascii DEFAULT NULL AFTER `represented_usage_rule_id`;
UPDATE `usage_rule_expression`, `usage_rule` SET `usage_rule_expression`.`represented_usage_rule_uuid` = `usage_rule`.`usage_rule_uuid`
WHERE `usage_rule_expression`.`represented_usage_rule_id` = `usage_rule`.`usage_rule_id`;

-- Drop old `*_id` columns
ALTER TABLE `acc_manifest_tag` DROP FOREIGN KEY `acc_manifest_tag_cc_tag_id_fk`;
ALTER TABLE `acc_manifest_tag` DROP COLUMN `cc_tag_id`;

ALTER TABLE `app_oauth2_user` DROP FOREIGN KEY `app_oauth2_user_oauth2_app_id_fk`;
ALTER TABLE `app_oauth2_user` DROP COLUMN `oauth2_app_id`;

ALTER TABLE `asbie_bizterm` DROP FOREIGN KEY `asbie_bizterm_ascc_bizterm_fk`;
ALTER TABLE `asbie_bizterm` DROP COLUMN `ascc_bizterm_id`;

ALTER TABLE `ascc_bizterm` DROP FOREIGN KEY `ascc_bizterm_business_term_fk`;
ALTER TABLE `ascc_bizterm` DROP COLUMN `business_term_id`;

ALTER TABLE `asccp_manifest_tag` DROP FOREIGN KEY `asccp_manifest_tag_cc_tag_id_fk`;
ALTER TABLE `asccp_manifest_tag` DROP COLUMN `cc_tag_id`;

ALTER TABLE `bbie` DROP FOREIGN KEY `bbie_bdt_pri_restri_id_fk`;
ALTER TABLE `bbie` DROP COLUMN `bdt_pri_restri_id`;

ALTER TABLE `bbie_bizterm` DROP FOREIGN KEY `bbie_bizterm_bcc_bizterm_fk`;
ALTER TABLE `bbie_bizterm` DROP COLUMN `bcc_bizterm_id`;

ALTER TABLE `bbie_sc` DROP FOREIGN KEY `bbie_sc_dt_sc_pri_restri_id_fk`;
ALTER TABLE `bbie_sc` DROP COLUMN `dt_sc_pri_restri_id`;

ALTER TABLE `bcc_bizterm` DROP FOREIGN KEY `bcc_bizterm_business_term_fk`;
ALTER TABLE `bcc_bizterm` DROP COLUMN `business_term_id`;

ALTER TABLE `bccp_manifest_tag` DROP FOREIGN KEY `bccp_manifest_tag_cc_tag_id_fk`;
ALTER TABLE `bccp_manifest_tag` DROP COLUMN `cc_tag_id`;

ALTER TABLE `bie_usage_rule` DROP FOREIGN KEY `bie_usage_rule_assigned_usage_rule_id_fk`;
ALTER TABLE `bie_usage_rule` DROP COLUMN `assigned_usage_rule_id`;

ALTER TABLE `cdt_ref_spec` DROP FOREIGN KEY `cdt_ref_spec_ref_spec_id_fk`;
ALTER TABLE `cdt_ref_spec` DROP COLUMN `ref_spec_id`;

ALTER TABLE `cdt_sc_ref_spec` DROP FOREIGN KEY `cdt_sc_ref_spec_ref_spec_id_fk`;
ALTER TABLE `cdt_sc_ref_spec` DROP COLUMN `ref_spec_id`;

ALTER TABLE `comment` DROP FOREIGN KEY `comment_prev_comment_id_fk`;
ALTER TABLE `comment` DROP COLUMN `prev_comment_id`;

ALTER TABLE `dt_manifest_tag` DROP FOREIGN KEY `dt_manifest_tag_cc_tag_id_fk`;
ALTER TABLE `dt_manifest_tag` DROP COLUMN `cc_tag_id`;

ALTER TABLE `dt_usage_rule` DROP FOREIGN KEY `dt_usage_rule_assigned_usage_rule_id_fk`;
ALTER TABLE `dt_usage_rule` DROP COLUMN `assigned_usage_rule_id`;

ALTER TABLE `oauth2_app_scope` DROP FOREIGN KEY `oauth2_app_scope_oauth2_app_id_fk`;
ALTER TABLE `oauth2_app_scope` DROP COLUMN `oauth2_app_id`;

ALTER TABLE `usage_rule_expression` DROP FOREIGN KEY `usage_rule_expression_represented_usage_rule_id_fk`;
ALTER TABLE `usage_rule_expression` DROP COLUMN `represented_usage_rule_id`;

ALTER TABLE `app_oauth2_user` DROP COLUMN `app_oauth2_user_id`;
ALTER TABLE `asbie_bizterm` DROP COLUMN `asbie_bizterm_id`;
ALTER TABLE `ascc_bizterm` DROP COLUMN `ascc_bizterm_id`;
ALTER TABLE `bbie_bizterm` DROP COLUMN `bbie_bizterm_id`;
ALTER TABLE `bcc_bizterm` DROP COLUMN `bcc_bizterm_id`;
ALTER TABLE `bdt_pri_restri` DROP COLUMN `bdt_pri_restri_id`;
ALTER TABLE `bdt_sc_pri_restri` DROP COLUMN `bdt_sc_pri_restri_id`;
ALTER TABLE `bie_usage_rule` DROP COLUMN `bie_usage_rule_id`;
ALTER TABLE `bie_user_ext_revision` DROP COLUMN `bie_user_ext_revision_id`;
ALTER TABLE `business_term` DROP COLUMN `business_term_id`;
ALTER TABLE `cc_tag` DROP COLUMN `cc_tag_id`;
ALTER TABLE `cdt_ref_spec` DROP COLUMN `cdt_ref_spec_id`;
ALTER TABLE `cdt_sc_ref_spec` DROP COLUMN `cdt_sc_ref_spec_id`;
ALTER TABLE `comment` DROP COLUMN `comment_id`;
ALTER TABLE `dt_usage_rule` DROP COLUMN `dt_usage_rule_id`;
ALTER TABLE `exception` DROP COLUMN `exception_id`;
ALTER TABLE `message` DROP COLUMN `message_id`;
ALTER TABLE `oauth2_app` DROP COLUMN `oauth2_app_id`;
ALTER TABLE `oauth2_app_scope` DROP COLUMN `oauth2_app_scope_id`;
ALTER TABLE `ref_spec` DROP COLUMN `ref_spec_id`;
ALTER TABLE `usage_rule` DROP COLUMN `usage_rule_id`;
ALTER TABLE `usage_rule_expression` DROP COLUMN `usage_rule_expression_id`;

-- Rename `*_uuid` TO `*_id`
ALTER TABLE `app_oauth2_user` CHANGE `app_oauth2_user_uuid` `app_oauth2_user_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `asbie_bizterm` CHANGE `asbie_bizterm_uuid` `asbie_bizterm_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `ascc_bizterm` CHANGE `ascc_bizterm_uuid` `ascc_bizterm_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bbie_bizterm` CHANGE `bbie_bizterm_uuid` `bbie_bizterm_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bcc_bizterm` CHANGE `bcc_bizterm_uuid` `bcc_bizterm_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bdt_pri_restri` CHANGE `bdt_pri_restri_uuid` `bdt_pri_restri_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bdt_sc_pri_restri` CHANGE `bdt_sc_pri_restri_uuid` `bdt_sc_pri_restri_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bie_usage_rule` CHANGE `bie_usage_rule_uuid` `bie_usage_rule_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `bie_user_ext_revision` CHANGE `bie_user_ext_revision_uuid` `bie_user_ext_revision_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `business_term` CHANGE `business_term_uuid` `business_term_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `cc_tag` CHANGE `cc_tag_uuid` `cc_tag_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `cdt_ref_spec` CHANGE `cdt_ref_spec_uuid` `cdt_ref_spec_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `cdt_sc_ref_spec` CHANGE `cdt_sc_ref_spec_uuid` `cdt_sc_ref_spec_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `comment` CHANGE `comment_uuid` `comment_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `dt_usage_rule` CHANGE `dt_usage_rule_uuid` `dt_usage_rule_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `exception` CHANGE `exception_uuid` `exception_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `message` CHANGE `message_uuid` `message_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `oauth2_app` CHANGE `oauth2_app_uuid` `oauth2_app_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `oauth2_app_scope` CHANGE `oauth2_app_scope_uuid` `oauth2_app_scope_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `ref_spec` CHANGE `ref_spec_uuid` `ref_spec_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `usage_rule` CHANGE `usage_rule_uuid` `usage_rule_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';
ALTER TABLE `usage_rule_expression` CHANGE `usage_rule_expression_uuid` `usage_rule_expression_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Primary, internal database key.';

ALTER TABLE `acc_manifest_tag` CHANGE `cc_tag_uuid` `cc_tag_id` char(36) CHARACTER SET ascii DEFAULT NULL;
ALTER TABLE `app_oauth2_user` CHANGE `oauth2_app_uuid` `oauth2_app_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'A reference to the record in `oauth2_app`.';
ALTER TABLE `asbie_bizterm` CHANGE `ascc_bizterm_uuid` `ascc_bizterm_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'An internal ID of the ascc_business_term record.';
ALTER TABLE `ascc_bizterm` CHANGE `business_term_uuid` `business_term_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'An internal ID of the associated business term.';
ALTER TABLE `asccp_manifest_tag` CHANGE `cc_tag_uuid` `cc_tag_id` char(36) CHARACTER SET ascii DEFAULT NULL;
ALTER TABLE `bbie` CHANGE `bdt_pri_restri_uuid` `bdt_pri_restri_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This is the foreign key to the BDT_PRI_RESTRI table. It indicates the primitive assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this specific association). This is assigned by the user who authors the BIE. The assignment would override the default from the CC side.';
ALTER TABLE `bbie_bizterm` CHANGE `bcc_bizterm_uuid` `bcc_bizterm_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'An internal ID of the bcc_business_term record.';
ALTER TABLE `bbie_sc` CHANGE `dt_sc_pri_restri_uuid` `dt_sc_pri_restri_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'This must be one of the allowed primitive/code list as specified in the corresponding SC of the based BCC of the BBIE (referred to by the BBIE_ID column).\n\nIt is the foreign key to the BDT_SC_PRI_RESTRI table. It indicates the primitive assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this specific association). This is assigned by the user who authors the BIE. The assignment would override the default from the CC side.\n\nThis column, the CODE_LIST_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.';
ALTER TABLE `bcc_bizterm` CHANGE `business_term_uuid` `business_term_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'An internal ID of the associated business term.';
ALTER TABLE `bccp_manifest_tag` CHANGE `cc_tag_uuid` `cc_tag_id` char(36) CHARACTER SET ascii DEFAULT NULL;
ALTER TABLE `bie_usage_rule` CHANGE `assigned_usage_rule_uuid` `assigned_usage_rule_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the USAGE_RULE table indicating the usage rule assigned to a BIE.';
ALTER TABLE `cdt_ref_spec` CHANGE `ref_spec_uuid` `ref_spec_id` char(36) CHARACTER SET ascii DEFAULT NULL;
ALTER TABLE `cdt_sc_ref_spec` CHANGE `ref_spec_uuid` `ref_spec_id` char(36) CHARACTER SET ascii DEFAULT NULL;
ALTER TABLE `comment` CHANGE `prev_comment_uuid` `prev_comment_id` char(36) CHARACTER SET ascii DEFAULT NULL;
ALTER TABLE `dt_manifest_tag` CHANGE `cc_tag_uuid` `cc_tag_id` char(36) CHARACTER SET ascii DEFAULT NULL;
ALTER TABLE `dt_usage_rule` CHANGE `assigned_usage_rule_uuid` `assigned_usage_rule_id` char(36) CHARACTER SET ascii DEFAULT NULL COMMENT 'Foreign key to the USAGE_RULE table indicating the usage rule assigned to a BIE.';
ALTER TABLE `oauth2_app_scope` CHANGE `oauth2_app_uuid` `oauth2_app_id` char(36) CHARACTER SET ascii DEFAULT NULL;
ALTER TABLE `usage_rule_expression` CHANGE `represented_usage_rule_uuid` `represented_usage_rule_id` char(36) CHARACTER SET ascii DEFAULT NULL;

-- Add foreign key constraints
ALTER TABLE `app_oauth2_user` ADD PRIMARY KEY (`app_oauth2_user_id`);
ALTER TABLE `asbie_bizterm` ADD PRIMARY KEY (`asbie_bizterm_id`);
ALTER TABLE `ascc_bizterm` ADD PRIMARY KEY (`ascc_bizterm_id`);
ALTER TABLE `bbie_bizterm` ADD PRIMARY KEY (`bbie_bizterm_id`);
ALTER TABLE `bcc_bizterm` ADD PRIMARY KEY (`bcc_bizterm_id`);
ALTER TABLE `bdt_pri_restri` ADD PRIMARY KEY (`bdt_pri_restri_id`);
ALTER TABLE `bdt_sc_pri_restri` ADD PRIMARY KEY (`bdt_sc_pri_restri_id`);
ALTER TABLE `bie_usage_rule` ADD PRIMARY KEY (`bie_usage_rule_id`);
ALTER TABLE `bie_user_ext_revision` ADD PRIMARY KEY (`bie_user_ext_revision_id`);
ALTER TABLE `business_term` ADD PRIMARY KEY (`business_term_id`);
ALTER TABLE `cc_tag` ADD PRIMARY KEY (`cc_tag_id`);
ALTER TABLE `cdt_ref_spec` ADD PRIMARY KEY (`cdt_ref_spec_id`);
ALTER TABLE `cdt_sc_ref_spec` ADD PRIMARY KEY (`cdt_sc_ref_spec_id`);
ALTER TABLE `comment` ADD PRIMARY KEY (`comment_id`);
ALTER TABLE `dt_usage_rule` ADD PRIMARY KEY (`dt_usage_rule_id`);
ALTER TABLE `exception` ADD PRIMARY KEY (`exception_id`);
ALTER TABLE `message` ADD PRIMARY KEY (`message_id`);
ALTER TABLE `oauth2_app` ADD PRIMARY KEY (`oauth2_app_id`);
ALTER TABLE `oauth2_app_scope` ADD PRIMARY KEY (`oauth2_app_scope_id`);
ALTER TABLE `ref_spec` ADD PRIMARY KEY (`ref_spec_id`);
ALTER TABLE `usage_rule` ADD PRIMARY KEY (`usage_rule_id`);
ALTER TABLE `usage_rule_expression` ADD PRIMARY KEY (`usage_rule_expression_id`);

ALTER TABLE `acc_manifest_tag` ADD CONSTRAINT `acc_manifest_tag_cc_tag_id_fk` FOREIGN KEY (`cc_tag_id`) REFERENCES `cc_tag` (`cc_tag_id`);
ALTER TABLE `app_oauth2_user` ADD CONSTRAINT `app_oauth2_user_oauth2_app_id_fk` FOREIGN KEY (`oauth2_app_id`) REFERENCES `oauth2_app` (`oauth2_app_id`);
ALTER TABLE `asbie_bizterm` ADD CONSTRAINT `asbie_bizterm_ascc_bizterm_id_fk` FOREIGN KEY (`ascc_bizterm_id`) REFERENCES `ascc_bizterm` (`ascc_bizterm_id`);
ALTER TABLE `ascc_bizterm` ADD CONSTRAINT `ascc_bizterm_business_term_id_fk` FOREIGN KEY (`business_term_id`) REFERENCES `business_term` (`business_term_id`);
ALTER TABLE `asccp_manifest_tag` ADD CONSTRAINT `asccp_manifest_tag_cc_tag_id_fk` FOREIGN KEY (`cc_tag_id`) REFERENCES `cc_tag` (`cc_tag_id`);
ALTER TABLE `bbie` ADD CONSTRAINT `bbie_bdt_pri_restri_id_fk` FOREIGN KEY (`bdt_pri_restri_id`) REFERENCES `bdt_pri_restri` (`bdt_pri_restri_id`);
ALTER TABLE `bbie_bizterm` ADD CONSTRAINT `bbie_bizterm_bcc_bizterm_id_fk` FOREIGN KEY (`bcc_bizterm_id`) REFERENCES `bcc_bizterm` (`bcc_bizterm_id`);
ALTER TABLE `bbie_sc` ADD CONSTRAINT `bbie_sc_dt_sc_pri_restri_id_fk` FOREIGN KEY (`dt_sc_pri_restri_id`) REFERENCES `bdt_sc_pri_restri` (`bdt_sc_pri_restri_id`);
ALTER TABLE `bcc_bizterm` ADD CONSTRAINT `bcc_bizterm_business_term_id_fk` FOREIGN KEY (`business_term_id`) REFERENCES `business_term` (`business_term_id`);
ALTER TABLE `bccp_manifest_tag` ADD CONSTRAINT `bccp_manifest_tag_cc_tag_id_fk` FOREIGN KEY (`cc_tag_id`) REFERENCES `cc_tag` (`cc_tag_id`);
ALTER TABLE `bie_usage_rule` ADD CONSTRAINT `bie_usage_rule_assigned_usage_rule_id_fk` FOREIGN KEY (`assigned_usage_rule_id`) REFERENCES `usage_rule` (`usage_rule_id`);
ALTER TABLE `cdt_ref_spec` ADD CONSTRAINT `cdt_ref_spec_ref_spec_id_fk` FOREIGN KEY (`ref_spec_id`) REFERENCES `ref_spec` (`ref_spec_id`);
ALTER TABLE `cdt_sc_ref_spec` ADD CONSTRAINT `cdt_sc_ref_spec_ref_spec_id_fk` FOREIGN KEY (`ref_spec_id`) REFERENCES `ref_spec` (`ref_spec_id`);
ALTER TABLE `comment` ADD CONSTRAINT `comment_prev_comment_id_fk` FOREIGN KEY (`prev_comment_id`) REFERENCES `comment` (`comment_id`);
ALTER TABLE `dt_manifest_tag` ADD CONSTRAINT `dt_manifest_tag_cc_tag_id_fk` FOREIGN KEY (`cc_tag_id`) REFERENCES `cc_tag` (`cc_tag_id`);
ALTER TABLE `dt_usage_rule` ADD CONSTRAINT `dt_usage_rule_assigned_usage_rule_id_fk` FOREIGN KEY (`assigned_usage_rule_id`) REFERENCES `usage_rule` (`usage_rule_id`);
ALTER TABLE `oauth2_app_scope` ADD CONSTRAINT `oauth2_app_scope_oauth2_app_id_fk` FOREIGN KEY (`oauth2_app_id`) REFERENCES `oauth2_app` (`oauth2_app_id`);
ALTER TABLE `usage_rule_expression` ADD CONSTRAINT `usage_rule_expression_represented_usage_rule_id_fk` FOREIGN KEY (`represented_usage_rule_id`) REFERENCES `usage_rule` (`usage_rule_id`);