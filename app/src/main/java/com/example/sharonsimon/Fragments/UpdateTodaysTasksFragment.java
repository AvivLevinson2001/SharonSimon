package com.example.sharonsimon.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.sharonsimon.Adapters.TaskAdapter;
import com.example.sharonsimon.Classes.Task;
import com.example.sharonsimon.Interfaces.FirebaseChangesListener;
import com.example.sharonsimon.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by ronto on 27-Nov-18.
 */

public class UpdateTodaysTasksFragment extends Fragment
{
    RecyclerView recyclerView;
    FloatingActionButton addTaskFab;
    FloatingActionButton confirmFab;

    ArrayList<Task> tasks;

    FirebaseChangesListener listener;

    public static UpdateTodaysTasksFragment newInstance(ArrayList<Task> tasks){
        UpdateTodaysTasksFragment fragment = new UpdateTodaysTasksFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("tasks",tasks);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity)context;
        try{
            listener = (FirebaseChangesListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException("Activity: " + activity.toString() + " must implement KensRecyclerViewFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState)
    {
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.update_todays_tasks_fragment, container, false);

        tasks = (ArrayList<Task>) getArguments().getSerializable("tasks");
        if(tasks == null){
            tasks = new ArrayList<>();
        }

        //Assigning the views
        addTaskFab = viewGroup.findViewById(R.id.update_daily_tasks_add_task_fab);
        recyclerView = viewGroup.findViewById(R.id.update_tasks_recycler);
        final CoordinatorLayout coordinatorLayout = viewGroup.findViewById(R.id.update_daily_tasks_coordinator);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);

        final TaskAdapter adapter = new TaskAdapter(tasks);
        adapter.setListener(new TaskAdapter.myTaskAdapterListener()
        {
            @Override
            public void onTaskClick(int position, View v)
            {

            }

            @Override
            public void onTaskLongClick(final int position, View v)
            {
                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                popupMenu.getMenuInflater().inflate(R.menu.long_click_delete_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem)
                    {
                        if (menuItem.getItemId() == R.id.action_delete)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("האם אתה בטוח שברצונך למחוק את המשימה: " + tasks.get(position).getDesc() + " ?").setPositiveButton("כן", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Task taskToRemove = tasks.get(position);
                                    int index = tasks.indexOf(taskToRemove);
                                    tasks.remove(taskToRemove);
                                    listener.removeTaskFromFirebase(taskToRemove);
                                    adapter.notifyItemRemoved(index);
                                    Snackbar.make(container,"משימה נמחקה", BaseTransientBottomBar.LENGTH_LONG).show();
                                }
                            }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create().show();
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });
        recyclerView.setAdapter(adapter);

        addTaskFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                final View v = inflater.inflate(R.layout.create_task_dialog, null);
                builder.setPositiveButton("אישור", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                        EditText descEt = v.findViewById(R.id.create_task_dialog_desc_et);
                        EditText pointsEt = v.findViewById(R.id.create_task_dialog_points_et);

                        String desc = descEt.getText().toString();
                        String points = pointsEt.getText().toString();


                        if (points.equals("") || desc.equals("")) //Checking for null fields
                        {
                            Snackbar.make(container, "מלאו את כל השדות", Snackbar.LENGTH_LONG).show();
                        }
                        else
                        {
                            for(int k = 0; k < tasks.size(); k ++) {
                                if (tasks.get(k).getDesc().equals(desc)) {
                                    Snackbar.make(container, "המשימה כבר קיימת", BaseTransientBottomBar.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            Task newTask = new Task(desc, Integer.parseInt(points), false);
                            tasks.add(newTask);
                            listener.addTaskToFirebase(newTask);
                            adapter.notifyItemInserted(tasks.indexOf(newTask));//Updates the recycler
                            Snackbar.make(container, "משימה נוספה", Snackbar.LENGTH_LONG).show();
                        }

                    }
                });
                builder.setView(v);
                final Dialog dialog = builder.create();
                dialog.show();
            }
        });

        return viewGroup;
    }
}

