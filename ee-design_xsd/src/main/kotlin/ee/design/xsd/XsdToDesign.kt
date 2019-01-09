package ee.design.xsd

import com.sun.xml.xsom.*
import com.sun.xml.xsom.impl.ElementDecl
import com.sun.xml.xsom.impl.ListSimpleTypeImpl
import com.sun.xml.xsom.parser.XSOMParser
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.safe
import ee.common.ext.toCamelCase
import ee.lang.nL
import org.slf4j.LoggerFactory
import java.math.BigInteger
import java.nio.file.Path
import java.util.*
import javax.xml.parsers.SAXParserFactory

private val log = LoggerFactory.getLogger("XsdToDesign")

data class Item(val element: XSElementDecl, val min: Int, val max: Int)
data class DslTypes(val name: String, val types: Map<String, String>, val elements: Map<String, String>)

class XsdToDesign(private val pathsToEntityNames: MutableMap<String, String> = mutableMapOf(),
                  private val namesToTypeName: MutableMap<String, String> = mutableMapOf(),
                  private val onlyItems: MutableSet<String> = mutableSetOf(),
                  private val excludeItems: MutableSet<String> = mutableSetOf()) {

    fun toDslTypes(xsdFile: Path): DslTypes =
            XsdToDesignExecutor(xsdFile, namesToTypeName, onlyItems, excludeItems).toDslTypes()

}

