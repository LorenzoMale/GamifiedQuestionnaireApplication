DROP DATABASE IF EXISTS `db_gamifiedapp`;

CREATE DATABASE IF NOT EXISTS `db_gamifiedapp`;
USE `db_gamifiedapp`;

DROP TABLE IF EXISTS `user_table`;

CREATE TABLE `user_table` (
	`id` int NOT NULL AUTO_INCREMENT,
	`username` varchar(40) UNIQUE NOT NULL,
    `password` varchar(40) NOT NULL,
    `email` varchar(60) NOT NULL,
    `role` CHAR NOT NULL DEFAULT 'U',
    `lastAccess` datetime,
    PRIMARY KEY(`id`)
    );
    
LOCK TABLES `user_table` WRITE;
INSERT INTO `user_table`  VALUES(1, 'USER', 'PSW', 'USER@GMAIL.COM', 'U', '1.1.01');
INSERT INTO `user_table`  VALUES(2, 'l', 'l', 'l@GMAIL.COM', 'U', '1.1.01');
INSERT INTO `user_table`  VALUES(3, 'USER2', 'PSW2', 'USER2@GMAIL.COM', 'U', '1.1.01');
INSERT INTO `user_table`  VALUES(4, 'a', 'a', 'a@GMAIL.COM', 'A', '1.1.01');
INSERT INTO `user_table`  VALUES(5, 'b', 'b', 'a@GMAIL.COM', 'B', '1.1.01');
UNLOCK TABLES;
    

DROP TABLE IF EXISTS `product`;

CREATE TABLE `product`(
	`id` int NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(100),
    `image` LONGBLOB,
    `date` date,
    PRIMARY KEY(`id`)
);


LOCK TABLES `product` WRITE;
INSERT INTO `product`  VALUES(2, 'oldProduct', 'description', '', '01.01.01');
INSERT INTO `product`  VALUES(3, 'chair', 'chair description', '', '2021.09.05');

UNLOCK TABLES;

DROP TABLE IF EXISTS `review`;

CREATE TABLE `review`(
	`id` int not null auto_increment,
    `reviewString` varchar(200),
    `product` int,
    primary key(`id`),
    foreign key(`product`) references `product`(`id`)
    );
    
DROP TABLE IF EXISTS `questionnaire`;

CREATE TABLE `questionnaire`(
	`id` int not null auto_increment,
    `product` int,
    primary key(`id`),
    foreign key(`product`) references `product`(`id`)
    );
    
LOCK TABLES `questionnaire` WRITE;
INSERT INTO `questionnaire`  VALUES(100, 2);
INSERT INTO `questionnaire`  VALUES(101, 3);

UNLOCK TABLES;



DROP TABLE IF EXISTS `question`;

CREATE TABLE `question`(
	`id` int NOT NULL auto_increment,
    `questionString` VARCHAR(150) NOT NULL,
    `questiontype` VARCHAR(50) NOT NULL DEFAULT 'marketing',
    `questionnaire` int,
    primary key(`id`),
    foreign key(`questionnaire`) references `questionnaire`(`id`)
    );
    
    
LOCK TABLES `question` WRITE;
INSERT INTO `question`  VALUES(100, 'marketing question string for oldProduct', 'marketing', 100);
INSERT INTO `question`  VALUES(101, 'statistical question string for oldProduct', 'statistical', 100);
INSERT INTO `question`  VALUES(102, 'first marketing question for chair product', 'marketing', 101);
INSERT INTO `question`  VALUES(103, 'first statistical question for chair product', 'statistical', 101);
INSERT INTO `question`  VALUES(104, 'second marketing question for chair product', 'marketing', 101);
INSERT INTO `question`  VALUES(105, 'second statistical question for chair product', 'statistical', 101);
INSERT INTO `question`  VALUES(106, 'third statistical question for chair product', 'statistical', 101);
UNLOCK TABLES; 
    
    
DROP TABLE IF EXISTS `answer`;


CREATE TABLE `answer`(
	`id` int NOT NULL auto_increment,
    `answerstring` VARCHAR(150),
	`question` int,
    `user` int,
    PRIMARY KEY(`id`),
    FOREIGN KEY(`question`) REFERENCES `question`(`id`),
    FOREIGN KEY(`user`) REFERENCES `user_table`(`id`)
    );
    

    
DROP TABLE IF EXISTS `leaderboard`;

CREATE TABLE `leaderboard`(
	`id` int not null auto_increment,
    `questionnaire` int,
    `user` int,
    `points` int DEFAULT 0,
    primary key(`id`),
    foreign key(`questionnaire`) references `questionnaire`(`id`),
    foreign key(`user`) references `user_table`(`id`)
    );
    
LOCK TABLES `leaderboard` WRITE;
INSERT INTO `leaderboard`  VALUES(1, 101, 3, 0);

UNLOCK TABLES;


DROP TABLE IF EXISTS `bad_words`;

CREATE TABLE `bad_words`(
	`word` varchar(25),
    primary key(`word`)
    );

delimiter $$

CREATE TRIGGER insert_in_leaderboard
BEFORE INSERT ON `answer`
FOR EACH ROW
BEGIN
IF NOT EXISTS (SELECT * FROM leaderboard l WHERE l.user=NEW.user 
		and l.questionnaire=(SELECT q.questionnaire FROM question q WHERE q.id=NEW.question)) THEN
    INSERT INTO leaderboard(questionnaire, user, points) VALUE((SELECT q.questionnaire FROM question q WHERE q.id=NEW.question), NEW.user, 0);
END IF;

END$$

DELIMITER $$
CREATE TRIGGER marketing_points
AFTER INSERT ON `answer`
FOR EACH ROW
BEGIN
IF((SELECT q.questiontype FROM question q where q.id=NEW.question)='marketing' ) THEN
	UPDATE leaderboard l
	SET points=points+1
	WHERE l.user=NEW.user and l.questionnaire=(SELECT q.questionnaire FROM question q WHERE q.id=NEW.question);
ELSE
	UPDATE leaderboard l
	SET points=points +2
    WHERE l.user=NEW.user and l.questionnaire=(SELECT q.questionnaire FROM question q WHERE q.id=NEW.question);
END IF;
END$$


LOCK TABLES `answer` WRITE;
INSERT INTO `answer`  VALUES(100, 'USER answer for the marketing question regarding oldProduct', 100, 1);
INSERT INTO `answer`  VALUES(101, 'USER answer for the statistical question regarding oldProduct', 101, 1);
INSERT INTO `answer`  VALUES(102, 'l answer for the marketing question regarding oldProduct', 100, 2);
INSERT INTO `answer`  VALUES(103, 'l answer for the statistical question regarding oldProduct', 101, 2);
INSERT INTO `answer`  VALUES(104, 'USER answer for the first marketing question regarding chair', 102, 1);
INSERT INTO `answer`  VALUES(105, 'USER answer for the first statistical question regarding chair', 103, 1);
INSERT INTO `answer`  VALUES(106, 'l answer for the second marketing question regarding chair', 104, 2);
INSERT INTO `answer`  VALUES(107, 'l answer for the second statistical question regarding chair', 105, 2);
INSERT INTO `answer`  VALUES(108, 'l answer for the third statistical question regarding chair', 106, 2);
UNLOCK TABLES; 


LOCK TABLES `bad_words` WRITE;
INSERT INTO `bad_words`  VALUES('badword1');
INSERT INTO `bad_words`  VALUES('badword2');
UNLOCK TABLES; 






    