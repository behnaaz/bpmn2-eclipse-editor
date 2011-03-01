package org.jboss.bpmn2.editor.core.features.lane;

import java.io.IOException;

import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.jboss.bpmn2.editor.core.Activator;
import org.jboss.bpmn2.editor.core.ModelHandler;
import org.jboss.bpmn2.editor.core.features.BusinessObjectUtil;

public class MoveFromDiagramToParticipantFeature extends MoveLaneFeature {

	public MoveFromDiagramToParticipantFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		Participant p = (Participant) BusinessObjectUtil.getFirstElementOfType(context.getTargetContainer(),
				Participant.class);

		if (getMovedLane(context).getFlowNodeRefs().isEmpty()) {
			return true;
		}

		if (p.getProcessRef() == null) {
			return true;
		}

		if (!p.getProcessRef().getLaneSets().isEmpty()) {
			return true;
		}

		return false;
	}

	@Override
	protected void internalMove(IMoveShapeContext context) {
		modifyModelStructure(context);
		support.redraw(context.getTargetContainer());
	}

	private void modifyModelStructure(IMoveShapeContext context) {
		try {
			Participant targetParticipant = (Participant) BusinessObjectUtil.getFirstElementOfType(
					context.getTargetContainer(), Participant.class);
			ModelHandler handler = support.getModelHanderInstance(getDiagram());
			Lane movedLane = getMovedLane(context);
			handler.moveLane(movedLane, targetParticipant);
			Participant internalParticipant = handler.getParticipant(getDiagram());
			LaneSet laneSet = null;
			for (LaneSet set : internalParticipant.getProcessRef().getLaneSets()) {
				if (set.getLanes().contains(movedLane)) {
					laneSet = set;
					break;
				}
			}
			if (laneSet != null) {
				laneSet.getLanes().remove(movedLane);
				if (laneSet.getLanes().isEmpty()) {
					internalParticipant.getProcessRef().getLaneSets().remove(laneSet);
				}

				Process process = targetParticipant.getProcessRef();
				if (process.getLaneSets().isEmpty()) {
					process.getLaneSets().add(ModelHandler.FACTORY.createLaneSet());
				}
				process.getLaneSets().get(0).getLanes().add(movedLane);
			}
		} catch (IOException e) {
			Activator.logError(e);
		}
	}
}
