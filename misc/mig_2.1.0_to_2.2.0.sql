-- ----------------------------------------------------
-- Migration script for Score v2.2.0                 --
--                                                   --
-- Author: Kwanghoon Lee <kwanghoon.lee@nist.gov>    --
--             										 --
-- ----------------------------------------------------

-- ADD `module_set_id` to `module`
ALTER TABLE `module` ADD COLUMN `module_set_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates a module set.' AFTER `module_dir_id`,
ADD COLUMN `parent_module_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates a parent module id. root module will be NULL.' AFTER `module_set_id`,
ADD COLUMN `type` VARCHAR(45) NOT NULL COMMENT 'This is a type column for indicates module is FILE or DIRECTORY.' AFTER `parent_module_id`,
ADD COLUMN `path` TEXT NOT NULL COMMENT 'Absolute path to the module.' AFTER `type`,
ADD CONSTRAINT `module_module_set_id_fk` FOREIGN KEY (`module_set_id`) REFERENCES `module_set` (`module_set_id`),
ADD CONSTRAINT `module_parent_module_id_fk` FOREIGN KEY (`parent_module_id`) REFERENCES `module` (`module_id`);
ALTER TABLE `module` DROP FOREIGN KEY `module_module_dir_id_fk`;
UPDATE `module` SET `module_set_id` = (SELECT `module_set`.`module_set_id` FROM `module_set` LIMIT 1);


-- INSERT module_dir to module
INSERT INTO `module` (`module_dir_id`, `module_set_id`, `parent_module_id`, `type`, `path`, `name`, `namespace_id`, `version_num`, `created_by`, `last_updated_by`, `owner_user_id`, `creation_timestamp`, `last_update_timestamp`)
SELECT
    NULL, (SELECT `module_set`.`module_set_id` FROM `module_set` LIMIT 1), NULL, 'DIRECTORY',`module_dir`.`path`,`module_dir`.`name`,
    (SELECT `namespace`.`namespace_id` FROM `namespace` WHERE `namespace`.`is_std_nmsp` = 1 LIMIT 1), null,
    `module_dir`.`created_by`, `module_dir`.`last_updated_by`, `module_dir`.`created_by`, `module_dir`.`creation_timestamp`, `module_dir`.`last_update_timestamp`
FROM
    `module_dir`;

-- UPDATE MODULE.type, parent_mdoule_id, path;
UPDATE
    `module`
    JOIN (SELECT module.module_id, module_dir.module_dir_id, module_dir.path FROM `module` JOIN `module_dir` on `module`.`path` = `module_dir`.`path` WHERE `module`.`type` = 'DIRECTORY') AS `t`
    ON `module`.`module_dir_id` = `t`.`module_dir_id`
SET
    `module`.`parent_module_id` = `t`.`module_id`,
    `module`.`type` = 'FILE',
    `module`.`path` = CONCAT(`t`.`path`, '\\', `module`.`name`)
WHERE
    `module`.`type` = '';


UPDATE
    `module`
    JOIN `module_dir` ON `module`.`path` = `module_dir`.`path`
    JOIN `module_dir` AS `parent` ON `module_dir`.`parent_module_dir_id` = `parent`.`module_dir_id`
    JOIN `module` AS `prent_module` ON `parent`.`path` = `prent_module`.`path`
SET
    `module`.`parent_module_id` = `prent_module`.`module_id`
WHERE
    `module`.`type` = 'DIRECTORY';

ALTER TABLE `module` MODIFY COLUMN `module_set_id` bigint(20) unsigned NOT NULL COMMENT 'This indicates a module set.';
ALTER TABLE `module` DROP COLUMN `module_dir_id`;

