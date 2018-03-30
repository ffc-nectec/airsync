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

import ffc.model.PersonOrg
import org.jdbi.v3.core.config.ConfigRegistry
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.mapper.RowMapperFactory
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.config.RegisterRowMapperFactory
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.lang.reflect.Type
import java.sql.ResultSet
import java.util.*

interface QueryPerson {
    @SqlQuery("SELECT \n" +
      "person.idcard,\n" +
      "person.fname,\n" +
      "person.lname,\n" +
      "person.hcode,\n" +
      "person.pcucodeperson\n" +
      "\n" +
      "FROM person")
    @RegisterRowMapper(PersonMapper::class)
    fun getPerson() :List<PersonOrg>
}


class PersonMapper : RowMapper<PersonOrg> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): PersonOrg {

        if (rs == null) throw ClassNotFoundException()

        val idcard = rs.getString("idcard")
        val fname = rs.getString("fname")
        val lname = rs.getString("lname")
        val hcode = rs.getString("hcode")
        val pcucodeperson = rs.getString("pcucodeperson")

        return PersonOrg(fname = fname, hcode = hcode, id = idcard, lname = lname, pcucodeperson = pcucodeperson)

    }
}

