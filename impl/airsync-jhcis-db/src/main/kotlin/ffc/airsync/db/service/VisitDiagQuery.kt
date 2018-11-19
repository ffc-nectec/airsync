package ffc.airsync.db.service

import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.Disease
import ffc.entity.util.generateTempId
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
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

private const val visitNumberIndex = """CREATE  INDEX visitnumber ON visitdiag(visitno)"""

interface VisitDiagQuery {
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
        return Diagnosis(
            disease = Disease(
                id = generateTempId(),
                name = rs.getString("diagcode")
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
