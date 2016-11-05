package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * PocketSphinxActivity is the class used to implement speech recognition through Pocketsphinx.
 *
 * @author R.H.Ramawickrama
 */
public class PocketSphinxActivity extends ActionBarActivity implements
        RecognitionListener {

    private ListView listView;
    private ProgressBar progressBar;
    private ImageButton pause;
    private ImageButton stop;
    private ImageButton newconvo;
    boolean isPlaying = true;

    private TextView dialogTitle;
    private TextView dialogMessage;
    private EditText givenFileName;

    private ChatArrayAdapter chatArrayAdapter;
    private boolean side = false;
    private boolean isfileexists = false;
    File messagesDir1;
    /* Named searches allow to quickly reconfigure the decoder */
    private static final String SEARCH = "menu";

    private SpeechRecognizer recognizer;

    private final static Logger LOGGER = Logger.getLogger(AddParticipantsActivity.class.getName());


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
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

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseConversation(v);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopConversation(v);
            }
        });

        newconvo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNewConversation(v);
            }
        });

        runRecognizerSetup();
    }

    /**
     * Method used to pause an ongoing conversation.
     *
     * @param view View
     */
    private void onPauseConversation(View view) {
        if (isPlaying) {
            recognizer.stop();
            isPlaying = false;
            progressBar.setIndeterminate(false);
            pause.setImageResource(R.drawable.play1);
        } else {
            runRecognizerSetup();
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

        recognizer.stop();
        progressBar.setIndeterminate(false);

        generateStopConfirmationDialog();
    }

    /**
     * Method used to generate a stop confirmation dialog.
     */
    private void generateStopConfirmationDialog() {
        AlertDialog.Builder alertDialogStop = new AlertDialog.Builder(PocketSphinxActivity.this);

        // Get the layout inflater
        LayoutInflater inflater1 = PocketSphinxActivity.this.getLayoutInflater();

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

                generateSaveConversationDialogWhenStopped();

            }
        });
        alertDialogStop.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                progressBar.setIndeterminate(true);
                runRecognizerSetup();
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
                        Intent i = new Intent(PocketSphinxActivity.this, AllConversationsActivity.class);
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
                Intent i = new Intent(PocketSphinxActivity.this, AddParticipantsActivity.class);
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

        recognizer.stop();
        progressBar.setIndeterminate(false);

        generateNewConversationConfirmationDialog();
    }

    /**
     * Method used to show the dialog related to new conversation confirmation.
     */
    private void generateNewConversationConfirmationDialog() {
        AlertDialog.Builder alertDialogStop = new AlertDialog.Builder(PocketSphinxActivity.this);

        // Get the layout inflater
        LayoutInflater inflater1 = PocketSphinxActivity.this.getLayoutInflater();

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

                generateSaveConversationDialog();

            }
        });
        alertDialogStop.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                progressBar.setIndeterminate(true);
                runRecognizerSetup();
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
                for (String savedfilename : messagesDir1.list()) {
                    if (savedfilename.compareTo(filename) == 0) {
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
                        Intent i = new Intent(PocketSphinxActivity.this, AddParticipantsActivity.class);
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
                Intent i = new Intent(PocketSphinxActivity.this, AddParticipantsActivity.class);
                startActivity(i);
            }
        });

        alertDialogSave.show();
    }

    /**
     * Method used to set up Pocketsphinx.
     */
    private void runRecognizerSetup() {

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(PocketSphinxActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {

                } else {
                    recognizer.stop();
                    recognizer.startListening(SEARCH, 10000);
                }
            }
        }.execute();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        String text = null;
        if (hypothesis != null) {
            text = hypothesis.getHypstr();
            //((TextView) findViewById(R.id.result_text)).setText(text);

            //generating current time in am pm format
            String delegate = "hh:mm aaa";
            String currentTimeAmPm = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());

            Date d = new Date();

            //adding chat messages as items to the chatArrayAdapter
            chatArrayAdapter.add(new ChatMessage(side, text, currentTimeAmPm, System.currentTimeMillis(),
                    (String) DateFormat.format("MM-dd-yy", d.getTime())));

            //variable used to display the side of the cat bubble
            side = !side;

            //continuous voice recognition
            recognizer.stop();
            recognizer.startListening(SEARCH, 10000);
        }
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
    public void onEndOfSpeech() {
        //continuous speech recognition.
        recognizer.stop();
        recognizer.startListening(SEARCH, 10000);
    }

    /**
     * Method used to set up the language model and dictionary.
     *
     * @param assetsDir Assets Directory
     */
    private void setupRecognizer(File assetsDir) throws IOException {

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setKeywordThreshold(1e-45f) // Threshold to tune for keyphrase to balance between false alarms and misses
                .setBoolean("-allphone_ci", true)  // Use context-independent phonetic search, context-dependent is too slow for mobile
                .getRecognizer();
        recognizer.addListener(this);

        // Create grammar-based search for selection between demos
        File menuGrammar = new File(assetsDir, "test.gram");
        recognizer.addGrammarSearch(SEARCH, menuGrammar);

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onError(Exception error) {
        recognizer.stop();
        recognizer.startListening(SEARCH, 10000);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onTimeout() {
        recognizer.stop();
        recognizer.startListening(SEARCH, 10000);
    }
}
