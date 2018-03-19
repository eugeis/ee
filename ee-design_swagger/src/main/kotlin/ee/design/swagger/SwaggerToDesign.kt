package ee.design.swagger

import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.common.ext.toUnderscoredUpperCase
import ee.design.Basic
import ee.design.Module
import ee.lang.TypeI
import ee.lang.doc
import ee.lang.n
import ee.lang.nL
import io.swagger.models.Swagger
import io.swagger.models.properties.*
import io.swagger.parser.SwaggerParser
import org.slf4j.LoggerFactory
import java.nio.file.Path

private val log = LoggerFactory.getLogger("SwaggerToDesign")

val TYPE_LINK = "Link"

data class DslTypes(val name: String, val types: Map<String, String>)

class SwaggerToDesign {
    fun toDslTypes(swaggerFile: Path): DslTypes {
        val swagger = SwaggerParser().read(swaggerFile.toString())
        return swagger.toDslTypes()
    }
}

fun Swagger.toDslTypes(): DslTypes {
    val ret = mutableMapOf<String, String>()
    definitions?.entries?.forEach {
        it.value.toDslPropertyEnums(ret)
        ret[it.key] = it.value.toDslBasic(it.key)
    }
    return DslTypes(name = info?.title ?: "", types = ret)
}

fun Swagger.toModule(): Module {
    return Module {
        name("Shared")

        definitions?.forEach { defName, def ->
            basic(def.toBasic(defName))
        }
    }.init()
}

fun io.swagger.models.Model.toDslBasic(name: String): String {
    return """
object ${name.toDslTypeName()} : Basic(${description?.isNotEmpty().then(
            { "{doc(\"\"\"$description\"\"\")}" })}) {${properties?.entries?.isNotEmpty().then(
            { properties.entries.joinToString(nL, nL) { it.value.toDslTypes(it.key) } })}
}"""
}

fun io.swagger.models.Model.toDslPropertyEnums(typesToFill: MutableMap<String, String>) {
    properties?.entries?.filter {
        val v = it.value
        v is StringProperty && v.enum != null && v.enum.isNotEmpty() && !typesToFill.containsKey(it.key)
    }?.forEach {
        val prop = it.value as StringProperty
        val enumName = it.key.toDslTypeName()
        typesToFill[enumName] = prop.toDslEnum(enumName)
    }
}

fun io.swagger.models.properties.StringProperty.toDslEnum(name: String): String {
    return """
object $name : EnumType() {
    val value = prop(n.String)${enum.joinToString(nL, nL) {
        "    val ${it.toCamelCase().toLowerCase()} = lit(value,\"$it\")"
    }}
}"""
}

fun io.swagger.models.properties.Property.toDslTypes(name: String): String {
    return "    val $name = prop { ${toDslInit(name)} }"
}

fun io.swagger.models.properties.Property.toDslType(name: String): String {
    val prop = this

    return when (prop) {
        is ArrayProperty       -> {
            "n.List.GT(${prop.items.toDslType(name)})"
        }
        is BinaryProperty      -> {
            "n.Bytes"
        }
        is BooleanProperty     -> {
            "n.Boolean"
        }
        is ByteArrayProperty   -> {
            "n.Bytes"
        }
        is DateProperty        -> {
            "n.Date"
        }
        is DateTimeProperty    -> {
            "n.Date"
        }
        is DecimalProperty     -> {
            "n.Double"
        }
        is DoubleProperty      -> {
            "n.Double"
        }
        is EmailProperty       -> {
            "n.String"
        }
        is FileProperty        -> {
            "n.File"
        }
        is FloatProperty       -> {
            "n.Float"
        }
        is IntegerProperty     -> {
            "n.Int"
        }
        is BaseIntegerProperty -> {
            "n.Int"
        }
        is LongProperty        -> {
            "n.Long"
        }
        is MapProperty         -> {
            "n.Map"
        }
        is ObjectProperty      -> {
            if (prop.properties.size == 1 && prop.properties.keys.first() == "href") {
                TYPE_LINK
            } else {
                "n.Object"
            }
        }
        is PasswordProperty    -> {
            "n.String"
        }
        is UntypedProperty     -> {
            "n.String"
        }
        is UUIDProperty        -> {
            "n.String"
        }
        is RefProperty         -> {
            prop.simpleRef.toDslTypeName()
        }
        is StringProperty      -> {
            if (prop.enum != null && prop.enum.isNotEmpty()) {
                name.toDslTypeName()
            } else {
                "n.String"
            }
        }
        else                   -> {
            ""
        }
    }
}

fun String.toDslTypeName(): String {
    return if (this == "rel.self") {
        TYPE_LINK
    } else {
        capitalize()
    }
}

fun io.swagger.models.properties.Property.toDslInit(name: String): String {
    return "type(${toDslType(name)})${required?.not().then({ ".nullable(true)" })}${(this is PasswordProperty).then(
            { ".hidden(true)" })}${(this is StringProperty && default.isNullOrEmpty().not()).then {
        ".value(\"${(this as StringProperty).default}\")"
    }}${description?.isNotEmpty().then({ ".doc(\"\"\"$description\"\"\")" })}"
}

fun io.swagger.models.Model.toBasic(name: String): Basic {
    val model = this
    return Basic {
        name(name).doc(model.description ?: "")
        model.properties?.forEach { propName, p ->
            prop { name(propName).type(p.type.toType()).doc(p.description ?: "") }
        }
    }.init()
}

fun String?.toType(): TypeI<*> {
    return if (this == null) {
        n.String
    } else {
        log.info("type: {}", this)
        n.String
    }
}