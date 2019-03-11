package dominando.android.mvp

interface InternetSearchView {
    fun showProgress()
    fun hideProgress()
    fun showResults(results: List<SearchResult>)
    fun showSearchError()
}
