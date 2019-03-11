package dominando.android.enghaw

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.core.util.Pair
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dominando.android.enghaw.model.Album
import dominando.android.enghaw.model.AlbumHttp
import kotlinx.android.synthetic.main.album_list.*
import kotlinx.android.synthetic.main.item_album.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumListWebFragment : AlbumListBaseFragment() {
    private var albums: List<Album>? = null
    private var downloadJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swpRefresh.setOnRefreshListener {
            loadAlbumsAsync()
        }
        rvAlbums.run {
            tag = "web" // será utilizado no capítulo de testes
            setHasFixedSize(true)
            val orientation = resources.configuration.orientation
            layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                LinearLayoutManager(activity)
            } else {
                GridLayoutManager(activity, 2)
            }
            addItemDecoration(
                DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            )
            itemAnimator = DefaultItemAnimator()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (albums == null) {
            if (downloadJob?.isActive == true) {
                showProgress(true)
            } else {
                loadAlbumsAsync()
            }
        } else {
            updateList()
        }
    }

    private fun loadAlbumsAsync() {
        downloadJob = launch {
            IdleResource.instance.increment()
            showProgress(true)
            albums = withContext(Dispatchers.IO) {
                AlbumHttp.loadAlbums()?.toList()
            }
            IdleResource.instance.decrement()
            showProgress(false)
            updateList()
            downloadJob = null
        }
    }

    private fun onItemClick(v: View, album: Album, position: Int) {
        activity?.run {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                Pair.create(v.imgCover, "cover${album.title}"),
                Pair.create(v.txtTitle, "title${album.title}"),
                Pair.create(v.txtYear, "year${album.title}")
            )
            val intent = Intent(this, DetailsActivity::class.java).apply {
                putExtra(DetailsActivity.EXTRA_ALBUM, album)
            }
            ActivityCompat.startActivity(this, intent, options.toBundle())
        }
    }

    private fun updateList() {
        val list = albums ?: emptyList()
        rvAlbums.adapter = AlbumAdapter(list, this::onItemClick)
    }
    private fun showProgress(show: Boolean) {
        swpRefresh.post { swpRefresh.isRefreshing = show }
    }
}