-- REPLACE `module_set_assignment` to `module_id`
ALTER TABLE `module_acc_manifest` ADD COLUMN `module_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_set_assignment_id`,
ADD CONSTRAINT `module_acc_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
UPDATE `module_acc_manifest` JOIN `module_set_assignment` ON `module_acc_manifest`.`module_set_assignment_id` = `module_set_assignment`.`module_set_assignment_id` SET `module_acc_manifest`.`module_id` = `module_set_assignment`.`module_id`;
ALTER TABLE `module_acc_manifest` DROP FOREIGN KEY `module_acc_manifest_module_set_assignment_id_fk`;
ALTER TABLE `module_acc_manifest` DROP COLUMN `module_set_assignment_id`;
ALTER TABLE `module_acc_manifest` MODIFY COLUMN `module_id` bigint(20) unsigned NOT NULL COMMENT 'This indicates a module.';

-- REPLACE `module_set_assignment` to `module_id`
ALTER TABLE `module_agency_id_list_manifest` ADD COLUMN `module_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_set_assignment_id`,
ADD CONSTRAINT `module_agency_id_list_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
UPDATE `module_agency_id_list_manifest` JOIN `module_set_assignment` ON `module_agency_id_list_manifest`.`module_set_assignment_id` = `module_set_assignment`.`module_set_assignment_id` SET `module_agency_id_list_manifest`.`module_id` = `module_set_assignment`.`module_id`;
ALTER TABLE `module_agency_id_list_manifest` DROP FOREIGN KEY `module_agency_id_list_manifest_module_set_assignment_id_fk`;
ALTER TABLE `module_agency_id_list_manifest` DROP COLUMN `module_set_assignment_id`;
ALTER TABLE `module_agency_id_list_manifest` MODIFY COLUMN `module_id` bigint(20) unsigned NOT NULL COMMENT 'This indicates a module.';

-- REPLACE `module_set_assignment` to `module_id`
ALTER TABLE `module_asccp_manifest` ADD COLUMN `module_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_set_assignment_id`,
ADD CONSTRAINT `module_asccp_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
UPDATE `module_asccp_manifest` JOIN `module_set_assignment` ON `module_asccp_manifest`.`module_set_assignment_id` = `module_set_assignment`.`module_set_assignment_id` SET `module_asccp_manifest`.`module_id` = `module_set_assignment`.`module_id`;
ALTER TABLE `module_asccp_manifest` DROP FOREIGN KEY `module_asccp_manifest_module_set_assignment_id_fk`;
ALTER TABLE `module_asccp_manifest` DROP COLUMN `module_set_assignment_id`;
ALTER TABLE `module_asccp_manifest` MODIFY COLUMN `module_id` bigint(20) unsigned NOT NULL COMMENT 'This indicates a module.';

-- REPLACE `module_set_assignment` to `module_id`
ALTER TABLE `module_bccp_manifest` ADD COLUMN `module_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_set_assignment_id`,
ADD CONSTRAINT `module_bccp_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
UPDATE `module_bccp_manifest` JOIN `module_set_assignment` ON `module_bccp_manifest`.`module_set_assignment_id` = `module_set_assignment`.`module_set_assignment_id` SET `module_bccp_manifest`.`module_id` = `module_set_assignment`.`module_id`;
ALTER TABLE `module_bccp_manifest` DROP FOREIGN KEY `module_bccp_manifest_module_set_assignment_id_fk`;
ALTER TABLE `module_bccp_manifest` DROP COLUMN `module_set_assignment_id`;
ALTER TABLE `module_bccp_manifest` MODIFY COLUMN `module_id` bigint(20) unsigned NOT NULL COMMENT 'This indicates a module.';

