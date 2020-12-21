import darwinWorld.po.MapRelated.*;
import darwinWorld.po.WorldRelated.WorldMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class MapRectangleTest {
    WorldMap worldMap = new WorldMap(100,100, 100,1,5, 0.3f);
    MapRectangle map = worldMap.getMap();
    Random random = new Random();
    Animal[] animals;
    Vector2d position = new Vector2d(0,0);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp(){
        map.setMaxAnimalsPerCell(100);
        animals = new Animal[map.getMaxAnimalsPerCell()];
        for(int i = 0; i < map.getMaxAnimalsPerCell(); i += 1){
            animals[i] = new Animal(position, random.nextInt(10000), worldMap);
        }
    }

    @Test
    public void getAllAnimals() {
        worldMap.startSimulation(10);
        LinkedList<Animal> animals = map.getAllAnimals();

        assertEquals(10, animals.size());
    }

    @Test
    public void getAnimalsAt() {
        Grass grass = new Grass(position, 10);
        map.place(grass);
        Animal animal1 = new Animal(new Vector2d(1,1).add(position), 100, worldMap);

        for(Animal animal : animals){
            map.place(animal);
        }

        map.place(animal1);
        TreeSet<Animal> animalTreeSet = map.getAnimalsAt(position);
        assertEquals(map.getMaxAnimalsPerCell(), animalTreeSet.size());

        int[] animalEnergy = new int[map.getMaxAnimalsPerCell()];
        int i = 0;
        for (Animal animal: animalTreeSet){
            assertEquals(position, animal.getPosition());
            animalEnergy[i] = animal.getEnergy();
            i+=1;
        }
        for(i = 1 ; i < map.getMaxAnimalsPerCell(); i++){
            assertTrue(animalEnergy[i-1] <= animalEnergy[i]);
        }
    }

    @Test
    public void getElementAt() {
        Vector2d position = new Vector2d(0,0);
        Grass grass = new Grass(position, 10);
        map.place(grass);
        Animal[] animals = new Animal[4];
        animals[0] = new Animal(position, 10, worldMap);
        animals[1] = new Animal(position, 1000, worldMap);
        animals[2] = new Animal(position, 100, worldMap);
        animals[3] = new Animal(new Vector2d(1,1).add(position), 100, worldMap);

        for(Animal animal: animals){
            map.place(animal);
        }

        MapElement element = map.getElementAt(position);

        assertEquals(Animal.class, element.getClass());
        assertEquals(1000, ((Animal) element).getEnergy());
    }

    @Test
    public void canPlaceAnimalAt() {
        Grass grass = new Grass(position, 10);
        map.place(grass);

        Animal animal1 = new Animal(new Vector2d(1,1).add(position), 100, worldMap);

        for(Animal animal: animals){
            assertTrue(map.canPlaceAnimalAt(animal.getPosition()));
            map.place(animal);
        }

        map.place(animal1);

        Animal animal = new Animal(position, 1, worldMap);
        assertFalse(map.canPlaceAnimalAt(animal.getPosition()));
    }

    @Test
    public void isOccupied() {
        Grass grass = new Grass(position, 10);

        assertFalse(map.isOccupied(position));
        map.place(grass);
        assertTrue(map.isOccupied(position));

        position.setX(1);
        Animal animal = new Animal(position, 10, worldMap);
        assertFalse(map.isOccupied(position));
        map.place(animal);
        assertTrue(map.isOccupied(position));
    }

    @Test
    public void placeGrassTest() {
        Vector2d position = new Vector2d(0,0);
        Grass grass = new Grass(position, 10);

        map.place(grass);
        assertEquals(grass, map.getElementAt(position));

        grass = new Grass(position, 20);
//        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("cannot place grass " + grass.toString() + " at " + position.toString());
        map.place(grass);
    }

    @Test
    public void placeAnimalTest(){
        Vector2d position = new Vector2d(0,0);
        Animal animal = new Animal(position, 10, worldMap);
        map.place(animal);

        assertEquals(worldMap.getAnimalID() - 1, animal.getAnimalID());
        assertTrue(animal.getObservers().contains(worldMap));
    }

    @Test
    public void addAnimal() {

        Vector2d position = new Vector2d(0,0);
        Grass grass = new Grass(position, 10);
        map.place(grass);


        for(int i = 0 ; i < animals.length; i+=1){
            map.place(animals[i]);
            TreeSet<Animal> animalTreeSet = map.getAnimalsAt(position);
            for (int j = 0; j <= i ; j +=1){
                assertTrue(animalTreeSet.contains(animals[j]));
            }
        }


        Animal animal = new Animal(position, 1, worldMap);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("cannot place animal " + animal.toString() + " at " + position.toString());
        map.place(animal);
    }

    @Test
    public void removeGrassTest() {
        Vector2d position = new Vector2d(0,0);
        Grass grass = new Grass(position, 10);
        map.place(grass);

        map.remove(grass);
        assertFalse(map.getGrassMap().containsKey(grass.getPosition()));

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("position empty, grass doesnt exist" + grass.getPosition().toString());
        map.remove(grass);
    }

    @Test
    public void removeAnimalTest(){
        Vector2d position = new Vector2d(0,0);
        Vector2d oldPosition = new Vector2d(1,1);
        Animal animal = new Animal(oldPosition, 10, worldMap);
        Animal animal1 = new Animal(oldPosition, 10, worldMap);

        map.place(animal);
        map.place(animal1);

        animal.setPosition(position);
        map.place(animal);

        //removing from position with 2 animals and if not deleted from new position
        map.remove(animal, oldPosition);
        assertEquals(1, map.getAnimalMap().get(oldPosition).size());
        assertEquals(1, map.getAnimalMap().get(position).size());

        //removing single animals
        map.remove(animal1, oldPosition);
        assertNull(map.getAnimalMap().get(oldPosition));

        map.remove(animal, position);
        assertNull(map.getAnimalMap().get(position));

        //if removing from empty position
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("position empty, animal doesnt exist" + position.toString());
        map.remove(animal, position);
    }


    @Test
    public void getAmountOfOccupiedPositions() {
        int nAnimals = 100;

        worldMap.startSimulation(nAnimals);

        assertEquals(nAnimals, worldMap.getMap().getAmountOfOccupiedPositions());

        worldMap.spawnGrass();
        int nGrass = worldMap.getMap().getGrassMap().size();
        LinkedList<Animal> animals = worldMap.getMap().getAllAnimals();

        int deleted = 0;
        for (Animal animal : animals){
            worldMap.getMap().remove(animal, animal.getPosition());
            deleted+=1;
            assertEquals(nGrass + nAnimals - deleted, worldMap.getMap().getAmountOfOccupiedPositions());
        }
    }
}