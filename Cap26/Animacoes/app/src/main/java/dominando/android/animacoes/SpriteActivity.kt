package dominando.android.animacoes

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sprite.*

class SpriteActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sprite)
        imgSprite.setOnClickListener {
            val spriteAnimation = imgSprite.background as AnimationDrawable
            if (spriteAnimation.isRunning) {
                spriteAnimation.stop()
            } else {
                spriteAnimation.start()
            }
        }
    }
}
