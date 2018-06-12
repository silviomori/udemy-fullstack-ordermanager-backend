-- MySQL dump 10.16  Distrib 10.1.32-MariaDB, for Win32 (AMD64)
--
-- Host: localhost    Database: order_manager
-- ------------------------------------------------------
-- Server version	10.1.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `complement` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `district` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `number` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `street` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `zip_code` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `city_id` int(11) NOT NULL,
  `customer_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpo044ng5x4gynb291cv24vtea` (`city_id`),
  KEY `FK93c3js0e22ll1xlu21nvrhqgg` (`customer_id`),
  CONSTRAINT `FK93c3js0e22ll1xlu21nvrhqgg` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FKpo044ng5x4gynb291cv24vtea` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'ap 203','Garden City','300','Flower Street','38220834',3,1),(2,'room 600','Downtown','105','Mattos Avenue','38777012',1,1);
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Computing'),(2,'Office'),(3,'Bath & Bedding'),(4,'Tech'),(5,'Garden'),(6,'Decor'),(7,'Health & Beauty');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `state_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6p2u50v8fg2y0js6djc6xanit` (`state_id`),
  CONSTRAINT `FK6p2u50v8fg2y0js6djc6xanit` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (1,'São Paulo',1),(2,'Campinas',1),(3,'Uberlândia',2);
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credit_card_payment`
--

DROP TABLE IF EXISTS `credit_card_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `credit_card_payment` (
  `payment_status` int(11) DEFAULT NULL,
  `order_id` int(11) NOT NULL,
  `installments` int(11) DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  CONSTRAINT `FK_4mjy1pep5vwx1xhyutpqs92tc` FOREIGN KEY (`order_id`) REFERENCES `order_table` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credit_card_payment`
--

LOCK TABLES `credit_card_payment` WRITE;
/*!40000 ALTER TABLE `credit_card_payment` DISABLE KEYS */;
INSERT INTO `credit_card_payment` VALUES (1,1,6),(0,3,NULL);
/*!40000 ALTER TABLE `credit_card_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customer_type` int(11) DEFAULT NULL,
  `document_number` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `email` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `name` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_dwk6cx0afu8bs9o4t536v1j5v` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,0,'36378912377','maria@gmail.com','Maria Silva');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_item` (
  `discount` double NOT NULL,
  `price` double NOT NULL,
  `quantity` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  PRIMARY KEY (`order_id`,`product_id`),
  KEY `FK551losx9j75ss5d6bfsqvijna` (`product_id`),
  CONSTRAINT `FK551losx9j75ss5d6bfsqvijna` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FK8kq6npeucy89uvw8llgpiatj6` FOREIGN KEY (`order_id`) REFERENCES `order_table` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (0,2000,1,1,1),(0,80,2,1,3),(12.5,800,1,2,2),(0,80,5,3,3),(0,300,5,3,4);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_table`
--

DROP TABLE IF EXISTS `order_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instant` datetime DEFAULT NULL,
  `address_id` int(11) DEFAULT NULL,
  `customer_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKan38srg49qpanv9u1hskl9m7p` (`address_id`),
  KEY `FKdit09e676nqbguvt1k1w8mlj2` (`customer_id`),
  CONSTRAINT `FKan38srg49qpanv9u1hskl9m7p` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `FKdit09e676nqbguvt1k1w8mlj2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_table`
--

LOCK TABLES `order_table` WRITE;
/*!40000 ALTER TABLE `order_table` DISABLE KEYS */;
INSERT INTO `order_table` VALUES (1,'2018-05-28 23:02:27',1,1),(2,'2018-06-09 23:02:27',2,1),(3,'2018-06-09 23:02:28',2,1);
/*!40000 ALTER TABLE `order_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phone`
--

DROP TABLE IF EXISTS `phone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phone` (
  `customer_id` int(11) NOT NULL,
  `phones` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  KEY `FKgvgc4f2c3qkiswyv6bbs0br1p` (`customer_id`),
  CONSTRAINT `FKgvgc4f2c3qkiswyv6bbs0br1p` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phone`
--

LOCK TABLES `phone` WRITE;
/*!40000 ALTER TABLE `phone` DISABLE KEYS */;
INSERT INTO `phone` VALUES (1,'27363323'),(1,'93838393');
/*!40000 ALTER TABLE `phone` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'Computer',2000),(2,'Printer',800),(3,'Mouse',80),(4,'Office Desk',300),(5,'Bath Towel',50),(6,'Bed Spread',200),(7,'UHD TV 55 in.',3900),(8,'Brush Cutter',800),(9,'Lampshade',100),(10,'Pendant',180),(11,'Shampoo',10);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_categories`
--

DROP TABLE IF EXISTS `product_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_categories` (
  `products_id` int(11) NOT NULL,
  `categories_id` int(11) NOT NULL,
  KEY `FK86pfomapgvxb87x9nnxuc0pdj` (`categories_id`),
  KEY `FK57esbehwbrmfec085lnwn1u5x` (`products_id`),
  CONSTRAINT `FK57esbehwbrmfec085lnwn1u5x` FOREIGN KEY (`products_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FK86pfomapgvxb87x9nnxuc0pdj` FOREIGN KEY (`categories_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_categories`
--

LOCK TABLES `product_categories` WRITE;
/*!40000 ALTER TABLE `product_categories` DISABLE KEYS */;
INSERT INTO `product_categories` VALUES (1,1),(1,4),(2,1),(2,2),(2,4),(3,1),(3,4),(4,2),(5,3),(6,3),(7,4),(8,5),(9,6),(10,6),(11,7);
/*!40000 ALTER TABLE `product_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `state`
--

DROP TABLE IF EXISTS `state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `state` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `state`
--

LOCK TABLES `state` WRITE;
/*!40000 ALTER TABLE `state` DISABLE KEYS */;
INSERT INTO `state` VALUES (1,'SP'),(2,'MG');
/*!40000 ALTER TABLE `state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket_payment`
--

DROP TABLE IF EXISTS `ticket_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ticket_payment` (
  `payment_status` int(11) DEFAULT NULL,
  `order_id` int(11) NOT NULL,
  `expiration_date` datetime DEFAULT NULL,
  `payment_date` datetime DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  CONSTRAINT `FK_cudkeph1my5dfbwae9077caom` FOREIGN KEY (`order_id`) REFERENCES `order_table` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_payment`
--

LOCK TABLES `ticket_payment` WRITE;
/*!40000 ALTER TABLE `ticket_payment` DISABLE KEYS */;
INSERT INTO `ticket_payment` VALUES (0,2,'2018-06-14 23:02:27','2018-06-09 23:02:27');
/*!40000 ALTER TABLE `ticket_payment` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-09 21:01:44
