-- ----------------------------------------------------
-- Migration script for Score v2.0.0                 --
--                                                   --
-- Author: Kwanghoon Lee <kwanghoon.lee@nist.gov>    --
--                                                   --
-- ----------------------------------------------------

set @ascc_manifest_id := (select ascc_manifest.ascc_manifest_id from ascc join ascc_manifest on ascc.ascc_id = ascc_manifest.ascc_id where ascc.den = 'Sales Lead Header Base. Party. Party' and ascc_manifest.release_id = (select release_id from `release` where release_num = '10.7'));
set @prev_seq_key_id := (select seq_key.prev_seq_key_id from seq_key where ascc_manifest_id = @ascc_manifest_id);
update seq_key set next_seq_key_id = null where seq_key_id = @prev_seq_key_id;
update ascc_manifest set seq_key_id = null where ascc_manifest_id = @ascc_manifest_id;
update ascc_manifest set next_ascc_manifest_id = null where next_ascc_manifest_id = @ascc_manifest_id;
update ascc_manifest set prev_ascc_manifest_id = null where prev_ascc_manifest_id = @ascc_manifest_id;
delete from seq_key where ascc_manifest_id = @ascc_manifest_id;
delete from ascc_manifest where ascc_manifest_id = @ascc_manifest_id;

set @ascc_manifest_id := (select ascc_manifest.ascc_manifest_id from ascc join ascc_manifest on ascc.ascc_id = ascc_manifest.ascc_id where ascc.den = 'Sales Lead Header Base. Party. Party' and ascc_manifest.release_id = (select release_id from `release` where release_num = 'Working'));
set @prev_seq_key_id := (select seq_key.prev_seq_key_id from seq_key where ascc_manifest_id = @ascc_manifest_id);
update seq_key set next_seq_key_id = null where seq_key_id = @prev_seq_key_id;
update ascc_manifest set seq_key_id = null where ascc_manifest_id = @ascc_manifest_id;
update ascc_manifest set next_ascc_manifest_id = null where next_ascc_manifest_id = @ascc_manifest_id;
update ascc_manifest set prev_ascc_manifest_id = null where prev_ascc_manifest_id = @ascc_manifest_id;
delete from seq_key where ascc_manifest_id = @ascc_manifest_id;
delete from ascc_manifest where ascc_manifest_id = @ascc_manifest_id;

update `release` set release_num = '10.7.0.1' where release_num = '10.7';