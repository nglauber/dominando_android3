package dominando.android.persistencia

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.io.*

@RuntimePermissions
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnRead.setOnClickListener {
            btnReadClick()
        }
        btnSave.setOnClickListener {
            btnSaveClick()
        }
        btnOpenPref.setOnClickListener {
            startActivity(Intent(this, ConfigActivity::class.java))
        }
        btnReadPref.setOnClickListener {
            readPrefs()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showDeniedForExternal() {
        Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
    }

    private fun checkStoragePermission(permission: String, requestCode: Int): Boolean {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toast.makeText(this, R.string.message_permission_requested,Toast.LENGTH_SHORT).show()
            }
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            return false
        }
        return true
    }

    private fun btnReadClick() {
        val type = rgType.checkedRadioButtonId
        when (type) {
            R.id.rbInternal -> loadFromInternal()
            R.id.rbExternalPriv -> loadFromExternalWithPermissionCheck(true)
            R.id.rbExternalPublic -> loadFromExternalWithPermissionCheck(false)
        }
    }

    private fun btnSaveClick() {
        val type = rgType.checkedRadioButtonId
        when (type) {
            R.id.rbInternal -> saveToInternal()
            R.id.rbExternalPriv -> saveToExternalWithPermissionCheck(true)
            R.id.rbExternalPublic -> saveToExternalWithPermissionCheck(false)
        }
    }

    private fun saveToInternal() {
        try {
            val fos = openFileOutput("arquivo.txt", Context.MODE_PRIVATE)
            save(fos)
        } catch (e : Exception) {
            Log.e("NGVL", "Erro ao salvar o arquivo", e)
        }
    }

    private fun loadFromInternal() {
        try {
            val fis = openFileInput("arquivo.txt")
            load(fis)
        } catch (e: Exception) {
            Log.e("NGVL", "Erro ao carregar o arquivo", e)
        }
    }

    private fun getExternalDir(privateDir : Boolean) =
    // SDCard/Android/data/pacote.da.app/files
        if (privateDir) getExternalFilesDir(null)
        // SDCard/DCIM
        else Environment.getExternalStorageDirectory()

    @NeedsPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun saveToExternal(privateDir : Boolean) {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            val myDir = getExternalDir(privateDir)
            try {
                if (myDir?.exists() == false) {
                    myDir.mkdir()
                }
                val txtFile = File(myDir, "arquivo.txt")
                if (!txtFile.exists()) {
                    txtFile.createNewFile()
                }
                val fos = FileOutputStream(txtFile)
                save(fos)
            } catch (e : IOException) {
                Log.d("NGVL", "Erro ao salvar arquivo", e)
            }
        } else {
            Log.e("NGVL", "Não é possível escrever no SD Card")
        }
    }

    @NeedsPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun loadFromExternal(privateDir: Boolean) {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state ||
            Environment.MEDIA_MOUNTED_READ_ONLY == state) {
            val myDir = getExternalDir(privateDir)
            if (myDir?.exists() == true) {
                val txtFile = File(myDir, "arquivo.txt")
                if (txtFile.exists()) {
                    try {
                        txtFile.createNewFile()
                        val fis = FileInputStream(txtFile)
                        load(fis)
                    } catch (e : IOException) {
                        Log.d("NGVL", "Erro ao carregar arquivo", e)
                    }
                }
            }
        } else {
            Log.e("NGVL", "SD Card indisponível")
        }
    }

    private fun save(fos : FileOutputStream) {
        val lines = TextUtils.split(edtText.text.toString(), "\n")
        val writer = PrintWriter(fos)
        for (line in lines) {
            writer.println(line)
        }
        writer.flush()
        writer.close()
        fos.close()
    }

    private fun load(fis : FileInputStream) {
        val reader = BufferedReader(InputStreamReader(fis))
        val sb = StringBuilder()
        do {
            val line = reader.readLine() ?: break
            if (sb.isNotEmpty()) sb.append('\n')
            sb.append(line)
        } while (true)
        reader.close()
        fis.close()
        txtText.text = sb.toString()
    }

    private fun readPrefs() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val city = prefs.getString(
            getString(R.string.pref_city),
            getString(R.string.pref_city_default))
        val socialNetwork = prefs.getString(
            getString(R.string.pref_social_network),
            getString(R.string.pref_social_network_default))
        val messages = prefs.getBoolean(
            getString(R.string.pref_messages), false)
        val msg = String.format("%s = %s\n%s = %s\n%s = %s",
            getString(R.string.title_city), city,
            getString(R.string.title_social_network), socialNetwork,
            getString(R.string.title_messages), messages.toString())
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        val RC_STORAGE_PERMISSION = 0
    }
}
