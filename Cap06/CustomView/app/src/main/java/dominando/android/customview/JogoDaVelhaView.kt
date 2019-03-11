package dominando.android.customview

import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

class JogoDaVelhaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    style: Int = 0
) : View(context, attrs, style) {

    private var tamanho: Int = 0
    private var vez: Int = XIS
    private var tabuleiro = Array(3) { IntArray(3) }
    private val rect: RectF = RectF()
    private lateinit var paint: Paint
    private lateinit var imageX: Bitmap
    private lateinit var imageO: Bitmap

    private lateinit var detector: GestureDetector
    var listener : JogoDaVelhaListener? = null

    var corDaBarra : Int
    var larguraBarra : Float

    init {
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.JogoDaVelhaView)
        corDaBarra = styledAttrs.getColor(R.styleable.JogoDaVelhaView_corDaBarra, Color.BLACK)
        larguraBarra = styledAttrs.getDimension(R.styleable.JogoDaVelhaView_larguraDaBarra, 3F)
        styledAttrs.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        imageX = BitmapFactory.decodeResource(resources, R.drawable.x_mark)
        imageO = BitmapFactory.decodeResource(resources, R.drawable.o_mark)
        detector = GestureDetector(context, VelhaTouchListener())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        tamanho = when (layoutParams.width) {
            ViewGroup.LayoutParams.WRAP_CONTENT -> {
                (TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 48F,
                    resources.displayMetrics
                ) * 3).toInt()
            }
            ViewGroup.LayoutParams.MATCH_PARENT ->
                Math.min(
                    View.MeasureSpec.getSize(widthMeasureSpec),
                    View.MeasureSpec.getSize(heightMeasureSpec)
                )
            else -> layoutParams.width
        }
        setMeasuredDimension(tamanho, tamanho)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val quadrante = (tamanho / 3).toFloat()
        val tamanhoF = tamanho.toFloat()
        // Desenhando as linhas
        paint.color = corDaBarra
        paint.strokeWidth = larguraBarra
        // Verticais
        canvas?.drawLine(quadrante, 0F, quadrante, tamanhoF, paint)
        canvas?.drawLine(quadrante * 2, 0F, quadrante * 2, tamanhoF, paint)
        // Horizontais
        canvas?.drawLine(0F, quadrante, tamanhoF, quadrante, paint)
        canvas?.drawLine(0F, quadrante * 2, tamanhoF, quadrante * 2, paint)

        tabuleiro.forEachIndexed { rowIndex, rowValue ->
            rowValue.forEachIndexed { columnIndex, columnValue ->
                val x = (columnIndex * quadrante)
                val y = (rowIndex * quadrante)
                rect.set(x, y, x + quadrante, y + quadrante)
                if (columnValue == XIS) {
                    canvas?.drawBitmap(imageX, null, rect, null)
                } else if (columnValue == BOLA) {
                    canvas?.drawBitmap(imageO, null, rect, null)
                }
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val p = super.onSaveInstanceState()
        return EstadoJogo(p, vez, tabuleiro)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val estado = state as EstadoJogo
        super.onRestoreInstanceState(estado.superState)
        vez = estado.vez
        tabuleiro = estado.tabuleiro
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return detector.onTouchEvent(event)
    }

    fun reiniciarJogo() {
        tabuleiro = Array(3) { IntArray(3) }
        invalidate()
    }

    private fun gameOver() : Int {
        // Horizontais
        if (ganhou(tabuleiro[0][0], tabuleiro[0][1], tabuleiro[0][2])) {
            return tabuleiro[0][0]
        }
        if (ganhou(tabuleiro[1][0], tabuleiro[1][1], tabuleiro[1][2])) {
            return tabuleiro[1][0]
        }
        if (ganhou(tabuleiro[2][0], tabuleiro[2][1], tabuleiro[2][2])) {
            return tabuleiro[2][0]
        }
        // Verticais
        if (ganhou(tabuleiro[0][0], tabuleiro[1][0], tabuleiro[2][0])) {
            return tabuleiro[0][0]
        }
        if (ganhou(tabuleiro[0][1], tabuleiro[1][1], tabuleiro[2][1])) {
            return tabuleiro[0][1]
        }
        if (ganhou(tabuleiro[0][2], tabuleiro[1][2], tabuleiro[2][2])) {
            return tabuleiro[0][2]
        }
        // Diagonais
        if (ganhou(tabuleiro[0][0], tabuleiro[1][1], tabuleiro[2][2])) {
            return tabuleiro[0][0]
        }
        if (ganhou(tabuleiro[0][2], tabuleiro[1][1], tabuleiro[2][0])) {
            return tabuleiro[0][2]
        }
        // Existem espaços vazios
        if (tabuleiro.flatMap { it.asList() }.any { it == VAZIO }) {
            return VAZIO
        }
        return EMPATE
    }

    private fun ganhou(a : Int, b : Int, c : Int) = (a == b && b == c && a != VAZIO)

    interface JogoDaVelhaListener {
        fun fimDeJogo(vencedor : Int)
    }

    inner class VelhaTouchListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            var vencedor = gameOver()
            if (e?.action == MotionEvent.ACTION_UP && vencedor == VAZIO) {
                val quadrante = tamanho / 3
                val linha = (e.y / quadrante).toInt()
                val coluna = (e.x / quadrante).toInt()
                if (tabuleiro[linha][coluna] == VAZIO) {
                    tabuleiro[linha][coluna] = vez
                    vez = if (vez == XIS) BOLA else XIS
                    invalidate()
                    vencedor = gameOver()
                    if (vencedor != VAZIO) {
                        listener?.fimDeJogo(vencedor)
                    }
                    return true
                }
            }
            return super.onSingleTapUp(e)
        }
    }

    class EstadoJogo : BaseSavedState {
        var vez: Int
        var tabuleiro: Array<IntArray>

        constructor(superState: Parcelable?, vez: Int, tabuleiro: Array<IntArray>) : super(superState) {
            this.vez = vez
            this.tabuleiro = tabuleiro
        }
        constructor(p: Parcel?) : super(p) {
            vez = p?.readInt() ?: XIS
            tabuleiro = Array(3) { IntArray(3) }
            tabuleiro.forEach { p?.readIntArray(it) }
        }
        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(vez)
            tabuleiro.forEach { out?.writeIntArray(it) }
        }
        companion object CREATOR : Parcelable.Creator<EstadoJogo> {
            override fun createFromParcel(source: Parcel?) = EstadoJogo(source)
            override fun newArray(size: Int) = arrayOf<EstadoJogo>()
        }
    }

    companion object {
        const val XIS = 1
        const val BOLA = 2
        const val VAZIO = 0
        const val EMPATE = 3
    }
}
