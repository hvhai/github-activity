import com.codehunter.github_activity_kotlin.GhEventService
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test


class GhEventServiceTest {
    @Test
    fun getEvents() {
        val events = GhEventService().getEvents("8789g")
        events.forEach {
            println(it.buildMessage())
        }
    }

}