package ch.zhaw.shortestPath.model;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.SurfaceCircle;

import java.util.ArrayList;
import java.util.List;

public class Node extends SurfaceCircle {
	String name;
	List<Node> next = new ArrayList<Node>();
	List<Connector> edges = new ArrayList<Connector>();
	Node before;
	double distanceToStart;
	boolean status;

	public Node(Position curPos, int i) {
		super(curPos,i);
	}
	
	public boolean getStatus() {
		return status;
	}

	public void setGreenStatus() {
		status = true;
	}

	public void setRedStatus() {
		status = false;
	}

	public void setEdge(Connector edge) {
		this.edges.add(edge);
	}

	public List<Connector> getEdge() {
		return edges;
	}

	public Node(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setNext(Node next) {
		this.next.add(next);
	}

	List<Node> getNext() {
		return next;
	}

	void setBefore(Node before) {
		this.before = before;
	}

	Node getBefore() {
		return before;
	}

	void setDistance(double distance) {
		this.distanceToStart = distance;
	}

	double getDistance() {
		return distanceToStart;
	}
	
	public String toString(){
		return this.name;
	}

}
