package ee.system

import java.util.*
import kotlin.reflect.declaredFunctions

open class Prop {

}

open class SystemBase {
    companion object {
        fun findBy(vararg p: Any?) {
            println(p.toList())
        }

        fun prop(vararg p: Any?): Prop {
            println(p.toList())
            return Prop()
        }
    }
}

data class Interval(val start: Date, val end: Date)
class Service<T : String> : SystemBase() {
    val category: String? = null
    val dependsOn: MutableList<Service<*>>? = null
    val dependsOnMe: MutableList<Service<*>>? = null

    fun findByCategory() = findBy(category, dependsOn)
    class Finders {
        fun findByCategory() = findBy(arrayListOf(Service<*>::category), Service<*>::dependsOn, Interval::start)
    }

}

class DelegateService : SystemBase() {
    fun findServiceByCategory() = Service.Finders::findByCategory
}

fun main(args: Array<String>) {
    val s1 = Service<*>::findByCategory
    val s2 = Service.Finders::findByCategory
    val s3 = DelegateService::findServiceByCategory

    val df = Service::class.java.kotlin.declaredFunctions

    val o1 = Service<String>()
    o1.findByCategory()
    val o2 = Service.Finders()
    o2.findByCategory()
    println(s1)
    println(s2)
    println(s3)
    println("")
}
