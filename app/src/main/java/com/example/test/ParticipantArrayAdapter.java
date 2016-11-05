package com.example.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ParticipantArrayAdapter is the class representing the adapter for participants.
 *
 * @author R.H.Ramawickrama
 */
public class ParticipantArrayAdapter extends ArrayAdapter implements Serializable {

    private TextView participantName;
    private TextView bubbleColour;

    private LinearLayout singleParticipantContainer;
    private List<Participant> participantList = new ArrayList();

    /**
     * Class constructor.
     *
     * @param context            Context
     * @param textViewResourceId Resource Id of the text view
     */
    public ParticipantArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    /**
     * Method used to add items to the participant list.
     *
     * @param participant Participant object
     */
    public void add(Participant participant) {
        participantList.add(participant);
        super.add(participant);
    }

    /**
     * Method used to get the size of the participant list.
     *
     * @return list size
     */
    public int getCount() {
        return this.participantList.size();
    }

    /**
     * Method used to retrieve a single Participant object.
     *
     * @param index position of the object in the list
     * @return Participant object
     */
    public Participant getItem(int index) {
        return (Participant) this.participantList.get(index);
    }

    /**
     * Method used to retrieve all the participants in the list.
     *
     * @return participantList list of all participants
     */
    public List<Participant> getItems() {

        return participantList;
    }

    /**
     * Method used to generate the conversation thread view
     *
     * @param position    Position
     * @param convertView View
     * @param parent      ViewGroup
     * @return row Row
     */
    @SuppressLint("ResourceAsColor")
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_participant_item, parent, false);
        }
        singleParticipantContainer = (LinearLayout) row.findViewById(R.id.singleParticipantContainer);
        Participant participant = getItem(position);

        participantName = (TextView) row.findViewById(R.id.name);
        bubbleColour = (TextView) row.findViewById(R.id.color);

        participantName.setText(participant.getName());

        if (participant.getColor().equals("blue"))
            bubbleColour.setBackgroundResource(R.drawable.blue);
        else if (participant.getColor().equals("green"))
            bubbleColour.setBackgroundResource(R.drawable.green);

        return row;
    }
}