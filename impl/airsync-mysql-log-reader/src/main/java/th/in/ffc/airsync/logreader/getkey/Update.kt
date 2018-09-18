package th.`in`.ffc.airsync.logreader.getkey

import java.util.regex.Pattern

class Update(table: List<String>) : GetWhere {
    val pattern = Pattern.compile("^update .+ set .+ where +(.*)", Pattern.CASE_INSENSITIVE)

    override fun get(log: String): String {
        val query = pattern.matcher(log)
        if (query.find()) {
            return query.group(1)
        }
        return ""
    }
}
