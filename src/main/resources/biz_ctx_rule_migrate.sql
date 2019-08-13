SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `biz_ctx_rule`;
CREATE TABLE `biz_ctx_rule` (
  `biz_ctx_rule_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `from_biz_ctx_id` bigint(20) unsigned NOT NULL,
  `top_level_bie_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`biz_ctx_rule_id`),
  KEY `from_biz_ctx_id` (`from_biz_ctx_id`),
  KEY `top_level_bie_id` (`top_level_bie_id`),
  CONSTRAINT `biz_ctx_rule_ibfk_1` FOREIGN KEY (`from_biz_ctx_id`) REFERENCES `biz_ctx` (`biz_ctx_id`),
  CONSTRAINT `biz_ctx_rule_ibfk_2` FOREIGN KEY (`top_level_bie_id`) REFERENCES `top_level_abie` (`top_level_abie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;