SELECT
	'ACC' AS `type`,
	CONCAT('ACC_', `oagi`.`acc_manifest`.`acc_manifest_id`) AS 'id',
	`oagi`.`acc_manifest`.`acc_manifest_id` AS `manifest_id`,
	`oagi`.`log`.`revision_num`,
	`oagi`.`release`.`release_id`,
	`oagi`.`release`.`release_num`,
	`oagi`.`acc`.`guid`, 
	`oagi`.`acc`.`acc_id` AS `component_id`, 
	`oagi`.`acc`.`object_class_term` AS `name`, 
	`oagi`.`acc`.`den`, 
	`oagi`.`acc`.`definition`, 
	`oagi`.`acc`.`definition_source`,
	CASE `oagi`.`acc`.`is_deprecated`
	WHEN 0 THEN 'false'
	WHEN 1 THEN 'true'
	END AS `deprecated`,
	CASE `oagi`.`owner`.`is_developer`
	WHEN 0 THEN 'false'
	WHEN 1 THEN 'true'
	END AS `owned_by_developer`,
	`oagi`.`acc`.`state`,
	`oagi`.`acc`.`last_update_timestamp`,
	`oagi`.`acc`.`oagis_component_type`,
	NULL AS `asccp_type`,
	NULL AS `dt_type`,
	`owner`.`login_id` AS `owner`,
	`updater`.`login_id` AS `updater`,
	`oagi`.`module`.`path` AS `module`,
    NULL AS `six_digit_id`,
    NULL AS `default_value_domain`
FROM `oagi`.`acc_manifest`
JOIN `oagi`.`log` ON `oagi`.`acc_manifest`.`log_id` = `oagi`.`log`.`log_id`
JOIN `oagi`.`release` ON `oagi`.`acc_manifest`.`release_id` = `oagi`.`release`.`release_id`
JOIN `oagi`.`acc` ON `oagi`.`acc_manifest`.`acc_id` = `oagi`.`acc`.`acc_id`
JOIN `oagi`.`app_user` AS `owner` ON `oagi`.`acc`.`owner_user_id` = `owner`.`app_user_id`
JOIN `oagi`.`app_user` AS `updater` ON `oagi`.`acc`.`last_updated_by` = `updater`.`app_user_id`
LEFT JOIN `oagi`.`module_acc_manifest` ON `oagi`.`acc_manifest`.`acc_manifest_id` = `oagi`.`module_acc_manifest`.`acc_manifest_id`
LEFT JOIN `oagi`.`module_set_release` ON `oagi`.`module_acc_manifest`.`module_set_release_id` = `oagi`.`module_set_release`.`module_set_release_id` AND `oagi`.`module_set_release`.`is_default` = 1
LEFT JOIN `oagi`.`module` ON `oagi`.`module_acc_manifest`.`module_id` = `oagi`.`module`.`module_id`
WHERE `oagi`.`acc`.`last_update_timestamp` > ?
UNION
SELECT
	'ASCC' AS `type`,
	CONCAT('ASCC_', `oagi`.`ascc_manifest`.`ascc_manifest_id`) AS 'id',
	`oagi`.`ascc_manifest`.`ascc_manifest_id` AS `manifest_id`,
	`oagi`.`log`.`revision_num`,
	`oagi`.`release`.`release_id`,
	`oagi`.`release`.`release_num`,
	`oagi`.`ascc`.`guid`, 
	`oagi`.`ascc`.`ascc_id` AS `component_id`, 
	`oagi`.`ascc`.`den` AS `name`, 
	`oagi`.`ascc`.`den`, 
	`oagi`.`ascc`.`definition`, 
	`oagi`.`ascc`.`definition_source`,
	CASE `oagi`.`ascc`.`is_deprecated`
	WHEN 0 THEN 'false'
	WHEN 1 THEN 'true'
	END AS `deprecated`,
	CASE `oagi`.`owner`.`is_developer`
	WHEN 0 THEN 'false'
	WHEN 1 THEN 'true'
	END AS `owned_by_developer`,	
	`oagi`.`ascc`.`state`,
	`oagi`.`ascc`.`last_update_timestamp`, 
	NULL AS `oagis_component_type`,
	NULL AS `asccp_type`,
	NULL AS `dt_type`,
	`owner`.`login_id` AS `owner`,
	`updater`.`login_id` AS `updater`,
	`oagi`.`module`.`path` AS `module`,
    NULL AS `six_digit_id`,
    NULL AS `default_value_domain`
