package ee.system

open class JmxServiceQueries<T : JmxService> : JmxServiceQueriesBase<T> {
    companion object {
        val EMPTY = JmxServiceQueriesBase.EMPTY
    }
    constructor(): super() {

    }


}

