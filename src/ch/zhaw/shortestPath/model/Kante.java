package ch.zhaw.shortestPath.model;

public class Kante {
	int distance;
	Punkt from;
	Punkt to;

	Kante(Punkt from, Punkt to, int distance) {
		this.from = from;
		this.to = to;
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public Punkt getFrom() {
		return from;
	}

	public void setFrom(Punkt from) {
		this.from = from;
	}

	public Punkt getTo() {
		return to;
	}

	public void setTo(Punkt to) {
		this.to = to;
	}

}
