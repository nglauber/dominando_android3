package dominando.android.enghaw

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Transition
import android.view.ViewTreeObserver
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dominando.android.enghaw.db.AlbumRepository
import dominando.android.enghaw.model.Album
import dominando.android.enghaw.model.AlbumHttp
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.content_details.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class DetailsActivity : AppCompatActivity(), CoroutineScope {
    private var coverTarget: Target? = null
    private var album: Album? = null
    private val repo: AlbumRepository by lazy {
        AlbumRepository(this)
    }
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        job = Job()
        val album = intent.getParcelableExtra<Album>(EXTRA_ALBUM)
        if (album != null) {
            this.album = album
            initTitleBar(album.title)
            fillFields(album)
            initEnterAnimation(album)
            loadCover(album)
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBackPressed() {
        fabFavorite.animate()
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(100L)
            .withEndAction {
                val params = fabFavorite.layoutParams as? CoordinatorLayout.LayoutParams
                if (params != null) {
                    params.behavior = null
                    fabFavorite.requestLayout()
                }
                super.onBackPressed()
            }
    }

    private fun initEnterAnimation(album: Album) {
        imgCover.transitionName = "cover${album.title}"
        txtTitle.transitionName = "title${album.title}"
        txtYear.transitionName = "year${album.title}"
        postponeEnterTransition()
    }

    private fun loadCover(album: Album) {
        coverTarget = object: Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                imgCover.setImageBitmap(bitmap)
                setUiColors(bitmap)
                // startEnterAnimation()
            }
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                startEnterAnimation()
            }
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        }.apply {
            Picasso.get()
                .load(AlbumHttp.BASE_URL + album.coverBig)
                .into(this)
        }
    }

    private fun startEnterAnimation() {
        imgCover.viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                imgCover.viewTreeObserver.removeOnPreDrawListener(this)
                startPostponedEnterTransition()
                window.enterTransition.addListener(object: Transition.TransitionListener {
                    override fun onTransitionStart(transition: Transition?) {
                        fabFavorite.scaleX = 0f
                        fabFavorite.scaleY = 0f
                    }
                    override fun onTransitionEnd(transition: Transition?) {
                        fabFavorite.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .duration = 100L
                    }
                    override fun onTransitionCancel(transition: Transition?) { }
                    override fun onTransitionPause(transition: Transition?) {  }
                    override fun onTransitionResume(transition: Transition?) { }
                })
                return true
            }
        })
    }

    private fun initTitleBar(title: String) {
        setSupportActionBar(toolbar)
        if (appBar != null) {
            if (appBar?.layoutParams is CoordinatorLayout.LayoutParams) {
                val lp = appBar?.layoutParams as CoordinatorLayout.LayoutParams
                lp.height = resources.displayMetrics.widthPixels
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (collapseToolbar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(true)
            collapseToolbar?.title = title
        } else {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    private fun fillFields(album: Album) {
        txtTitle.text = album.title
        txtYear.text = album.year.toString()
        txtRecordingCompany.text = album.recordingCompany
        var sb = StringBuilder()
        for (member in album.formation){
            if (sb.isNotEmpty()) sb.append('\n')
            sb.append(member)
        }
        txtFormation.text = sb.toString()
        sb = StringBuilder()
        album.tracks.forEachIndexed { index, track ->
            if (sb.isNotEmpty()) sb.append('\n')
            sb.append(index+1).append(". ").append(track)
        }
        txtSongs.text = sb.toString()
        updateFab()
        fabFavorite.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun setUiColors(bitmap: Bitmap?) {
        if (bitmap == null) {
            startEnterAnimation()
            return
        }
        Palette.from(bitmap).generate { palette ->
            palette?.let {
                val vibrantColor = palette.getVibrantColor(Color.BLACK)
                val darkVibrantColor = palette.getDarkVibrantColor(Color.BLACK)
                val darkMutedColor = palette.getDarkMutedColor (Color.BLACK)
                val lightMutedColor = palette.getLightMutedColor(Color.WHITE)
                txtTitle.setTextColor(vibrantColor)
                if (appBar != null) {
                    appBar?.setBackgroundColor(vibrantColor)
                } else {
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                }
                window.navigationBarColor = darkMutedColor
                if (collapseToolbar != null) {
                    collapseToolbar?.setStatusBarScrimColor(darkMutedColor)
                    collapseToolbar?.setContentScrimColor(darkVibrantColor)
                }
                coordinatorLayout?.setBackgroundColor(lightMutedColor)
            }
            startEnterAnimation()
        }
    }

    private fun toggleFavorite() {
        album?.let {
            launch {
                withContext(Dispatchers.IO) {
                    val isFavorite = repo.isFavorite(it)
                    if (isFavorite) {
                        repo.delete(it)
                    } else {
                        repo.save(it)
                    }
                }
                updateFab()
            }
        }
    }

    private fun updateFab() {
        album?.let {
            launch {
                val isFavorite = withContext(Dispatchers.IO) { repo.isFavorite(it) }
                val icon = getFabIcon(isFavorite)
                fabFavorite.setImageDrawable(icon)
                fabFavorite.backgroundTintList = getFabBackground(isFavorite)
                if (icon is AnimatedVectorDrawable) {
                    icon.start()
                }
            }
        }
    }

    private fun getFabIcon(favorite: Boolean): Drawable? {
        return ContextCompat.getDrawable(this,
            if (favorite)
                R.drawable.avd_check
            else
                R.drawable.avd_clear
        )
    }

    private fun getFabBackground(favorite: Boolean): ColorStateList? {
        return ContextCompat.getColorStateList(this,
            if (favorite)
                R.color.bg_fab_clear
            else
                R.color.bg_fab_favorite
        )
    }

    companion object {
        const val EXTRA_ALBUM = "album"
        fun start(context: Context, album: Album) {
            context.startActivity(Intent(context, DetailsActivity::class.java).apply {
                putExtra(EXTRA_ALBUM, album)
            })
        }
    }
}
