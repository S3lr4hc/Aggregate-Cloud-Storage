-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 07, 2015 at 02:22 PM
-- Server version: 5.6.12-log
-- PHP Version: 5.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


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
-- Table structure for table `complementaryrestriction`
--

CREATE TABLE IF NOT EXISTS `complementaryrestriction` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) NOT NULL,
  `DocsChecked` tinyint(1) NOT NULL DEFAULT '0',
  `PresentationChecked` tinyint(1) NOT NULL DEFAULT '0',
  `SpreadSheetChecked` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UserID` (`UserID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `complementaryrestriction`
--

INSERT INTO `complementaryrestriction` (`ID`, `UserID`, `DocsChecked`, `PresentationChecked`, `SpreadSheetChecked`) VALUES
(1, 1, 1, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `customrestriction`
--

CREATE TABLE IF NOT EXISTS `customrestriction` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) NOT NULL,
  `FileType` varchar(10) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `UserID` (`UserID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `customrestriction`
--

INSERT INTO `customrestriction` (`ID`, `UserID`, `FileType`) VALUES
(1, 1, '.mp3'),
(2, 1, '.c'),
(3, 1, '.exe');

-- --------------------------------------------------------

--
-- Table structure for table `drive`
--

CREATE TABLE IF NOT EXISTS `drive` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) NOT NULL,
  `Service` varchar(45) NOT NULL,
  `Token` varchar(250) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Token` (`Token`),
  UNIQUE KEY `ID` (`ID`),
  KEY `UserID` (`UserID`),
  KEY `UserID_2` (`UserID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=67 ;

--
-- Dumping data for table `drive`
--

INSERT INTO `drive` (`ID`, `UserID`, `Service`, `Token`) VALUES
(5, 1, 'Dropbox', 'g-6oqOysVeQAAAAAAAAAD7clr5vAEPeL52bJwtqdaxajAbY41zPlG_dhSxxsusdq'),
(6, 1, 'Google Drive', '1/yirW8AUUIEBYrMzfDKDIaSXJCTDQhFg5v7C2xdPcmX0MEudVrK5jSpoR30zcRFq6');

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

INSERT INTO `useraccount` (`ID`, `Username`, `Email`, `Password`) VALUES
(1, 'ChriSmith', 'sm1thchr1s9231@gmail.com', '123456');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `complementaryrestriction`
--
ALTER TABLE `complementaryrestriction`
  ADD CONSTRAINT `complementaryrestriction_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `useraccount` (`ID`);

--
-- Constraints for table `customrestriction`
--
ALTER TABLE `customrestriction`
  ADD CONSTRAINT `customrestriction_ibfk_1` FOREIGN KEY (`UserID`) REFERENCES `useraccount` (`ID`);

--
-- Constraints for table `drive`
--
ALTER TABLE `drive`
  ADD CONSTRAINT `fk_UserAccount_Drive_UserID` FOREIGN KEY (`UserID`) REFERENCES `useraccount` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
