package com.example.sharonsimon.Activities;


import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sharonsimon.Adapters.TaskAdapter;
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
import com.example.sharonsimon.Services.UploadHighlightToFirebaseService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
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
import com.takusemba.spotlight.OnSpotlightStateChangedListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.shape.Circle;
import com.takusemba.spotlight.target.SimpleTarget;
import com.takusemba.spotlight.target.Target;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
import androidx.recyclerview.widget.RecyclerView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

import static android.graphics.Color.argb;

public class  MainActivity extends AppCompatActivity implements KensRecyclerViewFragment.KensRecyclerViewFragmentListener, FirebaseChangesListener
{

    ArrayList<Ken> kensList;
    Ken myKen;
    ArrayList<Task> allTasks;
    ArrayList<Highlight> highlights;
    boolean isAdmin;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        videoUploadedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getSerializableExtra("highlight") != null) {
                    Highlight updatedHighlight = (Highlight) intent.getSerializableExtra("highlight");
                    for (Highlight highlight : highlights) {
                        if (highlight.getKenName().equals(updatedHighlight.getKenName()) && highlight.getTaskDesc().equals(updatedHighlight.getTaskDesc())) {
                            highlight.setVideoURL(updatedHighlight.getVideoURL());
                            Snackbar.make(coordinatorLayout,"העלאה הסתיימה", BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }
                }
                else if(intent.getSerializableExtra("highlightUploadCanceled") != null){
                    Highlight canceledHighlight = (Highlight) intent.getSerializableExtra("highlightUploadCanceled");
                    for (Highlight highlight : highlights) {
                        if(highlight.getKenName().equals(canceledHighlight.getKenName()) && highlight.getTaskDesc().equals(canceledHighlight.getTaskDesc())){
                            highlights.remove(highlight);
                            Snackbar.make(coordinatorLayout,"העלאה התבטלה", BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }
                }
                if(currentFragment instanceof HighlightsFragment){
                    ((HighlightsFragment)currentFragment).notifyAdapter();
                }
            }
        };

        sp = getSharedPreferences("user",MODE_PRIVATE);
        isAdmin = sp.getBoolean("isAdmin",false);


        LocalBroadcastManager.getInstance(this).registerReceiver(videoUploadedReceiver,new IntentFilter("sharon_simon.highlight_uploaded_action"));

        getInfoFromFirebase();

        drawer = findViewById(R.id.drawer_layout);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        navigationView.getMenu().setGroupVisible(R.id.admin_items_group,isAdmin);
        if(isAdmin){
            MenuItem item = navigationView.getMenu().findItem(R.id.action_my_ken);
            item.setVisible(false);
            navigationView.setCheckedItem(R.id.action_leaderboard);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String fragmentTag = "";
                if(item.getItemId() == R.id.action_my_ken){
                    currentFragment = ViewKenFragment.newInstance(myKen,isAdmin);
                    fragmentTag = "MyKen";
                }
                else if(item.getItemId() == R.id.action_leaderboard){
                    currentFragment = KensRecyclerViewFragment.newInstance(kensList);
                    fragmentTag = "Leaderboard";
                }
                else if(item.getItemId() == R.id.action_highlights){
                    currentFragment = HighlightsFragment.newInstance(highlights);
                    fragmentTag = "Higlights";
                }
                else if(item.getItemId() == R.id.action_log_out){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("האם אתה בטוח שברצונך להתנתק?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sp.edit().putBoolean("isLoggedIn", false)
                                    .putString("name", "")
                                    .putString("ken","")
                                    .putBoolean("isAdmin",false)
                                    .apply();
                            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create().show();
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
                                    loadingDialog.dismiss();
                                    openFirstFragment();
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
                                    loadingDialog.dismiss();
                                    openFirstFragment();
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
                                    loadingDialog.dismiss();
                                    openFirstFragment();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onKenClick(Ken ken) {
        currentFragment = ViewKenFragment.newInstance(ken,isAdmin);
        fragmentManager.beginTransaction().add(R.id.main_fragments_holder, currentFragment, "ShowKen").addToBackStack("backStack").commit();
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
        for(Highlight highlight : highlights){
            if(highlight.getKenName().equals(kenName) && highlight.getTaskDesc().equals(taskDesc)){
                Snackbar.make(coordinatorLayout,"המשימה כבר נמצאת בקטעים החמים", BaseTransientBottomBar.LENGTH_SHORT).show();
                return;
            }
            if(highlight.getVideoURL() == null || highlight.getVideoURL().equals("")){
                Snackbar.make(coordinatorLayout,"חכה שההעלאה הקודמת תסתיים", BaseTransientBottomBar.LENGTH_SHORT).show();
                return;
            }
        }
        Highlight newHighlight = new Highlight(taskDesc,kenName,null);
        highlights.add(newHighlight);
        getVideoFromGallery();
    }

    @Override
    public void removeTaskFromHighlights(Highlight highlight) {
        for(Highlight highlight1 : highlights)
        {
            if(highlight1.isSameHighlight(highlight))
                highlights.remove(highlight1);
        }
        databaseReference.child("highlights").setValue(highlights);
        if(highlight.getVideoURL() != null && !highlight.getVideoURL().equals("")) {
            firebaseStorage.getReferenceFromUrl(highlight.getVideoURL()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Snackbar.make(coordinatorLayout, "המשימה נמחקה מהקטעים החמים", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else {
            if (currentFragment instanceof HighlightsFragment) {
                if (JCVideoPlayer.backPress()) {
                    return;
                }
            }

            if (fragmentManager.getBackStackEntryCount() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("האם אתה בטוח שברצונך לסגור את האפליקציה?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.super.onBackPressed();
                    }
                }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create().show();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void getVideoFromGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent,"select video"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if (resultCode == RESULT_OK) {
                final Uri videoUri = data.getData();
                final Intent uploadVideoToFirebaseService = new Intent(MainActivity.this, UploadHighlightToFirebaseService.class);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("האם אתה בטוח שברצונך להוסיף את המשימה: " + '"' + highlights.get(highlights.size() - 1).getTaskDesc() + '"' + " של קן: " + highlights.get(highlights.size() - 1).getKenName() + " לקטעים החמים?")
                        .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uploadVideoToFirebaseService.putExtra("highlight", highlights.get(highlights.size() - 1));
                        uploadVideoToFirebaseService.putExtra("videoUri", videoUri);
                        databaseReference.child("highlights").setValue(highlights).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                UploadHighlightToFirebaseService.isCanceled = false;
                                startService(uploadVideoToFirebaseService);
                            }
                        });
                    }
                }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        highlights.remove(highlights.size() - 1);
                        Snackbar.make(coordinatorLayout,"העלאה התבטלה", BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                }).create().show();
            }
            else{
                highlights.remove(highlights.size() - 1);
            }
        }
    }

    private void openFirstFragment(){
        if(!isAdmin && navigationView.getCheckedItem().getItemId() == R.id.action_my_ken) {
            currentFragment = ViewKenFragment.newInstance(myKen, isAdmin);
            fragmentManager.beginTransaction().replace(R.id.main_fragments_holder, currentFragment, "MyKen").commitAllowingStateLoss();
        }
        else{
            currentFragment = KensRecyclerViewFragment.newInstance(kensList);
            fragmentManager.beginTransaction().replace(R.id.main_fragments_holder, currentFragment, "Leaderboard").commit();
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

    public static int[] getViewCenterPoint(View view){
        if(view != null) {
            int[] location = new int[2];
            view.getLocationInWindow(location);
            location[0] = location[0] + view.getWidth() / 2;
            location[1] = location[1] + view.getHeight() / 2;
            return location;
        }
        else{
            return new int[]{0,0};
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!isAdmin && sp.getBoolean("isFirstOpen", true)) {

            //sp.edit().putBoolean("isFirstOpen", false).apply();

            SimpleTarget taskTarget = new SimpleTarget.Builder(this)
                    .setPoint(getViewCenterPoint(findViewById(R.id.task_card_view_placeholder))[0], getViewCenterPoint(findViewById(R.id.task_card_view_placeholder))[1]).setShape(new Circle(300f))
                    .setTitle("משימות").setDescription("זאת המשימה הראשונה שלכם! עליכם לצלם את עצמכם מבצעים כמה שיותר משימות ולשלוח את הסרטונים לקומונר/ית שלכם.").build();
            SimpleTarget kenPointsTarget = new SimpleTarget.Builder(this)
                    .setPoint(getViewCenterPoint(findViewById(R.id.my_ken_points_placeholder_tv))[0], getViewCenterPoint(findViewById(R.id.my_ken_points_placeholder_tv))[1]).setShape(new Circle(100f))
                    .setTitle("ניקוד הקן").setDescription("כאן נמצא הניקוד של הקן שלכם, כל משימה שתבצעו תוסיף נקודות לקן!").build();
            SimpleTarget hamburgerTarget = new SimpleTarget.Builder(this)
                    .setPoint(getViewCenterPoint(findViewById(R.id.home_hamburger_placeholder_tv))[0], getViewCenterPoint(findViewById(R.id.home_hamburger_placeholder_tv))[1]).setShape(new Circle(100f))
                    .setTitle("תפריט").setDescription("כאן תוכלו לראות את העמודים השונים: הקן שלי, קני האיזור, והקטעים החמים").build();


            Spotlight.with(this)
                    .setOverlayColor(R.color.background)
                    .setDuration(250L).setAnimation(new DecelerateInterpolator(2f))
                    .setTargets(taskTarget, kenPointsTarget, hamburgerTarget)
                    .setClosedOnTouchedOutside(true).setOnSpotlightStateListener(new OnSpotlightStateChangedListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onEnded() {
                    drawer.openDrawer(GravityCompat.START);

                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SimpleTarget myKenItemViewTarget = new SimpleTarget.Builder(MainActivity.this)
                                    .setPoint(getViewCenterPoint(findViewById(R.id.my_ken_itemview_placeholder_tv))[0], getViewCenterPoint(findViewById(R.id.my_ken_itemview_placeholder_tv))[1]).setShape(new Circle(80f))
                                    .setTitle("הקן שלי").setDescription("כאן תראו את המשימות של הקן שלכם.").build();
                            SimpleTarget leaderboardItemViewTarget = new SimpleTarget.Builder(MainActivity.this)
                                    .setPoint(getViewCenterPoint(findViewById(R.id.leaderboard_itemview_placeholder_tv))[0], getViewCenterPoint(findViewById(R.id.leaderboard_itemview_placeholder_tv))[1]).setShape(new Circle(80f))
                                    .setTitle("קני האיזור").setDescription("פה תוכלו לראות את טבלת הניקוד של כל הקינים באזור").build();
                            SimpleTarget highlightsItemViewTarget = new SimpleTarget.Builder(MainActivity.this)
                                    .setPoint(getViewCenterPoint(findViewById(R.id.highlights_itemview_placeholder_tv))[0], getViewCenterPoint(findViewById(R.id.highlights_itemview_placeholder_tv))[1]).setShape(new Circle(80f))
                                    .setTitle("קטעים חמים").setDescription("כאן תוכלו לצפות בסרטונים של הקטעים הכי טובים והכי מצחיקים מכל יום!").build();


                            Spotlight.with(MainActivity.this)
                                    .setOverlayColor(R.color.background)
                                    .setDuration(250L).setAnimation(new DecelerateInterpolator(2f))
                                    .setTargets(myKenItemViewTarget, leaderboardItemViewTarget, highlightsItemViewTarget)
                                    .setClosedOnTouchedOutside(true).setOnSpotlightStateListener(new OnSpotlightStateChangedListener() {
                                @Override
                                public void onStarted() {

                                }

                                @Override
                                public void onEnded() {
                                    drawer.closeDrawer(GravityCompat.START);
                                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                                }
                            }).start();
                        }
                    }, 500);

                }
            }).start();
            super.onWindowFocusChanged(hasFocus);
        }
    }
}
