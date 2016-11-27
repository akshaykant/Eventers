package com.akshaykant.com.eventers;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.akshaykant.com.eventers.databinding.ActivityProfileBinding;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityProfileBinding binding;

    /*One class from Firebase Auth API*/
    private FirebaseAuth mFirebaseAuth;

    /*Event Listener that reacts to auth state change. It execute when user signs in, signs out, attached  to FriebaseAuth*/
    // Best Practices: attach AuthStateListener in onResume() and detach in onPause()
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);


          /*Instantiate the firebase auth object*/
        mFirebaseAuth = FirebaseAuth.getInstance();

           /*Instantiate new AuthStateListener*/
        //Attach and detach in onResume() and onPause()
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //Check the state of the user
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in

                } else {
                    // user is signed out
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

        //Get the USerInfo
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        //Display the User name, Email and Image
        binding.articleName.setText(user.getDisplayName());
        binding.articleEmail.setText(user.getEmail());

        if (user.getPhotoUrl() != null) {
            Glide.with(binding.profilePhoto.getContext())
                    .load(user.getPhotoUrl())
                    .into(binding.profilePhoto);
        }

        //back button functionality
        binding.backButton.setOnClickListener(this);

        //Sign out functionality
        binding.cardViewSignOut.setOnClickListener(this);

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
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_button) {
            Intent intent = new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return;
        } else if (view.getId() == R.id.card_view_sign_out) {
            //sign out
            AuthUI.getInstance().signOut(this);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        return;
    }
}
