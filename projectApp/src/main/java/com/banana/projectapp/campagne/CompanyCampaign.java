package com.banana.projectapp.campagne;

import android.graphics.Bitmap;

public class CompanyCampaign {
    private long id;
    private String url;
	private Bitmap logo;
	private String name;
    private float userGain;

    public CompanyCampaign(long id, String url, String name, float userGain){
        this.setId(id);
        this.setUrl(url);
        this.setName(name);
        this.setUserGain(userGain);
    }

	public CompanyCampaign(long id, String url, Bitmap logo, String name, float userGain){
        this.setId(id);
        this.setUrl(url);
		this.setLogo(logo);
		this.setName(name);
        this.setUserGain(userGain);
	}
	public Bitmap getLogo() {
		return logo;
	}
	public void setLogo(Bitmap logo) {
		this.logo = logo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    public long getId() {
        return id;
    }
    public void setId(long id) { this.id = id; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getUserGain() {
        return userGain;
    }

    public void setUserGain(float userGain) {
        this.userGain = userGain;
    }
}
