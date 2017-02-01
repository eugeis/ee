package ee.task

import ee.design.ItemI
import ee.design.ModuleI
import ee.design.StructureUnitI
import java.nio.file.Path
import java.nio.file.Paths

open class PathResolver : PathResolverBase {
    companion object {
        val EMPTY = PathResolverBase.EMPTY
    }

    constructor(home: Path = Paths.get(""), itemToHome: MutableMap<String, String> = hashMapOf()) : super(home, itemToHome) {
    }

    override fun <T : ItemI> resolve(item: T): Path {
        if (!itemToHome.containsKey(item.name())) {
            val ret = resolve(item.parent())
            if (item is StructureUnitI) {
                if (item is ModuleI) {
                    return ret.parent.resolve(item.artifact())
                } else {
                    return ret.resolve(item.artifact())
                }
            } else {
                return ret
            }
        } else {
            return home.resolve(itemToHome[item.name()])
        }
    }
}

