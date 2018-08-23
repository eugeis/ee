package ee.design.xsd

import com.sun.xml.xsom.parser.SchemaDocument
import com.sun.xml.xsom.parser.XSOMParser
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.*
import javax.xml.parsers.SAXParserFactory

private val log = LoggerFactory.getLogger("XsdToDesign")

data class DslTypes(val name: String, val types: Map<String, String>)

class XsdToDesign(private val pathsToEntityNames: MutableMap<String, String> = mutableMapOf(),
                  private val namesToTypeName: MutableMap<String, String> = mutableMapOf(),
                  private val ignoreTypes: MutableSet<String> = mutableSetOf()) {

    fun toDslTypes(xsdFile: Path): DslTypes =
        XsdToDesignExecutor(xsdFile, namesToTypeName, ignoreTypes).toDslTypes()

}

private class XsdToDesignExecutor(xsdFile: Path,
                                  private val namesToTypeName: MutableMap<String, String> = mutableMapOf(),
                                  private val ignoreTypes: MutableSet<String> = mutableSetOf()) {
    private val primitiveTypes = mapOf("integer" to "n.Int", "string" to "n.String")
    private val typeToPrimitive = mutableMapOf<String, String>()
    private val parser = XSOMParser(SAXParserFactory.newInstance())

    private val xsd: SchemaDocument by lazy {
        parser.parse(xsdFile.toFile())
        parser.documents.first()
    }
    private val typesToFill = TreeMap<String, String>()

    fun toDslTypes(): DslTypes {

        return DslTypes(name = xsd.toString(), types = typesToFill)
    }
}

