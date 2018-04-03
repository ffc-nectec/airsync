/*
 * Copyright (c) 2561 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.client.client.module.daojdbi

import ffc.model.Identity
import ffc.model.Person
import ffc.model.ThaiCitizenId
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.joda.time.LocalDate
import java.sql.ResultSet

interface QueryPerson {
    @SqlQuery("SELECT " +
      "person.idcard," +
      "person.fname," +
      "person.lname," +
      "person.hcode," +
      "person.pcucodeperson," +
      "person.birth," +
      "person.pid," +
      "person.dischargetype," +
      "ctitle.titlename " +
      "FROM person LEFT JOIN ctitle ON person.prename=ctitle.titlecode")
    @RegisterRowMapper(PersonMapper::class)
    fun getPerson() :List<Person>
}


class PersonMapper : RowMapper<Person> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): Person {

        if (rs == null) throw ClassNotFoundException()

        val citizenId = rs.getString("idcard")
        val firstname = rs.getString("fname")
        val lastname = rs.getString("lname")

        val hospCode = rs.getString("pcucodeperson")

        val pid=rs.getInt("pid")
        val prename=rs.getString("titlename")
        val houseId = rs.getInt("hcode")
        val birth=rs.getDate("birth")
        val statusLive=rs.getString("dischargetype")


        val person = Person()
        person.firstname=firstname
        person.lastname=lastname
        person.hospCode=hospCode
        person.prename=prename


        //person.identities.add(ThaiCitizenId(citizenId))

        //person.birthData= LocalDate.fromDateFields(birth)
        person.pid=pid.toLong()


        return person

    }
}

