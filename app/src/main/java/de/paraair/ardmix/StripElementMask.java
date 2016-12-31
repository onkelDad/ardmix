package de.paraair.ardmix;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by onkel on 21.11.16.
 */

public class StripElementMask {
    public boolean bTitle = true;
    public boolean bFX = true;
    public boolean bSend = true;
    public boolean bRecord = true;
    public boolean bReceive = true;
    public boolean bInput = true;

    public boolean bMeter = true;
    public boolean bMute = true;
    public boolean bSolo = true;

    public boolean bPan = true;
    public boolean bFader = true;
    public boolean bSoloIso = false;
    public boolean bSoloSafe = false;

    public int stripSize = 1;

    public int autoSize = 32;

    public void config(AppCompatActivity context) {
        StripMaskDialogFragment sdlg = new StripMaskDialogFragment();
        Bundle settingsBundle = new Bundle();

        settingsBundle.putInt("stripSize", stripSize);
        settingsBundle.putBoolean("title", bTitle);
        settingsBundle.putBoolean("fx", bFX);
        settingsBundle.putBoolean("send", bSend);
        settingsBundle.putBoolean("record", bRecord);
        settingsBundle.putBoolean("receive", bReceive);
        settingsBundle.putBoolean("input", bInput);
        settingsBundle.putBoolean("meter", bMeter);
        settingsBundle.putBoolean("mute", bMute);
        settingsBundle.putBoolean("solo", bSolo);
        settingsBundle.putBoolean("soloiso", bSoloIso);
        settingsBundle.putBoolean("solosafe", bSoloSafe);
        settingsBundle.putBoolean("pan", bPan);
        settingsBundle.putBoolean("fader", bFader);

        sdlg.item = this;
        sdlg.setArguments(settingsBundle);

        sdlg.show(context.getSupportFragmentManager(), "Connection Settings");

    }

    public void loadSettings(SharedPreferences settings) {
        bTitle = settings.getBoolean("mskTitle", true);
        bFX = settings.getBoolean("mskFx", true);
        bSend = settings.getBoolean("mskSend", true);
        bRecord = settings.getBoolean("mskRecord", true);
        bReceive = settings.getBoolean("mskReceive", true);
        bInput = settings.getBoolean("mskInput", true);

        bMeter = settings.getBoolean("mskMeter", true);
        bMute = settings.getBoolean("mskMute", true);
        bSolo = settings.getBoolean("mskSolo", true);
        bSoloIso = settings.getBoolean("mskSoloIso", true);
        bSoloSafe = settings.getBoolean("mskSoloSafe", true);
        bPan = settings.getBoolean("mskPan", true);
        bFader = settings.getBoolean("mskFader", true);

        stripSize = settings.getInt("strip_wide", 1);

    }

    public void saveSettings(SharedPreferences.Editor editor) {
        editor.putBoolean("mskTitle", bTitle);
        editor.putBoolean("mskMeter", bMeter);
        editor.putBoolean("mskFX", bFX);
        editor.putBoolean("mskSend", bSend);
        editor.putBoolean("mskRecord", bRecord);
        editor.putBoolean("mskReceive", bReceive);
        editor.putBoolean("mskInput", bInput);
        editor.putBoolean("mskSolo", bSolo);
        editor.putBoolean("mskSoloIso", bSoloIso);
        editor.putBoolean("mskSoloSafe", bSoloSafe);
        editor.putBoolean("mskMute", bMute);
        editor.putBoolean("mskPan", bPan);

        editor.putInt("strip_wide", stripSize);

    }
}
