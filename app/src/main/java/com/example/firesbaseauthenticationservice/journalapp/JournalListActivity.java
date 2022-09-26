package com.example.firesbaseauthenticationservice.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firesbaseauthenticationservice.journalapp.adapter.JournalAdapter;
import com.example.firesbaseauthenticationservice.journalapp.model.Journal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import util.JournalUser;

public class JournalListActivity extends AppCompatActivity {

    //Firebase Auth(authentication)
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener stateListener;
    private FirebaseUser user;

    //FireStore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Firebase Storage reference;
    private StorageReference storageReference;

    //FireStore Collection reference
    private final CollectionReference collectionReference = db.collection("Journal");

    //List of Journal
    private final ArrayList<Journal> journalList = new ArrayList<>();

    //Recycler View
    private RecyclerView recyclerView;

    //Adapter
    private JournalAdapter journalAdapter;

    //Widgets
    private TextView noPostTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        //Initializing the fireAuth
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //Initializing the widgets
        noPostTV = findViewById(R.id.no_post_TV);

        //Setting the recycler view
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    //Menu creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_add:
                //Go to AddJournalActivity
                if(user != null && firebaseAuth != null){
                    Intent intent = new Intent(JournalListActivity.this, AddJournalActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.action_signOut:
                //Signing Out and going to Login page again
                if(user != null && firebaseAuth != null){
                    firebaseAuth.signOut();
                    Intent intent = new Intent(JournalListActivity.this, MainActivity.class);
                    startActivity(intent);

                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //Getting all the user

    @Override
    protected void onStart() {
        super.onStart();

        JournalUser journalUser = JournalUser.getInstance();

        collectionReference.whereEqualTo("userId", journalUser.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //Each time clearing the data of ArrayList journalList when activity journalListActivity is opening.
                        if(journalList.size() > 0)
                            journalList.clear();

                        if(!queryDocumentSnapshots.isEmpty()){

                            for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                Journal journal = snapshot.toObject(Journal.class);
                                journalList.add(journal);
                            }

                            //Creating adapter instance
                            journalAdapter = new JournalAdapter(JournalListActivity.this, journalList);
                            recyclerView.setAdapter(journalAdapter);
                            journalAdapter.notifyDataSetChanged();
                        }else{
                            noPostTV.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JournalListActivity.this, "Oops! Something went wrong..." , Toast.LENGTH_SHORT).show();
                    }
                });
    }
}