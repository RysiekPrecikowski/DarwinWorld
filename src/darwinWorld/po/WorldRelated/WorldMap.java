package darwinWorld.po.WorldRelated;

import darwinWorld.po.MapRelated.*;
import darwinWorld.po.Simulation.Parameters;
import darwinWorld.po.Simulation.Statistics;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

public class WorldMap implements AnimalObserver, WorldActions {
    private final int grassEnergy;
    private final int energyPerDay;
    private final int startEnergy;
    private int grassPerDaySteppe = 1;
    private int grassPerDayJungle = 1;
    private int worldID = 0;

    public void setAnimalID(int animalID) {
        this.animalID = animalID;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    private final Statistics statistics;


    private int animalID = 0;
    private int day = 0;
    private final MapRectangle map;
    private Rectangle jungle;

    private void setJungleCords(float jungleRatio) {
        int jungleHeight = (int) ((float) this.map.getHeight() * jungleRatio);
        int jungleWidth = (int) ((float) this.map.getWidth() * jungleRatio);

        int llx = (int) ((float) (this.map.getWidth() - jungleWidth) / 2);
        int lly = (int) ((float) (this.map.getHeight() - jungleHeight) / 2);

        Vector2d lowerLeftJungle = new Vector2d(llx, lly);
        Vector2d upperRightJungle = new Vector2d(llx + jungleWidth - 1, lly + jungleHeight - 1);

        this.jungle = new Rectangle(lowerLeftJungle, upperRightJungle);
    }

    public WorldMap(Parameters parameters){
        startEnergy = parameters.getStartEnergy();
        energyPerDay = parameters.getEnergyPerDay();
        grassEnergy = parameters.getGrassEnergy();
        this.map = new MapRectangle(parameters.getWidth(), parameters.getHeight(), this);
        setJungleCords(parameters.getJungleRatio());
        grassPerDaySteppe = parameters.getGrassPerDaySteppe();
        grassPerDayJungle = parameters.getGrassPerDayJungle();

        statistics = new Statistics(this);
    }

    public WorldMap(int width, int height, int startEnergy, int energyPerDay, int grassEnergy, float jungleRatio) {
        this.startEnergy = startEnergy;
        this.energyPerDay = energyPerDay;
        this.grassEnergy = grassEnergy;

        this.map = new MapRectangle(width, height, this);

        setJungleCords(jungleRatio);
        statistics = new Statistics(this);

    }

    public WorldMap(int width, int height, int startEnergy, int energyPerDay, int grassEnergy, float jungleRatio, int worldID){
        this(width, height, startEnergy, energyPerDay, grassEnergy, jungleRatio);
        this.worldID = worldID;
    }

    public WorldMap(Parameters parameters, int worldID){
        this(parameters);
        this.worldID = worldID;
    }

    public void startSimulation(int startingAnimals) {
        if (startingAnimals > map.getSize())
            throw new IllegalArgumentException("amount of starting animals is too high! size: " + map.getSize() + " > " + startingAnimals);

        for (int i = 0; i < startingAnimals; i += 1) {
            Random random = new Random();
            Vector2d position = new Vector2d(0,0);
            do {
                int x = random.nextInt(this.map.getWidth());
                int y = random.nextInt(this.map.getHeight());
                position.setX(x);
                position.setY(y);
            } while (map.isOccupied(position));

            Animal animal = new Animal(position, startEnergy, this);
            map.place(animal);
        }
    }

    @Override
    public void animalHasDied(Animal animal) {
        map.remove(animal, animal.getPosition());
        animal.removeObserver(this);
        map.getDeadAnimals().add(animal);

        statistics.animalDied(animal);
    }

    @Override
    public void animalMoved(Animal animal, Vector2d oldPosition) {
        map.remove(animal, oldPosition);
        map.addAnimal(animal);
    }

    @Override
    public void animalNeedsToChangeEnergy(Animal animal, int energyDiff) {
        map.remove(animal, animal.getPosition());

        animal.changeEnergy(energyDiff);
//        currentEnergy += energyDiff;gg
        statistics.actualizeEnergy(energyDiff);

        map.addAnimal(animal);
        animal.isAlive();//            animal.hasDied();
    }

    @Override
    public void animalHasChildWith(Animal animal, Animal other, Animal child) {
        map.place(child);
//        currentChildrenCount += 2;
        statistics.actualizeChildrenAmount(2);
    }

    private int getRandomNumber(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    @Override
    public void spawnGrass() {
        int howManyTimesTry = 1;
        for (int i = 0; i < grassPerDayJungle; i++) {
            Vector2d position = new Vector2d(0, 0);

            if (map.getAmountOfOccupiedPositions(jungle) < map.getSize()) {
                int iterator = 0;
                do {
                    int x = getRandomNumber(this.jungle.getLowerLeft().getX(), this.jungle.getUpperRight().getX());
                    int y = getRandomNumber(this.jungle.getLowerLeft().getY(), this.jungle.getUpperRight().getY());

                    position.setX(x);
                    position.setY(y);
                    iterator += 1;

                } while (map.isOccupied(position) && iterator < howManyTimesTry * jungle.getSize());

                if (iterator == howManyTimesTry * jungle.getSize()) {
                    continue;
                }
                Grass grass = new Grass(position, grassEnergy);
                map.place(grass);

            }
        }

        for (int i = 0; i < grassPerDaySteppe; i++) {
            Vector2d position = new Vector2d(0, 0);
            if (map.getAmountOfOccupiedPositions(map) - map.getAmountOfOccupiedPositions(jungle) < map.getSize() - jungle.getSize()) {
                int iterator = 0;
                do {
                    int x = getRandomNumber(this.map.getLowerLeft().getX(), this.map.getUpperRight().getX());
                    int y = getRandomNumber(this.map.getLowerLeft().getY(), this.map.getUpperRight().getY());

                    position.setX(x);
                    position.setY(y);
                    iterator += 1;

                } while ((map.isOccupied(position) ||
                        jungle.contains(position)) &&
                        iterator < howManyTimesTry * map.getSize() );

                if (iterator == howManyTimesTry * map.getSize())
                    continue;

                Grass grass = new Grass(position, grassEnergy);
                map.place(grass);
            }
        }

        statistics.actualizeGrassAmount(map.getGrassMap().size());
    }

    @Override
    public void useAnimalEnergyPerDay() {
        LinkedList<Animal> animals = map.getAllAnimals();
        for (Animal animal : animals) {
            animal.energyChanged(-energyPerDay);
        }
    }

    @Override
    public void moveAllAnimals(HashSet<Grass> grassToBeEaten, HashSet<Vector2d> animalsToCopulatePositions) {
        LinkedList<Animal> animals = map.getAllAnimals();
        for (Animal animal : animals) {
            Vector2d oldPosition = animal.getPosition();

            animal.move();
            animal.rotate();

            if (animalsToCopulatePositions.contains(oldPosition)) {
                if (!map.getAnimalMap().containsKey(oldPosition) || map.getAnimalMap().get(oldPosition).size() <= 1) {
                    animalsToCopulatePositions.remove(oldPosition);
                }
            }

            if (map.getAnimalMap().get(animal.getPosition()).size() == 2) {
                animalsToCopulatePositions.add(animal.getPosition());
            }

            Grass grass = map.getGrassMap().get(animal.getPosition());
            if (grass != null) {
                animal.visitGrass(grass, grassToBeEaten);
            }
        }
    }

    @Override
    public void eatGrassOnMap(HashSet<Grass> grassToBeEaten) {
        for (Grass grass : grassToBeEaten) {
            grass.toBeEaten();
            map.remove(grass);
        }
    }

    @Override
    public void copulation(HashSet<Vector2d> animalsToCopulatePositions) {
        for (Vector2d position : animalsToCopulatePositions) {

            TreeSet<Animal> animalsAt = map.getAnimalsAt(position);

            Animal mother = animalsAt.last();
            Animal father = animalsAt.lower(mother);

            if (father == null || mother == null)
                throw new IllegalArgumentException("parent doesnt exist at " + position.toString());

            if (father.getEnergy() >= startEnergy / 2 && mother.getEnergy() >= startEnergy / 2) {
                mother.copulate(father);
            }
        }
    }

    @Override
    public void nextDay() throws IOException {
        HashSet<Grass> grassToBeEaten = new HashSet<>();
        HashSet<Vector2d> animalsToCopulatePositions = new HashSet<>();

        moveAllAnimals(grassToBeEaten, animalsToCopulatePositions);

        eatGrassOnMap(grassToBeEaten);

        copulation(animalsToCopulatePositions);

        useAnimalEnergyPerDay();
        spawnGrass();

        statistics.nextDay(day);

        this.day += 1;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("day: ").append(this.day).append("\n");

        for (int y = this.map.getUpperRight().getY(); y >= this.map.getLowerLeft().getY(); y -= 1) {
            for (int x = this.map.getLowerLeft().getX(); x <= this.map.getUpperRight().getX(); x += 1) {
                Vector2d pos = new Vector2d(x, y);
                if (this.map.isOccupied(pos)) {
                    TreeSet<Animal> animals = map.getAnimalsAt(pos);
                    if (animals != null && animals.size() > 1) {
                        res.append(animals.size());
                    } else
                        res.append(this.map.getElementAt(pos));
                } else if (jungle.contains(pos)) {
                    res.append("|");
                } else {
                    res.append("_");
                }
            }
            res.append("\n");
        }

        res.append("population: ").append(statistics.getCurrentAmountOfAnimals()).append("\n");
        res.append("grass: ").append(map.getGrassMap().size()).append("\n");

//        res.append("\ntotal amount of animals: ").append(animalID).append("\n");
        res.append("\nStatistics:\n");
        res.append(statistics.getGenesStatistics());

        res.append(statistics.getAnimalsStatistics());

        return res.toString();
    }

    public int getEnergyPerDay() {
        return energyPerDay;
    }

    public int getStartEnergy() {
        return startEnergy;
    }

    public MapRectangle getMap() {
        return map;
    }

    public Rectangle getJungle() {
        return jungle;
    }

    public int getAnimalID() {
        return animalID;
    }

    public int getDay() {
        return day;
    }

    public void setGrassPerDaySteppe(int i) {
        grassPerDaySteppe = i;
    }

    public void setGrassPerDayJungle(int i) {
        grassPerDayJungle = i;
    }

    public int getWorldID() {
        return worldID;
    }
}
