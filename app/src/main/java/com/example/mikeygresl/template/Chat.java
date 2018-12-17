package com.example.mikeygresl.template;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Chat extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener
{

    //whenever a user signs in
    //contacts activity opens
    //there is a recycler view of contacts
    //whenever a user selects a contact
    //new activity chat is opened
    //all his messages are read
    //and added to the layout
    //whenever a user writes a message

    private final TextView.OnEditorActionListener onEditingActions =
            new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            && event.getAction() == KeyEvent.ACTION_DOWN) {
                        return true;
                    }
                    return false;
                }
            };

    private RecyclerView messagesRecyclerView;          //recycler view to display messages
    private MessagesAdapter messagesAdapter;            //adapter for messagesRecyclerView
    private EditText messageEditText;                   //input for messages
    private Dialog pendingDisplayedDialog;              //dialog to paste image URL
    private LinearLayoutManager lm;                     //layout manager for messagesRecyclerView
    private ImageView action_send_message;              //btn to send message
    private ImageView action_select_image;              //btn to attach image

    private FirebaseAuth authentication;
    private DatabaseReference dbref;
    private DatabaseReference chatsRef;                 //reference to chats db to write messages

    private User currentUser;
    private String CID;
    private String sender_id;
    private String rec_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //no idea what it is
        Picasso.setSingletonInstance(new Picasso.Builder(this)
                .loggingEnabled(true)
                .build());

        setContentView(R.layout.activity_chat);

        initLayout();
        initFirebase();

        //get information about sender and receiver
//        Intent chatIntent = getIntent();
//        CID = chatIntent.getStringExtra("CID");
//        sender_id = chatIntent.getStringExtra("sender_id");
//        rec_id = chatIntent.getStringExtra("rec_id");
//        final String[] MIDs = chatIntent.getStringArrayExtra("MIDs");
//        final String[] timestamps = chatIntent.getStringArrayExtra("timestamps");
//        final String[] types = chatIntent.getStringArrayExtra("types");
//        final String[] sender_ids = chatIntent.getStringArrayExtra("sender_ids");
//        final String[] rec_ids = chatIntent.getStringArrayExtra("rec_ids");
//        final String[] contents = chatIntent.getStringArrayExtra("contents");
//        final String[] urls = chatIntent.getStringArrayExtra("urls");

        action_send_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onMessageSend(null);
                    }
                });

        //when you press attach btn
        action_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View container = LayoutInflater.from(v.getContext())                      //container to keep image
                        .inflate(R.layout.dialog_select_image, null, false);

                final EditText imageInputView = container.findViewById(R.id.input_image);       //to paste url

                container.findViewById(R.id.action_send_message)                                //get container from XML
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (pendingDisplayedDialog != null) {
                                    pendingDisplayedDialog.dismiss();
                                }

                                onMessageSend(imageInputView.getText().toString());
                            }
                        });

                pendingDisplayedDialog =
                        new AlertDialog.Builder(v.getContext(), R.style.Theme_AppCompat_Dialog)
                                .setCancelable(true)
                                .setView(container)
                                .setTitle("Attach image:")
                                .create();

                pendingDisplayedDialog.show();
            }
        });

//        adapter.addMessage(
//                new Message(0,
//                        "https://poster.nicefon.ru/2016_12/20/800x450/189696e2bc985098b8b0f0.jpg",
//                        "hi"));
//        adapter.addMessage(new Message(1, null, "hello"));
//        adapter.addMessage(
//                new Message(0,
//                        "https://poster.nicefon.ru/2016_12/20/800x450/189696e2bc985098b8b0f0.jpg",
//                        ""));

        messagesAdapter.setOnMessageClickListener(this);
        messagesAdapter.setOnLongClickListener(this);
    }

    //function to send messages with and without image attached
    private void onMessageSend(String imageUrl) {

        final String text = messageEditText.getText().toString();

////        chatsRef.push().setValue(CID);
////        String MID = chatsRef.child(CID).push().getKey();
////        Message message = new Message();
////        message.setMID(MID);
////        message.setType("text");
////        message.setSender_id(sender_id);
////        message.setRec_id(rec_id);
////        message.setContent(text);
////        message.setURL(imageUrl);
////        messagesAdapter.addMessage(message);
//
//        chatsRef.child(CID).setValue(Message.class);
        messagesAdapter.addMessage(new Message("Some MID", "text", "", "", text, imageUrl));

        messageEditText.setText(null);
        messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        final int adapterPosition = messagesRecyclerView.getChildAdapterPosition(v);
        final Message message = messagesAdapter.getMessage(adapterPosition);

        new AlertDialog.Builder(this)
                .setTitle("Message Actions")
                .setItems(new CharSequence[]{"Copy"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                if (cm != null) {
                                    cm.setPrimaryClip(ClipData.newPlainText("message", message.getContent()));
                                }
                                break;
                        }
                    }
                })
                .show();
        return true;
    }

    //init layout from xml
    private void initLayout() {

        messageEditText = findViewById(R.id.messageEditText);
        messageEditText.setOnEditorActionListener(onEditingActions);

        lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        lm.setReverseLayout(false);

        messagesAdapter = new MessagesAdapter();

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messagesRecyclerView.setLayoutManager(lm);
        messagesRecyclerView.setAdapter(messagesAdapter);

        action_send_message = findViewById(R.id.action_send_message);
        action_select_image = findViewById(R.id.action_select_image);
    }

    private void initFirebase() {

        authentication = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference();
        chatsRef = dbref.child("chats");
    }

}

