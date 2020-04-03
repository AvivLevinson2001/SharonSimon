package com.example.sharonsimon.Classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Highlight implements Serializable
{
    String taskDesc;
    String kenName;
    String videoURL;

    public Highlight(){ }

    public Highlight(String taskDesc, String kenName, String videoURL)
    {
        this.taskDesc = taskDesc;
        this.kenName = kenName;
        this.videoURL = videoURL;
    }

    public String getTaskDesc()
    {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc)
    {
        this.taskDesc = taskDesc;
    }

    public String getKenName()
    {
        return kenName;
    }

    public void setKenName(String kenName)
    {
        this.kenName = kenName;
    }

    public String getVideoURL()
    {
        return videoURL;
    }

    public void setVideoURL(String videoURL)
    {
        this.videoURL = videoURL;
    }

    @Override
    public String toString() {
        return "Highlight{" +
                "taskDesc='" + taskDesc + '\'' +
                ", kenName='" + kenName + '\'' +
                ", videoURL='" + videoURL + '\'' +
                '}';
    }

    public boolean isSameHighlight(Highlight highlight){
        return this.kenName.equals(highlight.kenName) && this.taskDesc.equals(highlight.taskDesc);
    }

    public static String serializeHighlightsForSharedPreferences(ArrayList<Highlight> highlights){
        ArrayList<String> serializedHighlights = new ArrayList<>();
        for(Highlight highlight : highlights){
            serializedHighlights.add(highlight.kenName + "-" + highlight.taskDesc);
        }
        return serializedHighlights.toString();
    }
}