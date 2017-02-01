package ee.system

open class JavaServiceQueries<T : JavaService> : JavaServiceQueriesBase<T> {
    companion object {
        val EMPTY = JavaServiceQueriesBase.EMPTY
    }
    constructor(): super() {

    }


}

