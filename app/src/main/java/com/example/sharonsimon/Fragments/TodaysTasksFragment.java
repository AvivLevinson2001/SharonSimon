package com.example.sharonsimon.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.Adapters.TaskAdapter;
import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Classes.Task;
import com.example.sharonsimon.Dialogs.LoadingDialogBuilder;
import com.example.sharonsimon.R;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TodaysTasksFragment extends Fragment {

    RecyclerView recycler;
    TaskAdapter adapter;

    ArrayList<Task> tasksArrayList;

    public static TodaysTasksFragment newInstance(ArrayList<Task> tasksArrayList){
        TodaysTasksFragment fragment = new TodaysTasksFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("tasksArrayList",tasksArrayList);
        fragment.setArguments(arguments);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.todays_tasks_fragment,container,false);

        tasksArrayList = (ArrayList<Task>)getArguments().getSerializable("tasksArrayList");

        recycler = viewGroup.findViewById(R.id.tasks_rv);

        adapter = new TaskAdapter(tasksArrayList);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        return viewGroup;
    }
}
