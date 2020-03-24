package com.example.sharonsimon.Activities;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.sharonsimon.Classes.Highlight;
import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Classes.Task;
import com.example.sharonsimon.Dialogs.LoadingDialogBuilder;
import com.example.sharonsimon.Interfaces.FirebaseChangesListener;
import com.example.sharonsimon.Fragments.HighlightsFragment;
import com.example.sharonsimon.Fragments.KensRecyclerViewFragment;
import com.example.sharonsimon.Fragments.ViewKenFragment;
import com.example.sharonsimon.Fragments.UpdateTodaysTasksFragment;
import com.example.sharonsimon.R;
import com.example.sharonsimon.Services.UploadVideoToFirebaseService;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class  MainActivity extends AppCompatActivity implements KensRecyclerViewFragment.KensRecyclerViewFragmentListener, FirebaseChangesListener {

    ArrayList<Ken> kensList;
    Ken myKen;
    ArrayList<Task> allTasks;
    ArrayList<Highlight> highlights;
    Highlight newHighlight;

    SharedPreferences sp;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment currentFragment;
    BroadcastReceiver videoUploadedReceiver;

    DrawerLayout drawer;
    CoordinatorLayout coordinatorLayout;
    NavigationView navigationView;

    Dialog loadingDialog;

    JCVideoPlayerStandard videoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        videoUploadedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(currentFragment instanceof ViewKenFragment){
                    String taskDesc = intent.getStringExtra("taskDesc");
                    Uri videoUri = intent.getParcelableExtra("videoUri");
                    //TODO update highlights
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(videoUploadedReceiver,new IntentFilter("sharon_simon.video_uploaded_action"));

        sp = getSharedPreferences("login",MODE_PRIVATE);

        getInfoFromFirebase();

        drawer = findViewById(R.id.drawer_layout);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        navigationView.getMenu().setGroupVisible(R.id.admin_items_group,sp.getString("name","").equals("barvaz15"));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String fragmentTag = "";
                if(item.getItemId() == R.id.action_my_ken){
                    currentFragment = ViewKenFragment.newInstance(myKen,sp.getString("name","").equals("barvaz15"));
                    fragmentTag = "MyKen";
                }
                else if(item.getItemId() == R.id.action_leaderboard){
                    currentFragment = KensRecyclerViewFragment.newInstance(kensList);
                    fragmentTag = "Leaderboard";
                }
                else if(item.getItemId() == R.id.action_highlights){
                    currentFragment = new HighlightsFragment();
                }
                else if(item.getItemId() == R.id.action_log_out){
                    sp.edit().putBoolean("isLoggedIn", false)
                            .putString("name", "")
                            .putString("ken","")
                            .apply();
                    Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    finish();
                    return false;
                }
                else if(item.getItemId() == R.id.action_update_tasks){
                    currentFragment = UpdateTodaysTasksFragment.newInstance(allTasks);
                    fragmentTag = "UpdateTasks";
                }
                navigationView.setCheckedItem(item);
                drawer.closeDrawer(GravityCompat.START);
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.main_fragments_holder,currentFragment,fragmentTag).commit();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            drawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    public void getInfoFromFirebase(){
        loadingDialog = LoadingDialogBuilder.createLoadingDialog(this);
        loadingDialog.show();
        final String myKenName = sp.getString("ken","");
        databaseReference.child("kens").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    final String[] kensNames = new String[]
                            {"מקורות", "המעפיל", "מעיין", "העוגן", "רמות חפר", "יקום", "געש", "גן שמואל",
                                    "להבות חביבה", "מבואות עירון", "כרכור", "כפס מרום", "הרצליה", "חריש"};

                    ArrayList<Ken> newKens = new ArrayList<>();
                    for(String kenName : kensNames){
                        Ken newKen = new Ken(kenName, new ArrayList<Task>(), 0);
                        newKens.add(newKen);
                        if(kenName.equals(myKenName))
                            myKen = newKen;
                    }
                    kensList = newKens;
                    databaseReference.child("kens").setValue(newKens);
                    allTasks = new ArrayList<>();
                    highlights = new ArrayList<>();
                    loadingDialog.dismiss();
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
                                    loadingDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                if(navigationView.getCheckedItem().getItemId() == R.id.action_my_ken) {
                    currentFragment = ViewKenFragment.newInstance(myKen, sp.getString("name", "").equals("barvaz15"));
                    fragmentManager.beginTransaction().replace(R.id.main_fragments_holder, currentFragment, "MyKen").commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onKenClick(Ken ken) {
        currentFragment = ViewKenFragment.newInstance(ken,sp.getString("name","").equals("barvaz15"));
        if(ken.getName().equals(myKen.getName())){
            fragmentManager.beginTransaction().replace(R.id.main_fragments_holder,currentFragment,"MyKen").commit();
            navigationView.setCheckedItem(R.id.action_my_ken);
        }
        else {
            fragmentManager.beginTransaction().add(R.id.main_fragments_holder, currentFragment, "ShowKen").addToBackStack("backStack").commit();
        }
    }

    @Override
    public void saveKenToFirebase(Ken kenToSave) {
        for(int i = 0; i<kensList.size(); i++){
            if(kensList.get(i).getName().equals(kenToSave.getName())){
                kensList.set(i, kenToSave);
            }
        }
        databaseReference.child("kens").setValue(kensList);
    }

    @Override
    public void addTaskToFirebase(Task task) {
        // allTasks is already up to date
        databaseReference.child("tasks").setValue(allTasks);
        for(Ken ken : kensList){
            ken.addTask(new Task(task));
            ken.calculatePoints();
        }
        databaseReference.child("kens").setValue(kensList);
    }

    @Override
    public void removeTaskFromFirebase(Task task) {
        // allTasks is already up to date
        databaseReference.child("tasks").setValue(allTasks);
        for(Ken ken : kensList){
            ken.removeTaskByDesc(task.getDesc());
            ken.calculatePoints();
        }
        databaseReference.child("kens").setValue(kensList);
    }

    @Override
    public void addTaskToHighlights(String taskDesc, String kenName) {
        highlights.add(new Highlight(taskDesc,kenName,null));
        getVideoFromGallery();
        databaseReference.child("highlights").setValue(highlights);
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        if (currentFragment instanceof HighlightsFragment)
        {
            if (JCVideoPlayer.backPress()) {
                return;
            }
        }
        super.onBackPressed();
    }

    public void getVideoFromGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent,"select video"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                final Uri videoUri = data.getData();
                final String kenName = highlights.get(highlights.size() - 1).getKenName();
                final String taskDesc = highlights.get(highlights.size() - 1).getTaskDesc();
                Intent uploadVideoToFirebaseService = new Intent(MainActivity.this, UploadVideoToFirebaseService.class);
                uploadVideoToFirebaseService.putExtra("kenName",kenName);
                uploadVideoToFirebaseService.putExtra("taskDesc",taskDesc);
                uploadVideoToFirebaseService.putExtra("videoUri",videoUri);
                startService(uploadVideoToFirebaseService);
            }
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(videoUploadedReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        if (currentFragment instanceof HighlightsFragment)
        {
            JCVideoPlayer.releaseAllVideos();
        }
        super.onPause();
    }
}
