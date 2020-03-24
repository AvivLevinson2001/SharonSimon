package com.example.sharonsimon.Classes;

public class Highlight
{
    String taskDesc;
    String kenName;
    String videoURL;

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
}