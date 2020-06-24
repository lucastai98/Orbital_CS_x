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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

    private EditText SearchInputText,GroupNameInput;
    private int totalGroups;

    private DatabaseReference FriendsRef, UsersRef,GroupsRef;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_to_group);

        mToolbar = (Toolbar) findViewById(R.id.friends_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Friends");

        confirmButton = (Button) findViewById(R.id.confirm_group_button);
        GroupNameInput = (EditText) findViewById(R.id.edit_group_name);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        GroupsRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(online_user_id);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        myFriendList = (RecyclerView) findViewById(R.id.friend_list);

        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //i dont think you can do this actually
                        //cuz current group might not change while the total groups will change, unless its the creation not the update, maybe can separate the two
                        DatabaseReference currentGroup = GroupsRef.child("Group "+ (int) dataSnapshot.getChildrenCount());
                        HashMap groupMap = new HashMap<>();

                        groupMap.put("groupName", GroupNameInput);
                        currentGroup.updateChildren(groupMap);
                        currentGroup.child("Members");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void DisplayAllFriends() {

        FirebaseRecyclerAdapter<Friends, FriendsActivity.FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friends, FriendsActivity.FriendsViewHolder>(
                        Friends.class,
                        R.layout.all_users_display_layout,
                        FriendsActivity.FriendsViewHolder.class,
                        FriendsRef
                ) {
                    @Override
                    protected void populateViewHolder(final FriendsActivity.FriendsViewHolder friendsViewHolder, Friends friends, final int i) {

                        final String usersID = getRef(i).getKey();

                        UsersRef.child(usersID).addValueEventListener(new ValueEventListener() {
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

                                    friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String visit_user_id = getRef(i).getKey();


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
        myFriendList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(@NonNull View itemView) {
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
