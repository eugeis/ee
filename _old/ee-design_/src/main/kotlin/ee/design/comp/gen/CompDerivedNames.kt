package ee.design.comp.gen

import ee.design.comp.Entity
import ee.design.DerivedNames
import ee.design.TypeDerived
import ee.design.storage

val Entity.beanBase: TypeDerived<Entity>
    get() = storage.getOrPut(this, DerivedNames.BEAN_BASE.name, { TypeDerived(this, "${name}BeanBase", true) })

val Entity.bean: TypeDerived<Entity>
    get() = storage.getOrPut(this, DerivedNames.BEAN.name, { TypeDerived(this, "${name}Entity") })
