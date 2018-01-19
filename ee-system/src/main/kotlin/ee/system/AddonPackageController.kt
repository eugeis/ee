package ee.system

open class AddonPackageController<T : AddonPackage> : AddonPackageControllerBase<T> {
    companion object {
        val EMPTY = AddonPackageControllerBase.EMPTY
    }

    constructor(item: T) : super(item) {

    }


}

