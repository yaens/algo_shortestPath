package ch.zhaw.shortestPath.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DijkstraAlgorithm implements IPathAlgorithm {
	private static List<Kante> graph = new ArrayList<Kante>();
	private static List<Punkt> punkte = new ArrayList<Punkt>();
	private static List<Punkt> weg = new ArrayList<Punkt>();
	private static List<Punkt> tmplist = new ArrayList<Punkt>();
	private static int distanceToStart;
	private static Punkt A;
	private static Punkt B;
	private static Punkt C;
	private static Punkt D;
	private static Punkt E;
	private static Punkt F;
	private static Punkt G;
	private static Punkt H;
	private static Punkt I;
	private static Kante k1;
	private static Kante k2;
	private static Kante k3;
	private static Kante k4;
	private static Kante k5;
	private static Kante k6;
	private static Kante k7;
	private static Kante k8;
	private static Kante k9;
	private static Kante k10;
	private static Kante k11;
	private static Kante k12;
	private static Kante k13;
	private static Kante k14;
	private static Kante k15;

	@SuppressWarnings("rawtypes")
	static Comparator pascal = new Comparator() {
		public int compare(Object o1, Object o2) {
			Punkt p1 = (Punkt) o1;
			Punkt p2 = (Punkt) o2;
			return p1.getDistance() - p2.getDistance();
		}
	};

	public static void createTestSzenario() {

		// a1, b2, c3, d4, e5, f6, g7, h8, i9
		A = new Punkt("A");
		B = new Punkt("B");
		C = new Punkt("C");
		D = new Punkt("D");
		E = new Punkt("E");
		F = new Punkt("F");
		G = new Punkt("G");
		H = new Punkt("H");
		I = new Punkt("I");

		punkte.add(A);
		punkte.add(B);
		punkte.add(C);
		punkte.add(D);
		punkte.add(E);
		punkte.add(F);
		punkte.add(G);
		punkte.add(H);
		punkte.add(I);

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

		k1 = new Kante(A, B, 2);
		k2 = new Kante(A, F, 9);
		k3 = new Kante(A, G, 15);
		k4 = new Kante(B, C, 4);
		k5 = new Kante(B, G, 6);
		k6 = new Kante(C, I, 15);
		k7 = new Kante(C, D, 2);
		k8 = new Kante(D, I, 2);
		k9 = new Kante(D, E, 1);
		k10 = new Kante(E, H, 3);
		k11 = new Kante(E, F, 6);
		k12 = new Kante(F, H, 11);
		k13 = new Kante(G, H, 15);
		k14 = new Kante(G, I, 2);
		k15 = new Kante(H, I, 9);

		graph.add(k1);
		graph.add(k2);
		graph.add(k3);
		graph.add(k4);
		graph.add(k5);
		graph.add(k6);
		graph.add(k7);
		graph.add(k8);
		graph.add(k9);
		graph.add(k10);
		graph.add(k11);
		graph.add(k12);
		graph.add(k13);
		graph.add(k14);
		graph.add(k15);

	}

	public static int getLength(Punkt from, Punkt to) {
		int output = 0;
		for (Kante kant : graph) {
			Punkt start = kant.getFrom();
			Punkt end = kant.getTo();
			if (start == from && end == to) {
				output = kant.getDistance();
			}
		}
		return output;
	}

	public static void main(String[] args) {
		createTestSzenario();
		work(graph, A);
	}

	@SuppressWarnings("unchecked")
	public static String work(List<Kante> graph, Punkt start) {
		String output = "nichts";
		distanceToStart = 0;

		weg.add(start);

		for (Punkt next : start.getNext()) {
			next.setDistance(distanceToStart + getLength(start, next));
			tmplist.add(next);
		}

		for (int i = 0; i < graph.size(); i++) {

			Collections.sort(tmplist, pascal);

			if (tmplist.size() == 0) {
				break;
			}

			weg.add(tmplist.get(0));
			Punkt latest = weg.get(weg.size() - 1);
			tmplist.clear();

			for (Punkt next : latest.getNext()) {
				next.setDistance(latest.getDistance() + getLength(latest, next));
				tmplist.add(next);
			}

		}

		for (Punkt pascal : weg) {
			System.out.println(pascal.getName());
		}

		return output;
	}

	@Override
	public void CalculateAlgorithm(ArrayList<Node> nodeList,
			ArrayList<Connector> connectorList) {
	}

}
