package ee.common

open class EeAny {
    companion object {
        val TYPE_NAME_SEPARATOR = "#"
        val FIELD_SEPARATOR = ","
        val MAX_LENGTH_TO_STRING = 254
    }

    protected open fun fillToString(buffer: StringBuffer): StringBuffer {
        return buffer
    }

    override fun toString(): String {
        var b = StringBuffer()
        fillToStringType(b)
        fillToStringIdentity(b)
        b.append("[")
        fillToString(b)
        if (b.length > MAX_LENGTH_TO_STRING) {
            b.setLength(MAX_LENGTH_TO_STRING)
        }
        b.append("]")
        return b.toString()
    }

    protected open fun fillToStringType(b: StringBuffer) {
        b.append(getTypeName())
    }

    protected open fun fillToStringIdentity(b: StringBuffer) {
        b.append("@").append(Integer.toHexString(hashCode()))
    }

    protected open fun getTypeName(): String {
        return javaClass.simpleName
    }
}

data class Label(val name: String, val category: String = "")
