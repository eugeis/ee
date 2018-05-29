package ee.design.swagger

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.Module
import ee.lang.*
import io.swagger.models.ArrayModel
import io.swagger.models.ComposedModel
import io.swagger.models.ModelImpl
import io.swagger.models.Swagger
import io.swagger.models.parameters.Parameter
import io.swagger.models.parameters.RefParameter
import io.swagger.models.parameters.SerializableParameter
import io.swagger.models.properties.*
import io.swagger.parser.SwaggerParser
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.*

private val log = LoggerFactory.getLogger("SwaggerToDesign")

data class DslTypes(val name: String, val types: Map<String, String>)

class SwaggerToDesign(private val pathsToEntityNames: MutableMap<String, String> = mutableMapOf(),
    private val namesToTypeName: MutableMap<String, String> = mutableMapOf(),
    private val ignoreTypes: MutableSet<String> = mutableSetOf()) {

    fun toDslTypes(swaggerFile: Path): DslTypes =
        SwaggerToDesignExecutor(swaggerFile, namesToTypeName, ignoreTypes).toDslTypes()

}

private class SwaggerToDesignExecutor(swaggerFile: Path,
    private val namesToTypeName: MutableMap<String, String> = mutableMapOf(),
    private val ignoreTypes: MutableSet<String> = mutableSetOf()) {
    private val primitiveTypes = mapOf("integer" to "n.Int", "string" to "n.String")
    private val typeToPrimitive = mutableMapOf<String, String>()

    private val swagger = SwaggerParser().read(swaggerFile.toString())
    private val typesToFill = TreeMap<String, String>()

    fun toDslTypes(): DslTypes {
        extractTypeDefsFromPrimitiveAliases().forEach {
            it.value.toDslValues(it.key.toDslTypeName())
        }
        return DslTypes(name = swagger.info?.title ?: "", types = typesToFill)
    }

    private fun extractTypeDefsFromPrimitiveAliases(): Map<String, io.swagger.models.Model> {
        val ret = mutableMapOf<String, io.swagger.models.Model>()

        swagger.definitions?.entries?.forEach {
            if (!ignoreTypes.contains(it.key)) {
                val def = it.value
                if (def is ModelImpl && primitiveTypes.containsKey(def.type)) {
                    typeToPrimitive[it.key] = primitiveTypes[def.type]!!
                    typeToPrimitive[it.key.toDslTypeName()] = primitiveTypes[def.type]!!
                } else {
                    ret[it.key] = def
                }
            }
        }
        return ret
    }

    private fun Swagger.toModule(): Module {
        return Module {
            name("Shared")

            definitions?.forEach { defName, def ->
                values(def.toValues(defName))
            }
        }.init()
    }

    private fun Map<String, Property>?.toDslProperties(): String {
        return (this != null).ifElse(
            { this!!.entries.joinToString(nL, nL) { it.value.toDslProp(it.key) } }, { "" })
    }

    private fun Property.toDslPropValue(name: String, suffix: String = "", prefix: String = ""): String {
        return ((this is StringProperty && default.isNullOrEmpty().not())).ifElse({
            "${suffix}value(${(this as StringProperty).enum.isNotEmpty().ifElse({ "$name.$default" },
                { "\"$name.$default\"" })})$prefix"
        }, { "" })
    }

    private fun String?.toDslDoc(suffix: String = "", prefix: String = ""): String {
        return (this != null).ifElse({
            "${suffix}doc(${(this!!.contains("\"") || this!!.contains("\n") || this!!.contains("\\")).ifElse(
                "\"\"\"$this\"\"\"", "\"$this\"")})$prefix"
        }, { "" })
    }

    private fun io.swagger.models.Model.toDslValues(name: String) {
        if (this is ComposedModel) {
            typesToFill[name] = """
object ${name.toDslTypeName()} : Values({ ${interfaces.joinSurroundIfNotEmptyToString(",", "superUnit(", ")") {
                it.simpleRef.toDslTypeName()
            }}${description.toDslDoc()} }) {${allOf.filterNot { interfaces.contains(it) }.joinToString("") {
                it.properties.toDslProperties()
            }}
}"""
        } else if (this is ArrayModel) {
            val prop = items
            if (prop is ObjectProperty) {
                val typeName = prop.toDslTypeName()
                typesToFill[typeName] = prop.toDslType(typeName)
            } else {
                log.info("not supported yet {} {}", this, prop)
            }
        } else {
            typesToFill[name] = """
object ${name.toDslTypeName()} : Basic(${description.toDslDoc("{ ", " }")}) {${properties.toDslProperties()}
}"""
        }
    }

    private fun io.swagger.models.properties.StringProperty.toDslEnum(name: String): String {
        return """
object $name : EnumType() {
    val value = prop { type(n.String).key(true) }${enum.joinToString(nL, nL) {
            "    val ${it.toCamelCase().decapitalize()} = lit(value, \"$it\")"
        }}
}"""
    }

    private fun io.swagger.models.properties.ObjectProperty.toDslType(name: String): String {
        return """
object $name : Basic(${description.toDslDoc("{", "}")}) {${properties.toDslProperties()}
}"""
    }

    private fun io.swagger.models.properties.Property.toDslProp(name: String): String {
        val nameCamelCase = name.toCamelCase()
        return "    val $nameCamelCase = prop { ${(name != nameCamelCase).then(
            { "externalName(\"$name\")." })}${toDslInit(nameCamelCase)} }"
    }

    private fun io.swagger.models.properties.Property.toDslTypeName(name: String): String {
        val prop = this

        return when (prop) {
            is ArrayProperty -> {
                "n.List.GT(${prop.items.toDslTypeName(name)})"
            }
            is BinaryProperty -> {
                "n.Bytes"
            }
            is BooleanProperty -> {
                "n.Boolean"
            }
            is ByteArrayProperty -> {
                "n.Bytes"
            }
            is DateProperty -> {
                "n.Date"
            }
            is DateTimeProperty -> {
                "n.Date"
            }
            is DecimalProperty -> {
                "n.Double"
            }
            is DoubleProperty -> {
                "n.Double"
            }
            is EmailProperty -> {
                "n.String"
            }
            is FileProperty -> {
                "n.File"
            }
            is FloatProperty -> {
                "n.Float"
            }
            is IntegerProperty -> {
                "n.Int"
            }
            is BaseIntegerProperty -> {
                "n.Int"
            }
            is LongProperty -> {
                "n.Long"
            }
            is MapProperty -> {
                "n.Map"
            }
            is ObjectProperty -> {
                val typeName = prop.toDslTypeName()
                if (!typesToFill.containsKey(typeName)) {
                    typesToFill[typeName] = prop.toDslType(typeName)
                }
                typeName
            }
            is PasswordProperty -> {
                "n.String"
            }
            is UntypedProperty -> {
                "n.String"
            }
            is UUIDProperty -> {
                "n.String"
            }
            is RefProperty -> {
                prop.simpleRef.toDslTypeName()
            }
            is StringProperty -> {
                if (prop.enum != null && prop.enum.isNotEmpty()) {
                    val typeName = name.toDslTypeName()
                    if (!typesToFill.containsKey(typeName)) {
                        typesToFill[typeName] = prop.toDslEnum(typeName)
                    }
                    typeName
                } else {
                    "n.String"
                }
            }
            else -> {
                ""
            }
        }
    }

    private fun Parameter.toDslTypeName(name: String): String {
        val prop = this

        return when (prop) {
            is SerializableParameter -> {
                primitiveTypes.getOrDefault(prop.type, "n.String")
            }
            is RefParameter -> {
                swagger.parameters[prop.simpleRef]!!.toDslTypeName(name)
            }
            else -> {
                "n.String"
            }
        }
    }

    private fun ObjectProperty.toDslTypeName(): String = properties.keys.joinToString(
        "") { it.capitalize() }.toDslTypeName()

    private fun String.toDslTypeName(): String {
        return typeToPrimitive[this] ?: namesToTypeName.getOrPut(this, { toCamelCase().capitalize() })
    }

    private fun io.swagger.models.properties.Property.toDslInit(name: String): String {

        return if (this is RefProperty) {
            if (swagger.parameters != null && swagger.parameters.containsKey(simpleRef)) {
                swagger.parameters[simpleRef]!!.toDslInit(name)
            } else if (swagger.definitions != null && swagger.definitions.containsKey(simpleRef)) {
                toDslInitDirect(name)
            } else {
                toDslInitDirect(name)
            }
        } else {
            toDslInitDirect(name)
        }
    }

    private fun Parameter.toDslInit(name: String): String {
        val typeName = toDslTypeName(name)
        return "type($typeName)${required?.not().then({ ".nullable(true)" })}${description.toDslDoc(".")}"
    }

    private fun io.swagger.models.properties.Property.toDslInitDirect(name: String): String {
        val typeName = toDslTypeName(name)
        return "type($typeName)${required?.not().then({ ".nullable(true)" })}${(this is PasswordProperty).then(
            { ".hidden(true)" })}${toDslPropValue(typeName, ".")}${description.toDslDoc(".")}"
    }

    private fun io.swagger.models.Model.toValues(name: String): Values {
        val model = this
        return Values {
            name(name).doc(model.description ?: "")
            model.properties?.forEach { propName, p ->
                prop { name(propName).type(p.type.toType()).doc(p.description ?: "") }
            }
        }.init()
    }

    private fun String?.toType(): TypeI<*> {
        return if (this == null) {
            n.String
        } else {
            log.info("type: {}", this)
            n.String
        }
    }
}

