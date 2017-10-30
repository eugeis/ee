package ee.lang


import java.util.concurrent.TimeUnit

fun duration(amount: Long, timeUnit: TimeUnit): Attribute =
        Attribute { value(timeUnit.toMillis(amount)).type(n.Long) }

// Actually limited to 9223372036854775807 days, so unless you are very patient, it is unlimited ;-)
val unlimited = duration(amount = Long.MAX_VALUE, timeUnit = TimeUnit.DAYS)

val Long.days: Attribute
    get() = duration(this, TimeUnit.DAYS)

val Int.days: Attribute
    get() = duration(this + 0L, TimeUnit.DAYS)

val Long.hours: Attribute
    get() = duration(this, TimeUnit.HOURS)

val Int.hours: Attribute
    get() = duration(this + 0L, TimeUnit.HOURS)

val Long.microseconds: Attribute
    get() = duration(this, TimeUnit.MICROSECONDS)

val Int.microseconds: Attribute
    get() = duration(this + 0L, TimeUnit.MICROSECONDS)

val Long.minutes: Attribute
    get() = duration(this, TimeUnit.MINUTES)

val Int.minutes: Attribute
    get() = duration(this + 0L, TimeUnit.MINUTES)

val Long.milliseconds: Attribute
    get() = duration(this, TimeUnit.MILLISECONDS)

val Int.milliseconds: Attribute
    get() = duration(this + 0L, TimeUnit.MILLISECONDS)

val Long.nanoseconds: Attribute
    get() = duration(this, TimeUnit.NANOSECONDS)

val Int.nanoseconds: Attribute
    get() = duration(this + 0L, TimeUnit.NANOSECONDS)

val Long.seconds: Attribute
    get() = duration(this, TimeUnit.SECONDS)

val Int.seconds: Attribute
    get() = duration(this + 0L, TimeUnit.SECONDS)