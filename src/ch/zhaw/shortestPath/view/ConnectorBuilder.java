package ch.zhaw.shortestPath.view;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.SurfaceShape;
import gov.nasa.worldwind.util.measure.LengthMeasurer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

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
		
		this.measurer = new LengthMeasurer();
		
		//this.line.(10);
		this.lines = new ArrayList<Connector>();
		this.layer = lineLayer != null ? lineLayer : new RenderableLayer();
		//this.layer.addRenderable(this.line);
		this.wwd.getModel().getLayers().add(this.layer);

		this.wwd.getInputHandler().addMouseListener(new MouseAdapter() {
			//public void mousePressed(MouseEvent mouseEvent) {
				//mouseEvent.consume();
			//}

			/*public void mouseReleased(MouseEvent mouseEvent) {
				if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1) {
					if (positions.size() == 1)
						//removePosition();
					active = false;
					mouseEvent.consume();
				}
			}*/

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
			this.line.setToNode(currentNode);
			measurer.setPositions((ArrayList<? extends Position>) line.getPositions());
			this.line.setLenght(measurer.getLength(this.wwd.getModel().getGlobe()));
			this.lines.add(this.line);
			this.line = new Connector(currentNode);
			this.layer.addRenderable(this.line);
			this.positions.clear();
			this.active = false;
		}

		// this.positions.add(curPos);
		// this.line.setPositions(this.positions);

		this.firePropertyChange("LineBuilder.AddPosition", null, currentNode.getCenter());
		this.wwd.redraw();
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