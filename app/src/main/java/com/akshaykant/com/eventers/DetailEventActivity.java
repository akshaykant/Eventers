package com.akshaykant.com.eventers;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.akshaykant.com.eventers.databinding.ActivityDetailEventBinding;
import com.bumptech.glide.Glide;

public class DetailEventActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityDetailEventBinding binding;

    Events events;

    private static final String LOG_TAG = "DetailEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_event);

        events = (Events) getIntent().getExtras().getSerializable("event");
        binding.backButton.setOnClickListener(this);
        binding.navigateFab.setOnClickListener(this);


        String url = "https://maps.googleapis.com/maps/api/staticmap?center="
                + events.getLatitude() + "," + events.getLongitude()
                + "&zoom=15&size=400x300&sensor=false";
        Glide.with(binding.eventPhoto.getContext())
                .load(url)
                .into(binding.eventPhoto);

        binding.eventName.setText(events.getEventType());
        binding.articleLocation.setText(events.getLocation());
        binding.articleDateTime.setText(events.getDate() + " " + events.getTime());
        binding.articleDressCode.setText(events.getDressCode());

        if (events.getWeather().equals("art_clear"))
            binding.articleWeatherImg.setImageResource(R.drawable.art_clear);
        else if (events.getWeather().equals("art_clouds"))
            binding.articleWeatherImg.setImageResource(R.drawable.art_clouds);
        else if (events.getWeather().equals("art_fog"))
            binding.articleWeatherImg.setImageResource(R.drawable.art_fog);
        else if (events.getWeather().equals("art_light_clouds"))
            binding.articleWeatherImg.setImageResource(R.drawable.art_light_clouds);
        else if (events.getWeather().equals("art_light_rain"))
            binding.articleWeatherImg.setImageResource(R.drawable.art_light_rain);
        else if (events.getWeather().equals("art_rain"))
            binding.articleWeatherImg.setImageResource(R.drawable.art_rain);
        else if (events.getWeather().equals("art_snow"))
            binding.articleWeatherImg.setImageResource(R.drawable.art_snow);
        else if (events.getWeather().equals("art_storm"))
            binding.articleWeatherImg.setImageResource(R.drawable.art_storm);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        return;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_button) {
            Intent intent = new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return;
        }
        if (view.getId() == R.id.navigate_fab) {

            Uri geoLocation = Uri.parse("geo:" + events.getLatitude() + "," + events.getLongitude());

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geoLocation);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Log.d(LOG_TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
            }
        }
    }
}
