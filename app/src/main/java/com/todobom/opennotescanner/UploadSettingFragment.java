package com.todobom.opennotescanner;


import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class UploadSettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "UploadSettingFragment";

    private ListPreference listPreference;
    private EditTextPreference addressPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listPreference = findPreference("upload_option");
        addressPreference = findPreference("upload_address");

        if (listPreference != null) {
            setAddressTitle(listPreference.getValue(),
                    getResources().getStringArray(R.array.upload_values));
            listPreference.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.upload_preferences, rootKey);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange: " + newValue);
        String[] uploadValues = getResources().getStringArray(R.array.upload_values);
        if (preference == listPreference) {
            if (!newValue.equals(uploadValues[4])) {
                addressPreference.setVisible(true);
                addressPreference.setText("");
                setAddressTitle((String) newValue, uploadValues);
            } else {
                addressPreference.setVisible(false);
            }
            return true;
        }
        return false;
    }

    private void setAddressTitle(String value, String[] uploadValues) {
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
        }
    }
}
