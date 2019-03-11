package dominando.android.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class FavoritesWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context,
                          appWidgetManager: AppWidgetManager,
                          appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val views = RemoteViews(
            context.packageName, R.layout.widget_favorites)
        for (appWidgetId in appWidgetIds) {
            initButtons(context, appWidgetId, views)
        }
        appWidgetManager.updateAppWidget(appWidgetIds, views)
    }
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val it = Intent(context, FavoritesReceiver::class.java)
        it.putExtra(EXTRA_ACTION, ACTION_DELETE)
        it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        context.sendBroadcast(it)
    }
    private fun initButtons(context: Context, appWidgetId: Int, views: RemoteViews) {
        views.setOnClickPendingIntent(R.id.btnNext,
            actionPendingIntent(context, ACTION_NEXT, appWidgetId))
        views.setOnClickPendingIntent(R.id.btnPrevious,
            actionPendingIntent(context, ACTION_PREVIOUS, appWidgetId))
        views.setOnClickPendingIntent(R.id.txtSite,
            actionPendingIntent(context, ACTION_SITE, appWidgetId))
    }
    private fun actionPendingIntent(
        ctx: Context, action: String, appWidgetId: Int): PendingIntent {
        val actionIntent = Intent(ctx, FavoritesReceiver::class.java)
        actionIntent.putExtra(EXTRA_ACTION, action)
        actionIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        return PendingIntent.getBroadcast(
            ctx, appWidgetId+action.hashCode(), actionIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    companion object {
        const val EXTRA_ACTION = "acao"
        const val ACTION_PREVIOUS = "anterior"
        const val ACTION_NEXT = "proximo"
        const val ACTION_DELETE = "excluir"
        const val ACTION_SITE = "site"
    }
}