FROM `oagi`.`ascc_manifest`
JOIN `oagi`.`acc_manifest` ON `oagi`.`ascc_manifest`.`from_acc_manifest_id` = `oagi`.`acc_manifest`.`acc_manifest_id`
JOIN `oagi`.`log` ON `oagi`.`acc_manifest`.`log_id` = `oagi`.`log`.`log_id`
JOIN `oagi`.`release` ON `oagi`.`acc_manifest`.`release_id` = `oagi`.`release`.`release_id`
JOIN `oagi`.`ascc` ON `oagi`.`ascc_manifest`.`ascc_id` = `oagi`.`ascc`.`ascc_id`
JOIN `oagi`.`app_user` AS `owner` ON `oagi`.`ascc`.`owner_user_id` = `owner`.`app_user_id`
JOIN `oagi`.`app_user` AS `updater` ON `oagi`.`ascc`.`last_updated_by` = `updater`.`app_user_id`
LEFT JOIN `oagi`.`module_acc_manifest` ON `oagi`.`acc_manifest`.`acc_manifest_id` = `oagi`.`module_acc_manifest`.`acc_manifest_id`
LEFT JOIN `oagi`.`module_set_release` ON `oagi`.`module_acc_manifest`.`module_set_release_id` = `oagi`.`module_set_release`.`module_set_release_id` AND `oagi`.`module_set_release`.`is_default` = 1
LEFT JOIN `oagi`.`module` ON `oagi`.`module_acc_manifest`.`module_id` = `oagi`.`module`.`module_id`
WHERE `oagi`.`ascc`.`last_update_timestamp` > ?
UNION
SELECT
	'BCC' AS `type`,
	CONCAT('BCC_', `oagi`.`bcc_manifest`.`bcc_manifest_id`) AS 'id',
	`oagi`.`bcc_manifest`.`bcc_manifest_id` AS `manifest_id`,
	`oagi`.`log`.`revision_num`,
	`oagi`.`release`.`release_id`,
	`oagi`.`release`.`release_num`,
	`oagi`.`bcc`.`guid`, 
	`oagi`.`bcc`.`bcc_id` AS `component_id`, 
	`oagi`.`bcc`.`den` AS `name`, 
	`oagi`.`bcc`.`den`, 
	`oagi`.`bcc`.`definition`, 
	`oagi`.`bcc`.`definition_source`,
	CASE `oagi`.`bcc`.`is_deprecated`
	WHEN 0 THEN 'false'
	WHEN 1 THEN 'true'
	END AS `deprecated`,
	CASE `oagi`.`owner`.`is_developer`
	WHEN 0 THEN 'false'
	WHEN 1 THEN 'true'
	END AS `owned_by_developer`,	
	`oagi`.`bcc`.`state`,
	`oagi`.`bcc`.`last_update_timestamp`,
	NULL AS `oagis_component_type`,
	NULL AS `asccp_type`,
	NULL AS `dt_type`,
	`owner`.`login_id` AS `owner`,
	`updater`.`login_id` AS `updater`,
	`oagi`.`module`.`path` AS `module`,
    NULL AS `six_digit_id`,
    NULL AS `default_value_domain`
