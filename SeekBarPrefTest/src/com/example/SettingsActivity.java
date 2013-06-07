package com.example;

import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.example.seekbarpreftest.R;

public class SettingsActivity extends PreferenceActivity {

    public static class Fragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, true);
            addPreferencesFromResource(R.xml.settings);
        }
    }
    
    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        
        loadHeadersFromResource(R.xml.preference_header, target);
    }
}
