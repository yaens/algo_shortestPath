package ch.zhaw.shortestPath.model;

import java.util.ArrayList;

public interface IPathAlgorithm {
	void CalculateAlgorithm(ArrayList<Node> nodeList,ArrayList<Connector> connectorList);
}
