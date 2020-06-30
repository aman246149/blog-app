package com.example.javafirstfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import java.util.List;

import model.Journal;
import ui.JournalRecylerAdapter;
import util.JournalApi;

public class JournalListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private List<Journal> journalList;
    private RecyclerView recyclerView;
    private JournalRecylerAdapter journalRecylerAdapter;
    private CollectionReference collectionReference=db.collection("Journal");
    private TextView noJOurnalEntry;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_add:
                    //Take user to add journal
                if (user!=null && firebaseAuth!=null)
                {
                    startActivity(new Intent(JournalListActivity.this,PostJournalActivity.class));
                    //finish();
                }
                break;

            case R.id.action_signout:
                //sign user out
                if(user!=null && firebaseAuth!=null)
                {
                    firebaseAuth.signOut();

                    SharedPreferences sharedPreferences=getSharedPreferences("Email",MODE_PRIVATE);
                    sharedPreferences.edit().remove("email").apply();
                    sharedPreferences.edit().remove("passward").apply();
                    startActivity(new Intent(JournalListActivity.this,MainActivity.class));

                    //finish();
                }
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty())
                {
                    for (QueryDocumentSnapshot journals:queryDocumentSnapshots)
                    {
                        Journal journal=journals.toObject(Journal.class);
                        journalList.add(journal);

                    }
                    journalRecylerAdapter=new JournalRecylerAdapter(JournalListActivity.this,journalList);
                    recyclerView.setAdapter(journalRecylerAdapter);
                    journalRecylerAdapter.notifyDataSetChanged();
                }
                else{
                    noJOurnalEntry.setVisibility(View.VISIBLE);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();

        noJOurnalEntry=findViewById(R.id.list_no_thoughts);

        journalList=new ArrayList<>();

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




    }
}