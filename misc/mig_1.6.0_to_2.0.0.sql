-- ----------------------------------------------------
-- Migration script for Score v2.0.0                 --
--                                                   --
-- Author: Hakju Oh <hakju.oh@nist.gov>              --
--         Kwanghoon Lee <kwanghoon.lee@nist.gov>    --
-- ----------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM `app_user` WHERE `app_user_id` = 0;
INSERT INTO `app_user` (`login_id`, `password`, `name`, `organization`, `is_developer`)
VALUES
	('sysadm', '$2a$10$N6lPv6XNewi0eryQqlow5.dSEgOnFlxYGyGVIGZxXY1dimEPRmhOu', 'System Administrator', 'System', 1);

UPDATE `app_user` SET `app_user_id` = 0 WHERE `login_id` = 'sysadm';

SET @max_user_id = (SELECT MAX(`app_user_id`) + 1 FROM `app_user`);
SET @sql = CONCAT('ALTER TABLE `app_user` AUTO_INCREMENT = ', @max_user_id);
PREPARE st FROM @sql;
EXECUTE st;

DROP TABLE IF EXISTS `app_group`;
CREATE TABLE `app_group` (
    `app_group_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL DEFAULT '',
    `authority` int(10) unsigned NOT NULL,
    PRIMARY KEY (`app_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `app_group` (`app_group_id`, `name`, `authority`)
VALUES
	(1, 'Administrator', 0),
	(2, 'Developer', 10),
	(3, 'User', 99);

DROP TABLE IF EXISTS `app_group_user`;
CREATE TABLE `app_group_user` (
    `app_group_id` bigint(20) unsigned NOT NULL,
    `app_user_id` bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`app_group_id`,`app_user_id`),
    KEY `app_user_id` (`app_user_id`),
    CONSTRAINT `app_group_user_fk_1` FOREIGN KEY (`app_group_id`) REFERENCES `app_group` (`app_group_id`),
    CONSTRAINT `app_group_user_fk_2` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`app_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `app_group_user` (`app_group_id`, `app_user_id`)
VALUES
	(1, 0);

INSERT INTO `app_group_user` (`app_group_id`, `app_user_id`)
SELECT (SELECT `app_group_id` FROM `app_group` WHERE `name` = 'Developer'), `app_user_id`
FROM `app_user`
WHERE `app_user`.`is_developer` = 1 AND `app_user`.`app_user_id` != 0;

INSERT INTO `app_group_user` (`app_group_id`, `app_user_id`)
SELECT (SELECT `app_group_id` FROM `app_group` WHERE `name` = 'User'), `app_user_id`
FROM `app_user`
WHERE `app_user`.`is_developer` = 0;

-- Create syntax for TABLE 'app_permission'
DROP TABLE IF EXISTS `app_permission`;
CREATE TABLE `app_permission` (
    `app_permission_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `segment` varchar(64) NOT NULL DEFAULT '',
    `object` varchar(256) NOT NULL DEFAULT '',
    `operation` varchar(64) NOT NULL DEFAULT 'Unprepared',
    `description` tinytext,
    PRIMARY KEY (`app_permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'app_permission_group'
DROP TABLE IF EXISTS `app_permission_group`;
CREATE TABLE `app_permission_group` (
    `app_permission_id` bigint(20) unsigned NOT NULL,
    `app_group_id` bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`app_permission_id`,`app_group_id`),
    KEY `app_permission_id` (`app_permission_id`),
    KEY `app_permission_group_fk_1` (`app_group_id`),
    CONSTRAINT `app_permission_group_fk_1` FOREIGN KEY (`app_group_id`) REFERENCES `app_group` (`app_group_id`),
    CONSTRAINT `app_permission_group_fk_2` FOREIGN KEY (`app_permission_id`) REFERENCES `app_permission` (`app_permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create `revision` table for revision management
DROP TABLE IF EXISTS `revision`;
CREATE TABLE `revision` (
    `revision_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `revision_num` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'This is an incremental integer. It tracks changes in each component. If a change is made to a component after it has been published, the component receives a new revision number. Revision number can be 1, 2, and so on.',
    `revision_tracking_num` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'This supports the ability to undo changes during a revision (life cycle of a revision is from the component''s WIP state to PUBLISHED state). REVISION_TRACKING_NUM can be 1, 2, and so on.',
    `revision_action` varchar(20) DEFAULT NULL COMMENT 'This indicates the action associated with the record.',
    `reference` varchar(100) CHARACTER SET ascii NOT NULL DEFAULT '',
    `snapshot` JSON,
    `prev_revision_id` bigint(20) unsigned DEFAULT NULL,
    `next_revision_id` bigint(20) unsigned DEFAULT NULL,
    `created_by` bigint(20) unsigned NOT NULL,
    `creation_timestamp` datetime(6) NOT NULL,
    PRIMARY KEY (`revision_id`),
    KEY `reference` (`reference`),
    KEY `revision_created_by_fk` (`created_by`),
    KEY `revision_prev_revision_id_fk` (`prev_revision_id`),
    KEY `revision_next_revision_id_fk` (`next_revision_id`),
    CONSTRAINT `revision_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `revision_prev_revision_id_fk` FOREIGN KEY (`prev_revision_id`) REFERENCES `revision` (`revision_id`),
    CONSTRAINT `revision_next_revision_id_fk` FOREIGN KEY (`next_revision_id`) REFERENCES `revision` (`revision_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create `comment` table for comments
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `comment_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `reference` varchar(100) CHARACTER SET ascii NOT NULL DEFAULT '',
    `comment` text,
    `is_hidden` tinyint(1) NOT NULL DEFAULT '0',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
    `prev_comment_id` bigint(20) unsigned DEFAULT NULL,
    `created_by` bigint(20) unsigned NOT NULL,
    `creation_timestamp` datetime(6) NOT NULL,
    `last_update_timestamp` datetime(6) NOT NULL,
    PRIMARY KEY (`comment_id`),
    KEY `reference` (`reference`),
    KEY `comment_created_by_fk` (`created_by`),
    KEY `comment_prev_comment_id_fk` (`prev_comment_id`),
    CONSTRAINT `comment_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `comment_prev_comment_id_fk` FOREIGN KEY (`prev_comment_id`) REFERENCES `comment` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- This table purposes to replace `seq_key` column on `ascc` and `bcc` tables.
CREATE TABLE `seq_key` (
    `seq_key_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `from_acc_id` bigint(20) unsigned NOT NULL,
    `type` enum('ascc','bcc') DEFAULT NULL,
    `cc_id` bigint(20) unsigned NOT NULL,
    `prev_seq_key_id` bigint(20) unsigned DEFAULT NULL,
    `next_seq_key_id` bigint(20) unsigned DEFAULT NULL,
    PRIMARY KEY (`seq_key_id`),
    KEY `seq_key_from_acc_id` (`from_acc_id`),
    KEY `seq_key_prev_seq_key_id_fk` (`prev_seq_key_id`),
    KEY `seq_key_next_seq_key_id_fk` (`next_seq_key_id`),
    CONSTRAINT `seq_key_from_acc_id_fk` FOREIGN KEY (`from_acc_id`) REFERENCES `acc` (`acc_id`),
    CONSTRAINT `seq_key_prev_seq_key_id_fk` FOREIGN KEY (`prev_seq_key_id`) REFERENCES `seq_key` (`seq_key_id`),
    CONSTRAINT `seq_key_next_seq_key_id_fk` FOREIGN KEY (`next_seq_key_id`) REFERENCES `seq_key` (`seq_key_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Update `namespace_id` = NULL for all user extension groups.
UPDATE `acc` SET `namespace_id` = NULL WHERE `acc`.`oagis_component_type` = 4;
UPDATE `asccp`, (SELECT `asccp`.`asccp_id` FROM `acc` JOIN `asccp` ON `acc`.`acc_id` = `asccp`.`role_of_acc_id` WHERE `acc`.`oagis_component_type` = 4) AS t SET `asccp`.`namespace_id` = NULL WHERE `asccp`.`asccp_id` = t.`asccp_id`;

-- Modify `release` table.
ALTER TABLE `release` MODIFY COLUMN `state` varchar(20) DEFAULT 'Initialized' COMMENT 'This indicates the revision life cycle state of the Release.',
                      MODIFY COLUMN `namespace_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Foreign key to the NAMESPACE table. It identifies the namespace used with the release. It is particularly useful for a library that uses a single namespace such like the OAGIS 10.x. A library that uses multiple namespace but has a main namespace may also use this column as a specific namespace can be override at the module level.',
                      ADD COLUMN `guid` varchar(36) CHARACTER SET ascii NOT NULL COMMENT 'GUID of the release.' AFTER `release_id`,
                      ADD COLUMN `release_license` longtext COMMENT 'License associated with the release.' AFTER `release_note`;

-- Update `state` to 'Published' for current release.
UPDATE `release` SET `state` = 'Published';
UPDATE `release` SET `guid` = '67e8f19b-17a1-46cf-a808-7201c6319431';

-- Adding `Working` release.
INSERT INTO `release` (`release_num`, `guid`, `release_note`, `namespace_id`, `created_by`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `state`)
VALUES
('Working', '5ef5c4d1-1860-4845-88bf-aab79259a186', NULL, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 'Published');

-- Making relations between `acc` and `release` tables.
ALTER TABLE `acc` MODIFY COLUMN `state` varchar(20) COMMENT 'Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the ACC.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.';
UPDATE `acc` SET `state` = 'Editing' where `state` = '1';
UPDATE `acc` SET `state` = 'Candidate' where `state` = '2';
UPDATE `acc` SET `state` = 'Published' where `state` = '3';

CREATE TABLE `acc_manifest` (
    `acc_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `module_id` bigint(20) unsigned,
    `acc_id` bigint(20) unsigned NOT NULL,
    `based_acc_manifest_id` bigint(20) unsigned,
    `conflict` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'This indicates that there is a conflict between self and relationship.',
    `revision_id` bigint(20) unsigned COMMENT 'A foreign key pointed to revision for the current record.',
    `prev_acc_manifest_id` bigint(20) unsigned,
    `next_acc_manifest_id` bigint(20) unsigned,
    PRIMARY KEY (`acc_manifest_id`),
    KEY `acc_manifest_acc_id_fk` (`acc_id`),
    KEY `acc_manifest_based_acc_manifest_id_fk` (`based_acc_manifest_id`),
    KEY `acc_manifest_release_id_fk` (`release_id`),
    KEY `acc_manifest_module_id_fk` (`module_id`),
    KEY `acc_manifest_revision_id_fk` (`revision_id`),
    KEY `acc_manifest_prev_acc_manifest_id_fk` (`prev_acc_manifest_id`),
    KEY `acc_manifest_next_acc_manifest_id_fk` (`next_acc_manifest_id`),
    CONSTRAINT `acc_manifest_acc_id_fk` FOREIGN KEY (`acc_id`) REFERENCES `acc` (`acc_id`),
    CONSTRAINT `acc_manifest_based_acc_manifest_id_fk` FOREIGN KEY (`based_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `acc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `acc_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
    CONSTRAINT `acc_manifest_revision_id_fk` FOREIGN KEY (`revision_id`) REFERENCES `revision` (`revision_id`),
    CONSTRAINT `acc_manifest_prev_acc_manifest_id_fk` FOREIGN KEY (`prev_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `acc_manifest_next_acc_manifest_id_fk` FOREIGN KEY (`next_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Updating `based_acc_id`
UPDATE `acc`, `acc` as base, `acc` as current
SET `acc`.`based_acc_id` = current.`acc_id`
WHERE `acc`.`based_acc_id` = base.`acc_id` AND base.`acc_id` = current.`current_acc_id`;

-- Inserting initial acc_manifest records for 10.x release
INSERT `acc_manifest` (`release_id`, `acc_id`, `module_id`)
SELECT
    `release`.`release_id`,
    `acc`.`acc_id`, `acc`.`module_id`
FROM `acc` JOIN `release` ON `acc`.`release_id` = `release`.`release_id`
WHERE `acc`.`state` = 'Published';

SET @sql = CONCAT('ALTER TABLE `acc_manifest` AUTO_INCREMENT = ', (SELECT MAX(acc_manifest_id) + 1 FROM acc_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Updating `based_acc_manifest_id` for 10.x release
UPDATE `acc_manifest`, (
    SELECT
        `acc_manifest`.`acc_manifest_id`, b.`acc_manifest_id` as `based_acc_manifest_id`
    FROM `acc_manifest`
    JOIN `acc` ON `acc_manifest`.`acc_id` = `acc`.`acc_id` AND `acc_manifest`.`release_id` = `acc`.`release_id`
    JOIN `acc_manifest` AS b ON `acc`.`based_acc_id` = b.`acc_id` AND  b.`release_id` = `acc`.`release_id`
) t
SET `acc_manifest`.`based_acc_manifest_id` = t.`based_acc_manifest_id`
WHERE `acc_manifest`.`acc_manifest_id` = t.`acc_manifest_id`;

-- Copying acc_manifest records for 'Working' release from 10.x release
INSERT `acc_manifest` (`release_id`, `acc_id`, `module_id`, `based_acc_manifest_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`,
    `acc_manifest`.`acc_id`, `acc_manifest`.`module_id`, `acc_manifest`.`based_acc_manifest_id`
FROM `acc_manifest` JOIN `release` ON `acc_manifest`.`release_id` = `release`.`release_id`
WHERE `release`.`release_num` != 'Working';

-- Updating `based_acc_manifest_id` for Working release
UPDATE `acc_manifest`, (
    SELECT
        `acc_manifest`.`acc_manifest_id`, b.`acc_manifest_id` as `based_acc_manifest_id`
    FROM `acc_manifest`
    JOIN `acc` ON `acc_manifest`.`acc_id` = `acc`.`acc_id` AND `acc_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
    JOIN `acc_manifest` AS b ON `acc`.`based_acc_id` = b.`acc_id` AND  b.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
) t
SET `acc_manifest`.`based_acc_manifest_id` = t.`based_acc_manifest_id`
WHERE `acc_manifest`.`acc_manifest_id` = t.`acc_manifest_id`;

SET @sql = CONCAT('ALTER TABLE `acc_manifest` AUTO_INCREMENT = ', (SELECT MAX(acc_manifest_id) + 1 FROM acc_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Updating core component states' names.
UPDATE `acc`
	JOIN `app_user` ON `acc`.`owner_user_id` = `app_user`.`app_user_id`
SET `acc`.`state` = IF(`acc`.`revision_num` = 1 AND `acc`.`revision_tracking_num` = 1, 'Published', 'Candidate')
WHERE `acc`.`state` = 'Published' AND `app_user`.`is_developer` = 1 AND `acc`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

UPDATE `acc`
	JOIN `app_user` ON `acc`.`owner_user_id` = `app_user`.`app_user_id`
SET `acc`.`state` = IF(`acc`.`revision_num` = 1 AND `acc`.`revision_tracking_num` = 1, 'Published', 'Production')
WHERE `acc`.`state` = 'Published' AND (`app_user`.`is_developer` != 1 OR `acc`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `acc`
	JOIN `app_user` ON `acc`.`owner_user_id` = `app_user`.`app_user_id`
SET `acc`.`state` = 'QA'
WHERE `acc`.`state` = 'Candidate' AND (`app_user`.`is_developer` != 1 OR `acc`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

-- Add deprecated annotations
ALTER TABLE `acc` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.\n\nRELEASE_ID is an incremental integer. It is an unformatted counter part of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. A release ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\\n\\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released. USER_EXTENSION_GROUP component type is never part of a release.\\n\\nUnpublished components cannot be released.\\n\\nThis column is NULL for the current record.',
                  MODIFY COLUMN `current_acc_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.\n\nThis is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\\n\\nIt is noted that although this is a foreign key by definition, we don''t specify a foreign key in the data model. This is because when an entity is deleted the current record won''t exist anymore.\\n\\nThe value of this column for the current record should be left NULL.',
                  ADD COLUMN `prev_acc_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
                  ADD COLUMN `next_acc_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.',
                  ADD CONSTRAINT `acc_prev_acc_id_fk` FOREIGN KEY (`prev_acc_id`) REFERENCES `acc` (`acc_id`),
                  ADD CONSTRAINT `acc_next_acc_id_fk` FOREIGN KEY (`next_acc_id`) REFERENCES `acc` (`acc_id`);

-- Add indices
CREATE INDEX `acc_guid_idx` ON `acc` (`guid`);
CREATE INDEX `acc_last_update_timestamp_desc_idx` ON `acc` (`last_update_timestamp` DESC);


-- Making relations between `asccp` and `release` tables.
ALTER TABLE `asccp` MODIFY COLUMN `state` varchar(20) COMMENT 'Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the ASCCP.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.';
UPDATE `asccp` SET `state` = 'Editing' where `state` = '1';
UPDATE `asccp` SET `state` = 'Candidate' where `state` = '2';
UPDATE `asccp` SET `state` = 'Published' where `state` = '3';

CREATE TABLE `asccp_manifest` (
    `asccp_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `module_id` bigint(20) unsigned,
    `asccp_id` bigint(20) unsigned NOT NULL,
    `role_of_acc_manifest_id` bigint(20) unsigned NOT NULL,
    `conflict` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'This indicates that there is a conflict between self and relationship.',
    `revision_id` bigint(20) unsigned COMMENT 'A foreign key pointed to revision for the current record.',
    `prev_asccp_manifest_id` bigint(20) unsigned,
    `next_asccp_manifest_id` bigint(20) unsigned,
    PRIMARY KEY (`asccp_manifest_id`),
    KEY `asccp_manifest_asccp_id_fk` (`asccp_id`),
    KEY `asccp_manifest_role_of_acc_manifest_id_fk` (`role_of_acc_manifest_id`),
    KEY `asccp_manifest_release_id_fk` (`release_id`),
    KEY `asccp_manifest_module_id_fk` (`module_id`),
    KEY `asccp_manifest_revision_id_fk` (`revision_id`),
    KEY `asccp_manifest_prev_asccp_manifest_id_fk` (`prev_asccp_manifest_id`),
    KEY `asccp_manifest_next_asccp_manifest_id_fk` (`next_asccp_manifest_id`),
    CONSTRAINT `asccp_manifest_asccp_id_fk` FOREIGN KEY (`asccp_id`) REFERENCES `asccp` (`asccp_id`),
    CONSTRAINT `asccp_manifest_role_of_acc_manifest_id_fk` FOREIGN KEY (`role_of_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `asccp_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `asccp_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
    CONSTRAINT `asccp_manifest_revision_id_fk` FOREIGN KEY (`revision_id`) REFERENCES `revision` (`revision_id`),
    CONSTRAINT `asccp_manifest_prev_asccp_manifest_id_fk` FOREIGN KEY (`prev_asccp_manifest_id`) REFERENCES `asccp_manifest` (`asccp_manifest_id`),
    CONSTRAINT `asccp_manifest_next_asccp_manifest_id_fk` FOREIGN KEY (`next_asccp_manifest_id`) REFERENCES `asccp_manifest` (`asccp_manifest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Updating `role_of_acc_id`
UPDATE `asccp`, `acc`, `acc` as current
SET `asccp`.`role_of_acc_id` = current.`acc_id`
WHERE `acc`.`acc_id` = `asccp`.`role_of_acc_id` AND `acc`.`acc_id` = current.`current_acc_id`;

-- Inserting initial asccp_manifest records for 10.x release
INSERT `asccp_manifest` (`release_id`, `module_id`, `asccp_id`, `role_of_acc_manifest_id`)
SELECT
    `release`.`release_id`, `asccp`.`module_id`,
    `asccp`.`asccp_id`, `acc_manifest`.`acc_manifest_id`
FROM `asccp`
JOIN `release` ON `asccp`.`release_id` = `release`.`release_id`
JOIN `acc_manifest` ON `asccp`.`role_of_acc_id` = `acc_manifest`.`acc_id` AND `acc_manifest`.`release_id` = `release`.`release_id`
WHERE `asccp`.`state` = 'Published';

SET @sql = CONCAT('ALTER TABLE `asccp_manifest` AUTO_INCREMENT = ', (SELECT MAX(asccp_manifest_id) + 1 FROM asccp_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Copying asccp_manifest records for 'Working' release from 10.x release
INSERT `asccp_manifest` (`release_id`, `module_id`, `asccp_id`, `role_of_acc_manifest_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'), `asccp`.`module_id`,
    `asccp`.`asccp_id`, `acc_manifest`.`acc_manifest_id`
FROM `asccp`
JOIN `acc_manifest` ON `asccp`.`role_of_acc_id` = `acc_manifest`.`acc_id` AND `acc_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
WHERE `asccp`.`state` = 'Published' and `asccp`.`current_asccp_id` is not null;

SET @sql = CONCAT('ALTER TABLE `asccp_manifest` AUTO_INCREMENT = ', (SELECT MAX(asccp_manifest_id) + 1 FROM asccp_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Updating core component states' names.
UPDATE `asccp`
	JOIN `app_user` ON `asccp`.`owner_user_id` = `app_user`.`app_user_id`
SET `asccp`.`state` = IF(`asccp`.`revision_num` = 1 AND `asccp`.`revision_tracking_num` = 1, 'Published', 'Candidate')
WHERE `asccp`.`state` = 'Published' AND `app_user`.`is_developer` = 1 AND `asccp`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

UPDATE `asccp`
	JOIN `app_user` ON `asccp`.`owner_user_id` = `app_user`.`app_user_id`
SET `asccp`.`state` = IF(`asccp`.`revision_num` = 1 AND `asccp`.`revision_tracking_num` = 1, 'Published', 'Production')
WHERE `asccp`.`state` = 'Published' AND (`app_user`.`is_developer` != 1 OR `asccp`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `asccp`
	JOIN `app_user` ON `asccp`.`owner_user_id` = `app_user`.`app_user_id`
SET `asccp`.`state` = 'QA'
WHERE `asccp`.`state` = 'Candidate' AND (`app_user`.`is_developer` != 1 OR `asccp`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

-- Add deprecated annotations
ALTER TABLE `asccp` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.\n\nRELEASE_ID is an incremental integer. It is an unformatted counter part of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. A release ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\n\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released. USER_EXTENSION_GROUP component type is never part of a release.\n\nUnpublished components cannot be released.\n\nThis column is NULLl for the current record.',
                    MODIFY COLUMN `current_asccp_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.\n\nThis is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\n\nIt is noted that although this is a foreign key by definition, we don''t specify a foreign key in the data model. This is because when an entity is deleted the current record won''t exist anymore.\n\nThe value of this column for the current record should be left NULL.',
                    ADD COLUMN `prev_asccp_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
                    ADD COLUMN `next_asccp_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.',
                    ADD CONSTRAINT `asccp_prev_asccp_id_fk` FOREIGN KEY (`prev_asccp_id`) REFERENCES `asccp` (`asccp_id`),
                    ADD CONSTRAINT `asccp_next_asccp_id_fk` FOREIGN KEY (`next_asccp_id`) REFERENCES `asccp` (`asccp_id`);

-- Add indices
CREATE INDEX `asccp_guid_idx` ON `asccp` (`guid`);
CREATE INDEX `asccp_last_update_timestamp_desc_idx` ON `asccp` (`last_update_timestamp` DESC);


-- Making relations between `dt` and `release` tables.
ALTER TABLE `dt` MODIFY COLUMN `state` varchar(20) COMMENT 'Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the DT.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.';
UPDATE `dt` SET `state` = 'Editing' where `state` = '1';
UPDATE `dt` SET `state` = 'Candidate' where `state` = '2';
UPDATE `dt` SET `state` = 'Published' where `state` = '3';
UPDATE `dt` SET `revision_num` = 1, `revision_tracking_num` = 1, `revision_action` = 1;

CREATE TABLE `dt_manifest` (
    `dt_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `module_id` bigint(20) unsigned,
    `dt_id` bigint(20) unsigned NOT NULL,
    `conflict` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'This indicates that there is a conflict between self and relationship.',
    `revision_id` bigint(20) unsigned COMMENT 'A foreign key pointed to revision for the current record.',
    `prev_dt_manifest_id` bigint(20) unsigned,
    `next_dt_manifest_id` bigint(20) unsigned,
    PRIMARY KEY (`dt_manifest_id`),
    KEY `dt_manifest_dt_id_fk` (`dt_id`),
    KEY `dt_manifest_release_id_fk` (`release_id`),
    KEY `dt_manifest_module_id_fk` (`module_id`),
    KEY `dt_manifest_revision_id_fk` (`revision_id`),
    KEY `dt_manifest_prev_dt_manifest_id_fk` (`prev_dt_manifest_id`),
    KEY `dt_manifest_next_dt_manifest_id_fk` (`next_dt_manifest_id`),
    CONSTRAINT `dt_manifest_dt_id_fk` FOREIGN KEY (`dt_id`) REFERENCES `dt` (`dt_id`),
    CONSTRAINT `dt_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `dt_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
    CONSTRAINT `dt_manifest_revision_id_fk` FOREIGN KEY (`revision_id`) REFERENCES `revision` (`revision_id`),
    CONSTRAINT `dt_manifest_prev_dt_manifest_id_fk` FOREIGN KEY (`prev_dt_manifest_id`) REFERENCES `dt_manifest` (`dt_manifest_id`),
    CONSTRAINT `dt_manifest_next_dt_manifest_id_fk` FOREIGN KEY (`next_dt_manifest_id`) REFERENCES `dt_manifest` (`dt_manifest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inserting initial dt_manifest records for 10.x release
INSERT `dt_manifest` (`release_id`, `module_id`, `dt_id`)
SELECT
    `release`.`release_id`, `dt`.`module_id`,
    `dt`.`dt_id`
FROM `dt` JOIN `release` ON `dt`.`release_id` = `release`.`release_id`
WHERE `dt`.`state` = 'Published';

SET @sql = CONCAT('ALTER TABLE `dt_manifest` AUTO_INCREMENT = ', (SELECT MAX(dt_manifest_id) + 1 FROM dt_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Copying dt_manifest records for 'Working' release from 10.x release
INSERT `dt_manifest` (`release_id`, `module_id`, `dt_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`,
    `dt_manifest`.`module_id`, `dt_manifest`.`dt_id`
FROM `dt_manifest` JOIN `release` ON `dt_manifest`.`release_id` = `release`.`release_id`
WHERE `release`.`release_num` != 'Working';

SET @sql = CONCAT('ALTER TABLE `dt_manifest` AUTO_INCREMENT = ', (SELECT MAX(dt_manifest_id) + 1 FROM dt_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Updating core component states' names.
UPDATE `dt`
	JOIN `app_user` ON `dt`.`owner_user_id` = `app_user`.`app_user_id`
SET `dt`.`state` = IF(`dt`.`revision_num` = 1 AND `dt`.`revision_tracking_num` = 1, 'Published', 'Candidate')
WHERE `dt`.`state` = 'Published' AND `app_user`.`is_developer` = 1 AND `dt`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

UPDATE `dt`
	JOIN `app_user` ON `dt`.`owner_user_id` = `app_user`.`app_user_id`
SET `dt`.`state` = IF(`dt`.`revision_num` = 1 AND `dt`.`revision_tracking_num` = 1, 'Published', 'Production')
WHERE `dt`.`state` = 'Published' AND (`app_user`.`is_developer` != 1 OR `dt`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `dt`
	JOIN `app_user` ON `dt`.`owner_user_id` = `app_user`.`app_user_id`
SET `dt`.`state` = 'QA'
WHERE `dt`.`state` = 'Candidate' AND (`app_user`.`is_developer` != 1 OR `dt`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

-- Add deprecated annotations
ALTER TABLE `dt` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.\n\nRELEASE_ID is an incremental integer. It is an unformatted counter part of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. A release ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\n\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released. USER_EXTENSION_GROUP component type is never part of a release.\n\nUnpublished components cannot be released.\n\nThis column is NULL for the current record.',
                 MODIFY COLUMN `current_bdt_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.\n\nThis is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\n\nIt is noted that although this is a foreign key by definition, we don''t specify a foreign key in the data model. This is because when an entity is deleted the current record won''t exist anymore.\n\nThe value of this column for the current record should be left NULL.\n\nThe column name is specific to BDT because, the column does not apply to CDT.',
                 ADD COLUMN `prev_dt_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
                 ADD COLUMN `next_dt_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.',
                 ADD CONSTRAINT `dt_prev_dt_id_fk` FOREIGN KEY (`prev_dt_id`) REFERENCES `dt` (`dt_id`),
                 ADD CONSTRAINT `dt_next_dt_id_fk` FOREIGN KEY (`next_dt_id`) REFERENCES `dt` (`dt_id`);

-- Add indices
CREATE INDEX `dt_guid_idx` ON `dt` (`guid`);
CREATE INDEX `dt_last_update_timestamp_desc_idx` ON `dt` (`last_update_timestamp` DESC);

-- Drop unique index
ALTER TABLE `dt` DROP INDEX `dt_uk1`;


-- Making relations between `dt_sc` and `release` tables.
CREATE TABLE `dt_sc_manifest` (
    `dt_sc_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `dt_sc_id` bigint(20) unsigned NOT NULL,
    `owner_dt_manifest_id` bigint(20) unsigned NOT NULL,
    `conflict` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'This indicates that there is a conflict between self and relationship.',
    `prev_dt_sc_manifest_id` bigint(20) unsigned,
    `next_dt_sc_manifest_id` bigint(20) unsigned,
    PRIMARY KEY (`dt_sc_manifest_id`),
    KEY `dt_sc_manifest_dt_sc_id_fk` (`dt_sc_id`),
    KEY `dt_sc_manifest_release_id_fk` (`release_id`),
    KEY `dt_sc_manifest_owner_dt_manifest_id_fk` (`owner_dt_manifest_id`),
    KEY `dt_sc_prev_dt_sc_manifest_id_fk` (`prev_dt_sc_manifest_id`),
    KEY `dt_sc_next_dt_sc_manifest_id_fk` (`next_dt_sc_manifest_id`),
    CONSTRAINT `dt_sc_manifest_dt_sc_id_fk` FOREIGN KEY (`dt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`),
    CONSTRAINT `dt_sc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `dt_sc_manifest_owner_dt_manifest_id_fk` FOREIGN KEY (`owner_dt_manifest_id`) REFERENCES `dt_manifest` (`dt_manifest_id`),
    CONSTRAINT `dt_sc_prev_dt_sc_manifest_id_fk` FOREIGN KEY (`prev_dt_sc_manifest_id`) REFERENCES `dt_sc_manifest` (`dt_sc_manifest_id`),
    CONSTRAINT `dt_sc_next_dt_sc_manifest_id_fk` FOREIGN KEY (`next_dt_sc_manifest_id`) REFERENCES `dt_sc_manifest` (`dt_sc_manifest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inserting initial dt_sc_manifest records for 10.x release
INSERT `dt_sc_manifest` (`release_id`, `dt_sc_id`, `owner_dt_manifest_id`)
SELECT
    `release`.`release_id`,
    `dt_sc`.`dt_sc_id`, `dt_manifest`.`dt_manifest_id`
FROM `dt_sc` JOIN `dt_manifest` ON `dt_sc`.`owner_dt_id` = `dt_manifest`.`dt_id`
             JOIN `release` ON `dt_manifest`.`release_id` = `release`.`release_id`
WHERE `release`.`release_num` != 'Working'
ORDER BY `release`.`release_id`;

SET @sql = CONCAT('ALTER TABLE `dt_sc_manifest` AUTO_INCREMENT = ', (SELECT MAX(dt_sc_manifest_id) + 1 FROM dt_sc_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Copying dt_sc_manifest records for 'Working' release from 10.x release
INSERT `dt_sc_manifest` (`release_id`, `dt_sc_id`, `owner_dt_manifest_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`,
    `dt_sc_manifest`.`dt_sc_id`, `dt_manifest`.`dt_manifest_id`
    FROM `dt_sc_manifest`
	JOIN `dt_sc` on `dt_sc_manifest`.`dt_sc_id` = `dt_sc`.`dt_sc_id`
	JOIN `dt_manifest` on `dt_sc`.`owner_dt_id` = `dt_manifest`.`dt_id` and `dt_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');


SET @sql = CONCAT('ALTER TABLE `dt_sc_manifest` AUTO_INCREMENT = ', (SELECT MAX(dt_sc_manifest_id) + 1 FROM dt_sc_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add indices
CREATE INDEX `dt_sc_guid_idx` ON `dt_sc` (`guid`);

-- Drop unique index
ALTER TABLE `dt_sc` DROP INDEX `dt_sc_uk1`;


-- Making relations between `bccp` and `release` tables.
ALTER TABLE `bccp` MODIFY COLUMN `state` varchar(20) COMMENT 'Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the BCCP.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.';
UPDATE `bccp` SET `state` = 'Editing' where `state` = '1';
UPDATE `bccp` SET `state` = 'Candidate' where `state` = '2';
UPDATE `bccp` SET `state` = 'Published' where `state` = '3';

CREATE TABLE `bccp_manifest` (
    `bccp_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `module_id` bigint(20) unsigned,
    `bccp_id` bigint(20) unsigned NOT NULL,
    `bdt_manifest_id` bigint(20) unsigned NOT NULL,
    `conflict` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'This indicates that there is a conflict between self and relationship.',
    `revision_id` bigint(20) unsigned COMMENT 'A foreign key pointed to revision for the current record.',
    `prev_bccp_manifest_id` bigint(20) unsigned,
    `next_bccp_manifest_id` bigint(20) unsigned,
    PRIMARY KEY (`bccp_manifest_id`),
    KEY `bccp_manifest_bccp_id_fk` (`bccp_id`),
    KEY `bccp_manifest_bdt_manifest_id_fk` (`bdt_manifest_id`),
    KEY `bccp_manifest_release_id_fk` (`release_id`),
    KEY `bccp_manifest_module_id_fk` (`module_id`),
    KEY `bccp_manifest_revision_id_fk` (`revision_id`),
    KEY `bccp_manifest_prev_bccp_manifest_id_fk` (`prev_bccp_manifest_id`),
    KEY `bccp_manifest_next_bccp_manifest_id_fk` (`next_bccp_manifest_id`),
    CONSTRAINT `bccp_manifest_bccp_id_fk` FOREIGN KEY (`bccp_id`) REFERENCES `bccp` (`bccp_id`),
    CONSTRAINT `bccp_manifest_bdt_manifest_id_fk` FOREIGN KEY (`bdt_manifest_id`) REFERENCES `dt_manifest` (`dt_manifest_id`),
    CONSTRAINT `bccp_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `bccp_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
    CONSTRAINT `bccp_manifest_revision_id_fk` FOREIGN KEY (`revision_id`) REFERENCES `revision` (`revision_id`),
    CONSTRAINT `bccp_manifest_prev_bccp_manifest_id_fk` FOREIGN KEY (`prev_bccp_manifest_id`) REFERENCES `bccp_manifest` (`bccp_manifest_id`),
    CONSTRAINT `bccp_manifest_next_bccp_manifest_id_fk` FOREIGN KEY (`next_bccp_manifest_id`) REFERENCES `bccp_manifest` (`bccp_manifest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inserting initial bccp_manifest records for 10.x release
INSERT `bccp_manifest` (`release_id`, `module_id`, `bccp_id`, `bdt_manifest_id`)
SELECT
    `release`.`release_id`, `bccp`.`module_id`,
    `bccp`.`bccp_id`, `dt_manifest`.`dt_manifest_id`
FROM `bccp` JOIN `release` ON `bccp`.`release_id` = `release`.`release_id`
            JOIN `dt_manifest` ON `dt_manifest`.`dt_id` = `bccp`.`bdt_id`
    AND `dt_manifest`.`release_id` = `release`.`release_id`
WHERE `bccp`.`state` = 'Published';

SET @sql = CONCAT('ALTER TABLE `bccp_manifest` AUTO_INCREMENT = ', (SELECT MAX(bccp_manifest_id) + 1 FROM bccp_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Copying bccp_manifest records for 'Working' release from 10.x release
INSERT `bccp_manifest` (`release_id`, `module_id`, `bccp_id`, `bdt_manifest_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`,
    `bccp_manifest`.`module_id`, `bccp_manifest`.`bccp_id`, `dt_manifest`.`dt_manifest_id`
FROM `bccp_manifest` JOIN `bccp` ON `bccp_manifest`.`bccp_id` = `bccp`.`bccp_id`
	JOIN `dt_manifest` ON `dt_manifest`.`dt_id` = `bccp`.`bdt_id` AND `dt_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');


SET @sql = CONCAT('ALTER TABLE `bccp_manifest` AUTO_INCREMENT = ', (SELECT MAX(bccp_manifest_id) + 1 FROM bccp_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Updating core component states' names.
UPDATE `bccp`
	JOIN `app_user` ON `bccp`.`owner_user_id` = `app_user`.`app_user_id`
SET `bccp`.`state` = IF(`bccp`.`revision_num` = 1 AND `bccp`.`revision_tracking_num` = 1, 'Published', 'Candidate')
WHERE `bccp`.`state` = 'Published' AND `app_user`.`is_developer` = 1 AND `bccp`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

UPDATE `bccp`
	JOIN `app_user` ON `bccp`.`owner_user_id` = `app_user`.`app_user_id`
SET `bccp`.`state` = IF(`bccp`.`revision_num` = 1 AND `bccp`.`revision_tracking_num` = 1, 'Published', 'Production')
WHERE `bccp`.`state` = 'Published' AND (`app_user`.`is_developer` != 1 OR `bccp`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `bccp`
	JOIN `app_user` ON `bccp`.`owner_user_id` = `app_user`.`app_user_id`
SET `bccp`.`state` = 'QA'
WHERE `bccp`.`state` = 'Candidate' AND (`app_user`.`is_developer` != 1 OR `bccp`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

-- Add deprecated annotations
ALTER TABLE `bccp` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.\n\nRELEASE_ID is an incremental integer. It is an unformatted counter part of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. A release ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\n\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released. USER_EXTENSION_GROUP component type is never part of a release.\n\nUnpublished components cannot be released.\n\nThis column is NULLl for the current record.',
                   MODIFY COLUMN `current_bccp_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.\n\nThis is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\n\nIt is noted that although this is a foreign key by definition, we don''t specify a foreign key in the data model. This is because when an entity is deleted the current record won''t exist anymore.\n\nThe value of this column for the current record should be left NULL.',
                   ADD COLUMN `prev_bccp_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
                   ADD COLUMN `next_bccp_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.',
                   ADD CONSTRAINT `bccp_prev_bccp_id_fk` FOREIGN KEY (`prev_bccp_id`) REFERENCES `bccp` (`bccp_id`),
                   ADD CONSTRAINT `bccp_next_bccp_id_fk` FOREIGN KEY (`next_bccp_id`) REFERENCES `bccp` (`bccp_id`);

-- Add indices
CREATE INDEX `bccp_guid_idx` ON `bccp` (`guid`);
CREATE INDEX `bccp_last_update_timestamp_desc_idx` ON `bccp` (`last_update_timestamp` DESC);


-- Making relations between `ascc` and `release` tables.
ALTER TABLE `ascc` MODIFY COLUMN `state` varchar(20) COMMENT 'Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the BCC.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.',
                   MODIFY COLUMN `seq_key` int(11) COMMENT '@deprecated since 2.0.0. This indicates the order of the associations among other siblings. A valid value is positive integer. The SEQ_KEY at the CC side is localized. In other words, if an ACC is based on another ACC, SEQ_KEY of ASCCs or BCCs of the former ACC starts at 1 again.',
                   ADD COLUMN `seq_key_id` bigint(20) unsigned AFTER `seq_key`,
                   ADD CONSTRAINT `ascc_seq_key_id_fk` FOREIGN KEY (`seq_key_id`) REFERENCES `seq_key` (`seq_key_id`);
UPDATE `ascc` SET `state` = 'Editing' where `state` = '1';
UPDATE `ascc` SET `state` = 'Candidate' where `state` = '2';
UPDATE `ascc` SET `state` = 'Published' where `state` = '3';

CREATE TABLE `ascc_manifest` (
    `ascc_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `ascc_id` bigint(20) unsigned NOT NULL,
    `from_acc_manifest_id` bigint(20) unsigned NOT NULL,
    `to_asccp_manifest_id` bigint(20) unsigned NOT NULL,
    `conflict` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'This indicates that there is a conflict between self and relationship.',
    `prev_ascc_manifest_id` bigint(20) unsigned,
    `next_ascc_manifest_id` bigint(20) unsigned,
    PRIMARY KEY (`ascc_manifest_id`),
    KEY `ascc_manifest_ascc_id_fk` (`ascc_id`),
    KEY `ascc_manifest_release_id_fk` (`release_id`),
    KEY `ascc_manifest_from_acc_manifest_id_fk` (`from_acc_manifest_id`),
    KEY `ascc_manifest_to_asccp_manifest_id_fk` (`to_asccp_manifest_id`),
    KEY `ascc_manifest_prev_ascc_manifest_id_fk` (`prev_ascc_manifest_id`),
    KEY `ascc_manifest_next_ascc_manifest_id_fk` (`next_ascc_manifest_id`),
    CONSTRAINT `ascc_manifest_ascc_id_fk` FOREIGN KEY (`ascc_id`) REFERENCES `ascc` (`ascc_id`),
    CONSTRAINT `ascc_manifest_from_acc_manifest_id_fk` FOREIGN KEY (`from_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `ascc_manifest_to_asccp_manifest_id_fk` FOREIGN KEY (`to_asccp_manifest_id`) REFERENCES `asccp_manifest` (`asccp_manifest_id`),
    CONSTRAINT `ascc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `ascc_manifest_prev_ascc_manifest_id_fk` FOREIGN KEY (`prev_ascc_manifest_id`) REFERENCES `ascc_manifest` (`ascc_manifest_id`),
    CONSTRAINT `ascc_manifest_next_ascc_manifest_id_fk` FOREIGN KEY (`next_ascc_manifest_id`) REFERENCES `ascc_manifest` (`ascc_manifest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Updating `from_acc_id`
UPDATE `ascc`, `acc`, `acc` as current
SET `ascc`.`from_acc_id` = current.`acc_id`
WHERE `acc`.`acc_id` = `ascc`.`from_acc_id` AND `acc`.`acc_id` = current.`current_acc_id`;

-- Updating `to_asccp_id`
UPDATE `ascc`, `asccp`, `asccp` as current
SET `ascc`.`to_asccp_id` = current.`asccp_id`
WHERE `asccp`.`asccp_id` = `ascc`.`to_asccp_id` AND `asccp`.`asccp_id` = current.`current_asccp_id`;

-- Inserting initial ascc_manifest records for 10.x release
INSERT `ascc_manifest` (`release_id`, `ascc_id`, `from_acc_manifest_id`, `to_asccp_manifest_id`)
SELECT
    r1.`release_id`,
    `ascc`.`ascc_id`, `acc_manifest`.`acc_manifest_id`, `asccp_manifest`.`asccp_manifest_id`
FROM `ascc`
JOIN `release` as r1 ON r1.`release_num` != 'Working' AND `ascc`.`release_id` = r1.`release_id`
JOIN `acc` ON `ascc`.`from_acc_id` = `acc`.`acc_id`
JOIN `acc_manifest` ON `acc`.`acc_id` = `acc_manifest`.`acc_id`
JOIN `release` as r2 ON r2.`release_num` != 'Working' AND `acc_manifest`.`release_id` = r2.`release_id`
JOIN `asccp` ON `ascc`.`to_asccp_id` = `asccp`.`asccp_id`
JOIN `asccp_manifest` ON `asccp`.`asccp_id` = `asccp_manifest`.`asccp_id`
JOIN `release` as r3 ON r3.`release_num` != 'Working' AND `asccp_manifest`.`release_id` = r3.`release_id`
WHERE `ascc`.`state` = 'Published';

SET @sql = CONCAT('ALTER TABLE `ascc_manifest` AUTO_INCREMENT = ', (SELECT MAX(ascc_manifest_id) + 1 FROM ascc_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Copying ascc_manifest records for 'Working' release from 10.x release
INSERT `ascc_manifest` (`release_id`, `ascc_id`, `from_acc_manifest_id`, `to_asccp_manifest_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`,
    `ascc_manifest`.`ascc_id`, `acc_manifest`.`acc_manifest_id`, `asccp_manifest`.`asccp_manifest_id`
FROM `ascc_manifest`
	JOIN `ascc` ON `ascc`.`ascc_id` = `ascc_manifest`.`ascc_id`
	JOIN `acc_manifest` ON `acc_manifest`.`acc_id` = `ascc`.`from_acc_id` and `acc_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
	JOIN `asccp_manifest` ON `asccp_manifest`.`asccp_id` = `ascc`.`to_asccp_id` and `asccp_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

SET @sql = CONCAT('ALTER TABLE `ascc_manifest` AUTO_INCREMENT = ', (SELECT MAX(ascc_manifest_id) + 1 FROM ascc_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Updating core component states' names.
UPDATE `ascc`
	JOIN `app_user` ON `ascc`.`owner_user_id` = `app_user`.`app_user_id`
SET `ascc`.`state` = IF(`ascc`.`revision_num` = 1 AND `ascc`.`revision_tracking_num` = 1, 'Published', 'Candidate')
WHERE `ascc`.`state` = 'Published' AND `app_user`.`is_developer` = 1 AND `ascc`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

UPDATE `ascc`
	JOIN `app_user` ON `ascc`.`owner_user_id` = `app_user`.`app_user_id`
SET `ascc`.`state` = IF(`ascc`.`revision_num` = 1 AND `ascc`.`revision_tracking_num` = 1, 'Published', 'Production')
WHERE `ascc`.`state` = 'Published' AND (`app_user`.`is_developer` != 1 OR `ascc`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `ascc`
	JOIN `app_user` ON `ascc`.`owner_user_id` = `app_user`.`app_user_id`
SET `ascc`.`state` = 'QA'
WHERE `ascc`.`state` = 'Candidate' AND (`app_user`.`is_developer` != 1 OR `ascc`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

-- Add deprecated annotations
ALTER TABLE `ascc` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.\n\nRELEASE_ID is an incremental integer. It is an unformatted counterpart of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. RELEASE_ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\n\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released.\n\nUnpublished components cannot be released.\n\nThis column is NULL for the current record.',
                   MODIFY COLUMN `current_ascc_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.\n\nThis is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\n\nIt is noted that although this is a foreign key by definition, we don''t specify a foreign key in the data model. This is because when an entity is deleted the current record won''t exist anymore.\n\nThe value of this column for the current record should be left NULL.',
                   ADD COLUMN `prev_ascc_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
                   ADD COLUMN `next_ascc_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.',
                   ADD CONSTRAINT `ascc_prev_ascc_id_fk` FOREIGN KEY (`prev_ascc_id`) REFERENCES `ascc` (`ascc_id`),
                   ADD CONSTRAINT `ascc_next_ascc_id_fk` FOREIGN KEY (`next_ascc_id`) REFERENCES `ascc` (`ascc_id`);

-- Add indices
CREATE INDEX `ascc_guid_idx` ON `ascc` (`guid`);
CREATE INDEX `ascc_last_update_timestamp_desc_idx` ON `ascc` (`last_update_timestamp` DESC);


-- Making relations between `bcc` and `release` tables.
ALTER TABLE `bcc` MODIFY COLUMN `state` varchar(20) COMMENT 'Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the BCC.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.',
                  MODIFY COLUMN `seq_key` int(11) COMMENT '@deprecated since 2.0.0. This indicates the order of the associations among other siblings. A valid value is positive integer. The SEQ_KEY at the CC side is localized. In other words, if an ACC is based on another ACC, SEQ_KEY of ASCCs or BCCs of the former ACC starts at 1 again.',
                  ADD COLUMN `seq_key_id` bigint(20) unsigned AFTER `seq_key`,
                  ADD CONSTRAINT `bcc_seq_key_id_fk` FOREIGN KEY (`seq_key_id`) REFERENCES `seq_key` (`seq_key_id`);
UPDATE `bcc` SET `state` = 'Editing' where `state` = '1';
UPDATE `bcc` SET `state` = 'Candidate' where `state` = '2';
UPDATE `bcc` SET `state` = 'Published' where `state` = '3';

ALTER TABLE `bcc` MODIFY COLUMN `is_nillable` tinyint(1) NOT NULL DEFAULT '0' COMMENT '@deprecated since 2.0.0 in favor of impossibility of nillable association (element reference) in XML schema.\n\nIndicate whether the field can have a NULL This is corresponding to the nillable flag in the XML schema.';

CREATE TABLE `bcc_manifest` (
    `bcc_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `bcc_id` bigint(20) unsigned NOT NULL,
    `from_acc_manifest_id` bigint(20) unsigned NOT NULL,
    `to_bccp_manifest_id` bigint(20) unsigned NOT NULL,
    `conflict` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'This indicates that there is a conflict between self and relationship.',
    `prev_bcc_manifest_id` bigint(20) unsigned,
    `next_bcc_manifest_id` bigint(20) unsigned,
    PRIMARY KEY (`bcc_manifest_id`),
    KEY `bcc_manifest_bcc_id_fk` (`bcc_id`),
    KEY `bcc_manifest_release_id_fk` (`release_id`),
    KEY `bcc_manifest_from_acc_manifest_id_fk` (`from_acc_manifest_id`),
    KEY `bcc_manifest_to_bccp_manifest_id_fk` (`to_bccp_manifest_id`),
    KEY `bcc_manifest_prev_bcc_manifest_id_fk` (`prev_bcc_manifest_id`),
    KEY `bcc_manifest_next_bcc_manifest_id_fk` (`next_bcc_manifest_id`),
    CONSTRAINT `bcc_manifest_bcc_id_fk` FOREIGN KEY (`bcc_id`) REFERENCES `bcc` (`bcc_id`),
    CONSTRAINT `bcc_manifest_from_acc_manifest_id_fk` FOREIGN KEY (`from_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `bcc_manifest_to_bccp_manifest_id_fk` FOREIGN KEY (`to_bccp_manifest_id`) REFERENCES `bccp_manifest` (`bccp_manifest_id`),
    CONSTRAINT `bcc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `bcc_manifest_prev_bcc_manifest_id_fk` FOREIGN KEY (`prev_bcc_manifest_id`) REFERENCES `bcc_manifest` (`bcc_manifest_id`),
    CONSTRAINT `bcc_manifest_next_bcc_manifest_id_fk` FOREIGN KEY (`next_bcc_manifest_id`) REFERENCES `bcc_manifest` (`bcc_manifest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Updating `from_acc_id`
UPDATE `bcc`, `acc`, `acc` as current
SET `bcc`.`from_acc_id` = current.`acc_id`
WHERE `acc`.`acc_id` = `bcc`.`from_acc_id` AND `acc`.`acc_id` = current.`current_acc_id`;

-- Updating `to_bccp_id`
UPDATE `bcc`, `bccp`, `bccp` as current
SET `bcc`.`to_bccp_id` = current.`bccp_id`
WHERE `bccp`.`bccp_id` = `bcc`.`to_bccp_id` AND `bccp`.`bccp_id` = current.`current_bccp_id`;

-- Inserting initial bcc_manifest records for 10.x release
INSERT `bcc_manifest` (`release_id`, `bcc_id`, `from_acc_manifest_id`, `to_bccp_manifest_id`)
SELECT
    r1.`release_id`,
    `bcc`.`bcc_id`, `acc_manifest`.`acc_manifest_id`, `bccp_manifest`.`bccp_manifest_id`
FROM `bcc`
JOIN `release` as r1 ON r1.`release_num` != 'Working' AND `bcc`.`release_id` = r1.`release_id`
JOIN `acc` ON `bcc`.`from_acc_id` = `acc`.`acc_id`
JOIN `acc_manifest` ON `acc`.`acc_id` = `acc_manifest`.`acc_id`
JOIN `release` as r2 ON r2.`release_num` != 'Working' AND `acc_manifest`.`release_id` = r2.`release_id`
JOIN `bccp` ON `bcc`.`to_bccp_id` = `bccp`.`bccp_id`
JOIN `bccp_manifest` ON `bccp`.`bccp_id` = `bccp_manifest`.`bccp_id`
JOIN `release` as r3 ON r3.`release_num` != 'Working' AND `bccp_manifest`.`release_id` = r3.`release_id`
WHERE `bcc`.`state` = 'Published';

SET @sql = CONCAT('ALTER TABLE `bcc_manifest` AUTO_INCREMENT = ', (SELECT MAX(bcc_manifest_id) + 1 FROM bcc_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Copying bcc_manifest records for 'Working' release from 10.x release
INSERT `bcc_manifest` (`release_id`, `bcc_id`, `from_acc_manifest_id`, `to_bccp_manifest_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`,
    `bcc_manifest`.`bcc_id`, `acc_manifest`.`acc_manifest_id`, `bccp_manifest`.`bccp_manifest_id`
FROM `bcc_manifest`
	JOIN `bcc` ON `bcc`.`bcc_id` = `bcc_manifest`.`bcc_id`
	JOIN `acc_manifest` ON `acc_manifest`.`acc_id` = `bcc`.`from_acc_id` and `acc_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
	JOIN `bccp_manifest` ON `bccp_manifest`.`bccp_id` = `bcc`.`to_bccp_id` and `bccp_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

-- Updating core component states' names.
UPDATE `bcc`
	JOIN `app_user` ON `bcc`.`owner_user_id` = `app_user`.`app_user_id`
SET `bcc`.`state` = IF(`bcc`.`revision_num` = 1 AND `bcc`.`revision_tracking_num` = 1, 'Published', 'Candidate')
WHERE `bcc`.`state` = 'Published' AND `app_user`.`is_developer` = 1 AND `bcc`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

UPDATE `bcc`
	JOIN `app_user` ON `bcc`.`owner_user_id` = `app_user`.`app_user_id`
SET `bcc`.`state` = IF(`bcc`.`revision_num` = 1 AND `bcc`.`revision_tracking_num` = 1, 'Published', 'Production')
WHERE `bcc`.`state` = 'Published' AND (`app_user`.`is_developer` != 1 OR `bcc`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `bcc`
	JOIN `app_user` ON `bcc`.`owner_user_id` = `app_user`.`app_user_id`
SET `bcc`.`state` = 'QA'
WHERE `bcc`.`state` = 'Candidate' AND (`app_user`.`is_developer` != 1 OR `bcc`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

-- Add deprecated annotations
ALTER TABLE `bcc` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.\n\nRELEASE_ID is an incremental integer. It is an unformatted counterpart of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. RELEASE_ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\n\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released.\n\nUnpublished components cannot be released.\n\nThis column is NULLl for the current record.',
                  MODIFY COLUMN `current_bcc_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.\n\nThis is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\n\nIt is noted that although this is a foreign key by definition, we don''t specify a foreign key in the data model. This is because when an entity is deleted the current record won''t exist anymore.\n\nThe value of this column for the current record should be left NULL.',
                  ADD COLUMN `prev_bcc_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
                  ADD COLUMN `next_bcc_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.',
                  ADD CONSTRAINT `bcc_prev_bcc_id_fk` FOREIGN KEY (`prev_bcc_id`) REFERENCES `bcc` (`bcc_id`),
                  ADD CONSTRAINT `bcc_next_bcc_id_fk` FOREIGN KEY (`next_bcc_id`) REFERENCES `bcc` (`bcc_id`);

-- Add indices
CREATE INDEX `bcc_guid_idx` ON `bcc` (`guid`);
CREATE INDEX `bcc_last_update_timestamp_desc_idx` ON `bcc` (`last_update_timestamp` DESC);


-- Making relations between `code_list` and `release` tables.
ALTER TABLE `code_list`
    ADD COLUMN `owner_user_id` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.' AFTER `created_by`,
    ADD COLUMN `is_deprecated` tinyint(1) DEFAULT '0' COMMENT 'Indicates whether the code list is deprecated and should not be reused (i.e., no new reference to this record should be allowed).' AFTER `extensible_indicator`,
    ADD COLUMN `prev_code_list_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    ADD COLUMN `next_code_list_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.',
    MODIFY `state` VARCHAR (20),
    ADD CONSTRAINT `code_list_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `code_list_prev_code_list_id_fk` FOREIGN KEY (`prev_code_list_id`) REFERENCES `code_list` (`code_list_id`),
    ADD CONSTRAINT `code_list_next_code_list_id_fk` FOREIGN KEY (`next_code_list_id`) REFERENCES `code_list` (`code_list_id`);

UPDATE `code_list` SET `owner_user_id` = `last_updated_by`;
UPDATE `code_list` SET `state` = 'WIP' WHERE `state` = 'Editing';

ALTER TABLE `code_list`
    DROP KEY `code_list_uk1`,
    DROP KEY `code_list_uk2`;

CREATE TABLE `code_list_manifest` (
    `code_list_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `module_id` bigint(20) unsigned DEFAULT NULL,
    `code_list_id` bigint(20) unsigned NOT NULL,
    `based_code_list_manifest_id` bigint(20) unsigned DEFAULT NULL,
    `conflict` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'This indicates that there is a conflict between self and relationship.',
    `revision_id` bigint(20) unsigned COMMENT 'A foreign key pointed to revision for the current record.',
    `prev_code_list_manifest_id` bigint(20) unsigned,
    `next_code_list_manifest_id` bigint(20) unsigned,
    PRIMARY KEY (`code_list_manifest_id`),
    KEY `code_list_manifest_code_list_id_fk` (`code_list_id`),
    KEY `code_list_manifest_based_code_list_manifest_id_fk` (`based_code_list_manifest_id`),
    KEY `code_list_manifest_release_id_fk` (`release_id`),
    KEY `code_list_manifest_module_id_fk` (`module_id`),
    KEY `code_list_manifest_revision_id_fk` (`revision_id`),
    KEY `code_list_manifest_prev_code_list_manifest_id_fk` (`prev_code_list_manifest_id`),
    KEY `code_list_manifest_next_code_list_manifest_id_fk` (`next_code_list_manifest_id`),
    CONSTRAINT `code_list_manifest_code_list_id_fk` FOREIGN KEY (`code_list_id`) REFERENCES `code_list` (`code_list_id`),
    CONSTRAINT `code_list_manifest_based_code_list_manifest_id_fk` FOREIGN KEY (`based_code_list_manifest_id`) REFERENCES `code_list_manifest` (`code_list_manifest_id`),
    CONSTRAINT `code_list_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
    CONSTRAINT `code_list_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `code_list_manifest_revision_id_fk` FOREIGN KEY (`revision_id`) REFERENCES `revision` (`revision_id`),
    CONSTRAINT `code_list_manifest_prev_code_list_manifest_id_fk` FOREIGN KEY (`prev_code_list_manifest_id`) REFERENCES `code_list_manifest` (`code_list_manifest_id`),
    CONSTRAINT `code_list_manifest_next_code_list_manifest_id_fk` FOREIGN KEY (`next_code_list_manifest_id`) REFERENCES `code_list_manifest` (`code_list_manifest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inserting initial code_list_manifest records for 10.x release
INSERT `code_list_manifest` (`release_id`, `module_id`, `code_list_id`)
SELECT
    `release`.`release_id`,
    `code_list`.`module_id`,
    `code_list`.`code_list_id`
FROM `code_list`, `release`
WHERE `release`.`release_num` != 'Working'
ORDER BY `release_id`, `code_list_id`;

SET @sql = CONCAT('ALTER TABLE `code_list_manifest` AUTO_INCREMENT = ', (SELECT MAX(code_list_manifest_id) + 1 FROM code_list_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Copying code_list_manifest records for 'Working' release from 10.x release
INSERT `code_list_manifest` (`release_id`, `module_id`, `code_list_id`, `based_code_list_manifest_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`,
    `code_list_manifest`.`module_id`, `code_list_manifest`.`code_list_id`, `code_list_manifest`.`based_code_list_manifest_id`
FROM `code_list_manifest` JOIN `release` ON `code_list_manifest`.`release_id` = `release`.`release_id`
WHERE `release`.`release_num` != 'Working';

SET @sql = CONCAT('ALTER TABLE `code_list_manifest` AUTO_INCREMENT = ', (SELECT MAX(code_list_manifest_id) + 1 FROM code_list_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Updating `based_code_list_manifest_id`
UPDATE `code_list_manifest`, (
    SELECT a.`code_list_manifest_id`, `code_list`.`code_list_id`, b.`code_list_manifest_id` as `based_code_list_manifest_id`, `code_list`.`based_code_list_id`
    FROM `code_list` JOIN `code_list_manifest` a ON `code_list`.`code_list_id` = a.`code_list_id`
                     JOIN `code_list_manifest` b ON `code_list`.`based_code_list_id` = b.`code_list_id`
    WHERE `code_list`.`based_code_list_id` IS NOT NULL AND a.`release_id` = b.`release_id`
) t
SET `code_list_manifest`.`based_code_list_manifest_id` = t.`based_code_list_manifest_id`
WHERE `code_list_manifest`.`code_list_manifest_id` = t.`code_list_manifest_id`;

-- Making relations between `code_list_value` and `release` tables.
ALTER TABLE `code_list_value`
    ADD COLUMN `guid` varchar(41) NOT NULL COMMENT 'GUID of the code list. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.' AFTER `code_list_value_id`,
    ADD COLUMN `is_deprecated` tinyint(1) DEFAULT '0' COMMENT 'Indicates whether the code list value is deprecated and should not be reused (i.e., no new reference to this record should be allowed).' AFTER `extension_indicator`,
    ADD COLUMN `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the code list.',
    ADD COLUMN `owner_user_id` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn''t rollback the ownership.',
    ADD COLUMN `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the code list.',
    ADD COLUMN `creation_timestamp` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Timestamp when the code list was created.',
    ADD COLUMN `last_update_timestamp` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Timestamp when the code list was last updated.',
    ADD COLUMN `prev_code_list_value_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the previous history record.',
    ADD COLUMN `next_code_list_value_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A self-foreign key to indicate the next history record.',
    ADD CONSTRAINT `code_list_value_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `code_list_value_owner_user_id_fk` FOREIGN KEY (`owner_user_id`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `code_list_value_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `code_list_value_prev_code_list_value_id_fk` FOREIGN KEY (`prev_code_list_value_id`) REFERENCES `code_list_value` (`code_list_value_id`),
    ADD CONSTRAINT `code_list_value_next_code_list_value_id_fk` FOREIGN KEY (`next_code_list_value_id`) REFERENCES `code_list_value` (`code_list_value_id`);

UPDATE `code_list_value`, `code_list`
SET `code_list_value`.`created_by` = `code_list`.`created_by`,
    `code_list_value`.`owner_user_id` = `code_list`.`owner_user_id`,
    `code_list_value`.`last_updated_by` = `code_list`.`last_updated_by`,
    `code_list_value`.`creation_timestamp` = `code_list`.`creation_timestamp`,
    `code_list_value`.`last_update_timestamp` = `code_list`.`last_update_timestamp`
WHERE `code_list_value`.`code_list_id` = `code_list`.`code_list_id`;

-- Updating prev/next code_list_value_id
UPDATE `code_list_value`, (
    SELECT
        `code_list_value`.`code_list_value_id`, `code_list_value`.`value`, `code_list`.`code_list_id`,
        base_value.`code_list_value_id` as base_value_id, base_value.`value` as base_value, base.`code_list_id` as base_id
    FROM `code_list_value`
    JOIN `code_list` ON `code_list_value`.`code_list_id` = `code_list`.`code_list_id`
     AND `code_list`.`based_code_list_id` is not null
    JOIN `code_list` AS base ON `code_list`.`based_code_list_id` = base.`code_list_id`
    JOIN `code_list_value` AS base_value ON base_value.`code_list_id` = base.`code_list_id`
     AND `code_list_value`.`value` = base_value.`value`
) t
SET `code_list_value`.`prev_code_list_value_id` = t.`base_value_id`
WHERE `code_list_value`.`code_list_value_id` = t.`code_list_value_id`;

UPDATE `code_list_value`, (
    SELECT
        `code_list_value`.`code_list_value_id`, `code_list_value`.`value`, `code_list`.`code_list_id`,
        base_value.`code_list_value_id` as base_value_id, base_value.`value` as base_value, base.`code_list_id` as base_id
    FROM `code_list_value`
    JOIN `code_list` ON `code_list_value`.`code_list_id` = `code_list`.`code_list_id`
     AND `code_list`.`based_code_list_id` is not null
    JOIN `code_list` AS base ON `code_list`.`based_code_list_id` = base.`code_list_id`
    JOIN `code_list_value` AS base_value ON base_value.`code_list_id` = base.`code_list_id`
     AND `code_list_value`.`value` = base_value.`value`
) t
SET `code_list_value`.`next_code_list_value_id` = t.`code_list_value_id`
WHERE `code_list_value`.`code_list_value_id` = t.`base_value_id`;

CREATE TABLE `code_list_value_manifest` (
    `code_list_value_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `code_list_value_id` bigint(20) unsigned NOT NULL,
    `code_list_manifest_id` bigint(20) unsigned NOT NULL,
    `conflict` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'This indicates that there is a conflict between self and relationship.',
    `prev_code_list_value_manifest_id` bigint(20) unsigned,
    `next_code_list_value_manifest_id` bigint(20) unsigned,
    PRIMARY KEY (`code_list_value_manifest_id`),
    KEY `code_list_value_manifest_code_list_value_id_fk` (`code_list_value_id`),
    KEY `code_list_value_manifest_release_id_fk` (`release_id`),
    KEY `code_list_value_manifest_code_list_manifest_id_fk` (`code_list_manifest_id`),
    KEY `code_list_value_manifest_prev_code_list_value_manifest_id_fk` (`prev_code_list_value_manifest_id`),
    KEY `code_list_value_manifest_next_code_list_value_manifest_id_fk` (`next_code_list_value_manifest_id`),
    CONSTRAINT `code_list_value_manifest_code_list_value_id_fk` FOREIGN KEY (`code_list_value_id`) REFERENCES `code_list_value` (`code_list_value_id`),
    CONSTRAINT `code_list_value_manifest_code_list_manifest_id_fk` FOREIGN KEY (`code_list_manifest_id`) REFERENCES `code_list_manifest` (`code_list_manifest_id`),
    CONSTRAINT `code_list_value_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `code_list_value_manifest_prev_code_list_value_manifest_id_fk` FOREIGN KEY (`prev_code_list_value_manifest_id`) REFERENCES `code_list_value_manifest` (`code_list_value_manifest_id`),
    CONSTRAINT `code_list_value_manifest_next_code_list_value_manifest_id_fk` FOREIGN KEY (`next_code_list_value_manifest_id`) REFERENCES `code_list_value_manifest` (`code_list_value_manifest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inserting initial code_list_value_manifest records for 10.x release
INSERT INTO `code_list_value_manifest` (`release_id`, `code_list_value_id`, `code_list_manifest_id`)
SELECT
    `code_list_manifest`.`release_id`, `code_list_value`.`code_list_value_id`, `code_list_manifest`.`code_list_manifest_id`
FROM `code_list_value`
JOIN `code_list_manifest` ON `code_list_value`.`code_list_id` = `code_list_manifest`.`code_list_id`
JOIN `release` ON `code_list_manifest`.`release_id` = `release`.`release_id`
WHERE `release`.`release_num` != 'Working';

SET @sql = CONCAT('ALTER TABLE `code_list_value_manifest` AUTO_INCREMENT = ', (SELECT MAX(code_list_value_manifest_id) + 1 FROM code_list_value_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Copying code_list_value_manifest records for 'Working' release from 10.x release
INSERT `code_list_value_manifest` (`release_id`, `code_list_value_id`, `code_list_manifest_id`)
SELECT `code_list_manifest`.`release_id`, `code_list_value`.`code_list_value_id`, `code_list_manifest`.`code_list_manifest_id`
FROM `code_list_value`
	JOIN `code_list` ON `code_list`.`code_list_id` = `code_list_value`.`code_list_id`
	JOIN `code_list_manifest` ON `code_list`.`code_list_id` = `code_list_manifest`.`code_list_id`
WHERE `code_list_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

SET @sql = CONCAT('ALTER TABLE `code_list_value_manifest` AUTO_INCREMENT = ', (SELECT MAX(code_list_value_manifest_id) + 1 FROM code_list_value_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- BIEs
-- ABIE
ALTER TABLE `abie` ADD COLUMN `based_acc_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key to the ACC_MANIFEST table refering to the ACC, on which the business context has been applied to derive this ABIE.' AFTER `guid`,
                   ADD CONSTRAINT `abie_based_acc_manifest_id_fk` FOREIGN KEY (`based_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
                   ADD COLUMN `path` TEXT CHARACTER SET ascii AFTER `based_acc_manifest_id`,
                   ADD COLUMN `hash_path` varchar(64) CHARACTER SET ascii NOT NULL COMMENT 'hash_path generated from the path of the component graph using hash function, so that it is unique in the graph.' AFTER `path`,
                   ADD KEY `abie_path_k` (`path`(3072)),
                   ADD KEY `abie_hash_path_k` (`hash_path`);

UPDATE `abie`, `acc_manifest`, `release`
SET `abie`.`based_acc_manifest_id` = `acc_manifest`.`acc_manifest_id`
WHERE `abie`.`based_acc_id` = `acc_manifest`.`acc_id`
  AND `acc_manifest`.`release_id` = `release`.`release_id`
  AND `release`.`release_num` != 'Working';

ALTER TABLE `abie` DROP FOREIGN KEY `abie_based_acc_id_fk`,
                   DROP COLUMN `based_acc_id`;

-- ASBIE
ALTER TABLE `asbie` ADD COLUMN `based_ascc_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'The BASED_ASCC_MANIFEST_ID column refers to the ASCC_MANIFEST record, which this ASBIE contextualizes.' AFTER `guid`,
                    ADD CONSTRAINT `asbie_based_ascc_manifest_id_fk` FOREIGN KEY (`based_ascc_manifest_id`) REFERENCES `ascc_manifest` (`ascc_manifest_id`),
                    ADD COLUMN `path` TEXT CHARACTER SET ascii AFTER `based_ascc_manifest_id`,
                    ADD COLUMN `hash_path` varchar(64) CHARACTER SET ascii NOT NULL COMMENT 'hash_path generated from the path of the component graph using hash function, so that it is unique in the graph.' AFTER `path`,
                    ADD KEY `asbie_path_k` (`path`(3072)),
                    ADD KEY `asbie_hash_path_k` (`hash_path`);

UPDATE `asbie`, `ascc_manifest`, `release`
SET `asbie`.`based_ascc_manifest_id` = `ascc_manifest`.`ascc_manifest_id`
WHERE `asbie`.`based_ascc_id` = `ascc_manifest`.`ascc_id`
  AND `ascc_manifest`.`release_id` = `release`.`release_id`
  AND `release`.`release_num` != 'Working';

ALTER TABLE `asbie` DROP FOREIGN KEY `asbie_based_ascc_id_fk`,
                    DROP COLUMN `based_ascc_id`;

-- BBIE
ALTER TABLE `bbie` ADD COLUMN `based_bcc_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'The BASED_BCC_MANIFEST_ID column refers to the BCC_MANIFEST record, which this BBIE contextualizes.' AFTER `guid`,
                   ADD CONSTRAINT `bbie_based_bcc_manifest_id_fk` FOREIGN KEY (`based_bcc_manifest_id`) REFERENCES `bcc_manifest` (`bcc_manifest_id`),
                   ADD COLUMN `path` TEXT CHARACTER SET ascii AFTER `based_bcc_manifest_id`,
                   ADD COLUMN `hash_path` varchar(64) CHARACTER SET ascii NOT NULL COMMENT 'hash_path generated from the path of the component graph using hash function, so that it is unique in the graph.' AFTER `path`,
                   ADD KEY `bbie_path_k` (`path`(3072)),
                   ADD KEY `bbie_hash_path_k` (`hash_path`);

UPDATE `bbie`, `bcc_manifest`, `release`
SET `bbie`.`based_bcc_manifest_id` = `bcc_manifest`.`bcc_manifest_id`
WHERE `bbie`.`based_bcc_id` = `bcc_manifest`.`bcc_id`
  AND `bcc_manifest`.`release_id` = `release`.`release_id`
  AND `release`.`release_num` != 'Working';

ALTER TABLE `bbie` DROP FOREIGN KEY `bbie_based_bcc_id_fk`,
                   DROP COLUMN `based_bcc_id`;

-- ASBIEP
ALTER TABLE `asbiep` ADD COLUMN `based_asccp_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key pointing to the ASCCP_MANIFEST record. It is the ASCCP, on which the ASBIEP contextualizes.' AFTER `guid`,
                     ADD CONSTRAINT `asbiep_based_asccp_manifest_id_fk` FOREIGN KEY (`based_asccp_manifest_id`) REFERENCES `asccp_manifest` (`asccp_manifest_id`),
                     ADD COLUMN `path` TEXT CHARACTER SET ascii AFTER `based_asccp_manifest_id`,
                     ADD COLUMN `hash_path` varchar(64) CHARACTER SET ascii NOT NULL COMMENT 'hash_path generated from the path of the component graph using hash function, so that it is unique in the graph.' AFTER `path`,
                     ADD KEY `asbiep_path_k` (`path`(3072)),
                     ADD KEY `asbiep_hash_path_k` (`hash_path`);

UPDATE `asbiep`, `asccp_manifest`, `release`
SET `asbiep`.`based_asccp_manifest_id` = `asccp_manifest`.`asccp_manifest_id`
WHERE `asbiep`.`based_asccp_id` = `asccp_manifest`.`asccp_id`
  AND `asccp_manifest`.`release_id` = `release`.`release_id`
  AND `release`.`release_num` != 'Working';

ALTER TABLE `asbiep` DROP FOREIGN KEY `asbiep_based_asccp_id_fk`,
                     DROP COLUMN `based_asccp_id`;

-- BBIEP
ALTER TABLE `bbiep` ADD COLUMN `based_bccp_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key pointing to the BCCP_MANIFEST record. It is the BCCP, which the BBIEP contextualizes.' AFTER `guid`,
                    ADD CONSTRAINT `bbiep_based_bccp_manifest_id_fk` FOREIGN KEY (`based_bccp_manifest_id`) REFERENCES `bccp_manifest` (`bccp_manifest_id`),
                    ADD COLUMN `path` TEXT CHARACTER SET ascii AFTER `based_bccp_manifest_id`,
                    ADD COLUMN `hash_path` varchar(64) CHARACTER SET ascii NOT NULL COMMENT 'hash_path generated from the path of the component graph using hash function, so that it is unique in the graph.' AFTER `path`,
                    ADD KEY `bbiep_path_k` (`path`(3072)),
                    ADD KEY `bbiep_hash_path_k` (`hash_path`);

UPDATE `bbiep`, `bccp_manifest`, `release`
SET `bbiep`.`based_bccp_manifest_id` = `bccp_manifest`.`bccp_manifest_id`
WHERE `bbiep`.`based_bccp_id` = `bccp_manifest`.`bccp_id`
  AND `bccp_manifest`.`release_id` = `release`.`release_id`
  AND `release`.`release_num` != 'Working';

ALTER TABLE `bbiep` DROP FOREIGN KEY `bbiep_based_bccp_id_fk`,
                    DROP COLUMN `based_bccp_id`;

-- BBIE_SC
ALTER TABLE `bbie_sc` ADD COLUMN `based_dt_sc_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the DT_SC_MANIFEST table. This should correspond to the DT_SC of the BDT of the based BCC and BCCP.' AFTER `guid`,
                      ADD CONSTRAINT `bbie_sc_based_dt_sc_manifest_id_fk` FOREIGN KEY (`based_dt_sc_manifest_id`) REFERENCES `dt_sc_manifest` (`dt_sc_manifest_id`),
                      ADD COLUMN `path` TEXT CHARACTER SET ascii AFTER `based_dt_sc_manifest_id`,
                      ADD COLUMN `hash_path` varchar(64) CHARACTER SET ascii NOT NULL COMMENT 'hash_path generated from the path of the component graph using hash function, so that it is unique in the graph.' AFTER `path`,
                      ADD KEY `bbie_sc_path_k` (`path`(3072)),
                      ADD KEY `bbie_sc_hash_path_k` (`hash_path`),
                      ADD COLUMN `created_by` bigint(20) unsigned NOT NULL COMMENT 'A foreign key referring to the user who creates the BBIE_SC. The creator of the BBIE_SC is also its owner by default.' AFTER `is_used`,
                      ADD COLUMN `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'A foreign key referring to the user who has last updated the BBIE_SC record.' AFTER `created_by`,
                      ADD COLUMN `creation_timestamp` datetime(6) NOT NULL COMMENT 'Timestamp when the BBIE_SC record was first created.' AFTER `last_updated_by`,
                      ADD COLUMN `last_update_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the BBIE_SC was last updated.' AFTER `creation_timestamp`,
                      ADD KEY `bbie_sc_created_by_fk` (`created_by`),
                      ADD KEY `bbie_sc_last_updated_by_fk` (`last_updated_by`),
                      ADD CONSTRAINT `bbie_sc_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
                      ADD CONSTRAINT `bbie_sc_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

UPDATE `bbie_sc`, `dt_sc_manifest`, `release`
SET `bbie_sc`.`based_dt_sc_manifest_id` = `dt_sc_manifest`.`dt_sc_manifest_id`
WHERE `bbie_sc`.`dt_sc_id` = `dt_sc_manifest`.`dt_sc_id`
  AND `dt_sc_manifest`.`release_id` = `release`.`release_id`
  AND `release`.`release_num` != 'Working';

ALTER TABLE `bbie_sc` DROP FOREIGN KEY `bbie_sc_dt_sc_id_fk`,
                      DROP COLUMN `dt_sc_id`;


-- `top_level_abie`
ALTER TABLE `top_level_asbiep` MODIFY COLUMN `state` varchar(20) DEFAULT NULL;
UPDATE `top_level_asbiep` SET `state` = 'Initiating' where `state` = '1';
UPDATE `top_level_asbiep` SET `state` = 'WIP' where `state` = '2';
UPDATE `top_level_asbiep` SET `state` = 'QA' where `state` = '3';
UPDATE `top_level_asbiep` SET `state` = 'Production' where `state` = '4';


-- Making relations between `xbt` and `release` tables.
CREATE TABLE `xbt_manifest` (
    `xbt_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `module_id` bigint(20) unsigned,
    `xbt_id` bigint(20) unsigned NOT NULL,
    `conflict` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'This indicates that there is a conflict between self and relationship.',
    `revision_id` bigint(20) unsigned COMMENT 'A foreign key pointed to revision for the current record.',
    `prev_xbt_manifest_id` bigint(20) unsigned,
    `next_xbt_manifest_id` bigint(20) unsigned,
    PRIMARY KEY (`xbt_manifest_id`),
    KEY `xbt_manifest_xbt_id_fk` (`xbt_id`),
    KEY `xbt_manifest_release_id_fk` (`release_id`),
    KEY `xbt_manifest_module_id_fk` (`module_id`),
    KEY `xbt_manifest_revision_id_fk` (`revision_id`),
    KEY `xbt_manifest_prev_xbt_manifest_id_fk` (`prev_xbt_manifest_id`),
    KEY `xbt_manifest_next_xbt_manifest_id_fk` (`next_xbt_manifest_id`),
    CONSTRAINT `xbt_manifest_xbt_id_fk` FOREIGN KEY (`xbt_id`) REFERENCES `xbt` (`xbt_id`),
    CONSTRAINT `xbt_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `xbt_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
    CONSTRAINT `xbt_manifest_revision_id_fk` FOREIGN KEY (`revision_id`) REFERENCES `revision` (`revision_id`),
    CONSTRAINT `xbt_manifest_prev_xbt_manifest_id_fk` FOREIGN KEY (`prev_xbt_manifest_id`) REFERENCES `xbt_manifest` (`xbt_manifest_id`),
    CONSTRAINT `xbt_manifest_next_xbt_manifest_id_fk` FOREIGN KEY (`next_xbt_manifest_id`) REFERENCES `xbt_manifest` (`xbt_manifest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Add `guid` into `xbt` table.
ALTER TABLE `xbt` ADD COLUMN `guid` varchar(41) NULL COMMENT 'A globally unique identifier (GUID) of an XBT. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.' AFTER `xbt_id`;

UPDATE `xbt` SET `guid` = 'oagis-id-53e3599ca89e458f97cc649eaaceeba0' WHERE `name` = 'any type';
UPDATE `xbt` SET `guid` = 'oagis-id-7114441198054ad78d2fcf9bcdab2cbf' WHERE `name` = 'any simple type';
UPDATE `xbt` SET `guid` = 'oagis-id-aa64380f2d954d74a041bec1c327351e' WHERE `name` = 'duration';
UPDATE `xbt` SET `guid` = 'oagis-id-88bb4630e9464a44b09b25257e569c56' WHERE `name` = 'date time';
UPDATE `xbt` SET `guid` = 'oagis-id-28291becfc0b40fa9f429c9d968a30bd' WHERE `name` = 'time';
UPDATE `xbt` SET `guid` = 'oagis-id-47189c200a114cf39e911febee2780c0' WHERE `name` = 'date';
UPDATE `xbt` SET `guid` = 'oagis-id-5ce161d6ee8e4c5695e444a515421045' WHERE `name` = 'gregorian year month';
UPDATE `xbt` SET `guid` = 'oagis-id-e56e1fb620e843cca213c2818c802f99' WHERE `name` = 'gregorian year';
UPDATE `xbt` SET `guid` = 'oagis-id-ef9dde341f434aea9a90419b8e815dc1' WHERE `name` = 'gregorian month day';
UPDATE `xbt` SET `guid` = 'oagis-id-5671447a22984812ae2c1ddbbb1a0883' WHERE `name` = 'gregorian day';
UPDATE `xbt` SET `guid` = 'oagis-id-776ae1eb6642450899b3192088618c28' WHERE `name` = 'gregorian month';
UPDATE `xbt` SET `guid` = 'oagis-id-e7fe90c1b6a24d4a88ef7582267d57cd' WHERE `name` = 'string';
UPDATE `xbt` SET `guid` = 'oagis-id-41cadac404504e02862c97ebdce04002' WHERE `name` = 'normalized string';
UPDATE `xbt` SET `guid` = 'oagis-id-0963dd2d22084b4893ff69ff303e57d9' WHERE `name` = 'token';
UPDATE `xbt` SET `guid` = 'oagis-id-155163eb646c463caa263a4ed2d170f3' WHERE `name` = 'language';
UPDATE `xbt` SET `guid` = 'oagis-id-75f49858b7554f99bd7fcf7c51b6e92d' WHERE `name` = 'boolean';
UPDATE `xbt` SET `guid` = 'oagis-id-dd2d06c1ad4447b68285f6902919fcdc' WHERE `name` = 'base64 binary';
UPDATE `xbt` SET `guid` = 'oagis-id-9fec44d8855f48eaba7e6466a418db92' WHERE `name` = 'hex binary';
UPDATE `xbt` SET `guid` = 'oagis-id-37d18b90accf4d1c833aa7023a2c1ca4' WHERE `name` = 'float';
UPDATE `xbt` SET `guid` = 'oagis-id-7b6409f8157d4d9c8f5a6b0e69b02015' WHERE `name` = 'decimal';
UPDATE `xbt` SET `guid` = 'oagis-id-f3bc6f3ca44b47e9a9a66997fc5d3c2b' WHERE `name` = 'integer';
UPDATE `xbt` SET `guid` = 'oagis-id-da82a62e6bfd4db88710442a67356ff9' WHERE `name` = 'non negative integer';
UPDATE `xbt` SET `guid` = 'oagis-id-4313263875ae4a1399737a64379013a6' WHERE `name` = 'positive integer';
UPDATE `xbt` SET `guid` = 'oagis-id-73f10f9826ca4645926e8f8d83f33995' WHERE `name` = 'double';
UPDATE `xbt` SET `guid` = 'oagis-id-89c9230b6ac34e4da2216b17bfab0e43' WHERE `name` = 'any URI';
UPDATE `xbt` SET `guid` = 'oagis-id-2e9334c109a94fada9370689c55a49b5' WHERE `name` = 'xbt boolean';
UPDATE `xbt` SET `guid` = 'oagis-id-5e9e51b71a664530bbb87ad2d55e4125' WHERE `name` = 'xbt week duration';
UPDATE `xbt` SET `guid` = 'oagis-id-45a87474713b436e9c8e36286e9cd0b6' WHERE `name` = 'xbt century';
UPDATE `xbt` SET `guid` = 'oagis-id-101bda7e6f2b42b6b0d7066409d2f25c' WHERE `name` = 'xbt date';
UPDATE `xbt` SET `guid` = 'oagis-id-0bdc5988f4904c10b969fe376c09b4ee' WHERE `name` = 'xbt day of week';
UPDATE `xbt` SET `guid` = 'oagis-id-4bc9ef0476e3461b8cb64c8aa5454f19' WHERE `name` = 'xbt day of year';
UPDATE `xbt` SET `guid` = 'oagis-id-a00421ceebfe4019ad746d0f7f908a3d' WHERE `name` = 'xbt day';
UPDATE `xbt` SET `guid` = 'oagis-id-db934e6cbe024f94a2a41a894c689256' WHERE `name` = 'xbt month day';
UPDATE `xbt` SET `guid` = 'oagis-id-bcb24859ee6641049ed06b47ee53ed41' WHERE `name` = 'xbt month';
UPDATE `xbt` SET `guid` = 'oagis-id-d3d1223fbad746a9bfe2a71ac83d0cee' WHERE `name` = 'xbt week';
UPDATE `xbt` SET `guid` = 'oagis-id-60b5d214096a4aa5a203a9d42c2c2660' WHERE `name` = 'xbt week day';
UPDATE `xbt` SET `guid` = 'oagis-id-27ece956cc9a437e90204888a279dbba' WHERE `name` = 'xbt year day';
UPDATE `xbt` SET `guid` = 'oagis-id-89128ab3d09647f3b40b8c22a127daff' WHERE `name` = 'xbt year month';
UPDATE `xbt` SET `guid` = 'oagis-id-bc04da1aa2534ce687c7618ac682f3ac' WHERE `name` = 'xbt year';
UPDATE `xbt` SET `guid` = 'oagis-id-5e4c1ee4f5bf4110b2902c8229898f20' WHERE `name` = 'xbt year week';
UPDATE `xbt` SET `guid` = 'oagis-id-c8014b4f665e4f0ca628ed014dcf1821' WHERE `name` = 'xbt year week day';
UPDATE `xbt` SET `guid` = 'oagis-id-f01b3b058c8d47e7b121ddf0e97c8f79' WHERE `name` = 'xbt hour minute';
UPDATE `xbt` SET `guid` = 'oagis-id-a58e80cfa1504908a801bda0a2dbb8e3' WHERE `name` = 'xbt hour minute UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-4f4bdf55bf0a40c8857348afbe93c939' WHERE `name` = 'xbt hour minute UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-3c549b59a3b84643b0b546dcbf1fd6ba' WHERE `name` = 'xbt hour';
UPDATE `xbt` SET `guid` = 'oagis-id-852a452746e04746bd466247d16f711f' WHERE `name` = 'xbt hour UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-677164c357e24b538dba76fefdc6eb55' WHERE `name` = 'xbt hour UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-ab96588ee36b432c91c4a7795e24f04e' WHERE `name` = 'xbt minute';
UPDATE `xbt` SET `guid` = 'oagis-id-d4f9c15ab6fa4b5f84d56f9d64cd667a' WHERE `name` = 'xbt minute second';
UPDATE `xbt` SET `guid` = 'oagis-id-8df51c2a0a044c9388e05a283e235b5d' WHERE `name` = 'xbt second';
UPDATE `xbt` SET `guid` = 'oagis-id-48b2398b231b4cf4adc7f0f88da6361e' WHERE `name` = 'xbt time';
UPDATE `xbt` SET `guid` = 'oagis-id-b3ae6aeea86444b4978006aee864ec01' WHERE `name` = 'xbt time UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-9ecd8e2b8e1842fca4554b0551da52f8' WHERE `name` = 'xbt time UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-10d050e1cd5d4235934f5bf7ae1bae53' WHERE `name` = 'xbt date hour minute';
UPDATE `xbt` SET `guid` = 'oagis-id-593d60e122ef46e1bd63406abb3d5dcd' WHERE `name` = 'xbt date hour minute UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-a5d380bc999d470f87b3ac11137bc746' WHERE `name` = 'xbt date hour minute UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-b34a3af72c2e4f818aff0a8ff742e4b3' WHERE `name` = 'xbt date hour';
UPDATE `xbt` SET `guid` = 'oagis-id-b3e8791430b9415dbc3c4496da7eeb31' WHERE `name` = 'xbt date hour UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-a10589903ed242ec93f87e0bfe71f171' WHERE `name` = 'xbt date hour UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-69784a2c38c5490d9b3c775d43b2910e' WHERE `name` = 'xbt date time';
UPDATE `xbt` SET `guid` = 'oagis-id-de1aa4141f2e4606967550db08ff3066' WHERE `name` = 'xbt date time UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-fbdf44e0bf4242e4a61c1edcf7f15054' WHERE `name` = 'xbt date time UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-4228a3612abb43aaad84bfddd403ffe4' WHERE `name` = 'xbt day hour minute';
UPDATE `xbt` SET `guid` = 'oagis-id-e0e4af1a9c34463bb901e93131871fc6' WHERE `name` = 'xbt day hour minute UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-4ef52185f5784486ac07d280903c3905' WHERE `name` = 'xbt day hour minute UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-ea788561e67b4a7caaede471cfd9d2a9' WHERE `name` = 'xbt day hour';
UPDATE `xbt` SET `guid` = 'oagis-id-55e162980ebc43c894db7b911e875332' WHERE `name` = 'xbt day hour UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-623cf021e49f444faeaca513f0a7f38a' WHERE `name` = 'xbt day hour UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-0fc308e676aa47e18c62be398a57e315' WHERE `name` = 'xbt day of week hour minute';
UPDATE `xbt` SET `guid` = 'oagis-id-0538c66fec364726a67baad772f1d8a9' WHERE `name` = 'xbt day of week hour minute UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-5056ada814d1483e8117b9c80bc8c3de' WHERE `name` = 'xbt day of week hour minute UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-f8d68d8be8474deba922eed599a3ebd5' WHERE `name` = 'xbt day of week hour';
UPDATE `xbt` SET `guid` = 'oagis-id-bc12aac98cb44d57a2f9a16832b25056' WHERE `name` = 'xbt day of week hour UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-5abe3d8877de448da6952c4860d400f6' WHERE `name` = 'xbt day of week hour UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-88d0235f794746c788b1f430d472c006' WHERE `name` = 'xbt day of week time';
UPDATE `xbt` SET `guid` = 'oagis-id-0cc52e214543406087a7ec8fd0c6a019' WHERE `name` = 'xbt day of week time UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-4e822765e15647ae891f41e39127d6dc' WHERE `name` = 'xbt day of week time UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-c922270fd9fe43aa86348c61b0e28d3b' WHERE `name` = 'xbt day of year hour minute';
UPDATE `xbt` SET `guid` = 'oagis-id-e34577d64d3446a0b1f26743d5f8298f' WHERE `name` = 'xbt day of year hour minute UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-ca692658e7204728914384fa1e57d9fa' WHERE `name` = 'xbt day of year hour minute UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-0bf15c8154b744c6b023dbc877e6d897' WHERE `name` = 'xbt day of year hour';
UPDATE `xbt` SET `guid` = 'oagis-id-698e8017aa3c4ce7bf2b90044f510f11' WHERE `name` = 'xbt day of year hour UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-6da5a5b48d1c4fb9ac51f9113824738b' WHERE `name` = 'xbt day of year hour UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-9c38786426524b23a9068168d22dbd74' WHERE `name` = 'xbt day of year time';
UPDATE `xbt` SET `guid` = 'oagis-id-d762edf84421445d82014f9fa8c069f8' WHERE `name` = 'xbt day of year time UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-4d270262278846f29bc6d6eba5040e28' WHERE `name` = 'xbt day of year time UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-9167edb6f19847619dd4f7a4b7050b4c' WHERE `name` = 'xbt day time';
UPDATE `xbt` SET `guid` = 'oagis-id-24f82c82ca6942caa4ab175e5ba21128' WHERE `name` = 'xbt day time UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-ce39e9fd96e7437e95fe7b0529820533' WHERE `name` = 'xbt day time UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-d5b7bcd1d60c45198fee12c5921267da' WHERE `name` = 'xbt month day hour minute';
UPDATE `xbt` SET `guid` = 'oagis-id-eae36890008141759b64988b70fd7eaf' WHERE `name` = 'xbt month day hour minute UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-db993b77e0df47c4b08fba172f2e6486' WHERE `name` = 'xbt month day hour minute UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-508d1636bba24e17a36eaa1188122310' WHERE `name` = 'xbt month day hour';
UPDATE `xbt` SET `guid` = 'oagis-id-641b2a7beea44f04aeab4f5d4f6cb173' WHERE `name` = 'xbt month day hour UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-568f2f93cb3e4e008c0c0f6c62cbe988' WHERE `name` = 'xbt month day hour UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-25234b37877a4ba78306cdd328f235a7' WHERE `name` = 'xbt month day time';
UPDATE `xbt` SET `guid` = 'oagis-id-3f7041d51eec4dad80e346f468f235e5' WHERE `name` = 'xbt month day time UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-2a44a54bc14145f3a5959d3ded229a26' WHERE `name` = 'xbt month day time UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-256ed270ef444122a7a020ceeac3ac31' WHERE `name` = 'xbt week day hour minute';
UPDATE `xbt` SET `guid` = 'oagis-id-2d1cc3cd91914b49a277cd78291dc5b1' WHERE `name` = 'xbt week day hour minute UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-bb1e4c03ff7d4a0aa7bca9288ad46c0b' WHERE `name` = 'xbt week day hour minute UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-3eb7d75e67e44f85b5f9b6131ecd1286' WHERE `name` = 'xbt week day hour';
UPDATE `xbt` SET `guid` = 'oagis-id-79fc62d646534796a3d086f96ef93927' WHERE `name` = 'xbt week day hour UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-44ccb923d97246118fae7e53765595ab' WHERE `name` = 'xbt week day hour UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-0aefe0ae66914cc399ff63c9e4de6737' WHERE `name` = 'xbt week day time';
UPDATE `xbt` SET `guid` = 'oagis-id-71bc69564d0140cca425abc83a84151b' WHERE `name` = 'xbt week day time UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-56b0dd3e33104497b7f5026d6c2ed382' WHERE `name` = 'xbt week day time UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-3d7cb9b3b31c455daefca7207fece250' WHERE `name` = 'xbt year day hour minute';
UPDATE `xbt` SET `guid` = 'oagis-id-ba2b4afcac9f4d4c932fd6d02a68517e' WHERE `name` = 'xbt year day hour minute UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-ded267bb7b7342f4a887ae2cf5552304' WHERE `name` = 'xbt year day hour minute UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-e41d9da7b3da4cf8bf35a3fffbd58ea0' WHERE `name` = 'xbt year day hour';
UPDATE `xbt` SET `guid` = 'oagis-id-6444bf6e4bd14dd8865bd2058bcbb41e' WHERE `name` = 'xbt year day hour UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-f07c25e2a9454be8904f3a0d8f5d5f9d' WHERE `name` = 'xbt year day hour UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-2d5c379f4f314100a9680b9bda962acb' WHERE `name` = 'xbt year day time';
UPDATE `xbt` SET `guid` = 'oagis-id-9bd91871cf1e48f488f2516dba2d035e' WHERE `name` = 'xbt year day time UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-93b831a878124421880a904e036de9e1' WHERE `name` = 'xbt year day time UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-bf19edc1844d424f89b67dcb23d69ae4' WHERE `name` = 'xbt year week day hour minute';
UPDATE `xbt` SET `guid` = 'oagis-id-e1c1bd2cd7ad4255b2d5db96f835ebf7' WHERE `name` = 'xbt year week day hour minute UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-a7c5063c47964cbb93c8e1e1cc458d93' WHERE `name` = 'xbt year week day hour minute UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-c9455f21ce20429194a31465a97e5d0f' WHERE `name` = 'xbt year week day hour';
UPDATE `xbt` SET `guid` = 'oagis-id-e9216b9633ad46fb8c087636b57f2245' WHERE `name` = 'xbt year week day hour UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-e02b195db8c64c8893559463711782ed' WHERE `name` = 'xbt year week day hour UTC offset';
UPDATE `xbt` SET `guid` = 'oagis-id-a908e3751d5842f0aca07a8baac645b5' WHERE `name` = 'xbt year week day time';
UPDATE `xbt` SET `guid` = 'oagis-id-50f68e9c8f4a47fda2b3f3ba3bff7952' WHERE `name` = 'xbt year week day time UTC';
UPDATE `xbt` SET `guid` = 'oagis-id-6aba2f0c928d426384417bc1a2b0d300' WHERE `name` = 'xbt year week day time UTC offset';

ALTER TABLE `xbt` MODIFY COLUMN `guid` varchar(41) NOT NULL COMMENT 'A globally unique identifier (GUID) of an XBT. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.';

-- Inserting initial xbt_manifest records for 10.x release
INSERT `xbt_manifest` (`release_id`, `module_id`, `xbt_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` != 'Working') as `release_id`,
    `xbt`.`module_id`, `xbt`.`xbt_id`
FROM `xbt`
WHERE `xbt`.`state` = 3;

SET @sql = CONCAT('ALTER TABLE `xbt_manifest` AUTO_INCREMENT = ', (SELECT MAX(xbt_manifest_id) + 1 FROM xbt_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Copying xbt_manifest records for 'Working' release from 10.x release
INSERT `xbt_manifest` (`release_id`, `module_id`, `xbt_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`,
    `xbt_manifest`.`module_id`, `xbt_manifest`.`xbt_id`
FROM `xbt_manifest` JOIN `release` ON `xbt_manifest`.`release_id` = `release`.`release_id`
WHERE `release`.`release_num` != 'Working';

SET @sql = CONCAT('ALTER TABLE `xbt_manifest` AUTO_INCREMENT = ', (SELECT MAX(xbt_manifest_id) + 1 FROM xbt_manifest));

PREPARE stmt from @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `xbt` SET `revision_num` = 1, `revision_tracking_num` = 1, `revision_action` = 1;

-- Add deprecated annotations
ALTER TABLE `xbt` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.',
                  MODIFY COLUMN `current_xbt_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.';

-- Add indices
CREATE INDEX `xbt_guid_idx` ON `xbt` (`guid`);
CREATE INDEX `xbt_last_update_timestamp_desc_idx` ON `xbt` (`last_update_timestamp` DESC);

-- Add columns and constraints on `agency_id_list` table.
ALTER TABLE `agency_id_list`
    ADD COLUMN `created_by` bigint(20) unsigned COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the agency ID list.',
    ADD COLUMN `last_updated_by` bigint(20) unsigned COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the agency ID list.',
    ADD COLUMN `creation_timestamp` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Timestamp when the agency ID list was created.',
    ADD COLUMN `last_update_timestamp` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Timestamp when the agency ID list was last updated.',
    ADD COLUMN `state` varchar(10) DEFAULT NULL COMMENT 'Life cycle state of the agency ID list. Possible values are Editing, Published, or Deleted. Only a code list in published state is available for derivation and for used by the CC and BIE. Once the agency ID list is published, it cannot go back to Editing. A new version would have to be created.',
    ADD CONSTRAINT `agency_id_list_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `agency_id_list_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

UPDATE `agency_id_list` SET `created_by` = (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'oagis'), `last_updated_by` = (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'oagis'), `state` = 'Published';

ALTER TABLE `agency_id_list`
    MODIFY COLUMN `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the agency ID list.',
    MODIFY COLUMN `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the agency ID list.';

-- Add columns and constraints on `ctx_category` table.
ALTER TABLE `ctx_category`
    ADD COLUMN `created_by` bigint(20) unsigned COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the context category.',
    ADD COLUMN `last_updated_by` bigint(20) unsigned COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the context category.',
    ADD COLUMN `creation_timestamp` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Timestamp when the context category was created.',
    ADD COLUMN `last_update_timestamp` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Timestamp when the context category was last updated.',
    ADD CONSTRAINT `ctx_category_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    ADD CONSTRAINT `ctx_category_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`);

UPDATE `ctx_category`, `ctx_scheme` SET `ctx_category`.`created_by` = `ctx_scheme`.`created_by`, `ctx_category`.`last_updated_by` = `ctx_scheme`.`last_updated_by` WHERE `ctx_category`.`ctx_category_id` = `ctx_scheme`.`ctx_category_id`;
UPDATE `ctx_category` SET `ctx_category`.`created_by` = (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'oagis'), `ctx_category`.`last_updated_by` = (SELECT `app_user_id` FROM `app_user` WHERE `login_id` = 'oagis') WHERE `ctx_category`.`created_by` IS NULL;

ALTER TABLE `ctx_category`
    MODIFY COLUMN `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created the context category.',
    MODIFY COLUMN `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It identifies the user who last updated the context category.';


-- Add `module_dir` table.
DROP TABLE IF EXISTS `module_dir`;
CREATE TABLE `module_dir` (
    `module_dir_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key.',
    `parent_module_dir_id` bigint(20) unsigned DEFAULT NULL COMMENT 'This indicates the parent of this directory.',
    `name` varchar(100) NOT NULL COMMENT 'This is the name of the directory.',
    `path` text NOT NULL COMMENT 'This is a full-path of this module directory for performance.',
    `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this MODULE_DIR.',
    `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.',
    `creation_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was first created.',
    `last_update_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was last updated.',
    PRIMARY KEY (`module_dir_id`),
    KEY `module_dir_created_by_fk` (`created_by`),
    KEY `module_dir_last_updated_by_fk` (`last_updated_by`),
    KEY `module_dir_parent_module_dir_id_fk` (`parent_module_dir_id`),
    KEY `module_dir_path_k` (`path`(1024)),
    CONSTRAINT `module_dir_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_dir_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
CONSTRAINT `module_dir_parent_module_dir_id_fk` FOREIGN KEY (`parent_module_dir_id`) REFERENCES `module_dir` (`module_dir_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `module_dir` (`module_dir_id`, `parent_module_dir_id`, `name`, `path`, `created_by`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`)
VALUES
(1, NULL, '', '', 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

-- Add 'module_set` table.
CREATE TABLE `module_set` (
    `module_set_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key.',
    `guid` varchar(41) NOT NULL COMMENT 'A globally unique identifier (GUID) of an SC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence. Note that each SC is considered intrinsic to each DT, so a SC has a different GUID from the based SC, i.e., SC inherited from the based DT has a new, different GUID.',
    `name` varchar(100) NOT NULL COMMENT 'This is the name of the module set.',
    `description` text COMMENT 'Description or explanation about the module set or use of the module set.',
    `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this MODULE_SET.',
    `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.',
    `creation_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was first created.',
    `last_update_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was last updated.',
    PRIMARY KEY (`module_set_id`),
    KEY `module_set_created_by_fk` (`created_by`),
    KEY `module_set_last_updated_by_fk` (`last_updated_by`),
    CONSTRAINT `module_set_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_set_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `module_set` (`module_set_id`, `guid`, `name`, `description`, `created_by`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`)
VALUES
(1, 'oagis-id-dd2c4bf4885e4b769504ab0afd0a7aae', 'OAGIS 10.6 Canonical XML Schema', 'Can be used for both global-global and local-global pattern', 1, 1, '2020-06-27 01:47:41.918540', '2020-06-27 01:47:41.918540');

-- Add 'module_set_assignment` table.
CREATE TABLE `module_set_assignment` (
    `module_set_assignment_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key.',
    `module_set_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module set.',
    `module_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module assigned in the module set.',
    `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this MODULE_SET_ASSIGNMENT.',
    `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.',
    `creation_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was first created.',
    `last_update_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was last updated.',
    PRIMARY KEY (`module_set_assignment_id`),
    KEY `module_set_assignment_module_set_id_fk` (`module_set_id`),
    KEY `module_set_assignment_module_id_fk` (`module_id`),
    KEY `module_set_assignment_created_by_fk` (`created_by`),
    KEY `module_set_assignment_last_updated_by_fk` (`last_updated_by`),
    CONSTRAINT `module_set_assignment_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_set_assignment_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_set_assignment_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
    CONSTRAINT `module_set_assignment_module_set_id_fk` FOREIGN KEY (`module_set_id`) REFERENCES `module_set` (`module_set_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `module_set_assignment`
(`module_set_id`, `module_id`, `created_by`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`)
SELECT `module_set`.`module_set_id`, `module`.`module_id`,
       `module`.`created_by`, `module`.`last_updated_by`, `module`.`creation_timestamp`, `module`.`last_update_timestamp`
FROM `module_set`, `module`;

DROP TABLE IF EXISTS `module_set_release`;
CREATE TABLE `module_set_release` (
    `module_set_release_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key.',
    `module_set_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module set.',
    `release_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the release.',
    `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'It would be a default module set if this indicator is checked. Otherwise, it would be an optional.',
    `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this MODULE_SET_RELEASE.',
    `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.',
    `creation_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was first created.',
    `last_update_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was last updated.',
    PRIMARY KEY (`module_set_release_id`),
    KEY `module_set_release_module_set_id_fk` (`module_set_id`),
    KEY `module_set_release_release_id_fk` (`release_id`),
    KEY `module_set_release_assignment_created_by_fk` (`created_by`),
    KEY `module_set_release_assignment_last_updated_by_fk` (`last_updated_by`),
    CONSTRAINT `module_set_release_assignment_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_set_release_assignment_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_set_release_module_set_id_fk` FOREIGN KEY (`module_set_id`) REFERENCES `module_set` (`module_set_id`),
    CONSTRAINT `module_set_release_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `module_set_release` (`module_set_release_id`, `module_set_id`, `release_id`, `is_default`, `created_by`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`)
VALUES
(1, 1, 1, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
(2, 1, 2, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

-- `module_manifest` tables.
-- 'module_acc_manifest'
DROP TABLE IF EXISTS `module_acc_manifest`;
CREATE TABLE `module_acc_manifest` (
    `module_acc_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key.',
    `module_set_release_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module set release record.',
    `acc_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the acc manifest record.',
    `module_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module record.',
    `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.',
    `creation_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was first created.',
    `last_update_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was last updated.',
    PRIMARY KEY (`module_acc_manifest_id`),
    KEY `module_acc_manifest_created_by_fk` (`created_by`),
    KEY `module_acc_manifest_last_updated_by_fk` (`last_updated_by`),
    KEY `module_acc_manifest_module_set_release_id_fk` (`module_set_release_id`),
    KEY `module_acc_manifest_acc_manifest_id_fk` (`acc_manifest_id`),
    KEY `module_acc_manifest_module_id_fk` (`module_id`),
    CONSTRAINT `module_acc_manifest_acc_manifest_id_fk` FOREIGN KEY (`acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `module_acc_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_acc_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_acc_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
    CONSTRAINT `module_acc_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert initial data of `module_acc_manifest`
INSERT INTO `module_acc_manifest` (
    `module_set_release_id`, `acc_manifest_id`, `module_id`,
    `created_by`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`
)
SELECT `module_set_release`.`module_set_release_id`, `acc_manifest`.`acc_manifest_id`, `module`.`module_id`,
       `module`.`created_by`, `module`.`last_updated_by`, `module`.`creation_timestamp`, `module`.`last_update_timestamp`
FROM `acc_manifest`
JOIN `module` ON `acc_manifest`.`module_id` = `module`.`module_id`
JOIN `release` ON `acc_manifest`.`release_id` = `release`.`release_id`
JOIN `module_set_release` ON `release`.`release_id` = `module_set_release`.`release_id`;

UPDATE `acc_manifest` SET `module_id` = NULL;
ALTER TABLE `acc_manifest`
    DROP FOREIGN KEY `acc_manifest_module_id_fk`,
    DROP COLUMN `module_id`;

-- 'module_asccp_manifest'
DROP TABLE IF EXISTS `module_asccp_manifest`;
CREATE TABLE `module_asccp_manifest` (
    `module_asccp_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key.',
    `module_set_release_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module set release record.',
    `asccp_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the asccp manifest record.',
    `module_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module record.',
    `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.',
    `creation_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was first created.',
    `last_update_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was last updated.',
    PRIMARY KEY (`module_asccp_manifest_id`),
    KEY `module_asccp_manifest_created_by_fk` (`created_by`),
    KEY `module_asccp_manifest_last_updated_by_fk` (`last_updated_by`),
    KEY `module_asccp_manifest_module_set_release_id_fk` (`module_set_release_id`),
    KEY `module_asccp_manifest_asccp_manifest_id_fk` (`asccp_manifest_id`),
    KEY `module_asccp_manifest_module_id_fk` (`module_id`),
    CONSTRAINT `module_asccp_manifest_asccp_manifest_id_fk` FOREIGN KEY (`asccp_manifest_id`) REFERENCES `asccp_manifest` (`asccp_manifest_id`),
    CONSTRAINT `module_asccp_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_asccp_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_asccp_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
CONSTRAINT `module_asccp_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert initial data of `module_asccp_manifest`
INSERT INTO `module_asccp_manifest` (
    `module_set_release_id`, `asccp_manifest_id`, `module_id`,
    `created_by`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`
)
SELECT `module_set_release`.`module_set_release_id`, `asccp_manifest`.`asccp_manifest_id`, `module`.`module_id`,
       `module`.`created_by`, `module`.`last_updated_by`, `module`.`creation_timestamp`, `module`.`last_update_timestamp`
FROM `asccp_manifest`
JOIN `module` ON `asccp_manifest`.`module_id` = `module`.`module_id`
JOIN `release` ON `asccp_manifest`.`release_id` = `release`.`release_id`
JOIN `module_set_release` ON `release`.`release_id` = `module_set_release`.`release_id`;

UPDATE `asccp_manifest` SET `module_id` = NULL;
ALTER TABLE `asccp_manifest`
    DROP FOREIGN KEY `asccp_manifest_module_id_fk`,
    DROP COLUMN `module_id`;

-- 'module_bccp_manifest'
DROP TABLE IF EXISTS `module_bccp_manifest`;
CREATE TABLE `module_bccp_manifest` (
    `module_bccp_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key.',
    `module_set_release_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module set release record.',
    `bccp_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the bccp manifest record.',
    `module_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module record.',
    `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.',
    `creation_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was first created.',
    `last_update_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was last updated.',
    PRIMARY KEY (`module_bccp_manifest_id`),
    KEY `module_bccp_manifest_created_by_fk` (`created_by`),
    KEY `module_bccp_manifest_last_updated_by_fk` (`last_updated_by`),
    KEY `module_bccp_manifest_module_set_release_id_fk` (`module_set_release_id`),
    KEY `module_bccp_manifest_bccp_manifest_id_fk` (`bccp_manifest_id`),
    KEY `module_bccp_manifest_module_id_fk` (`module_id`),
    CONSTRAINT `module_bccp_manifest_bccp_manifest_id_fk` FOREIGN KEY (`bccp_manifest_id`) REFERENCES `bccp_manifest` (`bccp_manifest_id`),
    CONSTRAINT `module_bccp_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_bccp_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_bccp_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
CONSTRAINT `module_bccp_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert initial data of `module_bccp_manifest`
INSERT INTO `module_bccp_manifest` (
    `module_set_release_id`, `bccp_manifest_id`, `module_id`,
    `created_by`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`
)
SELECT `module_set_release`.`module_set_release_id`, `bccp_manifest`.`bccp_manifest_id`, `module`.`module_id`,
       `module`.`created_by`, `module`.`last_updated_by`, `module`.`creation_timestamp`, `module`.`last_update_timestamp`
FROM `bccp_manifest`
JOIN `module` ON `bccp_manifest`.`module_id` = `module`.`module_id`
JOIN `release` ON `bccp_manifest`.`release_id` = `release`.`release_id`
JOIN `module_set_release` ON `release`.`release_id` = `module_set_release`.`release_id`;

UPDATE `bccp_manifest` SET `module_id` = NULL;
ALTER TABLE `bccp_manifest`
    DROP FOREIGN KEY `bccp_manifest_module_id_fk`,
    DROP COLUMN `module_id`;

-- 'module_code_list_manifest'
DROP TABLE IF EXISTS `module_code_list_manifest`;
CREATE TABLE `module_code_list_manifest` (
    `module_code_list_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key.',
    `module_set_release_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module set release record.',
    `code_list_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the code list manifest record.',
    `module_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module record.',
    `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.',
    `creation_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was first created.',
    `last_update_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was last updated.',
    PRIMARY KEY (`module_code_list_manifest_id`),
    KEY `module_code_list_manifest_created_by_fk` (`created_by`),
    KEY `module_code_list_manifest_last_updated_by_fk` (`last_updated_by`),
    KEY `module_code_list_manifest_module_set_release_id_fk` (`module_set_release_id`),
    KEY `module_code_list_manifest_code_list_manifest_id_fk` (`code_list_manifest_id`),
    KEY `module_code_list_manifest_module_id_fk` (`module_id`),
    CONSTRAINT `module_code_list_manifest_code_list_manifest_id_fk` FOREIGN KEY (`code_list_manifest_id`) REFERENCES `code_list_manifest` (`code_list_manifest_id`),
    CONSTRAINT `module_code_list_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_code_list_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_code_list_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
CONSTRAINT `module_code_list_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert initial data of `module_code_list_manifest`
INSERT INTO `module_code_list_manifest` (
    `module_set_release_id`, `code_list_manifest_id`, `module_id`,
    `created_by`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`
)
SELECT `module_set_release`.`module_set_release_id`, `code_list_manifest`.`code_list_manifest_id`, `module`.`module_id`,
       `module`.`created_by`, `module`.`last_updated_by`, `module`.`creation_timestamp`, `module`.`last_update_timestamp`
FROM `code_list_manifest`
JOIN `module` ON `code_list_manifest`.`module_id` = `module`.`module_id`
JOIN `release` ON `code_list_manifest`.`release_id` = `release`.`release_id`
JOIN `module_set_release` ON `release`.`release_id` = `module_set_release`.`release_id`;

UPDATE `code_list_manifest` SET `module_id` = NULL;
ALTER TABLE `code_list_manifest`
    DROP FOREIGN KEY `code_list_manifest_module_id_fk`,
    DROP COLUMN `module_id`;

-- 'module_dt_manifest'
DROP TABLE IF EXISTS `module_dt_manifest`;
CREATE TABLE `module_dt_manifest` (
    `module_dt_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key.',
    `module_set_release_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module set release record.',
    `dt_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the dt manifest record.',
    `module_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key of the module record.',
    `created_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table. It indicates the user who created this record.',
    `last_updated_by` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the APP_USER table referring to the last user who updated the record.',
    `creation_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was first created.',
    `last_update_timestamp` datetime(6) NOT NULL COMMENT 'The timestamp when the record was last updated.',
    PRIMARY KEY (`module_dt_manifest_id`),
    KEY `module_dt_manifest_created_by_fk` (`created_by`),
    KEY `module_dt_manifest_last_updated_by_fk` (`last_updated_by`),
    KEY `module_dt_manifest_module_set_release_id_fk` (`module_set_release_id`),
    KEY `module_dt_manifest_dt_manifest_id_fk` (`dt_manifest_id`),
    KEY `module_dt_manifest_module_id_fk` (`module_id`),
    CONSTRAINT `module_dt_manifest_dt_manifest_id_fk` FOREIGN KEY (`dt_manifest_id`) REFERENCES `dt_manifest` (`dt_manifest_id`),
    CONSTRAINT `module_dt_manifest_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_dt_manifest_last_updated_by_fk` FOREIGN KEY (`last_updated_by`) REFERENCES `app_user` (`app_user_id`),
    CONSTRAINT `module_dt_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`),
CONSTRAINT `module_dt_manifest_module_set_release_id_fk` FOREIGN KEY (`module_set_release_id`) REFERENCES `module_set_release` (`module_set_release_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert initial data of `module_dt_manifest`
INSERT INTO `module_dt_manifest` (
    `module_set_release_id`, `dt_manifest_id`, `module_id`,
    `created_by`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`
)
SELECT `module_set_release`.`module_set_release_id`, `dt_manifest`.`dt_manifest_id`, `module`.`module_id`,
       `module`.`created_by`, `module`.`last_updated_by`, `module`.`creation_timestamp`, `module`.`last_update_timestamp`
FROM `dt_manifest`
JOIN `module` ON `dt_manifest`.`module_id` = `module`.`module_id`
JOIN `release` ON `dt_manifest`.`release_id` = `release`.`release_id`
JOIN `module_set_release` ON `release`.`release_id` = `module_set_release`.`release_id`;

UPDATE `dt_manifest` SET `module_id` = NULL;
ALTER TABLE `dt_manifest`
    DROP FOREIGN KEY `dt_manifest_module_id_fk`,
    DROP COLUMN `module_id`;

-- Add columns and constraints on `module` table.
ALTER TABLE `module` CHANGE `module` `name` varchar(100) NOT NULL COMMENT 'The is the filename of the module. The reason to not including the extension is that the extension maybe dependent on the expression. For XML schema, ''.xsd'' maybe added; or for JSON, ''.json'' maybe added as the file extension.';
ALTER TABLE `module`
    ADD COLUMN `module_dir_id` bigint(20) unsigned COMMENT 'This indicates a module directory.' AFTER `module_id`,
    ADD CONSTRAINT `module_module_dir_id_fk` FOREIGN KEY (`module_dir_id`) REFERENCES `module_dir` (`module_dir_id`),
    DROP FOREIGN KEY `module_release_id_fk`,
    DROP COLUMN `release_id`;

-- Replace `module_id` with `module_assignment_id` on `module_dep` table.
ALTER TABLE `module_dep`
    ADD COLUMN `depending_module_set_assignment_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Foreign key to the MODULE_SET_ASSIGNMENT table. It identifies a depending module. For example, in XML schema if module A imports or includes module B, then module A is a depending module.',
    ADD COLUMN `depended_module_set_assignment_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Foreign key to the MODULE_SET_ASSIGNMENT table. It identifies a depended module counterpart of the depending module. For example, in XML schema if module A imports or includes module B, then module B is a depended module.',
    ADD CONSTRAINT `module_dep_depended_module_set_assignment_id_fk` FOREIGN KEY (`depended_module_set_assignment_id`) REFERENCES `module_set_assignment` (`module_set_assignment_id`),
    ADD CONSTRAINT `module_dep_depending_module_set_assignment_id_fk` FOREIGN KEY (`depending_module_set_assignment_id`) REFERENCES `module_set_assignment` (`module_set_assignment_id`);

UPDATE `module_dep`, (
    SELECT `module_set_assignment_id`, `module_id`
    FROM `module_set_assignment`
) t
SET `depending_module_set_assignment_id` = t.`module_set_assignment_id`
WHERE `module_dep`.`depending_module_id` = t.`module_id`;

UPDATE `module_dep`, (
    SELECT `module_set_assignment_id`, `module_id`
    FROM `module_set_assignment`
) t
SET `depended_module_set_assignment_id` = t.`module_set_assignment_id`
WHERE `module_dep`.`depended_module_id` = t.`module_id`;

ALTER TABLE `module_dep`
    DROP FOREIGN KEY `module_dep_depending_module_id_fk`,
    DROP FOREIGN KEY `module_dep_depended_module_id_fk`,
    DROP COLUMN `depending_module_id`,
    DROP COLUMN `depended_module_id`;

ALTER TABLE `module_dep`
    MODIFY COLUMN `depending_module_set_assignment_id` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the MODULE_SET_ASSIGNMENT table. It identifies a depending module. For example, in XML schema if module A imports or includes module B, then module A is a depending module.',
    MODIFY COLUMN `depended_module_set_assignment_id` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the MODULE_SET_ASSIGNMENT table. It identifies a depended module counterpart of the depending module. For example, in XML schema if module A imports or includes module B, then module B is a depended module.';


-- DROP `current_acc_id` column on `acc` table.
ALTER TABLE `acc` DROP FOREIGN KEY `acc_current_acc_id_fk`;
DROP INDEX `acc_current_acc_id_fk` ON `acc`;
ALTER TABLE `acc` DROP COLUMN `current_acc_id`;

-- DELETE `current` rows
DELETE FROM `acc` WHERE `release_id` IS NULL AND `revision_num` = 0;

-- DROP `release_id` column on `acc` table.
ALTER TABLE `acc` DROP FOREIGN KEY `acc_release_id_fk`;
DROP INDEX `acc_release_id_fk` ON `acc`;
ALTER TABLE `acc` DROP COLUMN `release_id`;

-- DROP `module_id` column on `acc` table.
ALTER TABLE `acc` DROP FOREIGN KEY `acc_module_id_fk`;
DROP INDEX `acc_module_id_fk` ON `acc`;
ALTER TABLE `acc` DROP COLUMN `module_id`;

-- DROP `revision_num`, `revision_tracking_num`, and `revision_action` columns on `acc` table.
ALTER TABLE `acc` DROP COLUMN `revision_num`,
                  DROP COLUMN `revision_tracking_num`,
                  DROP COLUMN `revision_action`;

-- DROP `current_asccp_id` column on `asccp` table.
ALTER TABLE `asccp` DROP FOREIGN KEY `asccp_current_asccp_id_fk`;
DROP INDEX `asccp_current_asccp_id_fk` ON `asccp`;
ALTER TABLE `asccp` DROP COLUMN `current_asccp_id`;

-- DELETE `current` rows
DELETE FROM `asccp` WHERE `release_id` IS NULL AND `revision_num` = 0;

-- DROP `release_id` column on `asccp` table.
ALTER TABLE `asccp` DROP FOREIGN KEY `asccp_release_id_fk`;
DROP INDEX `asccp_release_id_fk` ON `asccp`;
ALTER TABLE `asccp` DROP COLUMN `release_id`;

-- DROP `module_id` column on `asccp` table.
ALTER TABLE `asccp` DROP FOREIGN KEY `asccp_module_id_fk`;
DROP INDEX `asccp_module_id_fk` ON `asccp`;
ALTER TABLE `asccp` DROP COLUMN `module_id`;

-- DROP `revision_num`, `revision_tracking_num`, and `revision_action` columns on `asccp` table.
ALTER TABLE `asccp` DROP COLUMN `revision_num`,
                    DROP COLUMN `revision_tracking_num`,
                    DROP COLUMN `revision_action`;

-- DROP `current_bccp_id` column on `bccp` table.
ALTER TABLE `bccp` DROP FOREIGN KEY `bccp_current_bccp_id_fk`;
DROP INDEX `bccp_current_bccp_id_fk` ON `bccp`;
ALTER TABLE `bccp` DROP COLUMN `current_bccp_id`;

-- DELETE `current` rows
DELETE FROM `bccp` WHERE `release_id` IS NULL AND `revision_num` = 0;

-- DROP `release_id` column on `bccp` table.
ALTER TABLE `bccp` DROP FOREIGN KEY `bccp_release_id_fk`;
DROP INDEX `bccp_release_id_fk` ON `bccp`;
ALTER TABLE `bccp` DROP COLUMN `release_id`;

-- DROP `module_id` column on `bccp` table.
ALTER TABLE `bccp` DROP FOREIGN KEY `bccp_module_id_fk`;
DROP INDEX `bccp_module_id_fk` ON `bccp`;
ALTER TABLE `bccp` DROP COLUMN `module_id`;

-- DROP `revision_num`, `revision_tracking_num`, and `revision_action` columns on `bccp` table.
ALTER TABLE `bccp` DROP COLUMN `revision_num`,
                   DROP COLUMN `revision_tracking_num`,
                   DROP COLUMN `revision_action`;

-- DROP `current_ascc_id` column on `ascc` table.
ALTER TABLE `ascc` DROP FOREIGN KEY `ascc_current_ascc_id_fk`;
DROP INDEX `ascc_current_ascc_id_fk` ON `ascc`;
ALTER TABLE `ascc` DROP COLUMN `current_ascc_id`;

-- DELETE `current` rows on `ascc` table.
DELETE FROM `ascc` WHERE `release_id` IS NULL AND `revision_num` = 0;

-- DROP `release_id` column
ALTER TABLE `ascc` DROP FOREIGN KEY `ascc_release_id_fk`;
DROP INDEX `ascc_release_id_fk` ON `ascc`;
ALTER TABLE `ascc` DROP COLUMN `release_id`;

-- DROP `revision_num`, `revision_tracking_num`, and `revision_action` columns on `ascc` table.
ALTER TABLE `ascc` DROP COLUMN `revision_num`,
                   DROP COLUMN `revision_tracking_num`,
                   DROP COLUMN `revision_action`;

-- DROP `current_bcc_id` column on `bcc` table.
ALTER TABLE `bcc` DROP FOREIGN KEY `bcc_current_bcc_id_fk`;
DROP INDEX `bcc_current_bcc_id_fk` ON `bcc`;
ALTER TABLE `bcc` DROP COLUMN `current_bcc_id`;

-- DELETE `current` rows
DELETE FROM `bcc` WHERE `release_id` IS NULL AND `revision_num` = 0;

-- DROP `release_id` column on `bcc` table.
ALTER TABLE `bcc` DROP FOREIGN KEY `bcc_release_id_fk`;
DROP INDEX `bcc_release_id_fk` ON `bcc`;
ALTER TABLE `bcc` DROP COLUMN `release_id`;

-- DROP `revision_num`, `revision_tracking_num`, and `revision_action` columns on `bcc` table.
ALTER TABLE `bcc` DROP COLUMN `revision_num`,
                  DROP COLUMN `revision_tracking_num`,
                  DROP COLUMN `revision_action`;

-- DROP `current_bdt_id` column on `dt` table.
ALTER TABLE `dt` DROP FOREIGN KEY `dt_current_bdt_id_fk`;
DROP INDEX `dt_current_bdt_id_fk` ON `dt`;
ALTER TABLE `dt` DROP COLUMN `current_bdt_id`;

-- DROP `release_id` column on `dt` table.
ALTER TABLE `dt` DROP FOREIGN KEY `dt_release_id_fk`;
DROP INDEX `dt_release_id_fk` ON `dt`;
ALTER TABLE `dt` DROP COLUMN `release_id`;

-- DROP `module_id` column on `dt` table.
ALTER TABLE `dt` DROP FOREIGN KEY `dt_module_id_fk`;
DROP INDEX `dt_module_id_fk` ON `dt`;
ALTER TABLE `dt` DROP COLUMN `module_id`;

-- DROP `revision_num`, `revision_tracking_num`, and `revision_action` columns on `dt` table.
ALTER TABLE `dt` DROP COLUMN `revision_num`,
                 DROP COLUMN `revision_tracking_num`,
                 DROP COLUMN `revision_action`;

-- DROP `current_xbt_id` column on `xbt` table.
ALTER TABLE `xbt` DROP FOREIGN KEY `xbt_current_xbt_id_fk`;
DROP INDEX `xbt_current_xbt_id_fk` ON `xbt`;
ALTER TABLE `xbt` DROP COLUMN `current_xbt_id`;

-- DROP `release_id` column on `xbt` table.
ALTER TABLE `xbt` DROP FOREIGN KEY `xbt_release_id_fk`;
DROP INDEX `xbt_release_id_fk` ON `xbt`;
ALTER TABLE `xbt` DROP COLUMN `release_id`;

-- DROP `module_id` column on `xbt` table.
ALTER TABLE `xbt` DROP FOREIGN KEY `xbt_module_id_fk`;
DROP INDEX `xbt_module_id_fk` ON `xbt`;
ALTER TABLE `xbt` DROP COLUMN `module_id`;

-- DROP `revision_num`, `revision_tracking_num`, and `revision_action` columns on `xbt` table.
ALTER TABLE `xbt` DROP COLUMN `revision_num`,
                  DROP COLUMN `revision_tracking_num`,
                  DROP COLUMN `revision_action`;

-- DROP `based_code_list_id` and `module_id` columns on `code_list` table.
ALTER TABLE `code_list` DROP FOREIGN KEY `code_list_based_code_list_id_fk`,
                        DROP COLUMN `based_code_list_id`,
                        DROP FOREIGN KEY `code_list_module_id_fk`,
                        DROP COLUMN `module_id`;

ALTER TABLE `namespace` ADD CONSTRAINT `namespace_uk1` UNIQUE (prefix);
ALTER TABLE `dt` CHANGE `type` `type` VARCHAR(64) NOT NULL COMMENT 'This is the types of DT. List value is CDT, default BDT, unqualified BDT, qualified BDT.';


SET FOREIGN_KEY_CHECKS = 1;