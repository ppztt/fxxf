ALTER TABLE `model` ADD COLUMN `is_child` varchar(255) COMMENT '扩展业务标记' AFTER `model_parent_ids`;
ALTER TABLE `mdiy_dict` ADD COLUMN `is_child` varchar(255) COMMENT '扩展业务标记' AFTER `dict_description`;
ALTER TABLE `role` MODIFY COLUMN `app_id` int(11) COMMENT '应用编号' AFTER `role_managerid`;
ALTER TABLE `mdiy_dict` MODIFY COLUMN `dict_value` varchar(100) COMMENT '数据值' AFTER `app_id`;

UPDATE `cms_article` SET `article_url` = '/59/69\\70.html' WHERE `article_basicid` = 70;
UPDATE `cms_article` SET `article_url` = '/59/68\\71.html' WHERE `article_basicid` = 71;
UPDATE `cms_article` SET `article_url` = '/59/68\\72.html' WHERE `article_basicid` = 72;
UPDATE `cms_article` SET `article_url` = '/59/68\\73.html' WHERE `article_basicid` = 73;
UPDATE `cms_article` SET `article_url` = '/59/68\\74.html' WHERE `article_basicid` = 74;

DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文件编号',
  `category_id` int(11) DEFAULT NULL COMMENT '文件分类编号',
  `app_id` int(11) DEFAULT NULL COMMENT 'APP编号',
  `file_name` varchar(200) DEFAULT NULL COMMENT '文件名称',
  `file_url` varchar(500) DEFAULT NULL COMMENT '文件链接',
  `file_size` int(11) DEFAULT NULL COMMENT '文件大小',
  `file_json` varchar(500) DEFAULT NULL COMMENT '文件详情Json数据',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型：图片、音频、视频等',
  `is_child` varchar(50) DEFAULT NULL COMMENT '子业务',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` int(11) DEFAULT NULL COMMENT '更新者',
  `create_by` int(11) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `del` int(1) DEFAULT NULL COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='基础文件表';

DROP TABLE IF EXISTS `mdiy_tag`;
CREATE TABLE `mdiy_tag`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '标签名称',
  `tag_type` int(1) DEFAULT NULL COMMENT '标签类型',
  `tag_description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mdiy_tag
-- ----------------------------
INSERT INTO `mdiy_tag` VALUES (3, 'arclist', 3, '文章列表');
INSERT INTO `mdiy_tag` VALUES (4, 'channel', 3, '通用栏目');
INSERT INTO `mdiy_tag` VALUES (5, 'global', 2, '全局');
INSERT INTO `mdiy_tag` VALUES (7, 'field', 3, '文章内容');
INSERT INTO `mdiy_tag` VALUES (8, 'pre', 0, '文章上一篇');
INSERT INTO `mdiy_tag` VALUES (9, 'page', 2, '通用分页');
INSERT INTO `mdiy_tag` VALUES (10, 'next', 0, '文章下一篇');
INSERT INTO `mdiy_tag` VALUES (12, 'prclist', 3, '商品列表');
INSERT INTO `mdiy_tag` VALUES (13, 'goods', 3, '商品详情');

-- ----------------------------
-- Table structure for mdiy_tag_sql
-- ----------------------------
DROP TABLE IF EXISTS `mdiy_tag_sql`;
CREATE TABLE `mdiy_tag_sql`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_id` int(11) NOT NULL COMMENT '自定义标签编号',
  `tag_sql` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '自定义sql支持ftl写法',
  `sort` int(11) DEFAULT NULL COMMENT '排序升序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_mdiy_tag_id`(`tag_id`) USING BTREE,
  CONSTRAINT `mdiy_tag_sql_ibfk_1` FOREIGN KEY (`tag_id`) REFERENCES `mdiy_tag` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标签对应多个sql语句' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mdiy_tag_sql
