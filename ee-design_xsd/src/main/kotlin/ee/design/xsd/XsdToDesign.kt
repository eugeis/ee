package ee.design.xsd

import com.sun.xml.xsom.*
import com.sun.xml.xsom.impl.ListSimpleTypeImpl
import com.sun.xml.xsom.parser.XSOMParser
import ee.common.ext.ifElse
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

data class Item(val name: String, val type: String, val min: Int, val max: Int,
                val fixedValue: String? = null, val defaultValue: String? = null)

data class DslTypes(val name: String, val types: Map<String, String>, val elements: Map<String, String>)

class XsdToDesign(private val entityNames: Set<String> = emptySet(),
                  private val externalNameCaseSensitive: Boolean = false,
                  private val implodeTypes: Set<String> = emptySet(),
                  private val namesToTypeName: MutableMap<String, String> = mutableMapOf(),
                  private val onlyItems: Set<String> = emptySet(),
                  private val excludeItems: Set<String> = emptySet()) {

    fun toDslTypes(xsdFile: Path): DslTypes =
            XsdToDesignExecutor(xsdFile, entityNames, externalNameCaseSensitive, implodeTypes, namesToTypeName,
                    onlyItems, excludeItems).toDslTypes()

}

private class XsdToDesignExecutor(val xsdFile: Path,
                                  private val entityNames: Set<String>,
                                  private val externalNameCaseSensitive: Boolean = false,
                                  private val implodeTypes: Set<String>,
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
    private val implodeTypeToPropItem = mutableMapOf<String, Item>()

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

            //build implode props
            schema.buildImplodeProps()

            when {
                onlyItems.isNotEmpty() -> schema.dslTypesOnlyItems()
                excludeItems.isNotEmpty() -> schema.dslTypesExcludeItems()
                else -> schema.dslTypesAllItems()
            }
        }
        return DslTypes(xsdFile.toString(), typesToFill, elementsToFill)
    }


    private fun XSSchema.dslTypesOnlyItems() {
        elementDecls.forEach { name, item ->
            if (onlyItems.contains(name)) {
                item.toDslType(name)?.let {
                    elementsToFill[name] = it
                }
            }
        }

        complexTypes.forEach { name, item ->
            if (onlyItems.contains(name)) {
                item.toDslType(name)?.let {
                    typesToFill[name] = it
                }
            }
        }
    }

    private fun XSSchema.dslTypesExcludeItems() {
        elementDecls.forEach { name, item ->
            if (!excludeItems.contains(name)) {
                item.toDslType(name)?.let {
                    elementsToFill[name] = it
                }
            }
        }

        complexTypes.forEach { name, item ->
            if (!excludeItems.contains(name)) {
                item.toDslType(name)?.let {
                    typesToFill[name] = it
                }
            }
        }
    }

    private fun XSSchema.dslTypesAllItems() {
        elementDecls.forEach { name, item ->
            item.toDslType(name)?.let {
                elementsToFill[name] = it
            }
        }

        complexTypes.forEach { name, item ->
            item.toDslType(name)?.let {
                typesToFill[name] = it
            }
        }
    }

    private fun XSSchema.buildImplodeProps() {
        if (implodeTypes.isNotEmpty()) {
            complexTypes.forEach { name, item ->
                if (implodeTypes.contains(name)) {
                    item.toDslImplodeProp(name)?.let {
                        implodeTypeToPropItem[name] = it
                    }
                }
            }
        }
    }

    private fun XSComplexType.toDslImplodeProp(currentName: String = name): Item? {
        if (primitiveTypes.containsKey(currentName) || typeToPrimitive.containsKey(currentName)) return null

        val items = collectItems()
        return if (items.size == 1) items.first() else {
            log.info("can't implode the {}, because propsCount={} not one", currentName, items.size)
            null
        }
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

    private fun XSElementDecl.toDslType(currentName: String = name): String? =
            if (type.isComplexType) {
                type.asComplexType().toDslType(currentName)
            } else {
                "//not supported yet, $this"
            }


    private fun XSComplexType.toDslType(currentName: String = name): String? {
        if (primitiveTypes.containsKey(currentName) || typeToPrimitive.containsKey(currentName) ||
                implodeTypeToPropItem.containsKey(currentName)) return null

        val items = collectItems()

        val dslTypeName = currentName.toDslTypeName()
        val superUnit = if (baseType.isComplexType) baseType.asComplexType()?.takeIf { it.name != "anyType" } else null
        return """
    object $dslTypeName : ${entityNames.contains(currentName).ifElse("Entity", "Values")}(${if (superUnit != null)
            "{ superUnit(${superUnit.toDslTypeName()}) }" else ""}) {${
        items.joinSurroundIfNotEmptyToString(nL, nL) {
            it.toDslProp()
        }}
    }"""
    }

    private fun XSComplexType.collectItems(): MutableList<Item> {
        val elements = mutableListOf<Item>()
        if (root != null) elements.addAll(elementDecls.map { it.toItem(0, 1) })

        if (derivationMethod == XSType.EXTENSION &&
                (!baseType.isComplexType || baseType.asComplexType()?.takeIf { it.name != "anyType" } == null)) {
            elements.add(baseType.toItem("value"))
        }

        declaredAttributeUses.forEach { elements.add(it.toItem()) }

        contentType?.asParticle()?.let {
            //explicitContent?.asParticle()?.let {
            if (derivationMethod != XSType.EXTENSION) {
                it.term.flattenTo(elements, it.maxOccurs, it.maxOccurs)
            }
        }
        return elements
    }

    private fun XSElementDecl.toItem(min: Int = 0, max: Int = 1, currentName: String = name): Item =
            Item(currentName, safe(log) { type }?.name ?: "string", min, max, fixedValue?.value, defaultValue?.value)


    private fun XSAttributeUse.toItem(currentName: String = decl.name): Item =
            Item(currentName, safe(log) { decl.type }?.name ?: "string", 0, 1,
                    fixedValue?.value, defaultValue?.value)

    private fun XSDeclaration.toItem(currentName: String): Item =
            Item(currentName, safe(log) { name } ?: "string", 0, 1)

    private fun XSTerm.flattenTo(toFill: MutableCollection<Item>, min: BigInteger, max: BigInteger) {
        when {
            isElementDecl -> toFill.add(asElementDecl().toItem(min.toInt(), max.toInt()))
            isModelGroupDecl -> asModelGroupDecl().modelGroup.flattenTo(toFill, min, max)
            isModelGroup -> asModelGroup().flattenTo(toFill, min, max)
        }
    }

    private fun XSTerm.toTypeChain(): String = when {
        isElementDecl -> asElementDecl().toItem().let { "${it.name}.${it.type} " }
        isModelGroupDecl -> asModelGroupDecl().modelGroup.toTypeChain()
        isModelGroup -> asModelGroup().toTypeChain()
        else -> ""
    }

    private fun XSModelGroup.toTypeChain(): String {
        return ""
    }

    private fun XSModelGroup.flattenTo(toFill: MutableCollection<Item>, min: BigInteger, max: BigInteger) {
        if (compositor == XSModelGroup.Compositor.CHOICE) {
            val typeChains = toList().map {
                it.term.toTypeChain()
            }
            toFill.add(Item("items", "anyType", min.toInt(), max.toInt()))
        } else {
            toList().forEach {
                it.term.flattenTo(toFill, it.maxOccurs, it.maxOccurs)
            }
        }
    }


    private fun String?.addValue(parts: MutableList<String>, type: String) {
        if (this != null && isNotEmpty()) parts.add("value(${convertForType(type)})")
    }

    private fun Item.toDslProp(currentName: String = name): String {
        val nameCamelCase = currentName.toCamelCase()

        val body = toPropBodyCheckImplode()?.toProp(currentName, nameCamelCase)
        return "        val $nameCamelCase = $body"
    }

    private fun Pair<String, List<String>>.toProp(currentName: String, nameCamelCase: String): String {
        val externalName = if ((externalNameCaseSensitive && currentName != nameCamelCase) ||
                (!externalNameCaseSensitive && !currentName.equals(nameCamelCase, true)))
            "externalName(\"$currentName\")." else ""
        return "$first${
        second.joinSurroundIfNotEmptyToString(".", " { $externalName", " }", "()") { it }}"
    }

    private fun Item.toPropBodyCheckImplode(): Pair<String, List<String>> =
            (implodeTypeToPropItem[type] ?: this).toPropBody()

    private fun Item.toPropBody(): Pair<String, MutableList<String>> {
        val parts = mutableListOf<String>()

        val dslType = type.toDslTypeName()
        val prop = dslType.definePropType(parts, min, max)
        //if (!isRequired) parts.add("nullable()")
        defaultValue?.addValue(parts, dslType)
        fixedValue?.addValue(parts, dslType)
        return prop to parts
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
        ?: namesToTypeName.getOrPut(this) { toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
    }
}


