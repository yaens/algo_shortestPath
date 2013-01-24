package ch.zhaw.shortestPath.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DijkstraAlgorithm implements IPathAlgorithm {
	private static List<Edge> originalGraph = new ArrayList<Edge>();
	private static List<Point> points = new ArrayList<Point>();
	private static List<Point> redPoints = new ArrayList<Point>();
	private static List<Point> greenPoints = new ArrayList<Point>();
	private static List<Point> shortestPath = new ArrayList<Point>();
	private static Point A;
	private static Point B;
	private static Point C;
	private static Point D;
	private static Point E;
	private static Point F;
	private static Point G;
	private static Point H;
	private static Point I;
	private static Edge k1;
	private static Edge k2;
	private static Edge k3;
	private static Edge k4;
	private static Edge k5;
	private static Edge k6;
	private static Edge k7;
	private static Edge k8;
	private static Edge k9;
	private static Edge k10;
	private static Edge k11;
	private static Edge k12;
	private static Edge k13;
	private static Edge k14;
	private static Edge k15;

	static Comparator<Point> sortByDistance = new Comparator<Point>() {
		public int compare(Point point1, Point point2) {
			return point1.getDistance() - point2.getDistance();
		}
	};

	public static void createTestSzenario() {

		// a1, b2, c3, d4, e5, f6, g7, h8, i9
		A = new Point("A");
		B = new Point("B");
		C = new Point("C");
		D = new Point("D");
		E = new Point("E");
		F = new Point("F");
		G = new Point("G");
		H = new Point("H");
		I = new Point("I");

		points.add(A);
		points.add(B);
		points.add(C);
		points.add(D);
		points.add(E);
		points.add(F);
		points.add(G);
		points.add(H);
		points.add(I);

		A.setNext(B);
		A.setNext(F);
		A.setNext(G);
		B.setNext(C);
		B.setNext(G);
		C.setNext(I);
		C.setNext(D);
		D.setNext(I);
		D.setNext(E);
		E.setNext(H);
		E.setNext(F);
		F.setNext(H);
		G.setNext(H);
		G.setNext(I);
		H.setNext(I);

		// a1, b2, c3, d4, e5, f6, g7, h8, i9
		k1 = new Edge(A, B, 2);
		k2 = new Edge(A, F, 9);
		k3 = new Edge(A, G, 15);
		k4 = new Edge(B, C, 2);
		k5 = new Edge(B, G, 6);
		k6 = new Edge(C, I, 15);
		k7 = new Edge(C, D, 2);
		k8 = new Edge(D, I, 2);
		k9 = new Edge(D, E, 1);
		k10 = new Edge(E, H, 3);
		k11 = new Edge(E, F, 6);
		k12 = new Edge(F, H, 11);
		k13 = new Edge(G, H, 15);
		k14 = new Edge(G, I, 2);
		k15 = new Edge(H, I, 9);

		originalGraph.add(k1);
		originalGraph.add(k2);
		originalGraph.add(k3);
		originalGraph.add(k4);
		originalGraph.add(k5);
		originalGraph.add(k6);
		originalGraph.add(k7);
		originalGraph.add(k8);
		originalGraph.add(k9);
		originalGraph.add(k10);
		originalGraph.add(k11);
		originalGraph.add(k12);
		originalGraph.add(k13);
		originalGraph.add(k14);
		originalGraph.add(k15);

	}

	public static void changePointToRed(Point point) {
		if (point.getStatus()) {
			redPoints.add(point);
			greenPoints.remove(point);
		}
		point.setRedStatus();
	}

	public static void changePointToGreen(Point point) {
		redPoints.remove(point);
		if (!point.getStatus()) {
			greenPoints.add(point);
		}
		point.setGreenStatus();
	}

	public static int getDistanceFromTo(Point from, Point to) {
		int output = 0;
		for (Edge ed : originalGraph) {
			Point start = ed.getFrom();
			Point end = ed.getTo();
			if (start == from && end == to) {
				output = ed.getDistance();
			}
		}
		return output;
	}

	private static void getShortestPath(Point from, Point to) {
		System.out.println("kuerzester Pfad:");

		// add latest point to list
		shortestPath.add(to);
		System.out.println("Laenge:   " + to.getDistance() + "");

		int maximalRuns = points.size() * points.size();

		for (int i = 0; i < maximalRuns; i++) {

			// stop the loop, if the beforePoint is the startPoint
			if (to.getBefore().equals(from)) {
				shortestPath.add(from);
				break;
			}

			// add the beforePoint to the list
			shortestPath.add(to.getBefore());

			// set the beforePoint as activePoint for the next looping
			Point next = to.getBefore();
			to = next;
		}

		// change the order for the list
		Collections.reverse(shortestPath);

		for (Point point : shortestPath) {
			System.out.println(point.getName());
		}

	}

	public static void work(List<Edge> graph, Point start) {

		// preparation for all points
		for (Point all : points) {
			all.setDistance(Integer.MAX_VALUE);
			all.setBefore(null);
			all.setRedStatus();
			redPoints.add(all);
		}

		// preparation for startpoint
		changePointToGreen(start);
		start.setDistance(0);
		start.setBefore(start);

		// worst-case => dijkstra: (points * points)
		int maximalRuns = points.size() * points.size();

		// loop for djikstra
		for (int i = 0; i < maximalRuns; i++) {

			// first loop with startPoint
			if (i == 0) {
				for (Point next : start.getNext()) {

					// get distance from start to neighboors
					int distanceStartToNext = getDistanceFromTo(start, next);

					// set distance
					next.setDistance(distanceStartToNext);

					// set start as beforePoint
					next.setBefore(start);
				}
			}

			// stop the loop, if now points are red
			if (redPoints.size() == 0) {
				break;
			}

			// sort the red points
			Collections.sort(redPoints, sortByDistance);

			// take the point with the smallest distance
			Point nextPoint = redPoints.get(0);

			// make the nextPoint green
			changePointToGreen(nextPoint);

			// loop trough all neighboors
			for (Point next : nextPoint.getNext()) {

				// make the point red
				changePointToRed(next);

				// get distance between the neighboors
				int distanceNextPointToNext = getDistanceFromTo(nextPoint, next);

				// count the new distance
				int tmpDistance = (nextPoint.getDistance() + distanceNextPointToNext);

				// if the distance is closer, set the new distance and
				// beforePoint
				if (tmpDistance <= next.getDistance()) {
					next.setDistance(tmpDistance);
					next.setBefore(nextPoint);
				}

				// make the point green
				changePointToGreen(nextPoint);

			}
		}

		// show all points, beforePoints and distance
		System.out.println("Point - BeforePoint - Distance");
		for (Point all : points) {
			System.out.println(all.getName() + "     -      "
					+ all.getBefore().getName() + "      -      "
					+ all.getDistance());
		}

		// show all red points
		System.out.println("red points");
		if (redPoints.size() == 0) {
			System.out.println("-- empty --");
		}
		for (Point red : redPoints) {
			System.out.println(red.getName());
		}

		// show all green points
		System.out.println("green points");
		if (greenPoints.size() == 0) {
			System.out.println("-- empty --");
		}
		for (Point green : greenPoints) {
			System.out.println(green.getName());
		}

	}

	public static void main(String[] args) {
		createTestSzenario();
		work(originalGraph, A);
		getShortestPath(A, I);
	}

	@Override
	public void CalculateAlgorithm(ArrayList<Node> nodeList,
			ArrayList<Connector> connectorList) {
	}

}
