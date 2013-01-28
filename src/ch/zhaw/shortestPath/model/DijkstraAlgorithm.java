package ch.zhaw.shortestPath.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DijkstraAlgorithm implements IPathAlgorithm {
	private static List<Connector> originalGraph = new ArrayList<Connector>();
	private static List<Node> points = new ArrayList<Node>();
	private static List<Node> redPoints = new ArrayList<Node>();
	private static List<Node> greenPoints = new ArrayList<Node>();
	private static List<Node> shortestPath = new ArrayList<Node>();
	private static Node A;
	private static Node B;
	private static Node C;
	private static Node D;
	private static Node E;
	private static Node F;
	private static Node G;
	private static Node H;
	private static Node I;
	private static Connector k1;
	private static Connector k2;
	private static Connector k3;
	private static Connector k4;
	private static Connector k5;
	private static Connector k6;
	private static Connector k7;
	private static Connector k8;
	private static Connector k9;
	private static Connector k10;
	private static Connector k11;
	private static Connector k12;
	private static Connector k13;
	private static Connector k14;
	private static Connector k15;

	static Comparator<Node> sortByDistance = new Comparator<Node>() {
		public int compare(Node point1, Node point2) {
			return (int) (point1.getDistance() - point2.getDistance());
		}
	};

	public static void createTestSzenario() {

		// a1, b2, c3, d4, e5, f6, g7, h8, i9
		A = new Node("A");
		B = new Node("B");
		C = new Node("C");
		D = new Node("D");
		E = new Node("E");
		F = new Node("F");
		G = new Node("G");
		H = new Node("H");
		I = new Node("I");

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
		k1 = new Connector(A, B, 2);
		k2 = new Connector(A, F, 9);
		k3 = new Connector(A, G, 15);
		k4 = new Connector(B, C, 2);
		k5 = new Connector(B, G, 6);
		k6 = new Connector(C, I, 15);
		k7 = new Connector(C, D, 2);
		k8 = new Connector(D, I, 2);
		k9 = new Connector(D, E, 1);
		k10 = new Connector(E, H, 3);
		k11 = new Connector(E, F, 6);
		k12 = new Connector(F, H, 11);
		k13 = new Connector(G, H, 15);
		k14 = new Connector(G, I, 2);
		k15 = new Connector(H, I, 9);

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

	public static void changePointToRed(Node point) {
		if (point.getStatus()) {
			redPoints.add(point);
			greenPoints.remove(point);
		}
		point.setRedStatus();
	}

	public static void changePointToGreen(Node point) {
		redPoints.remove(point);
		if (!point.getStatus()) {
			greenPoints.add(point);
		}
		point.setGreenStatus();
	}

	public static double getDistanceFromTo(Node from, Node to, List<Connector> orgGraph) {
		originalGraph = orgGraph;
		double output = 0;
		for (Connector ed : originalGraph) {
			Node start = ed.getFrom();
			Node end = ed.getTo();
			if (start == from && end == to) {
				output = ed.getDistance();
			}
		}
		return output;
	}

	public static List<Node> getShortestPath(Node from, Node to) {
		System.out.println("kuerzester Pfad:");
		shortestPath.clear();
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
			Node next = to.getBefore();
			to = next;
		}

		// change the order for the list
		Collections.reverse(shortestPath);

		for (Node point : shortestPath) {
			System.out.println(point.getName());
		}
		
		return shortestPath;

	}

	public static void work(List<Connector> graph, Node start, List<Node> allPoints) {

		points = allPoints;
		
		// preparation for all points
		for (Node all : points) {
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
				for (Node next : start.getNext()) {

					// get distance from start to neighboors
					double distanceStartToNext = getDistanceFromTo(start, next,graph);

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
			Node nextPoint = redPoints.get(0);

			// make the nextPoint green
			changePointToGreen(nextPoint);

			// loop trough all neighboors
			for (Node next : nextPoint.getNext()) {

				// make the point red
				changePointToRed(next);

				// get distance between the neighboors
				double distanceNextPointToNext = getDistanceFromTo(nextPoint, next,graph);

				// count the new distance
				double tmpDistance = (nextPoint.getDistance() + distanceNextPointToNext);

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
		for (Node all : points) {
			System.out.println(all.getName() + "     -      "
					+ all.getBefore().getName() + "      -      "
					+ all.getDistance());
		}

		// show all red points
		System.out.println("red points");
		if (redPoints.size() == 0) {
			System.out.println("-- empty --");
		}
		for (Node red : redPoints) {
			System.out.println(red.getName());
		}

		// show all green points
		System.out.println("green points");
		if (greenPoints.size() == 0) {
			System.out.println("-- empty --");
		}
		for (Node green : greenPoints) {
			System.out.println(green.getName());
		}

	}

	public static void main(String[] args) {
		createTestSzenario();
		//work(originalGraph, A);
		getShortestPath(A, I);
	}

	@Override
	public void CalculateAlgorithm(ArrayList<Node> nodeList,
			ArrayList<Connector> connectorList) {
	}

}
