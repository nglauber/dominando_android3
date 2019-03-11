package dominando.android.mvp

class InternetSearchPresenter(
    val view: InternetSearchView,
    val internetSearch: InternetSearch) {

    fun search(term: String) {
        view.showProgress()
        try {
            internetSearch.search(term, object: SearchResultListener {
                override fun onSearchResult(results: List<SearchResult>) {
                    view.hideProgress()
                    view.showResults(results)
                }
            })
        } catch (e: Exception) {
            view.hideProgress()
            view.showSearchError()
        }
    }
}