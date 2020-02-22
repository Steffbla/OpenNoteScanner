package com.todobom.opennotescanner;


import android.os.Bundle;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class UploadSettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "UploadSettingFragment";

    private ListPreference listPreference;
    private EditTextPreference addressPreference;
    private boolean isAddressEmpty;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.upload_preferences, rootKey);

        listPreference = findPreference("upload_option");
        addressPreference = findPreference("upload_address");

        if (addressPreference != null) {
            setAddressEmpty();
            addressPreference.setOnPreferenceChangeListener(this);
        }
        if (listPreference != null) {
            setAddressTitle(listPreference.getValue());
            listPreference.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange: " + newValue);
        if (preference == listPreference) {
            // deletes the address if a new upload option is chosen
            if (!listPreference.getValue().equals(newValue)) {
                addressPreference.setText("");
            }

            setAddressTitle((String) newValue);
            setAddressEmpty();
            return true;
        } else if (preference == addressPreference) {
            isAddressEmpty = newValue.equals("");
            return true;
        }
        return false;
    }


    private void setAddressTitle(String value) {
        // updates titles for current upload option
        String[] uploadValues = getResources().getStringArray(R.array.upload_values);
        if (value.equals(uploadValues[0])) {
            addressPreference.setTitle(R.string.dracoon_title);
            addressPreference.setDialogTitle(R.string.dracoon_title);
        } else if (value.equals(uploadValues[1])) {
            addressPreference.setTitle(R.string.nextcloud_title);
            addressPreference.setDialogTitle(R.string.nextcloud_title);
        } else if (value.equals(uploadValues[2])) {
            addressPreference.setTitle(R.string.email_title);
            addressPreference.setDialogTitle(R.string.email_title);
        } else if (value.equals(uploadValues[3])) {
            addressPreference.setTitle(R.string.ftp_title);
            addressPreference.setDialogTitle(R.string.ftp_title);
        } else if (value.equals(uploadValues[4])) {
            addressPreference.setTitle(R.string.local_title);
            addressPreference.setDialogTitle(R.string.local_title);
            addressPreference.setText("OpenNoteScanner");
        } else {
            Log.e(TAG, "setAddressTitle: unknown value");
        }
        setAddressEmpty();
    }

    public boolean isAddressEmpty() {
        return isAddressEmpty;
    }

    private void setAddressEmpty() {
        isAddressEmpty = addressPreference.getText().equals("");
    }
}