-- REPLACE `module_set_assignment` to `module_id`
ALTER TABLE `module_blob_content_manifest` ADD COLUMN `module_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_set_assignment_id`,
ADD CONSTRAINT `module_blob_content_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
UPDATE `module_blob_content_manifest` JOIN `module_set_assignment` ON `module_blob_content_manifest`.`module_set_assignment_id` = `module_set_assignment`.`module_set_assignment_id` SET `module_blob_content_manifest`.`module_id` = `module_set_assignment`.`module_id`;
ALTER TABLE `module_blob_content_manifest` DROP FOREIGN KEY `module_blob_content_manifest_module_set_assignment_id_fk`;
ALTER TABLE `module_blob_content_manifest` DROP COLUMN `module_set_assignment_id`;
ALTER TABLE `module_blob_content_manifest` MODIFY COLUMN `module_id` bigint(20) unsigned NOT NULL COMMENT 'This indicates a module.';

-- REPLACE `module_set_assignment` to `module_id`
ALTER TABLE `module_code_list_manifest` ADD COLUMN `module_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_set_assignment_id`,
ADD CONSTRAINT `module_code_list_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
UPDATE `module_code_list_manifest` JOIN `module_set_assignment` ON `module_code_list_manifest`.`module_set_assignment_id` = `module_set_assignment`.`module_set_assignment_id` SET `module_code_list_manifest`.`module_id` = `module_set_assignment`.`module_id`;
ALTER TABLE `module_code_list_manifest` DROP FOREIGN KEY `module_code_list_manifest_module_set_assignment_id_fk`;
ALTER TABLE `module_code_list_manifest` DROP COLUMN `module_set_assignment_id`;
ALTER TABLE `module_code_list_manifest` MODIFY COLUMN `module_id` bigint(20) unsigned NOT NULL COMMENT 'This indicates a module.';

-- REPLACE `module_set_assignment` to `module_id`
ALTER TABLE `module_dt_manifest` ADD COLUMN `module_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_set_assignment_id`,
ADD CONSTRAINT `module_dt_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
UPDATE `module_dt_manifest` JOIN `module_set_assignment` ON `module_dt_manifest`.`module_set_assignment_id` = `module_set_assignment`.`module_set_assignment_id` SET `module_dt_manifest`.`module_id` = `module_set_assignment`.`module_id`;
ALTER TABLE `module_dt_manifest` DROP FOREIGN KEY `module_dt_manifest_module_set_assignment_id_fk`;
ALTER TABLE `module_dt_manifest` DROP COLUMN `module_set_assignment_id`;
ALTER TABLE `module_dt_manifest` MODIFY COLUMN `module_id` bigint(20) unsigned NOT NULL COMMENT 'This indicates a module.';

-- REPLACE `module_set_assignment` to `module_id`
ALTER TABLE `module_xbt_manifest` ADD COLUMN `module_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates a module.' AFTER `module_set_assignment_id`,
ADD CONSTRAINT `module_xbt_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`);
UPDATE `module_xbt_manifest` JOIN `module_set_assignment` ON `module_xbt_manifest`.`module_set_assignment_id` = `module_set_assignment`.`module_set_assignment_id` SET `module_xbt_manifest`.`module_id` = `module_set_assignment`.`module_id`;
ALTER TABLE `module_xbt_manifest` DROP FOREIGN KEY `module_xbt_manifest_module_set_assignment_id_fk`;
ALTER TABLE `module_xbt_manifest` DROP COLUMN `module_set_assignment_id`;
ALTER TABLE `module_xbt_manifest` MODIFY COLUMN `module_id` bigint(20) unsigned NOT NULL COMMENT 'This indicates a module.';

-- DROP module_dir, module_set_assignment table
DROP TABLE `module_dir`;
DROP TABLE `module_dep`;
DROP TABLE `module_set_assignment`;

ALTER TABLE `agency_id_list` DROP INDEX `agency_id_list_uk2`, DROP INDEX `agency_id_list_uk1`;

ALTER TABLE `agency_id_list_manifest` ADD COLUMN `agency_id_list_value_manifest_id` bigint(20) unsigned DEFAULT NULL AFTER `agency_id_list_id`,
ADD CONSTRAINT `agency_id_list_value_manifest_id_fk` FOREIGN KEY (`agency_id_list_value_manifest_id`) REFERENCES `agency_id_list_value_manifest` (`agency_id_list_value_manifest_id`);
UPDATE
	`agency_id_list_manifest`
	JOIN `agency_id_list` ON `agency_id_list_manifest`.`agency_id_list_id` = `agency_id_list`.`agency_id_list_id`
	JOIN `agency_id_list_value_manifest` ON `agency_id_list`.`agency_id_list_value_id` = `agency_id_list_value_manifest`.`agency_id_list_value_id` AND `agency_id_list_value_manifest`.`release_id` = `agency_id_list_manifest`.`release_id`
