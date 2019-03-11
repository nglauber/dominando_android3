package dominando.android.enghaw

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dominando.android.enghaw.db.AlbumRepository
import dominando.android.enghaw.model.Album
import kotlinx.android.synthetic.main.album_list.*
import kotlinx.android.synthetic.main.item_album.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumListDbFragment : AlbumListBaseFragment() {
    private val repo: AlbumRepository by lazy {
        AlbumRepository(requireContext())
    }
    private var albumList: LiveData<List<Album>>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swpRefresh.isEnabled = false
        rvAlbums.setHasFixedSize(true)
        rvAlbums.tag = "fav" // será utilizado no capítulo de testes

        rvAlbums.layoutManager =
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    LinearLayoutManager(requireActivity())
                } else {
                    GridLayoutManager(requireActivity(), 2)
                }
        rvAlbums.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )
        rvAlbums.itemAnimator = DefaultItemAnimator()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState);
        launch {
            IdleResource.instance.increment()
            albumList = withContext(Dispatchers.IO) { repo.loadFavorites() }
            albumList?.observe(this@AlbumListDbFragment, Observer { albums ->
                if (albums != null) {
                    updateList(albums)
                }
            })
            IdleResource.instance.decrement()
        }
    }

    private fun updateList(albums: List<Album>) {
        rvAlbums.adapter = AlbumAdapter(albums, this::onItemClick)
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
}