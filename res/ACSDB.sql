--
-- MySQL 5.6.12
-- Sun, 19 Oct 2014 18:52:59 +0000
--

CREATE TABLE `drive` (
   `ID` int(11) not null auto_increment,
   `UserID` int(11) not null,
   `Service` varchar(45) not null,
   `Token` varchar(50) not null,
   PRIMARY KEY (`ID`),
   UNIQUE KEY (`UserID`),
   UNIQUE KEY (`Token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;


CREATE TABLE `useraccount` (
   `ID` int(11) not null auto_increment,
   `Username` varchar(45) not null,
   `Email` varchar(45) not null,
   `Password` varchar(45) not null,
   PRIMARY KEY (`ID`),
   UNIQUE KEY (`Username`),
   UNIQUE KEY (`Email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=2;