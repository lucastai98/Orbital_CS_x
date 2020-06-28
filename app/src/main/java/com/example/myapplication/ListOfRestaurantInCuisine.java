package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ListOfRestaurantInCuisine extends AppCompatActivity {
    private Toolbar mToolbar;

    private RecyclerView RestList;

    private DatabaseReference CuisinesRef,allRestaurantsDatabaseReference, currentUserRef;

    private String cuisineName;

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
//                viewHolder.setName(find.getName());
//                viewHolder.setLocation(find.getMall()+", "+find.getUnit());
//                viewHolder.setRestaurantPicture(find.getImagelink());
//                viewHolder.setType(find.getCuisineone()+", "+find.getCuisinetwo());
//                viewHolder.setFavouriteButtonStatus(currentUserId, find);
//
//                viewHolder.favouriteButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        favouriteChecker = true;
//
//                        currentUserRef.child("favourite restaurants").child(Long.toString(find.getId()));
//
//                        final DatabaseReference RestaurantRef = allRestaurantsDatabaseReference.child("Restaurant "+find.id).child("favourites");
//                        final DatabaseReference RestaurantListRef = allRestaurantsDatabaseReference.child("Restaurant "+find.id);
//
//                        RestaurantListRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if(favouriteChecker) {
//                                    if (dataSnapshot.child("favourites").hasChild(currentUserId)) {
//
//                                        RestaurantRef.child(currentUserId).removeValue();
//                                        favouriteChecker = false;
//                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id)).removeValue();
//
//                                    } else {
//
//                                        RestaurantRef.child(currentUserId).setValue(true);
//                                        favouriteChecker = false;
//                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
//                                                .child("asianorwestern").setValue(dataSnapshot.child("asianorwestern").getValue());
//
//                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
//                                                .child("cuisineone").setValue(dataSnapshot.child("cuisineone").getValue());
//
//                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
//                                                .child("cuisinethree").setValue(dataSnapshot.child("cuisinethree").getValue());
//
//                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
//                                                .child("cuisinetwo").setValue(dataSnapshot.child("cuisinetwo").getValue());
//
//                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
//                                                .child("id").setValue(dataSnapshot.child("id").getValue());
//
//                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
//                                                .child("imagelink").setValue(dataSnapshot.child("imagelink").getValue());
//
//                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
//                                                .child("mall").setValue(dataSnapshot.child("mall").getValue());
//
//                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
//                                                .child("name").setValue(dataSnapshot.child("name").getValue());
//
//                                        currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant "+find.id))
//                                                .child("unit").setValue(dataSnapshot.child("unit").getValue());
//
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                        allRestaurantsDatabaseReference.child("Restaurant "+find.id).child("favourites").child(currentUserId).setValue(true);
//
//                    }
//                });
//
//            }
//        };


        RestList.setAdapter(firebaseRecyclerAdapter);

    }
}
