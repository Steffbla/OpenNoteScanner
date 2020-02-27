package com.todobom.opennotescanner;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TAG = "SettingsActivity";
    private static final String FRAGMENT_TAG_UPLOAD = "UploadSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // https://stackoverflow.com/questions/34222591/navigate-back-from-settings-activity
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            // https://stackoverflow.com/questions/9294603/how-do-i-get-the-currently-displayed-fragment
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_UPLOAD);
            if (fragment != null && fragment.isVisible()) {
                UploadSettingFragment uploadSettingFragment = (UploadSettingFragment) fragment;
                if (uploadSettingFragment.isAddressEmpty()) {
                    Log.d(TAG, "onOptionsItemSelected: address not set");
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.address_warn_dialog_title)
                            .setMessage(R.string.address_warn_dialog_message)
                            .setNeutralButton(android.R.string.ok, (dialog, which) -> dialog.cancel())
                            .create().show();
                    return false;
                }
            }
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // https://developer.android.com/guide/topics/ui/settings/organize-your-settings
        // instantiate the new fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // replace the existing fragment with the new fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment, FRAGMENT_TAG_UPLOAD)
                .addToBackStack(null)
                .commit();

        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

        private ListPreference pageSizePreference;
        private ListPreference fileFormatPreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            fileFormatPreference = findPreference("file_format");
            pageSizePreference = findPreference("page_size");

            if (pageSizePreference != null) {
                fileFormatPreference.setOnPreferenceChangeListener(this);
                setPageSizeVisible(fileFormatPreference.getValue());
            }
        }

        private void setPageSizeVisible(String value) {
            if (!value.equals("pdf")) {
                pageSizePreference.setVisible(false);
            } else {
                pageSizePreference.setVisible(true);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Log.d(TAG, "onPreferenceChange: " + newValue);
            if (preference == fileFormatPreference) {
                setPageSizeVisible((String) newValue);
                return true;
            }
            return false;
        }
    }
}