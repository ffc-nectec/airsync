import ffc.airsync.utils.PropertyStore
import ffc.entity.User
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class PropertyStoreTest {
    val propertyStore = PropertyStore("propertyStoreTest.cnf")

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
