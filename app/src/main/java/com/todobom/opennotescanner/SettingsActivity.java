package com.todobom.opennotescanner;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.todobom.opennotescanner.helpers.AppConstants;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TAG = "SettingsActivity";
    private static final String FRAGMENT_TAG_SAVE = "SaveSettings";

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
            // https://stackoverflow.com/questions/9294603/how-do-i-get-the-currently-displayed
            // -fragment
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_SAVE);
            if (fragment != null && fragment.isVisible()) {
                SaveSettingFragment saveSettingFragment = (SaveSettingFragment) fragment;
                if (saveSettingFragment.isAddressEmpty()) {
                    Log.d(TAG, "onOptionsItemSelected: address not set");
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.address_warn_dialog_title)
                            .setMessage(R.string.address_warn_dialog_message)
                            .setNeutralButton(android.R.string.ok,
                                    (dialog, which) -> dialog.cancel())
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
        final Fragment fragment =
                getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(),
                        pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // replace the existing fragment with the new fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment, FRAGMENT_TAG_SAVE)
                .addToBackStack(null)
                .commit();

        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

        private ListPreference pageSizePreference;
        private ListPreference fileFormatPreference;
        private Preference aboutPreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            fileFormatPreference = findPreference(getString(R.string.pref_key_file_format));
            pageSizePreference = findPreference(getString(R.string.pref_key_page_size));

            aboutPreference = findPreference(getString(R.string.pref_key_about));
            if (aboutPreference != null) {
                aboutPreference.setOnPreferenceClickListener(this);
            }
            if (fileFormatPreference != null) {
                fileFormatPreference.setEntryValues(AppConstants.FILE_FORMAT_VALUES);
                fileFormatPreference.setOnPreferenceChangeListener(this);

                if (pageSizePreference != null) {
                    pageSizePreference.setEntryValues(AppConstants.PAGE_SIZE_VALUES);
                    setPageSizeVisible(fileFormatPreference.getValue());
                }
            }
        }

        private void setPageSizeVisible(String value) {
            if (!value.equals(AppConstants.FILE_SUFFIX_PDF)) {
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

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Log.d(TAG, "onPreferenceClick: ");
            if (preference == aboutPreference) {
                FragmentManager fm = getFragmentManager();
                AboutFragment aboutDialog = new AboutFragment();
                if (fm != null) {
                    aboutDialog.show(fm, "about_view");
                }
                return true;
            }
            return false;
        }
    }
}