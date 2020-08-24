-- ----------------------------------------------------
-- Migration script for Score v1.6.0                 --
--                                                   --
-- Author: Hakju Oh <hakju.oh@nist.gov>              --
-- ----------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `oauth2_app`;
CREATE TABLE `oauth2_app` (
    `oauth2_app_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `provider_name` varchar(100) NOT NULL,
    `issuer_uri` varchar(200) DEFAULT NULL,
    `authorization_uri` varchar(200) DEFAULT NULL,
    `token_uri` varchar(200) DEFAULT NULL,
    `user_info_uri` varchar(200) DEFAULT NULL,
    `jwk_set_uri` varchar(200) DEFAULT NULL,
    `redirect_uri` varchar(200) NOT NULL,
    `client_id` varchar(200) NOT NULL,
    `client_secret` varchar(200) NOT NULL,
    `client_authentication_method` varchar(50) NOT NULL,
    `authorization_grant_type` varchar(50) NOT NULL,
    `prompt` varchar(20) DEFAULT NULL,
    `display_provider_name` varchar(100) DEFAULT NULL,
    `background_color` varchar(50) DEFAULT NULL,
    `font_color` varchar(50) DEFAULT NULL,
    `display_order` int(11) DEFAULT '0',
    `is_disabled` tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`oauth2_app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `oauth2_app_scope`;
CREATE TABLE `oauth2_app_scope` (
    `oauth2_app_scope_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `oauth2_app_id` bigint(20) unsigned NOT NULL,
    `scope` varchar(100) NOT NULL,
    PRIMARY KEY (`oauth2_app_scope_id`),
    KEY `oauth2_app_scope_oauth2_app_id_fk` (`oauth2_app_id`),
    CONSTRAINT `oauth2_app_scope_oauth2_app_id_fk` FOREIGN KEY (`oauth2_app_id`) REFERENCES `oauth2_app` (`oauth2_app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `app_oauth2_user`;
CREATE TABLE `app_oauth2_user` (
  `app_oauth2_user_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Primary key.',
  `app_user_id` bigint(20) unsigned DEFAULT NULL COMMENT 'A reference to the record in `app_user`. If it is not set, this is treated as a pending record.',
  `oauth2_app_id` bigint(20) unsigned NOT NULL COMMENT 'A reference to the record in `oauth2_app`.',
  `sub` varchar(100) NOT NULL COMMENT '`sub` claim defined in OIDC spec. This is a unique identifier of the subject in the provider.',
  `name` varchar(200) DEFAULT NULL COMMENT '`name` claim defined in OIDC spec.',
  `email` varchar(200) DEFAULT NULL COMMENT '`email` claim defined in OIDC spec.',
  `nickname` varchar(200) DEFAULT NULL COMMENT '`nickname` claim defined in OIDC spec.',
  `preferred_username` varchar(200) DEFAULT NULL COMMENT '`preferred_username` claim defined in OIDC spec.',
  `phone_number` varchar(200) DEFAULT NULL COMMENT '`phone_number` claim defined in OIDC spec.',
  `creation_timestamp` datetime(6) NOT NULL COMMENT 'Timestamp when this record is created.',
  PRIMARY KEY (`app_oauth2_user_id`),
  UNIQUE KEY `app_oauth2_user_uk1` (`oauth2_app_id`,`sub`),
  KEY `app_oauth2_user_app_user_id_fk` (`app_user_id`),
  CONSTRAINT `app_oauth2_user_app_user_id_fk` FOREIGN KEY (`app_user_id`) REFERENCES `app_user` (`app_user_id`),
  CONSTRAINT `app_oauth2_user_oauth2_app_id_fk` FOREIGN KEY (`oauth2_app_id`) REFERENCES `oauth2_app` (`oauth2_app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `app_user` MODIFY COLUMN `password` varchar(100) DEFAULT NULL COMMENT 'Password to authenticate the user.';

SET FOREIGN_KEY_CHECKS = 1;