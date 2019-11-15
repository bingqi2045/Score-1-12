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

-- Add `example` column on `bbie`, `bbie_sc`
ALTER TABLE `bbie`
ADD COLUMN `example` text DEFAULT NULL AFTER `definition`;

ALTER TABLE `bbie_sc`
ADD COLUMN `example` text DEFAULT NULL AFTER `definition`;

ALTER TABLE `bcc`
ADD COLUMN `fixed_value` text COMMENT 'This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.';

ALTER TABLE `bccp`
ADD COLUMN `fixed_value` text COMMENT 'This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.';

ALTER TABLE `dt_sc`
ADD COLUMN `default_value` text COMMENT 'This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.',
ADD COLUMN `fixed_value` text COMMENT 'This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.';

SET FOREIGN_KEY_CHECKS = 1;