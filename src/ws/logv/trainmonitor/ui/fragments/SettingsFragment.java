/*
 * Copyright 2012. Kolja Dummann <k.dummann@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ws.logv.trainmonitor.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.app.Constants;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 10.12.12
 * Time: 06:51
 * To change this template use File | Settings | File Templates.
 */
public class SettingsFragment extends PreferenceFragment {
    public SettingsFragment()
    {
        super();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        getPreferenceScreen().findPreference(Constants.Settings.LICENSE).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String content;
                InputStream input = preference.getContext().getResources().openRawResource(R.raw.license);
                try {
                    byte[] data = new byte[input.available()];
                    input.read(data);
                    content = new String(data);
                } catch (IOException e) {
                    content = "Error reading license file! \r\n" + e.toString();
                }
                new AlertDialog.Builder(preference.getContext()).setTitle(R.string.settings_about_license).setMessage(content).show();
                return true;
            }
        });

    }
}
