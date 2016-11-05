package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;

import java.util.logging.Logger;

/**
 * OptionsSubActivity is the class used to display the view related to sub options of thread based conversation option.
 *
 * @author R.H.Ramawickrama
 */
public class OptionsSubActivity extends ActionBarActivity {
    private final static Logger LOGGER = Logger.getLogger(OptionsSubActivity.class.getName());
    private ImageButton startConvo;
    private ImageButton allConvo;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_thread_sub);

        // hide the action bar
        getSupportActionBar().hide();

        startConvo = (ImageButton) findViewById(R.id.newConvo);
        allConvo = (ImageButton) findViewById(R.id.allConvo);

        startConvo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LOGGER.info("Starting the activity to view all conversations");
                Intent i = new Intent(OptionsSubActivity.this, AddParticipantsActivity.class);
                startActivity(i);
            }
        });

        //start the activity related to displaying all saved conversations
        allConvo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LOGGER.info("Starting the activity to start a new conversation");
                Intent i = new Intent(OptionsSubActivity.this, AllConversationsActivity.class);
                startActivity(i);
            }
        });

    }
}