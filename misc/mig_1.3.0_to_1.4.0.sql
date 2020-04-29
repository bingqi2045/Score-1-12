-- ----------------------------------------------------
-- Migration script for Score v1.4.0                 --
--                                                   --
-- Author: Hakju Oh <hakju.oh@nist.gov>              --
-- ----------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;

-- Insert `Date` CDT mapping to `xsd:date`.
INSERT INTO `cdt_awd_pri_xps_type_map` (`cdt_awd_pri_id`, `xbt_id`) VALUES (
(SELECT `cdt_awd_pri`.`cdt_awd_pri_id`
 FROM `cdt_awd_pri`
 JOIN `dt` ON `cdt_awd_pri`.`cdt_id` = `dt`.`dt_id`
 JOIN `cdt_pri` ON `cdt_awd_pri`.`cdt_pri_id` = `cdt_pri`.`cdt_pri_id`
 WHERE `dt`.`type` = 0
   AND `dt`.`data_type_term` = 'Date'
   AND `cdt_pri`.`name` = 'TimePoint'),
(SELECT `xbt`.`xbt_id`
 FROM `xbt`
 WHERE `xbt`.`name` = 'date'));

-- Insert BDT mapping with the record inserted above.
INSERT INTO `bdt_pri_restri` (`bdt_id`, `cdt_awd_pri_xps_type_map_id`, `is_default`)
SELECT `dt`.`dt_id`,
(
   SELECT `cdt_awd_pri_xps_type_map`.`cdt_awd_pri_xps_type_map_id`
   FROM `cdt_awd_pri_xps_type_map`
            JOIN `cdt_awd_pri` ON `cdt_awd_pri_xps_type_map`.`cdt_awd_pri_id` = `cdt_awd_pri`.`cdt_awd_pri_id`
            JOIN `xbt` ON `cdt_awd_pri_xps_type_map`.`xbt_id` = `xbt`.`xbt_id`
            JOIN `dt` ON `cdt_awd_pri`.`cdt_id` = `dt`.`dt_id`
            JOIN `cdt_pri` ON `cdt_awd_pri`.`cdt_pri_id` = `cdt_pri`.`cdt_pri_id`
   WHERE `dt`.`type` = 0
     AND `dt`.`data_type_term` = 'Date'
     AND `cdt_pri`.`name` = 'TimePoint'
     AND `xbt`.`name` = 'date'
) as `cdt_awd_pri_xps_type_map_id`,
0 as `is_default`
FROM `dt`
WHERE `dt`.`type` = 1
  AND `dt`.`data_type_term` = 'Date';

SET FOREIGN_KEY_CHECKS = 1;