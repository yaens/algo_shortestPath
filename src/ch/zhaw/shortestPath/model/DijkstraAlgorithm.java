package ch.zhaw.shortestPath.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class DijkstraAlgorithm implements IPathAlgorithm {
	private static List<Edge> originalGraph = new ArrayList<Edge>();
	private static List<Edge> whiteGraph = new ArrayList<Edge>();
	private static List<Edge> redGraph = new ArrayList<Edge>();
	private static List<Edge> greenGraph = new ArrayList<Edge>();
	private static List<Point> points = new ArrayList<Point>();
	private static List<Point> path = new ArrayList<Point>();
	private static List<Point> whitePoints = new ArrayList<Point>();
	private static List<Point> redPoints = new ArrayList<Point>();
	private static List<Point> greenPoints = new ArrayList<Point>();
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

		k1 = new Edge(A, B, 2);
		k2 = new Edge(A, F, 9);
		k3 = new Edge(A, G, 15);
		k4 = new Edge(B, C, 4);
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

	public static Edge getEdge(Point from, Point to) {
		Edge output = null;
		for (Edge ed : originalGraph) {
			if (ed.from == from && ed.to == to) {
				output = ed;
			}
		}
		return output;
	}

	public static void switchRedToGreen(Point point) {
		redPoints.remove(point);
		greenPoints.add(point);
	}

	public static void removeDuplicatePoints(List<Point> list) {
		HashSet<Point> hashSet = new HashSet<Point>(list);
		list.clear();
		list.addAll(hashSet);
	}

	public static int getLengthFromTo(Point from, Point to) {
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

	public static void main(String[] args) {
		createTestSzenario();
		work(originalGraph, A);
	}

	public static void work(List<Edge> graph, Point start) {

		// try
		greenPoints.add(start);

		path.add(start);

		for (Point next : start.getNext()) {
			// try
			redPoints.add(next);

			int distanceStartNext = getLengthFromTo(start, next);
			next.setDistance(distanceStartNext);
		}

		for (Point red : redPoints) {
			Edge edge = getEdge(start, red);
			greenGraph.add(edge);
		}

		// sort the red points
		Collections.sort(redPoints, sortByDistance);

		Point nextPoint = redPoints.get(0);
		switchRedToGreen(nextPoint);

		for (Point next : nextPoint.getNext()) {
			// try
			redPoints.add(next);

			Point latest = nextPoint.getBefore();
			int distanceLatestNext = getLengthFromTo(latest, next);
			System.out.println(next.getDistance());
			System.out.println(next.getName());
			System.out.println(distanceLatestNext);
			// next.setDistance(latest.getDistance() + distanceLatestNext);
		}

		// only one same point in the list...
		removeDuplicatePoints(redPoints);

		System.out.println("red points");
		for (Point red : redPoints) {
			System.out.println(red.getName());
		}
		System.out.println("green points");
		for (Point green : greenPoints) {
			System.out.println(green.getName());
		}
		System.out.println("green edges");
		for (Edge g : greenGraph) {
			System.out.println(g.getFrom().getName() + " - "
					+ g.getTo().getName() + "");
		}
		System.out.println("red edges");
		for (Edge g : redGraph) {
			System.out.println(g.getFrom().getName() + " - "
					+ g.getTo().getName() + "");
		}

		// im worst case muss der algo (anzahl konten * anzahl kanten)
		// durchlaufen (bellmann-ford)
		// int maximalRuns = graph.size() * punkte.size();

		// im worst case muss der algo (anzahl knoten * anzahl anzahl)
		// durchlaufen (Dijkstra)
		int maximalRuns = points.size() * points.size();

		for (int i = 0; i < maximalRuns; i++) {

			Collections.sort(greenPoints, sortByDistance);

			if (greenPoints.size() == 0) {
				break;
			}

			path.add(greenPoints.get(0));
			Point latest = path.get(path.size() - 1);
			Point before = path.get(path.size() - 2);
			latest.setBefore(before);
			greenPoints.clear();

			for (Point next : latest.getNext()) {
				int distanceLatestNext = getLengthFromTo(latest, next);
				next.setDistance(latest.getDistance() + distanceLatestNext);
				greenPoints.add(next);
			}

		}

		for (int i = 1; i < path.size(); i++) {
			String from = path.get(i).getBefore().getName();
			String to = path.get(i).getName();
			System.out.println("Der Weg geht ueber " + from + " zu " + to);
		}
	}

	@Override
	public void CalculateAlgorithm(ArrayList<Node> nodeList,
			ArrayList<Connector> connectorList) {
	}

}
