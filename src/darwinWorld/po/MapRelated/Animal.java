package darwinWorld.po.MapRelated;

import darwinWorld.po.WorldRelated.WorldMap;

import java.awt.*;
import java.util.*;

public class Animal implements MapElement, Comparable, GrassObserver {
    private Vector2d position;
    private Direction direction = Direction.NORTH;
    private int energy;

    private final int amountOfGenes = 32;

    private final WorldMap worldMap;
    private int animalID;
    private final int dateOfBirth;
    private int dateOfDeath = -1;

    private final LinkedList<Animal> parents = new LinkedList<>();
    private final LinkedList<Animal> childrenList = new LinkedList<>();

    private final ArrayList<AnimalObserver> observers = new ArrayList<>();

    private final Genes genes;

    private final Color baseColor = new Color(153, 35, 255);
    private Color actualColor = baseColor;

    public Animal(Vector2d position, int energy, WorldMap worldMap) {
        this.position = position;
        this.energy = energy;
        this.worldMap = worldMap;

        this.dateOfBirth = worldMap.getDay();
        this.genes = new Genes(amountOfGenes);
        rotate();
    }

    public Animal(Vector2d position, int energy, Genes genes, WorldMap worldMap) {
        this.position = position;
        this.energy = energy;
        this.worldMap = worldMap;

        this.dateOfBirth = worldMap.getDay();
        this.genes = genes;
        rotate();
    }

    public void setAnimalID(int animalID) {
        this.animalID = animalID;
    }

    public int getLifeDuration(){
        if (dateOfDeath != -1){
            return dateOfDeath - dateOfBirth;
        }else
            return worldMap.getDay() - dateOfBirth;
    }

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public void changeEnergy(int value) {
        this.energy += value;
//        this.energyChanged(value);
    }

    public boolean isAlive() {
        boolean alive = this.energy > 0;
        if (!alive){
            this.hasDied();
            return false;
        }

        return true;
    }

    public void rotate() {
        Random rand = new Random();
        int howManyTimesRotate = genes.getGenes()[rand.nextInt(amountOfGenes)];

        this.direction = Direction.values()[(this.direction.ordinal() + howManyTimesRotate) % Direction.values().length];
    }

    public void move() {
        Vector2d newPosition = this.getPosition().add(this.direction.directionToVector());
        newPosition = worldMap.getMap().fixPosition(newPosition);

        if (worldMap.getMap().canPlaceAnimalAt(newPosition)) {
            Vector2d oldPosition = new Vector2d(this.getPosition().getX(), this.getPosition().getY());
            this.position = newPosition;

            this.positionChanged(oldPosition);
        }
    }

    private void createChild(Animal other, Vector2d childPosition){
        int thisEnergyLoss = this.energy / 4;
        int otherEnergyLoss = other.energy / 4;

        this.energyChanged(-thisEnergyLoss);
        other.energyChanged(-otherEnergyLoss);

        Genes childGenes = this.genes.getMixedGenes(other.genes);

        Animal child = new Animal(childPosition, thisEnergyLoss + otherEnergyLoss, childGenes, this.worldMap);

        hasChildWith(other, child);
    }

    public void copulate(Animal other) {
        Random random = new Random();
        Direction startingDirection = Direction.values()[random.nextInt(Direction.values().length)];
        Direction direction;
        boolean succes = false;
        for (int i = 0; i < Direction.values().length ; i+=1) {
            direction = startingDirection.next();
            Vector2d childPosition = position.add(direction.directionToVector());
            if (!worldMap.getMap().isOccupied(childPosition)) {
                createChild(other, childPosition);

                succes = true;
                break;
            }
        }

        if(!succes){
            Vector2d childPosition = position.add(startingDirection.directionToVector());
            createChild(other, childPosition);
        }
    }

    public void visitGrass(Grass grass, HashSet<Grass> grassToBeEaten) {
        grass.addObserver(this);

        grassToBeEaten.add(grass);
    }

    //observer
    private void positionChanged(Vector2d oldPosition) {
        for (AnimalObserver o : observers) {
            o.animalMoved(this, oldPosition);
        }
    }

    public void energyChanged(int energyDiff) {
        ArrayList<AnimalObserver> observers = new ArrayList<>(this.observers);
        for (AnimalObserver o : observers) {
            o.animalNeedsToChangeEnergy(this, energyDiff);
        }
    }

    public void hasDied() {
        dateOfDeath = worldMap.getDay();
        ArrayList<AnimalObserver> observers = new ArrayList<>(this.observers);
        for (AnimalObserver o : observers) {
            o.animalHasDied(this);
        }
    }

    private void hasChildWith(Animal other, Animal child) {
        this.childrenList.add(child);
        other.childrenList.add(child);

        child.parents.add(this);
        child.parents.add(other);
        ArrayList<AnimalObserver> observers = new ArrayList<>(this.observers);
        for (AnimalObserver o : observers) {
            o.animalHasChildWith(this, other, child);
        }
    }

    @Override
    public void eatGrass(int energyGained) {
        this.energyChanged(energyGained);
    }

    public void addObserver(AnimalObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(AnimalObserver observer) {
        this.observers.remove(observer);
    }


    @Override
    public String toString() {
        if (this.isAlive()) {
            return this.direction.toString();
        } else {
            return "X";
        }
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass())
            throw new IllegalArgumentException("could not compare " + this + " with " + o);

        int c = Integer.compare(this.energy, ((Animal) o).energy);
        if (c == 0)
            return Integer.compare(this.animalID, ((Animal) o).animalID);
        else
            return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return amountOfGenes == animal.amountOfGenes &&
                animalID == animal.animalID &&
                dateOfBirth == animal.dateOfBirth &&
                Objects.equals(genes, animal.genes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amountOfGenes, animalID, dateOfBirth, genes);
    }

    private Color toColor(Color color){
        int divide = 3;
//        double range = 1;
        int r,g,b;

        r = (int) ((double) (worldMap.getStartEnergy() / divide + energy) / (double) (worldMap.getStartEnergy() + energy) * color.getRed());
        g = (int) ((double) (worldMap.getStartEnergy() / divide + energy) / (double) (worldMap.getStartEnergy() + energy) * color.getGreen());
        b = (int) ((double) (worldMap.getStartEnergy() / divide + energy) / (double) (worldMap.getStartEnergy() + energy) * color.getBlue());

        return new Color(r,g,b);
    }

    public Color toColor(){
        return toColor(actualColor);
    }

    public void resetColor(){
        actualColor = baseColor;
    }

    public void changeColor(Color color){
        actualColor = color;
    }


    public Direction getDirection() {
        return direction;
    }

    public int getEnergy() {
        return energy;
    }

    public int getAnimalID() {
        return animalID;
    }

    public LinkedList<Animal> getParents() {
        return parents;
    }

    public LinkedList<Animal> getChildrenList() {
        return childrenList;
    }

    public ArrayList<AnimalObserver> getObservers() {
        return observers;
    }

    public Genes getGenes() {
        return genes;
    }
}
