// STAR-CCM+ macro: MSTTestAPlane3.java
// Written by STAR-CCM+ 12.02.011
package macro;

import java.io.*;
import java.util.*;

import javax.swing.*;

import star.common.*;
import star.vis.*;
import star.base.neo.*;
import star.base.report.*;
import star.flow.*;

public class MSTTestAPlane3 extends StarMacro {
	
	public static final String nameRegion = "Region";
	public static final String[] nameBoundary = {"Plane"};
	public static final double refArea = 0.375;
	public static final double AirDensity = 1.18415;

	public void execute() {
		
		try {
			Simulation TheSim = 
				getActiveSimulation();
			
			String 	UsedCSName = "Вектор скорости", 
				VelocityName = "Скорость", 
				AoAName = "Угол атаки",
				ReNumName = "Число Re";
			
				TheSim.println("Начало всего!");
			
			RefCS refCS = 
				new RefCS(TheSim, UsedCSName);
			
				TheSim.println("Создана СК");
				
			MakeVariables Variables =
				new MakeVariables(TheSim);
			
				TheSim.println("Создан объект переменных и переменные");
				
			CartesianCoordinateSystem VelocityVectorCS = refCS.GetCS(TheSim, UsedCSName);
			
			MakeReports Reports = 
				new MakeReports(TheSim, VelocityVectorCS);
			
				TheSim.println("Создан объект отчетов");
			
			Reports.MakeGroup(TheSim, "Группа: Аннотации");
			
			Reports.MakeAnnotationReport(TheSim, VelocityName);
			
			Reports.MakeAnnotationReport(TheSim, AoAName);
			
			Reports.MakeAnnotationReport(TheSim, ReNumName);
			
				TheSim.println("Созданы отчеты для аннотаций");
			
			MakeAnnotations Annotations = 
				new MakeAnnotations();
			
				TheSim.println("Создан объект аннотаций");
				
			Annotations.CreateCoef(TheSim, Reports.Cx, 0.03);
			
			Annotations.CreateCoef(TheSim, Reports.Cx + Reports.sPressure, 0.03);
			
			Annotations.CreateCoef(TheSim, Reports.Cy, 0.03);
			
			Annotations.CreateCoef(TheSim, Reports.Cy + Reports.sPressure, 0.03);
			
			Annotations.CreateExpression(TheSim, Reports.K, 0.03);
			
			Annotations.CreateExpression(TheSim, Reports.K +  Reports.sPressure, 0.03);
			
			Annotations.CreateExpression(TheSim, VelocityName, 0.03);
			
			Annotations.CreateExpression(TheSim, AoAName, 0.03);
			
			Annotations.CreateExpression(TheSim, ReNumName, 0.03);			
			
			Annotations.CreateFullTime(TheSim, 0.02);
			
			Annotations.ChangeSceneAnnotation(TheSim, 0.05);
			
				TheSim.println("Созданы аннотации");
			
			MakeUserScenes UsedScenes = 
				new MakeUserScenes(/**TheSim*/);
			
				TheSim.println("Создан объект сцен");
			
			UsedScenes.MakeScalarScene(TheSim, UsedScenes.nameScenePressure);
			
			UsedScenes.MakeScalarScene(TheSim, UsedScenes.nameSceneVelocity);
			
			UsedScenes.MakeScalarScenePressure(TheSim, UsedScenes.nameScenePressure + " 2");
			
			UsedScenes.MakeScalarScenePressure(TheSim, UsedScenes.nameSceneVelocity + " 2");
			
			UsedScenes.MakeVectorScene(TheSim, UsedScenes.nameSceneVelVectors);
			
			UsedScenes.MakeVectorScenePressure(TheSim, UsedScenes.nameSceneVelVectors + " 2");
			
				TheSim.println("Созданы сцены");
			
		
		} catch (Exception e) {
            // Included for debugging, create a window displaying the error message
            JOptionPane.showMessageDialog(
                    null, e.toString()
            );
        }
	}

	/**
	* Класс для создания ссылочной системы координат и получения ссылки на нее
	*/
	public class RefCS {
		
		//Конструктор класса системы координат
		public RefCS(Simulation UsedSim, String CSName) {
			
			MakeCS(UsedSim, CSName);
		}
		
		/**
		* Создаем ссылочную систему координат
		*/
		public void MakeCS(Simulation theSim, String name) {

			LabCoordinateSystem LabVelVecCS = 
				theSim.getCoordinateSystemManager()
				.getLabCoordinateSystem();

			CartesianCoordinateSystem VelVecCS = 
				LabVelVecCS.getLocalCoordinateSystemManager()
				.createLocalCoordinateSystem(
					CartesianCoordinateSystem.class, "LocalCoordinateSystem"
				);
		//переименовываем систему координат
			VelVecCS.setPresentationName(name);
		}
		/**
		* Получаем ссылку на созданную систему координат
		*/
		public CartesianCoordinateSystem GetCS(Simulation theSim, String name) {

			LabCoordinateSystem lCS_UsedCS = 
			  theSim.getCoordinateSystemManager().getLabCoordinateSystem();

			CartesianCoordinateSystem cCS_UsedCS = 
			  ((CartesianCoordinateSystem) lCS_UsedCS.getLocalCoordinateSystemManager().getObject(name));
			  
			return cCS_UsedCS;
			
		}
	}
	
