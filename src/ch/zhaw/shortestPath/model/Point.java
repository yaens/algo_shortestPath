package ch.zhaw.shortestPath.model;

import java.util.ArrayList;
import java.util.List;

public class Point {
	String name;
	List<Point> next = new ArrayList<Point>();
	List<Edge> edges = new ArrayList<Edge>();
	Point before;
	int distanceToStart;
	boolean status;

	public boolean getStatus() {
		return status;
	}

	public void setGreenStatus() {
		status = true;
	}

	public void setRedStatus() {
		status = false;
	}

	public void setEdge(Edge edge) {
		this.edges.add(edge);
	}

	public List<Edge> getEdge() {
		return edges;
	}

	public Point(String name) {
		this.name = name;
	}

	void setName(String name) {
		this.name = name;
	}

	String getName() {
		return name;
	}

	void setNext(Point next) {
		this.next.add(next);
	}

	List<Point> getNext() {
		return next;
	}

	void setBefore(Point before) {
		this.before = before;
	}

	Point getBefore() {
		return before;
	}

	void setDistance(int distance) {
		this.distanceToStart = distance;
	}

	int getDistance() {
		return distanceToStart;
	}

}
