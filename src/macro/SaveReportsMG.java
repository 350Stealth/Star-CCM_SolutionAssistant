package macro;

import star.common.Boundary;
import star.common.Region;
import star.common.Simulation;
import star.common.StarMacro;
import star.flow.ForceReport;
import star.flow.MomentReport;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class SaveReportsMG extends StarMacro {
    
    String[] namesOfBoundary = new String[]{"WING", "TAIL", "FUSELAGE", "BEAMS", "GEARS"};
    HashMap<String, Boundary> boundaries = new HashMap<>();
    HashMap<String, String> reports = new HashMap<>();
    
    public void execute() {
        executePrivate();
    }
    
    private void executePrivate() {
        Simulation activeSim = getActiveSimulation();
        
        Region region = activeSim.getRegionManager().getRegion("Region");
        
        for (int i = 0; i < namesOfBoundary.length; i++) {
            boundaries.put(namesOfBoundary[i], region.getBoundaryManager().getBoundary(namesOfBoundary[i]));
        }
        ForceReport forceRepXwing = (ForceReport) activeSim.getReportManager().getReport("Xwing");
        ForceReport forceRepYwing = (ForceReport) activeSim.getReportManager().getReport("Ywing");
        MomentReport momentumRepMzwing = (MomentReport) activeSim.getReportManager().getReport("Mzwing");
        
        for (int i = -1; i < namesOfBoundary.length; i++) {
            double repXwing;
            double repYwing;
            double repMzwing;
            if (i == -1) {
                forceRepXwing.getParts().setObjects(boundaries.values());
                repXwing = forceRepXwing.getReportMonitorValue();
                forceRepYwing.getParts().setObjects(boundaries.values());
                repYwing = forceRepYwing.getReportMonitorValue();
                momentumRepMzwing.getParts().setObjects(boundaries.values());
                repMzwing = momentumRepMzwing.getReportMonitorValue();
                reports.put("Plane", String.format("%s:\t%s\t%s\t%s\n", "Plane", repXwing, repYwing, repMzwing));
            } else {
                forceRepXwing.getParts().setObjects(boundaries.get(namesOfBoundary[i]));
                repXwing = forceRepXwing.getReportMonitorValue();
                forceRepYwing.getParts().setObjects(boundaries.get(namesOfBoundary[i]));
                repYwing = forceRepYwing.getReportMonitorValue();
                momentumRepMzwing.getParts().setObjects(boundaries.get(namesOfBoundary[i]));
                repMzwing = momentumRepMzwing.getReportMonitorValue();
                reports.put(namesOfBoundary[i], String.format("%s:\t%s\t%s\t%s\n", namesOfBoundary[i], repXwing, repYwing, repMzwing));
            }
        }
        
        String fileName = promptUserForInput("File name", "C:\\Output");
        try {
            FileWriter writer = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            activeSim.println(String.format("Saving reports in %s", fileName));
            bufferedWriter.write("Boundary\tXwing\tYwing\tMzwing\n\n");
            for (int i = -1; i < namesOfBoundary.length; i++) {
                if (i == -1) {
                    bufferedWriter.write(reports.get("Plane"));
                } else {
                    bufferedWriter.write(reports.get(namesOfBoundary[i]));
                }
            }
            bufferedWriter.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        activeSim.println("All is done!");
    }
}
