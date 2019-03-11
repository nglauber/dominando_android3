package dominando.android.youtube

import android.os.Bundle
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : YouTubeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        youtubePlayerView.initialize(API_KEY,
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationFailure(
                    provider: YouTubePlayer.Provider,
                    result: YouTubeInitializationResult) {
                    Toast.makeText(this@MainActivity,
                        "Erro ao reproduzir vídeo",
                        Toast.LENGTH_SHORT).show()
                }
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider,
                    player: YouTubePlayer,
                    wasRestored: Boolean) {
                    if (!wasRestored) {
                        player.cueVideo(VIDEO_ID)
                    }
                }
            })
    }
    companion object {
        private const val API_KEY = "SUA_API_KEY_AQUI"
        private const val VIDEO_ID = "FHZ6bI3zb4M"
    }
}

