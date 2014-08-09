package se.mitucha.showtracker.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import se.mitucha.showtracker.R;

/**
 * Created by mr11011 on 2014-08-07.
 */
public class SettingFragment extends PreferenceFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.setting);
    }

}
