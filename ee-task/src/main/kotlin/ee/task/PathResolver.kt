package ee.task

import ee.lang.ItemIB
import java.nio.file.Path
import java.nio.file.Paths

open class PathResolver : PathResolverBase {
    companion object {
        val EMPTY = PathResolverBase.EMPTY
    }

    constructor(home: Path = Paths.get(""), itemToHome: MutableMap<String, String> = hashMapOf()) : super(home, itemToHome) {
    }

    override fun <T : ItemIB<*>> resolve(item: T): Path {
        if (!itemToHome.containsKey(item.name())) {
            val ret = resolve(item.parent())
            return ret
        } else {
            return home.resolve(itemToHome[item.name()])
        }
    }
}
