package ch.zhaw.shortestPath.model;

import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwindx.examples.util.DirectedPath;

public class Connector extends Polyline{
	private Node from;
	private Node to;
	private double lenght;
	
	public Node getFrom() {
		return from;
	}
	public void setFrom(Node from) {
		this.from = from;
	}
	public Node getTo() {
		return to;
	}
	public void setTo(Node to) {
		this.to = to;
	}
	public double getLenght() {
		return lenght;
	}
	public void setLenght(double lenght) {
		this.lenght = lenght;
	}
	
	

}
