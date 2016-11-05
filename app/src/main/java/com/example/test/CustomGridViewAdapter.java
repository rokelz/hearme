package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Created by USER on 5/10/2016.
 *
 * @author R.K.Opatha (IT13510572)
 * @see BaseAdapter
 * Use to handle Adapter to view the saved recordings after retrieving from the database
 */
public class CustomGridViewAdapter extends BaseAdapter {

    int layoutResourceId;
    String[] result;
    String[] idResult;
    String[] pResult;
    private static final int TEXT_ID = 0;
    Context context;
    public EditText input;
    public String newProfileName;
    SqliteController controller;
    private static LayoutInflater inflater = null;
    private final static Logger LOGGER = Logger.getLogger(CustomGridViewAdapter.class.getName());

    /**
     * Use to initialize the adapter class sending the activated activity class and the data set.
     *
     * @param mainActivity
     * @param prgmNameList
     * @param idArray
     * @param pathArray
     */
    public CustomGridViewAdapter(viewrecords mainActivity, String[] prgmNameList, String[] idArray, String[] pathArray) {
        controller = new SqliteController(mainActivity);
        result = prgmNameList;
        idResult = idArray;
        context = mainActivity;
        pResult = pathArray;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public int reposition;
    LinearLayout l;

    /**
     * Use to determine the action when a single list item is clicked for long.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        final View rowView;
        rowView = inflater.inflate(R.layout.row_grid, null);
        holder.tv = (TextView) rowView.findViewById(R.id.item_text);
        holder.img = (ImageView) rowView.findViewById(R.id.item_image);
        l = (LinearLayout) rowView.findViewById(R.id.layoutss);

        holder.tv.setText(result[position]);
        final Intent playMusic = new Intent(context, playmusic.class);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playMusic.putExtra("path", pResult[position]);
                playMusic.putExtra("songname", result[position]);
                context.startActivity(playMusic);

            }
        });
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playMusic.putExtra("path", pResult[position]);
                playMusic.putExtra("songname", result[position]);
                context.startActivity(playMusic);

            }
        });
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic.putExtra("path", pResult[position]);
                playMusic.putExtra("songname", result[position]);
                context.startActivity(playMusic);/*
                //IMediaPlayerFactory factory = new OpenSLMediaPlayerFactory(context);
                //IBasicMediaPlayer player = factory.createMediaPlayer();
                try {
                    //player.setDataSource(context, Uri.parse(pResult[position]));
                    //player.prepare();
                    //player.start();
                    playMusic.putExtra("path",pResult[position]);
                    context.startActivity(playMusic);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        });


        holder.img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                reposition = position;
                System.out.println("-----------------------------------------------------\n" + reposition);
                return false;
            }
        });
        holder.tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                reposition = position;
                System.out.println("-----------------------------------------------------\n" + reposition);
                return false;
            }
        });
        l.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                reposition = position;
                System.out.println("-----------------------------------------------------\n" + reposition);
                return false;
            }
        });
        return rowView;

    }

    /**
     * Use to determine the action when a single list item is clicked for long.
     *
     * @param s
     */
    public void getItemAction(String s) {
        if (s == "Edit") {
            Toast.makeText(context, "You Clicked " + result[reposition], Toast.LENGTH_LONG).show();
            System.out.println("-----------------------------------------------------\n" + reposition);
            System.out.println("-----------------------------------------------------\n" + result[reposition]);
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View promptView = layoutInflater.inflate(R.layout.activity_editprofile, null);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Edit Recording Name...");

            alertDialog.setIcon(R.drawable.icon);
            alertDialog.setView(promptView);

            input = (EditText) promptView.findViewById(R.id.edittext);
            input.setText(result[reposition]);


            alertDialog.setPositiveButton("Save",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int which) {
                            // User pressed Cancel button. Write Logic Here
                            Toast.makeText(context, "You clicked on Save", Toast.LENGTH_SHORT).show();
                            newProfileName = input.getText().toString();
                            File currentFile = new File(pResult[reposition]);
                            String outFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + newProfileName + ".3gp";
                            System.out.println("************************************");
                            System.out.println("-----------------------------------------------------\n" + reposition);
                            System.out.println(pResult[reposition]);
                            System.out.println(outFile);
                            File newFile = new File(outFile);
                            File sdcard = Environment.getExternalStorageDirectory();
                            File to = new File(sdcard, newProfileName + ".3gp");
                            File from = new File(pResult[reposition]);
                            //File to = new File(sdcard,"recording.3gp");
                            //File from = new File(sdcard,"recordingedited.3gp");
                            System.out.println(to);
                            System.out.println(from);
                            boolean answer = from.renameTo(to);
                            System.out.println(answer);
                            if (answer == true) {
                                Log.i("HearMe", "Success");
                                controller.editRecordingName(idResult[reposition], newProfileName, outFile);
                                Toast.makeText(context, "Profile is edited successfully!", Toast.LENGTH_SHORT).show();
                                context.startActivity(new Intent(context, viewrecords.class));
                            } else {

                                Log.i("HearMe", "Fail");
                            }


                        }
                    });
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Toast.makeText(context,
                                    "You clicked on Cancel",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
            alertDialog.show();


        } else if (s == "Delete") {
            Toast.makeText(context, "You Clicked " + result[reposition], Toast.LENGTH_LONG).show();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Delete File...");

            alertDialog.setMessage("Do you want to Delete this file?");

            alertDialog.setIcon(R.drawable.icon);

            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int which) {

                            Toast.makeText(context, "You clicked on YES", Toast.LENGTH_SHORT).show();
                            File currentFile = new File(pResult[reposition]);
                            Boolean deleted = currentFile.delete();

                            if (deleted) {
                                Log.i("HearMe", "Success Deleted");
                                controller.deleteRecording(idResult[reposition]);
                                Toast.makeText(context, "Profile is deleted successfully!", Toast.LENGTH_SHORT).show();
                                context.startActivity(new Intent(context, viewrecords.class));
                            } else {
                                Log.i("HearMe", "Failed Deleted");
                            }

                        }
                    });
            alertDialog.setNeutralButton("NO",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Toast.makeText(context,
                                    "You clicked on NO", Toast.LENGTH_SHORT)
                                    .show();


                        }
                    });
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Toast.makeText(context,
                                    "You clicked on Cancel",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
            alertDialog.show();
        } else if (s == "Share") {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            //Uri screenshotUri = Uri.parse("android.resource://comexample.sairamkrishna.myapplication/*");
            Uri filetype = Uri.parse("file://" + pResult[reposition]);

            try {
                InputStream stream = context.getContentResolver().openInputStream(filetype);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            sharingIntent.setType("video/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, filetype);
            context.startActivity(Intent.createChooser(sharingIntent, "Share music using"));

        }
    }

    /**
     * Use to declare a class holder to keep the profile name, delete option and edit option in the listview
     * for each data item.
     */
    static class Holder {
        TextView tv;
        ImageView img;

    }
}