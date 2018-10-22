package ee.common.ext

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.InetAddress

class NetExtTest {

    @Test
    fun testIsLocalAddress() {
        val inet = InetAddress.getLocalHost()
        assertTrue(inet.isLocalAddress())
    }
}
