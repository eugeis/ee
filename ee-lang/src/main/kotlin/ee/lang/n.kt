package ee.lang

object n : StructureUnit({ name("native") }) {
    //types
    val Void = NativeType()
    val Any = NativeType()
    val Comparable = NativeType()
    val Path = NativeType()
    val Text = NativeType()
    val Blob = NativeType()
    val Byte = NativeType()
    val String = NativeType()
    val Boolean = NativeType()
    val Short = NativeType()
    val Int = NativeType()
    val Long = NativeType()
    val Float = NativeType()
    val Double = NativeType()
    val Date = NativeType()
    val Exception = NativeType()
    val Error = NativeType()
    val Url = NativeType()
    val UUID = NativeType()

    val UByte = NativeType()
    val UShort = NativeType()
    val UInt = NativeType()
    val ULong = NativeType()

    object TimeUnit : EnumType() {
        val Milliseconds = lit()
        val Nanoseconds = lit()
        val Microseconds = lit()
        val Seconds = lit()
        val Minutes = lit()
        val Hours = lit()
        val Days = lit()
    }

    object Class : NativeType() {
        val T = G()
    }

    object Collection : NativeType({ multi(true) }) {
        val T = G { type(String) }
    }

    object List : NativeType({ superUnit(Collection).multi(true) }) {
        val T = G { type(String) }
    }

    object Map : NativeType({ multi(true) }) {
        val K = G { type(String) }
        val V = G { type(String) }
    }

    object ItemEmpty : ExternalType({ namespace("ee.lang") })
}