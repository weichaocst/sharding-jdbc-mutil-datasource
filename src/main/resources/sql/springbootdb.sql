/*
Navicat MySQL Data Transfer

Source Server         : 本地MySQL
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : springbootdb

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-10-22 21:00:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(32) NOT NULL COMMENT '用户编号',
  `user_name` varchar(32) DEFAULT NULL COMMENT '用户名称',
  `description` varchar(32) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
