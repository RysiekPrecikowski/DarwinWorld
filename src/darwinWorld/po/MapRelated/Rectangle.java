package darwinWorld.po.MapRelated;

public class Rectangle {
    private final Vector2d upperRight;
    private final Vector2d lowerLeft;

    private final int width;
    private final int height;

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;

        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width - 1, height - 1);
    }

    public Rectangle(Vector2d lowerLeft, Vector2d upperRight) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;

        this.width = this.upperRight.getX() - this.lowerLeft.getX() + 1;
        this.height = this.upperRight.getY() - this.lowerLeft.getY() + 1;
    }

    public Vector2d fixPosition(Vector2d position) {
        if (this.contains(position) && position.getX() != width && position.getY() != height  ) {
            return position;
        }

        int x = (position.getX() + this.width) % this.width;
        int y = (position.getY() + this.height) % this.height;
        return new Vector2d(x, y);
    }

    public boolean contains(Vector2d position) {
        return this.lowerLeft.isRightAndUpOrEq(position) && this.upperRight.isLeftAndDownOrEq(position);
    }

    public int getSize() {
        return (upperRight.getX() - lowerLeft.getX()) * (upperRight.getY() - lowerLeft.getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rectangle rectangle = (Rectangle) o;
        return width == rectangle.width &&
                height == rectangle.height &&
                upperRight.equals(rectangle.upperRight) &&
                lowerLeft.equals(rectangle.lowerLeft);
    }

    public Vector2d getUpperRight() {
        return upperRight;
    }

    public Vector2d getLowerLeft() {
        return lowerLeft;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
