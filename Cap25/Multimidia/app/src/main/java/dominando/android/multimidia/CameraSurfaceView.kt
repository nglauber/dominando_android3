package dominando.android.multimidia

import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import java.io.IOException

class CameraSurfaceView(context: Context, private val camera: Camera) :
    SurfaceView(context), SurfaceHolder.Callback {

    var previewSizeReadyCallback: ((Int, Int)->Unit)? = null
    private var previewSize: Camera.Size? = null

    init {
        holder.addCallback(this)
    }
    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            camera.setPreviewDisplay(holder)
            camera.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        if (holder.surface == null) {
            return
        }
        try {
            camera.stopPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            val parameters = camera.parameters
            parameters.setPreviewSize(previewWidth(), previewHeight())
            camera.parameters = parameters
            camera.setPreviewDisplay(holder)
            camera.startPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = View.resolveSize(suggestedMinimumHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
        try {
            if (camera.parameters.supportedPreviewSizes != null) {
                previewSize = getOptimalPreviewSize(
                    camera.parameters.supportedPreviewSizes, width, height
                )
                previewSizeReadyCallback?.invoke(previewWidth(), previewHeight())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun previewWidth() = previewSize?.width ?: 0
    private fun previewHeight() = previewSize?.height ?: 0

    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, w: Int, h: Int): Camera.Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = h.toDouble() / w
        if (sizes == null) return null
        var optimalSize: Camera.Size? = null
        var minDiff = Double.MAX_VALUE
        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - h).toDouble()
            }
        }
        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - h).toDouble()
                }
            }
        }
        return optimalSize
    }
}

