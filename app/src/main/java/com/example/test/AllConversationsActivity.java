package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * AllConversationsActivity is the class used to display all saved conversations.
 *
 * @author R.H.Ramawickrama
 */
public class AllConversationsActivity extends ActionBarActivity {
    private final static Logger LOGGER = Logger.getLogger(AllConversationsActivity.class.getName());
    Button button;
    ArrayAdapter adapter;
    ListView listView;
    TextView name;
    TextView date;
    TextView duration;
    File messagesDir1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_conversations);

        // hide the action bar
        getSupportActionBar().hide();
        messagesDir1 = getApplicationContext().getDir("messagesDir", Context.MODE_PRIVATE);

        adapter = new ArrayAdapter<String>(this, R.layout.all_conversations_item, messagesDir1.list());

        listView = (ListView) findViewById(R.id.allConvoList);
        listView.setAdapter(adapter);

        //to scroll the list view to bottom on data change
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(adapter.getCount() - 1);
            }
        });

        //use to start the new intent to view a clicked conversation
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String filename = (String) parent.getItemAtPosition(position);

                Intent i = new Intent(AllConversationsActivity.this, SingleConversationActivity.class);
                i.putExtra("filename", filename);
                startActivity(i);
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

        menu.add(0, v.getId(), 0, "View");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Details");

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "View") {

            //getting the selected conversation name
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String filename = messagesDir1.list()[info.position];

            //starting the activity related to viewing a single selected conversation
            LOGGER.info("Starting the activity to display a selected conversation");
            Intent i = new Intent(AllConversationsActivity.this, SingleConversationActivity.class);
            i.putExtra("filename", filename);
            startActivity(i);

        } else if (item.getTitle() == "Delete") {

            LOGGER.warning("Deleting a selected conversation");
            //getting the selected conversation name
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String filename = messagesDir1.list()[info.position];

            //delete conversation
            File file = new File(messagesDir1, filename);
            file.delete();

            //generating a toast displaying conversation deleted
            Toast toast = Toast.makeText(getApplicationContext(), "Conversation deleted", Toast.LENGTH_SHORT);
            toast.show();

            //refresh
            Intent i = new Intent(AllConversationsActivity.this, AllConversationsActivity.class);
            i.putExtra("filename", filename);
            startActivity(i);

        } else if (item.getTitle() == "Details") {

            //getting the selected conversation name
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String filename = messagesDir1.list()[info.position];

            //get the values from the file
            ArrayList<ChatMessage> chatMessageList = new ArrayList();
            File file = new File(messagesDir1, filename);
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

            String convodate = "";
            long startTime = 0;
            long endTime = 0;
            long convoduration = 0;

            if (chatMessageList.size() != 0) {

                convodate = chatMessageList.get(0).getDate();

                startTime = chatMessageList.get(0).getTimeinseconds();
                endTime = chatMessageList.get(chatMessageList.size() - 1).getTimeinseconds();

                //calculating the conversation duration
                convoduration = endTime - startTime;
            }

            //calculating the conversation duration in hh:mm:ss
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(convoduration),
                    TimeUnit.MILLISECONDS.toMinutes(convoduration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(convoduration)),
                    TimeUnit.MILLISECONDS.toSeconds(convoduration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(convoduration)));

            AlertDialog.Builder alertDialogStop = new AlertDialog.Builder(AllConversationsActivity.this);

            // Get the layout inflater
            LayoutInflater inflater1 = AllConversationsActivity.this.getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View dialogView = inflater1.inflate(R.layout.chat_details, null);
            alertDialogStop.setView(dialogView);

            name = (TextView) dialogView.findViewById(R.id.name);
            date = (TextView) dialogView.findViewById(R.id.date);
            duration = (TextView) dialogView.findViewById(R.id.duration);

            name.setText("Name: " + filename);
            date.setText("Date: " + convodate);
            duration.setText("Duration: " + hms);

            alertDialogStop.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialogStop.show();

        } else {
            return false;
        }
        return true;
    }
}