package ffc.airsync.utils

import ffc.airsync.getLogger
import me.piruin.geok.geometry.Point
import org.jdbi.v3.core.result.ResultSetException
import java.sql.ResultSet

private interface GetLocation

private val logger = getLogger(GetLocation::class)
fun getLocation(rs: ResultSet): Point? {
    return try {
        val xgis = rs.getDouble("xgis")
        val ygis = rs.getDouble("ygis")
        if ((xgis != 0.0) && (ygis != 0.0))
            if (xgis < ygis)
                Point(xgis, ygis)
            else
                Point(ygis, xgis)
        else
            null
    } catch (ex: ResultSetException) {
        logger.error("xgix, ygis error", ex)
        null
    }
}
