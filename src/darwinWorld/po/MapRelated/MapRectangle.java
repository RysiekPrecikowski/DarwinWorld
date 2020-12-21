package darwinWorld.po.MapRelated;

import darwinWorld.po.WorldRelated.WorldMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapRectangle extends Rectangle {
    private final Map<Vector2d, Grass> grassMap = new HashMap<>();
    private final ConcurrentHashMap<Vector2d, TreeSet<Animal>> animalMap = new ConcurrentHashMap<>(); // concurrent hashmap,
                                                                                                        // bo byly problemy podczas wizualizacji,
                                                                                                        // przez użycie timera repaint mapy odbywał się
                                                                                                        // czasem w trakcie działania na hashmapie, co powodowało błędy
    private final HashSet<Animal> deadAnimals = new HashSet<>();

    private final WorldMap worldMap;

    private int maxAnimalsPerCell = Integer.MAX_VALUE;

    public MapRectangle(int width, int height, WorldMap worldMap) {
        super(width, height);
        this.worldMap = worldMap;
    }

    public TreeSet<Animal> getAnimalsAt(Vector2d position) {
        position = fixPosition(position);
        return animalMap.get(position);
    }

    public MapElement getElementAt(Vector2d position) {
        //animals have priority
        position = fixPosition(position);
        TreeSet<Animal> animals = getAnimalsAt(position);
        if (animals == null)
            return grassMap.get(position);

        return animals.last();
    }

    public int getAmountOfOccupiedPositions(){
        return getAmountOfOccupiedPositions(this);
    }

    public int getAmountOfOccupiedPositions(Rectangle area){
        int amount = 0;
        for(Vector2d position : grassMap.keySet()){
            if(area.contains(position)){
                amount += 1;
            }
        }

        for(Vector2d position : animalMap.keySet()){
            if (area.contains(position)) {
                if (!grassMap.containsKey(position))
                    amount += 1;
            }
        }
        return amount;
    }

    public boolean canPlaceAnimalAt(Vector2d position) {
        position = fixPosition(position);
        if (animalMap.get(position) == null)
            return true;

        return animalMap.get(position).size() < this.maxAnimalsPerCell;
    }

    public boolean isOccupied(Vector2d position) {
        position = fixPosition(position);
        return getElementAt(position) != null;
    }

    public void place(Animal animal) {
        animal.setAnimalID(worldMap.getAnimalID());
        addAnimal(animal);
        animal.addObserver(this.worldMap);
        worldMap.setAnimalID(worldMap.getAnimalID() + 1);

        worldMap.getStatistics().animalPlaced(animal);
    }

    public void place(Grass grass) {
        Vector2d position = fixPosition(grass.getPosition());
        grass.setPosition(position);

        if (isOccupied(position))
            throw new IllegalArgumentException("cannot place grass " + grass.toString() + " at " + position.toString());
        grassMap.put(position, grass);
    }

    public void addAnimal(Animal animal) {
        Vector2d position = fixPosition(animal.getPosition());
        animal.setPosition(position);

        if (!canPlaceAnimalAt(position))
            throw new IllegalArgumentException("cannot place animal " + animal.toString() + " at " + position.toString());
        TreeSet<Animal> animals = animalMap.get(position);
        if (animals == null) {
            animals = new TreeSet<>();
            animals.add(animal);
            animalMap.put(position, animals);
        } else {
            animals.add(animal);
        }
    }

    public  void remove(Animal animal, Vector2d oldPosition) {
        TreeSet<Animal> animals = animalMap.get(oldPosition);
        if (animals == null)
            throw new IllegalArgumentException("position empty, animal doesnt exist" + oldPosition.toString());
        if (animals.size() == 0)
            throw new IllegalArgumentException("position contains an empty list " + oldPosition.toString());

        animals.remove(animal);
        if (animals.size() == 0) {
            animalMap.remove(oldPosition);
        }
    }

    public void remove(Grass grass) {
        if (grassMap.get(grass.getPosition()) == null)
            throw new IllegalArgumentException("position empty, grass doesnt exist" + grass.getPosition().toString());
        grassMap.remove(grass.getPosition());
    }

    public LinkedList<Animal> getAllAnimals() {
        LinkedList<Animal> animals = new LinkedList<>();

        for (Vector2d position : animalMap.keySet()) {
            for (Animal animal : animalMap.get(position)) {
                if (animal.isAlive())
                    animals.add(animal);
            }
        }
        return animals;
    }

    public Map<Vector2d, Grass> getGrassMap() {
        return grassMap;
    }

    public Map<Vector2d, TreeSet<Animal>> getAnimalMap() {
        return animalMap;
    }

    public HashSet<Animal> getDeadAnimals() {
        return deadAnimals;
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public int getMaxAnimalsPerCell() {
        return maxAnimalsPerCell;
    }

    public void setMaxAnimalsPerCell(int maxAnimalsPerCell) {
        this.maxAnimalsPerCell = maxAnimalsPerCell;
    }
}
