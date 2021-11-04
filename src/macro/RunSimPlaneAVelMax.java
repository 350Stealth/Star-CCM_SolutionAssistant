// STAR-CCM+ macro: RunSimPlaneAVelMax.java
package macro;

import java.io.*;
import java.util.*;
import java.lang.Math.*;

import javax.swing.*;

import star.common.*;
import star.vis.*;
import star.base.neo.*;
import star.base.report.*;
import star.flow.*;

public class RunSimPlaneAVelMax extends StarMacro {

    // Folder path: must contain input text file; all outputs will be located here
    public static final String folder = 
            "d:\\Projects\\Tantal\\BPLA\\Theory\\CFD";
    public static final String[] sceneNames = {
            "Вектора скорости",
            "Распределение скоростей",
            "Распределение давления",
            "Линии потока"};
    public static final int iterations = 8000;
    public int counter = 1;
    public boolean flag = true;

//    public double[] arr_reports;

    public String
            folderForResults = folder + "\\Results",
            n_testName = "A Plane VelMax",
            n_folderToSave = "",
            n_folderToSave_2 = "",
			n_folderToSavePress = "",
            n_folderToSavePress_2 = "";
    
    public double
			v_angle = 0.0,
    		v_speed = 0.0,
    		v_trust = 0.0,
    		v_lift = 0.0,
    		v_deltaAngle = 0.5,
    		v_deltaSpeed = 100.0,
    		v_deltaA = 0,
			v_deltaS = 0;
			
