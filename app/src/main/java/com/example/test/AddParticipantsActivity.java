package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.logging.Logger;

/**
 * AddParticipantsActivity is the class used to implement the functionality to add participants for a given conversation.
 *
 * @author R.H.Ramawickrama
 */
public class AddParticipantsActivity extends ActionBarActivity {
    private final static Logger LOGGER = Logger.getLogger(AddParticipantsActivity.class.getName());
    ImageButton addButton;
    ImageButton startConvo;
    ListView listView;

    private ParticipantArrayAdapter participantArrayAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participants);

        // hide the action bar
        getSupportActionBar().hide();
        addButton = (ImageButton) findViewById(R.id.addItem);
        startConvo = (ImageButton) findViewById(R.id.startConvo);
        listView = (ListView) findViewById(R.id.listView);

        participantArrayAdapter = new ParticipantArrayAdapter(getApplicationContext(), R.layout.single_participant_item);

        listView.setAdapter(participantArrayAdapter);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(participantArrayAdapter);

        //to scroll the list view to bottom on data change
        participantArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(participantArrayAdapter.getCount() - 1);
            }
        });

        //on click listener to add participants
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String color = null;
                String name = null;

                if (participantArrayAdapter.getCount() < 2) {
                    //TODO: call the voice regognizer
                    //TODO: Get the recognized name

                    if (participantArrayAdapter.getCount() == 0) {
                        //assigning a chat bubble color
                        color = "blue";
                        //mocking the participant name
                        name = "Sarah";
                    } else if (participantArrayAdapter.getCount() == 1) {
                        color = "green";
                        name = "John";
                    }

                    LOGGER.info("Adding new participant to the participant list");
                    participantArrayAdapter.add(new Participant(name, color, true));
                } else {
                    //if participants are more than two, cannot add participants
                    LOGGER.warning("More than two participants");
                    Toast toast = Toast.makeText(getApplicationContext(), "No more participants can be added", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        //start conversation
        startConvo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //display an informational message if not internet connection is available
                if (!isNetworkAvailable()) {
                    LOGGER.warning("No internet connection");

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddParticipantsActivity.this);

                    // Get the layout inflater
                    LayoutInflater inflater = AddParticipantsActivity.this.getLayoutInflater();

                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    alertDialog.setView(inflater.inflate(R.layout.no_internet_view, null));

                    //start the activity related to displaying an ongoing conversation
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LOGGER.info("Starting the activity to display an ongoing conversation");
                            Intent i = new Intent(AddParticipantsActivity.this, PocketSphinxActivity.class);
                            startActivity(i);
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();

                } else {
                    Intent i = new Intent(AddParticipantsActivity.this, ThreadViewActivity.class);
                    startActivity(i);
                }
            }
        });

        //the list view will generate a context menu on long click
        registerForContextMenu(listView);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Delete");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Delete") {
            //getting the selected conversation name
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            participantArrayAdapter.getItems().remove(info.position);
            participantArrayAdapter.notifyDataSetChanged();
        }
        return true;
    }

    /**
     * Method used to check whether the internet connection is available
     *
     * @return boolean true or false
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}