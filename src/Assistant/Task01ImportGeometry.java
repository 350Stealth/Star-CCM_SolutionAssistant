package Assistant;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;
import star.base.neo.DoubleVector;
import star.common.GeometryPart;
import star.common.Simulation;
import star.common.SimulationPartManager;
import star.meshing.PartImportManager;
import star.vis.CurrentView;
import star.vis.PartDisplayer;
import star.vis.Scene;

import javax.swing.*;
import java.io.File;
import java.util.Collection;

@StarAssistantTask(display = "Import Geometry",
    contentPath = "XHTML/01_ImportGeometry.xhtml",
    controller = Task01ImportGeometry.ImportGeometryTaskController.class)
public class Task01ImportGeometry extends Task {
    
    public Task01ImportGeometry() {
    }
    
    public class ImportGeometryTaskController extends FunctionTaskController {
        
        public void importSurfaceMeshDialog() {
            
            //open a file chooser
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File cadFile = fileChooser.getSelectedFile();
            
            // import the part
            Simulation simulation01 = getSimulation();
            PartImportManager partImportManager01 = simulation01.get(PartImportManager.class);
            
            // use default import options
            partImportManager01.importCadPart(cadFile.getPath(), "SharpEdges", 30.0, 2, true, 1.0E-5, true, false);
            
            // add the new part to the lookup
            Collection<GeometryPart> new_parts = simulation01.get(SimulationPartManager.class).getParts();
            if (!new_parts.isEmpty()) {
                addToTaskLookup(new_parts.iterator().next());
            }
            
            //create a Geometry Scene
            simulation01.getSceneManager().createGeometryScene("Geometry Scene", "Outline", "Geometry", 1);
            Scene scene01 =
                simulation01.getSceneManager().getScene("Geometry Scene 1");
            
            scene01.initializeAndWait();
            PartDisplayer partDisplayer01 =
                ((PartDisplayer) scene01.getCreatorDisplayer());
            partDisplayer01.initialize();
            PartDisplayer partDisplayer02 =
                ((PartDisplayer) scene01.getDisplayerManager().getDisplayer("Outline 1"));
            partDisplayer02.initialize();
            PartDisplayer partDisplayer03 =
                ((PartDisplayer) scene01.getDisplayerManager().getDisplayer("Geometry 1"));
            partDisplayer03.initialize();
//            scene01.open(/*true*/);
            scene01.openSceneView();
            CurrentView currentView_0 =
                scene01.getCurrentView();
            currentView_0.setInput(new DoubleVector(new double[]{0.07000000029802322, 0.02000000048428774, 0.0}),
                new DoubleVector(new double[]{-0.14607243684130425, 0.11741414039370678, 0.17860456694301519}),
                new DoubleVector(new double[]{0.20083797287207372, 0.9415226723669483, -0.2705534440210218}),
                0.07747426272518879, 0);
        }
    }
}
