package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;

    private RecyclerView myRestaurantList;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUsername;

    private DatabaseReference UsersRef, myRestaurantListRef, RestaurantsRef;

    String currentUserID;
    private DatabaseReference FriendsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar (mToolbar);
        getSupportActionBar().setTitle("No Apples Today");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle( MainActivity.this, drawerLayout, R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        myRestaurantList = (RecyclerView) findViewById(R.id.all_favourited_restaurants_list);
        myRestaurantList.bringToFront();
        myRestaurantList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);

        myRestaurantList.setLayoutManager(linearLayoutManager);
        myRestaurantListRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("favourite restaurants");
        RestaurantsRef = FirebaseDatabase.getInstance().getReference().child("Restaurants");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserID);

        DisplayAllRestaurants();

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUsername = (TextView) navView.findViewById(R.id.nav_user_full_name);

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("fullname")) {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        NavProfileUsername.setText(fullname);
                    }

                    if(dataSnapshot.hasChild("profileimage")) {

                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                    }else{

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelector(item);
                return false;
            }
        });
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }
    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null){
            SendUserToLoginActivity();
        }else{
            CheckUserExistence();
        }
    }

    private void CheckUserExistence(){
        final String current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(current_user_id)){
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();

    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static int save = -1;

    private void UserMenuSelector(MenuItem item){

        switch (item.getItemId()){
            case R.id.nav_profile:
                SendUserToProfileActivity();
                Toast.makeText(this,"Profile",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_friends:
                SendUserToFriendsActivity();
                Toast.makeText(this,"Friend List",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_find_friends:
                SendUserToFindFriendsActivity();
                Toast.makeText(this,"Find Friends",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_groups:
                SendUserToGroupsActivity();
                Toast.makeText(this,"Groups",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                SendUserToSettingsActivity();
                Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_cuisines:
                SendUserToCuisineActivity();
                Toast.makeText(this,"Cuisines",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_locations:
                SendUserToLocationsActivity();
                Toast.makeText(this,"Locations",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }
    }

    private void SendUserToCuisineActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, CuisinesListActivity.class);
        startActivity(settingsIntent);

    }
    private void SendUserToLocationsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, LocationsActivity.class);
        startActivity(settingsIntent);

    }

    private void DisplayAllRestaurants() {
        Query myRestaurants = myRestaurantListRef.orderByChild("name");

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

        myRestaurantList.setAdapter(firebaseRecyclerAdapter);
    }
//    public static class RestaurantListViewHolder extends RecyclerView.ViewHolder{
//        View mView;
//
//        public RestaurantListViewHolder(View itemView) {
//            super(itemView);
//            mView = itemView;
//        }
//
//        public void setRestaurantPicture(String url) {
//            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_restaurants_profile_image);
//            Picasso.get().load(url).placeholder(R.drawable.profile).into(myImage);
//        }
//        public void setName (String currentName){
//            TextView Name = (TextView) mView.findViewById(R.id.all_restaurants_name);
//            Name.setText(currentName);
//        }
//
//        public void setLocation (String currentLocation){
//            TextView location = (TextView) mView.findViewById(R.id.all_restaurants_location);
//            location.setText(currentLocation);
//        }
//        public void setType (String currentType){
//            TextView type = (TextView) mView.findViewById(R.id.all_restaurants_type);
//            type.setText(currentType);
//        }
//        public void setFavouriteButtonInvisible(){
//            ImageButton favouriteButton = (ImageButton) mView.findViewById(R.id.favourite_button);
//            favouriteButton.setVisibility(View.GONE);
//
//        }
//
//    }


    private void SendUserToRestaurantListActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, RestaurantListActivity.class);
        startActivity(settingsIntent);

    }
    private void SendUserToGroupsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, GroupsActivity.class);
        startActivity(settingsIntent);

    }

    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
    private void SendUserToProfileActivity() {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
    }
    private void SendUserToFindFriendsActivity() {
        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }
    private void SendUserToFriendsActivity() {
        Intent FriendsIntent = new Intent(MainActivity.this, FriendsActivity.class);
        startActivity(FriendsIntent);
    }

}
