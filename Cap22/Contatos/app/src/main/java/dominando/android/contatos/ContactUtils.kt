package dominando.android.contatos

import android.content.ContentProviderOperation
import android.content.Context
import android.content.Intent
import android.content.OperationApplicationException
import android.graphics.Bitmap
import android.os.RemoteException
import android.provider.ContactsContract
import java.io.ByteArrayOutputStream
import java.util.ArrayList

object ContactUtils {
    fun insertContact(context: Context, name: String, phoneNumber: String,
                      address: String, photo: Bitmap) {
        // Lista de operações que serão realizadas em batch
        val operation = ArrayList<ContentProviderOperation>()
        // Armazenará o id interno do contato e servirá para inserir os detalhes
        val backRefIndex = 0
        // Associa o contato à conta-padrão do phoneNumber
        operation.add(
            ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build())
        // Adiciona o name do contato e alimenta id
        operation.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRefIndex)
                .withValue(ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                    name)
                .build())
        // Adiciona um endereço ao contato a partir do id
        operation.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRefIndex)
                .withValue(ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(
                    ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                    address)
                .build())
        // Associa um phoneNumber ao contato do tipo "Home"
        operation.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRefIndex)
                .withValue(ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build())
        // Adiciona imagem ao contato
        val stream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 75, stream)
        operation.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRefIndex)
                .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                .withValue(ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
                    stream.toByteArray())
                .build())
        // Aplica o batch de inclusão
        try {
            context.contentResolver.applyBatch(
                ContactsContract.AUTHORITY, operation)
        } catch (e: RemoteException) {
            e.printStackTrace()
        } catch (e: OperationApplicationException) {
            e.printStackTrace()
        }
    }

    fun insertContactWithApp(context: Context,
                             name: String, phone: String, email: String, address: String) {
        context.startActivity(
            Intent(ContactsContract.Intents.Insert.ACTION).apply {
                setType(ContactsContract.RawContacts.CONTENT_TYPE)
                    .putExtra(ContactsContract.Intents.Insert.NAME, name)
                    .putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                    .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                    .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .putExtra(ContactsContract.Intents.Insert.POSTAL, address)
            })
    }
}
