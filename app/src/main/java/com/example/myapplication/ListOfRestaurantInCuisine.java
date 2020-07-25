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
import com.google.firebase.auth.FirebaseAuth;
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
    private ImageButton favouriteButton;

    private boolean favouriteChecker;
    private boolean favResChecker;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_restaurant_in_cuisine);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cuisineName = getIntent().getExtras().get("cuisineName").toString();
        CuisinesRef = FirebaseDatabase.getInstance().getReference().child("Cuisines").child("Cuisines");
        allRestaurantsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Restaurants");
        currentUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        mToolbar = (Toolbar) findViewById(R.id.cuisine_restaurants_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(cuisineName);

        RestList = (RecyclerView) findViewById(R.id.cuisine_restaurant_list);
        RestList.setHasFixedSize(true);
        RestList.setLayoutManager(new LinearLayoutManager(this));

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

        favouriteButton = (ImageButton) findViewById(R.id.cuisine_favourite_button);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favouriteChecker = true;
                currentUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(favouriteChecker) {
                            if (dataSnapshot.hasChild("favourite cuisines")) {
                                if(dataSnapshot.child("favourite cuisines").hasChild(cuisineName)){
                                    currentUserRef.child("favourite cuisines").child(cuisineName).removeValue();
                                    favouriteChecker = false;
                                }else{
                                    currentUserRef.child("favourite cuisines").child(cuisineName).setValue(cuisineName);
                                    favouriteChecker = false;

                                }
                            }else{
                                currentUserRef.child("favourite cuisines").child(cuisineName).setValue(cuisineName);
                                favouriteChecker = false;

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("favourite cuisines")){

                    if(dataSnapshot.child("favourite cuisines").hasChild(cuisineName)){
                        favouriteButton.setImageResource(R.drawable.redheart);
                    }else{
                        favouriteButton.setImageResource(R.drawable.greyheart);
                    }

                }else{
                    favouriteButton.setImageResource(R.drawable.greyheart);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                viewHolder.setLocation1(find.getMall1() + " " + find.getUnit1());
                viewHolder.setLocation2(find.getMall2() + " " + find.getUnit2());
                viewHolder.setLocation3(find.getMall3() + " " + find.getUnit3());
                viewHolder.setRestaurantPicture(find.getImagelink());
                viewHolder.setType(find.getCuisineone() + ", " + find.getCuisinetwo());
                viewHolder.setFavouriteButtonStatus(currentUserId, Long.toString(find.getId()));
                viewHolder.favouriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        favResChecker = true;

                        currentUserRef.child("favourite restaurants").child(Long.toString(find.getId()));

                        final DatabaseReference RestaurantRef = allRestaurantsDatabaseReference.child("Restaurant "+find.id).child("favourites");
                        final DatabaseReference RestaurantListRef = allRestaurantsDatabaseReference.child("Restaurant "+find.id);

                        RestaurantListRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(favResChecker) {
                                    if (dataSnapshot.child("favourites").hasChild(currentUserId)) {

                                        RestaurantRef.child(currentUserId).removeValue();
                                        favResChecker = false;
                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id)).removeValue();

                                    } else {

                                        RestaurantRef.child(currentUserId).setValue(true);
                                        favResChecker = false;
                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
                                                .child("asianorwestern").setValue(dataSnapshot.child("asianorwestern").getValue());

                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
                                                .child("cuisineone").setValue(dataSnapshot.child("cuisineone").getValue());

                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
                                                .child("cuisinethree").setValue(dataSnapshot.child("cuisinethree").getValue());

                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
                                                .child("cuisinetwo").setValue(dataSnapshot.child("cuisinetwo").getValue());

                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
                                                .child("id").setValue(dataSnapshot.child("id").getValue());

                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
                                                .child("imagelink").setValue(dataSnapshot.child("imagelink").getValue());

                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
                                                .child("mall1").setValue(dataSnapshot.child("mall1").getValue());

                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
                                                .child("mall2").setValue(dataSnapshot.child("mall2").getValue());

                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
                                                .child("mall3").setValue(dataSnapshot.child("mall3").getValue());

                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
                                                .child("unit1").setValue(dataSnapshot.child("unit1").getValue());

                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
                                                .child("unit2").setValue(dataSnapshot.child("unit2").getValue());

                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
                                                .child("unit3").setValue(dataSnapshot.child("unit3").getValue());

                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
                                                .child("name").setValue(dataSnapshot.child("name").getValue());

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        allRestaurantsDatabaseReference.child("Restaurant "+find.id).child("favourites").child(currentUserId).setValue(true);

                    }
                });

            }
        };



        RestList.setAdapter(firebaseRecyclerAdapter);

    }

}
