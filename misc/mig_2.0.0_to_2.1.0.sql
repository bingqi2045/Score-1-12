-- ----------------------------------------------------
-- Migration script for Score v2.1.0                 --
--                                                   --
-- Author: Hakju Oh <hakju.oh@nist.gov>              --
--         Kwanghoon Lee <kwanghoon.lee@nist.gov>    --
-- ----------------------------------------------------

-- ADD `module_set_id` to `module`
ALTER TABLE `module` ADD COLUMN `module_set_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates a module set.' AFTER `module_dir_id`,
ADD CONSTRAINT `module_module_set_id_fk` FOREIGN KEY (`module_set_id`) REFERENCES `module_set` (`module_set_id`);
UPDATE `module` SET `module_set_id` = (SELECT `module_set`.`module_set_id` FROM `module_set` LIMIT 1);
ALTER TABLE `module` MODIFY COLUMN `module_set_id` bigint(20) unsigned NOT NULL COMMENT 'This indicates a module set.';

-- ADD `module_set_id` to `module_dir`
ALTER TABLE `module_dir` ADD COLUMN `module_set_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates a module set.' AFTER `parent_module_dir_id`,
ADD CONSTRAINT `module_dir_module_set_id_fk` FOREIGN KEY (`module_set_id`) REFERENCES `module_set` (`module_set_id`);
UPDATE `module_dir` SET `module_set_id` = (SELECT `module_set`.`module_set_id` FROM `module_set` LIMIT 1);
ALTER TABLE `module_dir` MODIFY COLUMN `module_set_id` bigint(20) unsigned NOT NULL COMMENT 'This indicates a module set.';

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

-- DROP unused tables
DROP TABLE `module_dep`;
DROP TABLE `module_set_assignment`;
