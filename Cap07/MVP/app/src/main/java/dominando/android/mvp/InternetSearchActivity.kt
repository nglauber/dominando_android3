package dominando.android.mvp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class InternetSearchActivity: AppCompatActivity(), InternetSearchView {
    val presenter = InternetSearchPresenter(this, GoogleInternetSearch())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonSearch.setOnClickListener {
            presenter.search(editText.text.toString())
        }
    }
    override fun showProgress() {
        // Exibir mensagem de progresso (ex.: procurando...)
    }
    override fun hideProgress() {
        // Ocultar progresso...
    }
    override fun showResults(results: List<SearchResult>) {
        // Exibir o resultado (em uma RecyclerView, por exemplo)
    }
    override fun showSearchError() {
        // Exibir mensagem de erro
    }
}
