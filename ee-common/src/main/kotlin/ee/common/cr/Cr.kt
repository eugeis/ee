package ee.common.cr

import kotlinx.coroutines.delay

suspend fun waitFor(
        times: Int = Int.MAX_VALUE,
        initialDelay: Long = 1000, // 1 second
        maxDelay: Long = 10000,    // 10 second
        factor: Double = 2.0,
        check: () -> Boolean): Boolean {
    var currentDelay = initialDelay
    repeat(times - 1) {
        if (check()) {
            return true
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return false
}