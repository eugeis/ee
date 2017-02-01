package ee.asm

internal interface ReflectionUtils {

    fun <T> initializeClass(clazz: Class<out T>): T {
        try {
            val constructor = clazz.getConstructor()
            return constructor.newInstance()
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Can't initialize class ${clazz.getName()}, no <init>()", e)
        }
    }

    fun <T> initializeClassWithArgs(clazz: Class<out T>, vararg args: Pair<Any, Class<*>>): T {
        // https://youtrack.jetbrains.com/issue/KT-5793
        val argList = args.map { it.first }.toTypedArray()
        val argTypes = args.map { it.second }.toTypedArray()

        try {
            val constructor = clazz.getConstructor(*argTypes)
            return constructor.newInstance(*argList)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Can't initialize class ${clazz.getName()}, no <init>(${argTypes.joinToString()})", e)
        }
    }


}