/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package ffc.airsync.visit

import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.Disease
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.ResultSet

private const val visitDiagQuery = """
SELECT
	visitdiag.diagcode,
	visitdiag.conti,
	visitdiag.dxtype,
	visitdiag.appointdate,
	visitdiag.dateupdate,
	visitdiag.doctordiag
FROM
    visitdiag
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

private const val visitNumberIndex = """CREATE INDEX visitnumber ON visitdiag(visitno)"""

interface VisitDiagQuery {

    @SqlUpdate(visitNumberIndex)
    fun createIndex()

    @SqlQuery(
        visitDiagQuery + """
        WHERE visitdiag.visitno = :visitnumber
    """
    )
    @RegisterRowMapper(VisitDiagMapper::class)
    fun getDiag(@Bind("visitnumber") visitnumber: Long): List<Diagnosis>

    @SqlBatch(insertVisitDiag)
    fun insertVisitDiag(@BindBean insertDiagData: Iterable<InsertDiagData>)

    @SqlUpdate(updateVisitDiag)
    fun updateVisitDiag(@BindBean insertDiagData: InsertDiagData)
}

class VisitDiagMapper : RowMapper<Diagnosis> {
    override fun map(rs: ResultSet, ctx: StatementContext?): Diagnosis {
        val icd10 = rs.getString("diagcode")
        return Diagnosis(
            disease = Disease(
                id = icd10,
                name = ""
            ),
            dxType = when (rs.getString("dxtype")) {
                "01" -> Diagnosis.Type.PRINCIPLE_DX
                "02" -> Diagnosis.Type.CO_MORBIDITY
                "03" -> Diagnosis.Type.COMPLICATION
                "04" -> Diagnosis.Type.OTHER
                else -> Diagnosis.Type.EXTERNAL_CAUSE
            },
            isContinued = rs.getString("conti") == "1"
        )
    }
}
