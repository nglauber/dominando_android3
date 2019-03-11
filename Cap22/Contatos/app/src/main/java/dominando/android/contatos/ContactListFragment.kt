package dominando.android.contatos

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.ListView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.fragment.app.ListFragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader

class ContactListFragment : ListFragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var contactAdapter: CursorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        contactAdapter = ContactAdapter(requireContext(), null)
        listAdapter = contactAdapter
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }
    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        contactAdapter?.cursor?.let { cursor ->
            cursor.moveToPosition(position)
            val contactId = cursor.getLong(
                cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val lookupKey = cursor.getString(
                cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
            val contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey)
            val it = Intent(Intent.ACTION_VIEW, contactUri)
            startActivity(it)
        }
    }
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(
            requireContext(),
            ContactsContract.Contacts.CONTENT_URI,
            COLUMNS,
            ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1", null,
            ContactsContract.Contacts.DISPLAY_NAME
        )
    }
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        contactAdapter?.swapCursor(data)
    }
    override fun onLoaderReset(loader: Loader<Cursor>) {
        contactAdapter?.swapCursor(null)
    }

    companion object {
        private val COLUMNS = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME
        )
    }
}
