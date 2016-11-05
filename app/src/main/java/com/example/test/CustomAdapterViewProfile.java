package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by USER on 5/9/2016.
 *
 * @author R.K.Opatha (IT13510572)
 * @see BaseAdapter
 * Use to handle Adapter to view the saved profiles after retrieving from the database
 */
public class CustomAdapterViewProfile extends BaseAdapter implements AdapterView.OnItemSelectedListener {
    String[] result;
    String[] idResult;
    private static final int TEXT_ID = 0;
    Context context;
    public EditText input;
    int[] imageId;
    public String newProfileName;
    SqliteController controller;
    private static LayoutInflater inflater = null;
    private final static Logger LOGGER = Logger.getLogger(CustomAdapterViewProfile.class.getName());

    /**
     * Use to initialize the adapter class sending the activated activity class and the data set.
     *
     * @param mainActivity
     * @param prgmNameList
     * @param idArray
     */
    public CustomAdapterViewProfile(savedProfiles mainActivity, String[] prgmNameList, String[] idArray) {
        // TODO Auto-generated constructor stub
        controller = new SqliteController(mainActivity);
        result = prgmNameList;
        idResult = idArray;
        context = mainActivity;

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

    public int selectedItemOnSpinner;

    /**
     * Use to handle the long item click listener to set a reminder using google
     * calander option.
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if (item == "After 3 days") {
            selectedItemOnSpinner = 1;
        } else if (item == "After 5 days") {
            selectedItemOnSpinner = 2;
        } else if (item == "After 7 days") {
            selectedItemOnSpinner = 3;
        } else if (item == "Every 3 days") {
            selectedItemOnSpinner = 4;
        } else if (item == "Every 5 days") {
            selectedItemOnSpinner = 5;
        } else if (item == "Every 7 days") {
            selectedItemOnSpinner = 6;
        } else {
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Use to declare a class holder to keep the profile name, delete option and edit option in the listview
     * for each data item.
     */
    public class Holder {
        TextView tv;
        ImageButton img;
        Button img1;
    }

    public int reposition;

    /**
     * Use to get the value of the variable reposition.
     *
     * @return
     */
    public int getReposition() {
        return reposition;
    }

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
        // TODO Auto-generated method stub
        final Holder holder = new Holder();
        final View rowView;
        rowView = inflater.inflate(R.layout.activity_singleprofile, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textView);
        // holder.img=(ImageButton) rowView.findViewById(R.id.imageButton39);
        holder.img1 = (Button) rowView.findViewById(R.id.imageButton40);
        holder.tv.setText(result[position]);

       /* holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //editName()
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.activity_editprofile, null);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Edit Profile...");

                alertDialog.setIcon(R.drawable.icon);
                alertDialog.setView(promptView);

               input = (EditText) promptView.findViewById(R.id.edittext);
                input.setText(result[position]);

                System.out.println("-----------------------------------------------------\n"+newProfileName);
                alertDialog.setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // User pressed Cancel button. Write Logic Here
                                Toast.makeText(context,"You clicked on Save",Toast.LENGTH_SHORT).show();
                                newProfileName = input.getText().toString();
                                controller.editProfile(idResult[position], newProfileName);
                                Toast.makeText(context,"Profile is edited successfully!",Toast.LENGTH_SHORT).show();
                                context.startActivity(new Intent(context,savedProfiles.class));
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

                Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();

            }
        });*/
        holder.img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //delete()
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Delete File...");

                alertDialog.setMessage("Do you want to Delete this file?");

                alertDialog.setIcon(R.drawable.icon);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // User pressed Cancel button. Write Logic Here
                                Toast.makeText(context, "You clicked on YES", Toast.LENGTH_SHORT).show();
                                System.out.println(idResult[position]);
                                controller.deleteProfile(idResult[position]);
                                Toast.makeText(context, "Profile is deleted successfully!", Toast.LENGTH_SHORT).show();
                                context.startActivity(new Intent(context, savedProfiles.class));
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
        return rowView;
    }