FROM `oagi`.`bcc_manifest`
JOIN `oagi`.`acc_manifest` ON `oagi`.`bcc_manifest`.`from_acc_manifest_id` = `oagi`.`acc_manifest`.`acc_manifest_id`
JOIN `oagi`.`log` ON `oagi`.`acc_manifest`.`log_id` = `oagi`.`log`.`log_id`
JOIN `oagi`.`release` ON `oagi`.`acc_manifest`.`release_id` = `oagi`.`release`.`release_id`
JOIN `oagi`.`bcc` ON `oagi`.`bcc_manifest`.`bcc_id` = `oagi`.`bcc`.`bcc_id`
JOIN `oagi`.`app_user` AS `owner` ON `oagi`.`bcc`.`owner_user_id` = `owner`.`app_user_id`
JOIN `oagi`.`app_user` AS `updater` ON `oagi`.`bcc`.`last_updated_by` = `updater`.`app_user_id`
LEFT JOIN `oagi`.`module_acc_manifest` ON `oagi`.`acc_manifest`.`acc_manifest_id` = `oagi`.`module_acc_manifest`.`acc_manifest_id`
LEFT JOIN `oagi`.`module_set_release` ON `oagi`.`module_acc_manifest`.`module_set_release_id` = `oagi`.`module_set_release`.`module_set_release_id` AND `oagi`.`module_set_release`.`is_default` = 1
LEFT JOIN `oagi`.`module` ON `oagi`.`module_acc_manifest`.`module_id` = `oagi`.`module`.`module_id`
WHERE `oagi`.`bcc`.`last_update_timestamp` > ?
UNION
SELECT
    'ASCCP' AS `type`,
    CONCAT('ASCCP_', `oagi`.`asccp_manifest`.`asccp_manifest_id`) AS 'id',
    `oagi`.`asccp_manifest`.`asccp_manifest_id` AS `manifest_id`,
    `oagi`.`log`.`revision_num`,
    `oagi`.`release`.`release_id`,
    `oagi`.`release`.`release_num`,
    `oagi`.`asccp`.`guid`,
    `oagi`.`asccp`.`asccp_id` AS `component_id`,
    `oagi`.`asccp`.`property_term` AS `name`,
    `oagi`.`asccp`.`den`,
    `oagi`.`asccp`.`definition`,
    `oagi`.`asccp`.`definition_source`,
    CASE `oagi`.`asccp`.`is_deprecated`
        WHEN 0 THEN 'false'
        WHEN 1 THEN 'true'
        END AS `deprecated`,
    CASE `oagi`.`owner`.`is_developer`
        WHEN 0 THEN 'false'
        WHEN 1 THEN 'true'
        END AS `owned_by_developer`,
    `oagi`.`asccp`.`state`,
    `oagi`.`asccp`.`last_update_timestamp`,
    NULL AS `oagis_component_type`,
    `oagi`.`asccp`.`type` as `asccp_type`,
    NULL AS `dt_type`,
    `owner`.`login_id` AS `owner`,
    `updater`.`login_id` AS `updater`,
    `oagi`.`module`.`path` AS `module`,
    NULL AS `six_digit_id`,
    NULL AS `default_value_domain`
FROM `oagi`.`asccp_manifest`
         JOIN `oagi`.`log` ON `oagi`.`asccp_manifest`.`log_id` = `oagi`.`log`.`log_id`
         JOIN `oagi`.`release` ON `oagi`.`asccp_manifest`.`release_id` = `oagi`.`release`.`release_id`
         JOIN `oagi`.`asccp` ON `oagi`.`asccp_manifest`.`asccp_id` = `oagi`.`asccp`.`asccp_id`
         JOIN `oagi`.`app_user` AS `owner` ON `oagi`.`asccp`.`owner_user_id` = `owner`.`app_user_id`
         JOIN `oagi`.`app_user` AS `updater` ON `oagi`.`asccp`.`last_updated_by` = `updater`.`app_user_id`
         LEFT JOIN `oagi`.`module_asccp_manifest` ON `oagi`.`asccp_manifest`.`asccp_manifest_id` = `oagi`.`module_asccp_manifest`.`asccp_manifest_id`
         LEFT JOIN `oagi`.`module_set_release` ON `oagi`.`module_asccp_manifest`.`module_set_release_id` = `oagi`.`module_set_release`.`module_set_release_id` AND `oagi`.`module_set_release`.`is_default` = 1
         LEFT JOIN `oagi`.`module` ON `oagi`.`module_asccp_manifest`.`module_id` = `oagi`.`module`.`module_id`