SET
	`agency_id_list_manifest`.`agency_id_list_value_manifest_id` = `agency_id_list_value_manifest`.`agency_id_list_value_manifest_id`;

ALTER TABLE `agency_id_list`
ADD COLUMN `remark` varchar(225) DEFAULT NULL COMMENT 'Usage information about the agency id list.' AFTER `definition`,
ADD COLUMN `definition_source` varchar(100) DEFAULT NULL COMMENT 'This is typically a URL which indicates the source of the agency id list DEFINITION.' AFTER `definition`,
MODIFY COLUMN `list_id` varchar(100) DEFAULT NULL COMMENT 'This is a business or standard identification assigned to the agency identification list.';

ALTER TABLE `agency_id_list_value`
ADD COLUMN `definition_source` varchar(100) DEFAULT NULL COMMENT 'This is typically a URL which indicates the source of the agency id list value DEFINITION.' AFTER `definition`;

-- migrate developer code list to do not use based code list id
-- update bdt_pri_restri
UPDATE
    `bdt_pri_restri`
    JOIN (SELECT
            `code_list`.`code_list_id` AS `code_list_id`, `base`.`code_list_id` AS `new_id`
          FROM
            `code_list`
            JOIN `code_list` AS `base` ON `code_list`.`based_code_list_id` = `base`.`code_list_id`
            JOIN `app_user` ON `code_list`.`owner_user_id` = `app_user`.`app_user_id` AND `app_user`.`is_developer` = 1) AS `temp`
    ON `bdt_pri_restri`.`code_list_id` = `temp`.`code_list_id`
SET
    `bdt_pri_restri`.`code_list_id` = `temp`.`new_id`;
-- update bdt_pri_restri_sc
UPDATE
    `bdt_sc_pri_restri`
    JOIN (SELECT
            `code_list`.`code_list_id` AS `code_list_id`, `base`.`code_list_id` AS `new_id`
          FROM
            `code_list`
            JOIN `code_list` AS `base` ON `code_list`.`based_code_list_id` = `base`.`code_list_id`
            JOIN `app_user` ON `code_list`.`owner_user_id` = `app_user`.`app_user_id` AND `app_user`.`is_developer` = 1) AS `temp`
    ON `bdt_sc_pri_restri`.`code_list_id` = `temp`.`code_list_id`
SET
    `bdt_sc_pri_restri`.`code_list_id` = `temp`.`new_id`;
-- update bbie
UPDATE
    `bbie`
    JOIN (SELECT
            `code_list`.`code_list_id` AS `code_list_id`, `base`.`code_list_id` AS `new_id`
          FROM
            `code_list`
            JOIN `code_list` AS `base` ON `code_list`.`based_code_list_id` = `base`.`code_list_id`
            JOIN `app_user` ON `code_list`.`owner_user_id` = `app_user`.`app_user_id` AND `app_user`.`is_developer` = 1) AS `temp`
    ON `bbie`.`code_list_id` = `temp`.`code_list_id`
SET
    `bbie`.`code_list_id` = `temp`.`new_id`;
-- update bbie_sc
UPDATE
    `bbie_sc`
    JOIN (SELECT
            `code_list`.`code_list_id` AS `code_list_id`, `base`.`code_list_id` AS `new_id`
          FROM
            `code_list`
            JOIN `code_list` AS `base` ON `code_list`.`based_code_list_id` = `base`.`code_list_id`
            JOIN `app_user` ON `code_list`.`owner_user_id` = `app_user`.`app_user_id` AND `app_user`.`is_developer` = 1) AS `temp`
    ON `bbie_sc`.`code_list_id` = `temp`.`code_list_id`
SET
    `bbie_sc`.`code_list_id` = `temp`.`new_id`;

-- set deprecate code_list_manifest/code_list
UPDATE
    `code_list_manifest`
    JOIN `code_list` ON `code_list_manifest`.`code_list_id` = `code_list`.`code_list_id`
    JOIN `app_user` ON `code_list`.`owner_user_id` = `app_user`.`app_user_id` AND `app_user`.`is_developer` = 1
SET
    `code_list_manifest`.`based_code_list_manifest_id` = NULL,
    `code_list`.`based_code_list_id` = NULL,
    `code_list`.`is_deprecated` = 1
