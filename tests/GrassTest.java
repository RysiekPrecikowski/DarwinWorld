import darwinWorld.po.MapRelated.Grass;
import darwinWorld.po.MapRelated.GrassObserver;
import darwinWorld.po.MapRelated.Vector2d;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GrassTest {
    Vector2d position = new Vector2d(0,0);
    Grass grass = new Grass(position, 10);

    @Test
    public void toBeEaten() {
        GrassObserver animal1 = mock(GrassObserver.class);
        GrassObserver animal2 = mock(GrassObserver.class);

        grass.addObserver(animal1);
        grass.addObserver(animal2);

        grass.toBeEaten();
        int energyGained = (int) ((float) grass.getEnergy() / (float) grass.getAmountOfMaxEnergyObservers());
        verify(animal1).eatGrass(energyGained);
        verify(animal2).eatGrass(energyGained);
    }

    @Test
    public void getPosition() {
        assertEquals(position, grass.getPosition());
    }

    @Test
    public void setPosition() {
        Vector2d newPosition = new Vector2d(10,10);
        grass.setPosition(newPosition);

        assertEquals(newPosition, grass.getPosition());
    }
}