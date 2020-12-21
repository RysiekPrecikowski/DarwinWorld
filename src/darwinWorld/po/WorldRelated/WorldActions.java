package darwinWorld.po.WorldRelated;

import darwinWorld.po.MapRelated.Grass;
import darwinWorld.po.MapRelated.Vector2d;

import java.io.IOException;
import java.util.HashSet;

public interface WorldActions {
    void spawnGrass();

    void useAnimalEnergyPerDay();

    void moveAllAnimals(HashSet<Grass> grassToBeEaten, HashSet<Vector2d> animalsToCopulatePositions);

    void eatGrassOnMap(HashSet<Grass> grassToBeEaten);

    void copulation(HashSet<Vector2d> animalsToCopulatePositions);

    void nextDay() throws IOException;
}
