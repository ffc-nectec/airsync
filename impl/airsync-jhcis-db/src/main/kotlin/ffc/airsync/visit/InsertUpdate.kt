package ffc.airsync.visit

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface InsertUpdate {
    @SqlUpdate(
            """
        INSERT INTO `jhcisdb`.`visit` (`pcucode`, `visitno`) VALUES ( :pcuCode, :visitNumber )
    """
    )
    fun inserVisit(
        @Bind("pcuCode") pcuCode: String,
        @Bind("visitNumber") visitNumber: Long
    )

    @SqlBatch(
            """
INSERT INTO `jhcisdb`.`visit`
    (`pcucode`,
	`visitno`,
	`visitdate`,
	`pcucodeperson`,
	`pid`,
	`timeservice`,
	`timestart`,
	`timeend`,
	`symptoms`,
	`vitalcheck`,
	`weight`,
	`height`,
	`pressure`,
    `pressure2`,
	`pressurelevel`,
	`temperature`,
	`pulse`,
	`respri`,
    `username`,
    `flagservice`,
    `dateupdate`,
    `bmilevel`,
    `flag18fileexpo`,
    `rightcode`,
    `rightno`,
    `hosmain`,
    `hossub`,
    `waist`,
    `ass`)
VALUES
    (
    :pcucode,
	:visitno,
	:visitdate,
	:pcucodeperson,
	:pid,
	:timeservice,
	:timestart,
	:timeend,
	:symptoms,
	:vitalcheck,
	:weight,
	:height,
	:pressure,
	:pressure2,
	:pressurelevel,
	:temperature,
	:pulse,
	:respri,
    :username,
    :flagservice,
    :dateupdate,
    :bmilevel,
    :flag18fileexpo,
    :rightcode,
    :rightno,
    :hosmain,
    :hossub,
    :waist,
    :ass
    )
    """
    )
    fun insertVisit(@BindBean homeInsert: List<InsertData>)

    @SqlBatch(
            """
INSERT INTO `jhcisdb`.`visitdiag` (
	`pcucode`,
	`visitno`,
	`diagcode`,
	`conti`,
	`dxtype`,
	`appointdate`,
	`dateupdate`,
	`doctordiag`)
VALUES(
	:pcucode ,
	:visitno ,
	:diagcode ,
	:conti ,
	:dxtype ,
	:appointdate ,
	:dateupdate ,
	:doctordiag )
    """
    )
    fun insertVisitDiag(@BindBean insertDiagData: Iterable<InsertDiagData>)

    @SqlUpdate(
            """
INSERT INTO `jhcisdb`.`visithomehealthindividual` (
	`pcucode`,
	`visitno`,
	`homehealthtype`,
	`patientsign`,
	`homehealthdetail`,
	`homehealthresult`,
	`homehealthplan`,
	`dateappoint`,
	`user`,
	`dateupdate`)
VALUES(
	:pcucode ,
	:visitno ,
	:homehealthtype ,
	:patientsign ,
	:homehealthdetail ,
	:homehealthresult ,
	:homehealthplan ,
	:dateappoint ,
	:user ,
	:dateupdate)
    """
    )
    fun insertVitsitIndividual(@BindBean insertIndividualData: InsertIndividualData)

    @SqlUpdate(
            """
UPDATE `jhcisdb`.`visit`
	SET
		`visitdate` = :visitdate,
		`pcucodeperson` = :pcucodeperson,
		`pid` = :pid,
		`timeservice` = :timeservice,
		`timestart` = :timestart,
		`timeend` = :timeend,
		`symptoms` = :symptoms,
		`vitalcheck` = :vitalcheck,
		`weight` = :weight,
		`height` = :height,
		`pressure` = :pressure,
		`pressurelevel` = :pressurelevel,
		`temperature` = :temperature,
		`pulse` = :pulse,
		`respri` = :respri
	WHERE
		`pcucode`= :pcucode AND `visitno`= :visitno
    """
    )

    fun updateVisit(
        @BindBean homeInsert: List<InsertData>
    )
}
