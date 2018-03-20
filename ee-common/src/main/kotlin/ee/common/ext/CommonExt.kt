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

inline fun emptyOr(condition: Boolean, text: () -> String): String = if (condition) text() else ""
fun emptyOr(element: Any?): String = element?.toString() ?: ""

fun <T> T?.orEmpty(prefix: String = "",
    suffix: String = ""): String = if (this != null && (this !is String || trim().isNotEmpty())) "$prefix${this}$suffix" else ""


inline fun <T> T?.orEmpty(prefix: String = "", suffix: String = "",
    map: T.() -> String): String = if (this != null && (this !is String || trim().isNotEmpty())) "$prefix${map()}$suffix" else ""

fun Any.buildLabel(): Label = javaClass.buildLabel()

//Class
fun <T> Class<T>.buildLabel(): Label {
    val nameParts = name.replace("$", ".").split(".")
    return if (nameParts.size > 1) {
        Label(nameParts[nameParts.size - 1], nameParts[nameParts.size - 2])
    } else {
        Label(nameParts[0])
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
        it.parameterCount == 1 && it.genericParameterTypes.isNotEmpty() && !it.toString().contains("Function")
    }
}

fun Any.logger(): Logger = LoggerFactory.getLogger(javaClass)

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

fun <T> Boolean.ifElse(t: T, f: T): T = if (this) t else f

fun <T> Boolean.ifElse(t: T, f: () -> T): T = if (this) t else f()

fun <T> Boolean.ifElse(t: () -> T, f: T): T = if (this) t() else f

fun <T> Boolean.ifElse(t: () -> T, f: () -> T): T = if (this) t() else f()

fun Boolean?.then(text: String): String = if (this != null && this) text else ""

fun Boolean?.then(text: () -> String): String = if (this != null && this) text() else ""

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
fun String.fileExt(): String = substringAfterLast(".").toLowerCase()

fun String.fileName(): String = substringBeforeLast(".")

fun String.withFileNameSuffix(suffix: String): String = fileName() + suffix + "." + fileExt()

fun String.toPathString(): String = replace("\\", "/")

fun String.toDotsAsPath(): String = replace(".", "/")

fun String.relativeTo(string: String): String = replace(".", "/")

fun String.toPlural(): String = endsWith("rch").ifElse({ "${this}es" }, { "${this}s" })

fun String.toConvertUmlauts(): String = Umlauts.replaceUmlauts(this)

fun String.toKey(): String = toConvertUmlauts().replace("[^a-zA-Z0-9.]".toRegex(), "_").replace("_+".toRegex(), "_")

fun String.toUrlKey(): String = toConvertUmlauts().toLowerCase().replace("[^a-z0-9]".toRegex(), "-").replace(
        "_+".toRegex(), "-")

val strToCamelCase = WeakHashMap<String, String>()
fun String.toCamelCase(): String = strToCamelCase.getOrPut(this, {
    val parts = split('_')
    return if (parts.size > 1) {
        parts.joinToString("") { it.toLowerCase().capitalize() }.decapitalize()
    } else {
        this
    }
})

val smallLetters = Regex("[a-z]")

val strToUnderscoredUpper = WeakHashMap<String, String>()
fun String.toUnderscoredUpperCase(): String = strToUnderscoredUpper.getOrPut(this, {
    if (smallLetters.matches(this)) this.replace("(\\B[A-Z])".toRegex(), "_$1").toUpperCase()
    else this
})

val strToUnderscoredLower = WeakHashMap<String, String>()
fun String.toUnderscoredLowerCase(): String = strToUnderscoredLower.getOrPut(this, {
    if (smallLetters.matches(this)) this.replace("(\\B[A-Z])".toRegex(), "_$1").toLowerCase()
    else this.toLowerCase()
})

fun String.toHyphenLowerCase(): String = strToUnderscoredLower.getOrPut(this, {
    if (smallLetters.matches(this)) this.replace("(\\B[A-Z])".toRegex(), "-$1").toLowerCase()
    else this.toLowerCase()
})

val sqlKeywords = mapOf("group" to "group_")
val strToSql = WeakHashMap<String, String>()
fun String.toSql(limit: Int = 64): String = strToSql.getOrPut(this, {
    val base = this.toUnderscoredLowerCase()
    (base.length > limit).ifElse({
        base.replace("(?<!^)(?<!_)[qeuioajy]".toRegex(), "").replace("(\\w)\\1+".toRegex(), "$1")
    }, sqlKeywords.getOrElse(base, { base }))
})

fun <T> String.asClass(namespace: String = ""): Class<T> {
    return if (namespace.isEmpty()) {
        Class.forName(this) as Class<T>
    } else {
        Class.forName("$namespace.$this") as Class<T>
    }
}

fun <T> String.asClassInstance(namespace: String = ""): T {
    val clazz: Class<T> = asClass(namespace)
    return clazz.newInstance()
}


fun String.toIntOr0(): Int = try {
    this.toInt()
} catch (e: Exception) {
    0
}

// Date
private val longDateTimeFormat = SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS")
private val longTimeFormat = SimpleDateFormat("HH:mm:ss.SSS")

fun Date.longDateTime(): String = longDateTimeFormat.format(this)

fun Date.longTime(): String = longTimeFormat.format(this)

// File
fun File.toPathString(): String = canonicalPath.toPathString()

fun File.ext(): String = name.fileExt()

