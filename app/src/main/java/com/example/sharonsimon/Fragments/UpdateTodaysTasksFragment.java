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

import com.example.sharonsimon.Adapters.TaskAdapter;
import com.example.sharonsimon.Classes.Ken;
import com.example.sharonsimon.Classes.Task;
import com.example.sharonsimon.Dialogs.LoadingDialogBuilder;
import com.example.sharonsimon.Interfaces.FirebaseChangesListener;
import com.example.sharonsimon.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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

        //Assigning the views
        addTaskFab = viewGroup.findViewById(R.id.update_daily_tasks_add_task_fab);
        recyclerView = viewGroup.findViewById(R.id.update_daily_tasks_recycler);
        confirmFab = viewGroup.findViewById(R.id.update_daily_tasks_confirm_fab);
        final CoordinatorLayout coordinatorLayout = viewGroup.findViewById(R.id.update_daily_tasks_coordinator);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        tasks = new ArrayList<>();
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
                popupMenu.getMenuInflater().inflate(R.menu.task_long_click_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem)
                    {
                        if (menuItem.getItemId() == R.id.item_edit)
                        {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                            final View v = inflater.inflate(R.layout.create_task_dialog, null);
                            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()
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
                                        Snackbar.make(coordinatorLayout, "מלאו את כל השדות", Snackbar.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        com.example.sharonsimon.Classes.Task task = tasks.get(position);
                                        task.setPoints(Integer.parseInt(points));
                                        task.setDesc(desc);
                                        adapter.notifyDataSetChanged();//Updates the recycler
                                        Snackbar.make(coordinatorLayout, "משימה התעדכנה", Snackbar.LENGTH_SHORT);
                                    }

                                }
                            });
                            builder.setView(v);
                            final Dialog dialog = builder.create();
                            dialog.show();

                            EditText descEt = v.findViewById(R.id.create_task_dialog_desc_et);
                            EditText pointsEt = v.findViewById(R.id.create_task_dialog_points_et);

                            descEt.setText(tasks.get(position).getDesc());
                            pointsEt.setText(tasks.get(position).getPoints());

                        }

                        else if (menuItem.getItemId() == R.id.item_delete)
                        {
                            tasks.remove(position);
                            recyclerView.removeViewAt(position);
                        }

                        return true;
                    }
                });

                popupMenu.show();
            }

            @Override
            public void onCheckBoxClick(int position, View v) {

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
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()
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
                            Snackbar.make(coordinatorLayout, "מלאו את כל השדות", Snackbar.LENGTH_SHORT).show();
                        }
                        else
                        {
                            com.example.sharonsimon.Classes.Task newTask = new com.example.sharonsimon.Classes.Task(desc, Integer.parseInt(points), false);
                            tasks.add(newTask);
                            adapter.notifyDataSetChanged();//Updates the recycler

                            Snackbar.make(coordinatorLayout, "משימה נוספה", Snackbar.LENGTH_SHORT).show();
                            addTaskFab.callOnClick();
                        }

                    }
                });
                builder.setView(v);
                final Dialog dialog = builder.create();
                dialog.show();
            }
        });

        confirmFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                listener.addTasksToFirebase(tasks);
            }
        });

        return viewGroup;
    }
}

