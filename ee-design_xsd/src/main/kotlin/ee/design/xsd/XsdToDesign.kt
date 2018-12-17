package ee.design.xsd

import com.sun.xml.xsom.*
import com.sun.xml.xsom.parser.XSOMParser
import ee.common.ext.safe
import ee.common.ext.toCamelCase
import ee.lang.nL
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.*
import javax.xml.parsers.SAXParserFactory

private val log = LoggerFactory.getLogger("XsdToDesign")

data class DslTypes(val name: String, val types: Map<String, String>, val elements: Map<String, String>)

class XsdToDesign(private val pathsToEntityNames: MutableMap<String, String> = mutableMapOf(),
                  private val namesToTypeName: MutableMap<String, String> = mutableMapOf(),
                  private val ignoreTypes: MutableSet<String> = mutableSetOf()) {

    fun toDslTypes(xsdFile: Path): DslTypes =
            XsdToDesignExecutor(xsdFile, namesToTypeName, ignoreTypes).toDslTypes()

}

private class XsdToDesignExecutor(val xsdFile: Path,
                                  private val namesToTypeName: MutableMap<String, String> = mutableMapOf(),
                                  private val ignoreTypes: MutableSet<String> = mutableSetOf()) {
    private val primitiveTypes = mapOf("integer" to "n.Int", "string" to "n.String")
    private val typeToPrimitive = mutableMapOf<String, String>()
    private val parser = XSOMParser(SAXParserFactory.newInstance())

    private val schemas: Collection<XSSchema> by lazy {
        parser.parse(xsdFile.toFile())
        parser.result.schemas
    }
    private val typesToFill = TreeMap<String, String>()
    private val elementsToFill = TreeMap<String, String>()

    fun toDslTypes(): DslTypes {
        //fillRepository(xsd.schema)

        //val repository = Repository
        //log.info("{}", repository)
        schemas.forEach { schema ->
            schema.elementDecls.forEach { name, item ->
                elementsToFill[name] = item.toDslValue(name)
            }

            schema.complexTypes.forEach { name, item ->
                typesToFill[name] = item.toDslValue(name)
            }
        }
        return DslTypes(xsdFile.toString(), typesToFill, elementsToFill)
    }

    private fun XSElementDecl.toDslValue(currentName: String = name): String =
            if (type.isComplexType) {
                type.asComplexType().toDslValue(currentName)
            } else {
                "//not supported yet, $this"
            }


    private fun XSComplexType.toDslValue(currentName: String = name): String {
        val superUnit = safe(log) { baseType.name?.takeIf { it != "anyType" } }
        val elements = mutableListOf<XSElementDecl>()
        if (root != null) elements.addAll(elementDecls)
        contentType?.asParticle()?.term?.flattenTo(elements)
        return """
object ${currentName.toDslTypeName()} : Values({${superUnit?.let { "superUnit(${baseType.name})" } ?: ""}}) {${
        declaredAttributeUses.joinToString(nL, nL) { it.toDslProp() }}${
        elements.joinToString(nL, nL) {
            it.toDslProp()
        }}
}"""
    }

    private fun XSTerm.flattenTo(toFill: MutableCollection<XSElementDecl>) {
        if (isElementDecl) {
            toFill.add(asElementDecl())
        } else if (isModelGroupDecl) {
            asModelGroupDecl().modelGroup.toList().map { it.term }.forEach {
                it.flattenTo(toFill)
            }
        } else if (isModelGroup) {
            asModelGroup().toList().map { it.term }.forEach {
                it.flattenTo(toFill)
            }
        }
    }

    private fun XSAttributeUse.toDslProp(currentName: String = decl.name): String {
        val nameCamelCase = currentName.toCamelCase().decapitalize()

        val parts = mutableListOf<String>()

        if (!currentName.equals(nameCamelCase, true)) parts.add("externalName(\"$currentName\")")
        if (!isRequired) parts.add("nullable()")
        if (defaultValue?.value != null && defaultValue.value.isNotEmpty()) parts.add("value(${defaultValue.value})")
        if (fixedValue?.value != null && fixedValue.value.isNotEmpty()) parts.add("value(${fixedValue.value})")
        val type = safe(log) { decl.type }?.toDslTypeName(decl.name ?: "n.String") ?: "n.String"
        val prop = type.definePropType(parts)

        return "    val $nameCamelCase = $prop { ${parts.joinToString(".") { it }} }"
    }

    private fun XSAttGroupDecl.toDslProp(currentName: String = name): String {
        return """${attributeUses.joinToString(nL, nL) { it.toDslProp() }}"""
    }

    private fun XSElementDecl.toDslProp(currentName: String = name): String {
        val nameCamelCase = currentName.toCamelCase().decapitalize()

        val parts = mutableListOf<String>()

        if (!currentName.equals(nameCamelCase, true)) parts.add("externalName(\"$currentName\")")
        //if (!isRequired) parts.add("nullable()")
        if (defaultValue?.value != null && defaultValue.value.isNotEmpty()) parts.add("value(${defaultValue.value})")
        if (fixedValue?.value != null && fixedValue.value.isNotEmpty()) parts.add("value(${fixedValue.value})")
        val type = safe(log) { type }?.toDslTypeName(name ?: "n.String") ?: "n.String"
        val prop = type.definePropType(parts)

        return "    val $nameCamelCase = $prop { ${parts.joinToString(".") { it }} }"
    }

    private fun String.definePropType(parts: MutableList<String>): String {
        return when (this) {
            "n.String" -> {
                "propS"
            }
            "n.Bool" -> {
                "propB"
            }
            "n.Int" -> {
                "propI"
            }
            else -> {
                parts.add("type($this)")
                "prop"
            }
        }
    }

    private fun XSSimpleType.toDslTypeName(currentName: String = name): String =
            if (isPrimitive) {
                when (currentName) {
                    "boolean" -> "n.Bool"
                    "Int" -> "n.Int"
                    else -> {
                        "n.String"
                    }
                }
            } else {
                currentName.toDslTypeName()
            }

    private fun XSType.toDslTypeName(currentName: String = name): String =
            currentName.toDslTypeName()

    private fun String.toDslTypeName(): String {
        return typeToPrimitive[this] ?: namesToTypeName.getOrPut(this) { toCamelCase().capitalize() }
    }
}


