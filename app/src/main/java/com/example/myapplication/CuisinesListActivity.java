package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CuisinesListActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton SearchButton;
    private EditText SearchInputText;

    private RecyclerView SearchResultList;

    private DatabaseReference cuisinesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine_search);

        cuisinesRef = FirebaseDatabase.getInstance().getReference().child("Cuisines");

        mToolbar = (Toolbar) findViewById(R.id.find_cuisines_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Cuisines");

        SearchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));

        SearchButton = (ImageButton) findViewById(R.id.search_cuisines_button);
        SearchInputText = (EditText) findViewById(R.id.search_box_input);

//        SearchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String searchBoxInput = SearchInputText.getText().toString();
//
//                SearchCuisines(searchBoxInput);
//
//            }
//        });
//
//    }
//
//    private void SearchCuisines(String searchBoxInput) {
//
//        Toast.makeText(this,"Searching...",Toast.LENGTH_SHORT).show();
//
//        Query SearchCuisinesQuery = allUsersDatabaseRef.orderByChild("fullname").startAt(searchBoxInput).endAt(searchBoxInput+"\uf8ff");
//
//        FirebaseRecyclerAdapter<FindFriends,FindFriendsViewHolder> firebaseRecyclerAdapter
//                = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>
//                (
//                        FindFriends.class,
//                        R.layout.all_users_display_layout,
//                        FindFriendsViewHolder.class,
//                        SearchCuisinesQuery
//                )
//        {
//            @Override
//            protected void populateViewHolder(FindFriendsViewHolder findFriendsViewHolder, FindFriends findFriends, final int i) {
//                findFriendsViewHolder.setFullname(findFriends.getFullname());
//                findFriendsViewHolder.setUsername(findFriends.getUsername());
//                findFriendsViewHolder.setProfileimage(getApplicationContext(), findFriends.getProfileimage());
//
//                findFriendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String visit_user_id = getRef(i).getKey();
//
//                        Intent profileIntent = new Intent(CuisinesListActivity.this,PersonProfileActivity.class);
//                        profileIntent.putExtra("visit_user_id",visit_user_id);
//                        startActivity(profileIntent);
//                    }
//                });
//            }
//        };
//
//        SearchResultList.setAdapter(firebaseRecyclerAdapter);
//
//    }
//
//    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
//        View mView;
//
//        public FindFriendsViewHolder(View itemView) {
//            super(itemView);
//            mView = itemView;
//        }
//
//        public void setProfileimage(Context applicationContext, String profileimage) {
//            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
//            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(myImage);
//        }
//        public void setFullname (String fullname){
//            TextView myName = (TextView) mView.findViewById(R.id.all_users_fullname);
//            myName.setText(fullname);
//        }
//
//        public void setUsername (String username){
//            TextView myUsername = (TextView) mView.findViewById(R.id.all_users_username);
//            myUsername.setText("@"+username);
        }


    }
}

