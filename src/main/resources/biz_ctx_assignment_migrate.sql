SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `biz_ctx_assignment`;
CREATE TABLE `biz_ctx_assignment` (
  `biz_ctx_assignment_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `biz_ctx_id` bigint(20) unsigned NOT NULL,
  `top_level_abie_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`biz_ctx_assignment_id`),
  KEY `from_biz_ctx_id` (`biz_ctx_id`),
  KEY `top_level_abie_id` (`top_level_abie_id`),
  CONSTRAINT `biz_ctx_rule_ibfk_1` FOREIGN KEY (`biz_ctx_id`) REFERENCES `biz_ctx` (`biz_ctx_id`),
  CONSTRAINT `biz_ctx_rule_ibfk_2` FOREIGN KEY (`top_level_abie_id`) REFERENCES `top_level_abie` (`top_level_abie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;