	/**
	* Класс для создания списка отчетов 
	*/
	public class MakeReports {
		public static final String
				sPressure = "p", 
				sShear = "s";
		public static final String
				GroupName_0 = "Группа: Норма", 
				GroupName_1 = "Группа: Давление", 
				GroupName_2 = "Группа: сдвиг", 
				GroupAnnotationsName = "Группа: Аннотации";
		public static final String
				Cx = "Cx", 
				Cy = "Cy", 
				X = "X", 
				Y = "Y", 
				Cmz = "Cmz", 
				Mz = "Mz", 
				K = "K", 
				NameCPU = "Время ЦПУ", 
				NameElapsed = "Затраченное время ЦПУ";
		public Boundary
				boundary_0 = null,
				boundary_1 = null;
		public Region
				region_Main = null;

		
	//Конструктор
		public MakeReports(Simulation UsedSim, CartesianCoordinateSystem UsedCS) {
			
			MakeGroup(UsedSim, GroupName_0);
			
			GetBoundaries(UsedSim);
			
			MakeCx(UsedSim, 0, Cx, UsedCS);
			
			MakeCy(UsedSim, 0, Cy, UsedCS);
			
			MakeX(UsedSim, 0, X, UsedCS);
			
			MakeY(UsedSim, 0, Y, UsedCS);
			
			MakeCmz(UsedSim, 0, Cmz, UsedCS);
			
			MakeMz(UsedSim, 0, Mz, UsedCS);
			
			MakeK(UsedSim, 0, K);
			
			MakeGroup(UsedSim, GroupName_1);
			
			MakeCx(UsedSim, 1, Cx+sPressure, UsedCS);
			
			MakeCy(UsedSim, 1, Cy+sPressure, UsedCS);
			
			MakeX(UsedSim, 1, X+sPressure, UsedCS);
			
			MakeY(UsedSim, 1, Y+sPressure, UsedCS);
			
			MakeCmz(UsedSim, 1, Cmz+sPressure, UsedCS);
			
			MakeMz(UsedSim, 1, Mz+sPressure, UsedCS);
			
			MakeK(UsedSim, 1, K+sPressure);
	
			MakeTimes(UsedSim, NameCPU, NameElapsed);
		}
	
		public void MakeGroup(Simulation theSim, String name) {

			theSim.getReportManager().getGroupsManager().createGroup(name);
		}
		
		private void GetBoundaries(Simulation theSim) {
			
			region_Main =
				theSim.getRegionManager().getRegion(nameRegion);
			
			boundary_0 =
				region_Main.getBoundaryManager().getBoundary(nameBoundary[0]);
			
	/**		boundary_1 =
				region_Main.getBoundaryManager().getBoundary(nameBoundary[1]); */
		}
		
		private void MakeCx(Simulation theSim, int flag, String name, CartesianCoordinateSystem UsedLocCS) {
			
			ForceCoefficientReport fCR_Cx = 
			  theSim.getReportManager().createReport(ForceCoefficientReport.class);

			fCR_Cx.setPresentationName(name);
			
			fCR_Cx.getDirection().setComponents(1.0, 0.0, 0.0);

			fCR_Cx.getReferenceDensity().setValue(AirDensity);

			fCR_Cx.getReferenceVelocity().setDefinition("${Скорость}");

			fCR_Cx.getReferenceArea().setValue(refArea);

			fCR_Cx.setCoordinateSystem(UsedLocCS);
	//flag = 1 - for Pressure, flag = 2 for Shear, flag = any - for Pressure+Shear
			switch (flag) {
				case 1:
					fCR_Cx.getForceOption()
					.setSelected(
						ForceReportForceOption
						.Type
						.PRESSURE
					);
					
					((ClientServerObjectGroup) theSim.getReportManager()
					.getGroupsManager()
					.getObject(GroupName_1))
					.getGroupsManager()
					.groupObjects(
						GroupName_1, new NeoObjectVector(new Object[] {fCR_Cx}), true
					);			
					break;
				
				case 2:
					fCR_Cx.getForceOption()
					.setSelected(
						ForceReportForceOption
						.Type
						.SHEAR
					);
					
					((ClientServerObjectGroup) theSim.getReportManager()
					.getGroupsManager()
					.getObject(GroupName_2))
					.getGroupsManager()
					.groupObjects(
						GroupName_2, new NeoObjectVector(new Object[] {fCR_Cx}), true
					);
					
					break;
				
				default:
					fCR_Cx.getForceOption()
					.setSelected(
						ForceReportForceOption
						.Type
						.PRESSURE_AND_SHEAR
					);
					
					((ClientServerObjectGroup) theSim.getReportManager()
					.getGroupsManager()
					.getObject(GroupName_0))
					.getGroupsManager()
					.groupObjects(
						GroupName_0, new NeoObjectVector(new Object[] {fCR_Cx}), true
					);
			}
			
			fCR_Cx.getParts().setQuery(null);
			
			fCR_Cx.getParts().setObjects(boundary_0/**, boundary_1*/);
		}
		
		private void MakeCy(Simulation theSim, int flag, String name, CartesianCoordinateSystem UsedLocCS) {
			
			ForceCoefficientReport fCR_Cy = 
			  theSim.getReportManager().createReport(ForceCoefficientReport.class);

			fCR_Cy.setPresentationName(name);
			
			fCR_Cy.getDirection().setComponents(0.0, 1.0, 0.0);

			fCR_Cy.getReferenceDensity().setValue(AirDensity);

			fCR_Cy.getReferenceVelocity().setDefinition("${Скорость}");

			fCR_Cy.getReferenceArea().setValue(refArea);

			fCR_Cy.setCoordinateSystem(UsedLocCS);
	//flag = 1 - for Pressure, flag = 2 for Shear, flag = any - for Pressure+Shear		
			switch (flag) {
				case 1:
					fCR_Cy.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.PRESSURE
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_1)
					).getGroupsManager()
					.groupObjects(
						GroupName_1, new NeoObjectVector(new Object[] {fCR_Cy}), true
					);			
					break;
				
