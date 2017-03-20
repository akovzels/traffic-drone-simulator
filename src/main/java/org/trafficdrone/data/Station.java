package org.trafficdrone.data;

import org.trafficdrone.Position;

public class Station {
	
	private String name;
	
	private Position position;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "Station [name=" + name + ", position=" + position + "]";
	}

}
