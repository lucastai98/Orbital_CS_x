package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateNewGroupActivity extends AppCompatActivity {
    private EditText UserName, FullName;
    private Button SaveInformationButton;
    private CircleImageView ProfileImage;

    private ProgressDialog loadingBar;

    private String groupID;

    private ImageButton backButton;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,GroupsRef,GroupsRefList;
    private StorageReference GroupProfileImageRef;

    String currentUserID;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        GroupProfileImageRef = FirebaseStorage.getInstance().getReference().child("Group Images");

        loadingBar = new ProgressDialog(this);
        groupID = getIntent().getExtras().get("group_id").toString();

        GroupsRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentUserID).child(groupID);
        GroupsRefList = FirebaseDatabase.getInstance().getReference().child("GroupList").child(currentUserID).child(groupID);


        UserName = (EditText) findViewById(R.id.new_group_name);
        SaveInformationButton = (Button) findViewById(R.id.new_group_save_info_button);
        ProfileImage = (CircleImageView) findViewById( R.id.new_group_profile_image);

        SaveInformationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SaveAccountSetupInformation();
            }
        });

//        ProfileImage.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                Intent galleryIntent = new Intent();
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent, Gallery_Pick);
//            }
//        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("profileimage")) {
                        String image = dataSnapshot.child("profileimage").getValue().toString();

                        Picasso.get().load(image).placeholder(R.drawable.profile).into(ProfileImage);
                    }else{
                        Toast.makeText(CreateNewGroupActivity.this, "Please select a profile image",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        backButton = (ImageButton) findViewById(R.id.new_group_back_button);
        backButton.bringToFront();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GroupsRef.removeValue();

                Intent mainIntent = new Intent(CreateNewGroupActivity.this,GroupsActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });


    }

    // I DON'T REALLY UNDERSTAND THIS PART ! ! ! ! ! !
    // https://www.youtube.com/watch?v=Ef8DyErZhII&list=PLxefhmF0pcPnTQ2oyMffo6QbWtztXu1W_&index=14
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // some conditions for the picture
        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();
            // crop the image
            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        // Get the cropped image
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {       // store the cropped image into result
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Group Image");
                loadingBar.setMessage("Please wait, while we update your group image...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                final StorageReference filePath = GroupProfileImageRef.child(currentUserID+"space"+groupID + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                GroupsRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            //(These next 2 lines of code are not needed.
                                            // They actually make the app jump unnecessarily and it still functions without them.
                                            // In other words, you don't have to run this intent again.
                                            // After the pic posts, you are still on setupActivity.  Just go right to the Toast)

                                            Toast.makeText(CreateNewGroupActivity.this, "Image Stored", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                        else {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(CreateNewGroupActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });
                            }

                        });

                    }

                });
            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void SaveAccountSetupInformation() {
        String groupname = UserName.getText().toString();

        if(TextUtils.isEmpty(groupname)) {
            Toast.makeText(this, "Please fill in your group name...", Toast.LENGTH_SHORT).show();

        }else{
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait while we are creating your new group");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("groupName", groupname);
            GroupsRefList.updateChildren(userMap);
            GroupsRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        SendUserToAddFriendToGroupActivity();
                        Toast.makeText(CreateNewGroupActivity.this,"Your Group is created successfully.",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }else{
                        String message = task.getException().getMessage();
                        Toast.makeText(CreateNewGroupActivity.this, "Error occurred: "+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });

        }

    }

    private void SendUserToAddFriendToGroupActivity(){
        Intent mainIntent = new Intent(CreateNewGroupActivity.this,AddFriendToGroupActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mainIntent.putExtra("group_id",groupID);
        startActivity(mainIntent);
        finish();
    }
}
