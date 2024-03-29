package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Pop extends Activity {

    String favouriteRestaurant;

    String groupID;
    String location;
    String currentUserID;

    private FirebaseAuth mAuth;
    ArrayList<String> topThreeRestaurants;

    private RecyclerView ourBestRestaurant;
    private TextView noRestaurantsFound,bestRestaurantForYou;
    ImageButton clementiMallButton, starvistaButton, jemButton;
    Button anywhereButton;
    TextView whereText;

    private HashMap<String, Integer> restaurantFavouriteCounter;
    private HashMap<String, Integer> temp;

    private DatabaseReference FriendsRef, UsersRef,GroupMembersRef, GroupRef, RestaurantsRef, bestRestaurantRef,test;

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

//        location = getIntent().getExtras().get("location").toString();
        groupID = getIntent().getExtras().get("group_id").toString();

        clementiMallButton = (ImageButton) findViewById(R.id.clementi_mall);
        jemButton = (ImageButton) findViewById(R.id.jem_mall);
        starvistaButton = (ImageButton) findViewById(R.id.star_vista_mall);
        anywhereButton = (Button) findViewById(R.id.anywhere_button);
        whereText = (TextView) findViewById(R.id.where_are_you_text);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        noRestaurantsFound = (TextView) findViewById(R.id.no_restaurants_found);
        bestRestaurantForYou = (TextView) findViewById(R.id.bestrestauranttext);

        topThreeRestaurants = new ArrayList<>();
        restaurantFavouriteCounter = new HashMap<>();
        temp = new HashMap<>();

        ourBestRestaurant = (RecyclerView) findViewById(R.id.calculated_best_restaurant);
        ourBestRestaurant.bringToFront();
        ourBestRestaurant.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Pop.this);
        ourBestRestaurant.setLayoutManager(linearLayoutManager);

        RestaurantsRef = FirebaseDatabase.getInstance().getReference().child("Restaurants");
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentUserID).child(groupID);
        test = FirebaseDatabase.getInstance().getReference().child("test");
        GroupMembersRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentUserID).child(groupID).child("members");
        bestRestaurantRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentUserID).child(groupID).child("best restaurants");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        this.setFinishOnTouchOutside(true);

        noRestaurantsFound.setVisibility(View.GONE);
        bestRestaurantForYou.setVisibility(View.GONE);
        clementiMallButton.bringToFront();
        jemButton.bringToFront();
        starvistaButton.bringToFront();
        anywhereButton.bringToFront();


        clementiMallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clementiMallButton.setVisibility(View.GONE);
                starvistaButton.setVisibility(View.GONE);
                jemButton.setVisibility(View.GONE);
                anywhereButton.setVisibility(View.GONE);
                whereText.setVisibility(View.GONE);

                CalculateBestRestaurant("The Clementi Mall");

            }
        });
        starvistaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clementiMallButton.setVisibility(View.GONE);
                starvistaButton.setVisibility(View.GONE);
                jemButton.setVisibility(View.GONE);
                anywhereButton.setVisibility(View.GONE);
                whereText.setVisibility(View.GONE);

                CalculateBestRestaurant("The Star Vista");
            }
        });
        jemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clementiMallButton.setVisibility(View.GONE);
                starvistaButton.setVisibility(View.GONE);
                jemButton.setVisibility(View.GONE);
                anywhereButton.setVisibility(View.GONE);
                whereText.setVisibility(View.GONE);

                CalculateBestRestaurant("JEM");

            }
        });
        anywhereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clementiMallButton.setVisibility(View.GONE);
                starvistaButton.setVisibility(View.GONE);
                jemButton.setVisibility(View.GONE);
                anywhereButton.setVisibility(View.GONE);
                whereText.setVisibility(View.GONE);

                CalculateBestRestaurant("anywhere");

            }
        });
