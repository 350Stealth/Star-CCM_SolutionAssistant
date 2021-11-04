package Assistant;

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;
import star.common.GeometryPart;
import star.meshing.CadPart;

import java.util.ArrayList;
import java.util.Collection;

@StarAssistantTask(display = "Create Region from Part",
    contentPath = "XHTML/02_CreateRegionFromPart.xhtml",
    controller = Task02CreateRegionFromPart.RegionFromPartTaskController.class)
public class Task02CreateRegionFromPart extends Task {
    
    public Task02CreateRegionFromPart() {
    }
    
    public class RegionFromPartTaskController extends FunctionTaskController {
        
        public void createRegion() {
            
            CadPart cadPart_1 = lookupObject(CadPart.class);
            if (cadPart_1 != null) {
                Collection<GeometryPart> list = new ArrayList<>();
                list.add(cadPart_1);
                getSimulation().getRegionManager().newRegionsFromParts(list, "OneRegionPerPart",
                    null, "OneBoundaryPerPartSurface", null,
                    "OneFeatureCurve", null, true);
            }
        }
    }
}
