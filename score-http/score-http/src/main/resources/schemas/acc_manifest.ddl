CREATE TABLE `acc_manifest`
(
    `acc_manifest_id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `release_id`                  bigint(20) unsigned NOT NULL,
    `acc_id`                      bigint(20) unsigned NOT NULL,
    `based_acc_manifest_id`       bigint(20) unsigned          DEFAULT NULL,
    `conflict`                    tinyint(1)          NOT NULL DEFAULT '0' COMMENT 'This indicates that there is a conflict between self and relationship.',
    `log_id`                      bigint(20) unsigned          DEFAULT NULL COMMENT 'A foreign key pointed to a log for the current record.',
    `replacement_acc_manifest_id` bigint(20) unsigned          DEFAULT NULL COMMENT 'This refers to a replacement manifest if the record is deprecated.',
    `prev_acc_manifest_id`        bigint(20) unsigned          DEFAULT NULL,
    `next_acc_manifest_id`        bigint(20) unsigned          DEFAULT NULL,
    PRIMARY KEY (`acc_manifest_id`),
    KEY `acc_manifest_acc_id_fk` (`acc_id`),
    KEY `acc_manifest_based_acc_manifest_id_fk` (`based_acc_manifest_id`),
    KEY `acc_manifest_release_id_fk` (`release_id`),
    KEY `acc_manifest_log_id_fk` (`log_id`),
    KEY `acc_manifest_prev_acc_manifest_id_fk` (`prev_acc_manifest_id`),
    KEY `acc_manifest_next_acc_manifest_id_fk` (`next_acc_manifest_id`),
    KEY `acc_replacement_acc_manifest_id_fk` (`replacement_acc_manifest_id`),
    CONSTRAINT `acc_manifest_acc_id_fk` FOREIGN KEY (`acc_id`) REFERENCES `acc` (`acc_id`),
    CONSTRAINT `acc_manifest_based_acc_manifest_id_fk` FOREIGN KEY (`based_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `acc_manifest_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `log` (`log_id`),
    CONSTRAINT `acc_manifest_next_acc_manifest_id_fk` FOREIGN KEY (`next_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `acc_manifest_prev_acc_manifest_id_fk` FOREIGN KEY (`prev_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`),
    CONSTRAINT `acc_manifest_release_id_fk` FOREIGN KEY (`release_id`) REFERENCES `release` (`release_id`),
    CONSTRAINT `acc_replacement_acc_manifest_id_fk` FOREIGN KEY (`replacement_acc_manifest_id`) REFERENCES `acc_manifest` (`acc_manifest_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;