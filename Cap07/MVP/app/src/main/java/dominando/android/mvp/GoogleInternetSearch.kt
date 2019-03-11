package dominando.android.mvp

class GoogleInternetSearch: InternetSearch {
    override fun search(term: String, listener: SearchResultListener) {
        // Faça a busca...
        // Devolva os resultados
        val results = emptyList<SearchResult>()
        listener.onSearchResult(results)
    }
}