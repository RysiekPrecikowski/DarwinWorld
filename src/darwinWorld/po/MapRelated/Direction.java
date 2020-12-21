package darwinWorld.po.MapRelated;

public enum Direction {
    NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;

    public String toString() {
        return switch (this) {
            case NORTH -> "↑";
            case SOUTH -> "↓";
            case WEST -> "←";
            case EAST -> "→";
            case NORTHWEST -> "↖";
            case SOUTHWEST -> "↙";
            case NORTHEAST -> "↗";
            case SOUTHEAST -> "↘";
        };
    }

    public Direction next() {
        return Direction.values()[(this.ordinal() + 1) % Direction.values().length];
    }

    public Vector2d directionToVector() {
        int[] x = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] y = {1, 1, 0, -1, -1, -1, 0, 1};
        int i = this.ordinal();
        return new Vector2d(x[i], y[i]);
    }


}
