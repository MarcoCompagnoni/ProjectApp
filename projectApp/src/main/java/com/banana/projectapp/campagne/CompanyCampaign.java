package com.banana.projectapp.campagne;

import android.graphics.Bitmap;

public class CompanyCampaign {
    private long id = -1;
	private Bitmap logo;
	private String name;
	private int credits;
	public CompanyCampaign(Bitmap logo, String name, int credits){
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
}
