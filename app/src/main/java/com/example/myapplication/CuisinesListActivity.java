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
import android.renderscript.Sampler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CuisinesListActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView CuisineList;

    private DatabaseReference CuisinesRef;

    private String cuisineName;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine_search);

        CuisinesRef = FirebaseDatabase.getInstance().getReference().child("Cuisines").child("Cuisines");

        mToolbar = (Toolbar) findViewById(R.id.find_cuisines_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Cuisines");

        CuisineList = (RecyclerView) findViewById(R.id.Cuisine_list);
        CuisineList.setHasFixedSize(true);
        CuisineList.setLayoutManager(new LinearLayoutManager(this));
        backButton = (ImageButton) findViewById(R.id.cuisine_list_back_button);
        backButton.bringToFront();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(CuisinesListActivity.this,MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });


        SearchCuisines();
//
    }
//
    private void SearchCuisines() {

        Query SearchCuisinesQuery = CuisinesRef.orderByChild("name");

        FirebaseRecyclerAdapter<Cuisine,CuisineListViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Cuisine, CuisineListViewHolder>
                (
                        Cuisine.class,
                        R.layout.all_cuisines_display_layout,
                        CuisineListViewHolder.class,
                        SearchCuisinesQuery
                )
        {
            @Override
            protected void populateViewHolder(CuisineListViewHolder cuisineListViewHolder, final Cuisine cuisine, final int i) {
                cuisineListViewHolder.setCuisineName(cuisine.getName());

                cuisineListViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cuisineName = cuisine.getName();

                        Intent profileIntent = new Intent(CuisinesListActivity.this,ListOfRestaurantInCuisine.class);
                        profileIntent.putExtra("cuisineName",cuisineName);
                        startActivity(profileIntent);
                        finish();

                    }
                });
            }
        };

        CuisineList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class CuisineListViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public CuisineListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCuisineName (String fullname){
            TextView myName = (TextView) mView.findViewById(R.id.cuisine_name);
            myName.setText(fullname);
        }

    }
}

