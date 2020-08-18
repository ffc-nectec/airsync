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

import ffc.entity.healthcare.CommunityService
import ffc.entity.healthcare.HomeVisit
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.joda.time.LocalDate
import java.sql.ResultSet

private const val homeHealthQuery = """
SELECT
	visithomehealthindividual.homehealthtype,
	visithomehealthindividual.homehealthdetail,
	visithomehealthindividual.homehealthresult,
	visithomehealthindividual.dateappoint,
	visithomehealthindividual.homehealthplan,
    visithomehealthindividual.visitno
FROM
    visithomehealthindividual
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

private const val homehealthIndex = """CREATE INDEX visitnumber ON visithomehealthindividual(visitno)"""

interface HomeVisitIndividualQuery {
    @SqlUpdate(homehealthIndex)
    fun createIndex()

    @SqlQuery(
        homeHealthQuery + """
WHERE visithomehealthindividual.visitno = :visitnumber
    """
    )
    @RegisterRowMapper(VisitHomeHealthMapper::class)
    fun getBy(@Bind("visitnumber") visitnumber: Long): List<HomeVisit>

    @SqlQuery(
        homeHealthQuery + """
WHERE visithomehealthindividual.visitno IS NOT NULL
    """
    )
    @RegisterRowMapper(VisitHomeHealthMapperAll::class)
    fun getAll(): List<HashMap<Long, HomeVisit>>

    @SqlUpdate(insertVisitIndividual)
    fun insertVitsitIndividual(@BindBean insertIndividualData: InsertIndividualData)

    @SqlUpdate(updateVisitIndividual)
    fun updateVitsitIndividual(@BindBean insertIndividualData: InsertIndividualData)
}

class VisitHomeHealthMapper : RowMapper<HomeVisit> {
    override fun map(rs: ResultSet, ctx: StatementContext?): HomeVisit {
        return createHomeVisit(rs)
    }
}

class VisitHomeHealthMapperAll : RowMapper<HashMap<Long, HomeVisit>> {
    override fun map(rs: ResultSet, ctx: StatementContext?): HashMap<Long, HomeVisit> {
        return hashMapOf(rs.getLong("visitno") to createHomeVisit(rs))
    }
}

private fun createHomeVisit(rs: ResultSet): HomeVisit {
    return HomeVisit(
        serviceType = CommunityService.ServiceType(
            id = rs.getString("homehealthtype"),
            name = ""
        )
    ).apply {
        rs.getString("homehealthdetail")?.let { detail = it }
        rs.getString("homehealthresult")?.let { result = it }
        rs.getString("homehealthplan")?.let { plan = it }
        rs.getDate("dateappoint")?.let { bundle.put("dateappoint", LocalDate(it.time)) }
    }
}
