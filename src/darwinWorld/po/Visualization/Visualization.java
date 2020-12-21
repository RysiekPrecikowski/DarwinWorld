package darwinWorld.po.Visualization;

import darwinWorld.po.MapRelated.Animal;
import darwinWorld.po.MapRelated.AnimalObserver;
import darwinWorld.po.MapRelated.Vector2d;
import darwinWorld.po.WorldRelated.WorldMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class Visualization implements ActionListener, AnimalObserver {
    private final WorldMap worldMap;
    private final KeyboardListener keyboardListener = new KeyboardListener(this);

    private boolean genesMarked = false;
    private LinkedList<Animal> markedAnimals;
    private final HashMap<Animal, ChildObserverVisualization> selectedAnimals = new HashMap<>();

    private final int startingAnimals;

    private final JFrame mapFrame;
    private final JFrame statisticsFrame = new JFrame("Statistics");
    private final MapRenderer mapRenderer;
    private final StatisticsRenderer statisticsRenderer;
    private final Timer timer;

    private final NumberGetterFrame startingQuestion;
    private int afterHowManyDaysSave = Integer.MAX_VALUE;

    public Visualization(int delay, WorldMap worldMap, int startingAnimals){
        this.worldMap = worldMap;
        this.startingAnimals = startingAnimals;

        timer = new Timer(delay, this);

        mapFrame = new JFrame("Darwin World");
        mapFrame.setSize(1000,1000);
        mapFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mapFrame.setLocationRelativeTo(null);
        mapFrame.setVisible(true);
        mapFrame.setFocusable(true);
        mapFrame.addKeyListener(keyboardListener);

        statisticsFrame.setSize(500,550);
        statisticsFrame.setLocationRelativeTo(mapFrame);
        statisticsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        statisticsFrame.setVisible(true);
        statisticsFrame.addKeyListener(keyboardListener);

        mapRenderer = new MapRenderer(this.worldMap,this);
        mapFrame.add(mapRenderer);

        statisticsRenderer = new StatisticsRenderer(this.worldMap);
        statisticsFrame.add(statisticsRenderer);

        startingQuestion = new NumberGetterFrame("enter after how many days save statistics", mapFrame);
        startingQuestion.addButtonActionListener(this);
    }

    void startSimulation(){
        worldMap.getStatistics().setDayToSaveSimulation(afterHowManyDaysSave);
        worldMap.startSimulation(startingAnimals);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mapRenderer.repaint();
        statisticsRenderer.repaint();


        if (e.getSource() == startingQuestion.button){
            String gotVal = startingQuestion.numberGetter.getText();
            if(gotVal.length() != 0)
                afterHowManyDaysSave = Integer.parseInt(gotVal);
            startingQuestion.setVisible(false);
            startSimulation();
        }

        if((e.getSource() == timer)){
            try {
                worldMap.nextDay();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            if(genesMarked)
                markGenes();
            LinkedList<ChildObserverVisualization> childObserverVisualizations = new LinkedList<>(selectedAnimals.values());
            for (ChildObserverVisualization childObserver : childObserverVisualizations){
                childObserver.dayPassed();
            }
        }
    }

    public void unmarkGenes(){
        if (genesMarked){
            if(markedAnimals != null){
                for (Animal animal : markedAnimals) {
                    if (!selectedAnimals.containsKey(animal))
                        animal.resetColor();
                }
                markedAnimals.clear();
            }
            genesMarked = false;
        }
    }

    public void markGenes(){
        unmarkGenes();
        markedAnimals = new LinkedList<>(worldMap.getStatistics().getGenesMap().get(worldMap.getStatistics().getBestGenes()));
        for(Animal animal : markedAnimals){
            if (!selectedAnimals.containsKey(animal))
                animal.changeColor(new Color(255, 0, 0));
        }
        genesMarked = true;
    }

    public void endObservation(Animal animal){
        selectedAnimals.remove(animal);
        animal.removeObserver(this);
        animal.resetColor();
    }

    public void animalIsClicked(Animal animal){
        if (!selectedAnimals.containsKey(animal)){
//            selectAnimal
            animal.changeColor(new Color(255, 0, 0, 255));
            animal.addObserver(this);
            selectedAnimals.put(animal, new ChildObserverVisualization(animal, this));

        } else{
//             unselectAnimal

            animal.resetColor();
            animal.removeObserver(this);
            selectedAnimals.get(animal).getFrame().setVisible(false);
            selectedAnimals.remove(animal);
        }
        mapRenderer.repaint();
    }

    @Override
    public void animalMoved(Animal animal, Vector2d oldPosition) {
    }

    @Override
    public void animalHasDied(Animal animal) {
        selectedAnimals.get(animal).parentIsDead(worldMap.getDay());
        animal.removeObserver(this);
    }

    @Override
    public void animalNeedsToChangeEnergy(Animal animal, int energyDiff) {
        if (selectedAnimals.containsKey(animal))
            selectedAnimals.get(animal).getFrame().repaint();
    }

    @Override
    public void animalHasChildWith(Animal animal, Animal other, Animal child) {
        selectedAnimals.get(animal).parentHasChild();
        selectedAnimals.get(animal).animalHasChildWith(animal, other, child);

    }

    public KeyboardListener getKeyboardListener() {
        return keyboardListener;
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public JFrame getMapFrame() {
        return mapFrame;
    }

    public Timer getTimer() {
        return timer;
    }

    public boolean areGenesMarked() {
        return genesMarked;
    }
}
