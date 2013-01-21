package ch.zhaw.shortestPath.model;

import java.util.ArrayList;
import java.util.List;

public class Punkt {
	String name;
	List<Punkt> next = new ArrayList<Punkt>();
	Punkt before;
	int distance;

	public Punkt(String name) {
		this.name = name;
	}

	void setName(String name) {
		this.name = name;
	}

	String getName() {
		return name;
	}

	void setNext(Punkt next) {
		this.next.add(next);
	}

	List<Punkt> getNext() {
		return next;
	}

	void setBefore(Punkt before) {
		this.before = before;
	}

	Punkt getBefore() {
		return before;
	}

	void setDistance(int distance) {
		this.distance = distance;
	}

	int getDistance() {
		return distance;
	}

}
