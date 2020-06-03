-- ----------------------------------------------------
-- Migration script for Score v1.4.0                 --
--                                                   --
-- Author: Hakju Oh <hakju.oh@nist.gov>              --
-- ----------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;

-- update incorrectly revised ascc without release_id
UPDATE `ascc`, (
    SELECT `ascc`.* FROM `ascc` JOIN `acc` ON `ascc`.`from_acc_id` = `acc`.`acc_id`
    WHERE `acc`.`oagis_component_type` = 4
      AND `ascc`.`revision_num` > 0
      AND `ascc`.`release_id` IS NULL
) t
SET `ascc`.`release_id` = 1
WHERE `ascc`.`ascc_id` = t.`ascc_id`;

-- update incorrectly revised bcc without release_id
UPDATE `bcc`, (
    SELECT `bcc`.* FROM `bcc` JOIN `acc` ON `bcc`.`from_acc_id` = `acc`.`acc_id`
    WHERE `acc`.`oagis_component_type` = 4
      AND `bcc`.`revision_num` > 0
      AND `bcc`.`release_id` IS NULL
) t
SET `bcc`.`release_id` = 1
WHERE `bcc`.`bcc_id` = t.`bcc_id`;

-- update incorrectly migrated asbie pointed to the acc whose release_id is null
UPDATE `asbie`, (
    SELECT `asbie`.`asbie_id`, `asbie`.`based_ascc_id`, `ascc`.`ascc_id`, `base`.`ascc_id` as base_id
    FROM `asbie` JOIN `ascc` ON `asbie`.`based_ascc_id` = `ascc`.`ascc_id`
    JOIN `ascc` as base ON `ascc`.`ascc_id` = base.`current_ascc_id`
    WHERE `ascc`.`release_id` IS NULL
) t
SET `asbie`.`based_ascc_id` = t.`base_id`
WHERE `asbie`.`asbie_id` = t.`asbie_id`;

-- update incorrectly migrated bbie pointed to the acc whose release_id is null
UPDATE `bbie`, (
    SELECT `bbie`.`bbie_id`, `bbie`.`based_bcc_id`, `bcc`.`bcc_id`, `base`.`bcc_id` as base_id
    FROM `bbie` JOIN `bcc` ON `bbie`.`based_bcc_id` = `bcc`.`bcc_id`
    JOIN `bcc` as base ON `bcc`.`bcc_id` = base.`current_bcc_id`
    WHERE `bcc`.`release_id` IS NULL
) t
SET `bbie`.`based_bcc_id` = t.`base_id`
WHERE `bbie`.`bbie_id` = t.`bbie_id`;

-- delete duplicate asbie
DELETE FROM `asbie` WHERE `asbie_id` IN (
    SELECT id FROM (
        SELECT `asbie`.`asbie_id` as id FROM `asbie`, (
            SELECT max(`asbie_id`) `asbie_id`, `from_abie_id`, `based_ascc_id`
            FROM `asbie` GROUP BY `from_abie_id`, `based_ascc_id` HAVING count(`based_ascc_id`) > 1
    ) t
    WHERE `asbie`.`asbie_id` = t.`asbie_id`
      AND `asbie`.`from_abie_id` = t.`from_abie_id`
      AND `asbie`.`based_ascc_id` = t.`based_ascc_id`
    ) e
);

-- delete duplicate bbie
DELETE FROM `bbie` WHERE bbie_id IN (
    SELECT `id` FROM (
        SELECT `bbie`.`bbie_id` as id FROM `bbie`, (
            SELECT max(`bbie_id`) `bbie_id`, `from_abie_id`, `based_bcc_id`
            FROM `bbie` GROUP BY `from_abie_id`, `based_bcc_id` HAVING count(`based_bcc_id`) > 1
    ) t
    WHERE `bbie`.`bbie_id` = t.`bbie_id`
      AND `bbie`.`from_abie_id` = t.`from_abie_id`
      AND `bbie`.`based_bcc_id` = t.`based_bcc_id`
    ) e
);


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

-- Insert `Date Time` CDT mapping to `xsd:date`.
INSERT INTO `cdt_awd_pri_xps_type_map` (`cdt_awd_pri_id`, `xbt_id`) VALUES (
(SELECT `cdt_awd_pri`.`cdt_awd_pri_id`
 FROM `cdt_awd_pri`
 JOIN `dt` ON `cdt_awd_pri`.`cdt_id` = `dt`.`dt_id`
 JOIN `cdt_pri` ON `cdt_awd_pri`.`cdt_pri_id` = `cdt_pri`.`cdt_pri_id`
 WHERE `dt`.`type` = 0
   AND `dt`.`data_type_term` = 'Date Time'
   AND `cdt_pri`.`name` = 'TimePoint'),
(SELECT `xbt`.`xbt_id`
 FROM `xbt`
 WHERE `xbt`.`name` = 'date'));

-- Insert `Date` BDTs mapping with the record `cdt_awd_pri_xps_type_map` inserted above.
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

-- Insert `Date Time` BDTs mapping with the `cdt_awd_pri_xps_type_map` record inserted above.
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
     AND `dt`.`data_type_term` = 'Date Time'
     AND `cdt_pri`.`name` = 'TimePoint'
     AND `xbt`.`name` = 'date'
) as `cdt_awd_pri_xps_type_map_id`,
0 as `is_default`
FROM `dt`
WHERE `dt`.`type` = 1
  AND `dt`.`data_type_term` = 'Date Time';

SET FOREIGN_KEY_CHECKS = 1;