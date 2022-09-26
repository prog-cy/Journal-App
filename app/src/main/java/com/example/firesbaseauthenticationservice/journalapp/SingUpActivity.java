package com.example.firesbaseauthenticationservice.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import util.JournalUser;

public class SingUpActivity extends AppCompatActivity {

    //Initializing widgets
    private Button createBTN;
    private AutoCompleteTextView emailETOfSingUp;
    private EditText passwordETOfSignUp;
    private EditText userName;

    //Firebase authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener stateListener;
    private FirebaseUser currentUser;

    //FireStore initialization
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Collections reference
    private final CollectionReference reference = db.collection("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        createBTN = findViewById(R.id.createAcBTN);
        emailETOfSingUp = findViewById(R.id.emailETOfSingUP);
        passwordETOfSignUp = findViewById(R.id.passwordETOfSingUp);
        userName = findViewById(R.id.userName);

        //Initializing firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null){

                    //User have account;
                }else{

                    //No account
                }
            }
        };

        createBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!emailETOfSingUp.getText().toString().isEmpty() &&
                !passwordETOfSignUp.getText().toString().isEmpty()){

                    String email = emailETOfSingUp.getText().toString().trim();
                    String password = passwordETOfSignUp.getText().toString().trim();
                    String user = userName.getText().toString().trim();

                    createUserAccount(email, password, user);
                }else{
                    Toast.makeText(SingUpActivity.this, "fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //This method will create a new document in the fire store and add the email and password in the document
    private void createUserAccount(String email, String password, String user) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            currentUser = firebaseAuth.getCurrentUser();
                            assert currentUser != null;
                            final String currentUserId = currentUser.getUid();

                            Map<String, Object> userObj = new HashMap<>();
                            userObj.put("userId", currentUserId);
                            userObj.put("userName", user);

                            //Adding data to fire store
                            reference.add(userObj)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.getResult().exists()){
                                                        String user_name = task.getResult().getString("userName");

                                                        //Getting user of Global journal user
                                                        JournalUser journalUser = JournalUser.getInstance();
                                                        journalUser.setUserId(currentUserId);
                                                        journalUser.setUserName(user_name);
                                                        //User Registered
                                                        //Go the AddJournalActivity
                                                        Intent i = new Intent(SingUpActivity.this, AddJournalActivity.class);

                                                        i.putExtra("username", user_name);
                                                        i.putExtra("userId", currentUserId);
                                                        startActivity(i);
                                                    }
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SingUpActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(stateListener);
    }
}