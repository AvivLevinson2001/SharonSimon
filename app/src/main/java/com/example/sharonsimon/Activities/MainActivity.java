package com.example.sharonsimon.Activities;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Classes.Task;
import com.example.sharonsimon.Dialogs.LoadingDialogBuilder;
import com.example.sharonsimon.Fragments.HighlightsFragment;
import com.example.sharonsimon.Fragments.KensRecyclerViewFragment;
import com.example.sharonsimon.Fragments.ViewKenFragment;
import com.example.sharonsimon.Fragments.TasksRecyclerViewFragment;
import com.example.sharonsimon.Fragments.UpdateTodaysTasksFragment;
import com.example.sharonsimon.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class  MainActivity extends AppCompatActivity implements KensRecyclerViewFragment.KensRecyclerViewFragmentListener, UpdateTodaysTasksFragment.FirebaseChangesListener {

    ArrayList<Ken> kensList;
    Ken myKen;

    SharedPreferences sp;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference = firebaseDatabase.getReference();
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment currentFragment;

    DrawerLayout drawer;
    NavigationView navigationView;

    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences("login",MODE_PRIVATE);

        getInfoFromFirebase();

        drawer = findViewById(R.id.drawer_layout);
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
                /*else if(item.getItemId() == R.id.action_todays_tasks){
                    currentFragment = TasksRecyclerViewFragment.newInstance(todaysTasks);
                    fragmentTag = "TodaysTasks";
                }*/
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
                else if(item.getItemId() == R.id.action_update_todays_tasks){
                    currentFragment = new UpdateTodaysTasksFragment();
                    fragmentTag = "UpdateTodaysTasks";
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
        reference.child("kens").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    reference.child("kens").setValue(newKens);
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
                    loadingDialog.dismiss();
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
        fragmentManager.beginTransaction().add(R.id.main_fragments_holder,currentFragment,"ShowKen").addToBackStack("backStack").commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void reloadInfoFromFirebase() {
        getInfoFromFirebase();
    }
}