WHERE `oagi`.`asccp`.`last_update_timestamp` > ?
UNION
SELECT
    'BCCP' AS `type`,
    CONCAT('BCCP_', `oagi`.`bccp_manifest`.`bccp_manifest_id`) AS 'id',
    `oagi`.`bccp_manifest`.`bccp_manifest_id` AS `manifest_id`,
    `oagi`.`log`.`revision_num`,
    `oagi`.`release`.`release_id`,
    `oagi`.`release`.`release_num`,
    `oagi`.`bccp`.`guid`,
    `oagi`.`bccp`.`bccp_id` AS `component_id`,
    `oagi`.`bccp`.`property_term` AS `name`,
    `oagi`.`bccp`.`den`,
    `oagi`.`bccp`.`definition`,
    `oagi`.`bccp`.`definition_source`,
    CASE `oagi`.`bccp`.`is_deprecated`
        WHEN 0 THEN 'false'
        WHEN 1 THEN 'true'
        END AS `deprecated`,
    CASE `oagi`.`owner`.`is_developer`
        WHEN 0 THEN 'false'
        WHEN 1 THEN 'true'
        END AS `owned_by_developer`,
    `oagi`.`bccp`.`state`,
    `oagi`.`bccp`.`last_update_timestamp`,
    NULL AS `oagis_component_type`,
    NULL AS `asccp_type`,
    NULL AS `dt_type`,
    `owner`.`login_id` AS `owner`,
    `updater`.`login_id` AS `updater`,
    `oagi`.`module`.`path` AS `module`,
    NULL AS `six_digit_id`,
    NULL AS `default_value_domain`
FROM `oagi`.`bccp_manifest`
         JOIN `oagi`.`log` ON `oagi`.`bccp_manifest`.`log_id` = `oagi`.`log`.`log_id`
         JOIN `oagi`.`release` ON `oagi`.`bccp_manifest`.`release_id` = `oagi`.`release`.`release_id`
         JOIN `oagi`.`bccp` ON `oagi`.`bccp_manifest`.`bccp_id` = `oagi`.`bccp`.`bccp_id`
         JOIN `oagi`.`app_user` AS `owner` ON `oagi`.`bccp`.`owner_user_id` = `owner`.`app_user_id`
         JOIN `oagi`.`app_user` AS `updater` ON `oagi`.`bccp`.`last_updated_by` = `updater`.`app_user_id`
         LEFT JOIN `oagi`.`module_bccp_manifest` ON `oagi`.`bccp_manifest`.`bccp_manifest_id` = `oagi`.`module_bccp_manifest`.`bccp_manifest_id`
         LEFT JOIN `oagi`.`module_set_release` ON `oagi`.`module_bccp_manifest`.`module_set_release_id` = `oagi`.`module_set_release`.`module_set_release_id` AND `oagi`.`module_set_release`.`is_default` = 1
         LEFT JOIN `oagi`.`module` ON `oagi`.`module_bccp_manifest`.`module_id` = `oagi`.`module`.`module_id`
WHERE `oagi`.`bccp`.`last_update_timestamp` > ?
UNION
SELECT
    'DT' AS `type`,
    CONCAT('DT_', `oagi`.`dt_manifest`.`dt_manifest_id`) AS 'id',
    `oagi`.`dt_manifest`.`dt_manifest_id` AS `manifest_id`,
    `oagi`.`log`.`revision_num`,
    `oagi`.`release`.`release_id`,
    `oagi`.`release`.`release_num`,
    `oagi`.`dt`.`guid`,
    `oagi`.`dt`.`dt_id` AS `component_id`,
    `oagi`.`dt`.`data_type_term` AS `name`,
    `oagi`.`dt`.`den`,
    `oagi`.`dt`.`definition`,
    `oagi`.`dt`.`definition_source`,
    CASE `oagi`.`dt`.`is_deprecated`
        WHEN 0 THEN 'false'
        WHEN 1 THEN 'true'
        END AS `deprecated`,
    CASE `oagi`.`owner`.`is_developer`
        WHEN 0 THEN 'false'
        WHEN 1 THEN 'true'
        END AS `owned_by_developer`,
    `oagi`.`dt`.`state`,
    `oagi`.`dt`.`last_update_timestamp`,
    NULL AS `oagis_component_type`,
    NULL AS `asccp_type`,
    IF(`oagi`.`dt_manifest`.`based_dt_manifest_id` IS NULL, 'CDT', 'BDT') AS `dt_type`,
    `owner`.`login_id` AS `owner`,
    `updater`.`login_id` AS `updater`,
    `oagi`.`module`.`path` AS `module`,
    `oagi`.`dt`.`six_digit_id`,
    concat(ifnull(`oagi`.`cdt_pri`.`name`, ''), ifnull(`oagi`.`code_list`.`name`, ''),
           ifnull(`oagi`.`agency_id_list`.`name`, ''), ifnull(`pri_for_cdt`.`name`, '')) as `default_value_domain`
