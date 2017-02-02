package ee.design.task

import ee.lang.ItemI
import ee.lang.StructureUnitI
import ee.design.ModuleI
import ee.task.PathResolver
import java.nio.file.Path
import java.nio.file.Paths

open class DesignPathResolver : PathResolver {

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

