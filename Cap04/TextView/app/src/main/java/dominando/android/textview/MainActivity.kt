package dominando.android.textview

import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtStrike.paintFlags = txtStrike.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        val htmlText = """
                    <html>
                    <body>Html em <b>Negrito</b>, <i>Itálico</i>
                    e <u>Sublinhado</u>.<br>
                    Mario: <img src='mario.png' /><br>
                    Yoshi: <img src='yoshi.png' /><br>
                    Um texto depois da imagem
                    </body>
                    </html>"""
        val imgGetter = Html.ImageGetter { source ->
            try {
                val bmp = BitmapFactory.decodeStream(assets.open(source))
                val drawable = BitmapDrawable(resources, bmp)
                drawable.setBounds(0, 0, bmp.width, bmp.height)
                drawable
            } catch (e : IOException) {
                e.printStackTrace()
                null
            }
        }
        txtHtml.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT, imgGetter, null)
        } else {
            Html.fromHtml(htmlText, imgGetter, null)
        }
        initTextSpan()
    }

    private fun initTextSpan() {
        val textTitle = " ExemploSpanned"
        val textLarge = "Texto grande"
        val textBold = "Negrito"
        val textUnderline = "Sublinhado"
        val textColored = "Cor do texto"
        val textBackground = "Com Background"
        val textClick = "Click here"
        val textUrl = "www.nglauber.com.br"
        val textComplete = """
            $textTitle
            $textLarge
            $textBold
            $textUnderline
            $textColored
            $textBackground
            $textClick
            $textUrl""".trimIndent()

        val spannableString = SpannableString(textComplete)
        spannableString.setSpan(
            RelativeSizeSpan(2f),
            textComplete.indexOf(textLarge),
            textComplete.indexOf(textLarge) + textLarge.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            textComplete.indexOf(textBold),
            textComplete.indexOf(textBold) + textBold.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString.setSpan(
            UnderlineSpan(),
            textComplete.indexOf(textUnderline),
            textComplete.indexOf(textUnderline) + textUnderline.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            textComplete.indexOf(textColored),
            textComplete.indexOf(textColored) + textColored.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString.setSpan(
            BackgroundColorSpan(Color.YELLOW),
            textComplete.indexOf(textBackground),
            textComplete.indexOf(textBackground) + textBackground.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString.setSpan(
            object: ClickableSpan() {
                override fun onClick(widget: View?) {
                    Toast.makeText(this@MainActivity, "Click!",
                        Toast.LENGTH_SHORT).show()
                }
            },
            textComplete.indexOf(textClick),
            textComplete.indexOf(textClick) + textClick.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString.setSpan(
            URLSpan("http://$textUrl"),
            textComplete.indexOf(textUrl),
            textComplete.indexOf(textUrl) + textUrl.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString.setSpan(
            ImageSpan(this, R.mipmap.ic_launcher),
            0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
        txtSpan.movementMethod = LinkMovementMethod.getInstance() // Apenas para click
        txtSpan.text = spannableString
    }
}
