package ee.system

open class PackageController<T : Package> : PackageControllerBase<T> {
    companion object {
        val EMPTY = PackageControllerBase.EMPTY
    }
    constructor(item: T): super(item) {

    }

    override fun prepare(params: Map<String, String>) {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun configure(params: Map<String, String>) {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun install(params: Map<String, String>) {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun uninstall() {
        throw IllegalAccessException("Not implemented yet.")
    }

}

