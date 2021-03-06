package com.example.test.Fragments;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import com.example.test.Dialogs.NumberPickerDialog;
import com.example.test.Dialogs.OverlapFactorDialog;
import com.example.test.Dialogs.ThresholdDialog;
import com.example.test.Framer;
import com.example.test.R;
import com.example.test.Structures.FeatureExtractionStructure;
import com.example.test.Structures.Keys;

public class FeatureExtractionFragment extends PreferenceFragment {

    private final String[] rates = new String[]
            {"8000", "11025", "16000", "22050", "32000", "44100", "48000"};


    private PreferenceManager preferenceManager;
    private FragmentManager fragmentManager;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_extraction);

        preferenceManager = getPreferenceManager();
        fragmentManager   = getFragmentManager();

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = settings.edit();

        initSummaries();
        initRates();

    }

    private void initRates()
    {
        int maxRate = getValidSampleRates();
        ListPreference listPreference = (ListPreference) findPreference(Keys.sampleRate);
        String[] validRates = new String[maxRate];
        System.arraycopy(rates, 0, validRates, 0, maxRate);
        listPreference.setEntries(validRates);
        listPreference.setEntryValues(validRates);
    }


    private void initSummaries()
    {
        // Preferences that have a summary that needs to be updated
        Preference frameDuration;
        Preference sampleRate;
        Preference samplesInFrame;
        Preference overlapFactor;
        Preference frameSize;
        Preference energyThreshold;


        frameDuration           = preferenceManager.findPreference(Keys.frameDuration);
        sampleRate              = preferenceManager.findPreference(Keys.sampleRate);
        samplesInFrame          = preferenceManager.findPreference(Keys.samplesInFrame);
        overlapFactor           = preferenceManager.findPreference(Keys.overlapFactor);
        frameSize               = preferenceManager.findPreference(Keys.frameSize);
        energyThreshold         = preferenceManager.findPreference(Keys.energyThreshold);

        frameDuration   .setSummary("" + settings.getInt(Keys.frameDuration, 32));
        sampleRate      .setSummary("" + settings.getString(Keys.sampleRate, "8000"));
        samplesInFrame  .setSummary("" + settings.getInt(Keys.samplesInFrame, 256));
        overlapFactor   .setSummary("0." + settings.getInt(Keys.overlapFactor, 75));
        frameSize       .setSummary("" + settings.getInt(Keys.frameSize, 512));


        //energyThreshold .setSummary(settings.getString(Keys.energyThreshold, "5e7"));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();

        switch(key)
        {
            case "enable_custom_extraction":
            {
                SwitchPreference switchPreference =
                        (SwitchPreference) preferenceManager.findPreference(Keys.enableCustom);

                if(!switchPreference.isChecked())
                {
                    Framer.FRAME_LENGTH_MS      = 32;
                    Framer.SAMPLE_RATE          = 8000;
                    Framer.SAMPLES_IN_FRAME     = 256;
                    Framer.FRAME_OVERLAP_FACTOR = 0.75f;
                    Framer.BPS                  = 2;
                    Framer.FRAME_BYTE_SIZE      = 512;
                    Framer.FRAME_BYTE_SPACING   = (int) (512 * 0.25f);
                    Framer.FRAME_SHORT_SPACING  = Framer.FRAME_BYTE_SPACING / 2;
                    Framer.ENERGY_THRESHOLD     = 5e7;
                }
                break;
            }
            case "frame_duration":
            {
                DialogFragment dialogFragment =
                        NumberPickerDialog.newInstance(R.string.frame_duration, preferenceManager);
                super.onResume();
                dialogFragment.show(fragmentManager, "frame_duration");
                break;
            }

            case "sample_rate":
            {
                preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        int value = Integer.parseInt(newValue.toString());

                        FeatureExtractionStructure.sampleRate = value;
                        Framer.SAMPLE_RATE = value;
                        preference.setSummary("" + value);

                        Preference samplesInFramePreference =
                                getPreferenceManager()
                                        .findPreference(Keys.samplesInFrame);

                        samplesInFramePreference.setSummary
                                (
                                        "" + (value *
                                                FeatureExtractionStructure.frameDuration / 1000));

                        editor.putString(Keys.sampleRate, "" + value);
                        editor.putInt(Keys.samplesInFrame,
                                (value *
                                        FeatureExtractionStructure.frameDuration / 1000));
                        editor.apply();

                        return true;
                    }
                });
                break;
            }

            case "energy_threshold":
            {
                ThresholdDialog thresholdDialog =
                        ThresholdDialog.newInstance(preferenceManager);
                super.onResume();
                thresholdDialog.show(fragmentManager, "energy_threshold");
                break;
            }

            case "frame_overlap_factor":
            {
                OverlapFactorDialog overlapFactorDialog =
                        OverlapFactorDialog.newInstance(preferenceManager);
                super.onResume();
                overlapFactorDialog.show(fragmentManager, "overlap_factor");
                break;
            }

            default:
                return false;
        }
        return true;
    }

    private int getValidSampleRates() {
        // add the rates you wish to check against
        int[] rates = new int[]{8000, 11025, 16000, 22050, 32000, 44100, 48000};
        int i = 0, bufferSize;

        do {
            bufferSize = AudioRecord.getMinBufferSize(rates[i], AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            i++;
        }
        while(bufferSize > 0 && i < rates.length);

        // If i < rates.length his value is an index of an invalid value in the rates array
        return (i < rates.length ? i - 1 : i);
    }


}
