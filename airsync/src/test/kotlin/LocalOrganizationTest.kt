import ffc.airsync.api.organization.LocalOrganization
import ffc.airsync.db.DatabaseDao
import ffc.entity.Person
import ffc.entity.Template
import ffc.entity.User
import ffc.entity.Village
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.CommunityService
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import ffc.entity.healthcare.SpecialPP
import ffc.entity.place.Business
import ffc.entity.place.House
import ffc.entity.place.ReligiousPlace
import ffc.entity.place.School
import org.amshove.kluent.`should be equal to`
import org.junit.After
import org.junit.Test
import java.io.File

class LocalOrganizationTest {

    val propertyStore = LocalOrganization(TestDao(), "propertyStoreTest.cnf")

    @After
    fun tearDown() {
        File("propertyStoreTest.cnf").deleteOnExit()
    }

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

    override fun getDatabaseLocaion(): File {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun init() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
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

    override fun getHouse(lookupVillage: (jVillageId: String) -> Village?, whereString: String): List<House> {
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
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ): HealthCareService {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun queryMaxVisit(): Long {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getVillage(): List<Village> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getBusiness(): List<Business> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getSchool(): List<School> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getTemple(): List<ReligiousPlace> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getHealthCareService(
        lookupPatientId: (pid: String) -> String,
        lookupProviderId: (name: String) -> String,
        lookupDisease: (icd10: String) -> Disease?,
        lookupSpecialPP: (ppCode: String) -> SpecialPP.PPType?,
        lookupServiceType: (serviceId: String) -> CommunityService.ServiceType?,
        whereString: String,
        progressCallback: (Int) -> Unit
    ): List<HealthCareService> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun updateHomeVisit(
        homeVisit: HomeVisit,
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ): HealthCareService {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getTemplate(): List<Template> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
