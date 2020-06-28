package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ListOfRestaurantInCuisine extends AppCompatActivity {
    private Toolbar mToolbar;

    private RecyclerView RestList,RestList2,asianWesternList;

    private DatabaseReference CuisinesRef,allRestaurantsDatabaseReference, currentUserRef;

    private String cuisineName;
    private ImageButton backButton;

    private boolean favouriteChecker;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_restaurant_in_cuisine);

        cuisineName = getIntent().getExtras().get("cuisineName").toString();
        CuisinesRef = FirebaseDatabase.getInstance().getReference().child("Cuisines").child("Cuisines");
        allRestaurantsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Restaurants");

        mToolbar = (Toolbar) findViewById(R.id.cuisine_restaurants_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(cuisineName);

        RestList = (RecyclerView) findViewById(R.id.cuisine_restaurant_list);
        RestList.setHasFixedSize(true);
        RestList.setLayoutManager(new LinearLayoutManager(this));

        asianWesternList = (RecyclerView) findViewById(R.id.asian_western_list);
        asianWesternList.setHasFixedSize(true);
        asianWesternList.setLayoutManager(new LinearLayoutManager(this));
        asianWesternList.bringToFront();

        backButton = (ImageButton) findViewById(R.id.cuisine_back_button);
        backButton.bringToFront();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ListOfRestaurantInCuisine.this,CuisinesListActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });


        DisplayRestaurantsInCuisine();

    }

    private void DisplayRestaurantsInCuisine() {

        String CuisineName = cuisineName.toString();

        Query SearchCuisinesQuery = allRestaurantsDatabaseReference.orderByChild(CuisineName).equalTo(cuisineName);

        FirebaseRecyclerAdapter<FindRestaurants, RestaurantListActivity.FindRestaurantsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindRestaurants, RestaurantListActivity.FindRestaurantsViewHolder>
                (
                        FindRestaurants.class,
                        R.layout.all_restaurants_display_layout,
                        RestaurantListActivity.FindRestaurantsViewHolder.class,
                        SearchCuisinesQuery
                ) {

            @Override
            protected void populateViewHolder(final RestaurantListActivity.FindRestaurantsViewHolder viewHolder, final FindRestaurants find, final int i) {
                viewHolder.setName(find.getName());
                viewHolder.setLocation(find.getMall() + ", " + find.getUnit());
                viewHolder.setRestaurantPicture(find.getImagelink());
                viewHolder.setType(find.getCuisineone() + ", " + find.getCuisinetwo());
                viewHolder.setFavouriteButtonInvisible();
            }
        };



        RestList.setAdapter(firebaseRecyclerAdapter);

    }

}
