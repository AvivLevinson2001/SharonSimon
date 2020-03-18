package com.example.sharonsimon.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sharonsimon.R;
import com.example.sharonsimon.classes.Task;

import java.util.List;

/**
 * Created by ronto on 18-Dec-18.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.CreateTaskViewHolder>
{

    private List<Task> tasks;
    private myTaskAdapterListener listener;

    public interface myTaskAdapterListener
    {
        void onTaskClick(int position, View v);
        void onTaskLongClick(int position, View v);
    }

    public void setListener(myTaskAdapterListener listener)
    {
        this.listener = listener;
    }

    public TaskAdapter(List<Task> tasks)
    {
        this.tasks = tasks;
    }

    @Override
    public int getItemCount()
    {
        return tasks.size();
    }

    public class CreateTaskViewHolder extends RecyclerView.ViewHolder
    {
        CheckBox isCompletedCB;
        TextView descTV;
        TextView pointsTV;

        public CreateTaskViewHolder(final View itemView)
        {
            super(itemView);

            isCompletedCB = itemView.findViewById(R.id.card_view_task_is_completed_cb);
            descTV = itemView.findViewById(R.id.card_view_task_desc_tv);
            pointsTV = itemView.findViewById(R.id.card_view_task_points_tv);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    listener.onTaskClick(getAdapterPosition(), view);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onTaskLongClick(getAdapterPosition(), view);
                    return false;
                }
            });
        }
    }

    @Override
    public CreateTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_tasks, parent, false);
        CreateTaskViewHolder createTaskViewHolder = new CreateTaskViewHolder(view);
        return createTaskViewHolder;
    }

    @Override
    public void onBindViewHolder(CreateTaskViewHolder holder, int position)
    {
        Task task = tasks.get(position);
        holder.isCompletedCB.setChecked(task.isCompleted());
        holder.descTV.setText(task.getDesc());
        holder.pointsTV.setText(task.getPoints());

        //Todo change opacity if completed
    }
}
