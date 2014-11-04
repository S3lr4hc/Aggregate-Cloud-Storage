-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 04, 2014 at 04:01 PM
-- Server version: 5.6.12-log
-- PHP Version: 5.4.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `acs`
--
CREATE DATABASE IF NOT EXISTS `acs` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `acs`;

-- --------------------------------------------------------

--
-- Table structure for table `drive`
--

CREATE TABLE IF NOT EXISTS `drive` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) NOT NULL,
  `Service` varchar(45) NOT NULL,
  `Token` varchar(50) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UserID` (`UserID`),
  UNIQUE KEY `Token` (`Token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `useraccount`
--

CREATE TABLE IF NOT EXISTS `useraccount` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(45) NOT NULL,
  `Email` varchar(45) NOT NULL,
  `Password` varchar(45) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Username` (`Username`),
  UNIQUE KEY `Email` (`Email`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `useraccount`
--

--
-- Constraints for dumped tables
--

--
-- Constraints for table `drive`
--
ALTER TABLE `drive`
  ADD CONSTRAINT `fk_UserAccount_Drive_UserID` FOREIGN KEY (`UserID`) REFERENCES `useraccount` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
