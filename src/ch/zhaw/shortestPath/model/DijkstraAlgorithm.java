package ch.zhaw.shortestPath.model;

import java.util.ArrayList;

public class DijkstraAlgorithm implements IPathAlgorithm {
	private static ArrayList<String> punkte;
	static ArrayList<String> graph = new ArrayList<String>();
	static ArrayList<String> start = new ArrayList<String>();
	static ArrayList<String> ziel = new ArrayList<String>();
	static ArrayList<String> weg = new ArrayList<String>();

	public static void createTestSzenario() {
		punkte = new ArrayList<String>();
		graph = new ArrayList<String>();
		weg = new ArrayList<String>();

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

		graph.add("A-B-2");
		graph.add("A-F-9");
		graph.add("A-G-15");
		graph.add("B-C-4");
		graph.add("B-G-6");
		graph.add("C-I-15");
		graph.add("C-D-2");
		graph.add("D-I-1");
		graph.add("D-E-1");
		graph.add("E-H-3");
		graph.add("E-F-6");
		graph.add("F-H-11");
		graph.add("G-H-15");
		graph.add("G-I-2");
		graph.add("H-I-9");

	}

	public static void count(ArrayList<String> weg) {

	}

	public static boolean reachable(String point) {
		boolean output = false;
		for (String kant : graph) {
			String ziel = kant.split("-")[1];
			if (ziel.equals(point)) {
				output = true;
			}
		}
		return output;
	}

	public static String getLength(String point) {
		String output = "nicht erreichbar";
		for (String kant : graph) {
			String start = kant.split("-")[0];
			String ziel = kant.split("-")[1];
			int laenge = Integer.parseInt(kant.split("-")[2]);
			if (ziel.equals(point)) {
				output = "von " + start + " nach " + ziel + ": " + laenge + " ";
			}
		}
		return output;
	}

	public static void buildShortestPath() {
		for (String kant : graph) {
			String ziel = kant.split("-")[1];
			if (reachable(ziel)) {
				System.out.println(getLength(ziel));
			}
		}

	}

	public static void main(String[] args) {
		createTestSzenario();
		buildShortestPath();
	}

	public static String doDijkstra(ArrayList<String> graph, String start) {
		String blaa = "haha";
		return blaa;
	}

	@Override
	public void CalculateAlgorithm(ArrayList<Node> nodeList,
			ArrayList<Connector> connectorList) {
	}

}
