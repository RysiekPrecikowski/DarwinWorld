import darwinWorld.po.MapRelated.Vector2d;
import org.junit.Test;

import static org.junit.Assert.*;

public class Vector2dTest {
    Vector2d v1 = new Vector2d(10,10);
    Vector2d v2 = new Vector2d(20,20);
    Vector2d v3 = new Vector2d(0,0);
    Vector2d v4 = new Vector2d(0,30);
    Vector2d v5 = new Vector2d(30,0);



    @Test
    public void add() {
        Vector2d added = v1.add(v2);
        assertEquals(v1.getX() + v2.getX(), added.getX());
        assertEquals(v1.getY() + v2.getY(), added.getY());
    }

    @Test
    public void isRightAndUpOrEq() {
        assertTrue(v1.isRightAndUpOrEq(v2));
        assertFalse(v1.isRightAndUpOrEq(v3));
        assertFalse(v1.isRightAndUpOrEq(v4));
        assertFalse(v1.isRightAndUpOrEq(v5));
    }

    @Test
    public void isLeftAndDownOrEq() {
        assertFalse(v1.isLeftAndDownOrEq(v2));
        assertTrue(v1.isLeftAndDownOrEq(v3));
        assertFalse(v1.isLeftAndDownOrEq(v4));
        assertFalse(v1.isLeftAndDownOrEq(v5));
    }

    @Test
    public void testEquals() {
        assertNotEquals(v1, v2);
        assertNotEquals(v1, v3);
        assertNotEquals(v1, v4);
        assertNotEquals(v1, v5);

        Vector2d v6 = new Vector2d(v1.getX(), v1.getY());
        assertEquals(v1, v6);
    }
}