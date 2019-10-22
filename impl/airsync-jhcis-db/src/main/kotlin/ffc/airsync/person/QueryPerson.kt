/*
 * Copyright (c) 2018 NECTEC
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

package ffc.airsync.person

import ffc.airsync.getLogger
import ffc.entity.Link
import ffc.entity.Person
import ffc.entity.System
import ffc.entity.ThaiCitizenId
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.Icd10
import ffc.entity.update
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.sql.ResultSet
import java.sql.SQLException

private val logger by lazy { getLogger(QueryPerson::class) }
private const val baseSql = """
SELECT
	person.idcard,
	person.fname,
	person.lname,
	person.hcode,
	person.pcucodeperson,
	person.birth,
	person.pid,
   person.sex,
   person.dateupdate,

	person.marystatus,
	cstatus.statusname,

	person.familyno,
	person.familyposition,
	cfamilyposition.famposname,

	person.father,
	person.fatherid,
	person.mother,
	person.motherid,
	person.mate,
	person.mateid,

	ctitle.titlename,
	`person`.`rightcode`,
	`person`.`rightno`,
	`person`.`hosmain`,
	`person`.`hossub`,

	persondeath.deadcause,
	persondeath.deaddate,
	persondeath.cdeatha,
	persondeath.cdeathb,
	persondeath.cdeathc,
	persondeath.cdeathd,
	persondeath.odisease

FROM person
	LEFT JOIN ctitle ON
		person.prename=ctitle.titlecode
	LEFT JOIN cfamilyposition ON
		person.familyposition=cfamilyposition.famposcode
	LEFT JOIN cstatus ON
		person.marystatus=cstatus.statuscode
	LEFT JOIN persondeath ON
		person.pcucodeperson=persondeath.pcucodeperson
			AND
		person.pid=persondeath.pid
"""

interface QueryPerson {

    @SqlQuery(baseSql) // WHERE hcode = 3890
    @RegisterRowMapper(PersonMapper::class)
    fun get(): List<Person>

    @SqlQuery(
        baseSql +
                """
	WHERE
		`person`.`pcucodeperson` = :pcucode AND `person`.`pid`= :pid
LIMIT 1
    """
    )
    @RegisterRowMapper(PersonMapper::class)
    fun findPerson(@Bind("pcucode") pcucode: String, @Bind("pid") pid: Long): List<Person>
}

class PersonMapper : RowMapper<Person> {

    override fun map(rs: ResultSet?, ctx: StatementContext?): Person {
        if (rs == null) throw ClassNotFoundException()
        // val statusLive = rs.getString("dischargetype")
        return Person().update(DateTime(rs.getTimestamp("dateupdate")).minusHours(7)) {

            rs.getString("idcard")?.let { identities.add(ThaiCitizenId(it)) }
            rs.getString("fname")?.let { firstname = it }
            rs.getString("lname")?.let { lastname = it }
            rs.getString("titlename")?.let { prename = it }
            rs.getString("sex")?.let {
                sex = if (it == "1") Person.Sex.MALE else Person.Sex.FEMALE
            }

            rs.getDate("birth")?.let { birthDate = LocalDate.fromDateFields(it) }

            death = rs.getString("deadcause")?.let { deadcause ->
                try {

                    LocalDate.fromDateFields(rs.getDate("deaddate"))?.let { deaddate ->
                        val disease = HashMap<String, Disease>()
                        disease[deadcause] = deadcause.toIcd10()
                        rs.getString("odisease")?.let { disease[it] = it.toIcd10() }
                        rs.getString("cdeatha")?.let { disease[it] = it.toIcd10() }
                        rs.getString("cdeathb")?.let { disease[it] = it.toIcd10() }
                        rs.getString("cdeathc")?.let { disease[it] = it.toIcd10() }
                        rs.getString("cdeathd")?.let { disease[it] = it.toIcd10() }
                        Person.Death(deaddate, disease.map { it.value })
                    }
                } catch (ex: java.lang.IllegalArgumentException) {
                    logger.debug("Person deat error ${this.name}")
                    bundle["remove"] = true
                    null
                } catch (ex: SQLException) {
                    logger.error(ex.message, ex)
                    null
                }
            }

            link = Link(System.JHICS)

            getResult("pcucodeperson", rs)?.let { link!!.keys["pcucodeperson"] = it }
            getResult("pid", rs)?.let { link!!.keys["pid"] = it }
            getResult("hcode", rs)?.let { link!!.keys["hcode"] = it }

            getResult("marystatus", rs)?.let { link!!.keys["marystatus"] = it }
            getResult("statusname", rs)?.let { link!!.keys["marystatusth"] = it }
            getResult("famposname", rs)?.let { link!!.keys["famposname"] = it }
            getResult("familyposition", rs)?.let { link!!.keys["familyposition"] = it }
            getResult("familyno", rs)?.let { link!!.keys["familyno"] = it }

            getResult("fatherid", rs)?.let { link!!.keys["fatherid"] = it }
            getResult("father", rs)?.let { link!!.keys["father"] = it }
            getResult("motherid", rs)?.let { link!!.keys["motherid"] = it }
            getResult("mother", rs)?.let { link!!.keys["mother"] = it }
            getResult("mate", rs)?.let { link!!.keys["mate"] = it }
            getResult("mateid", rs)?.let { link!!.keys["mateid"] = it }

            getResult("rightcode", rs)?.let { link!!.keys["rightcode"] = it }
            getResult("rightno", rs)?.let { link!!.keys["rightno"] = it }
            getResult("hosmain", rs)?.let { link!!.keys["hosmain"] = it }
            getResult("hossub", rs)?.let { link!!.keys["hossub"] = it }

            val bundleRemoveKey = arrayListOf<String>()
            bundle.forEach { key: String, value: Any ->
                try {
                    if ((value as String).isBlank() || value.toLowerCase() == "null") {
                        bundleRemoveKey.add(key)
                    }
                } catch (ignore: java.lang.ClassCastException) {
                }
            }

            val removeKey = arrayListOf<String>()
            link?.keys?.forEach { key, value ->
                if ((value as String).isBlank() || value.toLowerCase() == "null") {
                    removeKey.add(key)
                }
            }

            bundleRemoveKey.forEach {
                bundle.remove(it)
            }

            removeKey.forEach {
                link?.keys?.remove(it)
            }
        }
    }

    private fun getResult(column: String, rs: ResultSet): String? {
        return rs.getString(column)?.let {
            if (it.toLowerCase() == "null")
                null
            else
                it
        }
    }
}

private fun String.toIcd10() = Icd10(this, this)
