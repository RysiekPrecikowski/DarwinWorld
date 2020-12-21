package darwinWorld.po.Simulation;

import darwinWorld.po.Visualization.Visualization;
import darwinWorld.po.WorldRelated.WorldMap;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Simulation {
    List<Visualization> visualizations = new ArrayList<>();
    Parameters parameters;
    int worldID = 0;

    public Simulation() throws FileNotFoundException {
        parameters = Parameters.loadFromJson();
        parameters.validate();
    }

    public void addSimulation(){
        worldID+=1;
        WorldMap worldMap = new WorldMap(parameters, worldID);
        Visualization visualization = new Visualization(parameters.delay, worldMap, parameters.startingAnimals);
        visualizations.add(visualization);
    }
}

