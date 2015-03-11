package com.banana.projectapp.shop;

import android.graphics.Bitmap;

public class ShoppingItem {
    private long id;
    private String url;
	private Bitmap logo;
	private String name;
	private float credits;

    public ShoppingItem(long id, String url, Bitmap logo, String name, float credits){
        this.id = id;
        this.url = url;
        this.logo = logo;
        this.name = name;
        this.credits = credits;
    }

	public ShoppingItem(long id, String url, String name, float credits){
        this.id = id;
		this.url = url;
		this.name = name;
		this.credits = credits;
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
	public float getCredits() {
		return credits;
	}
	public void setCredits(float credits) {
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
