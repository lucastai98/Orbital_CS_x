package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindAllRestaurantsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton SearchButton;
    private EditText SearchInputText;

    private RecyclerView SearchResultList;
    ImageButton favouriteButton;

    private String location;

    private boolean favouriteChecker;

    private String currentUserId;
    private DatabaseReference allRestaurantsDatabaseReference,currentUserRef,restaurantsByLocationRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_all_restaurants);
        mToolbar = (Toolbar) findViewById(R.id.restaurant_list_appbar_layout2);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Restaurants");

        allRestaurantsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Restaurants");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        SearchResultList = (RecyclerView) findViewById(R.id.search_restaurants_list2);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));

        SearchButton = (ImageButton) findViewById(R.id.search_restaurants_button2);
        SearchInputText = (EditText) findViewById(R.id.restaurant_search_box_input2);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = SearchInputText.getText().toString();

                SearchRestaurants(searchBoxInput);

            }
        });
    }

    private void SearchRestaurants(String searchBoxInput) {

        Toast.makeText(this,"Searching...",Toast.LENGTH_SHORT).show();

        Query searchRestaurantsQuery = allRestaurantsDatabaseReference
//                .orderByChild("mall").equalTo(location);
                .orderByChild("name").startAt(searchBoxInput).endAt(searchBoxInput+"\uf8ff");

        FirebaseRecyclerAdapter<FindRestaurants, RestaurantListActivity.FindRestaurantsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindRestaurants, RestaurantListActivity.FindRestaurantsViewHolder>
                (
                        FindRestaurants.class,
                        R.layout.all_restaurants_display_layout,
                        RestaurantListActivity.FindRestaurantsViewHolder.class,
                        searchRestaurantsQuery
                )
        {

            @Override
            protected void populateViewHolder(final RestaurantListActivity.FindRestaurantsViewHolder viewHolder, final FindRestaurants find, final int i) {

                allRestaurantsDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

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

                                favouriteChecker = true;

                                currentUserRef.child("favourite restaurants").child(Long.toString(find.getId()));

                                final DatabaseReference RestaurantRef = allRestaurantsDatabaseReference.child("Restaurant "+find.id).child("favourites");
                                final DatabaseReference RestaurantListRef = allRestaurantsDatabaseReference.child("Restaurant "+find.id);

                                RestaurantListRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (favouriteChecker) {
                                            if (dataSnapshot.child("favourites").hasChild(currentUserId)) {

                                                RestaurantRef.child(currentUserId).removeValue();
                                                favouriteChecker = false;
                                                currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id)).removeValue();

                                            } else {

                                                RestaurantRef.child(currentUserId).setValue(true);
                                                favouriteChecker = false;
                                                currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
                                                        .child("asianorwestern").setValue(dataSnapshot.child("asianorwestern").getValue());

                                                currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
                                                        .child("cuisineone").setValue(dataSnapshot.child("cuisineone").getValue());

                                                currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
                                                        .child("cuisinethree").setValue(dataSnapshot.child("cuisinethree").getValue());

                                                currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
                                                        .child("cuisinetwo").setValue(dataSnapshot.child("cuisinetwo").getValue());

                                                currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
                                                        .child("id").setValue(dataSnapshot.child("id").getValue());

                                                currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
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

                                                currentUserRef.child("favourite restaurants").child(String.valueOf("Restaurant " + find.id))
                                                        .child("name").setValue(dataSnapshot.child("name").getValue());

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                allRestaurantsDatabaseReference.child("Restaurant " + find.id).child("favourites").child(currentUserId).setValue(true);

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        };

        SearchResultList.setAdapter(firebaseRecyclerAdapter);

    }
    public static class FindRestaurantsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        ImageButton favouriteButton;

        DatabaseReference RestaurantRef;

        String currentUserId;

        public FindRestaurantsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            RestaurantRef = FirebaseDatabase.getInstance().getReference().child("Restaurants");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            favouriteButton = (ImageButton) mView.findViewById(R.id.favourite_button);
        }

        public void setRestaurantPicture(String url) {
            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_restaurants_profile_image);
            Picasso.get().load(url).placeholder(R.drawable.profile).into(myImage);
        }
        public void setName (String currentName){
            TextView Name = (TextView) mView.findViewById(R.id.all_restaurants_name);
            Name.setText(currentName);
        }

        public void setLocation1 (String currentLocation){
            TextView location = (TextView) mView.findViewById(R.id.all_restaurants_location_1);
            location.setText(currentLocation);
        }
        public void setLocation2 (String currentLocation2){
            TextView location = (TextView) mView.findViewById(R.id.all_restaurants_location_2);
            location.setText(currentLocation2);
        }
        public void setLocation3 (String currentLocation3){
            TextView location = (TextView) mView.findViewById(R.id.all_restaurants_location_3);
            location.setText(currentLocation3);
        }
        public void setType (String currentType){
            TextView type = (TextView) mView.findViewById(R.id.all_restaurants_type);
            type.setText(currentType);
        }


        public void setFavouriteButtonStatus(final String currentUserId, final String currentRestaurant) {

            RestaurantRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("Restaurant "+currentRestaurant).hasChild("favourites")){

                        if(dataSnapshot.child("Restaurant "+currentRestaurant).child("favourites").hasChild(currentUserId)){
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
        }
        public void setFavouriteButtonInvisible(){
            ImageButton favouriteButton = (ImageButton) mView.findViewById(R.id.favourite_button);
            favouriteButton.setVisibility(View.GONE);

        }

    }

}

