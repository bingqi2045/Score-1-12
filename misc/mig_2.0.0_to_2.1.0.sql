-- ----------------------------------------------------
-- Migration script for Score v2.1.0                 --
--                                                   --
-- Author: Hakju Oh <hakju.oh@nist.gov>              --
-- ----------------------------------------------------

-- Add 'sysadm' user.
INSERT INTO `app_user` (`app_user_id`, `login_id`, `password`, `name`, `organization`, `is_developer`, `is_enabled`)
VALUES (0, 'sysadm', '', 'System', 'System', 1, 1)
ON DUPLICATE KEY UPDATE `login_id` = 'sysadm', `password` = '', `name` = 'System', `organization` = 'System', `is_developer` = 1, `is_enabled` = 1;

-- Add `message` table.
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
    `message_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `sender_id` bigint(20) unsigned NOT NULL COMMENT 'The user who created this record.',
    `recipient_id` bigint(20) unsigned NOT NULL COMMENT 'The user who is a target to possess this record.',
    `body` mediumtext COMMENT 'A body of the message.',
    `is_read` tinyint(1) DEFAULT '0' COMMENT 'An indicator whether this record is read or not.',
    `creation_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was first created.',
    PRIMARY KEY (`message_id`),
    KEY `message_sender_id_fk` (`sender_id`),
    KEY `message_recipient_id_fk` (`recipient_id`),
    CONSTRAINT `message_sender_id_fk` FOREIGN KEY (`sender_id`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `message_recipient_id_fk` FOREIGN KEY (`recipient_id`) REFERENCES `app_user` (`app_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
