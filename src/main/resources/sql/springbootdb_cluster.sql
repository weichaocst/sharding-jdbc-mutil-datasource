/*
Navicat MySQL Data Transfer

Source Server         : 本地MySQL
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : springbootdb_cluster

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-10-22 21:00:58
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for city
-- ----------------------------
DROP TABLE IF EXISTS `city`;
CREATE TABLE `city` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '城市编号',
  `province_id` int(10) unsigned NOT NULL COMMENT '省份编号',
  `city_name` varchar(25) DEFAULT NULL COMMENT '城市名称',
  `description` varchar(25) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_order_0
-- ----------------------------
DROP TABLE IF EXISTS `t_order_0`;
CREATE TABLE `t_order_0` (
  `order_id` varchar(32) NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_order_1
-- ----------------------------
DROP TABLE IF EXISTS `t_order_1`;
CREATE TABLE `t_order_1` (
  `order_id` varchar(32) NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_order_item_0
-- ----------------------------
DROP TABLE IF EXISTS `t_order_item_0`;
CREATE TABLE `t_order_item_0` (
  `order_item_id` varchar(32) NOT NULL,
  `order_id` varchar(32) DEFAULT NULL,
  `user_id` varchar(32) NOT NULL,
  PRIMARY KEY (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_order_item_1
-- ----------------------------
DROP TABLE IF EXISTS `t_order_item_1`;
CREATE TABLE `t_order_item_1` (
  `order_item_id` varchar(32) NOT NULL,
  `order_id` varchar(32) DEFAULT NULL,
  `user_id` varchar(32) NOT NULL,
  PRIMARY KEY (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
