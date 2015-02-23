package com.banana.projectapp.profile;

import android.graphics.Bitmap;

public class Social {
    private long id = -1;
	private Bitmap logo;
	private String name;
	public Social(Bitmap logo, String name){
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
}
