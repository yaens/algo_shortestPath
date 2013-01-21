package ch.zhaw.shortestPath.model;

import java.util.ArrayList;

public class DijkstraAlgorithm implements IPathAlgorithm {

	public static void main(String[] args) {
		ArrayList<String> punkte = new ArrayList<String>();
		ArrayList<String> kanten = new ArrayList<String>();
		ArrayList<String> start = new ArrayList<String>();
		ArrayList<String> ziel = new ArrayList<String>();
		ArrayList<String> weg = new ArrayList<String>();

		// a1, b2, c3, d4, e5, f6, g7, h8, i9
		punkte.add("A");
		punkte.add("B");
		punkte.add("C");
		punkte.add("D");
		punkte.add("E");
		punkte.add("F");
		punkte.add("G");
		punkte.add("H");
		punkte.add("I");

		kanten.add("A-B-2");
		kanten.add("A-F-9");
		kanten.add("A-G-15");
		kanten.add("B-C-4");
		kanten.add("B-G-6");
		kanten.add("C-I-15");
		kanten.add("C-D-2");
		kanten.add("D-I-1");
		kanten.add("D-E-1");
		kanten.add("E-H-3");
		kanten.add("E-F-6");
		kanten.add("F-H-11");
		kanten.add("G-H-15");
		kanten.add("G-I-2");
		kanten.add("H-I-9");

		start.add("A");
		start.add("B");
		start.add("C");
		start.add("D");
		start.add("E");
		start.add("F");
		start.add("G");
		start.add("H");

		ziel.add("B");
		ziel.add("C");
		ziel.add("D");
		ziel.add("E");
		ziel.add("F");
		ziel.add("G");
		ziel.add("H");
		ziel.add("I");

		for (String kant : kanten) {
			String a = kant.split("-")[0];
			String b = kant.split("-")[1];
			int laenge = Integer.parseInt(kant.split("-")[2]);
			System.out.println("Weg nachbilden... " + a + "-" + b + "-"
					+ laenge);

			// for (String yyy : start) {
			// for (String zzz : ziel) {
			// if (yyy.equals(a) && zzz.equals(b)) {
			// System.out.println("Weg nachbilden... " + a + "-" + b
			// + "-" + laenge);
			// }
			// }
			// }

		}

	}

	@Override
	public void CalculateAlgorithm(ArrayList<Node> nodeList,
			ArrayList<Connector> connectorList) {
		// TODO Auto-generated method stub

	}

}