-- ----------------------------
INSERT INTO `mdiy_tag_sql` VALUES (5, 3, 'SELECT\r\n	basic_id AS id,\r\n	LEFT (basic_title, ${titlelen ?default(40)}) AS title,\r\n	basic_title AS fulltitle,\r\n	article_author AS author,\r\n	article_source AS source,\r\n	article_content AS content,\r\n	category.category_title AS typename,\r\n	category.category_id AS typeid,\r\n	<#--列表页动态链接-->\r\n	<#if isDo?? && isDo>\r\n	CONCAT(\"/${modelName}/list.do?typeid=\", category.category_id) as typelink,\r\n	<#else>\r\n	(SELECT \"index.html\") AS typelink,\r\n	</#if>\r\n	basic.basic_thumbnails AS litpic,\r\n	<#--内容页动态链接-->\r\n	<#if isDo?? && isDo>\r\n	CONCAT(\"/mcms/view.do?id=\", basic_id) as link,\r\n	<#else>\r\n	cms_article.article_url AS link,\r\n	</#if>\r\n	basic_datetime AS date,<#if tableNname??>${tableNname}.*,</#if>\r\n	basic_description AS descrip,\r\n	basic_hit AS hit,\r\n	article_type AS flag,\r\n	category_title AS typetitle,\r\n	cms_article.article_keyword AS keyword \r\nFROM\r\n	basic\r\n	LEFT JOIN cms_article ON cms_article.article_basicid = basic.basic_id\r\n	LEFT JOIN category ON basic_categoryid = category.category_id\r\n	LEFT JOIN basic_column ON basic_column.column_category_id = basic.basic_categoryid \r\n	<#--判断是否有自定义模型表-->\r\n	<#if tableNname??>LEFT JOIN ${tableNname} ON ${tableNname}.basicId=cms_article.article_basicid </#if>\r\nWHERE  <#--查询栏目-->\r\n	1 = 1 <#if typeid??> and (basic_categoryid=${typeid} or basic_categoryid in \r\n	(select category_id FROM category where category.del=0 and find_in_set(${typeid},CATEGORY_PARENT_ID)))</#if>\r\n	<#--标题-->\r\n	<#if basic_title??> and basic_title like CONCAT(\"%\",\'${basic_title}\',\"%\")</#if>\r\n	<#--作者-->\r\n	<#if article_author??> and article_author like CONCAT(\"%\",\'${article_author}\',\"%\")</#if>\r\n	<#--来源-->\r\n	<#if article_source??> and article_source like CONCAT(\"%\",\'${article_source}\',\"%\")</#if>\r\n	<#--属性-->\r\n	<#if article_type??> and article_type like CONCAT(\"%\",\'${article_type}\',\"%\")</#if>\r\n	<#--图片-->\r\n	<#if basic_thumbnails??> and basic_thumbnails like CONCAT(\"%\",\'${basic_thumbnails}\',\"%\")</#if>\r\n	<#--描述-->\r\n	<#if basic_description??> and basic_description like CONCAT(\"%\",\'${basic_description}\',\"%\")</#if>\r\n	<#--关键字-->\r\n	<#if article_keyword??> and article_keyword like CONCAT(\"%\",\'${article_keyword}\',\"%\")</#if>\r\n	<#--内容-->\r\n	<#if article_content??> and article_content like CONCAT(\"%\",\'${article_content}\',\"%\")</#if>\r\n	<#--自定义顺序-->\r\n	<#if article_freeorder??> and article_freeorder=${article_freeorder}</#if>\r\n	<#--文章属性-->\r\n	<#if flag?? >\r\n			and cms_article.article_type like CONCAT(\'%\',\'${flag}\',\'%\') \r\n	</#if>\r\n	<#if noflag?? >\r\n			and cms_article.article_type not like CONCAT(\'%\',\'${noflag}\',\'%\') \r\n	</#if>\r\n	<#--字段排序-->\r\n	<#if orderby?? >\r\n		ORDER BY \r\n			<#if orderby==\"date\"> basic.basic_datetime</#if> \r\n			<#if orderby==\"hit\"> basic.basic_hit</#if>\r\n			<#if orderby==\"sort\"> basic.basic_sort</#if>\r\n	</#if>\r\n	<#if order?? >\r\n			<#if order==\"desc\"> desc</#if>\r\n			<#if order==\"asc\"> asc</#if>\r\n	</#if>\r\n	 LIMIT \r\n	<#--判断是否分页-->\r\n	<#if ispaging?? && pageNo??>${(pageNo?eval-1)*size?eval},${size?default(20)}\r\n	<#else>${size?default(20)}</#if>', 1);
INSERT INTO `mdiy_tag_sql` VALUES (6, 4, 'select category_id as id,category_id as typeid,category_title as typetitle,\r\n<#--动态链接-->\r\n	<#if isDo?? && isDo>\r\n	CONCAT(\"/${modelName}/list.do?typeid=\", category_id) as typelink,\r\n	<#else>\r\nCONCAT(column_path,\"/index.html\") as typelink,\r\n	</#if>column_keyword as typekeyword,column_descrip as typedescrip,category_smallimg as typelitpic  from category  \r\nLEFT JOIN basic_column bc on bc.column_category_id=category.category_id where \r\n<#if type?has_content>\r\n	<#--顶级栏目-->\r\n	<#if type==\"top\">\r\n		<#if typeid??>\r\n			category_categoryid=(select category_categoryid from category where category_id=(select category_categoryid from category  where category_id=${typeid})) 	\r\n		<#else>\r\n			category_categoryid=0\r\n		</#if>\r\n	<#--同级栏目-->\r\n	<#elseif type==\"level\">\r\n		<#if typeid??>\r\n		category_categoryid=(select category_categoryid from category where category_id=${typeid})\r\n		<#else>\r\n		 1=1\r\n		</#if>\r\n  <#--当前栏目-->\r\n	<#elseif type==\"self\">\r\n		 <#if typeid??>\r\n		 category_id=${typeid}\r\n		 <#else>\r\n		 1=1\r\n		 </#if>\r\n	<#--子栏目-->\r\n	<#elseif type==\"son\">\r\n		 <#if typeid??>\r\n		 category_categoryid=${typeid}\r\n		 <#else>\r\n		 1=1\r\n		 </#if>\r\n	</#if>\r\n<#else> <#--默认son-->\r\n	<#if typeid??>\r\n	category_categoryid=${typeid}\r\n	<#else>\r\n	category_categoryid=0\r\n	</#if>\r\n</#if>', 1);
INSERT INTO `mdiy_tag_sql` VALUES (7, 5, 'select \r\nAPP_NAME as name,\r\napp_logo as logo,\r\napp_keyword as keyword,\r\napp_description as descrip,\r\napp_copyright as copyright,\r\n<#--动态解析 -->\r\n<#if isDo?? && isDo>\r\n\"${url}\" as url,\r\n\"${url}\" as host,\r\n<#--使用地址栏的域名 -->\r\n<#elseif url??>\r\nCONCAT(\"${url}\",\"/${html}/\",app_id,\"/<#if m??>${m}</#if>\") as url,\r\n\"${url}\" as host,\r\n<#else>\r\nCONCAT(REPLACE(REPLACE(TRIM(substring_index(app_url,\"\\n\",1)), CHAR(10),\'\'), CHAR(13),\'\'),\"/html/\",app_id,\"/<#if m??>${m}</#if>\") as url,\r\nREPLACE(REPLACE(TRIM(substring_index(app_url,\"\\n\",1)), CHAR(10),\'\'), CHAR(13),\'\') as host,\r\n</#if>\r\nCONCAT(\"templets/\",app_id,\"/\",<#if m??>CONCAT(app_style,\"/${m}\")<#else>app_style</#if>) as style <#-- 判断是否为手机端 -->\r\nfrom app limit 1', 1);
INSERT INTO `mdiy_tag_sql` VALUES (8, 7, 'SELECT \r\nbasic_id as id,\r\nleft(basic_title,${titlelen?default(40)}) as title,\r\nbasic_title as fulltitle,\r\narticle_author as author, \r\narticle_source as source, \r\narticle_content as content,\r\ncategory.category_title as typename,\r\ncategory.category_id as typeid,\r\n<#--动态链接-->\r\n<#if isDo?? && isDo>\r\nCONCAT(\"/${modelName}/list.do?typeid=\", category.category_id) as typelink,\r\n<#else>\r\n(SELECT \"index.html\") as typelink,\r\n</#if>\r\nbasic.basic_thumbnails as litpic,\r\n<#--内容页动态链接-->\r\n<#if isDo?? && isDo>\r\nCONCAT(\"/mcms/view.do?id=\", basic_id) as link,\r\n<#else>\r\ncms_article.article_url AS link,\r\n</#if>\r\nbasic_datetime as date,\r\nbasic_description as descrip,\r\nbasic_hit as hit,\r\narticle_type as flag,\r\ncategory_title as typetitle,\r\n<#if tableName??>${tableName}.*,</#if>\r\ncms_article.article_keyword as keyword\r\nFROM basic LEFT JOIN cms_article ON cms_article.article_basicid = basic.basic_id \r\nLEFT JOIN category ON basic_categoryid=category.category_id \r\nLEFT JOIN basic_column ON basic_column.column_category_id=basic.basic_categoryid\r\n<#--判断是否有自定义模型表-->\r\n<#if tableName??>left join ${tableName} on ${tableName}.basicId=cms_article.ARTICLE_BASICID</#if>\r\nWHERE \r\n1=1\r\n<#if id??> and basic_id=${id}</#if>', 1);
INSERT INTO `mdiy_tag_sql` VALUES (9, 8, '<#assign select=\"(SELECT \'\')\"/>\r\n<#if preId??>\r\nSELECT \r\nbasic_id as id,\r\nleft(basic_title,${titlelen?default(40)}) as title,\r\nbasic_title as fulltitle,\r\narticle_author as author, \r\narticle_source as source, \r\narticle_content as content,\r\ncategory.category_title as typename,\r\ncategory.category_id as typeid,\r\n(SELECT \"index.html\") AS typelink,\r\nbasic.basic_thumbnails as litpic,\r\ncms_article.article_url as link,\r\nbasic_datetime as date,\r\nbasic_description as descrip,\r\nbasic_hit as hit,\r\narticle_type as flag,\r\ncms_article.article_keyword as keyword \r\nFROM basic LEFT JOIN cms_article ON cms_article.article_basicid = basic.basic_id \r\nLEFT JOIN category ON basic_categoryid=category.category_id \r\nLEFT JOIN basic_column ON basic_column.column_category_id=basic.basic_categoryid \r\nWHERE basic_id=${preId}\r\n<#else><#--没有上一页返回空字符串-->\r\nSELECT \r\n${select} as id,\r\n${select} as title,\r\n${select} as fulltitle,\r\n${select} as author, \r\n${select} as source, \r\n${select} as content,\r\n${select} as typename,\r\n${select} as typeid,\r\n${select} as typelink,\r\n${select} as litpic,\r\n${select} as link,\r\n${select} as date,\r\n${select} as descrip,\r\n${select} as hit,\r\n${select} as flag,\r\n${select} as keyword FROM basic\r\n</#if>', NULL);
INSERT INTO `mdiy_tag_sql` VALUES (10, 9, '  select\r\n	<#if !(indexUrl??)>\r\n	<#--判断是否有栏目对象，用于搜索不传栏目-->\r\n	<#if column??>\r\n		<#--顶级栏目处理-->\r\n		<#if column.categoryCategoryId==0>\r\n			<#assign path=column.columnPath/>\r\n		<#else>\r\n			<#assign path=column.columnPath/>\r\n		</#if>\r\n	<#else>\r\n		<#assign path=\"\"/>\r\n	</#if>\r\n  <#--总记录数、总页数-->\r\n	(SELECT ${total}) as total,\r\n	<#--记录总数-->\r\n	(SELECT ${rcount}) as rcount,\r\n	<#--当前页码-->\r\n	(SELECT ${pageNo}) as cur,\r\n	<#--首页-->\r\n  CONCAT(\"${path}\", \"/index.html\") as `index`,\r\n	<#--上一页-->\r\n	<#if (pageNo?eval-1) gt 1>\r\n	CONCAT(\"${path}\",\"/list-${pageNo?eval-1}.html\") as pre,\r\n	<#else>\r\n	CONCAT(\"${path}\",\"/index.html\") as pre,\r\n	</#if>\r\n	<#--下一页-->\r\n	<#if total==1>\r\n		CONCAT(\"${path}\", \"/index.html\") as `next`,\r\n		CONCAT(\"${path}\", \"/index.html\") as `last`\r\n	<#else>\r\n		<#if pageNo?eval gte total>\r\n		CONCAT(\"${path}\",\"/list-${total}.html\") as next,\r\n		<#else>\r\n		CONCAT(\"${path}\",\"/list-${pageNo?eval+1}.html\") as next,\r\n		</#if>\r\n	</#if>\r\n		<#--最后一页-->\r\n		CONCAT(\"${path}\",\"/list-${total}.html\") as last\r\n<#else><#--判断是否是搜索页面-->\r\n \"${indexUrl}\" as `index`,\"${lastUrl}\" as `last`,\"${preUrl}\" as `pre`,\"${nextUrl}\" as `next`\r\n</#if>', NULL);
INSERT INTO `mdiy_tag_sql` VALUES (11, 10, '<#assign select=\"(SELECT \'\')\"/>\r\n<#if nextId??>\r\nSELECT \r\nbasic_id as id,\r\nleft(basic_title,${titlelen?default(40)}) as title,\r\nbasic_title as fulltitle,\r\narticle_author as author, \r\narticle_source as source, \r\narticle_content as content,\r\ncategory.category_title as typename,\r\ncategory.category_id as typeid,\r\n(SELECT \"index.html\") as typelink,\r\nbasic.basic_thumbnails as litpic,\r\ncms_article.article_url as link,\r\nbasic_datetime as date,\r\nbasic_description as descrip,\r\nbasic_hit as hit,\r\narticle_type as flag,\r\ncms_article.article_keyword as keyword \r\nFROM basic LEFT JOIN cms_article ON cms_article.article_basicid = basic.basic_id \r\nLEFT JOIN category ON basic_categoryid=category.category_id \r\nLEFT JOIN basic_column ON basic_column.column_category_id=basic.basic_categoryid \r\nWHERE basic_id=${nextId}\r\n<#else>\r\nSELECT \r\n${select} as id,\r\n${select} as title,\r\n${select} as fulltitle,\r\n${select} as author, \r\n${select} as source, \r\n${select} as content,\r\n${select} as typename,\r\n${select} as typeid,\r\n${select} as typelink,\r\n${select} as litpic,\r\n${select} as link,\r\n${select} as date,\r\n${select} as descrip,\r\n${select} as hit,\r\n${select} as flag,\r\n${select} as keyword FROM basic\r\n</#if>', NULL);
INSERT INTO `mdiy_tag_sql` VALUES (13, 12, 'SELECT\r\n	basic_id AS id,\r\n	product_price as price,\r\n	product_cost_price AS costprice,\r\n	product_content AS content,\r\n	product_code AS code,\r\n	<#--详情页动态链接-->\r\n	<#if isDo?? && isDo>\r\n	CONCAT(\"/${modelName}/view.do?id=\", basic_id) as link,\r\n	<#else>\r\n	product_linkUrl AS link,\r\n	</#if>\r\n	basic_title AS title,\r\n	product_sale AS sale,\r\n	product_good AS specification,\r\n	product_inventory AS stock,\r\n	basic.basic_categoryid AS typeid,\r\n	basic_thumbnails AS litpic,\r\n	<#--列表页动态链接-->\r\n	<#if isDo?? && isDo>\r\n	CONCAT(\"/mmall/list.do?typeid=\", category.category_id) as typelink,\r\n	<#else>\r\n	(SELECT \"index.html\") AS typelink,\r\n	</#if>\r\n	<#--判断是否有自定义模型表-->\r\n	<#if tableNname??>${tableNname}.*,</#if>\r\n	category_title AS typetitle\r\nFROM\r\n	mall_product \r\n	LEFT JOIN basic ON mall_product.product_basicID = basic.basic_id\r\n	LEFT JOIN category ON basic_categoryid = category.category_id\r\n	<#--判断是否有自定义模型表-->\r\n	<#if tableNname??>LEFT JOIN ${tableNname} ON ${tableNname}.basicId=mall_product.product_basicID </#if>\r\nWHERE\r\n	1 = 1 <#--查询栏目-->\r\n	<#if (typeid)??> and (basic_categoryid=${typeid} or basic_categoryid in \r\n	(select category_id FROM category where find_in_set(${typeid},CATEGORY_PARENT_ID)))\r\n	</#if>\r\n	<#--模糊查询商品标题-->\r\n	<#if basic_title??> and basic_title like CONCAT(\"%\",\'${basic_title}\',\"%\")</#if> \r\n	LIMIT <#--判断是否分页-->\r\n	<#if ispaging?? && pageNo??>${(pageNo?eval-1)*size?eval},${size?default(20)}\r\n	<#else>${size?default(20)}</#if>', NULL);
INSERT INTO `mdiy_tag_sql` VALUES (14, 13, 'SELECT\r\n	basic_id AS id,\r\n	product_price as price,\r\n	product_cost_price AS costprice,\r\n	product_content AS content,\r\n	product_code AS code,\r\n	<#--详情页动态链接-->\r\n	<#if isDo?? && isDo>\r\n	CONCAT(\"/${modelName}/view.do?id=\", basic_id) as link,\r\n	<#else>\r\n	product_linkUrl AS link,\r\n	</#if>\r\n	basic_title AS title,\r\n	product_sale AS sale,\r\n	product_good AS specification,\r\n	product_inventory AS stock,\r\n	basic.basic_categoryid AS typeid,\r\n	basic_thumbnails AS litpic,\r\n	<#--列表页动态链接-->\r\n	<#if isDo?? && isDo>\r\n	CONCAT(\"/mmall/list.do?id=\", category.category_id) as typelink,\r\n	<#else>\r\n	(SELECT \"index.html\") AS typelink,\r\n	</#if>\r\n	<#--判断是否有自定义模型表-->\r\n	<#if tableNname??>${tableNname}.*,</#if>\r\n	category_title AS typetitle\r\nFROM\r\n	mall_product \r\n	LEFT JOIN basic ON mall_product.product_basicID = basic.basic_id\r\n	LEFT JOIN category ON basic_categoryid = category.category_id\r\n	LEFT JOIN basic_column ON basic_column.column_category_id = basic.basic_categoryid\r\n	<#--判断是否有自定义模型表-->\r\n	<#if tableNname??>LEFT JOIN ${tableNname} ON ${tableNname}.basicId=mall_product.product_basicID </#if>\r\nWHERE\r\n	1 = 1 <#if id??> and basic_id=${id} </#if>', NULL);

