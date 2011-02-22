package org.jboss.bpmn2.editor.core.features.subprocess;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.jboss.bpmn2.editor.core.ImageProvider;
import org.jboss.bpmn2.editor.core.ModelHandler;

public class SubProcessFeatureContainer extends AbstractSubProcessFeatureContainer {

	@Override
    public boolean canApplyTo(BaseElement element) {
	    return element instanceof SubProcess;
    }

	@Override
    public ICreateFeature getCreateFeature(IFeatureProvider fp) {
	    return new CreateSubProcessFeature(fp);
    }

	@Override
    public IAddFeature getAddFeature(IFeatureProvider fp) {
	    return new AddSubprocessFeature(fp);
	}
	
	public static class CreateSubProcessFeature extends AbstractCreateSubProcess {

		public CreateSubProcessFeature(IFeatureProvider fp) {
	        super(fp, "Sub-Process", "Inner activity");
        }

		@Override
		protected SubProcess createFlowElement(ICreateContext context) {
			SubProcess subProcess = ModelHandler.FACTORY.createSubProcess();
			subProcess.setName("SubProcess");
			return subProcess;
		}
		
		@Override
        protected String getStencilImageId() {
	        return ImageProvider.IMG_16_SUB_PROCESS;
        }
	}
}