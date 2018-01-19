package ee.system

open class ServiceQueries<T : Service> : ServiceQueriesBase<T> {
    companion object {
        val EMPTY = ServiceQueriesBase.EMPTY
    }

    constructor() : super() {

    }


}

