import darwinWorld.po.MapRelated.Rectangle;
import darwinWorld.po.MapRelated.Vector2d;
import org.junit.Test;

import static org.junit.Assert.*;

public class RectangleTest {
    Rectangle rectangle = new Rectangle(100,100);

    @Test
    public void testConstructors(){
        Vector2d ll = new Vector2d(0,0);
        Vector2d ur = new Vector2d(99,99);

        assertEquals(rectangle, new Rectangle(ll, ur));
    }

    @Test
    public void fixPosition() {
        Vector2d position = new Vector2d(rectangle.getWidth(), rectangle.getHeight());

        position = rectangle.fixPosition(position);

        assertTrue(rectangle.contains(position));
    }

    @Test
    public void contains() {
        Vector2d position = new Vector2d(rectangle.getWidth(), rectangle.getHeight());

        assertFalse(rectangle.contains(position));

        position.setY(0);
        position.setX(0);
        assertTrue(rectangle.contains(position));

    }
}