package org.trafficdrone.data.model;

import org.trafficdrone.Position;

public class Station {
	
	private final String name;
	
	private final Position position;

	public Station(String name, Position position) {
		super();
		this.name = name;
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public Position getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return "Station [name=" + name + ", position=" + position + "]";
	}

}
