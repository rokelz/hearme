package com.example.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.logging.Logger;

/**
 * Created by USER on 3/12/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         To handle the options in the mobile application in a vertical pattern.
 * @see android.app.ActionBar
 */
public class mainopdown extends ActionBarActivity {
    ImageButton imagebtn1;
    ImageButton imagebtn2;
    ImageButton habutton, threadViewButton,trainVoice;
    SqliteController controller = new SqliteController(this);
    public static int NUMBER_OF_PROFILES;
    public String HEADSET_TYPE;
    private final static Logger LOGGER = Logger.getLogger(mainopdown.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainopdown);
        getSupportActionBar().hide();
        imagebtn1 = (ImageButton) findViewById(R.id.imageButton30);
        threadViewButton = (ImageButton) findViewById(R.id.imageButton32);
        trainVoice = (ImageButton)findViewById(R.id.imageButton31);
        trainVoice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(mainopdown.this, Training.class);
                startActivity(intent);
            }
        });
        imagebtn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(mainopdown.this, mainopdown.class);
                startActivity(intent);
            }
        });
        imagebtn2 = (ImageButton) findViewById(R.id.imageButton29);
        imagebtn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                Intent intent = new Intent(mainopdown.this, mainoption.class);
                startActivity(intent);
            }
        });
        habutton = (ImageButton) findViewById(R.id.imageButton34);
        NUMBER_OF_PROFILES = controller.getNumberOFProfiles();
        if (NUMBER_OF_PROFILES <= 0) {

            habutton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    LayoutInflater layoutInflater = LayoutInflater.from(mainopdown.this);
                    View promptView = layoutInflater.inflate(R.layout.welcome, null);

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainopdown.this);
                    alertDialog.setTitle("HearMe");

                    alertDialog.setIcon(R.drawable.icon);
                    alertDialog.setView(promptView);
                    alertDialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // User pressed Cancel button. Write Logic Here
                                    Toast.makeText(mainopdown.this, "You clicked on Ok", Toast.LENGTH_SHORT).show();
                                }
                            });

                    alertDialog.show();

                    HEADSET_TYPE = "full";
                    Intent intent = new Intent(mainopdown.this, fullheadsetleft.class);
                    intent.putExtra("headsetType", HEADSET_TYPE);
                    startActivity(intent);

                }
            });
        } else {
            habutton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Intent intent = new Intent(mainopdown.this, hearingmain.class);
                    startActivity(intent);
                }
            });
        }
        threadViewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(mainopdown.this, OptionsSubActivity.class);
                startActivity(i);
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


}