WHERE
    `code_list_manifest`.`based_code_list_manifest_id` IS NOT NULL;

-- Update `openapi30_map` column values in `xbt`
UPDATE `xbt` SET `openapi30_map` = '{"type":"object"}' WHERE `name` = 'any type';
UPDATE `xbt` SET `openapi30_map` = '{"type":"string", "pattern":"^(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)$"}' WHERE `name` = 'time';
UPDATE `xbt` SET `openapi30_map` = '{"type":"string", "format":"normalizedString"}' WHERE `name` = 'normalized string';
UPDATE `xbt` SET `openapi30_map` = '{"type":"string", "format":"token"}' WHERE `name` = 'token';
UPDATE `xbt` SET `openapi30_map` = '{"type":"string", "format":"byte"}' WHERE `name` = 'base64 binary';
UPDATE `xbt` SET `openapi30_map` = '{"type":"string", "format":"binary"}' WHERE `name` = 'hex binary';
UPDATE `xbt` SET `openapi30_map` = '{"type":"string", "format":"date"}' WHERE `name` = 'xbt date';
UPDATE `xbt` SET `openapi30_map` = '{"type":"string"; "format":"date-time"}' WHERE `name` = 'xbt date hour minute';
UPDATE `xbt` SET `openapi30_map` = '{"type":"string"; "format":"date-time"}' WHERE `name` = 'xbt date hour minute UTC';
UPDATE `xbt` SET `openapi30_map` = '{"type":"string"; "format":"date-time"}' WHERE `name` = 'xbt date hour minute UTC offset';
UPDATE `xbt` SET `openapi30_map` = '{"type":"string", "pattern:"^[0-9]{4}-W(0[1-9]|[1-4][0-9]|5[0123])-[1-7]T((([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](|(\\.[0-9]+)))|(24:00:00))$"}' WHERE `name` = 'xbt year week day time';
UPDATE `xbt` SET `openapi30_map` = '{"type":"string", "pattern:"^[0-9]{4}-W(0[1-9]|[1-4][0-9]|5[0123])-[1-7]T((([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](|(\\.[0-9]+)))|(24:00:00))Z$"}' WHERE `name` = 'xbt year week day time UTC';
UPDATE `xbt` SET `openapi30_map` = '{"type":"string", "pattern:"^[0-9]{4}-W(0[1-9]|[1-4][0-9]|5[0123])-[1-7]T((([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](|(\\.[0-9]+)))|(24:00:00))([\\+|\\-]([0-1][0-9]|2[0-3]):[0-5][0-9])$"}' WHERE `name` = 'xbt year week day time UTC offset';


-- Alter rest of CC tables
ALTER TABLE `dt` AUTO_INCREMENT = 1000001;
ALTER TABLE `dt_manifest` AUTO_INCREMENT = 1000001;
ALTER TABLE `cdt_awd_pri_xps_type_map` AUTO_INCREMENT = 1000001;
ALTER TABLE `cdt_sc_awd_pri_xps_type_map` AUTO_INCREMENT = 1000001;
ALTER TABLE `bdt_pri_restri` AUTO_INCREMENT = 1000001;
ALTER TABLE `bdt_sc_pri_restri` AUTO_INCREMENT = 1000001;
ALTER TABLE `cdt_awd_pri` AUTO_INCREMENT = 1000001;
ALTER TABLE `cdt_sc_awd_pri` AUTO_INCREMENT = 1000001;
ALTER TABLE `xbt` AUTO_INCREMENT = 1000001;
ALTER TABLE `xbt_manifest` AUTO_INCREMENT = 1000001;
ALTER TABLE `agency_id_list` AUTO_INCREMENT = 1000001;
ALTER TABLE `agency_id_list_manifest` AUTO_INCREMENT = 1000001;
ALTER TABLE `agency_id_list_value` AUTO_INCREMENT = 1000001;
ALTER TABLE `agency_id_list_value_manifest` AUTO_INCREMENT = 1000001;

SET foreign_key_checks = 0;

