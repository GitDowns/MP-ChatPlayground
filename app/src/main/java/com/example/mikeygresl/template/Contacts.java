package com.example.mikeygresl.template;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//Activity containing all info about current User
//his personal data
//his contacts
//his chats
//all users - to search for a new contact

public class Contacts extends AppCompatActivity implements View.OnClickListener {

    private EditText searchEditText;
    private Button addContactBtn;
    private LinearLayout contactsLayout;
    private RecyclerView contactsRecyclerView;
    private RecyclerView.Adapter contactsAdapter;
    private RecyclerView.LayoutManager contactsLayoutManager;

    private User currentUser;
    private static List<User> users;
    private List<Contact> contacts;

    private FirebaseAuth authentication;
    private FirebaseUser currentFbUser;
    private DatabaseReference dbref;
    private DatabaseReference usersRef;
    private DatabaseReference contactsRef;
    private DatabaseReference chatsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initLayout();
        initFirebase();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                initUsers(dataSnapshot);
                initCurrentUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        contactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                initContacts(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        chatsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                initChats(dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//
//            }
//        });

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchEmail = searchEditText.getText().toString();
                boolean found = false;

                if (searchEmail.isEmpty()) {

                    Toast.makeText(getApplicationContext(),"Email field is empty!", Toast.LENGTH_SHORT).show();
                }
                else {

                    for (int i = 0; i < users.size(); i++) {


                        if (searchEmail.equals(users.get(i).getEmail())) {
                            found = true;
                            contactsRef.child(users.get(i).getUID()).setValue(true);
                            break;
                        }
                    }

                    if (found == true) {
                        Toast.makeText(getApplicationContext(), searchEmail + " added to your contacts!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), searchEmail + " doesn't exist!", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        authentication.signOut();
    }

    private void getIntentData() {

        Intent intent = getIntent();

        currentUser = new User();
        currentUser.setUID(intent.getStringExtra("currentUserUID"));
        currentUser.setFname(intent.getStringExtra("currentUserFname"));
        currentUser.setLname(intent.getStringExtra("currentUserLname"));
        currentUser.setEmail(intent.getStringExtra("currentUserEmail"));

        int usersSize = Integer.parseInt(intent.getStringExtra("users_size"));

        String [] UIDs = new String[usersSize];
        String [] fnames = new String[usersSize];
        String [] lnames = new String[usersSize];
        String [] emails = new String[usersSize];

        UIDs = intent.getStringArrayExtra("UIDs");
        fnames = intent.getStringArrayExtra("fnames");
        lnames = intent.getStringArrayExtra("lnames");
        emails = intent.getStringArrayExtra("emails");

        users = new ArrayList<User>();

        for (int i = 0; i < usersSize; i++) {

            users.get(i).setUID(UIDs[i]);
            users.get(i).setFname(fnames[i]);
            users.get(i).setLname(lnames[i]);
            users.get(i).setEmail(emails[i]);
        }
    }

    private void initLayout() {

        searchEditText = (EditText)findViewById(R.id.searchEditText);
        addContactBtn = (Button)findViewById(R.id.addContactBtn);
        contactsLayout = (LinearLayout)findViewById(R.id.contacts_layout);
    }

    private void initFirebase() {

        authentication = FirebaseAuth.getInstance();
        currentFbUser = authentication.getCurrentUser();
        dbref = FirebaseDatabase.getInstance().getReference();
        usersRef = dbref.child("users");
        contactsRef = dbref.child("contacts/" + currentFbUser.getUid());
        chatsRef = dbref.child("chats");
    }

    private void initUsers(DataSnapshot dataSnapshot) {

        users = new ArrayList<User>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

            User user = snapshot.getValue(User.class);
            users.add(user);
        }
    }

    private void initCurrentUser() {

        currentUser = new User();
        currentUser.setUID(currentFbUser.getUid());
        Log.d("UID", currentUser.getUID());

        for (int i = 0; i < users.size(); i++) {

            if (currentUser.getUID().equals(users.get(i).getUID())) {

                currentUser.setFname(users.get(i).getFname());
                currentUser.setLname(users.get(i).getLname());
                currentUser.setEmail(users.get(i).getEmail());
            }
        }
    }

    private void initContacts(DataSnapshot dataSnapshot) {

        contacts = new ArrayList<Contact>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

            Contact contact = new Contact();
            contact.setUID(snapshot.getKey());

            for (int i = 0; i < users.size(); i++) {

                if (contact.getUID().equals(users.get(i).getUID())) {

                    contact.setEmail(users.get(i).getEmail());
                    contact.setFname(users.get(i).getFname());
                    contact.setLname(users.get(i).getLname());
                    contacts.add(contact);
                }
            }
        }

        //initialize recycler view contacts of current user
        contactsRecyclerView = (RecyclerView)findViewById(R.id.contactsRecyclerView);
        contactsRecyclerView.setHasFixedSize(true);
        contactsLayoutManager = new LinearLayoutManager(getApplicationContext());
        contactsRecyclerView.setLayoutManager(contactsLayoutManager);
        contactsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        contactsAdapter = new ContactsAdapter(contacts);
        ((ContactsAdapter) contactsAdapter).setOnContactClickListener(this);
        contactsRecyclerView.setAdapter(contactsAdapter);
    }

    private void initChats(DataSnapshot dataSnapshot) {

        //get only the chats
        //where CID contains current UID
        if (dataSnapshot.getChildren() != null) {

            for (DataSnapshot chatsSnapshot : dataSnapshot.getChildren()) {

                //Log.d("CHAT ID", chatsSnapshot.getKey());
                if (currentUser == null)
                Log.d("currentUID", "SOSAT'");

                if (chatsSnapshot.getKey().contains(currentUser.getUID())) {

                    Conversation chat = new Conversation(chatsSnapshot.getKey());
                    currentUser.loadChats(chat);
                }
            }
        }
    }

    private List<Message> initMessages(DataSnapshot dataSnapshot) {

        List<Message> messages = new ArrayList<>();

        for (DataSnapshot messagesSnapshots : dataSnapshot.getChildren()) {

            messages.add(messagesSnapshots.getValue(Message.class));
        }

        return messages;
    }

    @Override
    public void onClick(View v) {

        final Intent chatIntent = new Intent(getApplicationContext(), Chat.class);

        //check CID already exist
        //if so, load the data

        //else create new CID

        final int adapterPosition = contactsRecyclerView.getChildAdapterPosition(v);
        String CID = currentUser.getUID() + contacts.get(adapterPosition).getUID();

        for (int i = 0; i < currentUser.getChats().size(); i++) {

            if (currentUser.getChats().get(i).getCID().contains(contacts.get(adapterPosition).getUID())) {

                CID = currentUser.getChats().get(i).getCID();
                final DatabaseReference messagesRef = chatsRef.child(CID);

                messagesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        List<Message> messages = initMessages(dataSnapshot);
                        String [] MIDs = new String[messages.size()];
                        String [] timestamps = new String[messages.size()];
                        String [] types = new String[messages.size()];
                        String [] sender_ids = new String[messages.size()];
                        String [] rec_ids = new String[messages.size()];
                        CharSequence [] contents = new CharSequence[messages.size()];
                        String [] urls = new String[messages.size()];

                        int i = 0;

                        for (Message message : messages) {

                            MIDs[i] = message.getMID();
                            timestamps[i] = message.getTimestamp();
                            types[i] = message.getType();
                            sender_ids[i] = message.getSender_id();
                            rec_ids[i] = message.getRec_id();
                            contents[i] = message.getContent();
                            urls[i] = message.getURL();

                            chatIntent.putExtra("MIDs", MIDs);
                            chatIntent.putExtra("timestamps", timestamps);
                            chatIntent.putExtra("types", types);
                            chatIntent.putExtra("sender_ids", sender_ids);
                            chatIntent.putExtra("rec_ids", rec_ids);
                            chatIntent.putExtra("contents", contents);
                            chatIntent.putExtra("urls", urls);

                            i++;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        chatIntent.putExtra("CID", CID);
        chatIntent.putExtra("sender_UID", currentUser.getUID());
        chatIntent.putExtra("rec_UID", contacts.get(adapterPosition).getUID());

        //retrieve rec_id
        //finish();
        startActivity(chatIntent);

    }
}
