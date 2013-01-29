package ch.zhaw.shortestPath.view;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.AnnotationLayer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.MarkerLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.render.Annotation;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfaceShape;
import gov.nasa.worldwind.render.markers.BasicMarker;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.BasicMarkerShape;
import gov.nasa.worldwind.render.markers.Marker;
import gov.nasa.worldwind.util.measure.LengthMeasurer;
import gov.nasa.worldwindx.examples.util.DirectedPath;
import gov.nasa.worldwindx.examples.util.LabeledPath;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import ch.zhaw.shortestPath.model.Connector;
import ch.zhaw.shortestPath.model.Node;

public class ConnectorBuilder extends AVListImpl {
	private SurfaceShape circle;
	private final WorldWindow wwd;
	private boolean armed = false;
	private ArrayList<Position> positions = new ArrayList<Position>();
	private LengthMeasurer measurer;
	private ArrayList<Connector> lines;
	private final RenderableLayer layer;
	private Connector line = null;
	private boolean active = false;
	private int clickCount = 0;
	private AnnotationLayer anLayer;
	private AnnotationAttributes defaultAttributes;
	private AnnotationAttributes attrs;
	private AnnotationAttributes geoAttr;
	private MarkerLayer markerLayer;
	private ArrayList<Marker> markers;

	/**
	 * Construct a new line builder using the specified polyline and layer and
	 * drawing events from the specified world window. Either or both the
	 * polyline and the layer may be null, in which case the necessary object is
	 * created.
	 * 
	 * @param wwd
	 *            the world window to draw events from.
	 * @param lineLayer
	 *            the layer holding the polyline. May be null, in which case a
	 *            new layer is created.
	 * @param polyline
	 *            the polyline object to build. May be null, in which case a new
	 *            polyline is created.
	 */
	public ConnectorBuilder(final WorldWindow wwd, RenderableLayer lineLayer) {
		
		this.wwd = wwd;
		LayerList layers = this.wwd.getModel().getLayers();
		this.anLayer = (AnnotationLayer) this.wwd.getModel().getLayers().getLayerByName("Label Layer");
		this.measurer = new LengthMeasurer();
		
		markerLayer = (MarkerLayer) this.wwd.getModel().getLayers().getLayerByName("marker Layer");
		
        defaultAttributes = new AnnotationAttributes();
        defaultAttributes.setCornerRadius(10);
        defaultAttributes.setInsets(new Insets(8, 8, 8, 8));
        defaultAttributes.setBackgroundColor(new Color(0f, 0f, 0f, .5f));
        defaultAttributes.setTextColor(Color.WHITE);
        //defaultAttributes.setDrawOffset(new Point(25, 25));
        defaultAttributes.setDistanceMinScale(.5);
        defaultAttributes.setDistanceMaxScale(2);
        defaultAttributes.setDistanceMinOpacity(.5);
        //defaultAttributes.setLeaderGapWidth(14);
        defaultAttributes.setDrawOffset(new Point(20, 40));
        
        attrs = new AnnotationAttributes();
        attrs.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
        attrs.setFrameShape(AVKey.SHAPE_RECTANGLE);
        attrs.setDrawOffset(new Point(0, 5));
        attrs.setLeaderGapWidth(1);
        attrs.setTextColor(Color.BLACK);
        attrs.setBackgroundColor(new Color(1f, 1f, 1f, 0.8f));
        attrs.setCornerRadius(5);
        attrs.setBorderColor(new Color(0xababab));
        attrs.setFont(Font.decode("Arial-PLAIN-12"));
        attrs.setTextAlign(AVKey.CENTER);
        attrs.setInsets(new Insets(5, 5, 5, 5));
        
        geoAttr = new AnnotationAttributes();
        geoAttr.setDefaults(defaultAttributes);
        geoAttr.setFrameShape(AVKey.SHAPE_NONE);  // No frame
        geoAttr.setFont(Font.decode("Arial-PLAIN-14"));
        geoAttr.setTextColor(Color.BLACK);
        geoAttr.setTextAlign(AVKey.CENTER);
        geoAttr.setDrawOffset(new Point(0, 5)); // centered just above
       // geoAttr.setEffect(AVKey.TEXT_EFFECT_OUTLINE);  // Black outline
        geoAttr.setBackgroundColor(Color.BLACK);
        
		this.lines = new ArrayList<Connector>();
		this.layer = lineLayer;
		//this.wwd.getModel().getLayers().add(this.layer);
		this.wwd.getInputHandler().addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent mouseEvent) {
				
				if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1) {
					if (armed
							&& (MouseEvent.BUTTON1_DOWN_MASK) != 0) {

							active = true;
							addPosition();
						
					}
					mouseEvent.consume();
				}
			}
		});

		this.wwd.addPositionListener(new PositionListener() {
			public void moved(PositionEvent event) {
				if (!active)
					return;
				replacePosition();
			}
		});
	}
	
	public Path testPath(List<Position> pathPosition){
        // Create and set an attribute bundle.
		this.layer.removeRenderable(line);
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setOutlineMaterial(Material.BLACK);
        attrs.setOutlineWidth(2d);

        // Create a path, set some of its properties and set its attributes.
        ArrayList<Position> pathPositions = new ArrayList<Position>();
        
        for(Position pos : pathPosition)
        {
        	pathPositions.add(pos);
        }

        Path path = new DirectedPath(pathPositions);
        
        // To ensure that the arrowheads resize smoothly, refresh each time the path is drawn.
        path.setAttributes(attrs);
        path.setFollowTerrain(true);
        path.setVisible(true);
        path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        path.setPathType(AVKey.LINEAR);
        this.layer.addRenderable(path);
        this.wwd.redraw();
        return path;
	}
	
	public List<Connector> getAllConnector(){
		return this.lines;
	}

	/**
	 * Returns the layer holding the polyline being created.
	 * 
	 * @return the layer containing the polyline.
	 */
	public RenderableLayer getLayer() {
		return this.layer;
	}

	/**
	 * Removes all positions from the polyline.
	 */
	public void clear() {

	}

	/**
	 * Identifies whether the line builder is armed.
	 * 
	 * @return true if armed, false if not armed.
	 */
	public boolean isArmed() {
		return this.armed;
	}

	/**
	 * Arms and disarms the line builder. When armed, the line builder monitors
	 * user input and builds the polyline in response to the actions mentioned
	 * in the overview above. When disarmed, the line builder ignores all user
	 * input.
	 * 
	 * @param armed
	 *            true to arm the line builder, false to disarm it.
	 */
	public void setArmed(boolean armed) {
		this.armed = armed;
	}

	private void addPosition() {
		// this is the way to get the object at one point
		PickedObjectList list = this.wwd.getObjectsAtCurrentPosition();
		Node currentNode = null;
		
		for(PickedObject obj : list){
			if (obj.getObject() instanceof Node) {
				currentNode = (Node) obj.getObject();
			}
		}
		if(currentNode==null){
			active = false;
			this.positions.clear();
			this.lines.remove(this.line);
			if(this.line==null){
				return;
			}
			this.layer.removeRenderable(this.line);
			this.line = null;
			return;
		}
		
		if(this.line == null){
			this.line = new Connector();
			
		}
		
		if (this.positions.size() < 2) {
			this.line.setFrom(currentNode);
			Position oldPosition = (Position)currentNode.getReferencePosition();
			Position newPosition = new Position(oldPosition.latitude,oldPosition.longitude,oldPosition.getAltitude());
			this.positions.add(newPosition);
			this.positions.add(newPosition);
			this.line.setPositions(this.positions);
			this.layer.addRenderable(line);
		}else if (this.positions.size() == 2) {
			Position oldPosition = (Position)currentNode.getReferencePosition();
			Position newPosition = new Position(oldPosition.latitude,oldPosition.longitude,oldPosition.getAltitude()+20.0d);
			this.positions.set(1,newPosition);
			this.line.setPositions(this.positions);
			this.line.setTo(currentNode);
			this.line.getFrom().setEdge(line);
			this.line.getFrom().setNext(currentNode);
			measurer.setPositions((ArrayList<? extends Position>) line.getPositions());
			this.line.setDistance(measurer.getLength(this.wwd.getModel().getGlobe()));
			Position middle = this.calcMiddle(this.positions.get(0),this.positions.get(1));
			GlobeAnnotation anno = new GlobeAnnotation(Integer.toString((int) this.line.getDistance()/100),middle, this.geoAttr);
			anno.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
			anLayer.addAnnotation(anno);
			this.lines.add(this.line);
			Path newPath = this.testPath(this.positions);
			//this.layer.addRenderable(this.line);
			this.line.setPath((DirectedPath) newPath);
			this.line = null;
			//this.layer.addRenderables(lines);
			//this.layer.addRenderable(this.line);
			
			this.positions.clear();
			this.active = false;
			
		}

		
		//LabeledPath label = new LabeledPath(positions, this.makeLabelAnnotation("test"));
		//label.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
		//this.layer.addRenderable(label);
		
		this.firePropertyChange("LineBuilder.AddPosition", null, currentNode.getCenter());
		this.wwd.redraw();
	}
	
    protected ScreenAnnotation makeLabelAnnotation(String text)
    {
        ScreenAnnotation ga = new ScreenAnnotation(text, new Point());
        ga.setPickEnabled(false);

        AnnotationAttributes attrs = new AnnotationAttributes();
        attrs.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
        attrs.setFrameShape(AVKey.SHAPE_RECTANGLE);
        attrs.setDrawOffset(new Point(0, 10));
        attrs.setLeaderGapWidth(5);
        attrs.setTextColor(Color.BLACK);
        attrs.setBackgroundColor(new Color(1f, 1f, 1f, 0.8f));
        attrs.setCornerRadius(5);
        attrs.setBorderColor(new Color(0xababab));
        attrs.setFont(Font.decode("Arial-PLAIN-12"));
        attrs.setTextAlign(AVKey.CENTER);
        attrs.setInsets(new Insets(5, 5, 5, 5));

        ga.setAttributes(attrs);

        return ga;
    }
    
    public void paintConnectors(List<Node> nodeList){
    	for(Connector con : this.getAllConnector()){
            ShapeAttributes attrs = new BasicShapeAttributes();
            attrs.setOutlineMaterial(Material.BLACK);
            attrs.setOutlineWidth(2d);
    		con.getPath().setAttributes(attrs);
    	}
    	
		for(int i = 0;i<nodeList.size()-1;i++){
			Node currentNode = nodeList.get(i);
			for(Connector edge: currentNode.getEdge()){
				if(edge.getTo()==nodeList.get(i+1)){
		            ShapeAttributes attrs = new BasicShapeAttributes();
		            attrs.setOutlineMaterial(Material.GREEN);
		            attrs.setOutlineWidth(2d);
		            edge.getPath().setAttributes(attrs);
					this.wwd.redraw();
				}
			}
		}

    }

	private void replacePosition() {
		Position curPos = this.wwd.getCurrentPosition();
		if (curPos == null)
			return;
		
		Position newPosition = new Position(curPos.latitude,curPos.longitude,curPos.getAltitude());
		
		int index = this.positions.size() - 1;
		if (index < 0)
			index = 0;

		Position currentLastPosition = this.positions.get(index);
		this.positions.set(index, newPosition);
		this.line.setPositions(this.positions);
		this.firePropertyChange("LineBuilder.ReplacePosition",
				currentLastPosition, curPos);
		this.wwd.redraw();
	}
	
	private Position calcMiddle(Position pos1,Position pos2){
		
		Angle newLatitude2 = Angle.fromDegrees((pos1.latitude.degrees + pos2.latitude.degrees)/2.0);
		Angle newLongitude2 = Angle.fromDegrees((pos1.longitude.degrees + pos2.longitude.degrees)/2.0);
		double height = wwd.getModel().getGlobe().getElevation(newLatitude2,newLongitude2);
		
		return new Position(newLatitude2,newLongitude2,height);
	}
}