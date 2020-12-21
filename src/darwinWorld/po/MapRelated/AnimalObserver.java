package darwinWorld.po.MapRelated;

public interface AnimalObserver {

    /**
     * Informs observer that animal now has different than old position
     * @param animal animal with new position
     * @param oldPosition previous position of the animal
     */
    void animalMoved(Animal animal, Vector2d oldPosition);

    /**
     * Informs observer that animal's energy is 0 or below
     * @param animal animal that has died
     */
    void animalHasDied(Animal animal);

    /**
     * Informs observer that animal should change his energy by defined amount
     * @param animal animal that energy needs to be changed
     * @param energyDiff difference in energy
     */
    void animalNeedsToChangeEnergy(Animal animal, int energyDiff);

    /**
     * Informs observer that 2 parents have one child
     * @param animal parent 1
     * @param other parent 2
     * @param child their child
     */
    void animalHasChildWith(Animal animal, Animal other, Animal child);
}
