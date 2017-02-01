package ee.design

import java.util.*

class UnitStorage<E>(val unitToStorage: MutableMap<E, MutableMap<String, Any>> = HashMap<E, MutableMap<String, Any>>()) {
    fun <T, EE : E> getOrPut(unit: EE, key: String, init: EE.() -> T): T {
        val unitStorage = unitToStorage.getOrPut(unit, { HashMap<String, Any>() })
        return unitStorage.getOrPut(key, { unit.init() as Any }) as T
    }

    fun <T, EE: E> put(unit: EE, key: String, derived: T) {
        val unitStorage = unitToStorage.getOrPut(unit, { HashMap<String, Any>() })
        unitStorage.put(key, derived as Any)
    }
}

val storage = UnitStorage<ElementIfc>()

enum class DerivedNames {
    API, API_BASE, IMPL, IMPL_BASE, ENUM, BEAN, BEAN_BASE, DSL_T, DSL_D
}

val CompilationUnitD.apiBase: TypeDerived<CompilationUnitD>
    get() = storage.getOrPut(this, DerivedNames.API_BASE.name, { TypeDerived(this, "${name}Base", true) })

val CompilationUnitD.api: TypeDerived<CompilationUnitD>
    get() = storage.getOrPut(this, DerivedNames.API.name, { TypeDerived(this, name) })

val CompilationUnitD.implBase: TypeDerived<CompilationUnitD>
    get() = storage.getOrPut(this, DerivedNames.IMPL_BASE.name, { TypeDerived(this, "${name}ImplBase", true) })

val CompilationUnitD.impl: TypeDerived<CompilationUnitD>
    get() = storage.getOrPut(this, DerivedNames.IMPL.name, { TypeDerived(this, "${name}Impl") })

val CompilationUnitD.dslD: TypeDerived<CompilationUnitD>
    get() = storage.getOrPut(this, DerivedNames.DSL_D.name, { TypeDerived(this, "${name}D") })

val CompilationUnitD.dslT: TypeDerived<CompilationUnitD>
    get() = storage.getOrPut(this, DerivedNames.DSL_T.name, { TypeDerived(this, "${name}T") })

val EnumTypeD.enum: TypeDerived<EnumTypeD>
    get() = storage.getOrPut(this, DerivedNames.ENUM.name, { TypeDerived(this, name, true) })
