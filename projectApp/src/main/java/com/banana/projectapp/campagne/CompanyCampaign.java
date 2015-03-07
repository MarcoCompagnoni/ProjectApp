package com.banana.projectapp.campagne;

import android.graphics.Bitmap;

public class CompanyCampaign {
    private long id;
    private String url;
	private Bitmap logo;
	private String name;

    public CompanyCampaign(long id, String url, String name){
        this.setId(id);
        this.setUrl(url);
        this.setName(name);
    }

	public CompanyCampaign(long id, String url, Bitmap logo, String name){
        this.setId(id);
        this.setUrl(url);
		this.setLogo(logo);
		this.setName(name);
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
}
