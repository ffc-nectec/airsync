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

package ffc.airsync.disease

import ffc.entity.Lang
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.Icd10
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet
import java.util.regex.Pattern

private const val queryDiesease = """
SELECT  cdisease.diseasecode,
        cdisease.mapdisease,
        cdisease.diseasename,
        cdisease.diseasenamethai,
        cdisease.code504,
        cdisease.code506,
        cdisease.codechronic,
        cdisease.codeoccupa
FROM
        cdisease

    """

interface QueryDisease {
    @SqlQuery(queryDiesease + "WHERE cdisease.diseasecode = :icd10")

    @RegisterRowMapper(GetDiseaseMapper::class)
    fun get(@Bind("icd10") icd10: String): List<Disease>
}

internal class GetDiseaseMapper : RowMapper<Disease> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): Disease {
        if (rs == null) throw ClassNotFoundException()

        val icd10 = rs.getString("diseasecode")
        val nameEn = rs.getString("diseasename")
        val nameTh = rs.getString("diseasenamethai")
        val chronicCode = rs.getString("codechronic")

        return Icd10(
            id = icd10,
            name = nameEn,
            icd10 = icd10,
            isChronic = (chronicCode != null),
            isNCD = ncdsFilter(icd10)
        ).apply {
            translation[Lang.th] = nameTh
        }
    }

    fun ncdsFilter(icd10: String): Boolean {

        val ncdFilterList = arrayListOf<String>().apply {
            add("""^e10\.\d$""")
            add("""^e11\.\d$""")
            add("""^i10$""")
        }

        ncdFilterList.forEach {
            val pattern = Pattern.compile(it, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(icd10)
            if (matcher.find()) return true
        }
        return false
    }
}
