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

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView myFriendList;

    private Toolbar mToolbar;

    private DatabaseReference FriendsRef, UsersRef,LikesRef;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mToolbar = (Toolbar) findViewById(R.id.friends_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Friends");


        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        myFriendList = (RecyclerView) findViewById(R.id.friend_list);

        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();

    }

    private void DisplayAllFriends() {

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                        Friends.class,
                        R.layout.all_users_display_layout,
                        FriendsViewHolder.class,
                        FriendsRef
                ) {
                    @Override
                    protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, final int i) {

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

                                            Intent profileIntent = new Intent(FriendsActivity.this,PersonProfileActivity.class);
                                            profileIntent.putExtra("visit_user_id",visit_user_id);
                                            startActivity(profileIntent);
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
