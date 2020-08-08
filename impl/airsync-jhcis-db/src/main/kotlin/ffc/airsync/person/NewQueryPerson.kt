package ffc.airsync.person

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.getLogger
import ffc.airsync.person.PersonDao.Lookup
import ffc.entity.Link
import ffc.entity.Person
import ffc.entity.System
import ffc.entity.ThaiCitizenId
import ffc.entity.healthcare.Behavior
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.Frequency
import ffc.entity.update
import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.sql.ResultSet
import java.sql.SQLException

class NewQueryPerson(private val jdbiDao: Dao = MySqlJdbi(null)) : PersonDao {
    private val logger = getLogger(this)

    override fun findBy(pcuCode: String, pid: String, lookup: () -> Lookup): Person {
        return jdbiDao.instant.withHandle<List<Person>, Exception> { handle ->
            handle.createQuery(
                query + """
    WHERE
	person.pcucodeperson = :pcucode AND
	person.pid = :pid
            """
            )
                .bind("pcucode", pcuCode)
                .bind("pid", pid)
                .map { rs, _ ->
                    getPersonResult(rs, lookup)
                }.list().mapNotNull { it }
        }.first()
    }

    override fun get(lookup: () -> Lookup): List<Person> {
        return jdbiDao.instant.withHandle<List<Person>, Exception> { handle ->
            handle.createQuery(query).map { rs, _ ->
                getPersonResult(rs, lookup)
            }.list().mapNotNull { it }
        }
    }

    private fun getPersonResult(
        rs: ResultSet,
        lookup: () -> Lookup
    ): Person? {
        val timestamp = DateTime(rs.getTimestamp("dateupdate")).minusHours(7)
        val pcuCode = getResult("pcucodeperson", rs)!!
        val pid = getResult("pid", rs)!!

        val personReturn = Person().update(timestamp) {

            rs.getString("idcard")?.let { identities.add(ThaiCitizenId(it)) }
            rs.getString("fname")?.let { firstname = it }
            rs.getString("lname")?.let { lastname = it }
            rs.getString("titlename")?.let { prename = it }
            rs.getString("sex")?.let {
                sex = if (it == "1") Person.Sex.MALE else Person.Sex.FEMALE
            }

            behavior = Behavior(
                rs.getString("ciga").smoke(),
                rs.getString("wisky").alcohol(),
                rs.getString("exercise").exercise(),
                rs.getString("bigaccidentever").bigaccidentever(),
                rs.getString("tonic").tonic(),
                rs.getString("habitfoming").habitfoming(),
                rs.getString("drugbyyourseft").drugbyyourseft(),
                rs.getString("sugar").sugar(),
                rs.getString("salt").salt()
            )

            try {
                rs.getDate("birth")?.let { birthDate = LocalDate.fromDateFields(it) }
            } catch (hotFix: Exception) {
                logger.warn("Hotfix Chumporn convert date", hotFix)
            }

            chronics.addAll(lookup().lookupChronic(pcuCode, pid))
            disabilities.addAll(lookup().lookupDisability(pcuCode, pid))

            death = rs.getString("deadcause")?.let { deadcause ->
                try {

                    LocalDate.fromDateFields(rs.getDate("deaddate"))?.let { deaddate ->
                        val disease = HashMap<String, Disease>()
                        disease[deadcause] = lookup().lookupDisease(deadcause)
                        rs.getString("odisease")?.let { disease[it] = lookup().lookupDisease(it) }
                        rs.getString("cdeatha")?.let { disease[it] = lookup().lookupDisease(it) }
                        rs.getString("cdeathb")?.let { disease[it] = lookup().lookupDisease(it) }
                        rs.getString("cdeathc")?.let { disease[it] = lookup().lookupDisease(it) }
                        rs.getString("cdeathd")?.let { disease[it] = lookup().lookupDisease(it) }
                        Person.Death(deaddate, disease.map { it.value })
                    }
                } catch (ex: IllegalArgumentException) {
                    logger.debug("Person deat error ${this.name}")
                    bundle["remove"] = true
                    null
                } catch (ex: SQLException) {
                    logger.error(ex) { ex.message }
                    null
                }
            }

            link = Link(System.JHICS)

            pcuCode.let { link!!.keys["pcucodeperson"] = it }
            pid.let { link!!.keys["pid"] = it }
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
                } catch (ignore: ClassCastException) {
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

        val isRemove = personReturn.bundle["remove"] ?: false

        return if (isRemove == true) null else personReturn
    }

    val query = """
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
	persondeath.odisease,

	personbehavior.ciga,
	personbehavior.wisky,
	personbehavior.exercise,
	personbehavior.bigaccidentever,
	personbehavior.tonic,
	personbehavior.habitfoming,
	personbehavior.drugbyyourseft,
	personbehavior.sugar,
	personbehavior.salt,
	personbehavior.dateupdate

FROM person
    LEFT JOIN personbehavior ON
        person.pcucodeperson=personbehavior.pcucodeperson
            AND
        person.pid=personbehavior.pid
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

    private fun String?.alcohol(): Frequency = when (this?.toInt()) {
        1 -> Frequency.NEVER
        2 -> Frequency.RARELY
        3 -> Frequency.OCCASIONALLY
        4 -> Frequency.USUALLY
        else -> Frequency.UNKNOWN
    }

    private fun String?.smoke(): Frequency = when (this?.toInt()) {
        1 -> Frequency.NEVER
        2 -> Frequency.RARELY
        3 -> Frequency.OCCASIONALLY
        4 -> Frequency.USUALLY
        else -> Frequency.UNKNOWN
    }

    private fun String?.exercise(): Frequency = when (this?.toInt()) {
        1 -> Frequency.NEVER
        2 -> Frequency.RARELY
        3 -> Frequency.OCCASIONALLY
        4 -> Frequency.USUALLY
        else -> Frequency.UNKNOWN
    }

    private fun String?.bigaccidentever(): Boolean? = when (this?.toInt()) {
        null -> null
        0, 9 -> false
        else -> true
    }

    private fun String?.tonic(): Boolean? = when (this?.toInt()) {
        null -> null
        0, 9 -> false
        else -> true
    }

    private fun String?.drugbyyourseft(): Boolean? = when (this?.toInt()) {
        null -> null
        0, 9 -> false
        else -> true
    }

    private fun String?.sugar(): Boolean? = when (this?.toInt()) {
        null -> null
        0, 9 -> false
        else -> true
    }

    private fun String?.salt(): Boolean? = when (this?.toInt()) {
        null -> null
        0, 9 -> false
        else -> true
    }

    private fun String?.habitfoming(): Boolean? {
        return when (this?.trim()) {
            null -> null
            "0", "9" -> false
            else -> true
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
