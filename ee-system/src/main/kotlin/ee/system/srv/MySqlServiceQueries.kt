package ee.system.srv

open class MySqlServiceQueries<T : MySqlService> : MySqlServiceQueriesBase<T> {
    companion object {
        val EMPTY = MySqlServiceQueriesBase.EMPTY
    }
    constructor(): super() {

    }


}

