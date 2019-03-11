package dominando.android.persistencia

import android.os.Bundle
import androidx.preference.*

class ConfigFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    private lateinit var editPrefCity: EditTextPreference
    private lateinit var listPrefSocialNetworks: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preferences, null)
        editPrefCity = findPreference(getString(R.string.pref_city)) as EditTextPreference
        listPrefSocialNetworks = findPreference(getString(R.string.pref_social_network)) as ListPreference
        fillSummary(editPrefCity)
        fillSummary(listPrefSocialNetworks)
    }
    private fun fillSummary(preference: Preference) {
        preference.onPreferenceChangeListener = this
        val pref = PreferenceManager.getDefaultSharedPreferences(activity)
        val value = pref.getString(preference.key, "")
        onPreferenceChange(preference, value)
    }
    override fun onPreferenceChange(
        preference: Preference, newValue: Any): Boolean {
        val stringValue = newValue.toString()
        if (preference == listPrefSocialNetworks) {
            val index = listPrefSocialNetworks.findIndexOfValue(stringValue)
            if (index >= 0) {
                listPrefSocialNetworks.summary = listPrefSocialNetworks.entries[index]
            }
        } else if (preference == editPrefCity) {
            editPrefCity.summary = stringValue
        }
        return true
    }
}
