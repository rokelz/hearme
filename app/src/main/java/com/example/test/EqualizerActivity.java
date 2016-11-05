package com.example.test;

import android.app.Activity;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 5/14/2016.
 */
public class EqualizerActivity extends Activity implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    TextView bass_boost_label = null;
    SeekBar bass_boost = null;
    CheckBox enabled = null;
    Button flat = null;
    Spinner spinner = null;
    Equalizer eq = null;
    BassBoost bb = null;

    int min_level = 0;
    int max_level = 100;

    static final int MAX_SLIDERS = 8; // Must match the XML layout
    SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    TextView slider_labels[] = new TextView[MAX_SLIDERS];
    int num_sliders = 0;

    /*=============================================================================
        onCreate
    =============================================================================*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /*=============================================================================
        onCreate
    =============================================================================*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);

        enabled = (CheckBox) findViewById(R.id.enabled);
        enabled.setOnCheckedChangeListener(this);


        spinner = (Spinner) findViewById(R.id.spinner);
        bass_boost = (SeekBar) findViewById(R.id.bass_boost);
        bass_boost.setOnSeekBarChangeListener(this);
        bass_boost_label = (TextView) findViewById(R.id.bass_boost_label);

        sliders[0] = (SeekBar) findViewById(R.id.slider_1);
        slider_labels[0] = (TextView) findViewById(R.id.slider_label_1);
        sliders[1] = (SeekBar) findViewById(R.id.slider_2);
        slider_labels[1] = (TextView) findViewById(R.id.slider_label_2);
        sliders[2] = (SeekBar) findViewById(R.id.slider_3);
        slider_labels[2] = (TextView) findViewById(R.id.slider_label_3);
        sliders[3] = (SeekBar) findViewById(R.id.slider_4);
        slider_labels[3] = (TextView) findViewById(R.id.slider_label_4);
        sliders[4] = (SeekBar) findViewById(R.id.slider_5);
        slider_labels[4] = (TextView) findViewById(R.id.slider_label_5);
        sliders[5] = (SeekBar) findViewById(R.id.slider_6);
        slider_labels[5] = (TextView) findViewById(R.id.slider_label_6);
        sliders[6] = (SeekBar) findViewById(R.id.slider_7);
        slider_labels[6] = (TextView) findViewById(R.id.slider_label_7);
        sliders[7] = (SeekBar) findViewById(R.id.slider_8);
        slider_labels[7] = (TextView) findViewById(R.id.slider_label_8);

        eq = new Equalizer(0, 0);
        if (eq != null) {
            eq.setEnabled(true);
            int num_bands = eq.getNumberOfBands();
            num_sliders = num_bands;
            short r[] = eq.getBandLevelRange();
            min_level = r[0];
            max_level = r[1];
            for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++) {
                int[] freq_range = eq.getBandFreqRange((short) i);
                sliders[i].setOnSeekBarChangeListener(this);
                slider_labels[i].setText(formatBandLabel(freq_range));
            }
        }
        for (int i = num_sliders; i < MAX_SLIDERS; i++) {
            sliders[i].setVisibility(View.GONE);
            slider_labels[i].setVisibility(View.GONE);
        }

        bb = new BassBoost(0, 0);
        if (bb != null) {
        } else {
            bass_boost.setVisibility(View.GONE);
            bass_boost_label.setVisibility(View.GONE);
        }

        updateUI();
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Flat");
        categories.add("Normal");
        categories.add("Pop");
        categories.add("Classic");
        categories.add("Jazz");
        categories.add("Rock");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        if (item == "Flat") {
            setFlat();
        }
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    /*=============================================================================
        onProgressChanged
    =============================================================================*/
    @Override
    public void onProgressChanged(SeekBar seekBar, int level,
                                  boolean fromTouch) {
        if (seekBar == bass_boost) {
            bb.setEnabled(level > 0 ? true : false);
            bb.setStrength((short) level); // Already in the right range 0-1000
        } else if (eq != null) {
            int new_level = min_level + (max_level - min_level) * level / 100;

            for (int i = 0; i < num_sliders; i++) {
                if (sliders[i] == seekBar) {
                    eq.setBandLevel((short) i, (short) new_level);
                    break;
                }
            }
        }
    }

    /*=============================================================================
        onStartTrackingTouch
    =============================================================================*/
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    /*=============================================================================
        onStopTrackingTouch
    =============================================================================*/
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    /*=============================================================================
        formatBandLabel
    =============================================================================*/
    public String formatBandLabel(int[] band) {
        return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
    }

    /*=============================================================================
        milliHzToString
    =============================================================================*/
    public String milliHzToString(int milliHz) {
        if (milliHz < 1000) return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }

    /*=============================================================================
        updateSliders
    =============================================================================*/
    public void updateSliders() {
        for (int i = 0; i < num_sliders; i++) {
            int level;
            if (eq != null)
                level = eq.getBandLevel((short) i);
            else
                level = 0;
            int pos = 100 * level / (max_level - min_level) + 50;
            sliders[i].setProgress(pos);
        }
    }

    /*=============================================================================
        updateBassBoost
    =============================================================================*/
    public void updateBassBoost() {
        if (bb != null)
            bass_boost.setProgress(bb.getRoundedStrength());
        else
            bass_boost.setProgress(0);
    }

    /*=============================================================================
        onCheckedChange
    =============================================================================*/
    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        if (view == (View) enabled) {
            eq.setEnabled(isChecked);
        }
    }

    /*=============================================================================
        onClick
    =============================================================================*/
    @Override
    public void onClick(View view) {

    }

    /*=============================================================================
        updateUI
    =============================================================================*/
    public void updateUI() {
        updateSliders();
        updateBassBoost();
        enabled.setChecked(eq.getEnabled());
    }

    /*=============================================================================
        setFlat
    =============================================================================*/
    public void setFlat() {
        if (eq != null) {
            for (int i = 0; i < num_sliders; i++) {
                eq.setBandLevel((short) i, (short) 0);
            }
        }

        if (bb != null) {
            bb.setEnabled(false);
            bb.setStrength((short) 0);
        }

        updateUI();
    }

    /*=============================================================================
       setRock
   =============================================================================*/
    public void setRock() {
        if (eq != null) {
            for (int i = 0; i < num_sliders; i++) {
                eq.setBandLevel((short) i, (short) 0);
            }
        }

        if (bb != null) {
            bb.setEnabled(false);
            bb.setStrength((short) 0);
        }

        updateUI();
    }

}
