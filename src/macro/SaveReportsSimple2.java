// Simcenter STAR-CCM+ macro: SaveReportsSimple2.java
// Written by Simcenter STAR-CCM+ 16.06.008
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.flow.*;

public class SaveReportsSimple2 extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation simulation_0 = 
      getActiveSimulation();

    ForceReport forceReport_0 = 
      ((ForceReport) simulation_0.getReportManager().getReport("Xwing"));

    forceReport_0.getParts().setQuery(null);

    Region region_0 = 
      simulation_0.getRegionManager().getRegion("Region");

    Boundary boundary_1 = 
      region_0.getBoundaryManager().getBoundary("FUSELAG");

    Boundary boundary_2 = 
      region_0.getBoundaryManager().getBoundary("GEARS");

    Boundary boundary_3 = 
      region_0.getBoundaryManager().getBoundary("TAIL");

    forceReport_0.getParts().setObjects(boundary_1, boundary_2, boundary_3);

    forceReport_0.printReport();
  }
}
