package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by USER on 5/19/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the retest sound profile save option
 * @see android.app.ActionBar
 */
public class retakesoundSave extends ActionBarActivity {
    ImageButton imagebtn1, btnOpenPopup;
    public static String LEFT_VOLUME;
    public static String RIGHT_VOLUME;
    EditText profileName;
    public String retakeTestName, reID;
    Button btnSave, btnDismiss;
    public static String SAVE_PROFILE;
    SqliteController controller = new SqliteController(this);
    private final static Logger LOGGER = Logger.getLogger(retakesoundSave.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundprofilesavepopup);
        getSupportActionBar().hide();
        profileName = (EditText) findViewById(R.id.editText);
        LEFT_VOLUME = getIntent().getStringExtra("leftvol");
        reID = getIntent().getStringExtra("retakeprofid");
        RIGHT_VOLUME = getIntent().getStringExtra("rightvol");
        retakeTestName = getIntent().getStringExtra("retakeprofname");
        btnDismiss = (Button) findViewById(R.id.button2);
        btnDismiss.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent backToMain = new Intent(retakesoundSave.this, headset.class);
                startActivity(backToMain);

            }
        });

        btnSave = (Button) findViewById(R.id.button);
        btnSave.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                SAVE_PROFILE = profileName.getText().toString();
                HashMap<String, String> profileDetails = new HashMap<String, String>();

                profileDetails.put("left", LEFT_VOLUME);
                profileDetails.put("right", RIGHT_VOLUME);
                Log.i("info", "Data send to controller");
                controller.retakeProfile(profileDetails, retakeTestName, reID);
                Toast.makeText(getApplicationContext(), "Test taken successfully", Toast.LENGTH_SHORT).show();
                Intent backToMain = new Intent(retakesoundSave.this, hearingmain.class);
                startActivity(backToMain);
            }
        });


    }
}
