package com.example.emyeraky.simplegithub;

/**
 * Created by Emy Eraky on 1/3/2018.
 */

public class Data {
    private String RepoName;
    private String Description;
    private String User_Name;
    private boolean fork;
    private String html_url;

    public String getRepoName() {
        return RepoName;
    }

    public void setRepoName(String repoName) {
        RepoName = repoName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public boolean getFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }
}
