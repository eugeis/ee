package ee.common.ext

import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.InetAddress

class NetExtTest {

    @Test
    fun testIsLocalAddress() {
        val inet = InetAddress.getLocalHost()
        assertTrue(inet.isLocalAddress())
    }
}