				case 2:
					fCR_Cy.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.SHEAR
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_2)
					).getGroupsManager()
					.groupObjects(
						GroupName_2, new NeoObjectVector(new Object[] {fCR_Cy}), true
					);
					
					break;
				
				default:
					fCR_Cy.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.PRESSURE_AND_SHEAR
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_0)
					).getGroupsManager()
					.groupObjects(
						GroupName_0, new NeoObjectVector(new Object[] {fCR_Cy}), true
					);
			}
			
			fCR_Cy.getParts().setQuery(null);
			
			fCR_Cy.getParts().setObjects(boundary_0/**, boundary_1*/);
			
			ReportMonitor rM_Cy = 
				fCR_Cy.createMonitor();
					
			MonitorPlot mP_Cy = 
				theSim.getPlotManager()
				.createMonitorPlot(
					new NeoObjectVector(
						new Object[] {rM_Cy}
					), name+" Monitor"
				);
		}
		
		private void MakeX(Simulation theSim, int flag, String name, CartesianCoordinateSystem UsedLocCS) {
			
			ForceReport fR_X = 
			  theSim.getReportManager().createReport(ForceReport.class);

			fR_X.setPresentationName(name);
			
			fR_X.getDirection().setComponents(1.0, 0.0, 0.0);
	//flag = 1 - for Pressure, flag = 2 for Shear, flag = any - for Pressure+Shear
			fR_X.setCoordinateSystem(UsedLocCS);
			
			switch (flag) {
				case 1:
					fR_X.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.PRESSURE
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_1)
					).getGroupsManager()
					.groupObjects(
						GroupName_1, new NeoObjectVector(new Object[] {fR_X}), true
					);			
					break;
				
				case 2:
					fR_X.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.SHEAR
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_2)
					).getGroupsManager()
					.groupObjects(
						GroupName_2, new NeoObjectVector(new Object[] {fR_X}), true
					);
					
					break;
				
				default:
					fR_X.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.PRESSURE_AND_SHEAR
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_0)
					).getGroupsManager()
					.groupObjects(
						GroupName_0, new NeoObjectVector(new Object[] {fR_X}), true
					);
			}
			
			fR_X.getParts().setQuery(null);
			
			fR_X.getParts().setObjects(boundary_0/**, boundary_1*/);
		}
		
		private void MakeY(Simulation theSim, int flag, String name, CartesianCoordinateSystem UsedLocCS) {
			
			ForceReport fR_Y = 
				theSim.getReportManager().createReport(ForceReport.class);

			fR_Y.setPresentationName(name);
			
			fR_Y.getDirection().setComponents(0.0, 1.0, 0.0);

			fR_Y.setCoordinateSystem(UsedLocCS);
	//flag = 1 - for Pressure, flag = 2 for Shear, flag = any - for Pressure+Shear		
			switch (flag) {
				case 1:
					fR_Y.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.PRESSURE
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_1)
					).getGroupsManager()
					.groupObjects(
						GroupName_1, new NeoObjectVector(new Object[] {fR_Y}), true
					);			
					break;
				
				case 2:
					fR_Y.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.SHEAR
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_2)
					).getGroupsManager()
					.groupObjects(
						GroupName_2, new NeoObjectVector(new Object[] {fR_Y}), true
					);
					
					break;
				
				default:
					fR_Y.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.PRESSURE_AND_SHEAR
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_0)
					).getGroupsManager()
					.groupObjects(
						GroupName_0, new NeoObjectVector(new Object[] {fR_Y}), true
					);
			}
			
			fR_Y.getParts().setQuery(null);
			
			fR_Y.getParts().setObjects(boundary_0/**, boundary_1*/);
		}
		
		private void MakeCmz(Simulation theSim, int flag, String name, CartesianCoordinateSystem UsedLocCS) {
			
			MomentCoefficientReport mCR_Cmz = 
				theSim.getReportManager().createReport(MomentCoefficientReport.class);

			mCR_Cmz.setPresentationName(name);
			
			mCR_Cmz.getDirection()
			.setComponents(0.0, 0.0, 1.0);
			
			mCR_Cmz.getOrigin()
			.setComponents(0.0, 0.0, 0.0);

			mCR_Cmz.getReferenceDensity()
			.setValue(AirDensity);

			mCR_Cmz.getReferenceVelocity()
			.setDefinition("${Скорость}");

			mCR_Cmz.getReferenceArea()
			.setValue(refArea);

			mCR_Cmz.setCoordinateSystem(UsedLocCS);
	//flag = 1 - for Pressure, flag = 2 for Shear, flag = any - for Pressure+Shear
			switch (flag) {
				case 1:
					mCR_Cmz.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.PRESSURE
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_1)
					).getGroupsManager()
					.groupObjects(
						GroupName_1, new NeoObjectVector(new Object[] {mCR_Cmz}), true
					);			
					break;
				
				case 2:
					mCR_Cmz.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.SHEAR
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_2)
					).getGroupsManager()
					.groupObjects(
						GroupName_2, new NeoObjectVector(new Object[] {mCR_Cmz}), true
					);
					
					break;
				
				default:
					mCR_Cmz.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.PRESSURE_AND_SHEAR
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_0)
					).getGroupsManager()
					.groupObjects(
						GroupName_0, new NeoObjectVector(new Object[] {mCR_Cmz}), true
					);
			}
			
			mCR_Cmz.getParts().setQuery(null);
			
			mCR_Cmz.getParts().setObjects(boundary_0/**, boundary_1*/);
		}
		
		private void MakeMz(Simulation theSim, int flag, String name, CartesianCoordinateSystem UsedLocCS) {
			
			MomentReport mR_Mz = 
				theSim.getReportManager()
				.createReport(MomentReport.class);

			mR_Mz.setPresentationName(name);
			
			mR_Mz.getDirection()
			.setComponents(0.0, 0.0, 1.0);
			
			mR_Mz.getOrigin()
			.setComponents(0.0, 0.0, 0.0);

			mR_Mz.setCoordinateSystem(UsedLocCS);
	//flag = 1 - for Pressure, flag = 2 for Shear, flag = any - for Pressure+Shear
			switch (flag) {
				case 1:
					mR_Mz.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.PRESSURE
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_1)
					).getGroupsManager()
					.groupObjects(
						GroupName_1, new NeoObjectVector(new Object[] {mR_Mz}), true
					);			
					break;
				
				case 2:
					mR_Mz.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.SHEAR
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_2)
					).getGroupsManager()
					.groupObjects(
						GroupName_2, new NeoObjectVector(new Object[] {mR_Mz}), true
					);
					
					break;
				
				default:
					mR_Mz.getForceOption()
					.setSelected(
						ForceReportForceOption.Type
						.PRESSURE_AND_SHEAR
					);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_0)
					).getGroupsManager()
					.groupObjects(
						GroupName_0, new NeoObjectVector(new Object[] {mR_Mz}), true
					);
			}
			
			mR_Mz.getParts().setQuery(null);
			
			mR_Mz.getParts().setObjects(boundary_0/**, boundary_1*/);
		}
		
		private void MakeK(Simulation theSim, int flag, String name) {
			
			ExpressionReport eR_K = 
				theSim.getReportManager()
				.createReport(ExpressionReport.class);

			eR_K.setPresentationName(name);
			
	//flag = 1 - for Pressure, flag = 2 for Shear, flag = any - for Pressure+Shear
			switch (flag) {
				case 1:
					eR_K.setDefinition("${YpReport}/${XpReport}");
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_1)
					).getGroupsManager()
					.groupObjects(
						GroupName_1, new NeoObjectVector(new Object[] {eR_K}), true
					);			
					break;
				
				case 2:		
					eR_K.setDefinition("${YsReport}/${XsReport}");
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_2)
					).getGroupsManager()
					.groupObjects(
						GroupName_2, new NeoObjectVector(new Object[] {eR_K}), true
					);
					
					break;
				
				default:
					eR_K.setDefinition("${YReport}/${XReport}");
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_0)
					).getGroupsManager()
					.groupObjects(
						GroupName_0, new NeoObjectVector(new Object[] {eR_K}), true
					);
			}
			
			ReportMonitor rM_K = 
				eR_K.createMonitor();
					
			MonitorPlot mP_K = 
				theSim.getPlotManager()
				.createMonitorPlot(
					new NeoObjectVector(
						new Object[] {rM_K}
					), name+" Monitor"
				);
		}
	//Создать отчет для аннотаций	
		public void MakeAnnotationReport(Simulation theSim, String name) {
			
			ExpressionReport eR_Annotation = 
				theSim.getReportManager().createReport(ExpressionReport.class);

			eR_Annotation.setPresentationName(name);
			
			Units units_for_report;
		
			switch (name) {
				case "Скорость":
					eR_Annotation.setDefinition("${Скорость}");
					
					eR_Annotation.setDimensionsVector(
						new IntVector(
							new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}
						)
					);
					
					units_for_report = 
						((Units) theSim.getUnitsManager().getObject("kph"));
						
					eR_Annotation.setUnits(units_for_report);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupAnnotationsName)
					).getGroupsManager()
					.groupObjects(
						GroupAnnotationsName, new NeoObjectVector(new Object[] {eR_Annotation}), true
					);
					break;
				
				case "Угол атаки":
					eR_Annotation.setDefinition("${Угол атаки}");
					
					eR_Annotation.setDimensionsVector(
						new IntVector(
							new int[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
						)
					);
					
					units_for_report = 
						((Units) theSim.getUnitsManager().getObject("deg"));
						
					eR_Annotation.setUnits(units_for_report);
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupAnnotationsName)
					).getGroupsManager()
					.groupObjects(
						GroupAnnotationsName, new NeoObjectVector(new Object[] {eR_Annotation}), true
					);
					
					break;
					
				case "Число Re":
					eR_Annotation.setDefinition("(${Скаляр V}*${Хорда}*1.225)/${Вязкость}");
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupName_0)
					).getGroupsManager()
					.groupObjects(
						GroupName_0, new NeoObjectVector(new Object[] {eR_Annotation}), true
					);				
				
				default:
				//	eR_Annotation.setDefinition("${YReport}/${XReport}");
					
					(
						(ClientServerObjectGroup) theSim.getReportManager()
						.getGroupsManager()
						.getObject(GroupAnnotationsName)
					).getGroupsManager()
					.groupObjects(
						GroupAnnotationsName, new NeoObjectVector(new Object[] {eR_Annotation}), true
					);
			}
		}
	//Создание отчетов, мониторов и графиков времени цикла и общего времени
		private void MakeTimes(Simulation theSim, String nameCPU, String nameElapsed) {
				
			String GroupName = "Класс: Время";
			
			MakeGroup(theSim, GroupName);
		//Создаем отчет времени ЦПУ за цикл		
			IteratorCpuTimeReport iCTR_CPUTime = 
				theSim.getReportManager().createReport(IteratorCpuTimeReport.class);
				
			iCTR_CPUTime.setPresentationName(nameCPU);
		//Переносим в группу Время	
			(
				(ClientServerObjectGroup) theSim.getReportManager()
				.getGroupsManager()
				.getObject(GroupName)
			).getGroupsManager()
			.groupObjects(
				GroupName, new NeoObjectVector(new Object[] {iCTR_CPUTime}), true
			);
		//Создаем отчет затраченного времени ЦПУ за цикл		
			IteratorElapsedTimeReport iETR_ElapsedTime = 
				theSim.getReportManager().createReport(IteratorElapsedTimeReport.class);
				
			iETR_ElapsedTime.setPresentationName(nameElapsed);
						
		//Переносим в группу Время	
			(
				(ClientServerObjectGroup) theSim.getReportManager()
				.getGroupsManager()
				.getObject(GroupName)
			).getGroupsManager()
			.groupObjects(
				GroupName, new NeoObjectVector(new Object[] {iETR_ElapsedTime}), true
			);
			
			ReportMonitor rM_CPU = 
				iCTR_CPUTime.createMonitor();
				
			rM_CPU.setPresentationName(nameCPU);
			
	//		theSim.println(rM_CPU.getPresentationName());
				
			ReportMonitor rM_Elapsed = 
				iETR_ElapsedTime.createMonitor();
				
			rM_Elapsed.setPresentationName(nameElapsed);
			
	//		theSim.println(rM_Elapsed.getPresentationName());
				
			MonitorPlot mP_Time = 
				theSim.getPlotManager()
				.createMonitorPlot(
					new NeoObjectVector(
						new Object[] {rM_CPU}
					), "Время цикла"
				);
			
			SimulationIteratorTimeReportMonitor sITRM_ElapsedTime = 
				(
					(SimulationIteratorTimeReportMonitor) theSim.getMonitorManager()
					.getMonitor(nameElapsed)
				);
			
			mP_Time.getDataSetManager()
			.addDataProviders(
				new NeoObjectVector(
					new Object[] {sITRM_ElapsedTime}
				)
			);
		//Создаем отчет Общего времени решения
			CumulativeElapsedTimeReport cETR_Used = 
				theSim.getReportManager()
				.createReport(CumulativeElapsedTimeReport.class);

			Units u_Minutes = 
				(
					(Units) theSim.getUnitsManager()
					.getObject("min")
				);

			cETR_Used.setUnits(u_Minutes);
			
			(
				(ClientServerObjectGroup) theSim.getReportManager()
				.getGroupsManager()
				.getObject(GroupName)
			).getGroupsManager()
			.groupObjects(
				GroupName, new NeoObjectVector(new Object[] {cETR_Used}), true
			);
		}
	}

	/**
	* Класс для создания переменных
	*/
	public class MakeVariables{
		
		//Конструктор
		public MakeVariables(Simulation UsedSim) {
			
			MakeAngleOfAttack(UsedSim);
			
			MakeScalarV(UsedSim);
			
			MakeVelocity(UsedSim);
			
			MakeTurbScale(UsedSim);
			
			MakeTurbVel(UsedSim);
			
			MakeTurbIntensity(UsedSim);
			
			MakeChord(UsedSim);
			
			MakeViscousity(UsedSim);
			
		//	MakeVelocityVec(UsedSim);
		}
	//Создаем переменные
		private void MakeAngleOfAttack(Simulation theSim) {

			//Создаем переменную угла атаки типа "скаляр"
			theSim.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Скаляр");

			//получаем ссылку на эту переменную
			ScalarGlobalParameter sGB_AngleOfAttack = 
			  ((ScalarGlobalParameter) theSim.get(GlobalParameterManager.class).getObject("Скаляр"));

			//переименовываем
			sGB_AngleOfAttack.setPresentationName("Угол атаки");

			//задаем тип переменной - угол
			sGB_AngleOfAttack.setDimensionsVector(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

			//делаем переменую с еденицами измерения градусы (deg)
			Units Unit_degs = 
			  ((Units) theSim.getUnitsManager().getObject("deg"));

			//назначаем градусы переменной угла атаки	  
			sGB_AngleOfAttack.getQuantity().setUnits(Unit_degs);

			//устанавливаем значение переменной угла атаки	
			sGB_AngleOfAttack.getQuantity().setValue(15.0);
		}
	  
	//создаем безразмерное значение скорости
		private void MakeScalarV(Simulation theSim) {

			//Создаем переменную угла атаки типа "скаляр"
			theSim.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Скаляр");

			//получаем ссылку на эту переменную
			ScalarGlobalParameter sGB_ScalarV = 
			  ((ScalarGlobalParameter) theSim.get(GlobalParameterManager.class).getObject("Скаляр"));

			//переименовываем
			sGB_ScalarV.setPresentationName("Скаляр V");

			//задаем тип переменной - безразмерная
			sGB_ScalarV.setDimensionsVector(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

			//устанавливаем значение переменной	
			sGB_ScalarV.getQuantity().setValue(90);
		}

	//создаем переменную скорости	
		private void MakeVelocity(Simulation theSim) {

			//Создаем переменную угла атаки типа "скаляр"
			theSim.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Скаляр");

			//получаем ссылку на эту переменную
			ScalarGlobalParameter sGB_Velosity = 
			  ((ScalarGlobalParameter) theSim.get(GlobalParameterManager.class).getObject("Скаляр"));

			//переименовываем
			sGB_Velosity.setPresentationName("Скорость");

			//задаем тип переменной - скорость
			sGB_Velosity.setDimensionsVector(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

			//устанавливаем значение переменной	
			sGB_Velosity.getQuantity().setDefinition("${Скаляр V}");
		}

	//создаем переменную Масштаба турбулентности	
		private void MakeTurbScale(Simulation theSim) {

			//Создаем переменную угла атаки типа "скаляр"
			theSim.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Скаляр");

			//получаем ссылку на эту переменную
			ScalarGlobalParameter sGB_TurbScale = 
			  ((ScalarGlobalParameter) theSim.get(GlobalParameterManager.class).getObject("Скаляр"));

			//переименовываем
			sGB_TurbScale.setPresentationName("Масштаб турбулентности");

			//задаем тип переменной - безразмерная
			sGB_TurbScale.setDimensionsVector(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

			//устанавливаем значение переменной	
			sGB_TurbScale.getQuantity().setValue(0.01);
		}

	//создаем переменную Масштаба турбулентной скорости	
		private void MakeTurbVel(Simulation theSim) {

			//Создаем переменную угла атаки типа "скаляр"
			theSim.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Скаляр");

			//получаем ссылку на эту переменную
			ScalarGlobalParameter sGB_TurbVel = 
			  ((ScalarGlobalParameter) theSim.get(GlobalParameterManager.class).getObject("Скаляр"));

			//переименовываем
			sGB_TurbVel.setPresentationName("Масштаб турбулентной скорости");

			//задаем тип переменной - безразмерная
			sGB_TurbVel.setDimensionsVector(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

			//устанавливаем значение переменной	
			sGB_TurbVel.getQuantity().setValue(1);
		}
		
	//создаем переменную Интенсивность турбулентности	
		private void MakeTurbIntensity(Simulation theSim) {

			//Создаем переменную угла атаки типа "скаляр"
			theSim.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Скаляр");

			//получаем ссылку на эту переменную
			ScalarGlobalParameter sGB_TurbIntensity = 
			  ((ScalarGlobalParameter) theSim.get(GlobalParameterManager.class).getObject("Скаляр"));

			//переименовываем
			sGB_TurbIntensity.setPresentationName("Интенсивность турбулентности");

			//задаем тип переменной - безразмерная
			sGB_TurbIntensity.setDimensionsVector(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

			//устанавливаем значение переменной	
			sGB_TurbIntensity.getQuantity().setValue(0.01);
		}
		
	//создаем переменную вектора скорости	
		private void MakeVelocityVec(Simulation theSim) {

			//Создаем переменную угла атаки типа "вектор"
			theSim.get(GlobalParameterManager.class).createGlobalParameter(VectorGlobalParameter.class, "Вектор");

			//получаем ссылку на эту переменную
			VectorGlobalParameter vGB_VelosityVec = 
			  ((VectorGlobalParameter) theSim.get(GlobalParameterManager.class).getObject("Вектор"));

			//переименовываем
			vGB_VelosityVec.setPresentationName("Вектор скорости");

			//задаем тип переменной - безразмерная
			vGB_VelosityVec.setDimensionsVector(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

			//устанавливаем значение переменной	
			vGB_VelosityVec.getQuantity().setDefinition("[cos(${Угол атаки}), sin(${Угол атаки}), 0.0]");
		}
		
	//создаем безразмерное значение вязкости
		private void MakeViscousity(Simulation theSim) {

			//Создаем переменную угла атаки типа "скаляр"
			theSim.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Скаляр");

			//получаем ссылку на эту переменную
			ScalarGlobalParameter sGB_Viscousity = 
			  ((ScalarGlobalParameter) theSim.get(GlobalParameterManager.class).getObject("Скаляр"));

			//переименовываем
			sGB_Viscousity.setPresentationName("Вязкость");

			//задаем тип переменной - безразмерная
			sGB_Viscousity.setDimensionsVector(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

			//устанавливаем значение переменной	
			sGB_Viscousity.getQuantity().setValue(1.7894e-5);
		}	

	//создаем безразмерное значение длины хорды (в метрах)
		private void MakeChord(Simulation theSim) {

			//Создаем переменную угла атаки типа "скаляр"
			theSim.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Скаляр");

			//получаем ссылку на эту переменную
			ScalarGlobalParameter sGB_Chord = 
			  ((ScalarGlobalParameter) theSim.get(GlobalParameterManager.class).getObject("Скаляр"));

			//переименовываем
			sGB_Chord.setPresentationName("Хорда");

			//задаем тип переменной - безразмерная
			sGB_Chord.setDimensionsVector(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

			//устанавливаем значение переменной	
			sGB_Chord.getQuantity().setValue(refArea);
		}		
	}
	
	/**
	* Класс для создания аннотаций
	*/
	public class MakeAnnotations{
		
		public void CreateCoef(Simulation theSim, String Name, double Height) {
			
			ForceCoefficientReport fCR_Used = 
				(
					(ForceCoefficientReport) theSim.getReportManager()
					.getReport(Name)
				);
			
			ReportAnnotation rA_Used = 
				theSim.getAnnotationManager()
				.createReportAnnotation(fCR_Used);
				
			rA_Used.setDefaultHeight(Height);
			
			rA_Used.setShadow(false);
			
			switch (Name) {
				case "Cx":
					rA_Used.setDefaultPosition(
						new DoubleVector(
							new double[] {0.005, 0.73, 0.0}
						)
					);
					break;
				
				case "Cxp":
					rA_Used.setDefaultPosition(
						new DoubleVector(
							new double[] {0.005, 0.73, 0.0}
						)
					);
					break;
				
				case "Cy":
					rA_Used.setDefaultPosition(
						new DoubleVector(
							new double[] {0.005, 0.77, 0.0}
						)
					);
					break;
				
				case "Cyp":
					rA_Used.setDefaultPosition(
						new DoubleVector(
							new double[] {0.005, 0.77, 0.0}
						)
					);
					break;
				
				default:
					rA_Used.setDefaultPosition(
						new DoubleVector(
							new double[] {0.0, 0.0, 0.0}
						)
					);
			}
		}
		
		public void CreateExpression(Simulation theSim, String Name, double Height) {
			
			ExpressionReport eR_Used = 
				(
					(ExpressionReport) theSim.getReportManager()
					.getReport(Name)
				);

			ReportAnnotation rA_Used = 
				theSim.getAnnotationManager()
				.createReportAnnotation(eR_Used);

			rA_Used.setDefaultHeight(Height);
			
			rA_Used.setShadow(false);
			
			switch (Name) {
				case "K":
					rA_Used.setDefaultPosition(
						new DoubleVector(
							new double[] {0.005, 0.81, 0.0}
						)
					);
					
					rA_Used.setPresentationName("Качество");
					
					break;
				
				case "Kp":
					rA_Used.setDefaultPosition(
						new DoubleVector(
							new double[] {0.005, 0.81, 0.0}
						)
					);
					
					rA_Used.setPresentationName("Качество по давлению");
					
					break;
				
				case "Скорость":
					rA_Used.setDefaultPosition(
						new DoubleVector(
							new double[] {0.005, 0.89, 0.0}
						)
					);
					
					rA_Used.setPresentationName(Name);
					
					break;
				
				case "Угол атаки":
					rA_Used.setDefaultPosition(
						new DoubleVector(
							new double[] {0.005, 0.85, 0.0}
						)
					);
					
					rA_Used.setPresentationName(Name);
					
					break;
					
				case "Число Re":
					rA_Used.setDefaultPosition(
						new DoubleVector(
							new double[] {0.005, 0.93, 0.0}
						)
					);
					
					rA_Used.setPresentationName(Name);
					
					break;	
				
				default:
					rA_Used.setDefaultPosition(
						new DoubleVector(
							new double[] {0.0, 0.0, 0.0}
						)
					);
			}
		}
		
		public void CreateFullTime(Simulation theSim, double Height) {
			
			CumulativeElapsedTimeReport cETR_Used = 
				(
					(CumulativeElapsedTimeReport) theSim.getReportManager()
					.getReport("Общее время решения")
				);

			ReportAnnotation rA_Used = 
				theSim.getAnnotationManager()
				.createReportAnnotation(cETR_Used);
				
			rA_Used.setDefaultHeight(Height);
			
			rA_Used.setShadow(false);
			
			rA_Used.setDefaultPosition(
				new DoubleVector(
					new double[] {0.0, 0.05, 0.0}
				)
			);
		}
		
		public void ChangeSceneAnnotation(Simulation theSim, double Height) {
			
			SceneAnnotation sA_Used =
				(
					(SceneAnnotation) theSim.getAnnotationManager()
					.getObject("Scene")
				);
			
			sA_Used.setShadow(false);
			
			sA_Used.setShowProduct(false);
			
			sA_Used.setShowProductVersion(false);
			
			sA_Used.setShowDate(true);
			
			sA_Used.setShowUser(false);
			
			sA_Used.setShowDatabase(true);
			
			sA_Used.setDefaultHeight(Height);
		}
	}
	
	/**
	* Класс для создания сцен
	*/
	public class MakeUserScenes{
	//Имена сцен	
		public static final String 
			nameScenePressure = "Распределение давления",
			nameSceneVelocity = "Распределение скоростей",
			nameSceneVelVectors = "Вектора скорости",
			nameSceneFlowLines = "Линии потока",
			nameSceneMesh2D = "Сетка 2D",
			nameSceneMesh3D = "Сетка 3D";
	//Список Аннотаций для сцены	
		private String[] AnnotationNames = {
			"Cx",
			"Cy",
			"Качество",
			"Скорость",
			"Угол атаки",
			"Scene",
			"Общее время решения",
			"Число Re"};
		private String[] AnnotationNamesPressure = {
			"Cxp",
			"Cyp",
			"Качество по давлению",
			"Скорость",
			"Угол атаки",
			"Scene",
			"Общее время решения",
			"Число Re"};
	//Конструктор		
/**		public void MakeUserScenes(Simulation theSim) {
			
			MakeScalarScene(theSim, nameScenePressure);
			
			MakeScalarScene(theSim, nameSceneVelocity);
		}*/
	//Создание сцен скаляров	
		public void MakeScalarScene(Simulation UsedSim, String Name){

		try {
		
			UsedSim.getSceneManager()
			.createScalarScene(
				"Сцена скаляра", "Контур", "Скаляр"
			);

			} catch (Exception e) {}
			
			Scene s_ScalarScene = 
				UsedSim.getSceneManager()
				.getScene(
					"Сцена скаляра 1"
				);

			s_ScalarScene.initializeAndWait();

			PartDisplayer pD_Used = 
				(
					(PartDisplayer) s_ScalarScene.getDisplayerManager()
					.getDisplayer("Контур 1")
				);

			pD_Used.initialize();

			ScalarDisplayer sD_Used = 
				(
					(ScalarDisplayer) s_ScalarScene.getDisplayerManager()
					.getDisplayer("Скаляр 1")
				);

			sD_Used.initialize();
			
			s_ScalarScene.setPresentationName(Name);
			
			switch (Name) {
				
				case "Распределение давления":

					PrimitiveFieldFunction pFF_UsedVel = 
					(
						(PrimitiveFieldFunction) UsedSim.getFieldFunctionManager()
						.getFunction("Pressure")
					);

					sD_Used.getScalarDisplayQuantity().setFieldFunction(pFF_UsedVel);

					sD_Used.setFillMode(ScalarFillMode.NODE_SMOOTH);
					
					break;
					
				case "Распределение скоростей":
					
					PrimitiveFieldFunction pFF_UsedPress = 
					(
						(PrimitiveFieldFunction) UsedSim.getFieldFunctionManager()
						.getFunction("Velocity")
					);
					
					VectorMagnitudeFieldFunction vMFF_Used = 
					(
						(VectorMagnitudeFieldFunction) pFF_UsedPress.getMagnitudeFunction()
					);

					sD_Used.getScalarDisplayQuantity().setFieldFunction(vMFF_Used);

					sD_Used.setFillMode(ScalarFillMode.NODE_SMOOTH);
					
					break;
			}
			
			SetAnnotationsInScene(UsedSim, s_ScalarScene, AnnotationNames);
			
			Legend l_Used =
				sD_Used.getLegend();
			
			l_Used.setShadow(false);
		}
		
		public void MakeScalarScenePressure(Simulation UsedSim, String Name){

		try {
		
			UsedSim.getSceneManager()
				.createScalarScene(
					"Сцена скаляра", "Контур", "Скаляр"
				);

			} catch (Exception e) {}
				
			Scene s_ScalarScene =
				UsedSim.getSceneManager()
					.getScene(
						"Сцена скаляра 1"
					);
			
			s_ScalarScene.initializeAndWait();
			
			PartDisplayer pD_Used =
				(
					(PartDisplayer) s_ScalarScene.getDisplayerManager()
						.getDisplayer("Контур 1")
				);
			
			pD_Used.initialize();
			
			ScalarDisplayer sD_Used =
				(
					(ScalarDisplayer) s_ScalarScene.getDisplayerManager()
						.getDisplayer("Скаляр 1")
				);
			
			sD_Used.initialize();
			
			s_ScalarScene.setPresentationName(Name);
			
			switch (Name) {
				
				case "Распределение давления 2":
					
					PrimitiveFieldFunction pFF_UsedVel =
						(
							(PrimitiveFieldFunction) UsedSim.getFieldFunctionManager()
								.getFunction("Pressure")
						);
					
					sD_Used.getScalarDisplayQuantity().setFieldFunction(pFF_UsedVel);
					
					sD_Used.setFillMode(ScalarFillMode.NODE_SMOOTH);
					
					break;
				
				case "Распределение скоростей 2":
					
					PrimitiveFieldFunction pFF_UsedPress =
						(
							(PrimitiveFieldFunction) UsedSim.getFieldFunctionManager()
								.getFunction("Velocity")
						);
					
					VectorMagnitudeFieldFunction vMFF_Used =
						(
							(VectorMagnitudeFieldFunction) pFF_UsedPress.getMagnitudeFunction()
						);
					
					sD_Used.getScalarDisplayQuantity().setFieldFunction(vMFF_Used);
					
					sD_Used.setFillMode(ScalarFillMode.NODE_SMOOTH);
					
					break;
			}
			
			SetAnnotationsInScene(UsedSim, s_ScalarScene, AnnotationNamesPressure);
			
			Legend l_Used =
				sD_Used.getLegend();
			
			l_Used.setShadow(false);
		}
		
		public void MakeVectorScene(Simulation UsedSim, String Name){

		try {
		
			UsedSim.getSceneManager().createVectorScene("Сцена векторов", "Контур", "Вектор");

			} catch (Exception e) {}
			
			Scene s_VectorScene = 
				UsedSim.getSceneManager().getScene("Сцена векторов 1");
				
			s_VectorScene.setPresentationName("Вектора скорости");

			s_VectorScene.initializeAndWait();
			
			PartDisplayer pD_Used = 
				(
					(PartDisplayer) s_VectorScene.getDisplayerManager()
					.getDisplayer("Контур 1")
				);

			pD_Used.initialize();

			VectorDisplayer vD_Used = 
				(
					(VectorDisplayer) s_VectorScene.getDisplayerManager()
					.getDisplayer("Вектор 1")
				);

			vD_Used.initialize();
			
			vD_Used.setDisplayMode(VectorDisplayMode.VECTOR_DISPLAY_MODE_LIC);
			
			PrimitiveFieldFunction pFF_Used = 
				(
					(PrimitiveFieldFunction) UsedSim.getFieldFunctionManager()
					.getFunction("Velocity")
				);

			vD_Used.getVectorDisplayQuantity()
			.setFieldFunction(pFF_Used);
			
			SetAnnotationsInScene(UsedSim, s_VectorScene, AnnotationNames);
			
			Legend l_Used =
				vD_Used.getLegend();
			
			l_Used.setShadow(false);
		}
		
		public void MakeVectorScenePressure(Simulation UsedSim, String Name){

		try {
		
			UsedSim.getSceneManager().createVectorScene("Сцена векторов", "Контур", "Вектор");

			} catch (Exception e) {}
			
			Scene s_VectorScene =
				UsedSim.getSceneManager().getScene("Сцена векторов 1");
			
			s_VectorScene.setPresentationName("Вектора скорости");
			
			s_VectorScene.initializeAndWait();
			
			PartDisplayer pD_Used =
				(
					(PartDisplayer) s_VectorScene.getDisplayerManager()
						.getDisplayer("Контур 1")
				);
			
			pD_Used.initialize();
			
			VectorDisplayer vD_Used =
				(
					(VectorDisplayer) s_VectorScene.getDisplayerManager()
						.getDisplayer("Вектор 1")
				);
			
			vD_Used.initialize();
			
			vD_Used.setDisplayMode(VectorDisplayMode.VECTOR_DISPLAY_MODE_LIC);
			
			PrimitiveFieldFunction pFF_Used =
				(
					(PrimitiveFieldFunction) UsedSim.getFieldFunctionManager()
						.getFunction("Velocity")
				);
			
			vD_Used.getVectorDisplayQuantity()
				.setFieldFunction(pFF_Used);
			
			SetAnnotationsInScene(UsedSim, s_VectorScene, AnnotationNamesPressure);
			
			Legend l_Used =
				vD_Used.getLegend();
			
			l_Used.setShadow(false);
		}
		
		private void SetAnnotationsInScene(Simulation UsedSim, Scene UsedScene, String[] Names){
			
			for (String AnnName: Names){
				
				switch (AnnName){
					
					case "Solution Time":
						PhysicalTimeAnnotation pTA_Used = 
							((PhysicalTimeAnnotation) UsedSim.getAnnotationManager().getObject(AnnName));

						PhysicalTimeAnnotationProp pTAP_Used = 
							(PhysicalTimeAnnotationProp) UsedScene.getAnnotationPropManager().createPropForAnnotation(pTA_Used);
						break;
						
					case "Scene":
						SceneAnnotation sA_Used = 
							(
								(SceneAnnotation) UsedSim.getAnnotationManager().
								getObject("Scene")
							);

						SceneAnnotationProp sAP_Used = 
							(SceneAnnotationProp) UsedScene.getAnnotationPropManager().
							createPropForAnnotation(sA_Used);
						
						break;
						
					default:
						
							ReportAnnotation rA_Used = 
								(
									(ReportAnnotation) UsedSim.getAnnotationManager()
									.getObject(AnnName)
								);

							FixedAspectAnnotationProp fAAP_Used = 
								(FixedAspectAnnotationProp) UsedScene.getAnnotationPropManager().
								createPropForAnnotation(rA_Used);
				}
			}
		}
	}
	
	

}
