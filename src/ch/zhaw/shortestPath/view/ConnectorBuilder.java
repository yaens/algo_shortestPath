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
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.render.Annotation;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.render.SurfaceShape;
import gov.nasa.worldwind.util.measure.LengthMeasurer;
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
        geoAttr.setFont(Font.decode("Arial-PLAIN-12"));
        geoAttr.setTextColor(Color.BLACK);
        geoAttr.setTextAlign(AVKey.CENTER);
        geoAttr.setDrawOffset(new Point(0, 5)); // centered just above
        //geoAttr.setEffect(AVKey.TEXT_EFFECT_OUTLINE);  // Black outline
        geoAttr.setBackgroundColor(Color.BLACK);
        
		
		this.lines = new ArrayList<Connector>();
		this.layer = lineLayer != null ? lineLayer : new RenderableLayer();
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
	 * Returns the layer currently used to display the polyline.
	 * 
	 * @return the layer holding the polyline.
	 */
	public Connector getLine() {
		return this.line;
	}

	/**
	 * Removes all positions from the polyline.
	 */
	public void clear() {
		while (this.positions.size() > 0)
			this.removePosition();
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
			this.line = new Connector(currentNode);
			this.layer.addRenderable(this.line);
		}
		
		if (this.positions.size() < 2) {
			this.positions.add((Position) currentNode.getCenter());
			this.positions.add((Position) currentNode.getCenter());
			this.line.setPositions(this.positions);
		}else if (this.positions.size() == 2) {
			this.positions.set(1,(Position) currentNode.getCenter());
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
			this.line = new Connector(currentNode);
			this.layer.addRenderable(this.line);
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

	private void replacePosition() {
		Position curPos = this.wwd.getCurrentPosition();
		if (curPos == null)
			return;

		int index = this.positions.size() - 1;
		if (index < 0)
			index = 0;

		Position currentLastPosition = this.positions.get(index);
		this.positions.set(index, curPos);
		this.line.setPositions(this.positions);
		this.firePropertyChange("LineBuilder.ReplacePosition",
				currentLastPosition, curPos);
		this.wwd.redraw();
	}
	
	private Position calcMiddle(Position pos1,Position pos2){
		/*Vec4 xyz1 = this.wwd.getModel().getGlobe().computePointFromPosition(pos1);
		Vec4 xyz2 = this.wwd.getModel().getGlobe().computePointFromPosition(pos2);
		
		double newX = (xyz1.x + xyz2.x) / 2;
		double newY = (xyz1.y + xyz2.y) / 2;
		double newZ = xyz1.z;
		
		Vec4 xyzNew = new Vec4(newX, newY, newZ);*/
		
		Angle newLatitude = (pos1.latitude.add(pos2.latitude).divide(2));
		Angle newLongitude = (pos1.longitude.add(pos2.longitude).divide(2));
		Angle newLatitude2 = Angle.fromDegrees((pos1.latitude.degrees + pos2.latitude.degrees)/2.0);
		Angle newLongitude2 = Angle.fromDegrees((pos1.longitude.degrees + pos2.longitude.degrees)/2.0);
		//return this.wwd.getModel().getGlobe().computePositionFromPoint(xyzNew);
		//return this.wwd.getModel().getGlobe()(newLatitude, newLatitude, pos1.getAltitude());
		return new Position(newLatitude2,newLongitude2,1000);
	}

	private void removePosition() {
		if (this.positions.size() == 0)
			return;

		Position currentLastPosition = this.positions
				.get(this.positions.size() - 1);
		this.positions.remove(this.positions.size() - 1);
		this.line.setPositions(this.positions);
		this.firePropertyChange("LineBuilder.RemovePosition",
				currentLastPosition, null);
		this.wwd.redraw();
	}
}