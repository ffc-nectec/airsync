package ffc.airsync.visit

import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.Disease
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
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
    fun getDiag(@Bind("visitnumber") visitnumber: Int): List<Diagnosis>
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
