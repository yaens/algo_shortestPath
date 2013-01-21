package ch.zhaw.shortestPath.model;

public class Edge {
	int distance;
	Point from;
	Point to;

	Edge(Point from, Point to, int distance) {
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

	public Point getFrom() {
		return from;
	}

	public void setFrom(Point from) {
		this.from = from;
	}

	public Point getTo() {
		return to;
	}

	public void setTo(Point to) {
		this.to = to;
	}

}
