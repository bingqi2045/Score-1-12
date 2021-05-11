-- ----------------------------------------------------
-- Migration script for Score v2.1.0                 --
--                                                   --
-- Author: Hakju Oh <hakju.oh@nist.gov>              --
-- ----------------------------------------------------

-- Add 'sysadm' user.
INSERT INTO `app_user` (`app_user_id`, `login_id`, `password`, `name`, `organization`, `is_developer`, `is_enabled`)
VALUES (0, 'sysadm', '', 'System', 'System', 1, 1)
ON DUPLICATE KEY UPDATE `app_user_id` = 0, `login_id` = 'sysadm', `password` = '', `name` = 'System', `organization` = 'System', `is_developer` = 1, `is_enabled` = 1;

-- Add `message` table.
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
    `message_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `sender_id` bigint(20) unsigned NOT NULL COMMENT 'The user who created this record.',
    `recipient_id` bigint(20) unsigned NOT NULL COMMENT 'The user who is a target to possess this record.',
    `subject` text COMMENT 'A subject of the message',
    `body` mediumtext COMMENT 'A body of the message.',
    `body_content_type` varchar(50) NOT NULL DEFAULT 'text/plain' COMMENT 'A content type of the body',
    `is_read` tinyint(1) DEFAULT '0' COMMENT 'An indicator whether this record is read or not.',
    `creation_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was first created.',
    PRIMARY KEY (`message_id`),
    KEY `message_sender_id_fk` (`sender_id`),
    KEY `message_recipient_id_fk` (`recipient_id`),
    CONSTRAINT `message_recipient_id_fk` FOREIGN KEY (`recipient_id`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `message_sender_id_fk` FOREIGN KEY (`sender_id`) REFERENCES `app_user` (`app_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- REPLACE 'XMLSchemaBuiltinType_1' to 'XMLSchemaBuiltinType_1_patterns' and DELETE 'XMLSchemaBuiltinType_1'
UPDATE `module_xbt_manifest` SET `module_set_assignment_id` = (SELECT `module_set_assignment`.`module_set_assignment_id` FROM `module` JOIN `module_set_assignment` ON `module`.`module_id` = `module_set_assignment`.`module_id` WHERE `module`.`name` = 'XMLSchemaBuiltinType_1_patterns')	WHERE `module_set_assignment_id` = (SELECT `module_set_assignment`.`module_set_assignment_id` FROM `module` JOIN `module_set_assignment` ON `module`.`module_id` = `module_set_assignment`.`module_id` WHERE `module`.`name` = 'XMLSchemaBuiltinType_1');
UPDATE `module_dep` SET  `depending_module_set_assignment_id` = (SELECT `module_set_assignment`.`module_set_assignment_id` FROM `module` JOIN `module_set_assignment` ON `module`.`module_id` = `module_set_assignment`.`module_id` WHERE `module`.`name` = 'XMLSchemaBuiltinType_1_patterns') WHERE `depending_module_set_assignment_id` = (SELECT `module_set_assignment`.`module_set_assignment_id` FROM `module` JOIN `module_set_assignment` ON `module`.`module_id` = `module_set_assignment`.`module_id` WHERE `module`.`name` = 'XMLSchemaBuiltinType_1');
DELETE FROM `module_set_assignment` WHERE `module_set_assignment`.`module_id` = (SELECT `module`.`module_id` FROM `module` WHERE `module`.`name` = 'XMLSchemaBuiltinType_1');