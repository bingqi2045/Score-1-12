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

-- Increase `release_id` to put 'Working' release at first on.
UPDATE `release` SET `release_id` = `release_id` + 1;
UPDATE `acc` SET `release_id` = `release_id` + 1 WHERE `release_id` IS NOT NULL;
UPDATE `ascc` SET `release_id` = `release_id` + 1 WHERE `release_id` IS NOT NULL;
UPDATE `asccp` SET `release_id` = `release_id` + 1 WHERE `release_id` IS NOT NULL;
UPDATE `bcc` SET `release_id` = `release_id` + 1 WHERE `release_id` IS NOT NULL;
UPDATE `bccp` SET `release_id` = `release_id` + 1 WHERE `release_id` IS NOT NULL;
UPDATE `blob_content` SET `release_id` = `release_id` + 1 WHERE `release_id` IS NOT NULL;
UPDATE `dt` SET `release_id` = `release_id` + 1 WHERE `release_id` IS NOT NULL;
UPDATE `module` SET `release_id` = `release_id` + 1 WHERE `release_id` IS NOT NULL;
UPDATE `top_level_abie` SET `release_id` = `release_id` + 1 WHERE `release_id` IS NOT NULL;
UPDATE `xbt` SET `release_id` = 1;
UPDATE `xbt` SET `release_id` = `release_id` + 1 WHERE `release_id` IS NOT NULL;

-- Update `namespace_id` = NULL for all user extension groups.
UPDATE `acc` SET `namespace_id` = NULL WHERE `acc`.`oagis_component_type` = 4;
UPDATE `asccp`, (SELECT `asccp`.`asccp_id` FROM `acc` JOIN `asccp` ON `acc`.`acc_id` = `asccp`.`role_of_acc_id` WHERE `acc`.`oagis_component_type` = 4) AS t SET `asccp`.`namespace_id` = NULL WHERE `asccp`.`asccp_id` = t.`asccp_id`;

-- Adding `Working` release.
INSERT INTO `release` (`release_id`, `release_num`, `release_note`, `namespace_id`, `created_by`, `last_updated_by`, `creation_timestamp`, `last_update_timestamp`, `state`)
VALUES
(1, 'Working', NULL, 1, 1, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6), 2);

-- Making relations between `acc` and `release` tables.

