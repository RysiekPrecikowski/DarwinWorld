package darwinWorld.po.MapRelated;

import java.util.Objects;

public class Vector2d {
    private int x;
    private int y;

    public Vector2d(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(this.getX() + other.getX(), this.getY() + other.getY());
    }

    public boolean isRightAndUpOrEq(Vector2d position) {
        return position.getX() >= this.getX() && position.getY() >= this.getY();
    }

    public boolean isLeftAndDownOrEq(Vector2d position) {
        return position.getX() <= this.getX() && position.getY() <= this.getY();
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2d vector2d = (Vector2d) o;
        return getX() == vector2d.getX() &&
                getY() == vector2d.getY();
    }

    @Override
    public String toString() {
        return "(" + getX() +
                ", " + getY() +
                ')';
    }
}
