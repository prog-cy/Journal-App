package com.example.firesbaseauthenticationservice.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import util.JournalUser;

public class MainActivity extends AppCompatActivity {

    //Initializing widgets
    private Button singUp;
    private Button login;
    private AutoCompleteTextView emailET;
    private EditText passwordET;

    //Firebase Authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firebase fire store initialization
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Collections reference
    private final CollectionReference collectionReference = db.collection("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        singUp = findViewById(R.id.singUPBTN);
        login = findViewById(R.id.loginBTN);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);

        //SingUp
        singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SingUpActivity.class);
                startActivity(i);
            }
        });

        //Initializing the firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //Login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();

                loginEmailPasswordUser(email, password);
            }
        });
    }

    private void loginEmailPasswordUser(String email, String password) {

        //Checking that fields are empty or not
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            assert user != null;
                            final String currentUserID = user.getUid();

                            collectionReference
                                    .whereEqualTo("userId", currentUserID)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                            if(error != null){
                                                Toast.makeText(MainActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                                            }

                                            assert value != null;
                                            if(!value.isEmpty()){
                                                //Getting all the QuerySnapShots
                                                for(QueryDocumentSnapshot snapshot : value){
                                                    JournalUser journalUser = JournalUser.getInstance();
                                                    journalUser.setUserName(snapshot.getString("userName"));
                                                    journalUser.setUserId(snapshot.getString("userId"));
                                                    startActivity(new Intent(MainActivity.this, JournalListActivity.class));
                                                }
                                            }
                                        }
                                    });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show();
        }
    }
}