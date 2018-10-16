import ffc.airsync.api.organization.LocalOrganization
import ffc.airsync.db.DatabaseDao
import ffc.entity.House
import ffc.entity.Person
import ffc.entity.User
import ffc.entity.Village
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.HomeVisit
import ffc.entity.place.Businsess
import ffc.entity.place.School
import ffc.entity.place.Temple
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class LocalOrganizationTest {

    val propertyStore = LocalOrganization(TestDao(), "propertyStoreTest.cnf")

    @Test
    fun setAndGetProperty() {
        propertyStore.setProperty("wow", "nectec")
        propertyStore.getProperty("wow") `should be equal to` "nectec"
    }

    @Test
    fun setAndGetToken() {
        propertyStore.token = "abcdef"
        propertyStore.token `should be equal to` "abcdef"
    }

    @Test
    fun setAndGetOrgId() {
        propertyStore.orgId = "nectecabcdef"
        propertyStore.orgId `should be equal to` "nectecabcdef"
    }

    @Test
    fun setAndGetUser() {
        val user = User().apply { name = "nectec" }
        propertyStore.userOrg = user
        propertyStore.userOrg.name `should be equal to` "nectec"
    }
}

class TestDao : DatabaseDao {
    override fun getDetail(): HashMap<String, String> {
        return hashMapOf()
    }

    override fun getUsers(): List<User> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getPerson(): List<Person> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun findPerson(pcucode: String, pid: Long): Person {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getHouse(): List<House> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getHouse(whereString: String): List<House> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getChronic(): List<Chronic> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun upateHouse(house: House) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun createHomeVisit(
        homeVisit: HomeVisit,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getVillage(): List<Village> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getBusiness(): List<Businsess> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getSchool(): List<School> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getTemple(): List<Temple> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
