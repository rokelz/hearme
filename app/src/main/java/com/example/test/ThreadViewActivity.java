package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * ThreadViewActivity is the class used to display the view related to an ongoing conversation.
 *
 * @author R.H.Ramawickrama
 */
public class ThreadViewActivity extends ActionBarActivity implements
        RecognitionListener {
    private final static Logger LOGGER = Logger.getLogger(ThreadViewActivity.class.getName());
    private SpeechRecognizer speech = null;
    private Intent intent;

    private ListView listView;
    private ProgressBar progressBar;
    private ImageButton pause;
    private ImageButton stop;
    private ImageButton newconvo;
    boolean isPlaying = true;

    private TextView dialogTitle;
    private TextView dialogMessage;
    private EditText givenFileName;
    File messagesDir1;
    private ChatArrayAdapter chatArrayAdapter;
    private boolean side = false;
    private boolean isfileexists = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_view);

        // hide the action bar
        getSupportActionBar().hide();

        messagesDir1 = getApplicationContext().getDir("messagesDir", Context.MODE_PRIVATE);

        listView = (ListView) findViewById(R.id.listView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        pause = (ImageButton) findViewById(R.id.pause);
        stop = (ImageButton) findViewById(R.id.stop);
        newconvo = (ImageButton) findViewById(R.id.newconvo);

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

        //creating an speech recognizer
        speech = SpeechRecognizer.createSpeechRecognizer(this);

        //setting an speech recognizer to the current activity
        speech.setRecognitionListener(this);

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOGGER.info("Calling the method to pause a conversation");
                onPauseConversation(v);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOGGER.info("Calling the method to stop a conversation");
                onStopConversation(v);
            }
        });

        newconvo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOGGER.info("Calling the method to start a new a conversation");
                onNewConversation(v);
            }
        });

        //call the method used to recognize the voice
        displayConversation();

    }

    /**
     * Method used to pause an ongoing conversation.
     *
     * @param view View
     */
    private void onPauseConversation(View view) {

        if (isPlaying) {
            speech.stopListening();
            speech.destroy();
            isPlaying = false;
            progressBar.setIndeterminate(false);
            pause.setImageResource(R.drawable.play1);
            //pause.setText("Resume");
        } else {
            speech = SpeechRecognizer.createSpeechRecognizer(ThreadViewActivity.this);
            speech.setRecognitionListener(ThreadViewActivity.this);
            displayConversation();
            isPlaying = true;
            progressBar.setIndeterminate(true);
            pause.setImageResource(R.drawable.pause);
        }
    }


    /**
     * Method used to stop an ongoing conversation.
     *
     * @param view View
     */
    private void onStopConversation(View view) {

        speech.stopListening();
        speech.destroy();
        progressBar.setIndeterminate(false);

        generateStopConfirmationDialog();

    }

    /**
     * Method used to generate a stop confirmation dialog.
     */
    private void generateStopConfirmationDialog() {
        AlertDialog.Builder alertDialogStop = new AlertDialog.Builder(ThreadViewActivity.this);

        // Get the layout inflater
        LayoutInflater inflater1 = ThreadViewActivity.this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater1.inflate(R.layout.no_internet_view, null);
        alertDialogStop.setView(dialogView);

        dialogTitle = (TextView) dialogView.findViewById(R.id.title);
        dialogMessage = (TextView) dialogView.findViewById(R.id.message);

        dialogTitle.setText("Stop Conversation");
        dialogMessage.setText("Are you sure you want to stop the conversation?");
        alertDialogStop.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                LOGGER.info("Calling the method to display save conversation dialog");
                generateSaveConversationDialogWhenStopped();

            }
        });
        alertDialogStop.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                speech = SpeechRecognizer.createSpeechRecognizer(ThreadViewActivity.this);
                speech.setRecognitionListener(ThreadViewActivity.this);
                progressBar.setIndeterminate(true);
                displayConversation();
            }
        });
        alertDialogStop.show();
    }

    /**
     * Method used to show save conversation dialog.
     */
    private void generateSaveConversationDialogWhenStopped() {
        AlertDialog.Builder alertDialogSave = new AlertDialog.Builder(this);

        // Get the layout inflater
        LayoutInflater inflater2 = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater2.inflate(R.layout.save_filename_view, null);
        alertDialogSave.setView(dialogView);
        givenFileName = (EditText) dialogView.findViewById(R.id.filename);

        alertDialogSave.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String filename = givenFileName.getText().toString();

                if (filename.isEmpty() || filename == null) {
                    Date d = new Date();
                    filename = (String) DateFormat.format("MM-dd-yy hh-mm-ss", d.getTime());
                }

                //check whether filename already exists
                for (String savedfilename : messagesDir1.list()) {
                    if (savedfilename.compareTo(filename) == 0) {
                        LOGGER.warning("Filename given to save a conversation already exists");
                        isfileexists = true;
                        break;
                    }
                }

                if (isfileexists) {
                    //display a toast saying that the conversation name already exists
                    Toast toast = Toast.makeText(getApplicationContext(), "Conversation name already exists", Toast.LENGTH_SHORT);
                    toast.show();
                    isfileexists = false;
                    generateSaveConversationDialogWhenStopped();
                } else {

                    //creating the file
                    File file = new File(messagesDir1, filename);

                    //saving to the file
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        ObjectOutputStream of = new ObjectOutputStream(fos);
                        of.writeObject(chatArrayAdapter.getItems());
                        of.flush();
                        of.close();
                        fos.close();
                        Toast toast = Toast.makeText(getApplicationContext(), "Conversation saved", Toast.LENGTH_SHORT);
                        toast.show();
                        Intent i = new Intent(ThreadViewActivity.this, OptionsSubActivity.class);
                        startActivity(i);
                    } catch (Exception e) {
                        Log.e("InternalStorage", e.getMessage());
                    }
                }
            }
        });

        alertDialogSave.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent i = new Intent(ThreadViewActivity.this, OptionsSubActivity.class);
                startActivity(i);
            }
        });

        alertDialogSave.show();
    }

    /**
     * Method used to stop an ongoing conversation and start a new conversation.
     *
     * @param v View
     */
    private void onNewConversation(View v) {

        speech.stopListening();
        speech.destroy();
        progressBar.setIndeterminate(false);

        generateNewConversationConfirmationDialog();

    }

    /**
     * Method used to show the dialog related to new conversation confirmation.
     */
    private void generateNewConversationConfirmationDialog() {
        AlertDialog.Builder alertDialogStop = new AlertDialog.Builder(ThreadViewActivity.this);

        // Get the layout inflater
        LayoutInflater inflater1 = ThreadViewActivity.this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater1.inflate(R.layout.no_internet_view, null);
        alertDialogStop.setView(dialogView);

        dialogTitle = (TextView) dialogView.findViewById(R.id.title);
        dialogMessage = (TextView) dialogView.findViewById(R.id.message);

        dialogTitle.setText("Start New Conversation");
        dialogMessage.setText("Are you sure you want to start a new conversation?");
        alertDialogStop.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LOGGER.info("Calling the method to display save conversation dialog");
                generateSaveConversationDialog();

            }
        });
        alertDialogStop.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                speech = SpeechRecognizer.createSpeechRecognizer(ThreadViewActivity.this);
                speech.setRecognitionListener(ThreadViewActivity.this);
                progressBar.setIndeterminate(true);
                displayConversation();
            }
        });
        alertDialogStop.show();
    }

    /**
     * Method used to generate dialog related to saving a conversation.
     */
    private void generateSaveConversationDialog() {
        AlertDialog.Builder alertDialogSave = new AlertDialog.Builder(this);

        // Get the layout inflater
        LayoutInflater inflater2 = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater2.inflate(R.layout.save_filename_view, null);
        alertDialogSave.setView(dialogView);
        givenFileName = (EditText) dialogView.findViewById(R.id.filename);

        alertDialogSave.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String filename = givenFileName.getText().toString();

                if (filename.isEmpty() || filename == null) {
                    Date d = new Date();
                    filename = (String) DateFormat.format("MM-dd-yy hh-mm-ss", d.getTime());

                }

                //check whether filename already exists
                for (String savedfilename : getApplicationContext().getDir("messagesDir", Context.MODE_PRIVATE).list()) {
                    if (savedfilename.compareTo(filename) == 0) {
                        LOGGER.warning("Filename given to save a conversation already exists");
                        isfileexists = true;
                        break;
                    }
                }

                if (isfileexists) {
                    //generate toast saying that the filename already exists
                    Toast toast = Toast.makeText(getApplicationContext(), "Conversation name already exists", Toast.LENGTH_SHORT);
                    toast.show();
                    isfileexists = false;
                    generateSaveConversationDialogWhenStopped();
                } else {

                    //creating the file
                    File file = new File(messagesDir1, filename);

                    //saving to the file
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(file);
                        ObjectOutputStream of = new ObjectOutputStream(fos);
                        of.writeObject(chatArrayAdapter.getItems());
                        of.flush();
                        of.close();
                        fos.close();
                        Toast toast = Toast.makeText(getApplicationContext(), "Conversation saved", Toast.LENGTH_SHORT);
                        toast.show();
                        Intent i = new Intent(ThreadViewActivity.this, AddParticipantsActivity.class);
                        startActivity(i);
                    } catch (Exception e) {
                        Log.e("InternalStorage", e.getMessage());
                    }
                }
            }
        });

        alertDialogSave.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent i = new Intent(ThreadViewActivity.this, AddParticipantsActivity.class);
                startActivity(i);
            }
        });

        alertDialogSave.show();
    }


    /**
     * Method used to recognize voice.
     */
    public void displayConversation() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                } catch (Exception e) {
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                speech.startListening(intent);

            }
        }.execute();

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onEndOfSpeech() {

        //continuous voice recognition
        displayConversation();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onError(int errorCode) {
        //continuous voice recognition
        displayConversation();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onEvent(int arg0, Bundle arg1) {

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onPartialResults(Bundle arg0) {

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onReadyForSpeech(Bundle arg0) {

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        //generating current time in am pm format
        String delegate = "hh:mm aaa";
        String currentTimeAmPm = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());

        Date d = new Date();

        LOGGER.info("Adding new chat message to the chat array adapter");
        //adding chat messages as items to the chatArrayAdapter
        chatArrayAdapter.add(new ChatMessage(side, matches.get(0), currentTimeAmPm, System.currentTimeMillis(),
                (String) DateFormat.format("MM-dd-yy", d.getTime())));

        //variable used to display the side of the cat bubble
        side = !side;

        //continuous voice recognition
        displayConversation();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onRmsChanged(float rmsdB) {
    }
}