FROM `oagi`.`dt_manifest`
         JOIN `oagi`.`log` ON `oagi`.`dt_manifest`.`log_id` = `oagi`.`log`.`log_id`
         JOIN `oagi`.`release` ON `oagi`.`dt_manifest`.`release_id` = `oagi`.`release`.`release_id`
         JOIN `oagi`.`dt` ON `oagi`.`dt_manifest`.`dt_id` = `oagi`.`dt`.`dt_id`
         JOIN `oagi`.`app_user` AS `owner` ON `oagi`.`dt`.`owner_user_id` = `owner`.`app_user_id`
         JOIN `oagi`.`app_user` AS `updater` ON `oagi`.`dt`.`last_updated_by` = `updater`.`app_user_id`
         LEFT JOIN `oagi`.`module_dt_manifest` ON `oagi`.`dt_manifest`.`dt_manifest_id` = `oagi`.`module_dt_manifest`.`dt_manifest_id`
         LEFT JOIN `oagi`.`module_set_release` ON `oagi`.`module_dt_manifest`.`module_set_release_id` = `oagi`.`module_set_release`.`module_set_release_id` AND `oagi`.`module_set_release`.`is_default` = 1
         LEFT JOIN `oagi`.`module` ON `oagi`.`module_dt_manifest`.`module_id` = `oagi`.`module`.`module_id`
         LEFT JOIN `oagi`.`bdt_pri_restri` ON (`oagi`.`dt`.`dt_id` = `oagi`.`bdt_pri_restri`.`bdt_id` and
                                               `oagi`.`bdt_pri_restri`.`is_default` = 1)
         LEFT JOIN `oagi`.`code_list`
                   ON `oagi`.`bdt_pri_restri`.`code_list_id` = `oagi`.`code_list`.`code_list_id`
         LEFT JOIN `oagi`.`agency_id_list`
                   ON `oagi`.`bdt_pri_restri`.`agency_id_list_id` = `oagi`.`agency_id_list`.`agency_id_list_id`
         LEFT JOIN `oagi`.`cdt_awd_pri_xps_type_map` ON `oagi`.`bdt_pri_restri`.`cdt_awd_pri_xps_type_map_id` =
                                                        `oagi`.`cdt_awd_pri_xps_type_map`.`cdt_awd_pri_xps_type_map_id`
         LEFT JOIN `oagi`.`cdt_awd_pri`
                   ON `oagi`.`cdt_awd_pri_xps_type_map`.`cdt_awd_pri_id` = `oagi`.`cdt_awd_pri`.`cdt_awd_pri_id`
         LEFT JOIN `oagi`.`cdt_pri` ON `oagi`.`cdt_awd_pri`.`cdt_pri_id` = `oagi`.`cdt_pri`.`cdt_pri_id`
         LEFT JOIN `oagi`.`cdt_awd_pri` as `awd_pri_for_cdt`
                   ON (`oagi`.`dt`.`dt_id` = `awd_pri_for_cdt`.`cdt_id` and `awd_pri_for_cdt`.`is_default` = 1)
         LEFT JOIN `oagi`.`cdt_pri` as `pri_for_cdt`
                   ON `awd_pri_for_cdt`.`cdt_pri_id` = `pri_for_cdt`.`cdt_pri_id`
WHERE `oagi`.`dt`.`last_update_timestamp` > ?