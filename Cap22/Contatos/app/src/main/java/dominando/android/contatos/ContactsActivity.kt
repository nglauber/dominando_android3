package dominando.android.contatos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import dominando.android.contatos.ContactUtils.insertContactWithApp

class ContactsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
    }
    override fun onResume() {
        super.onResume()
        if (!hasPermission(Manifest.permission.READ_CONTACTS) ||
            !hasPermission(Manifest.permission.WRITE_CONTACTS)) {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_CONTACTS
                ),
                RC_PERMISSION_CONTACT)
        } else {
            init()
        }
    }
    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == RC_PERMISSION_CONTACT && grantResults.isNotEmpty()) {
            if (!grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, R.string.error_permission,
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun init() {
        if (supportFragmentManager.findFragmentByTag(TAG_CONTACT_LIST) == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, ContactListFragment(), TAG_CONTACT_LIST)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.contacts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_new_contact){
            val fragment = ContactFragment()
            fragment.show(supportFragmentManager, "new_contact")

//            ContactUtils.insertContactWithApp(this, "Fulano", "81988990000", "fulano@email.com", "Rua do Bolo")
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG_CONTACT_LIST = "contacts_fragment"
        private const val RC_PERMISSION_CONTACT = 1
    }
}
