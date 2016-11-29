package com.akshaykant.com.eventers;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akshaykant.com.eventers.databinding.ActivityMainBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding binding;

    ImageView profileImg;

    private EventAdapter mEventAdapter;

    private static final String TAG = "MainActivity";

    //Set the value RC_SIGN_IN flag used for startActivityForResult for FirebaseUI and don't use the default value.
    private static final int RC_SIGN_IN = 1;

    /*One class from Firebase Auth API*/
    private FirebaseAuth mFirebaseAuth;

    /*Event Listener that reacts to auth state change. It execute when user signs in, signs out, attached  to FriebaseAuth*/
    // Best Practices: attach AuthStateListener in onResume() and detach in onPause()
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    /*Two classes from Firebase Database API.*/
    //Firebase database object is the entry point for our app to access the database.
    private FirebaseDatabase mFirebaseDatabase;
    //Database Reference object is a class that reference a specific part of the database.
    // This will be referencing the messaging portion of our database.
    private DatabaseReference mMessagesDatabaseReference;

    /*Event listener that reacts to the Firebase database changes in the real-time.*/
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        /*Instantiate the firebase auth object*/
        mFirebaseAuth = FirebaseAuth.getInstance();

         /*instantiate the two firebase database object*/
        //getting instance to the firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //getting reference to the specific part of the database.
        // getReference() will get the reference to the root, while child() will refer to the specific part i.e. "events"
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("events");

        profileImg = (ImageView) findViewById(R.id.right_icon_toolbar);
        profileImg.setOnClickListener(this);
        binding.fab.setOnClickListener(this);

        // Initialize message ListView and its adapter
        List<Events> events = new ArrayList<>();
        mEventAdapter = new EventAdapter(this, R.layout.item_event, events);
        binding.eventListView.setAdapter(mEventAdapter);

        //List View onClick listener
        binding.eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(MainActivity.this, DetailEventActivity.class);
                Object object = parent.getAdapter().getItem(position);
                intent.putExtra("event", (Serializable) object);
                startActivity(intent);

            }
        });

       /*Instantiate new AuthStateListener*/
        //Attach and detach in onResume() and onPause()
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //Check the state of the user
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    onSignedInInitialize();


                } else {
                    // user is signed out
                    onSignedOutCleanup();

                    //Show the Sign In Screen
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER
                                    )
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    /*startActivityForResult() return onAcivityResult() with RESULT_OK or RESULT_CANCEL.
    Here you can handle the back button pressed flow from the login page.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Sign in Cancelled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //attach AuthStateListener in onResume()
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //detach AuthStateListener in onPause()
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

        //When the activity is destroyed, the listener will also be cleaned up.
        detachDatabaseReadListener();
        mEventAdapter.clear();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.right_icon_toolbar) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);

        }
        if (view.getId() == R.id.fab) {
            Intent intent = new Intent(this, NewEventActivity.class);
            startActivity(intent);
        }
    }

    private void onSignedInInitialize() {

        //User will be able to get access to the database once successfully logged in.
        attachDatabaseReadListener();

    }

    private void onSignedOutCleanup() {

        //unset the Adapter
        mEventAdapter.clear();

        //detach the read listener
        detachDatabaseReadListener();

    }


    private void attachDatabaseReadListener() {

        //if listener is null, then only attach it
        if (mChildEventListener == null) {
        /*Instantiate new ChildEventListener*/
            mChildEventListener = new ChildEventListener() {
                //This method is called whenever a new child is inserted into the messages list.
                //Importantly, it is also triggered for every child message in the list when the listener is attached.
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                /*DataSnapshot contains data from the firebase database at the specific location
                  at the exact time the listener is triggered*/
                    //In this case, dataSnapshot will always contain the messag that has been added.
                    //The getValue() method can take a parameter which is a class by passing this parameter
                    //the code will deserialize the message from the database into our FriendlyMessage object.
                    Events events = dataSnapshot.getValue(Events.class);
                    //add the FriendlyMessage object to our adapter.
                    mEventAdapter.add(events);

                    // Initialize progress bar
                    binding.progressBar.setVisibility(ProgressBar.INVISIBLE);

                }

                //This is called when the content of the existing message gets changed.
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                //This method is called when an existing message is deleted.
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                //This method is called when whatever message changed position in the list.
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                //This method indicate that some sort of error occurred when you're trying to make changes.
                //Typically this means that you don't have permission to read it.
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

        /*Add the listener to the database reference.*/
            //This will trigger when one of the node of messages changes.
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        //If listener is not null then only detach it
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}