package ffc.airsync.visit.fixBug

import ffc.airsync.Dao
import ffc.airsync.extension
import ffc.airsync.utils.ignore

private const val DEEP_FIX = 5000

class FixBugDuplicate(internal val dao: Dao, internal val visitNumberError: Long) {

    fun fix() {
        val mainVisit = getVisitBy(visitNumberError)
        deleteVist(visitNumberError)
        subFix(mainVisit, visitNumberError, 1)
    }

    private fun subFix(mainVisit: VisitFixBug?, mainVisitNuber: Long, deep: Int) {
        if (deep >= DEEP_FIX) return
        val subVisitNumber = mainVisitNuber - deep
        if (subVisitNumber < 1) return
        val subVisit = getVisitBy(subVisitNumber)
        if (mainVisit == subVisit) {
            deleteVist(subVisitNumber)
            subFix(mainVisit, subVisitNumber, 1)
        } else {
            subFix(mainVisit, subVisitNumber, deep + 1)
        }
    }
}

private fun FixBugDuplicate.deleteVist(visitNumber: Long) {
    ignore { dao.extension<FixBugQuery, Unit> { deleteVisitDiag(visitNumber) } }
    ignore { dao.extension<FixBugQuery, Unit> { deleteVisitHomeHealthIndividual(visitNumber) } }
    ignore { dao.extension<FixBugQuery, Unit> { deleteF43SpecialPP(visitNumber) } }
    ignore { dao.extension<FixBugQuery, Unit> { deleteNCDs(visitNumber) } }
    ignore { dao.extension<FixBugQuery, Unit> { deleateVisit(visitNumber) } }
}

private fun FixBugDuplicate.getVisitBy(visitNumber: Long): VisitFixBug? {
    return dao.extension<FixBugQuery, List<VisitFixBug>> { getVisitBy(visitNumber) }.lastOrNull()
}
