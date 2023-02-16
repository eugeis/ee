package ee.design

import ee.common.ext.exists
import ee.common.ext.lastModified
import ee.common.ext.mkdirs
import ee.lang.*
import org.slf4j.LoggerFactory
import java.nio.file.Path

private val log = LoggerFactory.getLogger("GeneratorAngular")

open class GeneratorAngular<M>(
    name: String, contextBuilder: ContextBuilder<M>, val template: TemplateI<M>) : GeneratorBase<M>(name, contextBuilder) {

    override fun generate(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean) {
        if (shallSkip(model)) return
        val c = contextBuilder.builder.invoke(model)
        val module = target.resolve(c.moduleFolder)
        val metaData = module.loadMetaData()

        var pkg = resolveModulePath(module, c)

        val fileName = template.name(model).relativeFilePath.toString()

        var path = pkg.resolve(template.name(model).relativeFilePath)

        with(fileName) {
            //val lastIndex = path.toString().lastIndexOf("\\") + 1
            //val newFileName = template.name(model).fileName.substring(template.name(model).fileName.indexOf("_") + 1, template.name(model).fileName.length)

            when {
                contains("-routing.module") || contains("-model.module") -> {
                    val moduleName = extractFirstPart(fileName, "-")
                    pkg = pkg.resolve(moduleName)
                    path = pkg.resolve(fileName)
                }
                contains("module-view.component") -> {
                    val moduleName = extractFirstPart(fileName, "-")
                    pkg = pkg.resolve(moduleName).resolve("components").resolve("view")
                    path = pkg.resolve(fileName)
                }
                contains("entity-view.component") -> {
                    val moduleName = extractFirstPart(fileName, "_").toLowerCase()
                    val entityName = extractMiddledPart(fileName, "_", "-")
                    val name = extractSecondPart(fileName, "_")
                    pkg = pkg.resolve(moduleName).resolve(entityName).resolve("components").resolve("view")
                    path = pkg.resolve(name)

                    /*
                    val lastIndexOfParent = path.toString().lastIndexOf("_")
                    val parentName = path.toString().substring(lastIndex, lastIndexOfParent)
                    pkg = Paths.get(pkg.toString() +
                            "\\${parentName.toLowerCase()}\\${template.name(model).fileName.
                            substring(template.name(model).fileName.indexOf("_") + 1, template.name(model).fileName.indexOf("-"))}\\components\\view")
                    path = pkg.resolve(newFileName)
                    log.debug("{} == {}", path, newPath)

                     */
                }
                contains("entity-form.component") -> {
                    val moduleName = extractFirstPart(fileName, "_").toLowerCase()
                    val entityName = extractMiddledPart(fileName, "_", "-")
                    val name = extractSecondPart(fileName, "_")
                    pkg = pkg.resolve(moduleName).resolve(entityName).resolve("components").resolve("form")
                    path = pkg.resolve(name)
                    /*
                    val lastIndexOfParent = path.toString().lastIndexOf("_")
                    val parentName = path.toString().substring(lastIndex, lastIndexOfParent)
                    pkg = Paths.get(pkg.toString() +
                            "\\${parentName.toLowerCase()}\\${template.name(model).fileName.
                            substring(template.name(model).fileName.indexOf("_") + 1, template.name(model).fileName.indexOf("-"))}\\components\\form")
                    path = pkg.resolve(newFileName)
                    log.debug("{} == {}", path, newPath)
                     */
                }
                contains("entity-list.component") -> {
                    val moduleName = extractFirstPart(fileName, "_").toLowerCase()
                    val entityName = extractMiddledPart(fileName, "_", "-")
                    val name = extractSecondPart(fileName, "_")
                    pkg = pkg.resolve(moduleName).resolve(entityName).resolve("components").resolve("list")
                    path = pkg.resolve(name)
                    /*
                    val lastIndexOfParent = path.toString().lastIndexOf("_")
                    val parentName = path.toString().substring(lastIndex, lastIndexOfParent)
                    pkg = Paths.get(pkg.toString() +
                            "\\${parentName.toLowerCase()}\\${template.name(model).fileName.
                            substring(template.name(model).fileName.indexOf("_") + 1, template.name(model).fileName.indexOf("-"))}\\components\\list")
                    path = pkg.resolve(newFileName)
                    log.debug("{} == {}", path, newPath)
                     */
                }
                contains("data.service") -> {
                    val moduleName = extractFirstPart(fileName, "_").toLowerCase()
                    val entityName = extractMiddledPart(fileName, "_", "-")
                    val name = extractSecondPart(fileName, "_")
                    pkg = pkg.resolve(moduleName).resolve(entityName).resolve("service")
                    path = pkg.resolve(name)
                    /*
                    val lastIndexOfParent = path.toString().lastIndexOf("_")
                    val parentName = path.toString().substring(lastIndex, lastIndexOfParent)
                    pkg = Paths.get(pkg.toString() +
                            "\\${parentName.toLowerCase()}\\${template.name(model).fileName.
                            substring(template.name(model).fileName.indexOf("_") + 1, template.name(model).fileName.indexOf("-"))}\\service")
                    path = pkg.resolve(newFileName)
                    log.debug("{} == {}", path, newPath)
                     */
                }
                contains("module-view.service") -> {
                    val moduleName = extractFirstPart(fileName, "-")
                    pkg = pkg.resolve(moduleName).resolve("service")
                    path = pkg.resolve(fileName)
                }
                contains("basic.component") -> {
                    val moduleName = extractFirstPart(fileName, "_").toLowerCase()
                    val entityName = extractMiddledPart(fileName, "_", "-")
                    val name = extractSecondPart(fileName, "_")
                    pkg = pkg.resolve(moduleName).resolve("basics").resolve(entityName).resolve("service")
                    path = pkg.resolve(name)

                    /*
                    val lastIndexOfParent = path.toString().lastIndexOf("_")
                    val parentName = path.toString().substring(lastIndex, lastIndexOfParent)
                    pkg = Paths.get(pkg.toString() +
                            "\\${parentName}\\basics\\${template.name(model).fileName.
                            substring(template.name(model).fileName.indexOf("_") + 1, template.name(model).fileName.indexOf("-"))}")
                    path = pkg.resolve(newFileName)
                    log.debug("{} == {}", path, newPath)
                     */
                }
            }
        }

        if(!pkg.exists()) {
            pkg.mkdirs()
        }

        val relative = target.relativize(path).toString()
        if (!path.exists() || !metaData.wasModified(relative, path.lastModified())) {
            log.debug("generate $path for $model")
            val body = template.generate(model, c)
            if (body.isNotBlank()) {
                val text = c.complete(body)
                path.toFile().writeText(text)
                metaData.track(relative, path.lastModified())
            }
            c.clear()
            metaData.store(module)
        } else {
            log.debug("File exists $path and was modified after generation, skip generation.")
        }
    }

    private fun extractFirstPart(fileName: String, separator: String): String {
        return fileName.substring(0, fileName.indexOf(separator))
    }

    private fun extractSecondPart(fileName: String, separator: String): String {
        return fileName.substring(fileName.indexOf(separator) + 1)
    }

    private fun extractMiddledPart(fileName: String, firstSeparator: String, secondSeparator: String): String {
        val first = fileName.indexOf(firstSeparator)+1
        return fileName.substring(first, fileName.indexOf(secondSeparator, first))
    }

    private fun extractName(fileName: String, firstSeparator: String, secondSeparator: String): String {
        val first = fileName.indexOf(firstSeparator)+1
        val second = fileName.indexOf(secondSeparator, first)+1
        return fileName.substring(second)
    }
}