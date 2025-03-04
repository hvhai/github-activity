import com.codehunter.github_activity_kotlin.GhEventService
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull


class GhEventServiceTest {
    @Test
    fun getEvents() {
        val events = GhEventService().getEvents("kamranahmedse")
        events.forEach {
            println(it.buildMessage())
        }
        assertNotNull(events)
    }

}