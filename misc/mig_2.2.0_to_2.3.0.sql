-- ----------------------------------------------------
-- Migration script for Score v2.3.0                 --
--                                                   --
-- Author: Kwanghoon Lee <kwanghoon.lee@nist.gov>    --
-- ----------------------------------------------------


-- Alter dt tables
ALTER TABLE `dt_sc` ADD COLUMN `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the code list.',
				ADD COLUMN  `owner_user_id` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
				ADD COLUMN `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the code list.',
				ADD COLUMN  `creation_timestamp` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Timestamp when the code list was created.',
				ADD COLUMN  `last_update_timestamp` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Timestamp when the code list was last updated.',
				ADD COLUMN `prev_dt_sc_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
                ADD COLUMN `next_dt_sc_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.',
                ADD CONSTRAINT `dt_sc_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
                ADD CONSTRAINT `dt_sc_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
				ADD CONSTRAINT `dt_sc_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
                ADD CONSTRAINT `dt_sc_prev_dt_sc_id_fk` FOREIGN KEY (`prev_dt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`),
                ADD CONSTRAINT `dt_sc_next_dt_sc_id_fk` FOREIGN KEY (`next_dt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`);

UPDATE `dt_sc` JOIN `dt` ON `dt_sc`.`owner_dt_id` = `dt`.`dt_id` SET `dt_sc`.`created_by` = `dt`.`created_by`, `dt_sc`.`owner_user_id` = `dt`.`owner_user_id`, `dt_sc`.`last_updated_by` = `dt`.`last_updated_by`, `dt_sc`.`creation_timestamp` = `dt`.`creation_timestamp`, `dt_sc`.`last_update_timestamp` = `dt`.`last_update_timestamp`;

-- add six_digit_id to `dt`
ALTER TABLE `dt` ADD COLUMN `six_digit_id` varchar(45) DEFAULT NULL COMMENT 'The six number suffix comes from the UN/CEFACT XML Schema NDR.' AFTER `qualifier`;
UPDATE `dt` SET `six_digit_id` = REPLACE(REPLACE(`den`, CONCAT(`data_type_term`, '_'), ''), '. Type', '') WHERE `den` REGEXP '_[0-9|A-Z]+';

ALTER TABLE `dt` ADD COLUMN `representation_term` varchar(100) DEFAULT NULL AFTER `qualifier`;
ALTER TABLE `dt` ADD COLUMN `origin_den` varchar(200) DEFAULT NULL AFTER `den`;

UPDATE dt SET origin_den = den;
UPDATE dt SET representation_term = data_type_term;
UPDATE dt SET den = concat(data_type_term, '. Type') WHERE type = 'Default';

UPDATE dt SET qualifier = REPLACE(den, '. Type', ''), representation_term = null WHERE type in ('Unqualified') AND REPLACE(REPLACE(den, '. Type', ''), data_type_term, '') != '';

UPDATE dt SET qualifier = REPLACE(REPLACE(REPLACE(den, '. Type', ''), data_type_term, ''), '_  Content', ' Content') WHERE type in ('Qualified') AND concat(qualifier, '_') != REPLACE(REPLACE(den, '. Type', ''), data_type_term, '');

UPDATE dt SET qualifier = LEFT(qualifier, length(qualifier) - 2) WHERE right(qualifier, 2) = '_ ';

UPDATE dt SET qualifier = REPLACE(qualifier, ' Content', concat(' ', data_type_term, ' Content')), representation_term = null WHERE type = 'Qualified' AND concat(qualifier, '_ ', data_type_term, '. Type') != origin_den AND right(qualifier, 8) = ' Content';

UPDATE dt SET qualifier = 'Open Duration Measure', representation_term = null WHERE guid = '2634b8a9918f499a83e06df48cc0a525';
UPDATE dt SET qualifier = 'Name Value Pair' WHERE guid = 'e298a36a331247c3a2a4a585d5754531';

UPDATE dt SET den = concat(IF(qualifier is null, '', concat(qualifier, '_ ')) ,data_type_term, '. Type');

ALTER TABLE `dt` DROP FOREIGN KEY `dt_previous_version_dt_id_fk`,
DROP COLUMN `version_num`,
DROP COLUMN `type`,
DROP COLUMN `previous_version_dt_id`,
DROP COLUMN `origin_den`,
DROP COLUMN `content_component_den`,
DROP COLUMN `revision_doc`;

-- migrate `BDT` in path to `DT` and hash.
UPDATE `bbie_sc` SET `bbie_sc`.`path` = replace(`path`, 'BDT', 'DT');
UPDATE `bbie_sc` SET `bbie_sc`.`hash_path` = SHA2(`path`, 256);