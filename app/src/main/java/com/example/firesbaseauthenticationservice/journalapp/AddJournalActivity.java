package com.example.firesbaseauthenticationservice.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firesbaseauthenticationservice.journalapp.model.Journal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import util.JournalUser;

public class AddJournalActivity extends AppCompatActivity {

    //Gallery Code
    private static final int GALLERY_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    //Widgets
    private Button saveBTN;
    private ProgressBar progressBar;
    private EditText postTitleET;
    private EditText thoughtsET;
    private TextView postUserNameTV;
    private TextView dateTV;
    private ImageView photo;
    private Bitmap imgBitMap;


    //UserId and UserName
    private String currentUserID;
    private String currentUserName;

    //Firebase auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Fire store
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    //Collection
    private final CollectionReference collectionReference = db.collection("Journal");
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        //Initializing the references
        storageReference = FirebaseStorage.getInstance().getReference();

        //Firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Initializing widgets;
        saveBTN = findViewById(R.id.save_post);
        progressBar = findViewById(R.id.post_ProgressBar);
        postTitleET = findViewById(R.id.post_title_ET);
        thoughtsET = findViewById(R.id.post_description_ET);
        postUserNameTV = findViewById(R.id.post_user_name);
        dateTV = findViewById(R.id.post_date);
        photo = findViewById(R.id.post_ImageView);

        //When activity start progress base should be invisible
        progressBar.setVisibility(View.INVISIBLE);

        //Now getting the userName and userId;
        if(JournalUser.getInstance() != null){
            currentUserName = JournalUser.getInstance().getUserName();
            currentUserID = JournalUser.getInstance().getUserId();
            postUserNameTV.setText(currentUserName);
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            dateTV.setText(date);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if(user != null){

                }else{

                }
            }
        };

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJournal();
            }
        });
    }

    private void saveJournal() {
        String title = postTitleET.getText().toString().trim();
        String thoughts = thoughtsET.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts)
                                    &&imageUri != null){

            //Saving path of images in firebase storage
            final StorageReference filepath = storageReference
                    .child("journal_images")
                    .child("my_image_"+ Timestamp.now().getSeconds());

            //Uploading the images
            filepath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    //Object of Journal
                                    Journal journal = new Journal();
                                    journal.setTitle(title);
                                    journal.setThoughts(thoughts);
                                    journal.setImageUrl(imageUrl);
                                    journal.setUserName(currentUserName);
                                    journal.setUserId(currentUserID);
                                    journal.setTimeAdded(new Timestamp(new Date()));

                                    //Invocation of collectionReference
                                    collectionReference.add(journal)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Intent intent = new Intent(AddJournalActivity.this, JournalListActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(AddJournalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    //Menu Creation

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_journal_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Options menu

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.camera_BTN:
                if(checkPermission()){
                    captureImage();
                }else{
                    requestPermission();
                }
                break;
            case R.id.select_image:
                Intent galleryIntent  = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //These methods are for taking image using camera
    private void requestPermission() {

        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA
        }, PERMISSION_CODE);

    }

    private boolean checkPermission() {

        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA_SERVICE);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void captureImage() {

        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (cameraPermission) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                captureImage();
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            assert data != null;
            imageUri = data.getData(); //Getting actual image path
            photo.setImageURI(imageUri);
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extrass = data.getExtras();
            imgBitMap = (Bitmap) extrass.get("data");
            photo.setImageBitmap(imgBitMap);
            imageUri = getImageUri(AddJournalActivity.this, imgBitMap);
        }

    }

    //This will convert imageBitmap into Uri.
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}