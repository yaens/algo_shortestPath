package ch.zhaw.shortestPath.model;

import java.awt.Color;

import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolyline;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.BasicMarkerShape;

public class Connector extends Polyline{
	double distance;
	Node from;
	Node to;

	Connector(Node from, Node to, double distance) {
		this.from = from;
		this.to = to;
		this.distance = distance;
	}
	
	public Connector(){
		super();
		this.color = Color.BLACK;
		this.lineWidth = 3.0;
		this.setFollowTerrain(true);
	}
	
	public Connector(Node from){
		super();
		this.setFrom(from);
		//this.setFollowTerrain(true);
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setOutlineMaterial(Material.RED);
        attrs.setOutlineWidth(2d);
        attrs.setOutlineWidth(2.0);
        
       
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Node getFrom() {
		return from;
	}

	public void setFrom(Node from) {
		this.from = from;
	}

	public Node getTo() {
		return to;
	}

	public void setTo(Node to) {
		this.to = to;
	}

}
