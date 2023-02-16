package ee.design.gen.angular

import ee.design.EntityI
import ee.design.gen.ts.DesignTsContextFactory
import ee.lang.*
import ee.lang.gen.ts.TsContextBuilder

open class DesignAngularContextFactory : DesignTsContextFactory() {
    override fun buildName(item: ItemI<*>, kind: String): String {
        return if (item is EntityI<*>) {
            buildNameForEntity(item, kind)
        } else if (item is BasicI<*>) {
            buildNameForBasic(item, kind)
        } else {
            super.buildName(item, kind)
        }
    }

    protected open fun buildNameForEntity(item: EntityI<*>, kind: String) = item.dataTypeNameAndParentName().capitalize()
    protected open fun buildNameForBasic(item: BasicI<*>, kind: String) = item.name().capitalize()
}
