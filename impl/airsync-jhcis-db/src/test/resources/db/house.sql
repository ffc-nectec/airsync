-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.0.51b-community-nt-log - MySQL Community Edition (GPL)
-- Server OS:                    Win32
-- HeidiSQL Version:             10.2.0.5599
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for test_query_house

-- Dumping structure for table test_query_house.house
CREATE TABLE IF NOT EXISTS `house` (
  `pcucode` char(5) NOT NULL default '',
  `hcode` int(11) NOT NULL,
  `villcode` varchar(8) NOT NULL,
  `hid` varchar(11) default NULL,
  `hno` varchar(120) default NULL,
  `road` varchar(257) default NULL,
  `pcucodepersonvola` char(5) default NULL,
  `pidvola` int(11) default NULL,
  `xgis` varchar(55) default NULL,
  `ygis` varchar(55) default NULL,
  `housepic` blob,
  `usernamedoc` varchar(35) default NULL,
  `dateupdate` datetime default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table test_query_house.house: ~1 rows (approximately)
/*!40000 ALTER TABLE `house` DISABLE KEYS */;
INSERT INTO `house` (`pcucode`, `hcode`, `villcode`, `hid`, `hno`, `road`, `pcucodepersonvola`, `pidvola`, `xgis`, `ygis`, `housepic`, `usernamedoc`, `dateupdate`) VALUES
	('07918', 1, '89876789', '12765389876', '2', 'สม', '07854', 32, '13.509755642447844', '100.02421170473099', NULL, 'ธนชัย', '2020-07-20 10:28:42'),
	('04933', 2, '83743023', '12837463528', '3', 'ใจ', '9382', 43, '13.509755642447845', '100.02421170473090', NULL, 'ทองคำ', '2020-07-20 10:32:19');
/*!40000 ALTER TABLE `house` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
