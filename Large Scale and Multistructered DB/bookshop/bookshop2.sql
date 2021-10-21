CREATE DATABASE  IF NOT EXISTS `bookshop2` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `bookshop2`;
-- MySQL dump 10.13  Distrib 8.0.26, for macos11 (x86_64)
--
-- Host: localhost    Database: bookshop2
-- ------------------------------------------------------
-- Server version	8.0.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `author`
--

DROP TABLE IF EXISTS `author`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `author` (
  `idAuthor` int NOT NULL AUTO_INCREMENT,
  `firstName` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `lastName` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `biography` varchar(511) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  PRIMARY KEY (`idAuthor`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `author`
--

LOCK TABLES `author` WRITE;
/*!40000 ALTER TABLE `author` DISABLE KEYS */;
INSERT INTO `author` VALUES (1,'John Ronald Reuel','Tolkien','Tolkien\'s immediate paternal ancestors were middle-class craftsmen who made and sold clocks, watches and pianos in London and Birmingham. The Tolkien family originated in the East Prussian town Kreuzburg near Königsberg, which was founded during medieval German eastward expansion, where his earliest-known paternal ancestor Michel Tolkien was born around 1620.'),(2,'Agatha','Christie','Agatha Mary Clarissa Miller was born on 15 September 1890, into a wealthy upper-middle-class family in Torquay, Devon. She was the youngest of three children born to Frederick Alvah Miller, \"a gentleman of substance\", and his wife Clarissa Margaret (\"Clara\") Miller née Boehmer.'),(3,'Paula','Hawkins','Hawkins was born and raised in Salisbury, Rhodesia (now Harare, Zimbabwe), the daughter of Anthony \"Tony\" Hawkins and his wife Glynne.Her father was an economics professor and financial journalist. Before moving to London in 1989 at the age of 17, Hawkins attended Arundel School, Harare, Zimbabwe then studied for her A-Levels at Collingham College, an independent college in Kensington, West London.');
/*!40000 ALTER TABLE `author` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book`
--

DROP TABLE IF EXISTS `book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book` (
  `idBook` int NOT NULL,
  `title` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `price` float NOT NULL,
  `category` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `publicationYear` year NOT NULL,
  `numPages` int NOT NULL,
  `publisherId` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`idBook`),
  KEY `publisherId_idx` (`publisherId`),
  CONSTRAINT `publisherId` FOREIGN KEY (`publisherId`) REFERENCES `publisher` (`idPublisher`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book`
--

LOCK TABLES `book` WRITE;
/*!40000 ALTER TABLE `book` DISABLE KEYS */;
INSERT INTO `book` VALUES (1,'Lord of the rings',50,'Fantasy',1954,1230,1,103000000),(2,'Murder on the Orient Express',12,'Crime novel',1934,256,2,50000000),(3,'The A.B.C. Murders',15,'Crime novel',1936,256,2,40000000),(4,'The Girl on the Train',8,'Thriller',2015,395,3,11000000);
/*!40000 ALTER TABLE `book` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_has_author`
--

DROP TABLE IF EXISTS `book_has_author`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_has_author` (
  `bookId` int NOT NULL,
  `authorId` int NOT NULL,
  PRIMARY KEY (`bookId`,`authorId`),
  KEY `authorId_idx` (`authorId`),
  CONSTRAINT `authorId` FOREIGN KEY (`authorId`) REFERENCES `author` (`idAuthor`),
  CONSTRAINT `bookId` FOREIGN KEY (`bookId`) REFERENCES `book` (`idBook`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_has_author`
--

LOCK TABLES `book_has_author` WRITE;
/*!40000 ALTER TABLE `book_has_author` DISABLE KEYS */;
INSERT INTO `book_has_author` VALUES (1,1),(2,2),(3,2),(4,3);
/*!40000 ALTER TABLE `book_has_author` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `publisher`
--

DROP TABLE IF EXISTS `publisher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `publisher` (
  `idPublisher` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  `location` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL,
  PRIMARY KEY (`idPublisher`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `publisher`
--

LOCK TABLES `publisher` WRITE;
/*!40000 ALTER TABLE `publisher` DISABLE KEYS */;
INSERT INTO `publisher` VALUES (1,'Astrolabio','Italy'),(2,'Collins Crime Club','United Kingdom'),(3,'Riverhead','United States');
/*!40000 ALTER TABLE `publisher` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-10-19 23:11:28
