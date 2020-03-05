package com.todobom.opennotescanner;


import android.os.Bundle;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.todobom.opennotescanner.helpers.AppConstants;

public class SaveSettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "SaveSettingFragment";

    private ListPreference optionPreference;
    private EditTextPreference addressPreference;
    private boolean isAddressEmpty;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.save_preferences, rootKey);

        optionPreference = findPreference("save_option");
        addressPreference = findPreference("save_address");

        if (addressPreference != null) {
            setAddressEmpty();
            addressPreference.setOnPreferenceChangeListener(this);
        }
        if (optionPreference != null) {
            optionPreference.setEntryValues(AppConstants.SAVE_OPTION_VALUES);
            setAddressTitle(optionPreference.getValue());
            optionPreference.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange: " + newValue);
        if (preference == optionPreference) {
            // deletes the address if a new save option is chosen
            if (!optionPreference.getValue().equals(newValue)) {
                addressPreference.setText("");
            }

            setAddressTitle((String) newValue);
            return true;
        } else if (preference == addressPreference) {
            isAddressEmpty = newValue.equals("");
            return true;
        }
        return false;
    }


    private void setAddressTitle(String value) {
        // updates titles for current save option
        switch (value) {
            case AppConstants.DRACOON:
                addressPreference.setTitle(R.string.dracoon_title);
                addressPreference.setDialogTitle(R.string.dracoon_title);
                break;
            case AppConstants.NEXTCLOUD:
                addressPreference.setTitle(R.string.nextcloud_title);
                addressPreference.setDialogTitle(R.string.nextcloud_title);
                break;
            case AppConstants.EMAIL:
                addressPreference.setTitle(R.string.email_title);
                addressPreference.setDialogTitle(R.string.email_title);
                break;
            case AppConstants.FTP_SERVER:
                addressPreference.setTitle(R.string.ftp_title);
                addressPreference.setDialogTitle(R.string.ftp_title);
                break;
            case AppConstants.LOCAL:
                addressPreference.setTitle(R.string.local_title);
                addressPreference.setDialogTitle(R.string.local_title);
                addressPreference.setText(AppConstants.DEFAULT_FOLDER_NAME);
                break;
            default:
                Log.e(TAG, "setAddressTitle: unknown value");
        }
        setAddressEmpty();
    }

    public boolean isAddressEmpty() {
        return isAddressEmpty;
    }

    private void setAddressEmpty() {
        // on initial app start text of addressPreference is null
        if (addressPreference.getText() != null) {
            isAddressEmpty = addressPreference.getText().equals("");
        }
    }
}
