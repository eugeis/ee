package ee.system

open class ContentPackageController<T : ContentPackage> : ContentPackageControllerBase<T> {
    companion object {
        val EMPTY = ContentPackageControllerBase.EMPTY
    }

    constructor(item: T) : super(item) {

    }


}

