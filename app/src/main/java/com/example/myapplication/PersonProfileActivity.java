package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userUsername, userFullName;
    private CircleImageView userProfImage;
    private Button SendFriendReqBtn, DeclineFriendReqBtn;

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference FriendRequestRef, UsersRef, FriendsRef;
    private String senderUserId, receiverUserId, CURRENT_STATE;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mToolbar = (Toolbar) findViewById(R.id.person_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        senderUserId = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverUserId);
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        InitializeFields();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myFullName = dataSnapshot.child("fullname").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);

                    userUsername.setText("@"+myUserName);
                    userFullName.setText(myFullName);

                    MaintenanceOfButtons();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DeclineFriendReqBtn.setVisibility(View.INVISIBLE);
        DeclineFriendReqBtn.setEnabled(false);

        if(!senderUserId.equals(receiverUserId)){
            SendFriendReqBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendFriendReqBtn.setEnabled(false);
                    if(CURRENT_STATE.equals("not_friends")){

                        SendFriendRequestToPerson();
                    }
                    if(CURRENT_STATE.equals("request_sent")){

                        CancelFriendRequest();
                    }
                    if(CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }
                    if(CURRENT_STATE.equals("friends")){
                        UnfriendExistingFriend();
                    }
                }
            });
        }else{
            DeclineFriendReqBtn.setVisibility(View.INVISIBLE);
            SendFriendReqBtn.setVisibility(View.INVISIBLE);

        }


    }

    private void UnfriendExistingFriend() {
        FriendsRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendReqBtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                SendFriendReqBtn.setText("Send Friend Request");

                                                DeclineFriendReqBtn.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void AcceptFriendRequest() {

        FriendsRef.child(senderUserId).child(receiverUserId).child("relationship").setValue("Friends")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendsRef.child(receiverUserId).child(senderUserId).child("relationship").setValue("Friends")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FriendRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    FriendRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        SendFriendReqBtn.setEnabled(true);
                                                                                        CURRENT_STATE = "friends";
                                                                                        SendFriendReqBtn.setText("Unfriend this Person");

                                                                                        DeclineFriendReqBtn.setVisibility(View.INVISIBLE);
                                                                                        DeclineFriendReqBtn.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                        }
                    }
                });

    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendReqBtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                SendFriendReqBtn.setText("Send Friend Request");

                                                DeclineFriendReqBtn.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void MaintenanceOfButtons() {
        FriendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserId)){
                            String request_type = dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();

                            if(request_type.equals("sent")){
                                CURRENT_STATE = "request_sent";
                                SendFriendReqBtn.setText("Cancel Friend Request");

                                DeclineFriendReqBtn.setVisibility(View.INVISIBLE);
                                DeclineFriendReqBtn.setEnabled(false);
                            }else if(request_type.equals("received")){
                                CURRENT_STATE="request_received";
                                SendFriendReqBtn.setText("Accept Friend Request");

                                DeclineFriendReqBtn.setVisibility(View.VISIBLE);
                                DeclineFriendReqBtn.setEnabled(true);
                            }
                        }else{
                            FriendsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiverUserId)){
                                                CURRENT_STATE = "friends";
                                                SendFriendReqBtn.setText("Unfriend this Person");

                                                DeclineFriendReqBtn.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqBtn.setEnabled(false);
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

    private void SendFriendRequestToPerson() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendFriendReqBtn.setEnabled(true);
                                                CURRENT_STATE = "request_sent";
                                                SendFriendReqBtn.setText("Cancel Friend Request");

                                                DeclineFriendReqBtn.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void InitializeFields() {

        userUsername = (TextView) findViewById(R.id.person_username);
        userFullName = (TextView) findViewById(R.id.person_full_name);
        userProfImage = (CircleImageView) findViewById(R.id.person_profile_pic);

        SendFriendReqBtn = (Button) findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendReqBtn = (Button) findViewById(R.id.person_decline_friend_request);

        CURRENT_STATE = "not_friends";


    }
}
