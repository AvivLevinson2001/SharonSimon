package com.example.sharonsimon.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.sharonsimon.Classes.Highlight;
import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Classes.Task;
import com.example.sharonsimon.Dialogs.LoadingDialogBuilder;
import com.example.sharonsimon.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences sp;
    final int MIM_SPLASH_TIME_MILLISECONDS = 1000;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    Ken myKen;
    ArrayList<Ken> kensList;
    ArrayList <Task> allTasks;
    ArrayList<Highlight> highlights;

    boolean timePassed = false, infoLoaded = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        sp = getSharedPreferences("user", MODE_PRIVATE);

        if(isLoggedIn()){
            getInfoFromFirebase();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    timePassed = true;
                    openMainActivity();
                }
            },MIM_SPLASH_TIME_MILLISECONDS);
        }
        else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    timePassed = true;
                    openLogInActivity();
                }
            },MIM_SPLASH_TIME_MILLISECONDS);
        }
    }

    private boolean isLoggedIn(){
        if(sp == null) return false;
        return sp.getBoolean("isLoggedIn", false);
    }

    public void getInfoFromFirebase()
    {
        final String myKenName = sp.getString("ken","");
        databaseReference.child("kens").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    final String[] kensNames = new String[]
                            {"מקורות", "המעפיל", "מעיין", "העוגן", "רמות חפר", "יקום", "געש", "גן שמואל",
                                    "להבות חביבה", "מבואות עירון", "כרכור", "כפס מרום", "הרצליה", "חריש"};

                    for(final String kenName : kensNames){
                        final Ken newKen = new Ken(kenName, new ArrayList<Task>(), 0);
                        if(kenName.equals(myKenName))
                            myKen = newKen;
                        kensList = new ArrayList<>();
                        storageReference.child("animals_images/").child(newKen.getName() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if(uri != null)
                                    newKen.setAnimalImageUrl(uri.toString());
                                kensList.add(newKen);
                                if(kensList.size() == kensNames.length){
                                    databaseReference.child("kens").setValue(kensList);
                                    databaseReference.child("admin-password").setValue("barvaz15");
                                    allTasks = new ArrayList<>();
                                    highlights = new ArrayList<>();
                                    infoLoaded = true;
                                    openMainActivity();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                kensList.add(newKen);
                                if(kensList.size() == kensNames.length){
                                    databaseReference.child("kens").setValue(kensList);
                                    databaseReference.child("admin-password").setValue("barvaz15");
                                    allTasks = new ArrayList<>();
                                    highlights = new ArrayList<>();
                                    infoLoaded = true;
                                    openMainActivity();
                                }
                            }
                        });
                    }
                }
                else{
                    GenericTypeIndicator<ArrayList<Ken>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Ken>>() {};
                    kensList = dataSnapshot.getValue(genericTypeIndicator);
                    for(Ken ken : kensList){
                        if(ken.getName().equals(myKenName)){
                            myKen = ken;
                        }
                    }
                    databaseReference.child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                GenericTypeIndicator<ArrayList<Task>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Task>>() {};
                                allTasks = dataSnapshot.getValue(genericTypeIndicator);
                            }
                            else{
                                allTasks = new ArrayList<>();
                            }
                            databaseReference.child("highlights").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        GenericTypeIndicator<ArrayList<Highlight>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Highlight>>() {};
                                        highlights = dataSnapshot.getValue(genericTypeIndicator);
                                    }
                                    else{
                                        highlights = new ArrayList<>();
                                    }
                                    infoLoaded = true;
                                    openMainActivity();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    if(!isNetworkAvailable())
                                    {
                                        Snackbar.make(findViewById(android.R.id.content),"אין גישה לאינטרנט, בדוק את החיבור ונסה שנית.", BaseTransientBottomBar.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            if(!isNetworkAvailable())
                            {
                                Snackbar.make(findViewById(android.R.id.content),"אין גישה לאינטרנט, בדוק את החיבור ונסה שנית.", BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(!isNetworkAvailable())
                {
                    Snackbar.make(findViewById(android.R.id.content),"אין גישה לאינטרנט, בדוק את החיבור ונסה שנית.", BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openLogInActivity(){
        if(timePassed) {
            Intent openLogInActivity = new Intent(SplashScreenActivity.this, LogInActivity.class);
            startActivity(openLogInActivity);
            finish();
        }
    }

    private void openMainActivity(){
        if(timePassed && !isNetworkAvailable()){
            Snackbar.make(findViewById(android.R.id.content),"אין גישה לאינטרנט, בדוק את החיבור ונסה שנית.", BaseTransientBottomBar.LENGTH_LONG).show();
        }
        if(timePassed && infoLoaded) {
            Intent openMainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
            openMainActivity.putExtra("myKen", myKen);
            openMainActivity.putExtra("kensList", kensList);
            openMainActivity.putExtra("allTasks", allTasks);
            openMainActivity.putExtra("highlights", highlights);
            startActivity(openMainActivity);
            finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