fun collectFilesByExtension(sourceList: String, fileExtension: String, delimiter: String = ";"): ArrayList<File> {
    val files = arrayListOf<File>()
    val fileValidator = { file: File -> file.name.endsWith(fileExtension, true) }
    sourceList.split(delimiter).map { Paths.get(it).toFile() }.forEach {
        if (it.isDirectory) {
            files.addAll(it.listFiles(fileValidator))
        } else if (fileValidator(it)) {
            files.add(it)
        }
    }
    return files
}

// Path
fun Path.isRegularFile(): Boolean {
    return Files.isRegularFile(this)
}

fun Path.isDirectory(): Boolean = Files.isDirectory(this)

fun Path.exists(): Boolean = Files.exists(this)

fun Path.mkdirs(): Boolean = toFile().mkdirs()

fun Path.delete() = Files.delete(this)

fun Path.deleteIfExists() = Files.deleteIfExists(this)

fun Path.deleteFilesRecursively() {
    toFile().deleteRecursively()
}

fun Path.deleteFilesRecursively(pattern: Regex) = toFile().walkTopDown().filter {
    !it.isDirectory && pattern.matches(it.name)
}.forEach { it.delete() }


fun Path.copyRecursively(target: Path) = toFile().copyRecursively(target.toFile())

fun Path.ext(): String = toString().fileExt()

fun Path.lastModified(): Date = Date(toFile().lastModified())

// Collections

fun <T> Collection<T>.joinSurroundIfNotEmptyToString(separator: CharSequence = ", ", prefix: CharSequence = "",
    postfix: CharSequence = "", emptyString: String = "",
    transform: ((T) -> CharSequence)? = null): String = joinSurroundIfNotEmptyTo(StringBuilder(), separator, prefix,
        postfix, emptyString, transform).toString()

fun <T, A : Appendable> Collection<T>.joinSurroundIfNotEmptyTo(buffer: A, separator: CharSequence = ", ",
    prefix: CharSequence = "", postfix: CharSequence = "", emptyString: String = "",
    transform: ((T) -> CharSequence)? = null): A {
    if (size > 0) {
        buffer.append(prefix)
        forEachIndexed { index, item ->
            if (index > 0) {
                buffer.append(separator)
            }
            val str = if (transform != null) transform(item) else item?.toString() ?: ""
            buffer.append(str)
        }
        buffer.append(postfix)
    } else if (emptyString.isNotEmpty()) {
        buffer.append(emptyString)
    }
    return buffer
}

fun <T> Collection<T>.joinWithIndexToString(separator: CharSequence = ", ", prefix: CharSequence = "",
    postfix: CharSequence = "", emptyString: String = "",
    transform: ((Int, T) -> CharSequence)? = null): String = joinWithIndexToString(StringBuilder(), separator, prefix,
        postfix, emptyString, transform).toString()


fun <T, A : Appendable> Collection<T>.joinWithIndexToString(buffer: A, separator: CharSequence = ", ",
    prefix: CharSequence = "", postfix: CharSequence = "", emptyString: String = "",
    transform: ((Int, T) -> CharSequence)? = null): A {
    if (size > 0) {
        buffer.append(prefix)
        forEachIndexed { index, item ->
            if (index > 0) {
                buffer.append(separator)
            }
            val str = if (transform != null) transform(index, item) else item?.toString() ?: ""
            buffer.append(str)
        }
        buffer.append(postfix)
    } else if (emptyString.isNotEmpty()) {
        buffer.append(emptyString)
    }
    return buffer
}

fun <T> Collection<T>.joinWrappedToString(separator: CharSequence = ", ", wrapIndent: CharSequence = "",
    prefix: CharSequence = "", postfix: CharSequence = "", width: Int = 120, emptyString: String = "",
    transform: ((T) -> CharSequence)? = null): String {
    return joinWrappedTo(StringBuilder(), separator, wrapIndent, prefix, postfix, width, emptyString,
            transform).toString()
}

fun <T, A : Appendable> Collection<T>.joinWrappedTo(buffer: A, separator: CharSequence = ", ",
    wrapIndent: CharSequence = "", prefix: CharSequence = "", postfix: CharSequence = "", width: Int = 120,
    emptyString: String = "", transform: ((T) -> CharSequence)? = null): A {
    if (size > 0) {
        buffer.append(prefix)
        var currentWidth = 0
        val separatorLength = separator.length
        forEachIndexed { index, item ->
            if (index > 0) {
                buffer.append(separator)
                currentWidth += separatorLength
            }
            val str = if (transform != null) transform(item) else item?.toString() ?: ""
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
    prefix: CharSequence = "", postfix: CharSequence = "", emptyString: String = "",
    transform: ((K, T) -> CharSequence)? = null): String {
    return joinSurroundIfNotEmptyTo(StringBuilder(), separator, prefix, postfix, emptyString, transform).toString()
}

fun <K, T, A : Appendable> Map<K, Collection<T>>.joinSurroundIfNotEmptyTo(buffer: A, separator: CharSequence = ", ",
    prefix: CharSequence = "", postfix: CharSequence = "", emptyString: String = "",
    transform: ((K, T) -> CharSequence)? = null): A {
    if (size > 0) {
        buffer.append(prefix)
        var count = 0
        for ((k, value) in this) {
            for (item in value) {
                if (++count > 1) {
                    buffer.append(separator)
                }
                val str = if (transform != null) transform(k, item) else item?.toString() ?: ""
                buffer.append(str)
            }
        }
        buffer.append(postfix)
    } else if (emptyString.isNotEmpty()) {
        buffer.append(emptyString)
    }
    return buffer
}

