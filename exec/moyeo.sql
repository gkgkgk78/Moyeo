-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema moyeo
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema moyeo
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `moyeo` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `moyeo` ;

-- -----------------------------------------------------
-- Table `moyeo`.`BATCH_JOB_INSTANCE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`BATCH_JOB_INSTANCE` (
  `JOB_INSTANCE_ID` BIGINT NOT NULL,
  `VERSION` BIGINT NULL DEFAULT NULL,
  `JOB_NAME` VARCHAR(100) NOT NULL,
  `JOB_KEY` VARCHAR(32) NOT NULL,
  PRIMARY KEY (`JOB_INSTANCE_ID`),
  UNIQUE INDEX `JOB_INST_UN` (`JOB_NAME` ASC, `JOB_KEY` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`BATCH_JOB_EXECUTION`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`BATCH_JOB_EXECUTION` (
  `JOB_EXECUTION_ID` BIGINT NOT NULL,
  `VERSION` BIGINT NULL DEFAULT NULL,
  `JOB_INSTANCE_ID` BIGINT NOT NULL,
  `CREATE_TIME` TIMESTAMP NOT NULL,
  `START_TIME` TIMESTAMP NULL DEFAULT NULL,
  `END_TIME` TIMESTAMP NULL DEFAULT NULL,
  `STATUS` VARCHAR(10) NULL DEFAULT NULL,
  `EXIT_CODE` VARCHAR(2500) NULL DEFAULT NULL,
  `EXIT_MESSAGE` VARCHAR(2500) NULL DEFAULT NULL,
  `LAST_UPDATED` TIMESTAMP NULL DEFAULT NULL,
  `JOB_CONFIGURATION_LOCATION` VARCHAR(2500) NULL DEFAULT NULL,
  PRIMARY KEY (`JOB_EXECUTION_ID`),
  INDEX `JOB_INST_EXEC_FK` (`JOB_INSTANCE_ID` ASC) VISIBLE,
  CONSTRAINT `JOB_INST_EXEC_FK`
    FOREIGN KEY (`JOB_INSTANCE_ID`)
    REFERENCES `moyeo`.`BATCH_JOB_INSTANCE` (`JOB_INSTANCE_ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`BATCH_JOB_EXECUTION_CONTEXT`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`BATCH_JOB_EXECUTION_CONTEXT` (
  `JOB_EXECUTION_ID` BIGINT NOT NULL,
  `SHORT_CONTEXT` VARCHAR(2500) NOT NULL,
  `SERIALIZED_CONTEXT` LONGTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`JOB_EXECUTION_ID`),
  CONSTRAINT `JOB_EXEC_CTX_FK`
    FOREIGN KEY (`JOB_EXECUTION_ID`)
    REFERENCES `moyeo`.`BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`BATCH_JOB_EXECUTION_PARAMS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`BATCH_JOB_EXECUTION_PARAMS` (
  `JOB_EXECUTION_ID` BIGINT NOT NULL,
  `TYPE_CD` VARCHAR(6) NOT NULL,
  `KEY_NAME` VARCHAR(100) NOT NULL,
  `STRING_VAL` VARCHAR(250) NULL DEFAULT NULL,
  `DATE_VAL` DATETIME NULL DEFAULT NULL,
  `LONG_VAL` BIGINT NULL DEFAULT NULL,
  `DOUBLE_VAL` DOUBLE NULL DEFAULT NULL,
  `IDENTIFYING` CHAR(1) NOT NULL,
  INDEX `JOB_EXEC_PARAMS_FK` (`JOB_EXECUTION_ID` ASC) VISIBLE,
  CONSTRAINT `JOB_EXEC_PARAMS_FK`
    FOREIGN KEY (`JOB_EXECUTION_ID`)
    REFERENCES `moyeo`.`BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`BATCH_JOB_EXECUTION_SEQ`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`BATCH_JOB_EXECUTION_SEQ` (
  `ID` BIGINT NOT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`BATCH_JOB_SEQ`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`BATCH_JOB_SEQ` (
  `ID` BIGINT NOT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`BATCH_STEP_EXECUTION`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`BATCH_STEP_EXECUTION` (
  `STEP_EXECUTION_ID` BIGINT NOT NULL,
  `VERSION` BIGINT NOT NULL,
  `STEP_NAME` VARCHAR(100) NOT NULL,
  `JOB_EXECUTION_ID` BIGINT NOT NULL,
  `START_TIME` TIMESTAMP NOT NULL,
  `END_TIME` TIMESTAMP NULL DEFAULT NULL,
  `STATUS` VARCHAR(10) NULL DEFAULT NULL,
  `COMMIT_COUNT` BIGINT NULL DEFAULT NULL,
  `READ_COUNT` BIGINT NULL DEFAULT NULL,
  `FILTER_COUNT` BIGINT NULL DEFAULT NULL,
  `WRITE_COUNT` BIGINT NULL DEFAULT NULL,
  `READ_SKIP_COUNT` BIGINT NULL DEFAULT NULL,
  `WRITE_SKIP_COUNT` BIGINT NULL DEFAULT NULL,
  `PROCESS_SKIP_COUNT` BIGINT NULL DEFAULT NULL,
  `ROLLBACK_COUNT` BIGINT NULL DEFAULT NULL,
  `EXIT_CODE` VARCHAR(2500) NULL DEFAULT NULL,
  `EXIT_MESSAGE` VARCHAR(2500) NULL DEFAULT NULL,
  `LAST_UPDATED` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`STEP_EXECUTION_ID`),
  INDEX `JOB_EXEC_STEP_FK` (`JOB_EXECUTION_ID` ASC) VISIBLE,
  CONSTRAINT `JOB_EXEC_STEP_FK`
    FOREIGN KEY (`JOB_EXECUTION_ID`)
    REFERENCES `moyeo`.`BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`BATCH_STEP_EXECUTION_CONTEXT`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`BATCH_STEP_EXECUTION_CONTEXT` (
  `STEP_EXECUTION_ID` BIGINT NOT NULL,
  `SHORT_CONTEXT` VARCHAR(2500) NOT NULL,
  `SERIALIZED_CONTEXT` LONGTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`STEP_EXECUTION_ID`),
  CONSTRAINT `STEP_EXEC_CTX_FK`
    FOREIGN KEY (`STEP_EXECUTION_ID`)
    REFERENCES `moyeo`.`BATCH_STEP_EXECUTION` (`STEP_EXECUTION_ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`BATCH_STEP_EXECUTION_SEQ`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`BATCH_STEP_EXECUTION_SEQ` (
  `ID` BIGINT NOT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`batch_statistic`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`batch_statistic` (
  `batch_statistic_id` BIGINT NOT NULL AUTO_INCREMENT,
  `device_token` VARCHAR(200) NULL DEFAULT NULL,
  `address1` VARCHAR(50) NULL DEFAULT NULL,
  `address2` VARCHAR(50) NULL DEFAULT NULL,
  `address3` VARCHAR(50) NULL DEFAULT NULL,
  `address4` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY USING BTREE (`batch_statistic_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 121
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`user` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT,
  `client_id` VARCHAR(30) CHARACTER SET 'utf8mb3' NOT NULL,
  `nickname` VARCHAR(30) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `password` VARCHAR(100) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `profile_image_url` VARCHAR(100) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `refresh_token` VARCHAR(255) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `role` VARCHAR(255) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `device_token` VARCHAR(200) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `UK_rb7eox526ilbewv2wuv5bnsrt` (`client_id` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 18
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`time_line`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`time_line` (
  `timeline_id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL DEFAULT NULL,
  `modify_time` DATETIME NULL DEFAULT NULL,
  `finish_time` DATETIME NULL DEFAULT NULL,
  `is_complete` BIT(1) NULL DEFAULT b'0',
  `is_timeline_public` BIT(1) NULL DEFAULT b'1',
  `last_post` BIGINT NULL DEFAULT '0',
  `title` VARCHAR(100) CHARACTER SET 'utf8mb3' NOT NULL DEFAULT '여행중',
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`timeline_id`),
  INDEX `FK72uol7m2g94bstnk2okxcdjo` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK72uol7m2g94bstnk2okxcdjo`
    FOREIGN KEY (`user_id`)
    REFERENCES `moyeo`.`user` (`user_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 130
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`nation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`nation` (
  `nation_id` TINYINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(60) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `nation_url` VARCHAR(180) NULL DEFAULT NULL,
  PRIMARY KEY (`nation_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`post`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`post` (
  `post_id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL DEFAULT NULL,
  `modify_time` DATETIME NULL DEFAULT NULL,
  `address1` VARCHAR(50) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `address2` VARCHAR(50) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `address3` VARCHAR(50) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `address4` VARCHAR(50) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `favorite_count` MEDIUMINT NULL DEFAULT '0',
  `text` VARCHAR(100) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `voice_length` DOUBLE NULL DEFAULT NULL,
  `voice_url` VARCHAR(120) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `nation_id` TINYINT NULL DEFAULT NULL,
  `timeline_id` BIGINT NULL DEFAULT NULL,
  `user_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  INDEX `FKc8cj5j15kmp14xvp5r0hvo8fv` (`nation_id` ASC) VISIBLE,
  INDEX `FK3snh9odcaoulqfvetlboupwoo` (`timeline_id` ASC) VISIBLE,
  INDEX `FK72mt33dhhs48hf9gcqrq4fxte` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK3snh9odcaoulqfvetlboupwoo`
    FOREIGN KEY (`timeline_id`)
    REFERENCES `moyeo`.`time_line` (`timeline_id`)
    ON DELETE CASCADE,
  CONSTRAINT `FK72mt33dhhs48hf9gcqrq4fxte`
    FOREIGN KEY (`user_id`)
    REFERENCES `moyeo`.`user` (`user_id`),
  CONSTRAINT `FKc8cj5j15kmp14xvp5r0hvo8fv`
    FOREIGN KEY (`nation_id`)
    REFERENCES `moyeo`.`nation` (`nation_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 193
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`favorite`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`favorite` (
  `post_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`post_id`, `user_id`),
  INDEX `FKh3f2dg11ibnht4fvnmx60jcif` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FKh20v4bwlpu57uv12dl7i2qipe`
    FOREIGN KEY (`post_id`)
    REFERENCES `moyeo`.`post` (`post_id`)
    ON DELETE CASCADE,
  CONSTRAINT `FKh3f2dg11ibnht4fvnmx60jcif`
    FOREIGN KEY (`user_id`)
    REFERENCES `moyeo`.`user` (`user_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`firebasecm`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`firebasecm` (
  `id` VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NOT NULL DEFAULT 'AUTO_INCREMENT',
  `message` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`message_box`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`message_box` (
  `message_id` BIGINT NOT NULL AUTO_INCREMENT,
  `content` VARCHAR(2000) NULL DEFAULT NULL,
  `create_time` DATETIME NULL DEFAULT NULL,
  `is_checked` BIT(1) NULL DEFAULT b'0',
  `user_id` BIGINT NULL DEFAULT NULL,
  `invite_key` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`message_id`),
  INDEX `FKgq6ex14jxdivpftg9w3budrnw` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FKgq6ex14jxdivpftg9w3budrnw`
    FOREIGN KEY (`user_id`)
    REFERENCES `moyeo`.`user` (`user_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 706
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`moyeo_time_line`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`moyeo_time_line` (
  `moyeo_timeline_id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL DEFAULT NULL,
  `modify_time` DATETIME NULL DEFAULT NULL,
  `finish_time` DATETIME NULL DEFAULT NULL,
  `is_complete` BIT(1) NULL DEFAULT b'0',
  `is_timeline_public` BIT(1) NULL DEFAULT b'1',
  `members_count` MEDIUMINT NULL DEFAULT '0',
  `title` VARCHAR(100) CHARACTER SET 'utf8mb3' NOT NULL DEFAULT '동행 중',
  PRIMARY KEY (`moyeo_timeline_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 137
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`moyeo_post`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`moyeo_post` (
  `moyeo_post_id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL DEFAULT NULL,
  `modify_time` DATETIME NULL DEFAULT NULL,
  `address1` VARCHAR(50) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `address2` VARCHAR(50) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `address3` VARCHAR(50) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `address4` VARCHAR(50) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `favorite_count` MEDIUMINT NULL DEFAULT '0',
  `text` VARCHAR(100) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `voice_length` DOUBLE NULL DEFAULT NULL,
  `voice_url` VARCHAR(120) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `moyeo_timeline_id` BIGINT NULL DEFAULT NULL,
  `nation_id` TINYINT NULL DEFAULT NULL,
  PRIMARY KEY (`moyeo_post_id`),
  INDEX `FKijd6g9fxqcd9uiru2h9fuus2p` (`moyeo_timeline_id` ASC) VISIBLE,
  INDEX `FK3jcn3wfkaml0ksg5uwst2scox` (`nation_id` ASC) VISIBLE,
  CONSTRAINT `FK3jcn3wfkaml0ksg5uwst2scox`
    FOREIGN KEY (`nation_id`)
    REFERENCES `moyeo`.`nation` (`nation_id`),
  CONSTRAINT `FKijd6g9fxqcd9uiru2h9fuus2p`
    FOREIGN KEY (`moyeo_timeline_id`)
    REFERENCES `moyeo`.`moyeo_time_line` (`moyeo_timeline_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 69
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`moyeo_favorite`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`moyeo_favorite` (
  `moyeo_post_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`moyeo_post_id`, `user_id`),
  INDEX `FK326i3rn1u2ycybo13qpkqpqcx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK1ok1rrji06smhbepguxku974`
    FOREIGN KEY (`moyeo_post_id`)
    REFERENCES `moyeo`.`moyeo_post` (`moyeo_post_id`)
    ON DELETE CASCADE,
  CONSTRAINT `FK326i3rn1u2ycybo13qpkqpqcx`
    FOREIGN KEY (`user_id`)
    REFERENCES `moyeo`.`user` (`user_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`moyeo_members`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`moyeo_members` (
  `moyeo_members_id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `finish_time` DATETIME NULL DEFAULT NULL,
  `join_time` DATETIME NULL DEFAULT NULL,
  `moyeo_timeline_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`moyeo_members_id`, `user_id`),
  INDEX `FKs0nn9dqccu5ijv7yv40o7lt9o` (`user_id` ASC) VISIBLE,
  INDEX `moyeo_timeline_id_idx` (`moyeo_timeline_id` ASC) VISIBLE,
  CONSTRAINT `FKs0nn9dqccu5ijv7yv40o7lt9o`
    FOREIGN KEY (`user_id`)
    REFERENCES `moyeo`.`user` (`user_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 186
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`moyeo_photo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`moyeo_photo` (
  `moyeo_photo_id` BIGINT NOT NULL AUTO_INCREMENT,
  `photo_url` VARCHAR(120) CHARACTER SET 'utf8mb3' NOT NULL,
  `moyeo_post_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`moyeo_photo_id`),
  UNIQUE INDEX `UK_3vxif7knxy8mon0w2ux66lghu` (`photo_url` ASC) VISIBLE,
  INDEX `FK7x02srqrqerf26u5mb12pxvq1` (`moyeo_post_id` ASC) VISIBLE,
  CONSTRAINT `FK7x02srqrqerf26u5mb12pxvq1`
    FOREIGN KEY (`moyeo_post_id`)
    REFERENCES `moyeo`.`moyeo_post` (`moyeo_post_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 111
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`moyeo_public`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`moyeo_public` (
  `moyeo_post_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `create_time` DATETIME NULL DEFAULT NULL,
  `is_deleted` BIT(1) NULL DEFAULT b'0',
  `is_public` BIT(1) NULL DEFAULT b'1',
  PRIMARY KEY (`moyeo_post_id`, `user_id`),
  INDEX `FKqtv7jmnmpxg7y8048j3d26crr` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK706qi42mu9yjjnsgj6opu5lys`
    FOREIGN KEY (`moyeo_post_id`)
    REFERENCES `moyeo`.`moyeo_post` (`moyeo_post_id`)
    ON DELETE CASCADE,
  CONSTRAINT `FKqtv7jmnmpxg7y8048j3d26crr`
    FOREIGN KEY (`user_id`)
    REFERENCES `moyeo`.`user` (`user_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`photo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`photo` (
  `photo_id` BIGINT NOT NULL AUTO_INCREMENT,
  `photo_url` VARCHAR(120) CHARACTER SET 'utf8mb3' NOT NULL,
  `post_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`photo_id`),
  UNIQUE INDEX `UK_beoa8atls3i4o4s8dbsh6n182` (`photo_url` ASC) VISIBLE,
  INDEX `FKt47fmi9mi5p9dkjyyuoyfc63f` (`post_id` ASC) VISIBLE,
  CONSTRAINT `FKt47fmi9mi5p9dkjyyuoyfc63f`
    FOREIGN KEY (`post_id`)
    REFERENCES `moyeo`.`post` (`post_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 311
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`push_table`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`push_table` (
  `device_token` VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NOT NULL DEFAULT '''''',
  `user_id` BIGINT NULL DEFAULT NULL,
  `address1` VARCHAR(50) NULL DEFAULT NULL,
  `address2` VARCHAR(50) NULL DEFAULT NULL,
  `address3` VARCHAR(50) NULL DEFAULT NULL,
  `address4` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`device_token`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `moyeo`.`time_line_and_moyeo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `moyeo`.`time_line_and_moyeo` (
  `moyeo_id` BIGINT NOT NULL AUTO_INCREMENT,
  `last_post_order_number` BIGINT NULL DEFAULT '0',
  `moyeo_timeline_id` BIGINT NULL DEFAULT NULL,
  `timeline_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`moyeo_id`),
  INDEX `FKmgntpv59xvj9lmxu2dsl7ng0q` (`moyeo_timeline_id` ASC) VISIBLE,
  INDEX `FKcjwyyhpmhcmirfjeibbtos6js` (`timeline_id` ASC) VISIBLE,
  CONSTRAINT `FKcjwyyhpmhcmirfjeibbtos6js`
    FOREIGN KEY (`timeline_id`)
    REFERENCES `moyeo`.`time_line` (`timeline_id`)
    ON DELETE CASCADE,
  CONSTRAINT `FKmgntpv59xvj9lmxu2dsl7ng0q`
    FOREIGN KEY (`moyeo_timeline_id`)
    REFERENCES `moyeo`.`moyeo_time_line` (`moyeo_timeline_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 176
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

USE `moyeo`;

DELIMITER $$
USE `moyeo`$$
CREATE
DEFINER=`Wujin`@`%`
TRIGGER `moyeo`.`update_device_token`
AFTER UPDATE ON `moyeo`.`user`
FOR EACH ROW
BEGIN
	UPDATE `push_table` SET `device_token` = NEW.`device_token`
	WHERE `device_token` = OLD.`device_token`;
END$$

USE `moyeo`$$
CREATE
DEFINER=`Wujin`@`%`
TRIGGER `moyeo`.`post_after_insert`
AFTER UPDATE ON `moyeo`.`post`
FOR EACH ROW
BEGIN
    INSERT INTO `push_table` (`device_token`,`user_id`, `address1`, `address2`, `address3`, `address4`)
    SELECT `u`.`device_token`,`u`.`user_id`, `p`.`address1`, `p`.`address2`, `p`.`address3`, `p`.`address4`
    FROM `user` `u`
    INNER JOIN `post` `p` ON `u`.`user_id` = `p`.`user_id`
    WHERE `p`.`post_id` = NEW.`post_id`
ON DUPLICATE KEY UPDATE
    `address1` = NEW.`address1`,
    `address2` = NEW.`address2`,
    `address3` = NEW.`address3`,
    `address4` = NEW.`address4`;
END$$

USE `moyeo`$$
CREATE
DEFINER=`Wujin`@`%`
TRIGGER `moyeo`.`moyeo_post_after_insert`
AFTER UPDATE ON `moyeo`.`moyeo_post`
FOR EACH ROW
BEGIN
INSERT INTO `push_table` (`device_token`,`user_id`, `address1`, `address2`, `address3`, `address4`)
    SELECT `u`.`device_token`,`u`.`user_id`, `mp`.`address1`, `mp`.`address2`, `mp`.`address3`, `mp`.`address4`
    FROM `user` `u`
    INNER JOIN `moyeo_members` `mm` ON `u`.`user_id` = `mm`.`user_id`
    INNER JOIN `moyeo_time_line` `mtl` on `mm`.`moyeo_timeline_id` = `mtl`.`moyeo_timeline_id`
    INNER JOIN `moyeo_post` `mp` on `mm`.`moyeo_timeline_id` = `mp`.`moyeo_timeline_id`
    WHERE `mp`.`moyeo_timeline_id`= NEW.`moyeo_timeline_id`
    ON DUPLICATE KEY UPDATE
    `address1` = NEW.`address1`,
    `address2` = NEW.`address2`,
    `address3` = NEW.`address3`,
    `address4` = NEW.`address4`;
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
