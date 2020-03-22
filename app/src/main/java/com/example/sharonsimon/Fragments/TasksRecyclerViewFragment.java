package com.example.sharonsimon.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.Adapters.TaskAdapter;
import com.example.sharonsimon.Classes.Task;
import com.example.sharonsimon.R;

import java.util.ArrayList;

public class TasksRecyclerViewFragment extends Fragment {

    RecyclerView recycler;
    TaskAdapter adapter;

    ArrayList<Task> tasksArrayList;

    public static TasksRecyclerViewFragment newInstance(ArrayList<Task> tasksArrayList){
        TasksRecyclerViewFragment fragment = new TasksRecyclerViewFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("tasksArrayList",tasksArrayList);
        fragment.setArguments(arguments);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.tasks_recycler_view_fragment,container,false);

        tasksArrayList = (ArrayList<Task>)getArguments().getSerializable("tasksArrayList");

        recycler = viewGroup.findViewById(R.id.tasks_rv);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recycler.setLayoutManager(mLayoutManager);

        adapter = new TaskAdapter(tasksArrayList);
        recycler.setAdapter(adapter);


        return viewGroup;
    }
}
