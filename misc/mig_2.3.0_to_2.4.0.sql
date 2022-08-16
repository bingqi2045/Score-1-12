-- ----------------------------------------------------
-- Migration script for Score v2.4.0                 --
--                                                   --
-- Author: Hakju Oh <hakju.oh@nist.gov>              --
--         Dimitrije Milenkovic                      --
--                <dimitrije.milenkovic@nist.gov>    --
-- ----------------------------------------------------

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
    DECLARE currentRow INT;

    SET @sql = CONCAT('SELECT COUNT(*) INTO @rowCount FROM ', table_name);
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    SET currentRow = 0;

    updateLoop: WHILE (currentRow < @rowCount) DO
            SET @sql = CONCAT('UPDATE ', table_name, ' SET ', table_name, '_uuid = uuid_v4s() WHERE ', table_name, '_uuid IS NULL LIMIT 1');
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
            SET currentRow = currentRow + 1;
        END WHILE updateLoop;
END//
DELIMITER ;

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
    ('0d6f0c93-0222-47aa-952c-1c10c9e69883', '3395a2f88a397a2e85345fee3ec1746e', 'non positive integer', 'xsd:nonPositiveInteger', '{\"type\":\"number\", \"multipleOf\":1, \"maximum\":0, \"exclusiveMaximum\":false}', '{\"type\":\"integer\", \"maximum\":0, \"exclusiveMaximum\":false}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'non positive integer' AND subtype.`name` = 'integer';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('1dce4776-e435-41cc-9941-372c6ab0754c', 'f9044b20770e252037c894464e8e18df', 'negative integer', 'xsd:negativeInteger', '{\"type\":\"number\", \"multipleOf\":1, \"maximum\":0, \"exclusiveMaximum\":true}', '{\"type\":\"integer\", \"maximum\":0, \"exclusiveMaximum\":true}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'negative integer' AND subtype.`name` = 'non positive integer';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('010e4563-02d8-4715-acb6-5d7227988ca7', 'd484e5046be205ffe7ef0a84651ab595', 'long', 'xsd:long', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":-9223372036854775808, \"maximum\":9223372036854775807}', '{\"type\":\"integer\", \"minimum\":-9223372036854775808, \"maximum\":9223372036854775807}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'long' AND subtype.`name` = 'integer';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('c22e47f7-66db-433a-a1b4-9b1a151983b3', '8278dee5e8106cc9e2e02b4f6ee4c85f', 'int', 'xsd:int', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":-2147483648, \"maximum\":2147483647}', '{\"type\":\"integer\", \"minimum\":-2147483648, \"maximum\":2147483647}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'int' AND subtype.`name` = 'long';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('9895d011-6625-41f8-a01c-29692ae323e9', 'c776d8ccc5050987b0af9873a2ef95ab', 'short', 'xsd:short', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":-32768, \"maximum\":32767}', '{\"type\":\"integer\", \"minimum\":-32768, \"maximum\":32767}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'short' AND subtype.`name` = 'int';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('eba1be95-9fc1-4cd8-88c9-75e3c61fcddb', '9aa5f0beeb9d08109e0f6834790ab9a4', 'byte', 'xsd:byte', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":-128, \"maximum\":127}', '{\"type\":\"integer\", \"minimum\":-128, \"maximum\":127}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'byte' AND subtype.`name` = 'short';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('5dcfb77e-2869-4f77-9462-250ce7b7c492', 'a54e126ffd199e8df4bb44bc903926e8', 'unsigned long', 'xsd:unsignedLong', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":0, \"maximum\":18446744073709551615}', '{\"type\":\"integer\", \"minimum\":0, \"maximum\":18446744073709551615}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'unsigned long' AND subtype.`name` = 'non negative integer';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('65ead9cc-35eb-47fc-9006-078df7cfa32a', '12c448fbbe86ca3b7e93a7df771e0b94', 'unsigned int', 'xsd:unsignedInt', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":0, \"maximum\":4294967295}', '{\"type\":\"integer\", \"minimum\":0, \"maximum\":4294967295}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'unsigned int' AND subtype.`name` = 'unsigned long';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('2877cf6b-357b-4e0a-95f5-1e91413638d2', 'a422d1c18d382571e92ab3b0f85c37f7', 'unsigned short', 'xsd:unsignedShort', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":0, \"maximum\":65535}', '{\"type\":\"integer\", \"minimum\":0, \"maximum\":65535}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'unsigned short' AND subtype.`name` = 'unsigned int';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('8c093c3b-1216-401d-bda9-bdb450e02bad', '8451efe9c107d51a33ea623668afaa4a', 'unsigned byte', 'xsd:unsignedByte', '{\"type\":\"number\", \"multipleOf\":1, \"minimum\":0, \"maximum\":255}', '{\"type\":\"integer\", \"minimum\":0, \"maximum\":255}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'unsigned byte' AND subtype.`name` = 'unsigned short';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('7fd4451b-6ad1-4930-8570-34c67cb66081', '4ee4e35beded482a401d14879671cf2b', 'name', 'xsd:Name', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'name' AND subtype.`name` = 'token';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('d5af9325-d1c8-4bd0-8583-8ac06b4956d2', '954467cfde776fc6d45ed393de47ecff', 'non-colonized name', 'xsd:NCName', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'non-colonized name' AND subtype.`name` = 'name';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('98a97e1c-5d7c-429d-851e-2e0516015b7b', 'aafa09d5c14648f3fbd90808944d9f75', 'name token', 'xsd:NMTOKEN', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'name token' AND subtype.`name` = 'token';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('de57e550-fcaf-481d-b8a3-6b301ee48988', 'd99546b1bc2acfd6e87c1ea1487f5a59', 'name tokens', 'xsd:NMTOKENS', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'name tokens' AND subtype.`name` = 'name token';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b', '864c5d84696bdd17f7b36f61b2d42a0e', 'identifier', 'xsd:ID', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'identifier' AND subtype.`name` = 'non-colonized name';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('fddb814a-96b9-4392-bd07-2af08233e5f1', 'b20c9e033be4afc6ef1b11659afc4103', 'identifier reference', 'xsd:IDREF', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'identifier reference' AND subtype.`name` = 'non-colonized name';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('a08258f8-0ee9-44a4-b937-323ef1bb907d', '6112fe6c7fe8978d26dfc39f45f3c04b', 'identifier references', 'xsd:IDREFS', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'identifier references' AND subtype.`name` = 'identifier reference';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('126c78b2-0fb7-4dc4-9cc0-881aa205f7f3', 'ec21905187690f7af17686e7214013d2', 'entity', 'xsd:ENTITY', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'entity' AND subtype.`name` = 'non-colonized name';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('06f9b3ea-7699-4355-a02d-4826c47a78a9', '31cbacbd62c00094596914a66bd2b175', 'entities', 'xsd:ENTITIES', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'entities' AND subtype.`name` = 'entity';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('1cdcbd74-1896-4f8c-b2ab-c68e47976e18', '13e5d1379633033e1d1f8671c62ffb61', 'qualified name', 'xsd:QName', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'qualified name' AND subtype.`name` = 'any simple type';

INSERT INTO `xbt` (`xbt_id`, `guid`, `name`, `builtIn_type`, `jbt_draft05_map`, `openapi30_map`, `schema_definition`, `revision_doc`, `state`, `created_by`, `owner_user_id`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `is_deprecated`)
VALUES
    ('18bc4b23-48da-426c-9986-2b4a7bc38f39', '169bb1ded95c8edc188262c21affb79c', 'notation', 'xsd:NOTATION', '{\"type\":\"string\"}', '{\"type\":\"string\"}', NULL, NULL, 3, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 0);
UPDATE `xbt`, `xbt` AS subtype SET `xbt`.`subtype_of_xbt_id` = subtype.`xbt_id` WHERE `xbt`.`name` = 'notation' AND subtype.`name` = 'any simple type';

INSERT INTO `log` (`log_id`, `hash`, `revision_num`, `revision_tracking_num`, `log_action`, `reference`, `snapshot`, `prev_log_id`, `next_log_id`, `created_by`, `creation_timestamp`)
VALUES
    (27001, 'c2ddce6fcb8bbc558a24bd31a04bac4b', 1, 1, 'Added', 'd484e5046be205ffe7ef0a84651ab595', '{\"guid\": \"d484e5046be205ffe7ef0a84651ab595\", \"name\": \"long\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"d484e5046be205ffe7ef0a84651ab595\", \"name\": \"long\", \"state\": 3, \"xbt_id\": \"010e4563-02d8-4715-acb6-5d7227988ca7\", \"created_by\": 1, \"builtIn_type\": \"xsd:long\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-9223372036854775808, \\\"maximum\\\":9223372036854775807}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-9223372036854775808, \\\"maximum\\\":9223372036854775807}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"2cfd15d4-3678-4e65-be38-92629ed0ae45\", \"creation_timestamp\": \"2022-08-12T20:00:51.276113\", \"last_update_timestamp\": \"2022-08-12T20:00:51.276113\"}, \"xbtManifest\": {\"xbt_id\": \"010e4563-02d8-4715-acb6-5d7227988ca7\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3156}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:long\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-9223372036854775808, \\\"maximum\\\":9223372036854775807}\", \"subTypeOfXbt\": {\"guid\": \"f3bc6f3ca44b47e9a9a66997fc5d3c2b\", \"name\": \"integer\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-9223372036854775808, \\\"maximum\\\":9223372036854775807}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.276113'),
    (27002, 'e9ed5dcee2a7cb3c3cb86460ff6ede7e', 1, 1, 'Added', '31cbacbd62c00094596914a66bd2b175', '{\"guid\": \"31cbacbd62c00094596914a66bd2b175\", \"name\": \"entities\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"31cbacbd62c00094596914a66bd2b175\", \"name\": \"entities\", \"state\": 3, \"xbt_id\": \"06f9b3ea-7699-4355-a02d-4826c47a78a9\", \"created_by\": 1, \"builtIn_type\": \"xsd:ENTITIES\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"126c78b2-0fb7-4dc4-9cc0-881aa205f7f3\", \"creation_timestamp\": \"2022-08-12T20:00:51.331423\", \"last_update_timestamp\": \"2022-08-12T20:00:51.331423\"}, \"xbtManifest\": {\"xbt_id\": \"06f9b3ea-7699-4355-a02d-4826c47a78a9\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3167}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:ENTITIES\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"ec21905187690f7af17686e7214013d2\", \"name\": \"entity\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.331423'),
    (27003, '13460d8ef61ae7a07a052dcfe7b40980', 1, 1, 'Added', '3395a2f88a397a2e85345fee3ec1746e', '{\"guid\": \"3395a2f88a397a2e85345fee3ec1746e\", \"name\": \"non positive integer\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"3395a2f88a397a2e85345fee3ec1746e\", \"name\": \"non positive integer\", \"state\": 3, \"xbt_id\": \"0d6f0c93-0222-47aa-952c-1c10c9e69883\", \"created_by\": 1, \"builtIn_type\": \"xsd:nonPositiveInteger\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":false}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":false}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"2cfd15d4-3678-4e65-be38-92629ed0ae45\", \"creation_timestamp\": \"2022-08-12T20:00:51.268162\", \"last_update_timestamp\": \"2022-08-12T20:00:51.268162\"}, \"xbtManifest\": {\"xbt_id\": \"0d6f0c93-0222-47aa-952c-1c10c9e69883\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3178}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:nonPositiveInteger\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":false}\", \"subTypeOfXbt\": {\"guid\": \"f3bc6f3ca44b47e9a9a66997fc5d3c2b\", \"name\": \"integer\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":false}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.268162'),
    (27004, '78915018bd18a7f21a9a3d78985337c4', 1, 1, 'Added', 'ec21905187690f7af17686e7214013d2', '{\"guid\": \"ec21905187690f7af17686e7214013d2\", \"name\": \"entity\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"ec21905187690f7af17686e7214013d2\", \"name\": \"entity\", \"state\": 3, \"xbt_id\": \"126c78b2-0fb7-4dc4-9cc0-881aa205f7f3\", \"created_by\": 1, \"builtIn_type\": \"xsd:ENTITY\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"d5af9325-d1c8-4bd0-8583-8ac06b4956d2\", \"creation_timestamp\": \"2022-08-12T20:00:51.328373\", \"last_update_timestamp\": \"2022-08-12T20:00:51.328373\"}, \"xbtManifest\": {\"xbt_id\": \"126c78b2-0fb7-4dc4-9cc0-881aa205f7f3\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3189}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:ENTITY\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"954467cfde776fc6d45ed393de47ecff\", \"name\": \"non-colonized name\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.328373'),
    (27005, '96d30429ae122a86d9aa1c57beed80d2', 1, 1, 'Added', 'f9044b20770e252037c894464e8e18df', '{\"guid\": \"f9044b20770e252037c894464e8e18df\", \"name\": \"negative integer\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"f9044b20770e252037c894464e8e18df\", \"name\": \"negative integer\", \"state\": 3, \"xbt_id\": \"1dce4776-e435-41cc-9941-372c6ab0754c\", \"created_by\": 1, \"builtIn_type\": \"xsd:negativeInteger\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":true}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":true}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"0d6f0c93-0222-47aa-952c-1c10c9e69883\", \"creation_timestamp\": \"2022-08-12T20:00:51.272934\", \"last_update_timestamp\": \"2022-08-12T20:00:51.272934\"}, \"xbtManifest\": {\"xbt_id\": \"1dce4776-e435-41cc-9941-372c6ab0754c\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3200}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:negativeInteger\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":true}\", \"subTypeOfXbt\": {\"guid\": \"3395a2f88a397a2e85345fee3ec1746e\", \"name\": \"non positive integer\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"maximum\\\":0, \\\"exclusiveMaximum\\\":true}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.272934'),
    (27006, '0434b935ee0c9017efb00012d3a1404a', 1, 1, 'Added', 'a422d1c18d382571e92ab3b0f85c37f7', '{\"guid\": \"a422d1c18d382571e92ab3b0f85c37f7\", \"name\": \"unsigned short\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"a422d1c18d382571e92ab3b0f85c37f7\", \"name\": \"unsigned short\", \"state\": 3, \"xbt_id\": \"2877cf6b-357b-4e0a-95f5-1e91413638d2\", \"created_by\": 1, \"builtIn_type\": \"xsd:unsignedShort\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":65535}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":65535}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"65ead9cc-35eb-47fc-9006-078df7cfa32a\", \"creation_timestamp\": \"2022-08-12T20:00:51.298955\", \"last_update_timestamp\": \"2022-08-12T20:00:51.298955\"}, \"xbtManifest\": {\"xbt_id\": \"2877cf6b-357b-4e0a-95f5-1e91413638d2\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3211}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:unsignedShort\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":65535}\", \"subTypeOfXbt\": {\"guid\": \"12c448fbbe86ca3b7e93a7df771e0b94\", \"name\": \"unsigned int\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":65535}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.298955'),
    (27007, 'eade834ba5d0469dbad2261eaa29be8b', 1, 1, 'Added', 'a54e126ffd199e8df4bb44bc903926e8', '{\"guid\": \"a54e126ffd199e8df4bb44bc903926e8\", \"name\": \"unsigned long\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"a54e126ffd199e8df4bb44bc903926e8\", \"name\": \"unsigned long\", \"state\": 3, \"xbt_id\": \"5dcfb77e-2869-4f77-9462-250ce7b7c492\", \"created_by\": 1, \"builtIn_type\": \"xsd:unsignedLong\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":18446744073709551615}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":18446744073709551615}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"1df3a62d-53e1-46a0-b31c-bd0d6c50ff7c\", \"creation_timestamp\": \"2022-08-12T20:00:51.290947\", \"last_update_timestamp\": \"2022-08-12T20:00:51.290947\"}, \"xbtManifest\": {\"xbt_id\": \"5dcfb77e-2869-4f77-9462-250ce7b7c492\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3222}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:unsignedLong\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":18446744073709551615}\", \"subTypeOfXbt\": {\"guid\": \"da82a62e6bfd4db88710442a67356ff9\", \"name\": \"non negative integer\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":18446744073709551615}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.290947'),
    (27008, '5c54836a2785f25be328eafa08507423', 1, 1, 'Added', '12c448fbbe86ca3b7e93a7df771e0b94', '{\"guid\": \"12c448fbbe86ca3b7e93a7df771e0b94\", \"name\": \"unsigned int\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"12c448fbbe86ca3b7e93a7df771e0b94\", \"name\": \"unsigned int\", \"state\": 3, \"xbt_id\": \"65ead9cc-35eb-47fc-9006-078df7cfa32a\", \"created_by\": 1, \"builtIn_type\": \"xsd:unsignedInt\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":4294967295}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":4294967295}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"5dcfb77e-2869-4f77-9462-250ce7b7c492\", \"creation_timestamp\": \"2022-08-12T20:00:51.294112\", \"last_update_timestamp\": \"2022-08-12T20:00:51.294112\"}, \"xbtManifest\": {\"xbt_id\": \"65ead9cc-35eb-47fc-9006-078df7cfa32a\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3233}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:unsignedInt\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":4294967295}\", \"subTypeOfXbt\": {\"guid\": \"a54e126ffd199e8df4bb44bc903926e8\", \"name\": \"unsigned long\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":4294967295}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.294112'),
    (27009, '7de6bddd104817bae8ec945f823f627c', 1, 1, 'Added', '4ee4e35beded482a401d14879671cf2b', '{\"guid\": \"4ee4e35beded482a401d14879671cf2b\", \"name\": \"name\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"4ee4e35beded482a401d14879671cf2b\", \"name\": \"name\", \"state\": 3, \"xbt_id\": \"7fd4451b-6ad1-4930-8570-34c67cb66081\", \"created_by\": 1, \"builtIn_type\": \"xsd:Name\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"f4fb8c41-6374-4f7e-a340-7ba1b871e778\", \"creation_timestamp\": \"2022-08-12T20:00:51.30583\", \"last_update_timestamp\": \"2022-08-12T20:00:51.30583\"}, \"xbtManifest\": {\"xbt_id\": \"7fd4451b-6ad1-4930-8570-34c67cb66081\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3244}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:Name\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"0963dd2d22084b4893ff69ff303e57d9\", \"name\": \"token\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.305830'),
    (27010, 'f65bd7e40f0f37b4869f1c721abb126a', 1, 1, 'Added', '8451efe9c107d51a33ea623668afaa4a', '{\"guid\": \"8451efe9c107d51a33ea623668afaa4a\", \"name\": \"unsigned byte\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"8451efe9c107d51a33ea623668afaa4a\", \"name\": \"unsigned byte\", \"state\": 3, \"xbt_id\": \"8c093c3b-1216-401d-bda9-bdb450e02bad\", \"created_by\": 1, \"builtIn_type\": \"xsd:unsignedByte\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":255}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":255}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"2877cf6b-357b-4e0a-95f5-1e91413638d2\", \"creation_timestamp\": \"2022-08-12T20:00:51.302249\", \"last_update_timestamp\": \"2022-08-12T20:00:51.302249\"}, \"xbtManifest\": {\"xbt_id\": \"8c093c3b-1216-401d-bda9-bdb450e02bad\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3255}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:unsignedByte\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":0, \\\"maximum\\\":255}\", \"subTypeOfXbt\": {\"guid\": \"a422d1c18d382571e92ab3b0f85c37f7\", \"name\": \"unsigned short\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":0, \\\"maximum\\\":255}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.302249'),
    (27011, '22520c48f6f324a509147f90903809c5', 1, 1, 'Added', 'c776d8ccc5050987b0af9873a2ef95ab', '{\"guid\": \"c776d8ccc5050987b0af9873a2ef95ab\", \"name\": \"short\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"c776d8ccc5050987b0af9873a2ef95ab\", \"name\": \"short\", \"state\": 3, \"xbt_id\": \"9895d011-6625-41f8-a01c-29692ae323e9\", \"created_by\": 1, \"builtIn_type\": \"xsd:short\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-32768, \\\"maximum\\\":32767}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-32768, \\\"maximum\\\":32767}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"c22e47f7-66db-433a-a1b4-9b1a151983b3\", \"creation_timestamp\": \"2022-08-12T20:00:51.282907\", \"last_update_timestamp\": \"2022-08-12T20:00:51.282907\"}, \"xbtManifest\": {\"xbt_id\": \"9895d011-6625-41f8-a01c-29692ae323e9\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3266}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:short\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-32768, \\\"maximum\\\":32767}\", \"subTypeOfXbt\": {\"guid\": \"8278dee5e8106cc9e2e02b4f6ee4c85f\", \"name\": \"int\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-32768, \\\"maximum\\\":32767}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.282907'),
    (27012, 'e6501e1d8d2eeb6f8f9f707bf635a123', 1, 1, 'Added', 'aafa09d5c14648f3fbd90808944d9f75', '{\"guid\": \"aafa09d5c14648f3fbd90808944d9f75\", \"name\": \"name token\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"aafa09d5c14648f3fbd90808944d9f75\", \"name\": \"name token\", \"state\": 3, \"xbt_id\": \"98a97e1c-5d7c-429d-851e-2e0516015b7b\", \"created_by\": 1, \"builtIn_type\": \"xsd:NMTOKEN\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"f4fb8c41-6374-4f7e-a340-7ba1b871e778\", \"creation_timestamp\": \"2022-08-12T20:00:51.311743\", \"last_update_timestamp\": \"2022-08-12T20:00:51.311743\"}, \"xbtManifest\": {\"xbt_id\": \"98a97e1c-5d7c-429d-851e-2e0516015b7b\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3277}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:NMTOKEN\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"0963dd2d22084b4893ff69ff303e57d9\", \"name\": \"token\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.311743'),
    (27013, 'ab0c78811f66b17f8b50b7a7068ceb39', 1, 1, 'Added', '6112fe6c7fe8978d26dfc39f45f3c04b', '{\"guid\": \"6112fe6c7fe8978d26dfc39f45f3c04b\", \"name\": \"identifier references\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"6112fe6c7fe8978d26dfc39f45f3c04b\", \"name\": \"identifier references\", \"state\": 3, \"xbt_id\": \"a08258f8-0ee9-44a4-b937-323ef1bb907d\", \"created_by\": 1, \"builtIn_type\": \"xsd:IDREFS\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"fddb814a-96b9-4392-bd07-2af08233e5f1\", \"creation_timestamp\": \"2022-08-12T20:00:51.325116\", \"last_update_timestamp\": \"2022-08-12T20:00:51.325116\"}, \"xbtManifest\": {\"xbt_id\": \"a08258f8-0ee9-44a4-b937-323ef1bb907d\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3288}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:IDREFS\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"b20c9e033be4afc6ef1b11659afc4103\", \"name\": \"identifier reference\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.325116'),
    (27014, '490331d583e0b9ae5b39def271f7fef3', 1, 1, 'Added', '864c5d84696bdd17f7b36f61b2d42a0e', '{\"guid\": \"864c5d84696bdd17f7b36f61b2d42a0e\", \"name\": \"identifier\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"864c5d84696bdd17f7b36f61b2d42a0e\", \"name\": \"identifier\", \"state\": 3, \"xbt_id\": \"a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b\", \"created_by\": 1, \"builtIn_type\": \"xsd:ID\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"d5af9325-d1c8-4bd0-8583-8ac06b4956d2\", \"creation_timestamp\": \"2022-08-12T20:00:51.319093\", \"last_update_timestamp\": \"2022-08-12T20:00:51.319093\"}, \"xbtManifest\": {\"xbt_id\": \"a7ed18d7-f5ae-455f-a03b-8d2705fa6d0b\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3299}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:ID\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"954467cfde776fc6d45ed393de47ecff\", \"name\": \"non-colonized name\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.319093'),
    (27015, '9d856cd06d4dfbeb5995a51df92a0f1b', 1, 1, 'Added', '8278dee5e8106cc9e2e02b4f6ee4c85f', '{\"guid\": \"8278dee5e8106cc9e2e02b4f6ee4c85f\", \"name\": \"int\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"8278dee5e8106cc9e2e02b4f6ee4c85f\", \"name\": \"int\", \"state\": 3, \"xbt_id\": \"c22e47f7-66db-433a-a1b4-9b1a151983b3\", \"created_by\": 1, \"builtIn_type\": \"xsd:int\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-2147483648, \\\"maximum\\\":2147483647}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-2147483648, \\\"maximum\\\":2147483647}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"010e4563-02d8-4715-acb6-5d7227988ca7\", \"creation_timestamp\": \"2022-08-12T20:00:51.279799\", \"last_update_timestamp\": \"2022-08-12T20:00:51.279799\"}, \"xbtManifest\": {\"xbt_id\": \"c22e47f7-66db-433a-a1b4-9b1a151983b3\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3310}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:int\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-2147483648, \\\"maximum\\\":2147483647}\", \"subTypeOfXbt\": {\"guid\": \"d484e5046be205ffe7ef0a84651ab595\", \"name\": \"long\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-2147483648, \\\"maximum\\\":2147483647}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.279799'),
    (27016, '32166e9cf169c36d648443cbc046a486', 1, 1, 'Added', '954467cfde776fc6d45ed393de47ecff', '{\"guid\": \"954467cfde776fc6d45ed393de47ecff\", \"name\": \"non-colonized name\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"954467cfde776fc6d45ed393de47ecff\", \"name\": \"non-colonized name\", \"state\": 3, \"xbt_id\": \"d5af9325-d1c8-4bd0-8583-8ac06b4956d2\", \"created_by\": 1, \"builtIn_type\": \"xsd:NCName\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"7fd4451b-6ad1-4930-8570-34c67cb66081\", \"creation_timestamp\": \"2022-08-12T20:00:51.309084\", \"last_update_timestamp\": \"2022-08-12T20:00:51.309084\"}, \"xbtManifest\": {\"xbt_id\": \"d5af9325-d1c8-4bd0-8583-8ac06b4956d2\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3321}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:NCName\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"4ee4e35beded482a401d14879671cf2b\", \"name\": \"name\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.309084'),
    (27017, '740a949857d90afbfc48384e77ab129b', 1, 1, 'Added', 'd99546b1bc2acfd6e87c1ea1487f5a59', '{\"guid\": \"d99546b1bc2acfd6e87c1ea1487f5a59\", \"name\": \"name tokens\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"d99546b1bc2acfd6e87c1ea1487f5a59\", \"name\": \"name tokens\", \"state\": 3, \"xbt_id\": \"de57e550-fcaf-481d-b8a3-6b301ee48988\", \"created_by\": 1, \"builtIn_type\": \"xsd:NMTOKENS\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"98a97e1c-5d7c-429d-851e-2e0516015b7b\", \"creation_timestamp\": \"2022-08-12T20:00:51.315503\", \"last_update_timestamp\": \"2022-08-12T20:00:51.315503\"}, \"xbtManifest\": {\"xbt_id\": \"de57e550-fcaf-481d-b8a3-6b301ee48988\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3332}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:NMTOKENS\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"aafa09d5c14648f3fbd90808944d9f75\", \"name\": \"name token\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.315503'),
    (27018, 'a090a4401b6131a332b36983af0ba019', 1, 1, 'Added', '9aa5f0beeb9d08109e0f6834790ab9a4', '{\"guid\": \"9aa5f0beeb9d08109e0f6834790ab9a4\", \"name\": \"byte\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"9aa5f0beeb9d08109e0f6834790ab9a4\", \"name\": \"byte\", \"state\": 3, \"xbt_id\": \"eba1be95-9fc1-4cd8-88c9-75e3c61fcddb\", \"created_by\": 1, \"builtIn_type\": \"xsd:byte\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-128, \\\"maximum\\\":127}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-128, \\\"maximum\\\":127}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"9895d011-6625-41f8-a01c-29692ae323e9\", \"creation_timestamp\": \"2022-08-12T20:00:51.286613\", \"last_update_timestamp\": \"2022-08-12T20:00:51.286613\"}, \"xbtManifest\": {\"xbt_id\": \"eba1be95-9fc1-4cd8-88c9-75e3c61fcddb\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3343}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:byte\", \"openapi30Map\": \"{\\\"type\\\":\\\"integer\\\", \\\"minimum\\\":-128, \\\"maximum\\\":127}\", \"subTypeOfXbt\": {\"guid\": \"c776d8ccc5050987b0af9873a2ef95ab\", \"name\": \"short\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"number\\\", \\\"multipleOf\\\":1, \\\"minimum\\\":-128, \\\"maximum\\\":127}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.286613'),
    (27019, '6dbba707cffde0d9ea0be31bed0739f5', 1, 1, 'Added', 'b20c9e033be4afc6ef1b11659afc4103', '{\"guid\": \"b20c9e033be4afc6ef1b11659afc4103\", \"name\": \"identifier reference\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"b20c9e033be4afc6ef1b11659afc4103\", \"name\": \"identifier reference\", \"state\": 3, \"xbt_id\": \"fddb814a-96b9-4392-bd07-2af08233e5f1\", \"created_by\": 1, \"builtIn_type\": \"xsd:IDREF\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"d5af9325-d1c8-4bd0-8583-8ac06b4956d2\", \"creation_timestamp\": \"2022-08-12T20:00:51.322015\", \"last_update_timestamp\": \"2022-08-12T20:00:51.322015\"}, \"xbtManifest\": {\"xbt_id\": \"fddb814a-96b9-4392-bd07-2af08233e5f1\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3354}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:IDREF\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"954467cfde776fc6d45ed393de47ecff\", \"name\": \"non-colonized name\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, 1, '2022-08-12 20:00:51.322015'),
    (27020, '97b81c75beb9735080f8136d0f13d3ba', 1, 1, 'Added', '169bb1ded95c8edc188262c21affb79c', '{\"guid\": \"169bb1ded95c8edc188262c21affb79c\", \"name\": \"notation\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"169bb1ded95c8edc188262c21affb79c\", \"name\": \"notation\", \"state\": 3, \"xbt_id\": \"1cdcbd74-1896-4f8c-b2ab-c68e47976e18\", \"created_by\": 1, \"builtIn_type\": \"xsd:NOTATION\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"a5d1f255-ff15-4c38-a5f5-6b3660a80a88\", \"creation_timestamp\": \"2022-08-13 04:15:46.126781\", \"last_update_timestamp\": \"2022-08-13 04:15:46.126781\"}, \"xbtManifest\": {\"xbt_id\": \"18bc4b23-48da-426c-9986-2b4a7bc38f39\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3376}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:NOTATION\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"7114441198054ad78d2fcf9bcdab2cbf\", \"name\": \"any simple type\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, 1, '2022-08-13 04:30:38.195407'),
    (27021, 'f742e9879e085dd8bd1dd7e790509b3c', 1, 1, 'Added', '13e5d1379633033e1d1f8671c62ffb61', '{\"guid\": \"13e5d1379633033e1d1f8671c62ffb61\", \"name\": \"qualified name\", \"state\": 3, \"_metadata\": {\"xbt\": {\"guid\": \"13e5d1379633033e1d1f8671c62ffb61\", \"name\": \"qualified name\", \"state\": 3, \"xbt_id\": \"18bc4b23-48da-426c-9986-2b4a7bc38f39\", \"created_by\": 1, \"builtIn_type\": \"xsd:QName\", \"is_deprecated\": false, \"openapi30_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"owner_user_id\": 1, \"jbt_draft05_map\": \"{\\\"type\\\":\\\"string\\\"}\", \"last_updated_by\": 1, \"subtype_of_xbt_id\": \"a5d1f255-ff15-4c38-a5f5-6b3660a80a88\", \"creation_timestamp\": \"2022-08-13 04:15:46.133324\", \"last_update_timestamp\": \"2022-08-13 04:15:46.133324\"}, \"xbtManifest\": {\"xbt_id\": \"1cdcbd74-1896-4f8c-b2ab-c68e47976e18\", \"conflict\": false, \"release_id\": 1, \"xbt_manifest_id\": 3365}}, \"component\": \"xbt\", \"ownerUser\": {\"roles\": [\"developer\"], \"username\": \"oagis\"}, \"deprecated\": false, \"builtInType\": \"xsd:QName\", \"openapi30Map\": \"{\\\"type\\\":\\\"string\\\"}\", \"subTypeOfXbt\": {\"guid\": \"7114441198054ad78d2fcf9bcdab2cbf\", \"name\": \"any simple type\"}, \"jbtDraft05Map\": \"{\\\"type\\\":\\\"string\\\"}\"}', NULL, NULL, 1, '2022-08-13 04:30:57.641335');

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