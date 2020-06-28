package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendToGroupActivity extends AppCompatActivity {

    private RecyclerView myFriendList;

    private Toolbar mToolbar;

    private Button confirmButton;

    private String groupID;

    private EditText SearchInputText,GroupNameInput;
    private int totalGroups;

    private DatabaseReference FriendsRef, UsersRef,GroupsRef,GroupsRefList;
    private FirebaseAuth mAuth;
    private String online_user_id;
    private ImageButton backButton;

    private boolean inGroupChecker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_to_group);

        mToolbar = (Toolbar) findViewById(R.id.add_friends_to_group_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Friends");

        confirmButton = (Button) findViewById(R.id.confirm_group_button);
        GroupNameInput = (EditText) findViewById(R.id.edit_group_name);
        backButton = (ImageButton) findViewById(R.id.add_friend_back_button);
        backButton.bringToFront();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(AddFriendToGroupActivity.this,GroupsActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        groupID = getIntent().getExtras().get("group_id").toString();
        GroupsRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(online_user_id).child(groupID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupsRefList = FirebaseDatabase.getInstance().getReference().child("GroupList").child(online_user_id).child(groupID);

        getSupportActionBar().setTitle("Friends");

        myFriendList = (RecyclerView) findViewById(R.id.add_friend_to_group_list);

        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);


        GroupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("groupName")) {
                    GroupNameInput.setText(dataSnapshot.child("groupName").getValue().toString());
                }else{
                    GroupNameInput.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap groupMap = new HashMap<>();
                String groupName = GroupNameInput.getText().toString();
                groupMap.put("groupName", groupName);
                GroupsRefList.updateChildren(groupMap);
                GroupsRef.updateChildren(groupMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Intent profileIntent = new Intent(AddFriendToGroupActivity.this,GroupProfile.class);
                        profileIntent.putExtra("group_id",groupID);
                        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(profileIntent);
                        finish();
                    }
                });

            }
        });
        DisplayAllFriends();


    }

    private void DisplayAllFriends() {

        FirebaseRecyclerAdapter<Friends, AddFriendToGroupActivity.FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friends, AddFriendToGroupActivity.FriendsViewHolder>(
                        Friends.class,
                        R.layout.all_users_display_layout,
                        AddFriendToGroupActivity.FriendsViewHolder.class,
                        FriendsRef
                ) {
                    @Override
                    protected void populateViewHolder(final AddFriendToGroupActivity.FriendsViewHolder friendsViewHolder, Friends friends, final int i) {

                        final String friend_id = getRef(i).getKey();
                        friendsViewHolder.setFriendStatus(online_user_id,friend_id,groupID);


                        UsersRef.child(friend_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    if(dataSnapshot.child("profileimage").getValue()==null) {

                                        final String profileImage = "drawable://" + R.drawable.profile;
                                        friendsViewHolder.setProfileimage(getApplicationContext(),profileImage);

                                    }else {

                                        final String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                                        friendsViewHolder.setProfileimage(getApplicationContext(),profileImage);

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
                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                inGroupChecker = true;
                                GroupsRef.child("members").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(inGroupChecker) {
                                            if (dataSnapshot.hasChild(friend_id)) {

                                                GroupsRef.child("members").child(friend_id).removeValue();
                                                inGroupChecker = false;

                                            } else {

                                                GroupsRef.child("members").child(friend_id).setValue("In group");
                                                inGroupChecker = false;

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        });


                    }
                };
        myFriendList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        DatabaseReference GroupsRef;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            GroupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");

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
        public void setFriendStatus(final String currentUserId, final String currentFriend, final String groupID) {
            final ImageView inGroupChecker = (ImageView) mView.findViewById(R.id.in_group_checker);

            GroupsRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(groupID).child("members").hasChild(currentFriend)){
                            inGroupChecker.setImageResource(R.drawable.redcross);
                        }else{
                            inGroupChecker.setImageResource(R.drawable.inputs);
                        }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

}
