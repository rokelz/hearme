package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by USER on 5/9/2016.
 *
 * @author R.K.Opatha (IT13510572)
 *         Use to handle the profile saving option after the hearing test is being done.
 * @see android.app.ActionBar
 */
public class savedProfiles extends ActionBarActivity {
    ListView lv;
    Context context;
    ImageButton addProfile;
    SqliteController controller = new SqliteController(this);
    CustomAdapterViewProfile customeadapter;
    public String HEADSET_TYPE;
    private final static Logger LOGGER = Logger.getLogger(savedProfiles.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedprofiles);
        getSupportActionBar().hide();
        context = this;
        addProfile = (ImageButton) findViewById(R.id.imageButton43);


        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //editName()
                LayoutInflater layoutInflater = LayoutInflater.from(savedProfiles.this);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(savedProfiles.this);
                alertDialog.setTitle("Wish to add another sound profile ?...");

                alertDialog.setIcon(R.drawable.icon);
                alertDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(savedProfiles.this, "You clicked on Yes", Toast.LENGTH_SHORT).show();
                                HEADSET_TYPE = "full";
                                Intent intent = new Intent(savedProfiles.this, fullheadsetleft.class);
                                intent.putExtra("headsetType", HEADSET_TYPE);
                                startActivity(intent);
                            }
                        });
                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(savedProfiles.this,
                                        "You clicked on Cancel",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                alertDialog.show();


            }
        });
        lv = (ListView) findViewById(R.id.listView);
        ArrayList<HashMap<String, String>> profileNameList = controller.getAllProfiles();
        int i = 0;
        int arrleng = profileNameList.size();
        System.out.println(arrleng);
        String strArr[] = new String[profileNameList.size()];
        String idArr[] = new String[profileNameList.size()];
        for (i = 0; i < profileNameList.size(); i++) {
            strArr[i] = profileNameList.get(i).get("profileName").toString();
            idArr[i] = profileNameList.get(i).get("id").toString();
        }
        System.out.println(idArr);
        customeadapter = new CustomAdapterViewProfile(this, strArr, idArr);
        lv.setAdapter(customeadapter);
        registerForContextMenu(lv);

    }

    /**
     * Use to create a long clicked menu options
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle("----HearMe Menu----");
        menu.setHeaderIcon(R.drawable.icon);
        menu.add(1, v.getId(), 0, "Set as the Default Sound Profile");
        menu.add(1, v.getId(), 0, "Retake the test");
        menu.add(1, v.getId(), 0, "Profile Analysis");
        menu.add(1, v.getId(), 0, "Remind me to take the test");


    }

    @Override
    public void onBackPressed() {
        Intent goBack = new Intent(savedProfiles.this, hearingmain.class);
        startActivity(goBack);
    }

    String newProfileName;

    /**
     * Use to fire the functions when menu item is selected.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Set as the default") {
            ((HearMeSoundProfile) this.getApplication()).setheadsetProfileID(Integer.toString(customeadapter.getReposition()));


        } else if (item.getTitle() == "Retake the test") {
            customeadapter.profileRetest(customeadapter.getReposition());


        } else if (item.getTitle() == "Profile Analysis") {
            customeadapter.profileAnalysis(customeadapter.getReposition());


        } else if (item.getTitle() == "Remind me to take the test") {
            customeadapter.RemindProfileTest(customeadapter.getReposition());


        } else {
            return false;
        }

        return true;
    }

}