-- Delete old CC data
DELETE FROM `acc` WHERE `acc`.`acc_id` < 1000001;
DELETE FROM `acc_manifest` WHERE `acc_manifest`.`acc_manifest_id` < 1000001;
DELETE FROM `app_user` WHERE `app_user`.`app_user_id` < 1000001;
DELETE FROM `ascc` WHERE `ascc`.`ascc_id` < 1000001;
DELETE FROM `ascc_manifest` WHERE `ascc_manifest`.`ascc_manifest_id` < 1000001;
DELETE FROM `asccp` WHERE `asccp`.`asccp_id` < 1000001;
DELETE FROM `asccp_manifest` WHERE `asccp_manifest`.`asccp_manifest_id` < 1000001;
DELETE FROM `bcc` WHERE `bcc`.`bcc_id` < 1000001;
DELETE FROM `bcc_manifest` WHERE `bcc_manifest`.`bcc_manifest_id` < 1000001;
DELETE FROM `bccp` WHERE `bccp`.`bccp_id` < 1000001;
DELETE FROM `bccp_manifest` WHERE `bccp_manifest`.`bccp_manifest_id` < 1000001;
DELETE FROM `code_list` WHERE `code_list`.`code_list_id` < 1000001;
DELETE FROM `code_list_manifest` WHERE `code_list_manifest`.`code_list_manifest_id` < 1000001;
DELETE FROM `code_list_value` WHERE `code_list_value`.`code_list_value_id` < 1000001;
DELETE FROM `code_list_value_manifest` WHERE `code_list_value_manifest`.`code_list_value_manifest_id` < 1000001;
DELETE FROM `seq_key` WHERE `seq_key`.`seq_key_id` < 1000001;
DELETE FROM `namespace` WHERE `namespace`.`namespace_id` < 1000001;
DELETE FROM `log` WHERE `log`.`log_id` < 1000001;

DELETE FROM `dt` WHERE `dt`.`dt_id` < 1000001;
DELETE FROM `dt_manifest` WHERE `dt_manifest`.`dt_manifest_id` < 1000001;
DELETE FROM `dt_sc` WHERE `dt_sc`.`dt_sc_id` < 1000001;
DELETE FROM `dt_sc_manifest` WHERE `dt_sc_manifest`.`dt_sc_manifest_id` < 1000001;
DELETE FROM `cdt_awd_pri_xps_type_map` WHERE `cdt_awd_pri_xps_type_map`.`cdt_awd_pri_xps_type_map_id` < 1000001;
DELETE FROM `cdt_sc_awd_pri_xps_type_map` WHERE `cdt_sc_awd_pri_xps_type_map`.`cdt_sc_awd_pri_xps_type_map_id` < 1000001;
DELETE FROM `bdt_pri_restri` WHERE `bdt_pri_restri`.`bdt_pri_restri_id` < 1000001;
DELETE FROM `bdt_sc_pri_restri` WHERE `bdt_sc_pri_restri`.`bdt_sc_pri_restri_id` < 1000001;
DELETE FROM `cdt_awd_pri` WHERE `cdt_awd_pri`.`cdt_awd_pri_id` < 1000001;
DELETE FROM `cdt_sc_awd_pri` WHERE `cdt_sc_awd_pri`.`cdt_sc_awd_pri_id` < 1000001;
DELETE FROM `xbt` WHERE `xbt`.`xbt_id` < 1000001;
DELETE FROM `xbt_manifest` WHERE `xbt_manifest`.`xbt_manifest_id` < 1000001;
DELETE FROM `agency_id_list` WHERE `agency_id_list`.`agency_id_list_id` < 1000001;
DELETE FROM `agency_id_list_manifest` WHERE `agency_id_list_manifest`.`agency_id_list_manifest_id` < 1000001;
DELETE FROM `agency_id_list_value` WHERE `agency_id_list_value`.`agency_id_list_value_id` < 1000001;
DELETE FROM `agency_id_list_value_manifest` WHERE `agency_id_list_value_manifest`.`agency_id_list_value_manifest_id` < 1000001;
DELETE FROM `release` WHERE `release`.`release_id` < 1000001;

