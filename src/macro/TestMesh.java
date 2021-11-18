package macro;

import star.common.StarMacro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMesh extends StarMacro {
    
    @Override
    public void execute() {
    
    }
    
    /**
     * Класс данных для ввода
     */
    public class SimDataParam {
//        Map<String, Double> simMap = new HashMap<>();
        private double meshCellSize;
    
        public SimDataParam(double meshCellSize) {
            this.meshCellSize = meshCellSize;
        }
    
        public double getMeshCellSize() {
            return meshCellSize;
        }
    
        public void setMeshCellSize(double meshCellSize) {
            this.meshCellSize = meshCellSize;
        }
    }
    
    /**
     * Класс данных для вывода
     */
    public class SimDataReport {
        private double liftDragRatio;
        private double drag;
        private double lift;
        private double elapsedTime;
    
        public SimDataReport(double liftDragRatio, double drag, double lift, double elapsedTime) {
            this.liftDragRatio = liftDragRatio;
            this.drag = drag;
            this.lift = lift;
            this.elapsedTime = elapsedTime;
        }
    
        public double getLiftDragRatio() {
            return liftDragRatio;
        }
    
        public void setLiftDragRatio(double liftDragRatio) {
            this.liftDragRatio = liftDragRatio;
        }
    
        public double getDrag() {
            return drag;
        }
    
        public void setDrag(double drag) {
            this.drag = drag;
        }
    
        public double getLift() {
            return lift;
        }
    
        public void setLift(double lift) {
            this.lift = lift;
        }
    
        public double getElapsedTime() {
            return elapsedTime;
        }
    
        public void setElapsedTime(double elapsedTime) {
            this.elapsedTime = elapsedTime;
        }
    }
    
    
}
