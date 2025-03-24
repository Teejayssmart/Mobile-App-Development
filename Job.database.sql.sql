SHOW DATABASES;
 SHOW TABLES;
 USE mobile_app;
 
 SET FOREIGN_KEY_CHECKS = 0;
 
 -- ----------------------------
 -- Table structure for job_table
 -- ----------------------------
 DROP TABLE IF EXISTS `job_table`;
 CREATE TABLE `job_table`  (
   `j_no` int NOT NULL AUTO_INCREMENT,
   `j_user` varchar(50) ,
   `j_title` varchar(50) ,
   `j_content` varchar(500) ,
   `j_price` float NULL DEFAULT NULL,
   `j_state` int NULL DEFAULT 0,
   `j_apply_list` varchar(200) ,
   `j_taken` varchar(50) ,
   `j_time` datetime DEFAULT NULL,
   PRIMARY KEY (`j_no`),
   INDEX `j_user`(`j_user`),
   INDEX `j_taken`(`j_taken`),
   CONSTRAINT `job_table_ibfk_1` FOREIGN KEY (`j_user`) REFERENCES `user_table` (`u_account`) ON DELETE RESTRICT ON UPDATE RESTRICT,
   CONSTRAINT `job_table_ibfk_2` FOREIGN KEY (`j_taken`) REFERENCES `user_table` (`u_account`) ON DELETE RESTRICT ON UPDATE RESTRICT
 ) ;
 
 -- ----------------------------
 -- Table structure for user_table
 -- ----------------------------
 DROP TABLE IF EXISTS `user_table`;
 CREATE TABLE `user_table`  (
   `u_account` varchar(50) ,
   `u_password` varchar(50) ,
   `u_balance` float NULL DEFAULT NULL,
   PRIMARY KEY (`u_account`)
 ) ;
 
 SET FOREIGN_KEY_CHECKS = 1;
 