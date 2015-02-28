package com.banana.projectapp.campagne;

import android.graphics.Bitmap;

public class CompanyCampaign {
    private long id;
    private String url;
	private Bitmap logo;
	private String name;
	private int credits;

    public CompanyCampaign(long id, String url, String name, int credits){
        this.setId(id);
        this.setUrl(url);
        this.setName(name);
        this.setCredits(credits);
    }

	public CompanyCampaign(long id, String url, Bitmap logo, String name, int credits){
        this.setId(id);
        this.setUrl(url);
		this.setLogo(logo);
		this.setName(name);
		this.setCredits(credits);
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
	public int getCredits() {
		return credits;
	}
	public void setCredits(int credits) {
		this.credits = credits;
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
}
