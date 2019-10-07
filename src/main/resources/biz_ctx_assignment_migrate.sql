SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `biz_ctx_assignment`;
CREATE TABLE `biz_ctx_assignment` (
  `biz_ctx_assignment_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `biz_ctx_id` bigint(20) unsigned NOT NULL,
  `top_level_abie_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`biz_ctx_assignment_id`),
  UNIQUE KEY `biz_ctx_assignment_uk` (`biz_ctx_id`,`top_level_abie_id`),
  KEY `biz_ctx_id` (`biz_ctx_id`),
  KEY `top_level_abie_id` (`top_level_abie_id`),
  CONSTRAINT `biz_ctx_rule_ibfk_1` FOREIGN KEY (`biz_ctx_id`) REFERENCES `biz_ctx` (`biz_ctx_id`),
  CONSTRAINT `biz_ctx_rule_ibfk_2` FOREIGN KEY (`top_level_abie_id`) REFERENCES `top_level_abie` (`top_level_abie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `abie` MODIFY COLUMN `biz_ctx_id` bigint(20) unsigned DEFAULT NULL COMMENT '(Deprecated) A foreign key to the BIZ_CTX table. This column stores the business context assigned to the ABIE.';

INSERT INTO `biz_ctx_assignment` (`top_level_abie_id`, `biz_ctx_id`)
SELECT `top_level_abie_id`, `abie`.`biz_ctx_id`
FROM `top_level_abie`
         JOIN `abie` ON `top_level_abie`.`abie_id` = `abie`.`abie_id`
WHERE `abie`.`biz_ctx_id` IS NOT NULL;

SET FOREIGN_KEY_CHECKS = 1;