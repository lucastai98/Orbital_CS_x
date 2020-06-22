package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.WorkSource;
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

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class RestaurantListActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton SearchButton;
    private EditText SearchInputText;

    private RecyclerView SearchResultList;

    private DatabaseReference allRestaurantsDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        mToolbar = (Toolbar) findViewById(R.id.restaurant_list_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Restaurants");

        allRestaurantsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Restaurants");


        SearchResultList = (RecyclerView) findViewById(R.id.search_restaurants_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));

        SearchButton = (ImageButton) findViewById(R.id.search_restaurants_button);
        SearchInputText = (EditText) findViewById(R.id.restaurant_search_box_input);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = SearchInputText.getText().toString();

                SearchRestaurants(searchBoxInput);

            }
        });
    }

//    public void order(View v){
//        try{
//
//            AssetManager AM = getAssets();
//            InputStream IS = AM.open("Cuisines and Restaurants.xlsx");
//            Workbook WB = Workbook.getWorkbook(IS);
//            Sheet S = WB.getSheet(0);
//            int rows = S.getRows();
//            int cols = S.getColumns();
//
//            String info = "";
//
//            for (int r=0;r<rows;r++){
//                for (int c=0;c<cols;c++){
//                    Cell cell = S.getCell(c,r);
//                    info = cell.getContents();
//                }
//            }
//
//
//        }catch(Exception e){
//
//        }
//    }

    private void SearchRestaurants(String searchBoxInput) {
        Toast.makeText(this,"Searching...",Toast.LENGTH_SHORT).show();

        Query searchRestaurantsQuery = allRestaurantsDatabaseReference.orderByChild("name:").startAt(searchBoxInput).endAt(searchBoxInput+"\uf8ff");

        FirebaseRecyclerAdapter<FindRestaurants, FindRestaurantsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindRestaurants, FindRestaurantsViewHolder>
                (
                        FindRestaurants.class,
                        R.layout.all_restaurants_display_layout,
                        RestaurantListActivity.FindRestaurantsViewHolder.class,
                        searchRestaurantsQuery
                )
        {

            @Override
            protected void populateViewHolder(RestaurantListActivity.FindRestaurantsViewHolder viewHolder, FindRestaurants find, final int i) {
                viewHolder.setName(find.getName());
                viewHolder.setLocation(find.getLocation());
                viewHolder.setRestaurantPicture(getApplicationContext(), find.getPicture());
                viewHolder.setType(find.getType());

//                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String visit_user_id = getRef(i).getKey();
//
//                        Intent profileIntent = new Intent(FindFriendsActivity.this,PersonProfileActivity.class);
//                        profileIntent.putExtra("visit_user_id",visit_user_id);
//                        startActivity(profileIntent);
//                    }
//                });
            }
        };

        SearchResultList.setAdapter(firebaseRecyclerAdapter);

    }
    public static class FindRestaurantsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FindRestaurantsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setRestaurantPicture(Context applicationContext, String profileimage) {
            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_restaurants_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }
        public void setName (String currentName){
            TextView Name = (TextView) mView.findViewById(R.id.all_restaurants_name);
            Name.setText(currentName);
        }

        public void setLocation (String currentLocation){
            TextView location = (TextView) mView.findViewById(R.id.all_restaurants_location);
            location.setText(currentLocation);
        }
        public void setType (String currentType){
            TextView type = (TextView) mView.findViewById(R.id.all_restaurants_type);
            type.setText(currentType);
        }


    }

}
