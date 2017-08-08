package ee.common.ext

import ee.common.EeAny
import ee.common.Label
import ee.common.Umlauts
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import java.lang.reflect.Constructor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*

inline fun emptyOr(condition: Boolean, text: () -> String): String {
    if (condition) return text() else return ""
}

fun emptyOr(element: Any?): String {
    if (element != null) return element.toString() else return ""
}

fun <T> T?.orEmpty(prefix: String = "", suffix: String = ""): String =
        if (this != null && (this !is String || this.trim().isNotEmpty())) "$prefix${this}$suffix" else ""


inline fun <T> T?.orEmpty(prefix: String = "", suffix: String = "", map: T.() -> String): String =
        if (this != null && (this !is String || this.trim().isNotEmpty())) "$prefix${this.map()}$suffix" else ""

fun Any.buildLabel(): Label {
    return javaClass.buildLabel()
}

//Class
fun <T> Class<T>.buildLabel(): Label {
    val nameParts = this.name.replace("$", ".").split(".")
    if (nameParts.size > 1) {
        return Label(nameParts[nameParts.size - 1], nameParts[nameParts.size - 2])
    } else {
        return Label(nameParts[0])
    }
}


fun <T> Class<T>.location(): Path {
    val url = this.getResource("$simpleName.class")
    var pathString = url.file.substringAfter("file:").substringBefore("!")
    //remove first '/' for windows paths
    if (pathString.contains(":")) {
        pathString = pathString.substring(1)
    }
    return Paths.get(pathString)
}

fun <T> Class<T>.declaredConstuctorWithOneGenericType(): Constructor<*>? {
    return declaredConstructors.find {
        it.parameterCount == 1 &&
                it.genericParameterTypes.isNotEmpty() &&
                !it.toString().contains("Function")
    }
}

fun Any.logger(): Logger {
    return LoggerFactory.getLogger(javaClass)
}

fun <T, R> T.letTraceExc(debugLevel: Boolean = true, block: (T) -> R): R? = try {
    let(block)
} catch (e: Exception) {
    if (debugLevel) {
        this?.logger()?.debug("letTraceExc caused in {} an exception {}", this, e)
    } else {
        this?.logger()?.info("letTraceExc caused in {} an exception {}", this, e)
    }
    null
}

fun <T> Boolean.ifElse(t: T, f: T): T {
    return if (this) t else f
}

fun <T> Boolean.ifElse(t: T, f: () -> T): T {
    return if (this) t else f()
}

fun <T> Boolean.ifElse(t: () -> T, f: T): T {
    return if (this) t() else f
}

fun <T> Boolean.ifElse(t: () -> T, f: () -> T): T {
    return if (this) t() else f()
}

fun Boolean?.then(text: String): String =
        if (this != null && this) text else ""

fun Boolean?.then(text: () -> String): String =
        if (this != null && this) text() else ""

//System
val osName: String = System.getProperty("os.name")
var eeAppHome: String = {
    var ret = System.getProperty("ee.app.home", "")
    if (ret.isEmpty()) {
        ret = EeAny::class.java.protectionDomain.codeSource.location.path.substringBeforeLast("/lib", "")
    }
    println("ee.app.home: $ret")
    ret
}()

val isWindows: Boolean = osName.toLowerCase().contains("windows")
val isMac: Boolean = osName.toLowerCase().contains("mac")
val isLinux: Boolean = osName.toLowerCase().contains("linux")

val executableFileExtension = if (isWindows) ".exe" else ""

//Serializable

fun <T : Serializable> T.deepCopy(): T {
    var oos: ObjectOutputStream? = null
    var ois: ObjectInputStream? = null
    try {
        val bos = ByteArrayOutputStream()
        oos = ObjectOutputStream(bos)

        oos.writeObject(this)
        oos.flush()

        val bin = ByteArrayInputStream(bos.toByteArray())
        ois = ObjectInputStream(bin)

        return ois.readObject() as T
    } catch (e: Exception) {
        println("Exception in ObjectCloner = " + e)
        throw e
    } finally {
        oos?.close()
        ois?.close()
    }
}

//String
fun String.fileExt(): String {
    return this.substringAfterLast(".").toLowerCase()
}

fun String.fileName(): String {
    return this.substringBeforeLast(".")
}

fun String.toPathString(): String {
    return this.replace("\\", "/")
}

fun String.toDotsAsPath(): String {
    return this.replace(".", "/")
}

fun String.toConvertUmlauts(): String {
    return Umlauts.replaceUmlauts(this)
}

