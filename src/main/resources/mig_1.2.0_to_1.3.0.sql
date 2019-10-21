-- ----------------------------------------------------
-- Migration script for Score v1.3.0                 --
--                                                   --
-- Author: Hakju Oh <hakju.oh@nist.gov>              --
--         Kwanghoon Lee <kwanghoon.lee@nist.gov>    --
-- ----------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;

-- Add `openapi30_map` column on `xbt` table.
ALTER TABLE `xbt` ADD COLUMN `openapi30_map` varchar(500) AFTER `jbt_draft05_map`;
UPDATE `xbt` SET `openapi30_map` = `jbt_draft05_map`;
UPDATE `xbt` SET `openapi30_map` = '{"type":"string", "format":"date"}' WHERE `name` = 'date';
UPDATE `xbt` SET `openapi30_map` = '{"type":"number", "format": "float"}' WHERE `name` = 'float';
UPDATE `xbt` SET `openapi30_map` = '{"type":"integer"}' WHERE `name` = 'integer';
UPDATE `xbt` SET `openapi30_map` = '{"type":"integer", "minimum":0, "exclusiveMinimum":false}' WHERE `name` = 'non negative integer';
UPDATE `xbt` SET `openapi30_map` = '{"type":"integer", "minimum":0, "exclusiveMinimum":true}' WHERE `name` = 'positive integer';
UPDATE `xbt` SET `jbt_draft05_map` = '{"type":"number", "multipleOf":1, "minimum":0, "exclusiveMinimum":false}' WHERE `name` = 'non negative integer';
UPDATE `xbt` SET `jbt_draft05_map` = '{"type":"number", "multipleOf":1, "minimum":0, "exclusiveMinimum":true}' WHERE `name` = 'positive integer';
UPDATE `xbt` SET `openapi30_map` = '{"type":"number", "format":"double"}' WHERE `name` = 'double';

-- Add `text_content` table.
DROP TABLE IF EXISTS `text_content`;
CREATE TABLE `text_content` (
  `text_content_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `text_content_type` varchar(20) NOT NULL DEFAULT 'json',
  `text_content` text,
  PRIMARY KEY (`text_content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `bbie`
ADD COLUMN `example_text_content_id` bigint(20) unsigned DEFAULT NULL AFTER `definition`,
ADD KEY `bbie_example_text_content_id_fk` (`example_text_content_id`),
ADD CONSTRAINT `bbie_example_text_content_id_fk` FOREIGN KEY (`example_text_content_id`) REFERENCES `text_content` (`text_content_id`);

ALTER TABLE `bbie_sc`
ADD COLUMN `example_text_content_id` bigint(20) unsigned DEFAULT NULL AFTER `definition`,
ADD KEY `bbie_sc_example_text_content_id_fk` (`example_text_content_id`),
ADD CONSTRAINT `bbie_sc_example_text_content_id_fk` FOREIGN KEY (`example_text_content_id`) REFERENCES `text_content` (`text_content_id`);

SET FOREIGN_KEY_CHECKS = 1;