package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by USER on 5/9/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the profile saving option after the hearing test is being done.
 * @see android.app.ActionBar
 */
public class soundprofileSave extends ActionBarActivity {
    ImageButton imagebtn1, btnOpenPopup;
    public static String LEFT_VOLUME;
    public static String RIGHT_VOLUME;
    EditText profileName;
    Button btnSave, btnDismiss;
    public static String SAVE_PROFILE;
    SqliteController controller = new SqliteController(this);
    private final static Logger LOGGER = Logger.getLogger(soundprofileSave.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundprofilesavepopup);
        getSupportActionBar().hide();
        profileName = (EditText) findViewById(R.id.editText);
        final String LEFT_VOLUME = getIntent().getStringExtra("leftvol");

        final String RIGHT_VOLUME = getIntent().getStringExtra("rightvol");

        btnDismiss = (Button) findViewById(R.id.button2);
        btnDismiss.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent backToMain = new Intent(soundprofileSave.this, headset.class);
                startActivity(backToMain);

            }
        });

        btnSave = (Button) findViewById(R.id.button);
        btnSave.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                SAVE_PROFILE = profileName.getText().toString();
                HashMap<String, String> profileDetails = new HashMap<String, String>();
                profileDetails.put("profileName", SAVE_PROFILE);
                profileDetails.put("left", LEFT_VOLUME);
                profileDetails.put("right", RIGHT_VOLUME);
                Log.i("info", "Data send to controller");
                controller.insertProfile(profileDetails);
                Intent backToMain = new Intent(soundprofileSave.this, hearingmain.class);
                startActivity(backToMain);
            }
        });


    }
}
