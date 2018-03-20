package ee.design.swagger

import ee.common.ext.*
import ee.design.Basic
import ee.design.Model
import ee.design.Module
import ee.lang.TypeI
import ee.lang.doc
import ee.lang.n
import ee.lang.nL
import io.swagger.models.ComposedModel
import io.swagger.models.ModelImpl
import io.swagger.models.Swagger
import io.swagger.models.properties.*
import io.swagger.parser.SwaggerParser
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.*

private val log = LoggerFactory.getLogger("SwaggerToDesign")

data class DslTypes(val name: String, val types: Map<String, String>)

class SwaggerToDesign(private val namesToTypeName: MutableMap<String, String> = mutableMapOf(),
    private val ignoreTypes: MutableSet<String> = mutableSetOf()) {
    private val primitiveTypes = mapOf("integer" to "n.Int", "string" to "n.String")
    private val typeToPrimitive = mutableMapOf<String, String>()

    fun toDslTypes(swaggerFile: Path): DslTypes {
        val swagger = SwaggerParser().read(swaggerFile.toString())
        return swagger.toDslTypes()
    }

    private fun Swagger.toDslTypes(): DslTypes {
        val ret = TreeMap<String, String>()

        extractTypeDefsFromPrimitiveAliases().forEach {
            it.value.toDslBasic(it.key.toDslTypeName(), ret)
        }
        return DslTypes(name = info?.title ?: "", types = ret)
    }

    private fun Swagger.extractTypeDefsFromPrimitiveAliases(): Map<String, io.swagger.models.Model> {
        val ret = mutableMapOf<String, io.swagger.models.Model>()
        definitions?.entries?.forEach {
            if (!ignoreTypes.contains(it.key)) {
                val def = it.value
                if (def is ModelImpl && primitiveTypes.containsKey(def.type)) {
                    typeToPrimitive[it.key] = primitiveTypes[def.type]!!
                    typeToPrimitive[it.key.toDslTypeName()] = primitiveTypes[def.type]!!
                } else {
                    ret[it.key] = it.value
                }
            }
        }
        return ret
    }

    private fun Swagger.toModule(): Module {
        return Module {
            name("Shared")

            definitions?.forEach { defName, def ->
                basic(def.toBasic(defName))
            }
        }.init()
    }

    private fun Map<String, Property>?.toDslProperties(typesToFill: MutableMap<String, String>): String {
        return (this != null).ifElse(
                { this!!.entries.joinToString(nL, nL) { it.value.toDslProp(it.key, typesToFill) } }, { "" })
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

    private fun io.swagger.models.Model.toDslBasic(name: String, typesToFill: MutableMap<String, String>) {
        typesToFill[name] = if (this is ComposedModel) {
            """
object ${name.toDslTypeName()} : Basic({ ${interfaces.joinSurroundIfNotEmptyToString(",", "superUnit(", ")") {
                it.simpleRef.toDslTypeName()
            }}${description.toDslDoc()} }) {${properties.toDslProperties(
                    typesToFill)}${child?.properties.toDslProperties(typesToFill)}
}"""
        } else {
            """
object ${name.toDslTypeName()} : Basic(${description.toDslDoc("{ ", " }")}) {${properties.toDslProperties(typesToFill)}
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

    private fun io.swagger.models.properties.ObjectProperty.toDslType(name: String,
        typesToFill: MutableMap<String, String>): String {
        return """
object $name : Basic(${description.toDslDoc("{", "}")}) {${properties.toDslProperties(typesToFill)}
}"""
    }

    private fun io.swagger.models.properties.Property.toDslProp(name: String,
        typesToFill: MutableMap<String, String>): String {
        val nameCamelCase = name.toCamelCase()
        return "    val $nameCamelCase = prop { ${(name != nameCamelCase).then(
                { "externalName(\"$name\")." })}${toDslInit(nameCamelCase, typesToFill)} }"
    }

    private fun io.swagger.models.properties.Property.toDslTypeName(name: String,
        typesToFill: MutableMap<String, String>): String {
        val prop = this

        return when (prop) {
            is ArrayProperty       -> {
                "n.List.GT(${prop.items.toDslTypeName(name, typesToFill)})"
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
                val typeName = prop.toDslTypeName()
                if (!typesToFill.containsKey(typeName)) {
                    typesToFill[typeName] = prop.toDslType(typeName, typesToFill)
                }
                typeName
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
                    val typeName = name.toDslTypeName()
                    if (!typesToFill.containsKey(typeName)) {
                        typesToFill[typeName] = prop.toDslEnum(typeName)
                    }
                    typeName
                } else {
                    "n.String"
                }
            }
            else                   -> {
                ""
            }
        }
    }

    private fun ObjectProperty.toDslTypeName(): String = properties.keys.joinToString(
            "") { it.capitalize() }.toDslTypeName()

    private fun String.toDslTypeName(): String {
        return typeToPrimitive[this] ?: namesToTypeName.getOrPut(this, { toCamelCase().capitalize() })
    }

    private fun io.swagger.models.properties.Property.toDslInit(name: String,
        typesToFill: MutableMap<String, String>): String {
        val typeName = toDslTypeName(name, typesToFill)
        return "type($typeName)${required?.not().then({ ".nullable(true)" })}${(this is PasswordProperty).then(
                { ".hidden(true)" })}${toDslPropValue(typeName, ".")}${description.toDslDoc(".")}"
    }

    private fun io.swagger.models.Model.toBasic(name: String): Basic {
        val model = this
        return Basic {
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

