package com.akshaykant.com.eventers;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.akshaykant.com.eventers.data.EventContract;
import com.akshaykant.com.eventers.databinding.ActivityNewEventBinding;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class NewEventActivity extends AppCompatActivity implements View.OnClickListener {

    @SuppressWarnings("HardCodedStringLiteral")
    private static final String EVENTS = "events";
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String MM_DD_YY = "MM/dd/yy";
    ActivityNewEventBinding binding;
    ImageView rightToolbarIcon;
    ImageView leftToolbarIcon;
    TextView centerToolbarText;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    double latitude;
    double longitude;


    @SuppressWarnings("HardCodedStringLiteral")
    private static final String TAG = "NewEventActivity";

    //For Places API
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1000;

    /*Two classes from Firebase Database API.*/
    //Firebase database object is the entry point for our app to access the database.
    private FirebaseDatabase mFirebaseDatabase;
    //Database Reference object is a class that reference a specific part of the database.
    // This will be referencing the messaging portion of our database.
    private DatabaseReference mMessagesDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_event);

        /*instantiate the two firebase database object*/
        //getting instance to the firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //getting reference to the specific part of the database.
        // getReference() will get the reference to the root, while child() will refer to the specific part i.e. "events"
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child(EVENTS);

        rightToolbarIcon = (ImageView) findViewById(R.id.right_icon_toolbar);
        leftToolbarIcon = (ImageView) findViewById(R.id.left_icon_toolbar);
        centerToolbarText = (TextView) findViewById(R.id.center_text_toolbar);

        centerToolbarText.setText(R.string.create_event);
        leftToolbarIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black));
        rightToolbarIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_done));

        //Date Picker
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        binding.editDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(NewEventActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        //Time Picker
        binding.editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NewEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        binding.editTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle(getString(R.string.select_time));
                mTimePicker.show();
            }
        });

        //back button functionality
        leftToolbarIcon.setOnClickListener(this);

        //Create button functionality
        rightToolbarIcon.setOnClickListener(this);
    }


    //Places API on click of Location EditText
    public void onLocationEditTextClick(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.i(TAG, e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.i(TAG, e.getMessage());
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.left_icon_toolbar) {
            Intent intent = new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return;
        }

        if (view.getId() == R.id.right_icon_toolbar) {

            if (TextUtils.isEmpty(binding.editEventType.getText().toString()) && TextUtils.isEmpty(binding.editLocation.getText().toString())
                    && TextUtils.isEmpty(binding.editDate.getText().toString()) && TextUtils.isEmpty(binding.editTime.getText().toString())
                    && TextUtils.isEmpty(binding.editDressStyle.getText().toString())) {

                Toast.makeText(NewEventActivity.this, R.string.empty_fields, Toast.LENGTH_SHORT).show();


            } else {

                //Add to the Database the event details

                String[] array = this.getResources().getStringArray(R.array.weather);
                String weather = array[new Random().nextInt(array.length)];
                String organiser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                //Create a object for events
                Events events = new Events(binding.editEventType.getText().toString(), binding.editLocation.getText().toString(),
                        binding.editDate.getText().toString(), binding.editTime.getText().toString(),
                        binding.editDressStyle.getText().toString(), weather, organiser, latitude, longitude);

                /*A push ID contains 120 bits of information.
                        The first 48 bits are a timestamp, which both reduces the chance of collision
                and allows consecutively created push IDs to sort chronologically.
                        The timestamp is followed by 72 bits of randomness,
                        which ensures that even two people creating push IDs at the exact same millisecond
                are extremely unlikely to generate identical IDs.*/
                mMessagesDatabaseReference.push().setValue(events);

                Toast.makeText(NewEventActivity.this, R.string.successfully_added, Toast.LENGTH_SHORT).show();

                /*Insert to the SQLLITE DB using Content Provider*/
                // Create a ContentValues object where column names are the keys,
                // and pet attributes from the editor are the values.
                ContentValues values = new ContentValues();
                values.put(EventContract.EventsEntry.COLUMN_EVENT_TYPE, binding.editEventType.getText().toString());
                values.put(EventContract.EventsEntry.COLUMN_EVENT_LOCATION, binding.editLocation.getText().toString());
                values.put(EventContract.EventsEntry.COLUMN_EVENT_DATE, binding.editDate.getText().toString());
                values.put(EventContract.EventsEntry.COLUMN_EVENT_TIME, binding.editTime.getText().toString());
                values.put(EventContract.EventsEntry.COLUMN_EVENT_DRESS, binding.editDressStyle.getText().toString());
                values.put(EventContract.EventsEntry.COLUMN_EVENT_WEATHER, weather);
                values.put(EventContract.EventsEntry.COLUMN_EVENT_ORGANISER, organiser);
                values.put(EventContract.EventsEntry.COLUMN_EVENT_LATITUDE, latitude);
                values.put(EventContract.EventsEntry.COLUMN_EVENT_LONGITUDE, longitude);

                // Insert a new pet into the provider, returning the content URI for the new pet.
                Uri newUri = getContentResolver().insert(EventContract.EventsEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_event_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_insert_event_successful),
                            Toast.LENGTH_SHORT).show();
                }


                Intent intent = new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return;

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, getString(R.string.place) + place.getName());
                binding.editLocation.setText(place.getName());
                LatLng latLong = place.getLatLng();

                latitude = latLong.latitude;
                longitude = latLong.longitude;


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void updateLabel() {

        String myFormat = MM_DD_YY; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        binding.editDate.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        return;
    }
}
