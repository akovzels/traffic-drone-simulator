package org.trafficdrone;

public class Position {
	
	private double latitude;

	private double longitude;
	
	private Position() {
		// 
	}
	
	public static final Position of(double latitude, double longitude) {
		Position pos = new Position();
		pos.latitude = latitude;
		pos.longitude = longitude;
		return pos;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return "Position [latitude=" + latitude + ", longitude=" + longitude + "]";
	}
}
