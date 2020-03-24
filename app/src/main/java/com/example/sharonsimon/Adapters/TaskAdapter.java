package com.example.sharonsimon.Adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sharonsimon.R;
import com.example.sharonsimon.Classes.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by ronto on 18-Dec-18.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.CreateTaskViewHolder>
{

    private ArrayList<Task> tasks;
    private Uri[] videosUri;
    private myTaskAdapterListener listener;
    private boolean checkBoxIsClickable = false;
    private String kenName;
    private Context context;

    public interface myTaskAdapterListener
    {
        void onTaskClick(int position, View v);
        void onTaskLongClick(int position, View v);
        void onCheckBoxClick(int position, View v);
        void onVideoClick(int position, View v);
    }

    public void setListener(myTaskAdapterListener listener)
    {
        this.listener = listener;
    }

    public void setCheckBoxIsClickable(boolean isClickable){
        checkBoxIsClickable = isClickable;
    }

    public TaskAdapter(ArrayList<Task> tasks)
    {
        videosUri = new Uri[tasks.size()];
        this.tasks = tasks;
        if(this.tasks == null)
            this.tasks = new ArrayList<>();
    }

    public TaskAdapter(ArrayList<Task> tasks, Uri[] videosUri, Context context)
    {
        this.videosUri = new Uri[tasks.size()];
        this.tasks = tasks;
        if(this.tasks == null)
            this.tasks = new ArrayList<>();
        this.kenName = kenName;
        this.context = context;
    }

    @Override
    public int getItemCount()
    {
        if(tasks == null) return 0;
        return tasks.size();
    }

    public class CreateTaskViewHolder extends RecyclerView.ViewHolder
    {
        CheckBox isCompletedCB;
        TextView descTV;
        TextView pointsTV;
        ImageView starIV;
        ImageView videoThumbnailIV;
        ProgressBar loadingThumbnailPB;

        public CreateTaskViewHolder(final View itemView) {
            super(itemView);

            isCompletedCB = itemView.findViewById(R.id.card_view_task_is_completed_cb);
            descTV = itemView.findViewById(R.id.card_view_task_desc_tv);
            pointsTV = itemView.findViewById(R.id.card_view_task_points_tv);
            starIV = itemView.findViewById(R.id.card_view_task_star_iv);
            videoThumbnailIV = itemView.findViewById(R.id.card_view_task_video_thumbnail_iv);
            loadingThumbnailPB = itemView.findViewById(R.id.card_view_task_loading_thumbnail_pb);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onTaskClick(getAdapterPosition(), view);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null)
                        listener.onTaskLongClick(getAdapterPosition(), view);
                    return false;
                }
            });

            if (checkBoxIsClickable) {
                isCompletedCB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onCheckBoxClick(getAdapterPosition(), view);
                    }
                });
            }
            else{
                isCompletedCB.setClickable(false);
            }

            if(isCompletedCB.isChecked()){
                videoThumbnailIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onVideoClick(getAdapterPosition(), view);
                    }
                });
            }
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
    public void onBindViewHolder(final CreateTaskViewHolder holder, int position)
    {
        Task task = tasks.get(position);
        holder.isCompletedCB.setChecked(task.isCompleted());
        holder.descTV.setText(task.getDesc());
        holder.pointsTV.setText(task.getPoints() + "");

        if (task.isCompleted())
        {
            holder.starIV.setVisibility(View.INVISIBLE);
            holder.videoThumbnailIV.setVisibility(View.GONE);
            holder.loadingThumbnailPB.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.videoThumbnailIV.setVisibility(View.GONE);
            holder.loadingThumbnailPB.setVisibility(View.GONE);
            AnimationSet starAnimations = new AnimationSet(true);

            AlphaAnimation  blink= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
            blink.setDuration(600); // duration
            blink.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            blink.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            blink.setRepeatMode(Animation.REVERSE);

            starAnimations.addAnimation(blink);
            holder.starIV.startAnimation(starAnimations);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(holder.starIV, "scaleX", 1.5f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(holder.starIV, "scaleY", 1.5f);
            scaleDownX.setDuration(600);
            scaleDownY.setDuration(600);

            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);

            scaleDown.start();
        }

        if(task.isCompleted() && videosUri != null && videosUri[position] != null){
            Glide.with(context).asBitmap().load(videosUri[position]).into(holder.videoThumbnailIV);
            holder.videoThumbnailIV.setVisibility(View.VISIBLE);
            holder.loadingThumbnailPB.setVisibility(View.GONE);
        }
        else{
            holder.videoThumbnailIV.setImageURI(null);
            holder.videoThumbnailIV.setImageBitmap(null);
        }
    }

    public void setVideoUri(int position, Uri uri){
        if(position < videosUri.length){
            videosUri[position] = uri;
            notifyDataSetChanged();
        }
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}
