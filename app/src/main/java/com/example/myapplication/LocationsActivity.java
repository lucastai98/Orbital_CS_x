package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class LocationsActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    private RecyclerView LocationList;

    private DatabaseReference LocationRef;

    private String locationName;
    private ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

//        CuisinesRef = FirebaseDatabase.getInstance().getReference().child("Cuisines").child("Cuisines");

        LocationRef = FirebaseDatabase.getInstance().getReference().child("Locations").child("Locations");

        mToolbar = (Toolbar) findViewById(R.id.location_list_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Locations");

        LocationList = (RecyclerView) findViewById(R.id.location_list);
        LocationList.setHasFixedSize(true);
        LocationList.setLayoutManager(new LinearLayoutManager(this));
        backButton = (ImageButton) findViewById(R.id.location_list_back_button);
        backButton.bringToFront();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(LocationsActivity.this,MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });


        DisplayLocationList();
//
    }
    //
    private void DisplayLocationList() {

        Query MallQuery = LocationRef.orderByChild("name");

        FirebaseRecyclerAdapter<Cuisine, LocationsActivity.LocationListViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Cuisine, LocationsActivity.LocationListViewHolder>
                (
                        Cuisine.class,
                        R.layout.all_cuisines_display_layout,
                        LocationsActivity.LocationListViewHolder.class,
                        MallQuery
                )
        {
            @Override
            protected void populateViewHolder(LocationsActivity.LocationListViewHolder cuisineListViewHolder, final Cuisine cuisine, final int i) {
                cuisineListViewHolder.setCuisineName(cuisine.getName());

                cuisineListViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationName = cuisine.getName();

                        Intent profileIntent = new Intent(LocationsActivity.this,RestaurantListActivity.class);
                        profileIntent.putExtra("Location",locationName);
                        startActivity(profileIntent);
                        finish();

                    }
                });
            }
        };

        LocationList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class LocationListViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public LocationListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCuisineName (String fullname){
            TextView myName = (TextView) mView.findViewById(R.id.cuisine_name);
            myName.setText(fullname);
        }

    }

}
