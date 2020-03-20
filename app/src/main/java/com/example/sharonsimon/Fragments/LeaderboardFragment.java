package com.example.sharonsimon.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.backup.BackupDataInput;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.Adapters.KenAdapter;
import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Dialogs.LoadingDialogBuilder;
import com.example.sharonsimon.Interfaces.KensListFragmentInterface;
import com.example.sharonsimon.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LeaderboardFragment extends Fragment {

    ArrayList<Ken> kensArrayList = new ArrayList<>();
    KenAdapter adapter;

    public static LeaderboardFragment newInstance(ArrayList<Ken> kensArrayList){
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("kensArrayList",kensArrayList);
        fragment.setArguments(arguments);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.leaderboard_fragment,container,false);

        kensArrayList = (ArrayList<Ken>) getArguments().getSerializable("kensArrayList");

        RecyclerView recyclerView = viewGroup.findViewById(R.id.kens_rv);
        adapter = new KenAdapter(kensArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return viewGroup;
    }
}
