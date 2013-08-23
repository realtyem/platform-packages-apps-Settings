/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.katkiss;
import org.meerkats.katkiss.KKC;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;

import android.provider.Settings;
import android.content.ContentResolver;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.os.Bundle;
import android.os.UserHandle;
import android.app.Dialog;
import android.app.Activity;

import java.util.ArrayList;

public class UISettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "UISettings";

    private static final String KEY_UI_MODE = "kk_ui_mode";
    private static final String KEY_UI_BARSIZE = "kk_ui_barsize";
    private ContentResolver mResolver; 

    private ListPreference _uiModeList, _uiBarSizeList;
    private boolean _prevTabletUIMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.kk_ui_settings);
        _uiModeList = (ListPreference) findPreference(KEY_UI_MODE);
        _uiBarSizeList = (ListPreference) findPreference(KEY_UI_BARSIZE);
        refreshState();

        _uiModeList.setOnPreferenceChangeListener(this);
        _uiBarSizeList.setOnPreferenceChangeListener(this);

    }


    private void refreshState() {
        int uiVal =  Settings.System.getInt(mResolver, KKC.S.SYSTEMUI_UI_MODE, KKC.S.SYSTEMUI_UI_MODE_SYSTEMBAR );
        _prevTabletUIMode = (uiVal == KKC.S.SYSTEMUI_UI_MODE_SYSTEMBAR);
        _uiModeList.setDefaultValue(String.valueOf(uiVal));
        _uiModeList.setValue(String.valueOf(uiVal));

        int uiBarSize =  Settings.System.getInt(mResolver, KKC.S.SYSTEMUI_UI_BARSIZE, KKC.S.SYSTEMUI_BARSIZE_MODE_NORMAL );
        _uiBarSizeList.setDefaultValue(String.valueOf(uiBarSize));
        _uiBarSizeList.setValue(String.valueOf(uiBarSize));
    }
    
    @Override
    public void onResume() {
        super.onResume();

        refreshState();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public Dialog onCreateDialog(int dialogId) {
        return null;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
	if(key == null) return true;

        if (key.equals(KEY_UI_MODE)) 
        {
          int mode = Integer.parseInt((String) objValue);

          boolean tabletUIMode = (mode == KKC.S.SYSTEMUI_UI_MODE_SYSTEMBAR);
          Settings.System.putInt(getContentResolver(), KKC.S.SYSTEMUI_UI_MODE, mode);
          sendIntentToWindowManager(KKC.I.CMD_BARTYPE_CHANGED, true);
          _prevTabletUIMode = tabletUIMode;
        }
        else if(key.equals(KEY_UI_BARSIZE));
        {
          int size = Integer.parseInt((String) objValue);
          Settings.System.putInt(getContentResolver(), KKC.S.SYSTEMUI_UI_BARSIZE, size);
          sendIntentToWindowManager(KKC.I.CMD_BARSIZE_CHANGED, true);
        }
        return true;
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
/*        if (preference == mFontSizePref) {
            if (Utils.hasMultipleUsers(getActivity())) {
                showDialog(DLG_GLOBAL_CHANGE_WARNING);
                return true;
            } else {
                mFontSizePref.click();
            }
        }
*/
        return false;
    }

    private void sendIntentToWindowManager(String cmd, boolean shouldRestartUI) {
        Intent intent = new Intent()
                .setAction(KKC.I.UI_CHANGED)
                .putExtra(KKC.I.CMD,  cmd)
                .putExtra(KKC.I.EXTRA_RESTART_SYSTEMUI, shouldRestartUI);
        getActivity().sendBroadcastAsUser(intent, new UserHandle(UserHandle.USER_ALL));
    }
}
