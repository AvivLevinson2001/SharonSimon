package com.example.sharonsimon.Adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sharonsimon.Activities.MainActivity;
import com.example.sharonsimon.Classes.Highlight;
import com.example.sharonsimon.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by ronto on 18-Dec-18.
 */

public class HighlightAdapter extends RecyclerView.Adapter<HighlightAdapter.CreateHighlightViewHolder>
{
    private ArrayList<Highlight> highlights;
    private HighlightAdapterListener listener;
    private Context context;

    public interface HighlightAdapterListener{
        void onHighlightLongClick(int position, View v);
    }

    public void setListener(HighlightAdapterListener listener) {
        this.listener = listener;
    }

    public HighlightAdapter(ArrayList<Highlight> highlights, Context context)
    {
        this.highlights = highlights;
        if(this.highlights == null)
            this.highlights = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getItemCount()
    {
        if(highlights == null) return 0;
        return highlights.size();
    }

    public class CreateHighlightViewHolder extends RecyclerView.ViewHolder
    {
        TextView taskDescTV;
        TextView kenNameTV;
        JCVideoPlayerStandard video;

        public CreateHighlightViewHolder(final View itemView) {
            super(itemView);

            taskDescTV = itemView.findViewById(R.id.card_view_highlights_desc_tv);
            kenNameTV = itemView.findViewById(R.id.card_view_highlights_ken_tv);
            video = itemView.findViewById(R.id.card_view_highlights_video_player);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(listener != null){
                        listener.onHighlightLongClick(getAdapterPosition(),view);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public CreateHighlightViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_highlights, parent, false);
        CreateHighlightViewHolder createHighlightViewHolder = new CreateHighlightViewHolder(view);

        return createHighlightViewHolder;
    }

    @Override
    public void onBindViewHolder(final CreateHighlightViewHolder holder, int position)
    {
        Highlight highlight = highlights.get(position);
        holder.taskDescTV.setText(highlight.getTaskDesc());
        holder.kenNameTV.setText(highlight.getKenName());

        holder.video.setUp(highlight.getVideoURL(),
                JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL);

        Glide.with(context).load(highlight.getVideoURL()).apply(new RequestOptions()).into(holder.video.thumbImageView);

    }

    public void setHighlights(ArrayList<Highlight> highlights) {
        this.highlights = highlights;
    }
}
