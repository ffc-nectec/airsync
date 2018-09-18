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

package ffc.airsync.db

import ffc.airsync.utils.ncdsFilter
import ffc.entity.Lang
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.Disease
import ffc.entity.util.generateTempId
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

interface QueryChronic {
    @SqlQuery(
        """
SELECT
	personchronic.pcucodeperson,
	person.hcode,
	personchronic.chroniccode,
	personchronic.datedxfirst,
	personchronic.pid,
	cdisease.diseasecode,
	cdisease.mapdisease,
	cdisease.diseasename,
	cdisease.diseasenamethai,
	cdisease.code504,
	cdisease.code506,
	cdisease.codechronic,
	cdisease.codeoccupa
FROM person
	JOIN personchronic
		ON person.pcucodeperson=personchronic.pcucodeperson
		AND person.pid=personchronic.pid
	INNER JOIN cdisease
		ON personchronic.chroniccode=cdisease.diseasecode
ORDER BY person.hcode
            """
    )
    @RegisterRowMapper(ChronicMapper::class)
    fun get(): List<Chronic>
}

class ChronicMapper : RowMapper<Chronic> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): Chronic {
        if (rs == null) throw ClassNotFoundException()

        val disease = createDisease(rs)

        return createChronic(rs, disease)
    }

    private fun createChronic(rs: ResultSet, disease: Disease): Chronic {
        val hcode = rs.getInt("hcode")
        val hospCode = rs.getString("pcucodeperson")
        val pid = rs.getInt("pid")
        // val diagDate = LocalDate.fromDateFields(rs.getDate("datedxfirst"))

        val link = Link(
            System.JHICS,
            "hcode" to "$hcode",
            "pcucodeperson" to hospCode,
            "pid" to "$pid"
        )

        return Chronic(disease).apply {
            this.link = link
        }
    }

    private fun createDisease(rs: ResultSet): Disease {
        val icd10 = rs.getString("diseasecode")
        val nameEn = rs.getString("diseasename")
        val nameTh = rs.getString("diseasenamethai")
        val chronicCode = rs.getString("codechronic")

        val disease = Disease(
            id = generateTempId(),
            name = nameEn,
            icd10 = icd10,
            isChronic = (chronicCode != null),
            isNCD = icd10.ncdsFilter()
        ).apply {
            translation[Lang.th] = nameTh
        }
        return disease
    }
}
