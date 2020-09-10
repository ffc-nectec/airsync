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

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.Icd10
import java.sql.ResultSet
import java.util.SortedMap

internal class NewVisitDiagQuery(
    private val jdbiDao: Dao = MySqlJdbi(null),
    private val lookup: () -> Lookup
) {
    private val sql = """
SELECT
	visitdiag.diagcode,
	visitdiag.conti,
	visitdiag.dxtype,
	visitdiag.appointdate,
	visitdiag.dateupdate,
	visitdiag.doctordiag,
    visitdiag.visitno
FROM
    visitdiag
"""

    interface Lookup {
        fun lookupIcd10(icd10: String): Icd10
    }

    fun get(): SortedMap<Long, List<Diagnosis>> {
        val output = hashMapOf<Long, ArrayList<Diagnosis>>()
        jdbiDao.instant.withHandle<List<Pair<Long, Diagnosis>>, Exception> { handle ->
            handle.createQuery(sql).map { rs, _ ->
                rs.getVisitNo() to rs.getDiagnosis()
            }.list()
        }.forEach {
            val get = output[it.first]
            if (get == null) output[it.first] = arrayListOf()
            output[it.first]!!.add(it.second)
        }
        return output.toSortedMap()
    }

    private fun ResultSet.getVisitNo(): Long {
        return this.getLong("visitno")
    }

    private fun ResultSet.getDiagnosis(): Diagnosis {
        val icd10 = getString("diagcode")
        return Diagnosis(
            disease = lookup().lookupIcd10(icd10),
            dxType = when (getString("dxtype")) {
                "01" -> Diagnosis.Type.PRINCIPLE_DX
                "02" -> Diagnosis.Type.CO_MORBIDITY
                "03" -> Diagnosis.Type.COMPLICATION
                "04" -> Diagnosis.Type.OTHER
                else -> Diagnosis.Type.EXTERNAL_CAUSE
            },
            isContinued = getString("conti") == "1"
        )
    }
}
