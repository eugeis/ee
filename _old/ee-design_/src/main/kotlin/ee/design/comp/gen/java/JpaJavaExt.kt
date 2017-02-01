package ee.design.comp.gen.java

import ee.common.ext.ifElse
import ee.common.ext.toSql
import ee.common.ext.toUnderscoredUpperCase
import ee.design.comp.Entity
import ee.design.comp.ExternalModule
import ee.design.comp.gen.bean
import ee.design.*
import ee.design.gen.java.*

object Jpa : StructureUnit("Jpa") {
    object Persistence : ExternalModule("javax.persistence") {
        val CascadeType = type()
        val CollectionTable = type()
        val Column = type()
        val ElementCollection = type()
        val Embeddable = type()
        val Embedded = type()
        val Entity = type()
        val EntityManager = type()
        val EntityManagerFactory = type()
        val Enumerated = type()
        val FetchType = type()
        val GeneratedValue = type()
        val GenerationType = type()
        val Id = type()
        val Index = type()
        val JoinColumn = type()
        val JoinTable = type()
        val Lob = type()
        val ManyToMany = type()
        val ManyToOne = type()
        val MappedSuperclass = type()
        val NamedQueries = type()
        val NamedQuery = type()
        val OneToMany = type()
        val OneToOne = type()
        val PersistenceContext = type()
        val Table = type()
        val TableGenerator = type()
        val Temporal = type()
        val TemporalType = type()
        val Transactional = type()
        val Transient = type()
        val Version = type()

        fun Column(name: String): ExternalType {
            return type()
        }
    }
}

fun Attribute.toJavaStaticColumnName(context: JavaContext): String {
    return "COLUMN_${name.toUnderscoredUpperCase().toUpperCase()}"
}

fun Attribute.toJavaStaticColumnValue(context: JavaContext): String {
    return name.toSql()
}

fun Attribute.toJavaStaticColumnMember(context: JavaContext, indent: String = ""): String {
    return """${indent}public static final String ${toJavaStaticColumnName(context)} = "${toJavaStaticColumnValue(context)}";"""
}

fun Attribute.toJavaBeanMappings(context: JavaContext, indent: String = "", derived: TypeDerived<Entity>): String {
    return """$nL${context.ann(Jpa.Persistence.Column(toJavaStaticColumnName(context)), indent)}$nL"""
}

fun Entity.toJavaBeanMappings(context: JavaContext, indent: String = "", derived: TypeDerived<Entity>): String {
    return """${derived.base.ifElse({ context.ann(Jpa.Persistence.MappedSuperclass, indent) }, { context.ann(Jpa.Persistence.Entity, indent) })}"""
}

//completed

fun Entity.toJavaBean(context: JavaContext, indent: String = "", derived: TypeDerived<Entity> = bean): String {
    val newIndent = "$indent$tab"
    return context.complete("""${toJavaBeanMappings(context, indent, derived)}
${indent}public class ${bean.name} implements ${api.name} {
${serialVersionUID(true, newIndent)}
${props.joinToString(nL) { it.toJavaStaticColumnMember(context, newIndent) }}

${props.joinToString(nL) { it.toJavaMember(context, newIndent, it.toJavaBeanMappings(context, newIndent, derived)) }}
${props.joinToString(nL) { it.toJavaGetterImpl(context, newIndent, override(newIndent)) }}
${props.joinToString(nL) { it.toJavaSetterImpl(context, newIndent, override(newIndent)) }}
$indent}
""", indent)
}