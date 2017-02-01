package ee.design.gen

import ee.design.ExternalModule
import ee.design.ExternalTypeI

object jpa : ExternalModule({ namespace("javax.persistence") }) {
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

    fun Column(name: String): ExternalTypeI {
        return Column
    }
}