package com.example.test;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * ChatArrayAdapter is the class representing the adapter for chat messages.
 *
 * @author R.H.Ramawickrama
 */
public class ChatArrayAdapter extends ArrayAdapter implements Serializable {

    private TextView participantName;
    private TextView chatText;
    private TextView chatTime;

    private RelativeLayout singleMessageContainer;
    private List chatMessageList = new ArrayList();

    private final static Logger LOGGER = Logger.getLogger(ChatArrayAdapter.class.getName());

    /**
     * Method used to add items to the chat list.
     *
     * @param object Chat Message object
     */
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    /**
     * Class constructor.
     *
     * @param context            Context
     * @param textViewResourceId Resource Id of the text view
     */
    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    /**
     * Method used to get the size of the chat message list.
     *
     * @return list size
     */
    public int getCount() {
        return this.chatMessageList.size();
    }

    /**
     * Method used to retrieve a single Chat Message.
     *
     * @param index position of the object in the list
     * @return ChatMessage object
     */
    public ChatMessage getItem(int index) {
        return (ChatMessage) this.chatMessageList.get(index);
    }

    /**
     * Method used to retrieve all the Chat Message objects in the list.
     *
     * @return chatMessageList list of chat message objects
     */
    public List<ChatMessage> getItems() {

        return chatMessageList;
    }

    /**
     * Method used to generate the conversation thread view.
     *
     * @param position    Position
     * @param convertView View
     * @param parent      ViewGroup
     * @return row Row
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_conversation_chat_item, parent, false);
        }
        singleMessageContainer = (RelativeLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMessage chatMessageObj = getItem(position);

        participantName = (TextView) row.findViewById(R.id.name);

        LOGGER.info("Setting name, message and time in the chat display");
        //Mocking the participant's name
        if (chatMessageObj.isLeft())
            participantName.setText("John");
        else
            participantName.setText("Sarah");

        chatText = (TextView) row.findViewById(R.id.singleMessage);
        chatText.setText(chatMessageObj.getMessage());
        chatText.setBackgroundResource(chatMessageObj.isLeft() ? R.drawable.bubble_b_left : R.drawable.bubble_a_right);

        chatTime = (TextView) row.findViewById(R.id.time);
        chatTime.setText(chatMessageObj.getTime());

        singleMessageContainer.setGravity(chatMessageObj.isLeft() ? Gravity.LEFT : Gravity.RIGHT);

        return row;
    }
}