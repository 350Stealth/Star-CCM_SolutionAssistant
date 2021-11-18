// STAR-CCM+ macro: trainFlowAngles.java
package macro;

import star.common.*;
import star.flow.ForceCoefficientReport;
import star.flow.VelocityProfile;
import star.vis.Scene;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class trainFlowAngles extends StarMacro {
    
    // Folder path: must contain input text file; all outputs will be located here
    public static final String folder =
        "/myTutorials";
    
    /**
     * This is the main method in the macro. It is executed when the macro is
     * run in STAR-CCM+.
     */
    public void execute() {
        
        try {
            // Find the current simulation
            Simulation theSim =
                getActiveSimulation();
            
            // Set up DataReader
            DataReader reader =
                new DataReader();
            // Read from input file; populate list with SimData objects
            reader.readInput(folder + "/trainInput.txt");
            
            // Reference to list of SimData objects (to simplify later steps)
            List<SimData> listCases =
                reader.getFlowDetails();
            
            // Set up DataWriter (this creates the output file and writes headings)
            DataWriter writer =
                new DataWriter(folder + "/trainOutput.txt");
            
            // Set up Simrunner (retrieves various objects from the sim which will be set)
            SimRunner runner =
                new SimRunner(theSim);
            
            // Set up PostProcessor (retrieves scenes and plots which will be saved)
            PostProcessor postP =
                new PostProcessor(theSim);
            
            // The following construct is a "for-each" loop...
            for (SimData sD: listCases) {
                
                // Print line to output window to show how far the process has reached
                theSim.println("Inside the loop. Running case with angle " + sD.getAngle());
                
                // Set various conditions, clear previous solution, run simulation for x iterations
                runner.runCase(sD, 5);
                
                // Retrieve the drag coefficient from the SimData object and write it to file
                writer.writeDataLine(sD);
                
                // Save hardcopies of vel mag and streamlines scenes, and residual plot
                postP.saveVelMagScene(
                    folder + "/velMag" + sD.getAngle() + ".png"
                );
                postP.saveResidualPlot(
                    folder + "/residuals" + sD.getAngle() + ".png"
                );
                postP.saveStreamlinesScene(
                    folder + "/strlines" + sD.getAngle() + ".sce"
                );
                
                // Save simulation
                theSim.saveState(
                    folder + "/train" + sD.getAngle() + ".sim"
                );
            }
        } catch (Exception e) {
            // Included for debugging, create a window displaying the error message
            JOptionPane.showMessageDialog(
                null, e.toString()
            );
        }
        
    }
    
    /**
     * This class receives data from the input data file and contains getter
     * methods which can be used to set velocity components, etc. in the
     * simulation. The input data includes flow angle, train velocity, wind
     * velocity etc., and from these velocity components are calculated.
     */
    public class SimData {
        
        // Member variables
        private double m_angDeg = 0.0;
        private double m_velX = 0.0;
        private double m_velY = 0.0;
        private double m_initVelX = 0.0;
        private double m_initVelY = 0.0;
        private double m_dragC = 0.0;
        
        // Constructor specifies that any SimData object requires a set of input data
        public SimData(
            double angDeg,
            double velTrn,
            double velWnd,
            double initVelTrn,
            double initVelWnd) {
            // Assign input parameter to member variable
            m_angDeg = angDeg;
            // Local variable to convert angles from degrees to radians
            double angRad =
                Math.toRadians(angDeg);
            // Calculate velocity components using input parameters
            m_velX =
                -1 * velWnd * Math.sin(angRad);
            m_velY =
                velTrn + (
                    velWnd * (
                        Math.cos(angRad)));
            m_initVelX =
                -1 * initVelWnd * Math.sin(angRad);
            m_initVelY =
                initVelTrn + (
                    initVelWnd * (
                        Math.cos(angRad)));
        }
        
        // Getter methods to provide access to the member variables
        public double getAngle() {
            return m_angDeg;            // This will be used when naming files
        }
        
        public double getVelX() {
            return m_velX;
        }
        
        public double getVelY() {
            return m_velY;
        }
        
        public double getDrag() {
            return m_dragC;
        }
        
        public double getInitVelX() {
            return m_initVelX;
        }
        
        public double getInitVelY() {
            return m_initVelY;
        }
        
        /*
         * When the simulation has run, the drag coefficient will be stored in
         * the SimData object using the setDrag method.
         */
        public void setDrag(double dDrag) {
            m_dragC = dDrag;
        }
    }
    
    /**
     * This class reads data from the input file. Each line of data in the input
     * file is stored in a SimData object, and this is added to an array list of
     * SimData objects.
     */
    public class DataReader {
        
        // Create array list for SimData objects
        private List<SimData> m_flows =
            new ArrayList<SimData>();
        
        // No constructor is specified so class has default constructor
        
        /*
         * This is the main function in this class. The method takes the path to
         * the input file.
         */
        public void readInput(String fileToRead) {
            try {
                // Read input file: fileToRead
                FileReader fr =
                    new FileReader(fileToRead);
                BufferedReader br =
                    new BufferedReader(fr);
                Scanner sc =
                    new Scanner(br);
                
                /*
                 * Loop through each line in input file and create a new SimData
                 * object for each set of data.
                 */
                while (sc.hasNextLine()) {
                    sc.nextLine();
                    if (sc.hasNextDouble()) {
                        double angDeg =
                            sc.nextDouble();
                        double velTrn =
                            sc.nextDouble();
                        double velWnd =
                            sc.nextDouble();
                        double initVelTrn =
                            sc.nextDouble();
                        double initVelWnd =
                            sc.nextDouble();
                        
                        SimData sd =
                            new SimData(
                                angDeg, velTrn, velWnd, initVelTrn, initVelWnd
                            );
                        
                        // Add SimData object to list.
                        m_flows.add(sd);
                    }
                }
            } catch (Exception e) {
                // Create a window displaying the error message.
                JOptionPane.showMessageDialog(
                    null, e.toString()
                );
            }
        }
        
        // Returns the reference to the list of SimData objects.
        public List<SimData> getFlowDetails() {
            return m_flows;
        }
    }
    
    /**
     * This class is used to write an output file containing the drag coefficient
     * for each inlet flow angle.
     */
    public class DataWriter {
        
        private String m_outputFile = "";
        
        /*
         * Constructor takes the path to the output file. It will create an
         * output file and write the table headings, then close the file.
         * The exception is not caught in the constructor, if it fails, the
         * macro will stop.
         */
        public DataWriter(String fileToWrite) {
            // Assign the input parameter to the member variable
            m_outputFile = fileToWrite;
            
            try {
                FileWriter fw =
                    new FileWriter(m_outputFile);
                BufferedWriter bw =
                    new BufferedWriter(fw);
                bw.write("Angle (deg), DragCoefficient");
                bw.newLine();
                bw.close();
            } catch (Exception e) {
            }
        }
        
        /*
         * Method to write the drag coefficient and cross-wind flow angle to the
         * output file.
         */
        public void writeDataLine(SimData sD) {
            try {
                // Set up file writer
                FileWriter fw =
                    new FileWriter(m_outputFile, true);
                BufferedWriter bw =
                    new BufferedWriter(fw);
                // Write cross-wind flow angle and drag coefficient to output file
                bw.write(sD.getAngle() + ", " + sD.getDrag());
                // Move cursor to next line in file for next set of data to be added
                bw.newLine();
                // Close file
                bw.close();
            } catch (Exception e) { // No exception is displayed to the user
            }
        }
    }
    
    /**
     * This class sets various conditions in the simulation using data read in
     * from each SimData object, clear previous solutions, run the simulation
     * and obtain a value for the drag coefficient.
     */
    public class SimRunner {
        
        private Simulation m_sim = null;
        // Instance variables - properties in simulation that will be set using SimData
        private VelocityProfile m_initVel = null;
        private VelocityProfile m_inflowVel = null;
        private ForceCoefficientReport m_forceReport = null;
        
        // Constructor receives the current simulation as a Simulation object
        public SimRunner(Simulation theSim) {
            m_sim = theSim;
            
            // Find velocity initial condition and assign to member variable
            PhysicsContinuum physics =
                ((PhysicsContinuum) m_sim
                    .getContinuumManager()
                    .getContinuum("Physics 1"));
            m_initVel =
                ((VelocityProfile) physics
                    .getInitialConditions()
                    .get(VelocityProfile.class));
            
            // Find inlet velocity and assign to member variable
            Region region =
                ((Region) m_sim
                    .getRegionManager()
                    .getRegion("trainAndTrack"));
            Boundary inlet =
                ((Boundary) region
                    .getBoundaryManager()
                    .getBoundary("Inflow"));
            m_inflowVel =
                inlet.getValues()
                    .get(VelocityProfile.class);
            
            // Find drag coefficient report and assign to member variable
            m_forceReport =
                ((ForceCoefficientReport) m_sim
                    .getReportManager()
                    .getReport("Drag Coefficient"));
        }
        
        // Method to set variables, clear solution, run simulation, save drag coefficient
        public void runCase(SimData sD, int iterations) {
            // Print line to output window to show how far the process has reached
            m_sim.println("Inside runCase with angle " + sD.getAngle());
            
            // Local variables; values obtained from SimData object
            double initX =
                sD.getInitVelX();
            double initY =
                sD.getInitVelY();
            double velX =
                sD.getVelX();
            double velY =
                sD.getVelY();
            
            // Set initial velocity condition
            ((ConstantVectorProfileMethod) m_initVel
                .getMethod())
                .getQuantity()
                .setComponents(initX, initY, 0.0);
            
            // Set inlet velocity
            ((ConstantVectorProfileMethod) m_inflowVel
                .getMethod())
                .getQuantity()
                .setComponents(velX, velY, 0.0);
            
            // Clear any previous solution
            m_sim.clearSolution();
            
            // Run for x iterations
            m_sim.getSimulationIterator()
                .run(iterations);
            
            // Get CD value
            double cdValue =
                m_forceReport.getReportMonitorValue();
            
            // Save CD value in SimData
            sD.setDrag(cdValue);
        }
    }
    
    /**
     * This class exports various scenes and plots for post-processing purposes.
     * This includes pre-prepared scenes of velocity magnitude and streamlines
     * and a residual plot.
     */
    public class PostProcessor {
        
        private Simulation m_sim = null;
        private Scene m_velMag = null;
        private Scene m_streamlines = null;
        private ResidualPlot m_res = null;
        
        // Constructor receives current simulation, and finds the necessary scenes and plot
        public PostProcessor(Simulation theSim) {
            m_sim = theSim;
            
            // Read velocity magnitude scene
            m_velMag =
                ((Scene) m_sim
                    .getSceneManager()
                    .getScene("Velocity Magnitude"));
            
            // Read streamlines scene
            m_streamlines =
                ((Scene) m_sim
                    .getSceneManager()
                    .getScene("Streamlines"));
            
            // Read residuals plot
            m_res =
                ((ResidualPlot) m_sim
                    .getPlotManager()
                    .getObject("Residuals"));
        }
        
        // Method to save scene; receives path to where the scene will be saved
        public void saveVelMagScene(String sceneToSave) {
            // Save vel mag scene to file
            m_velMag.printAndWait(
                sceneToSave, 1, 1024, 768
            );
        }
        
        // Method to save scene; receives path to where the scene will be saved
        public void saveStreamlinesScene(String sceneToSave) {
            // Save streamlines scene as a STAR-View+ file
            m_streamlines.export3DSceneFileAndWait(
                sceneToSave, true
            );
            // Close streamlines scene
            m_streamlines.close(true);
        }
        
        // Method to save plot; receives path to where the plot will be saved
        public void saveResidualPlot(String plotToSave) {
            // Save residual plot as png file
            m_res.encode(
                plotToSave, "png", 1024, 768
            );
        }
    }
}
