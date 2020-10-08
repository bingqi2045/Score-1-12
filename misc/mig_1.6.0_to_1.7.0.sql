-- ----------------------------------------------------
-- Migration script for Score v1.7.0                 --
--                                                   --
-- Author: Hakju Oh <hakju.oh@nist.gov>              --
-- ----------------------------------------------------

-- Add `is_enabled` column.
ALTER TABLE `app_user` ADD COLUMN `is_enabled` tinyint(1) DEFAULT '1';
