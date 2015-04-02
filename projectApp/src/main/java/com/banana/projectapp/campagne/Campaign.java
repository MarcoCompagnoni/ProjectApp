package com.banana.projectapp.campagne;

import android.graphics.Bitmap;

public class Campaign {

    private long id;
    private String url;
	private Bitmap logo;
	private String name;
    private float userGain;
    private CampaignType type;
    private double latitude;
    private double longitude;

    public enum CampaignType {GEO,PHOTO,GEOPHOTO};

    public Campaign(long id, String url, String name, float userGain, CampaignType type,
                    double latitude, double longitude){
        this.setId(id);
        this.setUrl(url);
        this.setName(name);
        this.setUserGain(userGain);
        this.setType(type);
        this.setLatitude(latitude);
        this.setLongitude(longitude);

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
    public CampaignType getType() {
        return type;
    }
    public void setType(CampaignType type) {
        this.type = type;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
