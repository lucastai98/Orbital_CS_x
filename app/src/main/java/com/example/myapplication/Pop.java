package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Pop extends Activity {

    String favouriteRestaurant;

    String groupID;
    String currentUserID;

    private FirebaseAuth mAuth;

    private RecyclerView ourBestRestaurant;
    private TextView noRestaurantsFound,bestRestaurantForYou;

    private DatabaseReference FriendsRef, UsersRef,GroupMembersRef, GroupRef, RestaurantsRef, bestRestaurantRef;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calculatepopwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        int h = (int) ( height*0.6);
        int w = (int) ( width*0.8);

        getWindow().setLayout( w,h);

        groupID = getIntent().getExtras().get("group_id").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        noRestaurantsFound = (TextView) findViewById(R.id.no_restaurants_found);
        bestRestaurantForYou = (TextView) findViewById(R.id.bestrestauranttext);

        ourBestRestaurant = (RecyclerView) findViewById(R.id.calculated_best_restaurant);
        ourBestRestaurant.bringToFront();
        ourBestRestaurant.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Pop.this);
        ourBestRestaurant.setLayoutManager(linearLayoutManager);

        RestaurantsRef = FirebaseDatabase.getInstance().getReference().child("Restaurants");
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentUserID).child(groupID);
        GroupMembersRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentUserID).child(groupID).child("members");
        bestRestaurantRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentUserID).child(groupID).child("best restaurants");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        this.setFinishOnTouchOutside(true);

        CalculateBestRestaurant();

    }

    private void DisplayFavouriteRestaurant() {
        Query myRestaurants = bestRestaurantRef.orderByChild("name");

        FirebaseRecyclerAdapter<FindRestaurants, MainActivity.RestaurantListViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindRestaurants, MainActivity.RestaurantListViewHolder>
                (
                        FindRestaurants.class,
                        R.layout.all_restaurants_display_layout,
                        MainActivity.RestaurantListViewHolder.class,
                        myRestaurants
                )
        {

            @Override
            protected void populateViewHolder(final MainActivity.RestaurantListViewHolder viewHolder, final FindRestaurants find, final int i) {
                viewHolder.setName(find.getName());
                viewHolder.setLocation(find.getMall()+", "+find.getUnit());
                viewHolder.setRestaurantPicture(find.getImagelink());
                viewHolder.setType(find.getCuisineone()+", "+find.getCuisinetwo());
                viewHolder.setFavouriteButtonInvisible();

            }
        };

        ourBestRestaurant.setAdapter(firebaseRecyclerAdapter);
    }

    private void CalculateBestRestaurant() {


        GroupMembersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot xdataSnapshot) {

                    if(xdataSnapshot.exists()) {

                        CollateAllMembers((Map<String, Object>) xdataSnapshot.getValue());


                        RestaurantsRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                HashMap bestRestaurantMap = new HashMap();

                                bestRestaurantMap.put("asianorwestern",dataSnapshot.child("Restaurant " +favouriteRestaurant).child("asianorwestern").getValue() );
                                bestRestaurantMap.put("cuisineone",dataSnapshot.child("Restaurant " +favouriteRestaurant).child("cuisineone").getValue() );
                                bestRestaurantMap.put("cuisinetwo",dataSnapshot.child("Restaurant " +favouriteRestaurant).child("cuisinetwo").getValue() );
                                bestRestaurantMap.put("cuisinethree",dataSnapshot.child("Restaurant " +favouriteRestaurant).child("cuisinethree").getValue() );
                                bestRestaurantMap.put("id",dataSnapshot.child("Restaurant " +favouriteRestaurant).child("id").getValue() );
                                bestRestaurantMap.put("imagelink",dataSnapshot.child("Restaurant " +favouriteRestaurant).child("imagelink").getValue() );
                                bestRestaurantMap.put("unit",dataSnapshot.child("Restaurant " +favouriteRestaurant).child("unit").getValue() );
                                bestRestaurantMap.put("mall",dataSnapshot.child("Restaurant " +favouriteRestaurant).child("mall").getValue() );
                                bestRestaurantMap.put("name",dataSnapshot.child("Restaurant " +favouriteRestaurant).child("name").getValue() );

                                GroupRef.child("best restaurants").child("Restaurant " +favouriteRestaurant).updateChildren(bestRestaurantMap);

//                                bestRestaurantRef.child("Restaurant " + favouriteRestaurant)
//                                        .child("asianorwestern").setValue(dataSnapshot.child("asianorwestern").getValue());
//
//                                bestRestaurantRef.child("Restaurant " + favouriteRestaurant)
//                                        .child("cuisineone").setValue(dataSnapshot.child("cuisineone").getValue());

//                                bestRestaurantRef.child("Restaurant " + favouriteRestaurant)
//                                        .child("cuisinetwo").setValue(dataSnapshot.child("cuisinetwo").getValue());
//
//                                bestRestaurantRef.child("Restaurant " + favouriteRestaurant)
//                                        .child("cuisinethree").setValue(dataSnapshot.child("cuisinethree").getValue());
//
//                                bestRestaurantRef.child("Restaurant " + favouriteRestaurant)
//                                        .child("id").setValue(dataSnapshot.child("id").getValue());
//
//                                bestRestaurantRef.child("Restaurant " + favouriteRestaurant)
//                                        .child("imagelink").setValue(dataSnapshot.child("imagelink").getValue());
//
//                                bestRestaurantRef.child("Restaurant " + favouriteRestaurant)
//                                        .child("mall").setValue(dataSnapshot.child("mall").getValue());
//
//                                bestRestaurantRef.child("Restaurant " + favouriteRestaurant)
//                                        .child("name").setValue(dataSnapshot.child("name").getValue());
//
//                                bestRestaurantRef.child("Restaurant " + favouriteRestaurant)
//                                        .child("unit").setValue(dataSnapshot.child("unit").getValue());
                                noRestaurantsFound.setVisibility(View.GONE);

                                if(favouriteRestaurant.equals("No restaurants found")){

                                    noRestaurantsFound.setVisibility(View.VISIBLE);
                                    bestRestaurantForYou.setVisibility(View.GONE);

                                }else {
                                    bestRestaurantForYou.setVisibility(View.VISIBLE);
                                    DisplayFavouriteRestaurant();
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void CollateAllMembers(Map<String,Object> users) {

//        for(int i = 0 ; i<users.getChildrenCount();i++) {
//
//        }
        final HashMap<String, Integer> restaurantFavouriteCounter = new HashMap<>();

        for (Map.Entry<String, Object> entry : users.entrySet()) {

            final String userID = entry.getKey();
            UsersRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("favourite restaurants")){

                        for(DataSnapshot ds : dataSnapshot.child("favourite restaurants").getChildren()){

                            String id = ds.child("id").getValue().toString();
                            if(restaurantFavouriteCounter.containsKey(id)){
                                Integer count = (Integer) restaurantFavouriteCounter.get(id);
                                restaurantFavouriteCounter.put(id,(Integer) count+1);
                            }else{
                                restaurantFavouriteCounter.put(id,1);
                            }
                        }

                        Map.Entry<String, Integer> maxEntry = null;

                        for (Map.Entry<String, Integer> entry : restaurantFavouriteCounter.entrySet()) {
                            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                                maxEntry = entry;
                            }
                        }
                        if(maxEntry!=null) {
                            favouriteRestaurant = maxEntry.getKey();
                        }else{
                            favouriteRestaurant = "No restaurants found";
                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }


//        int max = Collections.max(restaurantFavouriteCounter.values());



    }

}
