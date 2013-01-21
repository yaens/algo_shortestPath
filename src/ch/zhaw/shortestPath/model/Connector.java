package ch.zhaw.shortestPath.model;

import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwindx.examples.util.DirectedPath;

public class Connector extends DirectedPath {
	private Node from;
	private Node to;
	
	private double lenght;

	public double getLenght() {
		return lenght;
	}

	public void setLenght(double lenght) {
		this.lenght = lenght;
	}

}
