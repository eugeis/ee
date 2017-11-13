package ee.system

import java.util.*
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.reflect


open class Prop {

}

open class SystemBase {
    companion object {
        fun findBy(vararg p: Any?) {
            println("SystemBase.findBy: " + p.toList())
        }

        fun prop(vararg p: Any?): Prop {
            println("SystemBase.prop: " + p.toList())
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
    val apply = category + ""

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

    val s3_reflect = s3.reflect()
    val s4 = Service<*>::apply

    val serviceFindersFunctions = Service.Finders::class.java.kotlin.functions

    val o1 = Service<String>()
    //o1.findByCategory()
    val o2 = Service.Finders()
    o2.findByCategory()
    println("serviceFindersFunctions: " + serviceFindersFunctions)
    println("s1: " + s1)
    println("s2: " + s2)
    println("s3: " + s3)
    println("s3_reflect: " + s3_reflect)
    println("")
}
