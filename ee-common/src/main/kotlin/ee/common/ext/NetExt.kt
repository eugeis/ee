package ee.common.ext

import java.net.InetAddress
import java.net.NetworkInterface

fun InetAddress.isLocalAddress(): Boolean {
    val ifaces = NetworkInterface.getNetworkInterfaces()
    while (ifaces.hasMoreElements()) {
        val iface = ifaces.nextElement()
        val addresses = iface.inetAddresses

        while (addresses.hasMoreElements()) {
            val addr = addresses.nextElement()
            if (addr.equals(this)) {
                return true
            }
        }
    }
    return false
}

fun String.toInetAddress() = InetAddress.getByName(this)
