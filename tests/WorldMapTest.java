import darwinWorld.po.MapRelated.Animal;
import darwinWorld.po.MapRelated.Grass;
import darwinWorld.po.MapRelated.Vector2d;
import darwinWorld.po.WorldRelated.WorldMap;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WorldMapTest {
    Vector2d position = new Vector2d(0,0);
    WorldMap worldMap = new WorldMap(100,100, 100, 1, 1, 0.3f);
    Animal animal = new Animal(position, 100, worldMap);
    WorldMap mockedMap = mock(WorldMap.class);

    @Before
    public void setUp(){
        worldMap.getMap().place(animal);
        animal.addObserver(mockedMap);
    }

    @Test
    public void animalHasDied() {
        animal.hasDied();
        verify(mockedMap).animalHasDied(animal);

        assertFalse(worldMap.getMap().getAllAnimals().contains(animal));

        assertFalse(animal.getObservers().contains(worldMap));

        assertTrue(worldMap.getMap().getDeadAnimals().contains(animal));
    }

    @Test
    public void animalMoved() {
        animal.move();
        verify(mockedMap).animalMoved(animal, position);

        assertNull(worldMap.getMap().getAnimalsAt(position));
        assertTrue(worldMap.getMap().getAnimalsAt(animal.getPosition()).contains(animal));
    }

    @Test
    public void animalNeedsToChangeEnergy() {
        int energyDiff = 10;
        animal.energyChanged(energyDiff);

        verify(mockedMap).animalNeedsToChangeEnergy(animal,energyDiff);

        assertTrue(worldMap.getMap().getAnimalsAt(animal.getPosition()).contains(animal));

        energyDiff = -animal.getEnergy();

        animal.energyChanged(energyDiff);
        verify(mockedMap).animalNeedsToChangeEnergy(animal, energyDiff);

        verify(mockedMap).animalHasDied(animal);

        assertNull(worldMap.getMap().getAnimalsAt(animal.getPosition()));
    }

    @Test
    public void animalHasChildWith() {
        Animal animal1 = new Animal(position, 100, worldMap);

        Animal child = new Animal(position.add(new Vector2d(1,1)), 100, worldMap);

        worldMap.animalHasChildWith(animal1, animal, child);

        assertTrue(worldMap.getMap().getAnimalsAt(position.add(new Vector2d(1,1))).contains(child));
    }

    @Test
    public void useAnimalEnergyPerDay() {
        worldMap.startSimulation(100);

        LinkedList<Animal> animals = worldMap.getMap().getAllAnimals();
        LinkedList<Integer> animalsEnergy = new LinkedList<>();

        for (Animal animal : animals) {
            animalsEnergy.add(animal.getEnergy());
        }
        worldMap.useAnimalEnergyPerDay();

        for (int i = 0 ; i < animals.size() ; i++){
            assertEquals(animals.get(i).getEnergy(), animalsEnergy.get(i) - worldMap.getEnergyPerDay());
        }
    }

    @Test
    public void eatGrassOnMap() {
        worldMap.setGrassPerDaySteppe(100);
        worldMap.setGrassPerDayJungle(50);

        worldMap.spawnGrass();

        worldMap.startSimulation(100);

        HashSet<Grass> grassToBeEaten = new HashSet<>();

        for(Vector2d position : worldMap.getMap().getGrassMap().keySet()){
            if (worldMap.getMap().getAnimalMap().containsKey(position))
                grassToBeEaten.add(worldMap.getMap().getGrassMap().get(position));
        }

        worldMap.eatGrassOnMap(grassToBeEaten);

        for(Grass grass : grassToBeEaten){
            assertFalse(worldMap.getMap().getGrassMap().containsValue(grass));
        }
    }

    @Test
    public void copulation() {
        worldMap.startSimulation(worldMap.getMap().getSize()/2);
        HashSet<Grass> grassToBeEaten = new HashSet<>();
        HashSet<Vector2d> animalsToCopulatePositions = new HashSet<>();
        worldMap.moveAllAnimals(grassToBeEaten, animalsToCopulatePositions);

        for (Vector2d position : worldMap.getMap().getAnimalMap().keySet()){
            if(worldMap.getMap().getAnimalMap().get(position).size() >= 2)
                assertTrue(animalsToCopulatePositions.contains(position));
        }


    }
}