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