//        Toast.makeText(this,"Calculating...",Toast.LENGTH_SHORT).show();


    }

    private void DisplayFavouriteRestaurant() {
        Query myRestaurants = bestRestaurantRef.orderByChild("rank");

        FirebaseRecyclerAdapter<FindRestaurants, RestaurantListActivity.FindRestaurantsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindRestaurants, RestaurantListActivity.FindRestaurantsViewHolder>
                (
                        FindRestaurants.class,
                        R.layout.all_restaurants_display_layout,
                        RestaurantListActivity.FindRestaurantsViewHolder.class,
                        myRestaurants
                )
        {

            @Override
            protected void populateViewHolder(final RestaurantListActivity.FindRestaurantsViewHolder viewHolder, final FindRestaurants find, final int i) {
                viewHolder.setName(find.getName());
                viewHolder.setLocation1(find.getMall1() + " " + find.getUnit1());
                viewHolder.setLocation2(find.getMall2() + " " + find.getUnit2());
                viewHolder.setLocation3(find.getMall3() + " " + find.getUnit3());
                viewHolder.setRestaurantPicture(find.getImagelink());
                viewHolder.setType(find.getCuisineone()+", "+find.getCuisinetwo());
                viewHolder.setFavouriteButtonInvisible();

            }
        };

        ourBestRestaurant.setAdapter(firebaseRecyclerAdapter);
    }


    private void CalculateBestRestaurant(final String location) {


        GroupMembersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot xdataSnapshot) {

                    if(xdataSnapshot.exists()) {

                        CollateAllMembers((Map<String, Object>) xdataSnapshot.getValue(),location);

                        RestaurantsRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                CalculateHighestScore();



                                if(favouriteRestaurant==null){
                                    bestRestaurantRef.removeValue();
                                    bestRestaurantForYou.setVisibility(View.VISIBLE);
                                    for(int i = 0 ;i<topThreeRestaurants.size();i++) {
                                        HashMap bestRestaurantMap = new HashMap();
                                        if(topThreeRestaurants.get(i)!=null) {
                                            favouriteRestaurant = topThreeRestaurants.get(i);
                                            bestRestaurantMap.put("asianorwestern", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("asianorwestern").getValue());
                                            bestRestaurantMap.put("cuisineone", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("cuisineone").getValue());
                                            bestRestaurantMap.put("cuisinetwo", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("cuisinetwo").getValue());
                                            bestRestaurantMap.put("cuisinethree", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("cuisinethree").getValue());
                                            bestRestaurantMap.put("id", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("id").getValue());
                                            bestRestaurantMap.put("imagelink", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("imagelink").getValue());
                                            bestRestaurantMap.put("unit1", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("unit1").getValue());
                                            bestRestaurantMap.put("mall1", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("mall1").getValue());
                                            bestRestaurantMap.put("unit2", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("unit2").getValue());
                                            bestRestaurantMap.put("mall2", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("mall2").getValue());
                                            bestRestaurantMap.put("unit3", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("unit3").getValue());
                                            bestRestaurantMap.put("mall3", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("mall3").getValue());
                                            bestRestaurantMap.put("name", dataSnapshot.child("Restaurant " + favouriteRestaurant).child("name").getValue());
                                            bestRestaurantMap.put("rank",3-i );
                                            GroupRef.child("best restaurants").child("Restaurant " + favouriteRestaurant).updateChildren(bestRestaurantMap);
                                        }

                                    }

                                    bestRestaurantForYou.setVisibility(View.VISIBLE);
                                    noRestaurantsFound.setVisibility(View.GONE);

                                    DisplayFavouriteRestaurant();


                                }else {
                                    noRestaurantsFound.setVisibility(View.VISIBLE);
                                    bestRestaurantForYou.setVisibility(View.GONE);

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

    private void CollateAllMembers(Map<String,Object> users, final String mall) {

        for (Map.Entry<String, Object> entry : users.entrySet()) {

            final String userID = entry.getKey();
            UsersRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("favourite restaurants")){

                        for(DataSnapshot ds : dataSnapshot.child("favourite restaurants").getChildren()){
                            String mall1 ="";
                            String mall2 ="";
                            String mall3 = "";
                            if(ds.child("mall1").getValue()!=null){
                                mall1 = ds.child("mall1").getValue().toString();
                            }
                            if(ds.child("mall2").getValue()!=null){
                                mall2 = ds.child("mall2").getValue().toString();
                            }
                            if(ds.child("mall3").getValue()!=null){
                                mall3 = ds.child("mall3").getValue().toString();
                            }

                            if(
                                    mall1.equals(mall) ||
                                    mall2.equals(mall) ||
                                    mall3.equals(mall) ||
                                    mall.equals("anywhere")
                            ) {

                                String id = ds.child("id").getValue().toString();
                                if (restaurantFavouriteCounter.containsKey(id)) {
                                    Integer count = (Integer) restaurantFavouriteCounter.get(id);
                                    restaurantFavouriteCounter.put(id, (Integer) count + 2);
                                } else {
                                    restaurantFavouriteCounter.put(id, 2);
                                }
                            }

                        }

                    }
                    if(dataSnapshot.hasChild("favourite cuisines")){
                        for(final DataSnapshot ds : dataSnapshot.child("favourite cuisines").getChildren()){

                            final String cuisines = ds.getValue().toString();
                            for(final String key: restaurantFavouriteCounter.keySet()){
                                temp.clear();
                                temp.putAll(restaurantFavouriteCounter);
                                RestaurantsRef.child("Restaurant "+key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot2) {
                                        restaurantFavouriteCounter.putAll(temp);

                                        if(dataSnapshot2.child("cuisineone").getValue().equals(cuisines) ||
                                                dataSnapshot2.child("cuisinetwo").getValue().equals(cuisines)||
                                                dataSnapshot2.child("cuisinethree").getValue().equals(cuisines))
                                        {

                                            Integer count = (Integer) restaurantFavouriteCounter.get(key);

                                            restaurantFavouriteCounter.put(key,(Integer) count+1);

                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError2) {

                                    }
                                });
                            }
                        }

                    }

//                    if(maxEntry!=null) {
//                        favouriteRestaurant = maxEntry.getKey();
//                    }else{
//                        favouriteRestaurant = "No restaurants found";
//                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });


        }
//        int max = Collections.max(restaurantFavouriteCounter.values());


    }
    private void CalculateHighestScore() {
        Map.Entry<String, Integer> maxEntry = null;
        for(int i =0 ; i <3;i++) {

            for (Map.Entry<String, Integer> entry1 : restaurantFavouriteCounter.entrySet()) {
                if (maxEntry == null || entry1.getValue() > maxEntry.getValue()) {

                    maxEntry = entry1;
                }
            }
            if(maxEntry!=null) {
                topThreeRestaurants.add(maxEntry.getKey());
                restaurantFavouriteCounter.remove(maxEntry.getKey());
            }else if(i==0){
                favouriteRestaurant = "No restaurants found";
            }
            maxEntry=null;
        }
    }


}
