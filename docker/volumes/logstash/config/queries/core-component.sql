SELECT
	'ACC' AS `type`,
	CONCAT(`oagi`.`acc_manifest`.`acc_manifest_id`, '_', `oagi`.`acc`.`guid`) AS 'id',
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
	`oagi`.`module`.`path` AS `module`
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
	'ASCCP' AS `type`,
	CONCAT(`oagi`.`asccp_manifest`.`asccp_manifest_id`, '_', `oagi`.`asccp`.`guid`) AS 'id',
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
	`oagi`.`module`.`path` AS `module`
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
	CONCAT(`oagi`.`bccp_manifest`.`bccp_manifest_id`, '_', `oagi`.`bccp`.`guid`) AS 'id',
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
	`oagi`.`module`.`path` AS `module`
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
	'ASCC' AS `type`,
	CONCAT(`oagi`.`ascc_manifest`.`ascc_manifest_id`, '_', `oagi`.`ascc`.`guid`) AS 'id',
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
	`oagi`.`module`.`path` AS `module`
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
	CONCAT(`oagi`.`bcc_manifest`.`bcc_manifest_id`, '_', `oagi`.`bcc`.`guid`) AS 'id',
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
	`oagi`.`module`.`path` AS `module`
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
	'DT' AS `type`,
	CONCAT(`oagi`.`dt_manifest`.`dt_manifest_id`, '_', `oagi`.`dt`.`guid`) AS 'id',
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
	`oagi`.`dt`.`type` AS `dt_type`,
	`owner`.`login_id` AS `owner`,
	`updater`.`login_id` AS `updater`,
	`oagi`.`module`.`path` AS `module`
FROM `oagi`.`dt_manifest`
JOIN `oagi`.`log` ON `oagi`.`dt_manifest`.`log_id` = `oagi`.`log`.`log_id`
JOIN `oagi`.`release` ON `oagi`.`dt_manifest`.`release_id` = `oagi`.`release`.`release_id`
JOIN `oagi`.`dt` ON `oagi`.`dt_manifest`.`dt_id` = `oagi`.`dt`.`dt_id`
JOIN `oagi`.`app_user` AS `owner` ON `oagi`.`dt`.`owner_user_id` = `owner`.`app_user_id`
JOIN `oagi`.`app_user` AS `updater` ON `oagi`.`dt`.`last_updated_by` = `updater`.`app_user_id`
LEFT JOIN `oagi`.`module_dt_manifest` ON `oagi`.`dt_manifest`.`dt_manifest_id` = `oagi`.`module_dt_manifest`.`dt_manifest_id`
LEFT JOIN `oagi`.`module_set_release` ON `oagi`.`module_dt_manifest`.`module_set_release_id` = `oagi`.`module_set_release`.`module_set_release_id` AND `oagi`.`module_set_release`.`is_default` = 1
LEFT JOIN `oagi`.`module` ON `oagi`.`module_dt_manifest`.`module_id` = `oagi`.`module`.`module_id`
WHERE `oagi`.`dt`.`last_update_timestamp` > ?
