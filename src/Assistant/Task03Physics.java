package Assistant;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;
import star.common.*;
import star.flow.*;
import star.material.ConstantMaterialPropertyMethod;
import star.material.Gas;
import star.material.SingleComponentGasModel;
import star.metrics.ThreeDimensionalModel;
import star.segregatedflow.SegregatedFlowModel;

import java.io.Serializable;

@StarAssistantTask(display = "Create Physics",
    contentPath = "XHTML/03_Physics.xhtml",
    controller = Task03Physics.PhysicsTaskController.class)
public class Task03Physics extends Task{
    
    public class PhysicsTaskController extends FunctionTaskController {
        
        String physicsContinuumName = "Physics";
        
        public void createPhysicsContinuum() {
            // code for Step 1: creating and defining the physics continuum.
            Simulation simulation = getActiveSimulation();
            PhysicsContinuum continuum = simulation.getContinuumManager().createContinuum(PhysicsContinuum.class);
            continuum.setPresentationName(physicsContinuumName);
            continuum.enable(ThreeDimensionalModel.class);
            continuum.enable(SteadyModel.class);
            continuum.enable(SingleComponentGasModel.class);
            continuum.enable(SegregatedFlowModel.class);
            continuum.enable(ConstantDensityModel.class);
            continuum.enable(LaminarModel.class);
        }
        
        public void materialProperties() {
            // code for Step 2: modifying the material properties of air.
            Simulation simulation = getActiveSimulation();
            PhysicsContinuum continuum = (PhysicsContinuum) simulation.getContinuumManager().getContinuum(physicsContinuumName);
            SingleComponentGasModel singleComponentGasModel = continuum.getModelManager().getModel(SingleComponentGasModel.class);
            Gas gas = (Gas) singleComponentGasModel.getMaterial();
            ConstantMaterialPropertyMethod constantMaterialPropertyMethod = (ConstantMaterialPropertyMethod) gas
                .getMaterialProperties().getMaterialProperty(ConstantDensityProperty.class).getMethod();
            constantMaterialPropertyMethod.getQuantity().setValue(1.0);
            constantMaterialPropertyMethod = (ConstantMaterialPropertyMethod) gas.getMaterialProperties()
                .getMaterialProperty(DynamicViscosityProperty.class).getMethod();
            constantMaterialPropertyMethod.getQuantity().setValue(1.716E-5);
//            Units units0 = simulation.getUnitsManager().getObject("kg/m^3");
//            Units units1 = simulation.getUnitsManager().getObject("Pa-s");
        }
        
        public void initialConditionsAndBoundarySettings() {
            // code for Step 3: defining the initial conditions, boundary type, and boundary conditions.
            Simulation simulation = getActiveSimulation();
            simulation.println("111");
            PhysicsContinuum continuum = (PhysicsContinuum) simulation.getContinuumManager().getContinuum(physicsContinuumName);
            simulation.println("222");
            VelocityProfile velocityProfile = continuum.getInitialConditions().get(VelocityProfile.class);
            simulation.println("333");
            velocityProfile.getMethod(ConstantVectorProfileMethod.class).getQuantity().setComponents(0.429, 0.0, 0.0);
            simulation.println("444");
            Region region = simulation.getRegionManager().getRegion("Fluid");
            simulation.println("555");
            Boundary boundary = region.getBoundaryManager().getBoundary("Inlet");
            simulation.println("666");
            boundary.setBoundaryType(InletBoundary.class);
            simulation.println("777");
            VelocityMagnitudeProfile velocityMagnitudeProfile = boundary.getValues().get(VelocityMagnitudeProfile.class);
            simulation.println("888");
            velocityMagnitudeProfile.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(0.429);
            simulation.println("999");
            boundary = region.getBoundaryManager().getBoundary("Outlet");
            simulation.println("000");
            boundary.setBoundaryType(PressureBoundary.class);
            simulation.println("done!");
        }
    }
}