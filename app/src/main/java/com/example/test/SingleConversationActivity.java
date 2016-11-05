package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * SingleConversationActivity is the class used to display a single selected conversation.
 *
 * @author R.H.Ramawickrama
 */
public class SingleConversationActivity extends ActionBarActivity {
    private final static Logger LOGGER = Logger.getLogger(SingleConversationActivity.class.getName());

    private ChatArrayAdapter chatArrayAdapter;

    private ListView listView;
    private TextView convoName;
    String filename;

    private boolean side = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_conversation);

        // hide the action bar
        getSupportActionBar().hide();

        listView = (ListView) findViewById(R.id.listView1);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.single_conversation_chat_item);
        listView.setAdapter(chatArrayAdapter);

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        //get file name
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            filename = (String) b.get("filename");
        }

        convoName = (TextView) findViewById(R.id.convoName);
        convoName.setText(filename);

        //get the values from the file
        ArrayList<ChatMessage> chatMessageList = new ArrayList();
        File file = new File(getApplicationContext().getDir("messagesDir", Context.MODE_PRIVATE), filename);

        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            ObjectInputStream oi = new ObjectInputStream(fis);
            chatMessageList = (ArrayList<ChatMessage>) oi.readObject();
            oi.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //iteratively set it to list items
        for (ChatMessage message : chatMessageList) {
            LOGGER.info("Setting chat messages of a given conversation");
            chatArrayAdapter.add(new ChatMessage(side, message.getMessage(), message.getTime(),
                    message.getTimeinseconds(), message.getDate()));
            side = !side;

        }
    }

}