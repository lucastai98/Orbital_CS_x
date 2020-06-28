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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsActivity extends AppCompatActivity {

    private RecyclerView myGroupList;

    private Toolbar mToolbar;

    private ImageButton newGroupButton,backButton;

    private DatabaseReference GroupsRef, UsersRef,GroupsRefList;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        mToolbar = (Toolbar) findViewById(R.id.all_groups_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Groups");

        newGroupButton = (ImageButton) findViewById(R.id.new_group_button);
        newGroupButton.bringToFront();
        backButton = (ImageButton) findViewById(R.id.groups_back_button);
        backButton.bringToFront();

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        GroupsRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(online_user_id);
        GroupsRefList = FirebaseDatabase.getInstance().getReference().child("GroupList").child(online_user_id);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        myGroupList = (RecyclerView) findViewById(R.id.group_list);

        myGroupList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myGroupList.setLayoutManager(linearLayoutManager);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(GroupsActivity.this,MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });
        DisplayAllGroups();

        newGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long temp = dataSnapshot.getChildrenCount()+1;
                        String group_id = Long.toString(temp);

                        while(dataSnapshot.hasChild(group_id)){
                            group_id = group_id+1;
                        }

                        FirebaseDatabase.getInstance().getReference().child("Groups").child(online_user_id).child(group_id);
                        FirebaseDatabase.getInstance().getReference().child("GroupList").child(online_user_id).child(group_id);
                        FirebaseDatabase.getInstance().getReference().child("Groups").child(online_user_id).child(group_id).child("best restaurants");

                        Intent profileIntent = new Intent(GroupsActivity.this,CreateNewGroupActivity.class);
                        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        profileIntent.putExtra("group_id",group_id);
                        startActivity(profileIntent);
                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


    }
    private void SendUserToAddFriendToGroupActivity() {
        Intent addFriendsIntent = new Intent(GroupsActivity.this, AddFriendToGroupActivity.class);
        startActivity(addFriendsIntent);

    }


    /*
     (NOT DONE)

     */

    private void DisplayAllGroups() {

        FirebaseRecyclerAdapter<Groups, GroupsActivity.GroupsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Groups, GroupsActivity.GroupsViewHolder>(
                        Groups.class,
                        R.layout.all_users_display_layout,
                        GroupsActivity.GroupsViewHolder.class,
                        GroupsRefList
                ) {
                    @Override
                    protected void populateViewHolder(final GroupsViewHolder GroupsViewHolder, Groups groups, final int i) {

                        final String groupID = getRef(i).getKey();

                        GroupsRefList.child(groupID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

//                                    if(dataSnapshot.child("profileimage").getValue()==null) {
//
//                                        final String profileImage = "drawable://" + R.drawable.profile;
//                                        GroupsViewHolder.setProfileimage(getApplicationContext(),profileImage);
//
//                                    }else {
//
//                                        final String profileImage = dataSnapshot.child("profileimage").getValue().toString();
//                                        GroupsViewHolder.setProfileimage(getApplicationContext(),profileImage);
//
//                                    }

//
//                                    final String groupName = dataSnapshot.child("groupName").getValue().toString();
                                    // add this back in later ^
                                    if(dataSnapshot.hasChild("groupName")) {
                                        final String groupName = dataSnapshot.child("groupName").getValue().toString();
                                        GroupsViewHolder.setFullname(groupName);

                                    }else{
                                        final String groupName = "";
                                        GroupsViewHolder.setFullname(groupName);
                                    }
                                    final String userUsername = "";

                                    GroupsViewHolder.setUsername(userUsername);

                                    GroupsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            String group_id = getRef(i).getKey();

                                            Intent profileIntent = new Intent(GroupsActivity.this,GroupProfile.class);
                                            profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                            profileIntent.putExtra("group_id",group_id);
                                            startActivity(profileIntent);
                                            finish();

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                };
        myGroupList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class GroupsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public GroupsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setProfileimage(Context applicationContext, String profileimage) {
            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }
        public void setFullname (String fullname){
            TextView myName = (TextView) mView.findViewById(R.id.all_users_fullname);
            myName.setText(fullname);
        }
        public void setUsername (String username){
            TextView myUsername = (TextView) mView.findViewById(R.id.all_users_username);
            myUsername.setText("@"+username);
        }


    }

}
