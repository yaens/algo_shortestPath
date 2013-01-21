package ch.zhaw.shortestPath.model;

import java.util.ArrayList;
import java.util.List;

public class Point {
	String name;
	List<Point> next = new ArrayList<Point>();
	Point before;
	int distance;

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
		this.distance = distance;
	}

	int getDistance() {
		return distance;
	}

}
