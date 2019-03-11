package dominando.android.multimidia

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.requestPermissions
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.fragment_camera_detection.*
import java.io.IOException

class CameraDetectionFragment : MultimediaFragment() {
    // Barcode
    private val detector: BarcodeDetector by lazy {
        BarcodeDetector.Builder(activity)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
    }
    private val cameraSource: CameraSource by lazy {
        CameraSource.Builder(activity, detector)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setAutoFocusEnabled(true)
            .setRequestedPreviewSize(800, 512)
            .build()
    }
    // Face
    /*
    private val detector: FaceDetector by lazy {
        FaceDetector.Builder(activity)
            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
            .setMode(FaceDetector.ACCURATE_MODE)
            .setProminentFaceOnly(false)
            .build()
    }
    private val cameraSource: CameraSource by lazy {
        CameraSource.Builder(activity, detector)
            .setFacing(CameraSource.CAMERA_FACING_FRONT)
            .setAutoFocusEnabled(true)
            .setRequestedPreviewSize(800, 512)
            .build()
    }
    */
    // Text
    /*
    private val detector: TextRecognizer by lazy {
        TextRecognizer.Builder(activity).build()
    }
    private val cameraSource: CameraSource by lazy {
        CameraSource.Builder(activity, detector)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setAutoFocusEnabled(true)
            .setRequestedPreviewSize(800, 512)
            .build()
    }*/

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera_detection,
            container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProcessor()
        surfaceView.holder.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }
            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }
            override fun surfaceCreated(holder: SurfaceHolder?) {
                startDetection()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.none { it == PackageManager.PERMISSION_DENIED }) {
            startDetection()
        }
    }

    // Barcode
    private fun initProcessor() {
        detector.setProcessor(object: Detector.Processor<Barcode> {
            override fun release() {
            }
            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barCodes = detections.detectedItems
                if (barCodes.size() != 0) {
                    txtBarCode.post {
                        txtBarCode.text = barCodes.valueAt(0).displayValue
                    }
                }
            }
        })
    }
    // Face
    /*
    private fun initProcessor() {
        detector.setProcessor(object: Detector.Processor<Face> {
            override fun release() {
            }
            override fun receiveDetections(detections: Detector.Detections<Face>) {
                val faces = detections.detectedItems
                if (faces.size() != 0) {
                    var text = ""
                    for (index in 0 until faces.size()) {
                        val face = faces[index]
                        if (face != null) {
                            text += "Face: ${face.id} \n" +
                                    "Smiling: ${face.isSmilingProbability}\n" +
                                    "RightEye: ${face.isRightEyeOpenProbability} \n" +
                                    "LeftEye: ${face.isLeftEyeOpenProbability} "
                        }
                        txtBarCode.post {
                            txtBarCode.text = text
                        }
                    }
                }
            }
        })
    }
    */
    // Text
    /*
    private fun initProcessor() {
        detector.setProcessor(object: Detector.Processor<TextBlock> {
            override fun release() {
            }
            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val texts = detections.detectedItems
                if (texts.size() != 0) {
                    var textOutput = ""
                    for (index in 0 until texts.size()) {
                        val text = texts[index]
                        if (text != null) {
                            textOutput += "$index: ${text.value}\n"
                        }
                    }
                    txtBarCode.post {
                        txtBarCode.text = textOutput
                    }
                }
            }
        })
    }
    */

    @SuppressLint("MissingPermission")
    private fun startDetection() {
        if (hasPermission()) {
            try {
                cameraSource.start(surfaceView.holder)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        } else {
            requestPermissions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.release()
        detector.release()
    }
}
