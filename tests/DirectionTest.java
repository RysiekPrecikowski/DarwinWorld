import darwinWorld.po.MapRelated.Direction;
import darwinWorld.po.MapRelated.Vector2d;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DirectionTest {

    @Test
    public void directionToVector() {
        for (Direction d: Direction.values()) {
            switch (d) {
                case NORTH -> assertEquals(new Vector2d(0, 1), d.directionToVector());
                case SOUTH -> assertEquals(new Vector2d(0, -1), d.directionToVector());
                case WEST -> assertEquals(new Vector2d(-1, 0), d.directionToVector());
                case EAST -> assertEquals(new Vector2d(1,0), d.directionToVector());
                case NORTHWEST -> assertEquals(new Vector2d(-1,1), d.directionToVector());
                case SOUTHWEST -> assertEquals(new Vector2d(-1,-1), d.directionToVector());
                case NORTHEAST -> assertEquals(new Vector2d(1,1), d.directionToVector());
                case SOUTHEAST -> assertEquals(new Vector2d(1,-1), d.directionToVector());
            }
        }
    }
}