package dominando.android.mvp

interface InternetSearch {
    fun search(term: String, listener: SearchResultListener)
}