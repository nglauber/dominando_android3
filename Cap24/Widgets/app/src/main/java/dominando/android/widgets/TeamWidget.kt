package dominando.android.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

class TeamWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_CLICK == intent.action) {
            val url = intent.getStringExtra(EXTRA_URL)
            val it = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
        super.onReceive(context, intent)
    }
    override fun onUpdate(context: Context,
                          appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, TeamService::class.java)
            val rv = RemoteViews(
                context.packageName, R.layout.widget_teams)
            rv.setRemoteAdapter(R.id.lstTeams, intent)
            val it = Intent(context, TeamWidget::class.java)
            it.action = ACTION_CLICK
            it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pit = PendingIntent.getBroadcast(
                context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
            rv.setPendingIntentTemplate(R.id.lstTeams, pit)
            appWidgetManager.updateAppWidget(appWidgetId, rv)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
    companion object {
        const val ACTION_CLICK = "chamar_url"
        const val EXTRA_URL = "url"
    }
}
