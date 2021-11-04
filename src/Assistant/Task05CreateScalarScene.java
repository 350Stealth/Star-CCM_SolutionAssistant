package Assistant;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;
import star.base.neo.DoubleVector;
import star.base.neo.IntVector;
import star.base.neo.NeoObjectVector;
import star.common.*;
import star.vis.*;

@StarAssistantTask(display = "Create Scalar Scene",
    contentPath = "XHTML/05_ScalarScene.xhtml",
    controller = Task05CreateScalarScene.ScalarSceneTaskController.class)
public class Task05CreateScalarScene extends Task {
    
    public class ScalarSceneTaskController extends FunctionTaskController {
        
        public void createScalarScene() {
            
            Simulation simulation =
                getActiveSimulation();
            simulation.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");
            Scene scene =
                simulation.getSceneManager().getScene("Scalar Scene 1");
            scene.initializeAndWait();
            PartDisplayer partDisplayer =
                ((PartDisplayer) scene.getCreatorDisplayer());
            partDisplayer.initialize();
            partDisplayer =
                ((PartDisplayer) scene.getDisplayerManager().getDisplayer("Outline 1"));
            partDisplayer.initialize();
            ScalarDisplayer scalarDisplayer =
                ((ScalarDisplayer) scene.getDisplayerManager().getDisplayer("Scalar 1"));
            scalarDisplayer.initialize();
            scene.open(true);
            CurrentView currentView =
                scene.getCurrentView();
            currentView.setInput(new DoubleVector(new double[]{0.07000000029802322, 0.02000000048428774, 0.0}),
                new DoubleVector(new double[]{0.07000000029802322, 0.02000000048428774, 0.2967532688582028}),
                new DoubleVector(new double[]{0.0, 1.0, 0.0}), 0.07746814842582884, 0);
            Units units =
                simulation.getUnitsManager().getPreferredUnits(new IntVector(
                    new int[]{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
            Region region =
                simulation.getRegionManager().getRegion("Fluid");
            scene.getCreatorGroup().setObjects(region);
            FvRepresentation fvRepresentation =
                ((FvRepresentation) simulation.getRepresentationManager().getObject("Volume Mesh"));
            partDisplayer.setRepresentation(fvRepresentation);
            PlaneSection planeSection =
                (PlaneSection) simulation.getPartManager().createImplicitPart(new NeoObjectVector(
                        new Object[]{region}), new DoubleVector(new double[]{0.0, 0.0, 1.0}),
                    new DoubleVector(new double[]{0.07000000000000006, 0.02000105390785938, 1.2549345096901296E-6}), 0, 1,
                    new DoubleVector(new double[]{0.0}));
            LabCoordinateSystem labCoordinateSystem =
                simulation.getCoordinateSystemManager().getLabCoordinateSystem();
            planeSection.setCoordinateSystem(labCoordinateSystem);
            Coordinate coordinate =
                planeSection.getOriginCoordinate();
            coordinate.setCoordinate(units, units, units, new DoubleVector(
                new double[]{0.07000000000000006, 0.02000105390785938, 1.2549345096901296E-6}));
            coordinate =
                planeSection.getOrientationCoordinate();
            coordinate.setCoordinate(units, units, units, new DoubleVector(new double[]{0.0, 0.0, 1.0}));
            coordinate.setValue(new DoubleVector(new double[]{0.0, 0.0, 1.0}));
            coordinate.setValue(new DoubleVector(new double[]{0.07000000000000006, 0.02000105390785938, 1.2549345096901296E-6}));
            SingleValue singleValue =
                planeSection.getSingleValue();
            singleValue.getValueQuantity().setValue(0.0);
            singleValue.getValueQuantity().setUnits(units);
            RangeMultiValue rangeMultiValue =
                planeSection.getRangeMultiValue();
            rangeMultiValue.setNValues(2);
            rangeMultiValue.getStartQuantity().setValue(0.0);
            rangeMultiValue.getStartQuantity().setUnits(units);
            rangeMultiValue.getEndQuantity().setValue(1.0);
            rangeMultiValue.getEndQuantity().setUnits(units);
            DeltaMultiValue deltaMultiValue =
                planeSection.getDeltaMultiValue();
            deltaMultiValue.setNValues(2);
            deltaMultiValue.getStartQuantity().setValue(0.0);
            deltaMultiValue.getStartQuantity().setUnits(units);
            deltaMultiValue.getDeltaQuantity().setValue(1.0);
            deltaMultiValue.getDeltaQuantity().setUnits(units);
            MultiValue multiValue =
                planeSection.getArbitraryMultiValue();
            multiValue.getValueQuantities().setUnits(units);
            multiValue.getValueQuantities().setArray(new DoubleVector(new double[]{0.0}));
            planeSection.setValueMode(0);
            scalarDisplayer.getParts().addParts(planeSection);
            currentView.setInput(new DoubleVector(new double[]{0.07000000029802322, 0.020000109914690256, 0.0}),
                new DoubleVector(new double[]{0.07000000029802322, 0.020000109914690256, 0.2967632316722423}),
                new DoubleVector(new double[]{0.0, 1.0, 0.0}), 0.07747074924218958, 0);
            PrimitiveFieldFunction primitiveFieldFunction_0 =
                ((PrimitiveFieldFunction) simulation.getFieldFunctionManager().getFunction("Velocity"));
            VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_0 =
                ((VectorMagnitudeFieldFunction) primitiveFieldFunction_0.getMagnitudeFunction());
            scalarDisplayer.getScalarDisplayQuantity().setFieldFunction(vectorMagnitudeFieldFunction_0);
            scalarDisplayer.setFillMode(1);
        }
    }
}
