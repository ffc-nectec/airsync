package ffc.airsync.visit

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlUpdate

private const val insertVisit = """
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
private const val insertVisitDiag = """
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
private const val insertVisitIndividual = """
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

private const val updateVisit = """
UPDATE `jhcisdb`.`visit` SET
	`timeservice`= :timeservice,
	`timestart`= :timestart,
	`timeend`= :timeend,
	`symptoms`= :symptoms,
	`vitalcheck`= :vitalcheck,
	`weight`= :weight,
	`height`= :height,
	`pressure`= :pressure,
	`pressure2`= :pressure2,
	`pressurelevel`= :pressurelevel,
	`temperature`= :temperature,
	`pulse`= :pulse,
	`respri`= :respri,
    `username`= :username,
    `flagservice`= :flagservice,
    `dateupdate`= :dateupdate,
    `bmilevel`= :bmilevel,
    `flag18fileexpo`= :flag18fileexpo,
    `rightcode`= :rightcode,
    `rightno`= :rightno,
    `hosmain`= :hosmain,
    `hossub`= :hossub,
    `waist`= :waist,
    `ass`= :ass
WHERE
	`pcucode`= :pcucode AND `visitno`= :visitno
"""
private const val updateVisitDiag = """
UPDATE `jhcisdb`.`visitdiag` SET
	`diagcode`= :diagcode,
	`conti`= :conti,
	`dxtype`= :dxtype,
	`appointdate`= :appointdate,
	`dateupdate`= :dateupdate,
	`doctordiag`= :doctordiag

WHERE
	`pcucode`= :pcucode AND `visitno`= :visitno
"""
private const val updateVisitIndividual = """
UPDATE `jhcisdb`.`visithomehealthindividual` SET
	`homehealthtype`= :homehealthtype,
	`patientsign`= :patientsign,
	`homehealthdetail`= :homehealthdetail,
	`homehealthresult`= :homehealthresult,
	`homehealthplan`= :homehealthplan,
	`dateappoint`= :dateappoint,
	`user`= :user,
	`dateupdate`= :dateupdate

WHERE
	`pcucode`= :pcucode AND `visitno`= :visitno
"""

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

    @SqlBatch(insertVisit)
    fun insertVisit(@BindBean homeInsert: List<InsertData>)

    @SqlUpdate(updateVisit)
    fun updateVisit(@BindBean homeInsert: List<InsertData>)

    @SqlBatch(insertVisitDiag)
    fun insertVisitDiag(@BindBean insertDiagData: Iterable<InsertDiagData>)

    @SqlUpdate(updateVisitDiag)
    fun updateVisitDiag(@BindBean insertDiagData: Iterable<InsertDiagData>)

    @SqlUpdate(insertVisitIndividual)
    fun insertVitsitIndividual(@BindBean insertIndividualData: InsertIndividualData)

    @SqlUpdate(updateVisitIndividual)
    fun updateVitsitIndividual(@BindBean insertIndividualData: InsertIndividualData)
}
