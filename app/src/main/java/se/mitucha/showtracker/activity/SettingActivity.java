package se.mitucha.showtracker.activity;

import android.app.Activity;
import android.os.Bundle;

import se.mitucha.showtracker.fragment.SettingFragment;

/**
 * Created by mr11011 on 2014-08-07.
 */
public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingFragment()).commit();
    }
}
