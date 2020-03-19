package com.example.sharonsimon.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.example.sharonsimon.Adapters.TaskAdapter;
import com.example.sharonsimon.R;
import com.example.sharonsimon.classes.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by ronto on 27-Nov-18.
 */

public class UpdateDailyTasksFragment extends Fragment
{

    RecyclerView recyclerView;
    FloatingActionButton addTaskFab;
    FloatingActionButton confirmFab;

    List<Task> tasks;

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
                                        Task task = tasks.get(position);
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
                            Task newTask = new Task(desc, Integer.parseInt(points), false);
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

                //TODO modify the firebase to the Project

                /*final Dialog loadingDialog = LoadingDialogBuilder.createLoadingDialog(getActivity());
                loadingDialog.show();

                myRef.child(UID).child("mainTasks").child(title).addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        loadingDialog.dismiss();

                        if (title.equals("") || desc.equals(""))
                        {
                            Snackbar.make(coordinatorLayout, "Please fill all fields", Snackbar.LENGTH_SHORT).show();
                        }
                        else if (dataSnapshot.getValue() != null) //If it exists
                        {
                            Snackbar.make(coordinatorLayout, "Task "+title+" Already exists", Snackbar.LENGTH_SHORT).show();
                        }
                        else if (subTasks.isEmpty()) //If there are no subTasks
                        {
                            Snackbar.make(coordinatorLayout, "No sub tasks", Snackbar.LENGTH_SHORT).show();
                        }
                        else
                        {
                            int logoNum = Integer.parseInt(logoNumTv.getText().toString());


                            MainTask task = new MainTask(title, desc, subTasks, logoNum, currDate);
                            final Dialog dialog = LoadingDialogBuilder.createLoadingDialog(getActivity());
                            dialog.show();
                            myRef.child(UID).child("mainTasks").child(titleEt.getText().toString()).setValue(task)
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task)
                                        {
                                            dialog.dismiss();
                                            Snackbar.make(coordinatorLayout, "Task successfully added!", Snackbar.LENGTH_SHORT).show();
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new HomeFragment()).commit();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });*/
            }
        });

        return viewGroup;
    }
}