    /**
     * Use to handle the plotting activity of the selected saved sound profile
     * with the past test log activities and predicting the precautions if neccessary.
     *
     * @param post
     */
    public void profileAnalysis(int post) {

        String id = result[post];
        Intent grapgh = new Intent(context, GraphActivity.class);
        System.out.println(post);
        System.out.println(id);
        grapgh.putExtra("profileID", id);
        context.startActivity(grapgh);

    }

    TextView inputForProfile;
    Spinner spinner;

    /**
     * Use to handle the reminder adding activity using the google calendar
     * services with predefined set of days to remind for testing purpose.
     *
     * @param post
     */
    public void RemindProfileTest(int post) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.activity_remind, null);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Remind Testing...");
        alertDialog.setIcon(R.drawable.icon);
        alertDialog.setView(promptView);

        inputForProfile = (TextView) promptView.findViewById(R.id.textView20);
        spinner = (Spinner) promptView.findViewById(R.id.spinner2);
        inputForProfile.setText(result[post]);
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("After 3 days");
        categories.add("After 5 days");
        categories.add("After 7 days");
        categories.add("Every 3 days");
        categories.add("Every 5 days");
        categories.add("Every 7 days");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Calendar ce = Calendar.getInstance();
                        String message = "This is a reminder to take the hearing test For the profile " + result[reposition] + " !!";
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Intent intent = new Intent(Intent.ACTION_INSERT);
                        intent.setData(CalendarContract.Events.CONTENT_URI);
                        intent.setType("vnd.android.cursor.item/event");

                        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                        intent.putExtra(CalendarContract.Events.TITLE, "Hear Me");
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, message);

                        String formattedDate;
                        switch (selectedItemOnSpinner) {
                            case 1:
                                Calendar c = Calendar.getInstance();
                                c.add(Calendar.DAY_OF_MONTH, 3);

                                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, c.getTimeInMillis());
                                //intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");
                                context.startActivity(intent);


                                break;
                            case 2:
                                Calendar c1 = Calendar.getInstance();
                                c1.add(Calendar.DAY_OF_MONTH, 5);
                                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, c1.getTimeInMillis());
                                //intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");
                                context.startActivity(intent);

                                break;
                            case 3:
                                Calendar c2 = Calendar.getInstance();
                                c2.add(Calendar.DAY_OF_MONTH, 7);
                                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, c2.getTimeInMillis());
                                //intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");
                                context.startActivity(intent);

                                break;
                            case 4:
                                Calendar c3 = Calendar.getInstance();
                                c3.add(Calendar.DAY_OF_MONTH, 3);
                                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, c3.getTimeInMillis());
                                intent.putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY;INTERVAL=3;");
                                context.startActivity(intent);

                                break;
                            case 5:
                                Calendar c4 = Calendar.getInstance();
                                c4.add(Calendar.DAY_OF_MONTH, 5);
                                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, c4.getTimeInMillis());
                                intent.putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY;INTERVAL=5;");
                                context.startActivity(intent);

                                break;
                            case 6:
                                Calendar c5 = Calendar.getInstance();
                                c5.add(Calendar.DAY_OF_MONTH, 7);
                                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, c5.getTimeInMillis());
                                intent.putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY;INTERVAL=7;");
                                context.startActivity(intent);


                                break;
                        }


                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int which) {


                    }
                });
        alertDialog.show();

    }

    /**
     * Use to handle the retest option for a given saved sound profile and update the sound levels of
     * each ear.
     *
     * @param post
     */
    public void profileRetest(int post) {

        String id = result[post];
        Intent grapgh = new Intent(context, retakeleft.class);
        System.out.println(post);
        System.out.println(id);
        grapgh.putExtra("retakeprofname", id);
        grapgh.putExtra("retakeprofid", idResult[post]);
        context.startActivity(grapgh);

    }


}