private class XsdToDesignExecutor(val xsdFile: Path,
                                  private val namesToTypeName: MutableMap<String, String>,
                                  private val onlyItems: Set<String>,
                                  private val excludeItems: Set<String>) {
    private val primitiveTypes = mapOf("integer" to "n.Int", "string" to "n.String",
            "boolean" to "n.Bool", "dateTime" to "n.Date", "date" to "n.Date",
            "byte" to "n.Byte",
            "unsignedInt" to "n.UInt", "unsignedShort" to "n.UShort", "unsignedByte" to "n.UByte",
            "anyType" to "n.Any")
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
            schema.simpleTypes.forEach { name, item ->
                typeToPrimitive[name] = when {
                    item.isList -> {
                        val listType = item as ListSimpleTypeImpl
                        val itemType = listType.baseListType.itemType
                        "n.List.GT(${listType.baseListType.toDslTypeName(itemType.name("string"))})"
                    }
                    item.baseType is ListSimpleTypeImpl -> {
                        val listType = item.baseType as ListSimpleTypeImpl
                        val itemType = listType.baseListType.itemType
                        "n.List.GT(${listType.baseListType.toDslTypeName(itemType.name("string"))})"
                    }
                    else -> item.baseType.toDslTypeName(item.baseType.name(item.name))
                }
            }
            when {
                onlyItems.isNotEmpty() -> {
                    schema.elementDecls.forEach { name, item ->
                        if (onlyItems.contains(name)) {
                            elementsToFill[name] = item.toDslValue(name)
                        }
                    }

                    schema.complexTypes.forEach { name, item ->
                        if (onlyItems.contains(name)) {
                            typesToFill[name] = item.toDslValue(name)
                        }
                    }
                }
                excludeItems.isNotEmpty() -> {
                    schema.elementDecls.forEach { name, item ->
                        if (!excludeItems.contains(name)) {
                            elementsToFill[name] = item.toDslValue(name)
                        }
                    }

                    schema.complexTypes.forEach { name, item ->
                        if (!excludeItems.contains(name)) {
                            typesToFill[name] = item.toDslValue(name)
                        }
                    }
                }
                else -> {
                    schema.elementDecls.forEach { name, item ->
                        elementsToFill[name] = item.toDslValue(name)
                    }

                    schema.complexTypes.forEach { name, item ->
                        typesToFill[name] = item.toDslValue(name)
                    }
                }
            }
        }
        return DslTypes(xsdFile.toString(), typesToFill, elementsToFill)
    }

    private fun XSSimpleType.name(default: String): String = name ?: {
        if (isRestriction) {
            val res = asRestriction()
            if (res.declaredFacets.find { it.name == "pattern" } != null) {
                default
            } else {
                default
            }
        } else {
            default
        }
    }()

    private fun XSType.name(default: String): String = name ?: {
        default
    }()

    private fun XSElementDecl.toDslValue(currentName: String = name): String =
            if (type.isComplexType) {
                type.asComplexType().toDslValue(currentName)
            } else {
                "//not supported yet, $this"
            }


    private fun XSComplexType.toDslValue(currentName: String = name): String {
        val superUnit = safe(log) {
            if (!baseType.isSimpleType) baseType.name?.takeIf { it != "anyType" } else null
        }
        val elements = mutableListOf<Item>()
        if (root != null) elements.addAll(elementDecls.map { Item(it, 0, 1) })
        contentType?.asParticle()?.let {
            it.term.flattenTo(elements, it.maxOccurs, it.maxOccurs)
        }

        return """
    object ${currentName.toDslTypeName()} : Values(${superUnit?.let { " { superUnit(${baseType.toDslTypeName()}) }" }
                ?: ""}) {${
        if (superUnit != null && baseType.isSimpleType && baseType.name?.takeIf { it != "anyType" } != null)
            "$nL${baseType.toDslProp("value")}" else ""}${
        declaredAttributeUses.joinToString(nL, nL) { it.toDslProp() }}${
        elements.joinToString(nL, nL) {
            it.toDslProp()
        }}
    }"""
    }

    private fun XSTerm.flattenTo(toFill: MutableCollection<Item>, min: BigInteger, max: BigInteger) {
        when {
            isElementDecl ->
                toFill.add(Item(asElementDecl(), min.toInt(), max.toInt()))
            isModelGroupDecl ->
                asModelGroupDecl().modelGroup.toList().forEach {
                    it.term.flattenTo(toFill, it.maxOccurs, it.maxOccurs)
                }
            isModelGroup ->
                asModelGroup().toList().forEach {
                    it.term.flattenTo(toFill, it.maxOccurs, it.maxOccurs)
                }
        }
    }

    private fun XSDeclaration.toDslProp(currentName: String): String {
        val nameCamelCase = currentName.toCamelCase().decapitalize()

        val parts = mutableListOf<String>()

        if (!currentName.equals(nameCamelCase, true)) parts.add("externalName(\"$currentName\")")
        val type = safe(log) { name }?.toDslTypeName() ?: "n.String"
        val prop = type.definePropType(parts, 0, 1)
        //if (!isRequired) parts.add("nullable()")

        return "        val $nameCamelCase = $prop${
        parts.joinSurroundIfNotEmptyToString(".", " { ", " }", "()") { it }}"
    }

    private fun XSAttributeUse.toDslProp(currentName: String = decl.name): String {
        val nameCamelCase = currentName.toCamelCase().decapitalize()

        val parts = mutableListOf<String>()

        if (!currentName.equals(nameCamelCase, true)) parts.add("externalName(\"$currentName\")")
        val type = safe(log) { decl.type }?.toDslTypeName() ?: "n.String"
        val prop = type.definePropType(parts, if (isRequired) 1 else 0, 1)
        //if (!isRequired) parts.add("nullable()")
        defaultValue?.value?.addValue(parts, type)
        fixedValue?.value?.addValue(parts, type)

        return "        val $nameCamelCase = $prop${
        parts.joinSurroundIfNotEmptyToString(".", " { ", " }", "()") { it }}"
    }

    private fun String?.addValue(parts: MutableList<String>, type: String) {
        if (this != null && isNotEmpty()) parts.add("value(${convertForType(type)})")
    }

    private fun XSAttGroupDecl.toDslProp(currentName: String = name): String {
        return attributeUses.joinToString(nL, nL) { it.toDslProp() }
    }

    private fun Item.toDslProp(currentName: String = element.name): String {
        val nameCamelCase = currentName.toCamelCase()

        val parts = mutableListOf<String>()

        if (!currentName.equals(nameCamelCase, true)) parts.add("externalName(\"$currentName\")")
        val type = safe(log) { element.type }?.toDslTypeName(element.type.name ?: "string") ?: "n.String"
        val prop = type.definePropType(parts, min, max)
        //if (!isRequired) parts.add("nullable()")
        element.defaultValue?.value?.addValue(parts, type)
        element.fixedValue?.value?.addValue(parts, type)

        return "        val $nameCamelCase = $prop${
        parts.joinSurroundIfNotEmptyToString(".", " { ", " }", "()") { it }}"
    }

    private fun String.convertForType(type: String) = if (type == "n.String") "\"$this\"" else this

    private fun String.definePropType(parts: MutableList<String>, min: Int, max: Int): String {
        return if (max != 1) {
            parts.add("type(n.List.GT($this))")
            "prop"
        } else {
            when (this) {
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
    }

    private fun XSSimpleType.toDslTypeName(currentName: String = name): String =
            currentName.toDslTypeName()

    private fun XSType.toDslTypeName(currentName: String = name): String =
            currentName.toDslTypeName()

    private fun String.toDslTypeName(): String {
        return primitiveTypes[this] ?: typeToPrimitive[this]
        ?: namesToTypeName.getOrPut(this) { toCamelCase().capitalize() }
    }
}