CREATE TABLE `acc_manifest` (
    `acc_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `module_id` bigint(20) unsigned,
    `acc_id` bigint(20) unsigned NOT NULL,
    `based_acc_manifest_id` bigint(20) unsigned,
    PRIMARY KEY (`acc_manifest_id`),
    KEY `acc_manifest_acc_id_fk` (`acc_id`),
    KEY `acc_manifest_based_acc_manifest_id_fk` (`based_acc_manifest_id`),
    KEY `acc_manifest_release_id_fk` (`release_id`),
    KEY `acc_manifest_module_id_fk` (`module_id`),
    CONSTRAINT `acc_manifest_acc_id_fk` FOREIGN KEY (`acc_id`) REFERENCES `acc` (`acc_id`),
    CONSTRAINT `acc_manifest_based_acc_manifest_id_fk` FOREIGN KEY (`based_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `acc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `acc_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Updating `based_acc_id`
UPDATE `acc`, (
    SELECT `acc`.`acc_id`, `acc`.`guid`, `acc`.`based_acc_id`, current.`acc_id` current_acc_id
    FROM `acc` as base
    JOIN `acc` ON base.`acc_id` = `acc`.`based_acc_id`
    JOIN `acc` current ON base.`acc_id` = current.`current_acc_id`
) t
SET `acc`.`based_acc_id` = t.current_acc_id
WHERE `acc`.`acc_id` = t.`acc_id`;

INSERT `acc_manifest` (`release_id`, `acc_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`,
    `acc`.`acc_id`
FROM `acc`
JOIN (SELECT MAX(`acc_id`) as `acc_id` FROM `acc` GROUP BY `guid`) t ON `acc`.`acc_id` = t.`acc_id`
ORDER BY `acc`.`acc_id`;

INSERT `acc_manifest` (`release_id`, `acc_id`)
SELECT
    `release`.`release_id`,
    `acc`.`acc_id`
FROM `acc` JOIN `release` ON `acc`.`release_id` = `release`.`release_id`
WHERE `acc`.`state` = 3;

-- Updating `based_acc_manifest_id`
UPDATE `acc_manifest`, (
    SELECT
        `acc_manifest`.`acc_manifest_id`, b.`acc_manifest_id` as `based_acc_manifest_id`
    FROM `acc_manifest`
    JOIN `acc` ON `acc_manifest`.`acc_id` = `acc`.`acc_id` AND `acc_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
    JOIN `acc_manifest` AS b ON `acc`.`based_acc_id` = b.`acc_id` AND  b.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
) t
SET `acc_manifest`.`based_acc_manifest_id` = t.`based_acc_manifest_id`
WHERE `acc_manifest`.`acc_manifest_id` = t.`acc_manifest_id`;

UPDATE `acc_manifest`, (
    SELECT
        `acc_manifest`.`acc_manifest_id`, b.`acc_manifest_id` as `based_acc_manifest_id`
    FROM `acc_manifest`
    JOIN `acc` ON `acc_manifest`.`acc_id` = `acc`.`acc_id` AND `acc_manifest`.`release_id` = `acc`.`release_id`
    JOIN `acc_manifest` AS b ON `acc`.`based_acc_id` = b.`acc_id` AND  b.`release_id` = `acc`.`release_id`
) t
SET `acc_manifest`.`based_acc_manifest_id` = t.`based_acc_manifest_id`
WHERE `acc_manifest`.`acc_manifest_id` = t.`acc_manifest_id`;

UPDATE `acc`
	JOIN `app_user` ON `acc`.`owner_user_id` = `app_user`.`app_user_id`
SET `acc`.`state` = IF(`acc`.`revision_num` = 1 AND `acc`.`revision_tracking_num` = 1, 7, 4)
WHERE `acc`.`state` = 3 AND `app_user`.`is_developer` = 1 AND `acc`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

UPDATE `acc`
	JOIN `app_user` ON `acc`.`owner_user_id` = `app_user`.`app_user_id`
SET `acc`.`state` = IF(`acc`.`revision_num` = 1 AND `acc`.`revision_tracking_num` = 1, 7, 5)
WHERE `acc`.`state` = 3 AND (`app_user`.`is_developer` != 1 OR `acc`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `acc`
	JOIN `app_user` ON `acc`.`owner_user_id` = `app_user`.`app_user_id`
SET `acc`.`state` = 3
WHERE `acc`.`state` = 2 AND (`app_user`.`is_developer` != 1 OR `acc`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));


-- Add deprecated annotations
ALTER TABLE `acc` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0. RELEASE_ID is an incremental integer. It is an unformatted counter part of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. A release ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\\n\\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released. USER_EXTENSION_GROUP component type is never part of a release.\\n\\nUnpublished components cannot be released.\\n\\nThis column is NULL for the current record.',
                  MODIFY COLUMN `current_acc_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0. This is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\\n\\nIt is noted that although this is a foreign key by definition, we don''t specify a foreign key in the data model. This is because when an entity is deleted the current record won''t exist anymore.\\n\\nThe value of this column for the current record should be left NULL.',
                  MODIFY COLUMN `state` int(11) COMMENT '1 = WIP, 2 = Draft, 3 = QA, 4 = Candidate, 5 = Production, 6 = Release Draft, 7 = Published. This the revision life cycle state of the ACC.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.';

-- Add indices
CREATE INDEX `acc_guid_idx` ON `acc` (`guid`);
CREATE INDEX `acc_revision_idx` ON `acc` (`revision_num`, `revision_tracking_num`);
CREATE INDEX `acc_last_update_timestamp_desc_idx` ON `acc` (`last_update_timestamp` DESC);

-- Making relations between `asccp` and `release` tables.

CREATE TABLE `asccp_manifest` (
    `asccp_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `module_id` bigint(20) unsigned,
    `asccp_id` bigint(20) unsigned NOT NULL,
    `role_of_acc_manifest_id` bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`asccp_manifest_id`),
    KEY `asccp_manifest_asccp_id_fk` (`asccp_id`),
    KEY `asccp_manifest_role_of_acc_manifest_id_fk` (`role_of_acc_manifest_id`),
    KEY `asccp_manifest_release_id_fk` (`release_id`),
    KEY `asccp_manifest_module_id_fk` (`module_id`),
    CONSTRAINT `asccp_manifest_asccp_id_fk` FOREIGN KEY (`asccp_id`) REFERENCES `asccp` (`asccp_id`),
    CONSTRAINT `asccp_manifest_role_of_acc_manifest_id_fk` FOREIGN KEY (`role_of_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `asccp_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `asccp_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Updating `role_of_acc_id`
UPDATE `asccp`, (
    SELECT `asccp`.`asccp_id`, `asccp`.`role_of_acc_id`, max(current.`acc_id`) current_acc_id
    FROM `asccp`
    JOIN `acc` ON `acc`.`acc_id` = `asccp`.`role_of_acc_id`
    JOIN `acc` current ON `asccp`.`role_of_acc_id` = current.`current_acc_id`
    GROUP BY
        `asccp`.`asccp_id`
) t
SET `asccp`.`role_of_acc_id` = t.current_acc_id
WHERE `asccp`.`asccp_id` = t.asccp_id;

INSERT `asccp_manifest` (`release_id`, `module_id`, `asccp_id`, `role_of_acc_manifest_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`, `asccp`.`module_id`,
    `asccp`.`asccp_id`, `acc_manifest`.`acc_manifest_id`
FROM `asccp`
JOIN (SELECT MAX(`asccp_id`) as `asccp_id` FROM `asccp` GROUP BY `guid`) t ON `asccp`.`asccp_id` = t.`asccp_id`
JOIN `acc_manifest` ON `asccp`.`role_of_acc_id` = `acc_manifest`.`acc_id`
 AND `acc_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
ORDER BY `asccp`.`asccp_id`;

INSERT `asccp_manifest` (`release_id`, `module_id`, `asccp_id`, `role_of_acc_manifest_id`)
SELECT
    `release`.`release_id`, `asccp`.`module_id`,
    `asccp`.`asccp_id`, `acc_manifest`.`acc_manifest_id`
FROM `asccp`
JOIN `release` ON `asccp`.`release_id` = `release`.`release_id`
JOIN `acc_manifest` ON `asccp`.`role_of_acc_id` = `acc_manifest`.`acc_id`
 AND `acc_manifest`.`release_id` = `release`.`release_id`
WHERE `asccp`.`state` = 3;

UPDATE `asccp`
	JOIN `app_user` ON `asccp`.`owner_user_id` = `app_user`.`app_user_id`
SET `asccp`.`state` = IF(`asccp`.`revision_num` = 1 AND `asccp`.`revision_tracking_num` = 1, 7, 4)
WHERE `asccp`.`state` = 3 AND `app_user`.`is_developer` = 1 AND `asccp`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

UPDATE `asccp`
	JOIN `app_user` ON `asccp`.`owner_user_id` = `app_user`.`app_user_id`
SET `asccp`.`state` = IF(`asccp`.`revision_num` = 1 AND `asccp`.`revision_tracking_num` = 1, 7, 5)
WHERE `asccp`.`state` = 3 AND (`app_user`.`is_developer` != 1 OR `asccp`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `asccp`
	JOIN `app_user` ON `asccp`.`owner_user_id` = `app_user`.`app_user_id`
SET `asccp`.`state` = 3
WHERE `asccp`.`state` = 2 AND (`app_user`.`is_developer` != 1 OR `asccp`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

-- Add deprecated annotations
ALTER TABLE `asccp` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0. RELEASE_ID is an incremental integer. It is an unformatted counter part of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. A release ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\n\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released. USER_EXTENSION_GROUP component type is never part of a release.\n\nUnpublished components cannot be released.\n\nThis column is NULLl for the current record.',
                    MODIFY COLUMN `current_asccp_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0. This is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\n\nIt is noted that although this is a foreign key by definition, we don''t specify a foreign key in the data model. This is because when an entity is deleted the current record won''t exist anymore.\n\nThe value of this column for the current record should be left NULL.',
                    MODIFY COLUMN `state` int(11) COMMENT '1 = WIP, 2 = Draft, 3 = QA, 4 = Candidate, 5 = Production, 6 = Release Draft, 7 = Published. This the revision life cycle state of the ASCCP.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.';

-- Add indices
CREATE INDEX `asccp_guid_idx` ON `asccp` (`guid`);
CREATE INDEX `asccp_revision_idx` ON `asccp` (`revision_num`, `revision_tracking_num`);
CREATE INDEX `asccp_last_update_timestamp_desc_idx` ON `asccp` (`last_update_timestamp` DESC);

-- Making relations between `dt` and `release` tables.

CREATE TABLE `dt_manifest` (
    `dt_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `module_id` bigint(20) unsigned,
    `dt_id` bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`dt_manifest_id`),
    KEY `dt_manifest_dt_id_fk` (`dt_id`),
    KEY `dt_manifest_release_id_fk` (`release_id`),
    KEY `dt_manifest_module_id_fk` (`module_id`),
    CONSTRAINT `dt_manifest_dt_id_fk` FOREIGN KEY (`dt_id`) REFERENCES `dt` (`dt_id`),
    CONSTRAINT `dt_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `dt_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT `dt_manifest` (`release_id`, `module_id`, `dt_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`, `dt`.`module_id`,
    `dt`.`dt_id`
FROM `dt` JOIN (SELECT MAX(`dt_id`) as `dt_id` FROM `dt` GROUP BY `guid`) t ON `dt`.`dt_id` = t.`dt_id`
ORDER BY `dt`.`dt_id`;

INSERT `dt_manifest` (`release_id`, `module_id`, `dt_id`)
SELECT
    `release`.`release_id`, `dt`.`module_id`,
    `dt`.`dt_id`
FROM `dt` JOIN `release` ON `dt`.`release_id` = `release`.`release_id`
WHERE `dt`.`state` = 3;

UPDATE `dt`
	JOIN `app_user` ON `dt`.`owner_user_id` = `app_user`.`app_user_id`
SET `dt`.`state` = IF(`dt`.`revision_num` = 0 AND `dt`.`revision_tracking_num` = 0, 7, 4)
WHERE `dt`.`state` = 3 AND `app_user`.`is_developer` = 1 AND `dt`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');


UPDATE `dt`
	JOIN `app_user` ON `dt`.`owner_user_id` = `app_user`.`app_user_id`
SET `dt`.`state` = IF(`dt`.`revision_num` = 0 AND `dt`.`revision_tracking_num` = 0, 7, 5)
WHERE `dt`.`state` = 3 AND (`app_user`.`is_developer` != 1 OR `dt`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `dt`
	JOIN `app_user` ON `dt`.`owner_user_id` = `app_user`.`app_user_id`
SET `dt`.`state` = 3
WHERE `dt`.`state` = 2 AND (`app_user`.`is_developer` != 1 OR `dt`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `dt` SET `revision_num` = 1, `revision_tracking_num` = 1, `revision_action` = 1;

-- Add deprecated annotations
ALTER TABLE `dt` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0. RELEASE_ID is an incremental integer. It is an unformatted counter part of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. A release ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\n\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released. USER_EXTENSION_GROUP component type is never part of a release.\n\nUnpublished components cannot be released.\n\nThis column is NULL for the current record.',
                 MODIFY COLUMN `current_bdt_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0. This is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\n\nIt is noted that although this is a foreign key by definition, we don''t specify a foreign key in the data model. This is because when an entity is deleted the current record won''t exist anymore.\n\nThe value of this column for the current record should be left NULL.\n\nThe column name is specific to BDT because, the column does not apply to CDT.',
                 MODIFY COLUMN `state` int(11) COMMENT '1 = WIP, 2 = Draft, 3 = QA, 4 = Candidate, 5 = Production, 6 = Release Draft, 7 = Published. This the revision life cycle state of the DT.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.';

-- Add indices
CREATE INDEX `dt_guid_idx` ON `dt` (`guid`);
CREATE INDEX `dt_revision_idx` ON `dt` (`revision_num`, `revision_tracking_num`);
CREATE INDEX `dt_last_update_timestamp_desc_idx` ON `dt` (`last_update_timestamp` DESC);

-- Drop unique index
ALTER TABLE `dt` DROP INDEX `dt_uk1`;

-- Making relations between `dt_sc` and `release` tables.

CREATE TABLE `dt_sc_manifest` (
    `dt_sc_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `dt_sc_id` bigint(20) unsigned NOT NULL,
    `owner_dt_manifest_id` bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`dt_sc_manifest_id`),
    KEY `dt_sc_manifest_dt_sc_id_fk` (`dt_sc_id`),
    KEY `dt_sc_manifest_release_id_fk` (`release_id`),
    KEY `dt_sc_manifest_owner_dt_manifest_id_fk` (`owner_dt_manifest_id`),
    CONSTRAINT `dt_sc_manifest_dt_sc_id_fk` FOREIGN KEY (`dt_sc_id`) REFERENCES `dt_sc` (`dt_sc_id`),
    CONSTRAINT `dt_sc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `dt_sc_manifest_owner_dt_manifest_id_fk` FOREIGN KEY (`owner_dt_manifest_id`) REFERENCES `dt_manifest` (`dt_manifest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT `dt_sc_manifest` (`release_id`, `dt_sc_id`, `owner_dt_manifest_id`)
SELECT
    `release`.`release_id`,
    `dt_sc`.`dt_sc_id`, `dt_manifest`.`dt_manifest_id`
FROM `dt_sc` JOIN `dt_manifest` ON `dt_sc`.`owner_dt_id` = `dt_manifest`.`dt_id`
             JOIN `release` ON `dt_manifest`.`release_id` = `release`.`release_id`
ORDER BY `release`.`release_id`;

-- Add indices
CREATE INDEX `dt_sc_guid_idx` ON `dt_sc` (`guid`);

-- Drop unique index
ALTER TABLE `dt_sc` DROP INDEX `dt_sc_uk1`;

-- Making relations between `bccp` and `release` tables.

CREATE TABLE `bccp_manifest` (
    `bccp_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `module_id` bigint(20) unsigned,
    `bccp_id` bigint(20) unsigned NOT NULL,
    `bdt_manifest_id` bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`bccp_manifest_id`),
    KEY `bccp_manifest_bccp_id_fk` (`bccp_id`),
    KEY `bccp_manifest_bdt_manifest_id_fk` (`bdt_manifest_id`),
    KEY `bccp_manifest_release_id_fk` (`release_id`),
    KEY `bccp_manifest_module_id_fk` (`module_id`),
    CONSTRAINT `bccp_manifest_bccp_id_fk` FOREIGN KEY (`bccp_id`) REFERENCES `bccp` (`bccp_id`),
    CONSTRAINT `bccp_manifest_bdt_manifest_id_fk` FOREIGN KEY (`bdt_manifest_id`) REFERENCES `dt_manifest` (`dt_manifest_id`),
    CONSTRAINT `bccp_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `bccp_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT `bccp_manifest` (`release_id`, `module_id`, `bccp_id`, `bdt_manifest_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`, `bccp`.`module_id`,
    `bccp`.`bccp_id`, `dt_manifest`.`dt_manifest_id`
FROM `bccp` JOIN (SELECT MAX(`bccp_id`) as `bccp_id` FROM `bccp` GROUP BY `guid`) t ON `bccp`.`bccp_id` = t.`bccp_id`
JOIN `dt_manifest` ON `dt_manifest`.`dt_id` = `bccp`.`bdt_id`
 AND `dt_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
ORDER BY `bccp`.`bccp_id`;

INSERT `bccp_manifest` (`release_id`, `module_id`, `bccp_id`, `bdt_manifest_id`)
SELECT
    `release`.`release_id`, `bccp`.`module_id`,
    `bccp`.`bccp_id`, `dt_manifest`.`dt_manifest_id`
FROM `bccp` JOIN `release` ON `bccp`.`release_id` = `release`.`release_id`
JOIN `dt_manifest` ON `dt_manifest`.`dt_id` = `bccp`.`bdt_id`
 AND `dt_manifest`.`release_id` = `release`.`release_id`
WHERE `bccp`.`state` = 3;

UPDATE `bccp`
	JOIN `app_user` ON `bccp`.`owner_user_id` = `app_user`.`app_user_id`
SET `bccp`.`state` = IF(`bccp`.`revision_num` = 1 AND `bccp`.`revision_tracking_num` = 1, 7, 4)
WHERE `bccp`.`state` = 3 AND `app_user`.`is_developer` = 1 AND `bccp`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

UPDATE `bccp`
	JOIN `app_user` ON `bccp`.`owner_user_id` = `app_user`.`app_user_id`
SET `bccp`.`state` = IF(`bccp`.`revision_num` = 1 AND `bccp`.`revision_tracking_num` = 1, 7, 5)
WHERE `bccp`.`state` = 3 AND (`app_user`.`is_developer` != 1 OR `bccp`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `bccp`
	JOIN `app_user` ON `bccp`.`owner_user_id` = `app_user`.`app_user_id`
SET `bccp`.`state` = 3
WHERE `bccp`.`state` = 2 AND (`app_user`.`is_developer` != 1 OR `bccp`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

-- Add deprecated annotations
ALTER TABLE `bccp` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0. RELEASE_ID is an incremental integer. It is an unformatted counter part of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. A release ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\n\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released. USER_EXTENSION_GROUP component type is never part of a release.\n\nUnpublished components cannot be released.\n\nThis column is NULLl for the current record.',
                   MODIFY COLUMN `current_bccp_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0. This is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\n\nIt is noted that although this is a foreign key by definition, we don''t specify a foreign key in the data model. This is because when an entity is deleted the current record won''t exist anymore.\n\nThe value of this column for the current record should be left NULL.',
                   MODIFY COLUMN `state` int(11) COMMENT '1 = WIP, 2 = Draft, 3 = QA, 4 = Candidate, 5 = Production, 6 = Release Draft, 7 = Published. This the revision life cycle state of the BCCP.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.';

-- Add indices
CREATE INDEX `bccp_guid_idx` ON `bccp` (`guid`);
CREATE INDEX `bccp_revision_idx` ON `bccp` (`revision_num`, `revision_tracking_num`);
CREATE INDEX `bccp_last_update_timestamp_desc_idx` ON `bccp` (`last_update_timestamp` DESC);

-- Making relations between `ascc` and `release` tables.

CREATE TABLE `ascc_manifest` (
    `ascc_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `ascc_id` bigint(20) unsigned NOT NULL,
    `from_acc_manifest_id` bigint(20) unsigned NOT NULL,
    `to_asccp_manifest_id` bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`ascc_manifest_id`),
    KEY `ascc_manifest_ascc_id_fk` (`ascc_id`),
    KEY `ascc_manifest_release_id_fk` (`release_id`),
    KEY `ascc_manifest_from_acc_manifest_id_fk` (`from_acc_manifest_id`),
    KEY `ascc_manifest_to_asccp_manifest_id_fk` (`to_asccp_manifest_id`),
    CONSTRAINT `ascc_manifest_ascc_id_fk` FOREIGN KEY (`ascc_id`) REFERENCES `ascc` (`ascc_id`),
    CONSTRAINT `ascc_manifest_from_acc_manifest_id_fk` FOREIGN KEY (`from_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `ascc_manifest_to_asccp_manifest_id_fk` FOREIGN KEY (`to_asccp_manifest_id`) REFERENCES `asccp_manifest` (`asccp_manifest_id`),
    CONSTRAINT `ascc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Updating `from_acc_id`
UPDATE `ascc`, (
    SELECT `ascc`.`ascc_id`, `ascc`.`from_acc_id`, MAX(current.`acc_id`) current_acc_id
    FROM `ascc`
    JOIN `acc` ON `acc`.`acc_id` = `ascc`.`from_acc_id`
    JOIN `acc` current ON `ascc`.`from_acc_id` = current.`current_acc_id`
    GROUP BY `ascc`.`ascc_id`
) t
SET `ascc`.`from_acc_id` = t.current_acc_id
WHERE `ascc`.`ascc_id` = t.ascc_id;

-- Updating `to_asccp_id`
UPDATE `ascc`, (
    SELECT `ascc`.`ascc_id`, `ascc`.`to_asccp_id`, MAX(current.`asccp_id`) current_asccp_id
    FROM `ascc`
    JOIN `asccp` ON `asccp`.`asccp_id` = `ascc`.`to_asccp_id`
    JOIN `asccp` current ON `asccp`.`asccp_id` = current.`current_asccp_id`
    GROUP BY `ascc`.`ascc_id`
) t
SET `ascc`.`to_asccp_id` = t.current_asccp_id
WHERE `ascc`.`ascc_id` = t.ascc_id;

INSERT `ascc_manifest` (`release_id`, `ascc_id`, `from_acc_manifest_id`, `to_asccp_manifest_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`,
    `ascc`.`ascc_id`, `acc_manifest`.`acc_manifest_id`, `asccp_manifest`.`asccp_manifest_id`
FROM `ascc`
JOIN (SELECT MAX(`ascc_id`) as `ascc_id`
      FROM `ascc`
      GROUP BY `guid`) t ON `ascc`.`ascc_id` = t.`ascc_id`
JOIN `acc_manifest` ON `acc_manifest`.`acc_id` = `ascc`.`from_acc_id`
 AND `acc_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
JOIN `asccp_manifest` ON `asccp_manifest`.`asccp_id` = `ascc`.`to_asccp_id`
 AND `asccp_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
ORDER BY `ascc`.`ascc_id`;

INSERT `ascc_manifest` (`release_id`, `ascc_id`, `from_acc_manifest_id`, `to_asccp_manifest_id`)
SELECT
    `release`.`release_id`,
    MAX(`ascc`.`ascc_id`) `ascc_id`, `acc_manifest`.`acc_manifest_id`, `asccp_manifest`.`asccp_manifest_id`
FROM `ascc` JOIN `release` ON `ascc`.`release_id` = `release`.`release_id`
JOIN `acc_manifest` ON `acc_manifest`.`acc_id` = `ascc`.`from_acc_id`
 AND `acc_manifest`.`release_id` = `release`.`release_id`
JOIN `asccp_manifest` ON `asccp_manifest`.`asccp_id` = `ascc`.`to_asccp_id`
 AND `asccp_manifest`.`release_id` = `release`.`release_id`
WHERE `ascc`.`state` = 3
GROUP BY `acc_manifest`.`acc_manifest_id`, `asccp_manifest`.`asccp_manifest_id`;

UPDATE `ascc`
	JOIN `app_user` ON `ascc`.`owner_user_id` = `app_user`.`app_user_id`
SET `ascc`.`state` = IF(`ascc`.`revision_num` = 1 AND `ascc`.`revision_tracking_num` = 1, 7, 4)
WHERE `ascc`.`state` = 3 AND `app_user`.`is_developer` = 1 AND `ascc`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

UPDATE `ascc`
	JOIN `app_user` ON `ascc`.`owner_user_id` = `app_user`.`app_user_id`
SET `ascc`.`state` = IF(`ascc`.`revision_num` = 1 AND `ascc`.`revision_tracking_num` = 1, 7, 5)
WHERE `ascc`.`state` = 3 AND (`app_user`.`is_developer` != 1 OR `ascc`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `ascc`
	JOIN `app_user` ON `ascc`.`owner_user_id` = `app_user`.`app_user_id`
SET `ascc`.`state` = 3
WHERE `ascc`.`state` = 2 AND (`app_user`.`is_developer` != 1 OR `ascc`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

-- Add deprecated annotations
ALTER TABLE `ascc` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0. RELEASE_ID is an incremental integer. It is an unformatted counterpart of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. RELEASE_ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\n\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released.\n\nUnpublished components cannot be released.\n\nThis column is NULL for the current record.',
                   MODIFY COLUMN `current_ascc_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0. This is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\n\nIt is noted that although this is a foreign key by definition, we don''t specify a foreign key in the data model. This is because when an entity is deleted the current record won''t exist anymore.\n\nThe value of this column for the current record should be left NULL.',
                   MODIFY COLUMN `state` int(11) COMMENT '1 = WIP, 2 = Draft, 3 = QA, 4 = Candidate, 5 = Production, 6 = Release Draft, 7 = Published. This the revision life cycle state of the ASCC.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.';

-- Add indices
CREATE INDEX `ascc_guid_idx` ON `ascc` (`guid`);
CREATE INDEX `ascc_revision_idx` ON `ascc` (`revision_num`, `revision_tracking_num`);
CREATE INDEX `ascc_last_update_timestamp_desc_idx` ON `ascc` (`last_update_timestamp` DESC);

-- Making relations between `bcc` and `release` tables.

CREATE TABLE `bcc_manifest` (
    `bcc_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `bcc_id` bigint(20) unsigned NOT NULL,
    `from_acc_manifest_id` bigint(20) unsigned NOT NULL,
    `to_bccp_manifest_id` bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`bcc_manifest_id`),
    KEY `bcc_manifest_bcc_id_fk` (`bcc_id`),
    KEY `bcc_manifest_release_id_fk` (`release_id`),
    KEY `bcc_manifest_from_acc_manifest_id_fk` (`from_acc_manifest_id`),
    KEY `bcc_manifest_to_bccp_manifest_id_fk` (`to_bccp_manifest_id`),
    CONSTRAINT `bcc_manifest_bcc_id_fk` FOREIGN KEY (`bcc_id`) REFERENCES `bcc` (`bcc_id`),
    CONSTRAINT `bcc_manifest_from_acc_manifest_id_fk` FOREIGN KEY (`from_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `bcc_manifest_to_bccp_manifest_id_fk` FOREIGN KEY (`to_bccp_manifest_id`) REFERENCES `bccp_manifest` (`bccp_manifest_id`),
    CONSTRAINT `bcc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Updating `from_acc_id`
UPDATE `bcc`, (
    SELECT `bcc`.`bcc_id`, `bcc`.`from_acc_id`, MAX(current.`acc_id`) current_acc_id
    FROM `bcc`
    JOIN `acc` ON `acc`.`acc_id` = `bcc`.`from_acc_id`
    JOIN `acc` current ON `bcc`.`from_acc_id` = current.`current_acc_id`
    GROUP BY `bcc`.`bcc_id`
) t
SET `bcc`.`from_acc_id` = t.current_acc_id
WHERE `bcc`.`bcc_id` = t.bcc_id;

-- Updating `to_bccp_id`
UPDATE `bcc`, (
    SELECT `bcc`.`bcc_id`, `bcc`.`to_bccp_id`, MAX(current.`bccp_id`) current_bccp_id
    FROM `bcc`
    JOIN `bccp` ON `bccp`.`bccp_id` = `bcc`.`to_bccp_id`
    JOIN `bccp` current ON `bccp`.`bccp_id` = current.`current_bccp_id`
    GROUP BY `bcc`.`bcc_id`
) t
SET `bcc`.`to_bccp_id` = t.current_bccp_id
WHERE `bcc`.`bcc_id` = t.bcc_id;

INSERT `bcc_manifest` (`release_id`, `bcc_id`, `from_acc_manifest_id`, `to_bccp_manifest_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`,
    `bcc`.`bcc_id`, `acc_manifest`.`acc_manifest_id`, `bccp_manifest`.`bccp_manifest_id`
FROM `bcc`
JOIN (SELECT MAX(`bcc_id`) as `bcc_id`
      FROM `bcc`
      GROUP BY `guid`) t ON `bcc`.`bcc_id` = t.`bcc_id`
JOIN `acc_manifest` ON `acc_manifest`.`acc_id` = `bcc`.`from_acc_id`
 AND `acc_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
JOIN `bccp_manifest` ON `bccp_manifest`.`bccp_id` = `bcc`.`to_bccp_id`
 AND `bccp_manifest`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working')
ORDER BY `bcc`.`bcc_id`;

INSERT `bcc_manifest` (`release_id`, `bcc_id`, `from_acc_manifest_id`, `to_bccp_manifest_id`)
SELECT
    `release`.`release_id`,
    MAX(`bcc`.`bcc_id`) as `bcc_id`, `acc_manifest`.`acc_manifest_id`, `bccp_manifest`.`bccp_manifest_id`
FROM `bcc` JOIN `release` ON `bcc`.`release_id` = `release`.`release_id`
JOIN `acc_manifest` ON `acc_manifest`.`acc_id` = `bcc`.`from_acc_id`
 AND `acc_manifest`.`release_id` = `release`.`release_id`
JOIN `bccp_manifest` ON `bccp_manifest`.`bccp_id` = `bcc`.`to_bccp_id`
 AND `bccp_manifest`.`release_id` = `release`.`release_id`
WHERE `bcc`.`state` = 3
GROUP BY `acc_manifest`.`acc_manifest_id`, `bccp_manifest`.`bccp_manifest_id`;

UPDATE `bcc`
	JOIN `app_user` ON `bcc`.`owner_user_id` = `app_user`.`app_user_id`
SET `bcc`.`state` = IF(`bcc`.`revision_num` = 1 AND `bcc`.`revision_tracking_num` = 1, 7, 4)
WHERE `bcc`.`state` = 3 AND `app_user`.`is_developer` = 1 AND `bcc`.`release_id` = (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working');

UPDATE `bcc`
	JOIN `app_user` ON `bcc`.`owner_user_id` = `app_user`.`app_user_id`
SET `bcc`.`state` = IF(`bcc`.`revision_num` = 1 AND `bcc`.`revision_tracking_num` = 1, 7, 5)
WHERE `bcc`.`state` = 3 AND (`app_user`.`is_developer` != 1 OR `bcc`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

UPDATE `bcc`
	JOIN `app_user` ON `bcc`.`owner_user_id` = `app_user`.`app_user_id`
SET `bcc`.`state` = 3
WHERE `bcc`.`state` = 2 AND (`app_user`.`is_developer` != 1 OR `bcc`.`release_id` != (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working'));

-- Add deprecated annotations
ALTER TABLE `bcc` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0. RELEASE_ID is an incremental integer. It is an unformatted counterpart of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. RELEASE_ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\n\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released.\n\nUnpublished components cannot be released.\n\nThis column is NULLl for the current record.',
                  MODIFY COLUMN `current_bcc_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0. This is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\n\nIt is noted that although this is a foreign key by definition, we don''t specify a foreign key in the data model. This is because when an entity is deleted the current record won''t exist anymore.\n\nThe value of this column for the current record should be left NULL.',
                  MODIFY COLUMN `state` int(11) COMMENT '1 = WIP, 2 = Draft, 3 = QA, 4 = Candidate, 5 = Production, 6 = Release Draft, 7 = Published. This the revision life cycle state of the BCC.\n\nState change can''t be undone. But the history record can still keep the records of when the state was changed.';

-- Add indices
CREATE INDEX `bcc_guid_idx` ON `bcc` (`guid`);
CREATE INDEX `bcc_revision_idx` ON `bcc` (`revision_num`, `revision_tracking_num`);
CREATE INDEX `bcc_last_update_timestamp_desc_idx` ON `bcc` (`last_update_timestamp` DESC);

-- BIEs
-- ABIE
ALTER TABLE `abie` ADD COLUMN `based_acc_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key to the ACC_MANIFEST table refering to the ACC, on which the business context has been applied to derive this ABIE.' AFTER `guid`,
                   ADD CONSTRAINT `abie_based_acc_manifest_id_fk` FOREIGN KEY (`based_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`);

UPDATE `abie`, `acc_manifest`, `release`
SET `abie`.`based_acc_manifest_id` = `acc_manifest`.`acc_manifest_id`
WHERE `abie`.`based_acc_id` = `acc_manifest`.`acc_id`
  AND `acc_manifest`.`release_id` = `release`.`release_id`
  AND `release`.`release_num` != 'Working';

ALTER TABLE `abie` DROP FOREIGN KEY `abie_based_acc_id_fk`,
                   DROP COLUMN `based_acc_id`;

-- ASBIE
ALTER TABLE `asbie` ADD COLUMN `based_ascc_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'The BASED_ASCC_MANIFEST_ID column refers to the ASCC_MANIFEST record, which this ASBIE contextualizes.' AFTER `guid`,
                    ADD CONSTRAINT `asbie_based_ascc_manifest_id_fk` FOREIGN KEY (`based_ascc_manifest_id`) REFERENCES `ascc_manifest` (`ascc_manifest_id`);

UPDATE `asbie`, `ascc_manifest`, `release`
SET `asbie`.`based_ascc_manifest_id` = `ascc_manifest`.`ascc_manifest_id`
WHERE `asbie`.`based_ascc_id` = `ascc_manifest`.`ascc_id`
  AND `ascc_manifest`.`release_id` = `release`.`release_id`
  AND `release`.`release_num` != 'Working';

ALTER TABLE `asbie` DROP FOREIGN KEY `asbie_based_ascc_id_fk`,
                    DROP COLUMN `based_ascc_id`;

-- BBIE
ALTER TABLE `bbie` ADD COLUMN `based_bcc_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'The BASED_BCC_MANIFEST_ID column refers to the BCC_MANIFEST record, which this BBIE contextualizes.' AFTER `guid`,
                   ADD CONSTRAINT `bbie_based_bcc_manifest_id_fk` FOREIGN KEY (`based_bcc_manifest_id`) REFERENCES `bcc_manifest` (`bcc_manifest_id`);

UPDATE `bbie`, `bcc_manifest`, `release`
SET `bbie`.`based_bcc_manifest_id` = `bcc_manifest`.`bcc_manifest_id`
WHERE `bbie`.`based_bcc_id` = `bcc_manifest`.`bcc_id`
  AND `bcc_manifest`.`release_id` = `release`.`release_id`
  AND `release`.`release_num` != 'Working';

ALTER TABLE `bbie` DROP FOREIGN KEY `bbie_based_bcc_id_fk`,
                   DROP COLUMN `based_bcc_id`;

-- ASBIEP
ALTER TABLE `asbiep` ADD COLUMN `based_asccp_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key pointing to the ASCCP_MANIFEST record. It is the ASCCP, on which the ASBIEP contextualizes.' AFTER `guid`,
                     ADD CONSTRAINT `asbiep_based_asccp_manifest_id_fk` FOREIGN KEY (`based_asccp_manifest_id`) REFERENCES `asccp_manifest` (`asccp_manifest_id`);

UPDATE `asbiep`, `asccp_manifest`, `release`
SET `asbiep`.`based_asccp_manifest_id` = `asccp_manifest`.`asccp_manifest_id`
WHERE `asbiep`.`based_asccp_id` = `asccp_manifest`.`asccp_id`
  AND `asccp_manifest`.`release_id` = `release`.`release_id`
  AND `release`.`release_num` != 'Working';

ALTER TABLE `asbiep` DROP FOREIGN KEY `asbiep_based_asccp_id_fk`,
                     DROP COLUMN `based_asccp_id`;

-- BBIEP
ALTER TABLE `bbiep` ADD COLUMN `based_bccp_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'A foreign key pointing to the BCCP_MANIFEST record. It is the BCCP, which the BBIEP contextualizes.' AFTER `guid`,
                    ADD CONSTRAINT `bbiep_based_bccp_manifest_id_fk` FOREIGN KEY (`based_bccp_manifest_id`) REFERENCES `bccp_manifest` (`bccp_manifest_id`);

UPDATE `bbiep`, `bccp_manifest`, `release`
SET `bbiep`.`based_bccp_manifest_id` = `bccp_manifest`.`bccp_manifest_id`
WHERE `bbiep`.`based_bccp_id` = `bccp_manifest`.`bccp_id`
  AND `bccp_manifest`.`release_id` = `release`.`release_id`
  AND `release`.`release_num` != 'Working';

ALTER TABLE `bbiep` DROP FOREIGN KEY `bbiep_based_bccp_id_fk`,
                    DROP COLUMN `based_bccp_id`;

-- BBIE_SC
ALTER TABLE `bbie_sc` ADD COLUMN `based_dt_sc_manifest_id` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the DT_SC_MANIFEST table. This should correspond to the DT_SC of the BDT of the based BCC and BCCP.' AFTER `guid`,
                      ADD CONSTRAINT `bbie_sc_based_dt_sc_manifest_id_fk` FOREIGN KEY (`based_dt_sc_manifest_id`) REFERENCES `dt_sc_manifest` (`dt_sc_manifest_id`);

UPDATE `bbie_sc`, `dt_sc_manifest`, `release`
SET `bbie_sc`.`based_dt_sc_manifest_id` = `dt_sc_manifest`.`dt_sc_manifest_id`
WHERE `bbie_sc`.`dt_sc_id` = `dt_sc_manifest`.`dt_sc_id`
  AND `dt_sc_manifest`.`release_id` = `release`.`release_id`
  AND `release`.`release_num` != 'Working';

ALTER TABLE `bbie_sc` DROP FOREIGN KEY `bbie_sc_dt_sc_id_fk`,
                      DROP COLUMN `dt_sc_id`;

-- Making relations between `xbt` and `release` tables.

CREATE TABLE `xbt_manifest` (
    `xbt_manifest_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id` bigint(20) unsigned NOT NULL,
    `module_id` bigint(20) unsigned,
    `xbt_id` bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`xbt_manifest_id`),
    KEY `xbt_manifest_xbt_id_fk` (`xbt_id`),
    KEY `xbt_manifest_release_id_fk` (`release_id`),
    KEY `xbt_manifest_module_id_fk` (`module_id`),
    CONSTRAINT `xbt_manifest_xbt_id_fk` FOREIGN KEY (`xbt_id`) REFERENCES `xbt` (`xbt_id`),
    CONSTRAINT `xbt_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `xbt_manifest_module_id_fk` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`)
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

INSERT `xbt_manifest` (`release_id`, `module_id`, `xbt_id`)
SELECT
    (SELECT `release_id` FROM `release` WHERE `release_num` = 'Working') as `release_id`, `xbt`.`module_id`,
    `xbt`.`xbt_id`
FROM `xbt` JOIN (SELECT MAX(`xbt_id`) as `xbt_id` FROM `xbt` GROUP BY `guid`) t ON `xbt`.`xbt_id` = t.`xbt_id`
ORDER BY `xbt`.`xbt_id`;

INSERT `xbt_manifest` (`release_id`, `module_id`, `xbt_id`)
SELECT
    `release`.`release_id`, `xbt`.`module_id`,
    `xbt`.`xbt_id`
FROM `xbt` JOIN `release` ON `xbt`.`release_id` = `release`.`release_id`
WHERE `xbt`.`state` = 3;

UPDATE `xbt` SET `revision_num` = 1, `revision_tracking_num` = 1, `revision_action` = 1;

-- Add deprecated annotations
ALTER TABLE `xbt` MODIFY COLUMN `release_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.',
                  MODIFY COLUMN `current_xbt_id` bigint(20) unsigned DEFAULT NULL COMMENT '@deprecated since 2.0.0.';

-- Add indices
CREATE INDEX `xbt_guid_idx` ON `xbt` (`guid`);
CREATE INDEX `xbt_revision_idx` ON `xbt` (`revision_num`, `revision_tracking_num`);
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

SET FOREIGN_KEY_CHECKS = 1;