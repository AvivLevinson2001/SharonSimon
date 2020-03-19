package com.example.sharonsimon.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.Adapters.KenAdapter;
import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LeaderboardFragment extends Fragment {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.leaderboard_fragment,container,false);

        RecyclerView recyclerView = viewGroup.findViewById(R.id.kens_rv);
        final KenAdapter  adapter = new KenAdapter(new ArrayList<Ken>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager();

        firebaseDatabase.getReference().child("kens").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    GenericTypeIndicator<ArrayList<Ken>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Ken>>() {};
                    ArrayList<Ken> kens = dataSnapshot.getValue(genericTypeIndicator);
                    adapter.setKens(kens);
                    adapter.notifyDataSetChanged();
                    Log.d("ddd","ddd");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return viewGroup;
    }
}
