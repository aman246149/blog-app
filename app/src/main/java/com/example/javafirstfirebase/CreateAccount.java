package com.example.javafirstfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.util.Map;
import java.util.Objects;

import util.JournalApi;

public class CreateAccount extends AppCompatActivity {



    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentuser;



    //Firestore connection

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("users");
    private EditText emailEditText;
    private  EditText passwordEditText;
    private ProgressBar progressBar;
    private Button createAccountBtn;
    private EditText userNAmeEditText;


    @Override
    protected void onStart() {
        super.onStart();

        currentuser=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth=FirebaseAuth.getInstance();

        createAccountBtn=findViewById(R.id.create_acct_button);
        emailEditText=findViewById(R.id.email_account);
        passwordEditText=findViewById(R.id.password_account);
        progressBar=findViewById(R.id.create_acct_progress);
        userNAmeEditText=findViewById(R.id.username_account);


        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentuser=firebaseAuth.getCurrentUser();

                if (currentuser!=null)
                {
                    //user is alreaddy loged in
                }
                else
                {
                    //no user yet..
                }
            }
        };



        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(emailEditText.getText().toString())
                && !TextUtils.isEmpty(passwordEditText.getText().toString())
                && !TextUtils.isEmpty(userNAmeEditText.getText().toString())
                )
                {
                    String email=emailEditText.getText().toString().trim();
                    String passward=passwordEditText.getText().toString().trim();
                    String username=userNAmeEditText.getText().toString().trim();
                    createUserEmailAccount(email,passward,username);


                    //used shared preferences

                    SharedPreferences sharedPreferences=getSharedPreferences("Email",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();

                    editor.putString("email",email);
                    editor.putString("passward",passward);
                    editor.apply();

                }
                else{
                    Toast.makeText(CreateAccount.this,"EMPTY FIELD NOT ALLOWED",Toast.LENGTH_LONG).show();
                }

            }
        });



    }

    private  void createUserEmailAccount(String email, String passward, final String username)
    {
        if(!TextUtils.isEmpty(email) &&!TextUtils.isEmpty(passward) && !TextUtils.isEmpty(username))
        {
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,passward)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                //we take user to Add JOurnalAcctivity
                                currentuser=firebaseAuth.getCurrentUser();
                                final String currentuserID=currentuser.getUid();

                                //CReate a user map to add it in user collection

                                Map<String,String> userObj=new HashMap<>();
                                userObj.put("userId",currentuserID);
                                userObj.put("username",username);

                                //save to our firestore database
                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                        if (Objects.requireNonNull(task.getResult()).exists())
                                                        {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            String name=task.getResult().getString("username");

                                                            JournalApi journalApi=JournalApi.getInstance();
                                                            journalApi.setUserId(currentuserID);
                                                            journalApi.setUsername(name);

                                                            Intent intent=new Intent(CreateAccount.this,PostJournalActivity.class);
                                                            intent.putExtra("username",name);
                                                            intent.putExtra("userId",currentuserID);
                                                            startActivity(intent);

                                                        }else {

                                                            progressBar.setVisibility(View.INVISIBLE);

                                                        }

                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });


                            }else{
                                //something went wrong
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        else{

        }
    }
}