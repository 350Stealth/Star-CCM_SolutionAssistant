package Assistant;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;
import star.base.neo.DoubleVector;
import star.common.*;
import star.dualmesher.DualMesherModel;
import star.extruder.GenCylConstantExtrusionValues;
import star.extruder.GenCylExtrusionType;
import star.extruder.GenCylModel;
import star.extruder.GenCylOption;
import star.meshing.*;
import star.prismmesher.NumPrismLayers;
import star.prismmesher.PrismLayerStretching;
import star.prismmesher.PrismMesherModel;
import star.prismmesher.PrismThickness;
import star.resurfacer.ResurfacerMeshingModel;
import star.vis.CurrentView;
import star.vis.PartDisplayer;
import star.vis.Scene;

@StarAssistantTask(display = "Generating the Mesh",
    contentPath = "XHTML/04_Meshing.xhtml",
    controller = Task04Meshing.MeshingTaskController.class)
public class Task04Meshing extends Task {
    
    public class MeshingTaskController extends FunctionTaskController {
        
        public void createMeshContinuum() {
            
            Simulation simulation = getSimulation();
            MeshContinuum meshContinuum =
                simulation.getContinuumManager().createContinuum(MeshContinuum.class);
            meshContinuum.enable(ResurfacerMeshingModel.class);
            meshContinuum.enable(DualMesherModel.class);
            meshContinuum.enable(GenCylModel.class);
            meshContinuum.enable(PrismMesherModel.class);
        }
        
        public void defineMeshParameters() {
            
            Simulation simulation = getSimulation();
            MeshContinuum meshContinuum =
                ((MeshContinuum) simulation.getContinuumManager().getContinuum("Mesh 1"));
            meshContinuum.getReferenceValues().get(BaseSize.class).setValue(8.0E-4);
            NumPrismLayers numPrismLayers =
                meshContinuum.getReferenceValues().get(NumPrismLayers.class);
            numPrismLayers.setNumLayers(5);
            PrismLayerStretching prismLayerStretching =
                meshContinuum.getReferenceValues().get(PrismLayerStretching.class);
            prismLayerStretching.setStretching(1.8);
            PrismThickness prismThickness =
                meshContinuum.getReferenceValues().get(PrismThickness.class);
            GenericRelativeSize genericRelativeSize =
                prismThickness.getRelativeSize();
            genericRelativeSize.setPercentage(45.0);
        }
        
        public void generateSurfaceMesh() {
            
            Simulation simulation =
                getActiveSimulation();
            MeshPipelineController meshPipelineController =
                simulation.get(MeshPipelineController.class);
            meshPipelineController.generateSurfaceMesh();
            simulation.getSceneManager().createGeometryScene("Mesh Scene", "Outline", "Mesh", 3);
            Scene scene =
                simulation.getSceneManager().getScene("Mesh Scene 1");
            scene.initializeAndWait();
            PartDisplayer partDisplayer =
                scene.getCreatorDisplayer();
            partDisplayer.initialize();
            partDisplayer = (PartDisplayer) scene.getDisplayerManager().getDisplayer("Mesh 1");
            partDisplayer.initialize();
            scene.open(true);
            CurrentView currentView =
                scene.getCurrentView();
            currentView.setInput(new DoubleVector(new double[]{0.07000000029802322, 0.02000000048428774, 0.0}),
                new DoubleVector(new double[]{-0.14607243684130425, 0.11741414039370678, 0.17860456694301519}),
                new DoubleVector(new double[]{0.20083797287207372, 0.9415226723669483, -0.2705534440210218}), 0.07747426272518879, 0);
        }
        
        public void generateVolumeMesh() {
            
            Simulation simulation =
                getActiveSimulation();
            Region region =
                simulation.getRegionManager().getRegion("Fluid");
            Boundary boundary =
                region.getBoundaryManager().getBoundary("Faces");
            GenCylOption genCylOption =
                boundary.get(MeshConditionManager.class).get(GenCylOption.class);
            genCylOption.getType().setSelected(GenCylExtrusionType.CONSTANT);
            GenCylConstantExtrusionValues genCylConstantExtrusionValues =
                boundary.get(MeshValueManager.class).get(GenCylConstantExtrusionValues.class);
            genCylConstantExtrusionValues.setNumLayers(82);
            MeshPipelineController meshPipelineController =
                simulation.get(MeshPipelineController.class);
            meshPipelineController.generateVolumeMesh();
            Scene scene =
                simulation.getSceneManager().getScene("Mesh Scene 1");
            CurrentView currentView =
                scene.getCurrentView();
            currentView.setInput(new DoubleVector(new double[]{0.07000000029802322, 0.02000000048428774, 0.0}),
                new DoubleVector(new double[]{-0.14607243684130425, 0.11741414039370678, 0.17860456694301519}),
                new DoubleVector(new double[]{0.20083797287207372, 0.9415226723669483, -0.2705534440210218}), 0.07747426272518879, 0);
            PartDisplayer partDisplayer =
                ((PartDisplayer) scene.getDisplayerManager().getDisplayer("Mesh 1"));
            FvRepresentation fvRepresentation =
                ((FvRepresentation) simulation.getRepresentationManager().getObject("Volume Mesh"));
            partDisplayer.setRepresentation(fvRepresentation);
        }
    }
}
