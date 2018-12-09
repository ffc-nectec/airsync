package th.`in`.ffc.airsync.logreader.getkey

import java.util.regex.Pattern

class Insert : GetWhere {
    val pattern = Pattern.compile(
        """^.*insert into[ `\w\d]+\(([ `,\w\d]+)\)[ \t]+?values[ \t]+?\((.+)\) *${'$'}""",
        Pattern.CASE_INSENSITIVE
    )

    override fun get(log: String): List<String> {
        val query = pattern.matcher(log)
        if (query.find()) {
            return listOf(query.group(1), query.group(2))
        }
        return emptyList()
    }
}
