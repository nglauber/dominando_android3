package dominando.android.widgets

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import java.util.ArrayList

class TeamService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        return TeamRemoteViewsFactory(this.applicationContext)
    }
    internal inner class TeamRemoteViewsFactory(
        private val context: Context
    ) : RemoteViewsService.RemoteViewsFactory {
        private val listTeams = ArrayList<Team>()

        override fun onCreate() {
            listTeams.add(Team("América-MG", "america-mg.png",
                "www.americamineiro.com.br"))
            listTeams.add(Team("Atlético-MG", "atl_mg.png",
                "www.atletico.com.br"))
            listTeams.add(Team("Atlético-PR", "atl_pr.png",
                "www.atleticoparanaense.com"))
            listTeams.add(Team("Bahia", "bahia.png",
                "www.esporteclubebahia.com.br"))
            listTeams.add(Team("Botafogo", "botafogo.png",
                "www.botafogo.com.br"))
            listTeams.add(Team("Ceará", "ceara.png",
                "www.cearasc.com"))
            listTeams.add(Team("Chapecoense", "chapecoense.png",
                "chapecoense.com"))
            listTeams.add(Team("Corinthians", "corinthians.png",
                "www.corinthians.com.br"))
            listTeams.add(Team("Cruzeiro", "cruzeiro.png",
                "www.cruzeiro.com.br"))
            listTeams.add(Team("Flamengo", "flamengo.png",
                "www.flamengo.com.br"))
            listTeams.add(Team("Fluminense", "fluminense.png",
                "www.fluminense.com.br"))
            listTeams.add(Team("Grêmio", "gremio.png",
                "www.gremio.net"))
            listTeams.add(Team("Internacional","internacional.png",
                "www.internacional.com.br"))
            listTeams.add(Team("Palmeiras", "palmeiras.png",
                "www.palmeiras.com.br"))
            listTeams.add(Team("Paraná", "parana.png",
                "www.paranaclube.com.br"))
            listTeams.add(Team("Santos", "santos.png",
                "www.santosfc.com.br"))
            listTeams.add(Team("São Paulo", "sao_paulo.png",
                "www.saopaulofc.net"))
            listTeams.add(Team("Sport", "sport.png",
                "www.sportrecife.com.br"))
            listTeams.add(Team("Vasco", "vasco.png",
                "www.crvascodagama.com"))
            listTeams.add(Team("Vitória", "vitoria.png",
                "www.ecvitoria.com.br"))
        }
        override fun onDestroy() {
            listTeams.clear()
        }
        override fun getCount(): Int {
            return listTeams.size
        }
        override fun getViewAt(position: Int): RemoteViews {
            val (name, crest, url) = listTeams[position]
            var bmp: Bitmap? = null
            try {
                bmp = BitmapFactory.decodeStream(context.assets.open(crest))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val rv = RemoteViews(context.packageName, R.layout.item_team_widget)
            rv.setTextViewText(R.id.txtName, name)
            rv.setImageViewBitmap(R.id.imgCrest, bmp)
            val fillInIntent = Intent()
            fillInIntent.putExtra(TeamWidget.EXTRA_URL, "http://$url")
            rv.setOnClickFillInIntent(R.id.root, fillInIntent)
            return rv
        }
        override fun getLoadingView(): RemoteViews? {
            return null
        }
        override fun getViewTypeCount(): Int {
            return 1
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun hasStableIds(): Boolean {
            return false
        }
        override fun onDataSetChanged() {}
    }
}
