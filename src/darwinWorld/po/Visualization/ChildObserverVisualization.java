package darwinWorld.po.Visualization;

import darwinWorld.po.MapRelated.Animal;
import darwinWorld.po.MapRelated.AnimalObserver;
import darwinWorld.po.MapRelated.Vector2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChildObserverVisualization extends JPanel implements AnimalObserver, ActionListener {
    private int parentAmountOfChildren;
    private int totalAmountOfChildren;
    private int absoluteTotalAmountOfChildren = 0;
    private int absoluteParentAmountOfChildren;
    private int deathDay = -1;
    private final int startDay;
    private int dayAmount = 50;
    private boolean showAllStatistics = false;
    private int totalAmountOfChildrenAfterN;
    private int parentAmountOfChildrenAfterN;
    private final Animal animal;
    private final JFrame frame;
    private final Visualization visualization;

    private final NumberGetterFrame numberGetterFrame;

    public void calculateChildren(Animal animal){
        absoluteTotalAmountOfChildren += animal.getChildrenList().size();

        for (Animal animal1 : animal.getChildrenList()){
            calculateChildren(animal1);
        }
    }

    public ChildObserverVisualization(Animal animal, Visualization visualization) {
        totalAmountOfChildren = 0;
        parentAmountOfChildren = 0;
        this.animal = animal;
        this.visualization = visualization;
        this.startDay = visualization.getWorldMap().getDay();

        absoluteParentAmountOfChildren = animal.getChildrenList().size();
        calculateChildren(animal);

        this.frame = new JFrame("Animal " + animal.getAnimalID());
        frame.setSize(675, 325);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(visualization.getMapFrame());
        frame.add(this);
        frame.addKeyListener(visualization.getKeyboardListener());

        frame.repaint();


        numberGetterFrame = new NumberGetterFrame("Enter how many days observe", frame);

        numberGetterFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                visualization.animalIsClicked(animal);
                super.windowClosing(e);
            }
        });



        numberGetterFrame.addButtonActionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0,0,getWidth(),getHeight());
        g.setFont(g.getFont().deriveFont(18f));

        int y = 5;
        int x = 5;

        g.drawString(animal.getGenes().toString(), x, y += g.getFontMetrics().getHeight());

        g.setFont(g.getFont().deriveFont(20f));

        g.drawString("Amount of offspring after starting simulation " +
                absoluteTotalAmountOfChildren, x, y += g.getFontMetrics().getHeight());
        g.drawString("Amount of children after starting simulation " +
                absoluteParentAmountOfChildren, x, y += g.getFontMetrics().getHeight());

        g.drawString("Amount of offspring " + totalAmountOfChildren, x, y += g.getFontMetrics().getHeight());
        g.drawString("Amount of children " + parentAmountOfChildren, x, y += g.getFontMetrics().getHeight());

        if (deathDay != -1){
            g.drawString("Death day " + deathDay, x, y += g.getFontMetrics().getHeight());
        } else {
            g.drawString("Animal is alive",x, y += g.getFontMetrics().getHeight());
            g.drawString("Animal energy " + animal.getEnergy(),x, y += g.getFontMetrics().getHeight());
        }

        y+= g.getFontMetrics().getHeight();

        if (showAllStatistics){
            g.drawString("Amount of offspring after " + dayAmount +" days " +
                    totalAmountOfChildrenAfterN, x, y += g.getFontMetrics().getHeight());
            g.drawString("Amount of children after " + dayAmount +" days " +
                    parentAmountOfChildrenAfterN, x, y += g.getFontMetrics().getHeight());
        }
    }

    @Override
    public void animalMoved(Animal animal, Vector2d oldPosition) {

    }

    @Override
    public void animalHasDied(Animal animal) {
        animal.removeObserver(this);
    }

    @Override
    public void animalNeedsToChangeEnergy(Animal animal, int energyDiff) {
    }

    @Override
    public void animalHasChildWith(Animal animal, Animal other, Animal child) {
        if (deathDay != -1 || showAllStatistics) {
            animal.removeObserver(this);
            return;
        }

        child.addObserver(this);
        totalAmountOfChildren += 1;
        absoluteTotalAmountOfChildren += 1;
        frame.repaint();
    }

    public void parentHasChild(){
        totalAmountOfChildren +=1;
        parentAmountOfChildren +=1;

        absoluteParentAmountOfChildren += 1;
        absoluteTotalAmountOfChildren += 1;
        frame.repaint();
    }

    public void parentIsDead(int deathDay){
        this.deathDay = deathDay;
        dayAmount = visualization.getWorldMap().getDay() - startDay;

        showAllStatistics = true;
        frame.setVisible(true);
        frame.repaint();
        visualization.endObservation(animal);
    }

    public void dayPassed(){
        if (visualization.getWorldMap().getDay() - startDay == dayAmount){
            totalAmountOfChildrenAfterN = totalAmountOfChildren;
            parentAmountOfChildrenAfterN = parentAmountOfChildren;
            frame.setVisible(true);
            showAllStatistics = true;
            frame.repaint();
            visualization.endObservation(animal);
        }
    }

    @Override
    public String toString() {
        return "ChildObserverVisualization{" +
                "parentAmountOfChildren=" + parentAmountOfChildren +
                ", totalAmountOfChildren=" + totalAmountOfChildren +
                ", deathDay=" + deathDay +
                '}';
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == numberGetterFrame.button){
            String gotVal = numberGetterFrame.numberGetter.getText();
            if(gotVal.length() != 0)
                dayAmount = Integer.parseInt(gotVal);
            else
                dayAmount = Integer.MAX_VALUE;
            numberGetterFrame.setVisible(false);
            frame.setVisible(true);
        }
    }

    public JFrame getFrame() {
        return frame;
    }
}