fun String.toKey(): String {
    return toConvertUmlauts().replace("[^a-zA-Z0-9.]".toRegex(), "_").replace("_+".toRegex(), "_")
}

fun String.toUrlKey(): String {
    return toConvertUmlauts().toLowerCase().replace("[^a-z0-9]".toRegex(), "-").replace("_+".toRegex(), "-")
}

val strToCamelCase = WeakHashMap<String, String>()
fun String.toCamelCase(): String {
    return strToCamelCase.getOrPut(this, { this.replace("_\\w".toRegex()) { it.value[1].toUpperCase().toString() } })
}

val strToUnderscoredUpper = WeakHashMap<String, String>()
fun String.toUnderscoredUpperCase(): String {
    return strToUnderscoredUpper.getOrPut(this, { this.replace("(\\B[A-Z])".toRegex(), "_$1").toUpperCase() })
}

val strToUnderscoredLower = WeakHashMap<String, String>()
fun String.toUnderscoredLowerCase(): String {
    return strToUnderscoredLower.getOrPut(this, { this.replace("(\\B[A-Z])".toRegex(), "_$1").toLowerCase() })
}

fun String.toHyphenLowerCase(): String {
    return strToUnderscoredLower.getOrPut(this, { this.replace("(\\B[A-Z])".toRegex(), "-$1").toLowerCase() })
}

val sqlKeywords = mapOf("group" to "group_")
val strToSql = WeakHashMap<String, String>()
fun String.toSql(limit: Int = 64): String {
    return strToSql.getOrPut(this, {
        val base = this.toUnderscoredLowerCase()
        (base.length > limit).ifElse({
            base.replace("(?<!^)(?<!_)[qeuioajy]".toRegex(), "").replace("(\\w)\\1+".toRegex(), "$1")
        }, sqlKeywords.getOrElse(base, { base }))
    })
}

fun <T> String.asClass(namespace: String = ""): Class<T> {
    if (namespace.isEmpty()) {
        return Class.forName(this) as Class<T>
    } else {
        return Class.forName("$namespace.$this") as Class<T>
    }
}

fun <T> String.asClassInstance(namespace: String = ""): T {
    val clazz: Class<T>
    clazz = asClass(namespace)
    return clazz.newInstance()
}


fun String.toIntOr0(): Int {
    try {
        return this.toInt()
    } catch (e: Exception) {
        return 0
    }
}

// Date
private val longDateTimeFormat = SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS")
private val longTimeFormat = SimpleDateFormat("HH:mm:ss.SSS")

fun Date.longDateTime(): String {
    return longDateTimeFormat.format(this)
}

fun Date.longTime(): String {
    return longTimeFormat.format(this)
}

// File
fun File.toPathString(): String {
    return this.canonicalPath.toPathString()
}

fun File.ext(): String {
    return this.name.fileExt()
}

// Path
fun Path.isRegularFile(): Boolean {
    return Files.isRegularFile(this)
}

fun Path.isDirectory(): Boolean {
    return Files.isDirectory(this)
}

fun Path.exists(): Boolean {
    return Files.exists(this)
}

fun Path.mkdirs(): Boolean {
    return toFile().mkdirs()
}

fun Path.delete() {
    Files.delete(this)
}

fun Path.deleteIfExists() {
    Files.deleteIfExists(this)
}

fun Path.deleteFilesRecursively() {
    toFile().deleteRecursively()
}

fun Path.deleteFilesRecursively(pattern: Regex) {
    toFile().walkTopDown().filter {
        !it.isDirectory && pattern.matches(it.name)
    }.forEach { it.delete() }
}

fun Path.copyRecursively(target: Path) {
    toFile().copyRecursively(target.toFile())
}

fun Path.ext(): String {
    return this.toString().fileExt()
}

fun Path.lastModified(): Date {
    return Date(toFile().lastModified())
}

// Collections

fun <T> Collection<T>.joinSurroundIfNotEmptyToString(separator: CharSequence = ", ",
                                                     prefix: CharSequence = "", postfix: CharSequence = "",
                                                     emptyString: String = "", transform: ((T) -> CharSequence)? = null): String {
    return joinSurroundIfNotEmptyTo(StringBuilder(), separator, prefix, postfix, emptyString, transform).toString()
}

