ALTER TABLE `MDIY_MODEL` ADD COLUMN `NOT_DEL` INT(1) NULL DEFAULT 0 COMMENT '1为不能删除，主要用于系统默认数据,0为一般数据，主要是前端控制';
ALTER TABLE `MDIY_TAG` ADD COLUMN `NOT_DEL` INT(1) NULL DEFAULT 0 COMMENT '1为不能删除，主要用于系统默认数据,0为一般数据，主要是前端控制';
UPDATE  MDIY_TAG SET NOT_DEL=1;
ALTER TABLE `MDIY_CONFIG` ADD COLUMN `NOT_DEL` INT(1) NULL DEFAULT 0 COMMENT '1为不能删除，主要用于系统默认数据,0为一般数据，主要是前端控制';
UPDATE  MDIY_CONFIG SET NOT_DEL=1;
ALTER TABLE `ROLE` DROP COLUMN `MANAGER_ID`;
ALTER TABLE  `MANAGER` DROP FOREIGN KEY `FK_ROLE_ID`;
ALTER TABLE `MANAGER` CHANGE COLUMN `ROLE_ID` `ROLE_IDS` VARCHAR(11) NULL DEFAULT NULL COMMENT '角色编号'  ;
ALTER TABLE `MANAGER` ADD COLUMN `MANAGER_LOCK` VARCHAR(10) NULL DEFAULT 0 COMMENT '锁定状态';
ALTER TABLE `ROLE` ADD COLUMN `NOT_DEL` INT(1) NULL DEFAULT 0 COMMENT '1为不能删除，主要用于系统默认数据,0为一般数据，主要是前端控制';
ALTER TABLE `MDIY_DICT` ADD COLUMN `NOT_DEL` INT(1) NULL DEFAULT 0 COMMENT '1为不能删除，主要用于系统默认数据,0为一般数据，主要是前端控制';
UPDATE  MDIY_DICT SET NOT_DEL=1;

