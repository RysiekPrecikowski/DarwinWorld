import darwinWorld.po.MapRelated.Animal;
import darwinWorld.po.MapRelated.AnimalObserver;
import darwinWorld.po.MapRelated.Genes;
import darwinWorld.po.MapRelated.Vector2d;
import darwinWorld.po.WorldRelated.WorldMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AnimalTest {
    Vector2d position = new Vector2d(0,0);
    int startEnergy = 100;
    Genes genes = new Genes(32);
    WorldMap worldMap = new WorldMap(100, 100, startEnergy, 1, 40, (float) 0.3);
    Animal animal = new Animal(position, startEnergy, genes, worldMap);

    @Before
    public void setWorldMap() {
        worldMap.getMap().setMaxAnimalsPerCell(100);
    }

    AnimalObserver observer = mock(AnimalObserver.class);



    @Test
    public void setAnimalID() {
        int ID = 10;
        animal.setAnimalID(ID);
        assertEquals(ID, animal.getAnimalID());
    }

    @Test
    public void testGetPosition() {
        assertEquals(position, animal.getPosition());
    }

    @Test
    public void setPosition() {
        Vector2d newPosition = new Vector2d(10,10);
        animal.setPosition(newPosition);

        assertEquals(newPosition, animal.getPosition());
    }

    @Test
    public void changeEnergy() {
        int newEnergy = 120;
        animal.addObserver(observer);
        animal.changeEnergy(newEnergy - startEnergy);
        animal.energyChanged(newEnergy - startEnergy);
        assertEquals(newEnergy, animal.getEnergy());

        verify(observer).animalNeedsToChangeEnergy(animal, newEnergy - startEnergy);
    }

    @Test
    public void isAlive() {
        animal.addObserver(observer);
        assertTrue(animal.isAlive());
        verifyZeroInteractions(observer);

        animal.changeEnergy(-animal.getEnergy());

        assertFalse(animal.isAlive());
        verify(observer).animalHasDied(animal);
    }

    @Test
    public void move() {
        Vector2d newPosition = animal.getPosition().add(animal.getDirection().directionToVector());
        newPosition = worldMap.getMap().fixPosition(newPosition);
        Vector2d oldPosition = animal.getPosition();
        animal.addObserver(observer);
        worldMap.getMap().place(animal);


        animal.move();
        assertEquals(newPosition, animal.getPosition());
        verify(observer).animalMoved(animal, oldPosition);

        //ma sie nie ruszyc jesli za duzo zwierzat
        oldPosition = animal.getPosition();

        for (int i = 0; i < worldMap.getMap().getMaxAnimalsPerCell(); i++){
            newPosition = animal.getPosition().add(animal.getDirection().directionToVector());
            Animal animal1 = new Animal(newPosition, startEnergy, worldMap);
            worldMap.getMap().place(animal1);
        }

        animal.move();
        assertEquals(oldPosition, animal.getPosition());
        verifyZeroInteractions(observer);
    }

    @Test
    public void copulate() {
        Animal animal1 = new Animal(animal.getPosition(), startEnergy, worldMap);

        animal.addObserver(observer);
        animal1.addObserver(observer);

        worldMap.getMap().place(animal1);
        worldMap.getMap().place(animal);

        int energy1 = -animal.getEnergy() /4;
        int energy2 = -animal1.getEnergy() /4;
        animal.copulate(animal1);

        Animal child = animal.getChildrenList().getLast();
        assertTrue(animal1.getChildrenList().contains(child));
        assertTrue(child.getParents().contains(animal1));
        assertTrue(child.getParents().contains(animal));

        verify(observer).animalHasChildWith(animal, animal1, child);
        verify(observer).animalNeedsToChangeEnergy(animal, energy1);
        verify(observer).animalNeedsToChangeEnergy(animal1, energy2);
    }

    @Test
    public void compareTo() {
        Animal other = new Animal(animal.getPosition(), startEnergy, worldMap);

        assertEquals("if animals have same id and energy", 0, animal.compareTo(other));

        other.setAnimalID(animal.getAnimalID() + 1);

        assertEquals("if animals have same energy and different id", animal.compareTo(other), -1);

        other.changeEnergy(-1);

        assertEquals("if animals have different energy", 1, animal.compareTo(other));



    }
}