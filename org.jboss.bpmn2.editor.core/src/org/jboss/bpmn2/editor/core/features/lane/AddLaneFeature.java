package org.jboss.bpmn2.editor.core.features.lane;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ITargetContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.AdaptedGradientColoredAreas;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.PredefinedColoredAreas;
import org.jboss.bpmn2.editor.core.features.AbstractBpmnAddFeature;
import org.jboss.bpmn2.editor.core.features.FeatureSupport;
import org.jboss.bpmn2.editor.core.features.StyleUtil;

public class AddLaneFeature extends AbstractBpmnAddFeature {

	public AddLaneFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		boolean isLane = context.getNewObject() instanceof Lane;
		boolean intoDiagram = context.getTargetContainer().equals(getDiagram());
		boolean intoLane = FeatureSupport.isTargetLane(context);
		boolean intoParticipant = FeatureSupport.isTargetParticipant(context);
		return isLane && (intoDiagram || intoLane || intoParticipant);
	}

	@Override
	public PictogramElement add(IAddContext context) {
		Lane lane = (Lane) context.getNewObject();

		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(context.getTargetContainer(), true);
		IGaService gaService = Graphiti.getGaService();

		int width = context.getWidth() > 0 ? context.getWidth() : 600;
		int height = context.getHeight() > 0 ? context.getHeight() : 100;

		Rectangle rect = gaService.createRectangle(containerShape);
		rect.setStyle(StyleUtil.getStyleForClass(getDiagram()));
		AdaptedGradientColoredAreas gradient = PredefinedColoredAreas.getBlueWhiteAdaptions();
		gaService.setRenderingStyle(rect, gradient);

		if (FeatureSupport.isTargetLane(context)) {
			GraphicsAlgorithm ga = context.getTargetContainer().getGraphicsAlgorithm();

			if (getNumberOfLanes(context) == 1) {
				gaService.setLocationAndSize(rect, 15, 0, ga.getWidth() - 15, ga.getHeight());
				for (Shape s : getFlowNodeShapes(context, lane)) {
					Graphiti.getPeService().sendToFront(s);
					s.setContainer(containerShape);
				}
			} else {
				gaService.setLocationAndSize(rect, 15, ga.getWidth() - 1, ga.getHeight() - 15, height);
			}
			containerShape.setContainer(context.getTargetContainer());
		} else {
			gaService.setLocationAndSize(rect, context.getX(), context.getY(), width, height);
		}

		Shape textShape = peCreateService.createShape(containerShape, false);
		Text text = gaService.createText(textShape, lane.getName());
		text.setStyle(StyleUtil.getStyleForText(getDiagram()));
		text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
		text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
		text.setAngle(-90);
		gaService.setLocationAndSize(text, 0, 0, 15, height);

		createDIShape(containerShape, lane);
		link(textShape, lane);

		peCreateService.createChopboxAnchor(containerShape);
		layoutPictogramElement(containerShape);

		if (FeatureSupport.isTargetLane(context)) {
			FeatureSupport.redraw(context.getTargetContainer());
		}
		return containerShape;
	}

	private List<Shape> getFlowNodeShapes(IAddContext context, Lane lane) {
		List<FlowNode> nodes = lane.getFlowNodeRefs();
		List<Shape> shapes = new ArrayList<Shape>();
		for (Shape s : context.getTargetContainer().getChildren()) {
			Object bo = getBusinessObjectForPictogramElement(s);
			if (bo != null && nodes.contains(bo)) {
				shapes.add(s);
			}
		}
		return shapes;
	}

	private int getNumberOfLanes(ITargetContext context) {
		ContainerShape targetContainer = context.getTargetContainer();
		Lane lane = (Lane) getBusinessObjectForPictogramElement(targetContainer);
		return lane.getChildLaneSet().getLanes().size();
	}
}