package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupProfile extends AppCompatActivity {


    private RecyclerView myFriendList;

    private Toolbar mToolbar;

    private DatabaseReference FriendGroupRef, UsersRef,FriendGroupListRef,GroupsRefList;
    private FirebaseAuth mAuth;
    private String online_user_id;

    private String groupID;

    private ImageButton addFriendButton,deleteGroupButton,backButton;
    private Button calculateButton;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        groupID = getIntent().getExtras().get("group_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendGroupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(online_user_id).child(groupID);
        GroupsRefList = FirebaseDatabase.getInstance().getReference().child("GroupList").child(online_user_id).child(groupID);

//        GroupsRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(online_user_id);
        FriendGroupListRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(online_user_id).child(groupID).child("members");

        mToolbar = (Toolbar) findViewById(R.id.group_profile_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        FriendGroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("groupName").exists()) {
                    getSupportActionBar().setTitle(dataSnapshot.child("groupName").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        calculateButton = (Button) findViewById(R.id.calculate_best_restaurant_button);
        addFriendButton = (ImageButton) findViewById(R.id.add_friend_to_group_button);
        deleteGroupButton = (ImageButton) findViewById(R.id.delete_group_button);
        backButton = (ImageButton) findViewById(R.id.group_profile_back_button);
        backButton.bringToFront();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(GroupProfile.this,GroupsActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });

        deleteGroupButton.bringToFront();

        deleteGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FriendGroupRef.removeValue();
                GroupsRefList.removeValue();

                Intent mainIntent = new Intent(GroupProfile.this,GroupsActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent popUpIntent = new Intent(GroupProfile.this,Pop.class);
                popUpIntent.putExtra("group_id",groupID);

                startActivity(popUpIntent);
            }
        });

        myFriendList = (RecyclerView) findViewById(R.id.friend_list);

        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addFriendIntent = new Intent(GroupProfile.this,AddFriendToGroupActivity.class);
                addFriendIntent.putExtra("group_id",groupID);
                startActivity(addFriendIntent);
                finish();
            }
        });
    }
    private void SendUserToAddFriendToGroupActivity() {

        Intent addFriendIntent = new Intent(GroupProfile.this,AddFriendToGroupActivity.class);
        startActivity(addFriendIntent);

    }


    private void DisplayAllFriends() {

        FirebaseRecyclerAdapter<String, FriendsActivity.FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<String, FriendsActivity.FriendsViewHolder>(
                        String.class,
                        R.layout.all_users_display_layout,
                        FriendsActivity.FriendsViewHolder.class,
                        FriendGroupListRef
                ) {
                    @Override
                    protected void populateViewHolder(final FriendsActivity.FriendsViewHolder friendsViewHolder, String friends, final int i) {

                        final String usersID = getRef(i).getKey();

                        UsersRef.child(usersID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    if (dataSnapshot.child("profileimage").getValue() == null) {

                                        final String profileImage = "drawable://" + R.drawable.profile;
                                        friendsViewHolder.setProfileimage(getApplicationContext(), profileImage);

                                    } else {

                                        final String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                                        friendsViewHolder.setProfileimage(getApplicationContext(), profileImage);

                                    }

                                    final String userName = dataSnapshot.child("fullname").getValue().toString();
                                    final String userUsername = dataSnapshot.child("username").getValue().toString();

                                    friendsViewHolder.setFullname(userName);
                                    friendsViewHolder.setUsername(userUsername);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                };
        myFriendList.setAdapter(firebaseRecyclerAdapter);
    }
}