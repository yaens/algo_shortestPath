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
	
	public Node getFromNode() {
		return from;
	}

	public void setFromNode(Node from) {
		this.from = from;
	}

	public Node getToNode() {
		return to;
	}

	public void setToNode(Node to) {
		this.to = to;
	}

}
