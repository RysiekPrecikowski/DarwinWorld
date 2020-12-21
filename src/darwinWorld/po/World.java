package darwinWorld.po;

import darwinWorld.po.Simulation.Simulation;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class World {
    public static void main(String[] args) throws FileNotFoundException {
        Simulation simulation = new Simulation();
        System.out.println(new String[]{"A"} == new String[]{"A"});

        try {
            simulation.addSimulation();
//            simulation.addSimulation();
        }catch (Exception e){
            System.out.println(e.toString());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }


    }
}