INSERT INTO `model` VALUES ('155', '自定义标签', '20060000', '104', 'mdiy/tag/index.do', '2017-09-04 11:18:51', '', '0', '0', '1', '104','');
INSERT INTO `model` VALUES ('156', '新增', '20060001', '155', 'mdiy:tag:save', '2017-09-04 14:28:41', '', '0', '0', '0', '104,155','');
INSERT INTO `model` VALUES ('157', '查看', '20060002', '155', 'mdiy:tag:view', '2018-06-20 17:53:51', '', '0', '0', '0', '104,155','');
INSERT INTO `model` VALUES ('158', '修改', '20060003', '155', 'mdiy:tag:update', '2018-06-20 17:54:43', null, '0', '0', '0', '104,155','');
INSERT INTO `model` VALUES ('159', '删除', '20060004', '155', 'mdiy:tag:del', '2018-06-20 17:55:26', null, '0', '0', '0', '104,155','');
INSERT INTO `model` VALUES ('160', '新增SQL', '20060005', '155', 'mdiy:tagSql:save', '2017-09-04 14:28:41', '', '0', '0', '0', '104,155','');
INSERT INTO `model` VALUES ('161', '查看SQL', '20060006', '155', 'mdiy:tagSql:view', '2018-06-20 17:53:51', '', '0', '0', '0', '104,155','');
INSERT INTO `model` VALUES ('162', '修改SQL', '20060007', '155', 'mdiy:tagSql:update', '2018-06-20 17:54:43', null, '0', '0', '0', '104,155','');
INSERT INTO `model` VALUES ('163', '删除SQL', '20060008', '155', 'mdiy:tagSql:del', '2018-06-20 17:55:26', null, '0', '0', '0', '104,155','');

INSERT INTO `role_model` VALUES ('155', '48');
INSERT INTO `role_model` VALUES ('156', '48');
INSERT INTO `role_model` VALUES ('157', '48');
INSERT INTO `role_model` VALUES ('158', '48');
INSERT INTO `role_model` VALUES ('159', '48');
INSERT INTO `role_model` VALUES ('160', '48');
INSERT INTO `role_model` VALUES ('161', '48');
INSERT INTO `role_model` VALUES ('162', '48');
INSERT INTO `role_model` VALUES ('163', '48');
