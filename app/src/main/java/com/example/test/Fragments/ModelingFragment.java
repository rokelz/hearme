package com.example.test.Fragments;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.example.test.Dialogs.CoefficientsDialog;
import com.example.test.Dialogs.FileChooserDialog;
import com.example.test.Dialogs.NumberPickerDialog;
import com.example.test.R;
import com.example.test.Structures.Keys;
import com.example.test.Structures.ModelingStructure;

import java.io.File;

public class ModelingFragment extends PreferenceFragment {
    private Preference labelsPreference;
    private Preference speakersNamePreference;

    private PreferenceManager preferenceManager;
    private FragmentManager fragmentManager;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_modeling);
        preferenceManager = getPreferenceManager();
        fragmentManager = getFragmentManager();

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = settings.edit();


        labelsPreference = preferenceManager.findPreference(Keys.labelAssociation);
        speakersNamePreference = getPreferenceManager().findPreference(Keys.speakersName);

        speakersNamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals(""))
                    return false;

                newValue = newValue.toString().replace(" ", "");
                editor.putString(Keys.speakersName, "" + newValue);
                String[] names = newValue.toString().split(",");
                ModelingStructure.speakersNames = names;

                int N = names.length;

                ModelingStructure.labels = new int[N];
                String labelsSummary = "";

                for (int i = 0; i < N; i++) {
                    ModelingStructure.labels[i] = i;
                    labelsSummary += names[i] + ": " + i + ", ";
                }

                labelsSummary = labelsSummary.substring(0, labelsSummary.length() - 2);
                labelsPreference.setSummary(labelsSummary);
                editor.putString(labelsPreference.getKey(), labelsSummary);
                editor.apply();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        labelsPreference.setSummary(settings.getString(Keys.labelAssociation, ""));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, final Preference preference) {

        String key = preference.getKey();
        if (key.equals(Keys.trainingFiles)) {
            new FileChooserDialog(getActivity()).setFileListener(new FileChooserDialog.FileSelectedListener() {
                @Override
                public void fileSelected(final File[] files) {
                    if (files != null)
                        ModelingStructure.trainingFiles = files;

                }
            }).showDialog();
        } else if (key.equals(Keys.cCoefficients)) {
            CoefficientsDialog coefficientsDialog = new CoefficientsDialog();
            super.onResume();
            coefficientsDialog.show(fragmentManager, "C_Coefficient");
        } else if (key.equals(Keys.gCoefficients)) {
            CoefficientsDialog coefficientsDialog = new CoefficientsDialog();
            super.onResume();
            coefficientsDialog.show(fragmentManager, "Gamma_Coefficient");
        } else if (key.equals(Keys.folds)) {
            DialogFragment dialogFragment = NumberPickerDialog.newInstance(R.string.folds);
            super.onResume();
            dialogFragment.show(fragmentManager, "folds");
        }
        return true;
    }

}