fun <T, A : Appendable> Collection<T>.joinSurroundIfNotEmptyTo(buffer: A, separator: CharSequence = ", ",
                                                               prefix: CharSequence = "", postfix: CharSequence = "",
                                                               emptyString: String = "", transform: ((T) -> CharSequence)? = null): A {
    if (size > 0) {
        buffer.append(prefix)
        var count = 0
        for (element in this) {
            if (++count > 1) {
                buffer.append(separator)
            }
            val str = if (transform != null) transform(element) else if (element == null) "" else element.toString()
            buffer.append(str)
        }
        buffer.append(postfix)
    } else if (emptyString.isNotEmpty()) {
        buffer.append(emptyString)
    }
    return buffer
}

fun <T> Collection<T>.joinWithIndexToString(separator: CharSequence = ", ",
                                            prefix: CharSequence = "", postfix: CharSequence = "",
                                            emptyString: String = "", transform: ((Int, T) -> CharSequence)? = null): String {
    return joinWithIndexToString(StringBuilder(), separator, prefix, postfix, emptyString, transform).toString()
}

fun <T, A : Appendable> Collection<T>.joinWithIndexToString(buffer: A, separator: CharSequence = ", ",
                                                            prefix: CharSequence = "", postfix: CharSequence = "",
                                                            emptyString: String = "", transform: ((Int, T) -> CharSequence)? = null): A {
    if (size > 0) {
        buffer.append(prefix)
        var count = 0
        for (element in this) {
            if (count > 0) {
                buffer.append(separator)
            }
            val str = if (transform != null) transform(count, element) else if (element == null) "" else element.toString()
            buffer.append(str)
            count++
        }
        buffer.append(postfix)
    } else if (emptyString.isNotEmpty()) {
        buffer.append(emptyString)
    }
    return buffer
}

fun <T> Collection<T>.joinWrappedToString(separator: CharSequence = ", ", wrapIndent: CharSequence = "",
                                          prefix: CharSequence = "", postfix: CharSequence = "",
                                          width: Int = 120, emptyString: String = "", transform: ((T) -> CharSequence)? = null): String {
    return joinWrappedTo(StringBuilder(), separator, wrapIndent, prefix, postfix, width, emptyString, transform).toString()
}

fun <T, A : Appendable> Collection<T>.joinWrappedTo(buffer: A, separator: CharSequence = ", ", wrapIndent: CharSequence = "",
                                                    prefix: CharSequence = "", postfix: CharSequence = "",
                                                    width: Int = 120, emptyString: String = "", transform: ((T) -> CharSequence)? = null): A {
    if (size > 0) {
        buffer.append(prefix)
        var count = 0
        var currentWidth = 0
        val separatorLength = separator.length
        for (element in this) {
            if (++count > 1) {
                buffer.append(separator)
                currentWidth += separatorLength
            }
            val str = if (transform != null) transform(element) else if (element == null) "" else element.toString()
            if (currentWidth + str.length > width) {
                buffer.appendln().append(wrapIndent)
                currentWidth = wrapIndent.length
            }
            currentWidth += str.length
            buffer.append(str)
        }
        buffer.append(postfix)
    } else if (emptyString.isNotEmpty()) {
        buffer.append(emptyString)
    }
    return buffer
}

fun <T> MutableCollection<T>.addReturn(item: T): T {
    this.add(item)
    return item
}

fun Boolean?.setAndTrue(): Boolean = this != null && this
fun Boolean?.notSetOrTrue(): Boolean = this == null || this


//map
fun <K, T> Map<K, Collection<T>>.joinSurroundIfNotEmptyToString(separator: CharSequence = ", ",
                                                                prefix: CharSequence = "", postfix: CharSequence = "",
                                                                emptyString: String = "", transform: ((K, T) -> CharSequence)? = null): String {
    return joinSurroundIfNotEmptyTo(StringBuilder(), separator, prefix, postfix, emptyString, transform).toString()
}

fun <K, T, A : Appendable> Map<K, Collection<T>>.joinSurroundIfNotEmptyTo(buffer: A, separator: CharSequence = ", ",
                                                                          prefix: CharSequence = "", postfix: CharSequence = "",
                                                                          emptyString: String = "", transform: ((K, T) -> CharSequence)? = null): A {
    if (size > 0) {
        buffer.append(prefix)
        var count = 0
        for ((k, value) in this) {
            for (element in value) {
                if (++count > 1) {
                    buffer.append(separator)
                }
                val str = if (transform != null) transform(k, element) else if (element == null) "" else element.toString()
                buffer.append(str)
            }
        }
        buffer.append(postfix)
    } else if (emptyString.isNotEmpty()) {
        buffer.append(emptyString)
    }
    return buffer
}

