package Assistant;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;
import star.common.Simulation;
import star.common.StepStoppingCriterion;

@StarAssistantTask(display = "Run the Simulation",
    contentPath = "XHTML/06_RunSimulation.xhtml",
    controller = Task06RunTheSimulation.RunTaskController.class)
public class Task06RunTheSimulation extends Task {
    
    public class RunTaskController extends FunctionTaskController {
        
        public void setStoppingCriteria() {
            
            Simulation simulation =
                getActiveSimulation();
            StepStoppingCriterion stepStoppingCriterion =
                ((StepStoppingCriterion) simulation.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps"));
            stepStoppingCriterion.setMaximumNumberSteps(500);
        }
        
        public void runSimulation() {
            Simulation simulation = getSimulation();
            simulation.getSimulationIterator().run();
        }
    }
}
