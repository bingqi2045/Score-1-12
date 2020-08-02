-- ----------------------------------------------------
-- Migration script for Score v1.5.0                 --
--                                                   --
-- Author: Hakju Oh <hakju.oh@nist.gov>              --
-- ----------------------------------------------------

-- Add `top_level_asbiep` table.
DROP TABLE IF EXISTS `top_level_asbiep`;
CREATE TABLE `top_level_asbiep` (
    `top_level_asbiep_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'A internal, primary database key of an top-level ASBIEP.',
    `asbiep_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Foreign key to the ASBIEP table pointing to a record which is a top-level ASBIEP.',
    `owner_user_id` bigint(20) unsigned NOT NULL,
    `last_update_timestamp` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'The timestamp when among all related bie records was last updated.',
    `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'A foreign key referring to the last user who has updated any related bie records.',
    `release_id` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the RELEASE table. It identifies the release, for which this module is associated.',
    `version` varchar(45) DEFAULT NULL COMMENT 'This column hold a version number assigned by the user. This column is only used by the top-level ASBIEP. No format of version is enforced.',
    `status` varchar(45) DEFAULT NULL COMMENT 'This is different from the STATE column which is CRUD life cycle of an entity. The use case for this is to allow the user to indicate the usage status of a top-level ASBIEP (a profile BOD). An integration architect can use this column. Example values are ?Prototype?, ?Test?, and ?Production?. Only the top-level ASBIEP can use this field.',
    `state` int(11) DEFAULT NULL,
    `top_level_abie_id` bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`top_level_asbiep_id`),
    KEY `top_level_asbiep_asbiep_id_fk` (`asbiep_id`),
    KEY `top_level_asbiep_owner_user_id_fk` (`owner_user_id`),
    KEY `top_level_asbiep_release_id_fk` (`release_id`),
    KEY `top_level_asbiep_last_updated_by_fk` (`last_updated_by`),
    KEY `top_level_asbiep_top_level_abie_id_fk` (`top_level_abie_id`),
    CONSTRAINT `top_level_asbiep_asbiep_id_fk` FOREIGN KEY (`asbiep_id`) REFERENCES `asbiep` (`asbiep_id`),
    CONSTRAINT `top_level_asbiep_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `top_level_asbiep_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `top_level_asbiep_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `top_level_asbiep_top_level_abie_id_fk` FOREIGN KEY (`top_level_abie_id`) REFERENCES `top_level_abie` (`top_level_abie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table indexes the ASBIEP which is a top-level ASBIEP. This table and the owner_top_level_asbiep_id column in all BIE tables allow all related BIEs to be retrieved all at once speeding up the profile BOD transactions.';

INSERT INTO `top_level_asbiep` (`asbiep_id`, `owner_user_id`, `last_update_timestamp`, `last_updated_by`, `release_id`, `state`, `top_level_abie_id`)
SELECT `asbiep`.`asbiep_id`, `top_level_abie`.`owner_user_id`,
       `top_level_abie`.`last_update_timestamp`, `top_level_abie`.`last_updated_by`,
       `top_level_abie`.`release_id`, `top_level_abie`.`state`, `top_level_abie`.`top_level_abie_id`
FROM `top_level_abie`
JOIN `abie` ON `top_level_abie`.`abie_id` = `abie`.`abie_id`
JOIN `asbiep` ON (
    `abie`.`abie_id` = `asbiep`.`role_of_abie_id` AND
    `abie`.`owner_top_level_abie_id` = `asbiep`.`owner_top_level_abie_id`
);

-- Add `owner_top_level_asbiep_id` column on `abie` table.
ALTER TABLE `abie` ADD COLUMN `owner_top_level_asbiep_id` bigint(20) unsigned DEFAULT NULL,
                   ADD CONSTRAINT `abie_owner_top_level_asbiep_id_fk` FOREIGN KEY (`owner_top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

UPDATE `abie`, (
    SELECT `top_level_asbiep`.`top_level_asbiep_id`, `abie`.`abie_id`
    FROM `top_level_asbiep`
    JOIN `top_level_abie` ON `top_level_asbiep`.`top_level_abie_id` = `top_level_abie`.`top_level_abie_id`
    JOIN `abie` ON `top_level_abie`.`top_level_abie_id` = `abie`.`owner_top_level_abie_id`
) t
SET `abie`.`owner_top_level_asbiep_id` = t.`top_level_asbiep_id`
WHERE `abie`.`abie_id` = t.`abie_id`;

ALTER TABLE `abie` MODIFY COLUMN `owner_top_level_asbiep_id` bigint(20) unsigned NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.',
                   DROP FOREIGN KEY `abie_owner_top_level_abie_id_fk`,
                   DROP FOREIGN KEY `abie_client_id_fk`,
                   DROP COLUMN `owner_top_level_abie_id`;

-- Drop unused table `client`
DROP TABLE `client`;

-- Add `owner_top_level_asbiep_id` column on `asbie` table.
ALTER TABLE `asbie` ADD COLUMN `owner_top_level_asbiep_id` bigint(20) unsigned DEFAULT NULL,
                    ADD CONSTRAINT `asbie_owner_top_level_asbiep_id_fk` FOREIGN KEY (`owner_top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

UPDATE `asbie`, (
    SELECT `top_level_asbiep`.`top_level_asbiep_id`, `asbie`.`asbie_id`
    FROM `top_level_asbiep`
    JOIN `top_level_abie` ON `top_level_asbiep`.`top_level_abie_id` = `top_level_abie`.`top_level_abie_id`
    JOIN `asbie` ON `top_level_abie`.`top_level_abie_id` = `asbie`.`owner_top_level_abie_id`
) t
SET `asbie`.`owner_top_level_asbiep_id` = t.`top_level_asbiep_id`
WHERE `asbie`.`asbie_id` = t.`asbie_id`;

ALTER TABLE `asbie` MODIFY COLUMN `owner_top_level_asbiep_id` bigint(20) unsigned NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.',
                    DROP FOREIGN KEY `asbie_owner_top_level_abie_id_fk`,
                    DROP COLUMN `owner_top_level_abie_id`,
                    MODIFY to_asbiep_id bigint(20) unsigned COMMENT 'A foreign key to the ASBIEP table. TO_ASBIEP_ID is basically a child data element of the FROM_ABIE_ID. The TO_ASBIEP_ID must be based on the TO_ASCCP_ID in the BASED_ASCC_ID. the ASBIEP is reused with the OWNER_TOP_LEVEL_ASBIEP is different after joining ASBIE and ASBIEP tables';


-- Add `owner_top_level_asbiep_id` column on `bbie` table.
ALTER TABLE `bbie` ADD COLUMN `owner_top_level_asbiep_id` bigint(20) unsigned DEFAULT NULL,
                   ADD CONSTRAINT `bbie_owner_top_level_asbiep_id_fk` FOREIGN KEY (`owner_top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

UPDATE `bbie`, (
    SELECT `top_level_asbiep`.`top_level_asbiep_id`, `bbie`.`bbie_id`
    FROM `top_level_asbiep`
    JOIN `top_level_abie` ON `top_level_asbiep`.`top_level_abie_id` = `top_level_abie`.`top_level_abie_id`
    JOIN `bbie` ON `top_level_abie`.`top_level_abie_id` = `bbie`.`owner_top_level_abie_id`
) t
SET `bbie`.`owner_top_level_asbiep_id` = t.`top_level_asbiep_id`
WHERE `bbie`.`bbie_id` = t.`bbie_id`;

ALTER TABLE `bbie` MODIFY COLUMN `owner_top_level_asbiep_id` bigint(20) unsigned NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.',
                   DROP FOREIGN KEY `bbie_owner_top_level_abie_id_fk`,
                   DROP COLUMN `owner_top_level_abie_id`;


-- Add `owner_top_level_asbiep_id` column on `asbiep` table.
ALTER TABLE `asbiep` ADD COLUMN `owner_top_level_asbiep_id` bigint(20) unsigned DEFAULT NULL,
                     ADD CONSTRAINT `asbiep_owner_top_level_asbiep_id_fk` FOREIGN KEY (`owner_top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

-- Migrate ABIE's version, status to TOP_LEVEL_ASBIEP.
UPDATE
	`top_level_asbiep`
	JOIN `top_level_abie` ON `top_level_asbiep`.`top_level_abie_id` = `top_level_abie`.`top_level_abie_id`
	JOIN `abie` ON `top_level_abie`.`abie_id` = `abie`.`abie_id`
SET
	`top_level_asbiep`.`version` = `abie`.`version`,
	`top_level_asbiep`.`status` = `abie`.`status`;


-- Migrate ABIE's Biz term, Remark, Definition, to ASBIEP.
UPDATE
	`asbiep`
	JOIN `abie` ON `asbiep`.`role_of_abie_id` = `abie`.`abie_id`
SET
	`asbiep`.`definition` = `abie`.`definition`,
	`asbiep`.`remark` = `abie`.`remark`,
	`asbiep`.`biz_term` = `abie`.`biz_term`;


-- remove ABIE's Biz term, Remark, Definition
UPDATE
	`abie`
SET
	`abie`.`definition` = NULL,
	`abie`.`remark` = NULL,
	`abie`.`biz_term` = NULL;

-- Migrate ASBIE's remark to ASBIEP.
UPDATE
	`asbiep`
	JOIN `asbie` ON `asbiep`.`asbiep_id` = `asbie`.`to_asbiep_id`
SET
	`asbiep`.`remark` = `asbie`.`remark`;

-- remove ASBIE's remark.
UPDATE
	`asbie`
SET
	`asbie`.`remark` = NULL;

-- Drop columns on ABIE
ALTER TABLE `abie` DROP COLUMN `version`,
                   DROP COLUMN `status`,
                   DROP COLUMN `client_id`;

UPDATE `asbiep`, (
    SELECT `top_level_asbiep`.`top_level_asbiep_id`, `asbiep`.`asbiep_id`
    FROM `top_level_asbiep`
    JOIN `top_level_abie` ON `top_level_asbiep`.`top_level_abie_id` = `top_level_abie`.`top_level_abie_id`
    JOIN `asbiep` ON `top_level_abie`.`top_level_abie_id` = `asbiep`.`owner_top_level_abie_id`
) t
SET `asbiep`.`owner_top_level_asbiep_id` = t.`top_level_asbiep_id`
WHERE `asbiep`.`asbiep_id` = t.`asbiep_id`;

ALTER TABLE `asbiep` MODIFY COLUMN `owner_top_level_asbiep_id` bigint(20) unsigned NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.',
                     DROP FOREIGN KEY `asbiep_owner_top_level_abie_id_fk`,
                     DROP COLUMN `owner_top_level_abie_id`;


-- Add `owner_top_level_asbiep_id` column on `bbiep` table.
ALTER TABLE `bbiep` ADD COLUMN `owner_top_level_asbiep_id` bigint(20) unsigned DEFAULT NULL,
                    ADD CONSTRAINT `bbiep_owner_top_level_asbiep_id_fk` FOREIGN KEY (`owner_top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

UPDATE `bbiep`, (
    SELECT `top_level_asbiep`.`top_level_asbiep_id`, `bbiep`.`bbiep_id`
    FROM `top_level_asbiep`
    JOIN `top_level_abie` ON `top_level_asbiep`.`top_level_abie_id` = `top_level_abie`.`top_level_abie_id`
    JOIN `bbiep` ON `top_level_abie`.`top_level_abie_id` = `bbiep`.`owner_top_level_abie_id`
) t
SET `bbiep`.`owner_top_level_asbiep_id` = t.`top_level_asbiep_id`
WHERE `bbiep`.`bbiep_id` = t.`bbiep_id`;

ALTER TABLE `bbiep` MODIFY COLUMN `owner_top_level_asbiep_id` bigint(20) unsigned NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.',
                    DROP FOREIGN KEY `bbiep_owner_top_level_abie_id_fk`,
                    DROP COLUMN `owner_top_level_abie_id`;


-- Add `owner_top_level_asbiep_id` column on `bbie_sc` table.
ALTER TABLE `bbie_sc` ADD COLUMN `owner_top_level_asbiep_id` bigint(20) unsigned DEFAULT NULL,
                      ADD CONSTRAINT `bbie_sc_owner_top_level_asbiep_id_fk` FOREIGN KEY (`owner_top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

UPDATE `bbie_sc`, (
    SELECT `top_level_asbiep`.`top_level_asbiep_id`, `bbie_sc`.`bbie_sc_id`
    FROM `top_level_asbiep`
    JOIN `top_level_abie` ON `top_level_asbiep`.`top_level_abie_id` = `top_level_abie`.`top_level_abie_id`
    JOIN `bbie_sc` ON `top_level_abie`.`top_level_abie_id` = `bbie_sc`.`owner_top_level_abie_id`
) t
SET `bbie_sc`.`owner_top_level_asbiep_id` = t.`top_level_asbiep_id`
WHERE `bbie_sc`.`bbie_sc_id` = t.`bbie_sc_id`;

ALTER TABLE `bbie_sc` MODIFY COLUMN `owner_top_level_asbiep_id` bigint(20) unsigned NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.',
                      DROP FOREIGN KEY `bbie_sc_owner_top_level_abie_id_fk`,
                      DROP COLUMN `owner_top_level_abie_id`;


-- Add `owner_top_level_asbiep_id` column on `biz_ctx_assignment` table.
ALTER TABLE `biz_ctx_assignment` ADD COLUMN `top_level_asbiep_id` bigint(20) unsigned DEFAULT NULL,
                                 ADD CONSTRAINT `biz_ctx_assignment_biz_ctx_id_fk` FOREIGN KEY (`biz_ctx_id`) REFERENCES `biz_ctx` (`biz_ctx_id`),
                                 ADD CONSTRAINT `biz_ctx_assignment_top_level_asbiep_id_fk` FOREIGN KEY (`top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

UPDATE `biz_ctx_assignment`, (
    SELECT `top_level_asbiep`.`top_level_asbiep_id`, `top_level_abie`.`top_level_abie_id`
    FROM `top_level_asbiep`
    JOIN `top_level_abie` ON `top_level_asbiep`.`top_level_abie_id` = `top_level_abie`.`top_level_abie_id`
) t
SET `biz_ctx_assignment`.`top_level_asbiep_id` = t.`top_level_asbiep_id`
WHERE `biz_ctx_assignment`.`top_level_abie_id` = t.`top_level_abie_id`;

ALTER TABLE `biz_ctx_assignment` MODIFY COLUMN `top_level_asbiep_id` bigint(20) unsigned NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.',
                                 DROP KEY `biz_ctx_assignment_uk`,
                                 DROP FOREIGN KEY `biz_ctx_rule_ibfk_1`,
                                 DROP FOREIGN KEY `biz_ctx_rule_ibfk_2`,
                                 DROP COLUMN `top_level_abie_id`;

ALTER TABLE `biz_ctx_assignment` ADD UNIQUE KEY `biz_ctx_assignment_uk` (`biz_ctx_id`,`top_level_asbiep_id`);


-- Add `owner_top_level_asbiep_id` column on `bie_user_ext_revision` table.
ALTER TABLE `bie_user_ext_revision` ADD COLUMN `top_level_asbiep_id` bigint(20) unsigned DEFAULT NULL,
                                    ADD CONSTRAINT `bie_user_ext_revision_top_level_asbiep_id_fk` FOREIGN KEY (`top_level_asbiep_id`) REFERENCES `top_level_asbiep` (`top_level_asbiep_id`);

UPDATE `bie_user_ext_revision`, (
    SELECT `top_level_asbiep`.`top_level_asbiep_id`, `top_level_abie`.`top_level_abie_id`
    FROM `top_level_asbiep`
    JOIN `top_level_abie` ON `top_level_asbiep`.`top_level_abie_id` = `top_level_abie`.`top_level_abie_id`
) t
SET `bie_user_ext_revision`.`top_level_asbiep_id` = t.`top_level_asbiep_id`
WHERE `bie_user_ext_revision`.`top_level_abie_id` = t.`top_level_abie_id`;

ALTER TABLE `bie_user_ext_revision` MODIFY COLUMN `top_level_asbiep_id` bigint(20) unsigned NOT NULL COMMENT 'This is a foreign key to the top-level ASBIEP.',
                                    DROP FOREIGN KEY `bie_user_ext_revision_top_level_abie_id_fk`,
                                    DROP COLUMN `top_level_abie_id`;


-- Polishing `top_level_asbiep` table.
ALTER TABLE `top_level_asbiep` DROP FOREIGN KEY `top_level_asbiep_top_level_abie_id_fk`,
                               DROP COLUMN `top_level_abie_id`;

-- Delete `top_level_abie` table.
DROP TABLE `top_level_abie`;

-- Extend length of proterty_term 60 to 100
ALTER TABLE `asccp` MODIFY COLUMN `property_term` varchar(100) DEFAULT NULL COMMENT 'The role (or property) the ACC as referred to by the Role_Of_ACC_ID play when the ASCCP is used by another ACC. There must be only one ASCCP without a Property_Term for a particular ACC.';
ALTER TABLE `bccp` MODIFY COLUMN `property_term` varchar(100) NOT NULL COMMENT 'The property concept that the BCCP models.';