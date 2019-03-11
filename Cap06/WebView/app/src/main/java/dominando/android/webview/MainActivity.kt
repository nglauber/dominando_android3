package dominando.android.webview

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val settings = webView.settings
        settings.javaScriptEnabled = true
        webView.addJavascriptInterface(this, "dominando")
        webView.loadUrl("file:///android_asset/app_page.html")
    }
    @JavascriptInterface
    fun showToast(s: String, t: String) {
        Toast.makeText(this, "Nome: $s Idade: $t", Toast.LENGTH_SHORT).show();
    }
}
