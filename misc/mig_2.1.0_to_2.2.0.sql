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

ALTER TABLE `dt_sc` ADD COLUMN `prev_dt_sc_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
                 ADD COLUMN `next_dt_sc_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.',
                 ADD CONSTRAINT `dt_sc_prev_dt_sc_id_fk` FOREIGN KEY (`prev_dt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`),
                 ADD CONSTRAINT `dt_sc_next_dt_sc_id_fk` FOREIGN KEY (`next_dt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`);
