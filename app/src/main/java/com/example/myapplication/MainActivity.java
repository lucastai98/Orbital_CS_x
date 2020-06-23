package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Sampler;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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

    private CircleImageView NavProfileImage;
    private TextView NavProfileUsername;

    private DatabaseReference UsersRef;

    String currentUserID;


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
                        Toast.makeText(MainActivity.this,"Profile name do not exist",Toast.LENGTH_SHORT).show();
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
            case R.id.nav_home:
                Toast.makeText(this,"Home",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_friends:
                SendUserToFriendsActivity();
                Toast.makeText(this,"Friend List",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_find_friends:
                SendUserToFindFriendsActivity();
                Toast.makeText(this,"Find Friends",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                SendUserToSettingsActivity();
                Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_find_restaurants:
                SendUserToRestaurantListActivity();
                Toast.makeText(this,"Find Restaurants",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }
    }

    private void SendUserToRestaurantListActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, RestaurantListActivity.class);
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
