package ee.asm

abstract class Renderer(config: AnkoConfiguration) : Configurable(config) {
    protected abstract fun processElements(state: GenerationState): String

    fun process(state: GenerationState): String {
        return ""
    }

    protected fun render(templateName: String): String {
        return ""
    }
}

class RenderFacade(val generationState: GenerationState) : Configurable(generationState.config), ReflectionUtils {

    private val cachedResults: MutableMap<Class<out Renderer>, String> = hashMapOf()

    operator fun get(clazz: Class<out Renderer>): String = cachedResults.getOrPut(clazz) {
        initializeClassWithArgs(clazz, config to AnkoConfiguration::class.java).process(generationState)
    }

    protected fun render(templateName: String): String {
        return ""
    }

}