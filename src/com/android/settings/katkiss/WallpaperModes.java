package com.android.settings.katkiss;


import android.app.*;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.app.AlertDialog.Builder;
import com.android.settings.R;
import org.meerkats.katkiss.KatUtils;
import org.meerkats.katkiss.KKC;
import android.content.Intent;
import android.content.res.Resources;
import android.os.UserHandle;
import android.util.Log;



public class WallpaperModes extends DialogFragment implements android.content.DialogInterface.OnClickListener
{
    private ContentResolver mContentRes = null; 

    public WallpaperModes() { }

    public void onCancel(DialogInterface dialoginterface)
    {
        getActivity().finish();
    }

    public void onClick(DialogInterface dialoginterface, int i)
    {
        if(i<0) return;
        int prevMode = android.provider.Settings.System.getInt(mContentRes, KKC.S.SYSTEMUI_WALLPAPER_MODE, Resources.getSystem().getInteger(com.android.internal.R.integer.wallpaper_mode_default));

        android.provider.Settings.System.putInt(mContentRes, KKC.S.SYSTEMUI_WALLPAPER_MODE, i);

	if(i == KKC.S.WALLPAPER_MODE_DISABLE_ALL || prevMode == KKC.S.WALLPAPER_MODE_DISABLE_ALL)
		sendIntentToWindowManager(KKC.I.CMD_REBOOT, false);
	else if(i == KKC.S.WALLPAPER_MODE_DISABLE_SYSTEM || prevMode == KKC.S.WALLPAPER_MODE_DISABLE_SYSTEM)
		sendIntentToWindowManager(null, true);

        getActivity().finish();
    }

    public Dialog onCreateDialog(Bundle bundle)
    {
        mContentRes = getActivity().getContentResolver();
        int currentMode = android.provider.Settings.System.getInt(mContentRes, KKC.S.SYSTEMUI_WALLPAPER_MODE, Resources.getSystem().getInteger(com.android.internal.R.integer.wallpaper_mode_default));
        Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(R.array.kk_wallpaper_modes, currentMode, this);
        builder.setTitle(R.string.kk_ui_wallpaper_mode_title);
        builder.setNegativeButton(android.R.string.cancel, this);
        return builder.create();
    }

    private void sendIntentToWindowManager(String cmd, boolean shouldRestartUI) {
        Intent intent = new Intent()
                .setAction(KKC.I.UI_CHANGED)
                .putExtra(KKC.I.CMD,  cmd)
                .putExtra(KKC.I.EXTRA_RESTART_SYSTEMUI, shouldRestartUI);
        getActivity().sendBroadcastAsUser(intent, new UserHandle(UserHandle.USER_ALL));
    }

}

