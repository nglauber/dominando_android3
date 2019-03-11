package dominando.android.hotel.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import dominando.android.hotel.R
import dominando.android.hotel.common.BaseActivity
import kotlinx.android.synthetic.main.fragment_user_profile.*
import org.koin.android.ext.android.inject

class UserProfileFragment : DialogFragment() {
    private val authManager: AuthManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val acct = authManager.getUserAccount()
        if (acct != null) {
            Glide.with(imgPhoto.context).load(acct.photoUrl).into(imgPhoto)
            txtName.text = acct.displayName
            txtEmail.text = acct.email
        } else {
            logout()
        }
        btnOk.setOnClickListener { dismiss() }
        btnLogout.setOnClickListener {
            authManager.signOut {
                logout()
            }
        }
        dialog.setTitle(R.string.action_user_profile)
    }
    private fun logout() {
        dismiss()
        if (activity is BaseActivity){
            (activity as BaseActivity).verifyUserLoggedIn()
        }
    }
    fun open(fm: FragmentManager) {
        val tag = "userProfileDialog"
        if (fm.findFragmentByTag(tag) == null) {
            show(fm, tag)
        }
    }
}
