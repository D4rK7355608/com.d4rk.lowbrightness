import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DummyCoroutineTest {
    @Test
    fun testCoroutineLogic() = runTest {
        val value = suspendReturn42()
        assertEquals(42, value)
    }

    private suspend fun suspendReturn42(): Int = 42
}
