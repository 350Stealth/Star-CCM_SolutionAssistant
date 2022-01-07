// Simcenter STAR-CCM+ macro: SaveReportsSimple.java
// Written by Simcenter STAR-CCM+ 16.06.008
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.flow.*;

public class SaveReportsSimple extends StarMacro {

    public void execute() {

    Simulation theSim =
      getActiveSimulation();

    ForceReport forceReport_0 = 
      ((ForceReport) theSim.getReportManager().getReport("Xwing"));

    forceReport_0.printReport();

    forceReport_0.getParts().setQuery(null);

    Region region_0 = 
      theSim.getRegionManager().getRegion("Region");

    Boundary boundary_4 = 
      region_0.getBoundaryManager().getBoundary("WING");

    forceReport_0.getParts().setObjects(boundary_4);

    forceReport_0.printReport();

    forceReport_0.getParts().setQuery(null);

    Boundary boundary_3 = 
      region_0.getBoundaryManager().getBoundary("TAIL");

    forceReport_0.getParts().setObjects(boundary_3);

    forceReport_0.printReport();
  }
}