    /**
     * This is the main method in the macro. It is executed when the macro is
     * run in STAR-CCM+.
     */
    public void execute() {

        try {
            // Find the current simulation
            Simulation theSim =
                    getActiveSimulation();

            theSim.println("Начало всего");

            // Set up DataReader
            DataReader reader = 
                    new DataReader();

            theSim.println("Создан объект DataReader");

            // Read from input file; populate list with SimData objects
            reader.readInput(folder + "\\Test " + n_testName + " Input.csv");

            theSim.println("Входные данные прочитаны");

            // Reference to list of SimData objects (to simplify later steps)
            List<SimData> listCases =
                    reader.getFlowDetails();

//			SimData sD = reader.getFlowDetails();
					
            theSim.println("Входные данные записаны в массив");

            OutputData outData =
                    new OutputData();

            theSim.println("Создан массив выходных данных");
            
            OutputData outDataPress =
                    new OutputData();  
            
            theSim.println("Создан массив выходных данных по давлению");
	
			// Set up PostProcessor (retrieves scenes and plots which will be saved)
			PostProcessor postP =
				new PostProcessor(theSim, folderForResults
					+ "\\Test "
					+ n_testName
					+ "\\Сетка.png");
	
			theSim.println("Создан объект PostProcessor");

            // Set up DataWriter (this creates the output file and writes headings)
		
			DataWriterFull writerFull = 
                    new DataWriterFull(
                            folderForResults
                                    + "\\Test "
                                    + n_testName
                                    + "\\Test "
                                    + n_testName
                                    + " "
                                    + " Results Full.txt");

            theSim.println("Создан объект DataWriterFull");

            // Set up Simrunner (retrieves various objects from the sim which will be set)
            SimRunner runner = 
                    new SimRunner(theSim);

            theSim.println("Создан объект SimRunner");

            // The following construct is a "for-each" loop...
	
			for (SimData sD : listCases) {
				
				// Подготовка переменных для следующего цикла
				runner.varPreparation(sD);
				
				v_angle = sD.getAngle();
				
				v_speed = sD.getVelocity();
				
				v_trust = sD.getTrust();
				
				v_lift = sD.getTargetLiftForce();
				
				do {
						// Запись текущей итерации
						// writerFull.writeDataLine(sD, outData, outDataPress);
						
						// Print line to output window to show how far the process has reached
						theSim.println("Inside the loop. Running case with angle " + sD.getAngle() + "° and Velosity " + sD.getVelocity() + "m/s");
						
						// Подготовка переменных
						runner.varPreparation(sD);
						
						// Поворот рабочей СК
						runner.RotateCS(sD);
						
						runner.SwitchBounderies(sD);
						
						// Set various conditions, clear previous solution, run simulation for x iterations
						runner.runCase(sD, outData, outDataPress, iterations);
						
				//		if (outData.getX() == 0) {outData.setX(1);}
						
				//		if (outData.getY() == 0) {outData.setY(1);}
					
						v_deltaA = v_deltaAngle * Math.abs(outData.getY() - v_lift) / v_lift;
					
						theSim.println("Дельта по углу атаки " + v_deltaA);
					
						v_angle = v_angle - v_deltaA * (outData.getY() - v_lift) / v_lift;
						
						v_deltaS = v_deltaSpeed * Math.abs(outData.getX() - v_trust) / v_trust;
					
						theSim.println("Дельта по скорости " + v_deltaS);
						
						if ((outData.getX() - v_trust) != 0) {
							
							v_speed = v_speed - v_deltaS * Math.sqrt(Math.abs((outData.getX() - v_trust) / v_trust)) * (outData.getX() - v_trust) / Math.abs(outData.getX() - v_trust);
							
						}
			
						if ((Math.abs(v_angle - sD.getAngle()) < 0.001) && (Math.abs(v_speed - sD.getVelocity()) < 0.1)) {
							
							flag = false;
						}
					
						// Запись текущей итерации
						writerFull.writeDataLine(sD, outData, outDataPress);
					
						sD.setAngle(v_angle);
					
						sD.setVelocity(v_speed);

				} while (flag);
				
						// Пишем в файл данные общие
//						writerFull.writeDataLine(sD, outData, outDataPress);
						
						n_folderToSave =
							folderForResults
								+ "\\Test "
								+ n_testName
								+ "\\Normal\\Pictures\\"
								+ counter
								+ " "
								+ n_testName
								+ " "
								+ sD.getAngle();
						
						n_folderToSave_2 =
							folderForResults
								+ "\\Test "
								+ n_testName
								+ "\\Normal\\Pictures\\";
						
						// Копия в отдельную папку с картинками
						postP.saveVelMagScene(
							n_folderToSave_2
								+ "\\"
								+ sceneNames[1]
								+ "\\"
								+ counter
								+ " Test "
								+ n_testName
								+ " "
								+ sD.getAngle()
								+ ".png", 1
						);
						
						// Копия в отдельную папку с картинками
						postP.saveVelVecScene(
							n_folderToSave_2
								+ "\\"
								+ sceneNames[0]
								+ "\\"
								+ counter
								+ " Test "
								+ n_testName
								+ " "
								+ sD.getAngle()
								+ ".png", 1
						);
						
						// Копия в отдельную папку с картинками
						postP.savePressScene(
							n_folderToSave_2
								+ "\\"
								+ sceneNames[2]
								+ "\\"
								+ counter
								+ " Test "
								+ n_testName
								+ " "
								+ sD.getAngle()
								+ ".png", 1
						);
						
						// Копия в отдельную папку с картинками
						postP.saveStreamLinesScene(
							n_folderToSave_2
								+ "\\"
								+ sceneNames[3]
								+ "\\"
								+ counter
								+ " Test "
								+ n_testName
								+ " "
								+ sD.getAngle()
								+ ".png", 1
						);
						
						// Копия в отдельную папку с картинками
						postP.saveResidualPlot(
							n_folderToSave_2
								+ "\\"
								+ "Residuals"
								+ "\\"
								+ counter
								+ " Test "
								+ n_testName
								+ " "
								+ sD.getAngle()
								+ ".png"
						);
						
						// Сохранение картинок с данными по давлению
						n_folderToSavePress =
							folderForResults
								+ "\\Test "
								+ n_testName
								+ "\\Pressure\\Pictures\\"
								+ counter
								+ " "
								+ n_testName
								+ " "
								+ sD.getAngle();
						
						n_folderToSavePress_2 =
							folderForResults
								+ "\\Test "
								+ n_testName
								+ "\\Pressure\\Pictures\\";
						
						// Копия в отдельную папку с картинками
						postP.saveVelMagScene(
							n_folderToSavePress_2
								+ "\\"
								+ sceneNames[1]
								+ "\\"
								+ counter
								+ " Test "
								+ n_testName
								+ " "
								+ sD.getAngle()
								+ ".png", 2
						);
						
						// Копия в отдельную папку с картинками
						postP.saveVelVecScene(
							n_folderToSavePress_2
								+ "\\"
								+ sceneNames[0]
								+ "\\"
								+ counter
								+ " Test "
								+ n_testName
								+ " "
								+ sD.getAngle()
								+ ".png", 2
						);
						
						// Копия в отдельную папку с картинками
						postP.savePressScene(
							n_folderToSavePress_2
								+ "\\"
								+ sceneNames[2]
								+ "\\" + counter
								+ " Test "
								+ n_testName
								+ " "
								+ sD.getAngle()
								+ ".png", 2
						);
						
						// Копия в отдельную папку с картинками
						postP.saveStreamLinesScene(
							n_folderToSavePress_2
								+ "\\"
								+ sceneNames[3]
								+ "\\"
								+ counter
								+ " Test "
								+ n_testName
								+ " "
								+ sD.getAngle()
								+ ".png", 2
						);
						
						// Копия в отдельную папку с картинками
						postP.saveResidualPlot(
							n_folderToSavePress_2
								+ "\\"
								+ "Residuals"
								+ "\\" + counter
								+ " Test "
								+ n_testName
								+ " "
								+ sD.getAngle()
								+ ".png"
						);
						
						++counter;
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
        private double m_alphaDeg = 0.0;
        private double m_velocity = 0.0;
        private double m_turbScale = 0.0;
        private double m_turbVelScale = 0.0;
        private double m_turbIntence = 0.0;
        private double m_chord = 0.0;
        private double m_targetLiftForce = 0.0;
        private double m_trust = 0.0;
        

        // Constructor specifies that any SimData object requires a set of input data
        public SimData(
                double alphaDeg, 
                double velocity, 
                double turbScale,
                double turbVelScale, 
                double turbIntence,
				double chord,
				double targetLiftForce,
				double trust) {
    		
					m_alphaDeg = alphaDeg;
					m_velocity = velocity;
					m_turbScale = turbScale;
					m_turbVelScale = turbVelScale;
					m_turbIntence = turbIntence;
					m_chord = chord;
					m_targetLiftForce = targetLiftForce;
					m_trust = trust;
     	 	    }

        // Getter methods to provide access to the member variables
        public double getAngle() {
            return m_alphaDeg;
        }
		
		public double getAngleRad() {
           
			double angRad = 
						Math.toRadians(m_alphaDeg);
			return angRad;       	     // For transform deg to rad
        }

        public double getVelocity() {
            return m_velocity;
        }

        public double getTurbScale() {
            return m_turbScale;
        }

        public double getTurbVelScale() {
            return m_turbVelScale;
        }

        public double getTurbIntence() {
            return m_turbIntence;
        }
	
		public double getChord() {
			return m_chord;
		}
	
		public double getTargetLiftForce() {
			return m_targetLiftForce;
		}
	
		public double getTrust() {
			return m_trust;
		}
		
		public void setAngle(double v_angle) {
			m_alphaDeg = v_angle;
		}
		
		public void setVelocity(double v_speed) {
        	m_velocity = v_speed;
		}
    }
	
    /**
     * Класс данных результатов расчета
     */
	
    public class OutputData {
		
	private double m_X = 0.0;
	private double m_Y = 0.0;
	private double m_Cx = 0.0;
	private double m_Cy = 0.0;
	private double m_K = 0.0;
	private double m_Mz = 0.0;
	private double m_Cmz = 0.0;
	private double m_Re = 0.0;
	
	// Getters
	public double getX() {
            return m_X;
        }
		
	public double getY() {
            return m_Y;
        }
		
	public double getCx() {
            return m_Cx;
        }
		
	public double getCy() {
            return m_Cy;
        }
		
	public double getK() {
            return m_K;
        }
		
	public double getMz() {
            return m_Mz;
        }
		
	public double getCmz() {
            return m_Cmz;
        }
		
	public double getRe() {
            return m_Re;
        }	
		
	//Setters	
	public void setX(double param) {
            m_X = param;
        }
		
	public void setY(double param) {
            m_Y = param;
        }
		
	public void setK(double param) {
            m_K = param;
        }
		
	public void setCx(double param) {
            m_Cx = param;
        }
		
	public void setCy(double param) {
            m_Cy = param;
        }
		
	public void setMz(double param) {
            m_Mz = param;
        }
		
	public void setCmz(double param) {
            m_Cmz = param;
        }
		
	public void setRe(double param) {
            m_Re = param;
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
    //    private String testName = "";

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
            //    testName = sc.nextString();

                while (sc.hasNextLine()) {
                    sc.nextLine();
                    if (sc.hasNextDouble()) {
                        double alpha = 
                                sc.nextDouble();
                        double vel = 
                                sc.nextDouble();
                        double turbScale = 
                                sc.nextDouble();
                        double turbVelScale = 
                                sc.nextDouble();
                        double turbIntence = 
                                sc.nextDouble();
						double chord =
								sc.nextDouble();
						double targetLiftForce =
								sc.nextDouble();
						double trust =
								sc.nextDouble();

                        SimData sd = 
                                new SimData(
                                alpha, vel, turbScale, turbVelScale, turbIntence, chord, targetLiftForce, trust
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
/**
        public String getTestName() {
            return testName;
        } */
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
                bw.write(
					"Alpha	" + 
					"V	" +
					"Cx	" + 
					"Cy	" + 
					"K	" + 
					"Y	" + 
					"X	" + 
					"Cmz	" + 
					"Mz"
				);
                bw.newLine();
                bw.close();
            } catch (Exception e) {
            }
        }

        /*
         * Method to write the drag coefficient and cross-wind flow angle to the
         * output file.
         */
        public void writeDataLine(SimData sD, OutputData outD) {
            try {
                // Set up file writer
                FileWriter fw = 
                        new FileWriter(m_outputFile, true);
                BufferedWriter bw = 
                        new BufferedWriter(fw);
                // Write cross-wind flow angle and drag coefficient to output file
                bw.write(
					sD.getAngle() + "	" +
					sD.getVelocity() + "	" +
					outD.getCx() + "	" +
					outD.getCy() + "	" +
					outD.getK() + "	" +
					outD.getY() + "	" +
					outD.getX() + "	" +
					outD.getCmz() + "	" +
					outD.getMz()
				);
                // Move cursor to next line in file for next set of data to be added
                bw.newLine();
                // Close file
                bw.close();
            } catch (Exception e) { // No exception is displayed to the user
            }
        }
    }
	
    public class DataWriterFull {

        private String m_outputFile = "";

        /*
         * Constructor takes the path to the output file. It will create an
         * output file and write the table headings, then close the file.
         * The exception is not caught in the constructor, if it fails, the
         * macro will stop.
         */
        public DataWriterFull(String fileToWrite) {
            // Assign the input parameter to the member variable
            m_outputFile = fileToWrite;

            try {
                FileWriter fw = 
                        new FileWriter(m_outputFile);
                BufferedWriter bw = 
                        new BufferedWriter(fw);
                bw.write(
					"Alpha	" + 
					"V	" +
					"Cx	" + 
					"Cy	" + 
					"K	" + 
					"Y	" + 
					"X	" + 
					"Cmz	" + 
					"Mz	" +
					"Cxp	" + 
					"Cyp	" + 
					"Kp	" + 
					"Cmzp	"
				);
                bw.newLine();
                bw.close();
            } catch (Exception e) {
            }
        }

        /*
         * Method to write the drag coefficient and cross-wind flow angle to the
         * output file.
         */
        public void writeDataLine(SimData sD, OutputData outD, OutputData outDp) {
            try {
                // Set up file writer
                FileWriter fw = 
                        new FileWriter(m_outputFile, true);
                BufferedWriter bw = 
                        new BufferedWriter(fw);
                // Write cross-wind flow angle and drag coefficient to output file
                bw.write(
					sD.getAngle() + "	" +
					sD.getVelocity() + "	" +
		/*			outD.getCx() + "	" +
					outD.getCy() + "	" +
					outD.getK() + "	" +
		**/			outD.getY() + "	" +
					outD.getX() //+ "	" +
		/*			outD.getCmz() + "	" +
					outD.getMz() + "	" +
					outDp.getCx() + "	" +
					outDp.getCy() + "	" +
					outDp.getK() + "	" +
					outDp.getCmz()
		**/		);
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

        private Simulation m_simUsed = null;
        // Instance variables - properties in simulation that will be set using SimData
        private VelocityProfile m_initVel = null;
        private VelocityProfile m_inflowVel = null;
        private ForceReport 
			m_forceX = null,
			m_forceY = null,
			m_forceXpress = null,
			m_forceYpress = null;
		private ForceCoefficientReport
			m_coefCx = null,
			m_coefCy = null,
			m_coefCxpress = null,
			m_coefCypress = null;
		private ExpressionReport
			m_expK = null,
			m_expKpress = null;
		private MomentCoefficientReport
			m_momentCoefCmz = null,
			m_momentCoefCmzpress = null;
		private	MomentReport
			m_momentMz = null,
			m_momentMzpress = null;
		private String
			n_AngleOfAttack = "Угол атаки",
			n_Velocity = "Скаляр V",
			n_CoordSys = "Вектор скорости",
			n_var1 = "Масштаб турбулентности",
			n_var2 = "Масштаб турбулентной скорости",
			n_var3 = "Интенсивность турбулентности",
			n_Chord = "Хорда",
			n_Trust = "Тяга",
			n_TargetLiftForce = "Подъемная сила";
		private	String
			n_Cx = "Cx",
			n_Cy = "Cy",
			n_X = "X",
			n_Y = "Y",
			n_Cmz = "Cmz",
			n_Mz = "Mz",
			n_K = "K";
		private	String
			np_Cx = "Cxp",
			np_Cy = "Cyp",
			np_X = "Xp",
			np_Y = "Yp",
			np_Cmz = "Cmzp",
			np_Mz = "Mzp",
			np_K = "Kp";
		private Region m_region = null;
		private Boundary
        	m_boundaryBottom = null,
			m_boundaryTop = null;
		private PressureBoundary m_pressureBoundary = null;
		private InletBoundary m_inletBoundary = null;

        // Constructor receives the current simulation as a Simulation object
        public SimRunner(Simulation theSim) {
            m_simUsed = theSim;

            // Find drag coefficient report and assign to member variable
            m_forceX = 
        		(
        	    	(ForceReport) m_simUsed
        	    		.getReportManager()
        	    		.getReport(n_X)
        		);
					
            m_forceY = 
                (
                	(ForceReport) m_simUsed
						.getReportManager()
						.getReport(n_Y)
				);
			
            m_coefCx = 
                (
                	(ForceCoefficientReport) m_simUsed
                		.getReportManager()
                		.getReport(n_Cx)
				);
			
            m_coefCy = 
                (
					(ForceCoefficientReport) m_simUsed
						.getReportManager()
						.getReport(n_Cy)
				);
			
            m_expK = 
                (
                	(ExpressionReport) m_simUsed
                		.getReportManager()
                		.getReport(n_K)
				);
			
            m_momentCoefCmz = 
                (
                	(MomentCoefficientReport) m_simUsed
                		.getReportManager()
                		.getReport(n_Cmz)
				);
			
            m_momentMz = 
                (
                	(MomentReport) m_simUsed
                		.getReportManager()
                		.getReport(n_Mz)
				);
            
            // Получаем ссылки на отчеты по давлению
            
            m_forceXpress = 
        		(
        	    	(ForceReport) m_simUsed
        	    		.getReportManager()
        	    		.getReport(np_X)
        		);
					
            m_forceYpress =
				(
                	(ForceReport) m_simUsed
						.getReportManager()
						.getReport(np_Y)
				);
			
            m_coefCxpress = 
                (
                	(ForceCoefficientReport) m_simUsed
                		.getReportManager()
               	 		.getReport(np_Cx)
				);
			
            m_coefCypress = 
                (
					(ForceCoefficientReport) m_simUsed
						.getReportManager()
						.getReport(np_Cy)
				);
			
            m_expKpress = 
                (
                	(ExpressionReport) m_simUsed
                		.getReportManager()
						.getReport(np_K)
				);
			
            m_momentCoefCmzpress = 
                (
                	(MomentCoefficientReport) m_simUsed
                		.getReportManager()
                		.getReport(np_Cmz)
				);
			
            m_momentMzpress = 
                (
                	(MomentReport) m_simUsed
                		.getReportManager()
                		.getReport(np_Mz)
				);
            
        // Получаем ссылки на регионы и границы
            m_region =
                    m_simUsed.getRegionManager()
                    	.getRegion("Region");

            m_boundaryBottom =
                    m_region.getBoundaryManager()
                    	.getBoundary("Bottom");

            m_boundaryTop =
                    m_region.getBoundaryManager()
                    	.getBoundary("Top");

            m_pressureBoundary =
        	 (
                    (PressureBoundary) m_simUsed
						.get(ConditionTypeManager.class)
                    	.get(PressureBoundary.class)
                 );

            m_inletBoundary =
                (
                    (InletBoundary) m_simUsed
						.get(ConditionTypeManager.class)
                    	.get(InletBoundary.class)
                );
        }

		// Подготовка переменных
		public void varPreparation(SimData sD) {
		// Устанавливаем значение угла атаки
			ScalarGlobalParameter sGP_Angle = 
				(
					(ScalarGlobalParameter) m_simUsed
						.get(GlobalParameterManager.class)
						.getObject(n_AngleOfAttack)
				);

			sGP_Angle.getQuantity().setValue(sD.getAngle());

		// Устанавливаем значение скорости
			ScalarGlobalParameter sGP_Velocity = 
				(
					(ScalarGlobalParameter) m_simUsed
						.get(GlobalParameterManager.class)
						.getObject(n_Velocity)
				);

			sGP_Velocity.getQuantity().setValue(sD.getVelocity());

		// Устанавливаем параметр Var1
			ScalarGlobalParameter sGP_Var1 =
				(
					(ScalarGlobalParameter) m_simUsed
						.get(GlobalParameterManager.class)
						.getObject(n_var1)
				);
			
			sGP_Var1.getQuantity().setValue(sD.getTurbScale());

		// Устанавливаем параметр Var2
			ScalarGlobalParameter sGP_Var2 =
				(
					(ScalarGlobalParameter) m_simUsed
						.get(GlobalParameterManager.class)
						.getObject(n_var2)
				);
			
			sGP_Var2.getQuantity().setValue(sD.getTurbVelScale());
			
		// Устанавливаем параметр Var2
			ScalarGlobalParameter sGP_Var3 =
				(
					(ScalarGlobalParameter) m_simUsed
						.get(GlobalParameterManager.class)
						.getObject(n_var3)
				);
			
			sGP_Var3.getQuantity().setValue(sD.getTurbIntence());
			
		// Устанавливаем параметр значение хорды
			ScalarGlobalParameter sGP_Chord =
				(
					(ScalarGlobalParameter) m_simUsed
						.get(GlobalParameterManager.class)
						.getObject(n_Chord)
				);
			
			sGP_Chord.getQuantity().setValue(sD.getChord());
			
		// Устанавливаем параметр значение целевой подъемной силы
			ScalarGlobalParameter sGP_TargetLiftForce =
				(
					(ScalarGlobalParameter) m_simUsed
						.get(GlobalParameterManager.class)
						.getObject(n_TargetLiftForce)
				);
			
			sGP_TargetLiftForce.getQuantity().setValue(sD.getTargetLiftForce());
			
		// Устанавливаем параметр значение тяги
			ScalarGlobalParameter sGP_Trust =
				(
					(ScalarGlobalParameter) m_simUsed
						.get(GlobalParameterManager.class)
						.getObject(n_Trust)
				);
			
			sGP_Trust.getQuantity().setValue(sD.getTrust());
		}
		
		// Поворот СК
		public void RotateCS(SimData sD) {
			
			LabCoordinateSystem lCS_Used = 
				m_simUsed.getCoordinateSystemManager()
				.getLabCoordinateSystem();

			CartesianCoordinateSystem cCS_Used = 
				((CartesianCoordinateSystem) lCS_Used.getLocalCoordinateSystemManager().
				getObject(n_CoordSys));
		// Обнуляем СК
			cCS_Used.setBasis0(
				new DoubleVector(new double[] {1.0, 0.0, 0.0})
			);

			cCS_Used.setBasis1(
				new DoubleVector(new double[] {0.0, 1.0, 0.0})
			);

			if (sD.getAngle() != 0) {

                // Не понятно, зачем объявляется, не используется
                    Units u_Angle =
                            m_simUsed.getUnitsManager()
                        .getPreferredUnits(
                            new IntVector(
                                new int[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                            )
                        );

                    Units u_Lines =
                            m_simUsed.getUnitsManager()
                        .getPreferredUnits(
                            new IntVector(
                                new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                            )
                        );
                    // Поворот на угол атаки в радианах
                    cCS_Used.getLocalCoordinateSystemManager()
                    .rotateLocalCoordinateSystems(
                        new NeoObjectVector(
                            new Object[] {cCS_Used}
                        ), new DoubleVector(
                            new double[] {0.0, 0.0, 1.0}
                        ), new NeoObjectVector(
                            new Object[] {u_Lines, u_Lines, u_Lines}
                        ), sD.getAngleRad(), lCS_Used
                    );
			}
		}

		public void SwitchBounderies(SimData sD) {

            if (sD.getAngle() < -1) {

                m_boundaryBottom.setBoundaryType(m_pressureBoundary);

                m_boundaryTop.setBoundaryType(m_inletBoundary);

            } else if (sD.getAngle() > 1) {

                m_boundaryBottom.setBoundaryType(m_inletBoundary);

                m_boundaryTop.setBoundaryType(m_pressureBoundary);

            } else /** if (sD.getAngle() == 0) */{

                m_boundaryBottom.setBoundaryType(m_inletBoundary);

                m_boundaryTop.setBoundaryType(m_inletBoundary);
            }
        }
		
        // Method to set variables, clear solution, run simulation, save drag coefficient
        public void runCase(SimData sD, OutputData outD, OutputData outDpress, int iterations) {
            // Print line to output window to show how far the process has reached
            m_simUsed.println("Цикл расчета для угла атаки: " + sD.getAngle() + "° и скорости " + sD.getVelocity() + "м/с");
/**
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
*/
            // Clear any previous solution
            m_simUsed.clearSolution();

            // Run for x iterations
            m_simUsed.getSimulationIterator()
                    .run(iterations);

// Записываем полученные значения после расчета
	// Получаем X
            double XValue =
                    m_forceX.getReportMonitorValue();

        // Сохраняем X в OutputData
            outD.setX(XValue);
			
            double YValue =
                    m_forceY.getReportMonitorValue();

            outD.setY(YValue);
			
            double CxValue =
                    m_coefCx.getReportMonitorValue();

            outD.setCx(CxValue);
			
            double CyValue =
                    m_coefCy.getReportMonitorValue();

            outD.setCy(CyValue);
			
            double KValue =
                    m_expK.getReportMonitorValue();

            outD.setK(KValue);
			
            double CmzValue =
                    m_momentCoefCmz.getReportMonitorValue();

            outD.setCmz(CmzValue);
			
            double MzValue =
                    m_momentMz.getReportMonitorValue();

            outD.setMz(MzValue);

    // Сохраняем данные по давлению в OutputDataPress            
            
            double XValuepress =
                    m_forceXpress.getReportMonitorValue();

            outDpress.setX(XValuepress);
			
            double YValuepress =
                    m_forceYpress.getReportMonitorValue();

            outDpress.setY(YValuepress);
			
            double CxValuepress =
                    m_coefCxpress.getReportMonitorValue();

            outDpress.setCx(CxValuepress);
			
            double CyValuepress =
                    m_coefCypress.getReportMonitorValue();

            outDpress.setCy(CyValuepress);
			
            double KValuepress =
                    m_expKpress.getReportMonitorValue();

            outDpress.setK(KValuepress);
			
            double CmzValuepress =
                    m_momentCoefCmzpress.getReportMonitorValue();

            outDpress.setCmz(CmzValuepress);
			
            double MzValuepress =
                    m_momentMzpress.getReportMonitorValue();

            outDpress.setMz(MzValuepress);
        }
    }

    /**
     * This class exports various scenes and plots for post-processing purposes.
     * This includes pre-prepared scenes of velocity magnitude and streamlines
     * and a residual plot.
     */
    public class PostProcessor {

        private Simulation m_sim = null;
        private Scene
			m_velMag = null,
			m_velMagPress = null,
			m_pressure = null,
			m_pressurePress = null,
			m_velVectors = null,
			m_velVectorsPress = null,
			m_streamlines = null,
			m_streamlinesPress = null,
			m_mesh = null;
        private ResidualPlot m_res = null;
        private String
            ns_velMag = "Распределение скоростей",
            ns_velVectors = "Вектора скорости",
            ns_pressure = "Распределение давления",
            ns_streamlines = "Линии потока",
			ns_velMagPress = "Распределение скоростей 2",
			ns_velVectorsPress = "Вектора скорости 2",
			ns_pressurePress = "Распределение давления 2",
			ns_streamlinesPress = "Линии потока 2",
			ns_mesh = "Сетка";

        // Constructor receives current simulation, and finds the necessary scenes and plot
        public PostProcessor(Simulation theSim, String pathName) {
            m_sim = theSim;

            // Read velocity magnitude scene
            m_velMag =
                ((Scene) m_sim
                    .getSceneManager()
                    .getScene(ns_velMag));
	
			m_velMagPress =
				((Scene) m_sim
					.getSceneManager()
					.getScene(ns_velMagPress));

            // Read velocity vectors scene
            m_velVectors =
                    ((Scene) m_sim
                    .getSceneManager()
                    .getScene(ns_velVectors));
	
			m_velVectorsPress =
				((Scene) m_sim
					.getSceneManager()
					.getScene(ns_velVectorsPress));

            // Read pressure scene
            m_pressure =
                    ((Scene) m_sim
                    .getSceneManager()
                    .getScene(ns_pressure));
	
			m_pressurePress =
				((Scene) m_sim
					.getSceneManager()
					.getScene(ns_pressurePress));

            // Read streamlines scene
 /*           m_streamlines =
                    ((Scene) m_sim
                    .getSceneManager()
                    .getScene(ns_streamlines));
	
			m_streamlinesPress =
				((Scene) m_sim
					.getSceneManager()
					.getScene(ns_streamlinesPress));
**/
            // Read residuals plot
            m_res =
                    ((ResidualPlot) m_sim
                    .getPlotManager()
                    .getObject("Residuals"));
	
			m_mesh =
				((Scene) m_sim
					.getSceneManager()
					.getScene(ns_mesh));
	
			m_mesh.printAndWait(
				pathName, 1, 1920, 1080, true, false
			);
        }

        // Method to save scene; receives path to where the scene will be saved
        public void saveVelMagScene(String sceneToSave, int flag) {
            // Save vel mag scene to file
            switch (flag) {
            
				case 1:
					m_velMag.printAndWait(
						sceneToSave, 1, 1920, 1080
					);
					break;
				
				case 2:
					m_velMagPress.printAndWait(
						sceneToSave, 1, 1920, 1080
					);
					break;
			}
			     
        //    m_velMag.close(true);
        }

        public void saveVelVecScene(String sceneToSave, int flag) {
            // Save vel mag scene to file
			switch (flag) {
				
				case 1:
					m_velVectors.printAndWait(
						sceneToSave, 1, 1920, 1080
					);
					break;
					
				case 2:
					m_velVectorsPress.printAndWait(
						sceneToSave, 1, 1920, 1080
					);
					break;
			}
        
        //    m_velVectors.close(true);
        }

        public void savePressScene(String sceneToSave, int flag) {
            // Save vel mag scene to file
			switch (flag) {
				
				case 1:
					m_pressure.printAndWait(
						sceneToSave, 1, 1920, 1080
					);
					break;
					
				case 2:
					m_pressurePress.printAndWait(
						sceneToSave, 1, 1920, 1080
					);
					break;
			}
        
        //    m_pressure.close(true);
        }

        public void saveStreamLinesScene(String sceneToSave, int flag) {
            // Save vel mag scene to file
			switch (flag) {
				
				case 1:
					m_streamlines.printAndWait(
						sceneToSave, 1, 1920, 1080
					);
					break;
					
				case 2:
					m_streamlinesPress.printAndWait(
						sceneToSave, 1, 1920, 1080
					);
			}

            // Save streamlines scene as a STAR-View+ file
        /**    m_streamlines.export3DSceneFileAndWait(
                    sceneToSave, true
            );*/
            // Close streamlines scene
        //    m_streamlines.close(true);
        }

        // Method to save scene; receives path to where the scene will be saved
    /**    public void saveStreamlinesScene(String sceneToSave) {
            // Save streamlines scene as a STAR-View+ file
            m_streamlines.export3DSceneFileAndWait(
                    sceneToSave, true
            );
            // Close streamlines scene
            m_streamlines.close(true);
        }
    */
        // Method to save plot; receives path to where the plot will be saved
        public void saveResidualPlot(String plotToSave) {
            // Save residual plot as png file
            m_res.encode(
                    plotToSave, "png", 1920, 1080
            );
        }
    }
}
