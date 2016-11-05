package com.example.test;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by USER on 5/7/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the profile saving confirm option after the hearing test is being done.
 * @see android.app.ActionBar
 */
public class soundprofile extends ActionBarActivity {
    ImageButton imagebtn1, btnOpenPopup;
    public static int LEFT_VOLUME;
    public static int RIGHT_VOLUME;
    EditText profileName;
    public static String SAVE_PROFILE;
    public String value;
    public String value1;
    private final static Logger LOGGER = Logger.getLogger(soundprofile.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundprofile);
        getSupportActionBar().hide();
        value = getIntent().getStringExtra("left");
        int leftvol = Integer.parseInt(value);
        value1 = getIntent().getStringExtra("right");
        int rightvol = Integer.parseInt(value1);
        LEFT_VOLUME = leftvol;
        RIGHT_VOLUME = rightvol;
        btnOpenPopup = (ImageButton) findViewById(R.id.imageButton21);
        btnOpenPopup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent soundprofsav = new Intent(soundprofile.this, soundprofileSave.class);
                soundprofsav.putExtra("leftvol", value);
                soundprofsav.putExtra("rightvol", value1);
                startActivity(soundprofsav);
                /*LayoutInflater layoutInflater
                        = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.activity_soundprofilesavepopup, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                //popupWindow.showAsDropDown(btnOpenPopup,-10, -550);//problem in x axis
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);
                profileName = (EditText)popupView.findViewById(R.id.editText);
                //profileName.requestFocus();
                //InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                //if(imm != null) {
                 //   imm.showSoftInput(profileName, 0);
                //}
                Button btnDismiss = (Button)popupView.findViewById(R.id.button2);
                btnDismiss.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        popupWindow.dismiss();
                    }});

                Button btnSave = (Button)popupView.findViewById(R.id.button);
                btnSave.setOnClickListener(new Button.OnClickListener(){
                    public void onClick(View v){


                        SAVE_PROFILE = profileName.getText().toString();
                        //saveProfile(LEFT_VOLUME,RIGHT_VOLUME,SAVE_PROFILE);
                    }
                });

           */
            }
        });


        imagebtn1 = (ImageButton) findViewById(R.id.imageButton37);
        imagebtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    confirmAudibility(LEFT_VOLUME, RIGHT_VOLUME);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Use to confirm the sound profile hearing levels by playing the hearing levels again.
     *
     * @param left
     * @param right
     * @throws IOException
     */
    public void confirmAudibility(int left, int right) throws IOException {
        Testhearing testhearing = new Testhearing();
        int[] decibelArray = {55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110};
        int[] decibelArray1 = {55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110};
        int decibelArrayLength = decibelArray.length;
        System.out.println(decibelArrayLength);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.tone);

        float rightVolume = 0.0f;
        int maxVolume = 110;
        int currLeftVolume = left;
        int currRightVolume = right;
        float logleft = (float) (Math.log(maxVolume - currLeftVolume) / Math.log(maxVolume));
        float logright = (float) (Math.log(maxVolume - currRightVolume) / Math.log(maxVolume));

        System.out.println("left......................." + logleft + "right...................." + logright);
        mp.setVolume(1 - logright, 1 - logleft);
        mp.start();
    }
}


