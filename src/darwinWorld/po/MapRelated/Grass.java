package darwinWorld.po.MapRelated;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

public class Grass implements MapElement {

    private Vector2d position;

    public int getEnergy() {
        return energy;
    }

    private final int energy;

    private final LinkedList<GrassObserver> observers = new LinkedList<>();

    public Grass(Vector2d position, int energy) {
        this.position = position;
        this.energy = energy;
    }

    /**
     * Notify observers that this grass will be eaten in this day,
     * also calculate amount of energy that animals will gain.
     */
    public void toBeEaten() {
        int energyGained = (int) ((float) energy / (float) getAmountOfMaxEnergyObservers());

        for (GrassObserver observer : observers) {
            observer.eatGrass(energyGained);
        }
    }

    public void addObserver(GrassObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(GrassObserver observer) {
        this.observers.remove(observer);
    }


    /**
     * @return amount of animals - observers
     * that have the biggest energy
     */
    public int getAmountOfMaxEnergyObservers() {
        int maxEnergyObservers = 0;
        int maxEnergy = 0;
        observers.sort(GrassObserver::compareTo);
        Collections.reverse(observers);

        for(GrassObserver o : observers){
            if (o instanceof Animal) {
                Animal animal = (Animal) o;
                maxEnergy = Math.max(maxEnergy, animal.getEnergy());

                if (animal.getEnergy() == maxEnergy)
                    maxEnergyObservers += 1;
            }
        }
        return maxEnergyObservers;
    }

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(Vector2d position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grass grass = (Grass) o;
        return getPosition().equals(grass.getPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPosition());
    }

    @Override
    public String toString() {
        return "*";
    }
}

