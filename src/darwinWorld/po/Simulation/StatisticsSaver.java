package darwinWorld.po.Simulation;

import darwinWorld.po.MapRelated.Genes;

public class StatisticsSaver {
    int day;
    int avgEnergy;
    int avgAmountOfAnimals;
    int avgAmountOfGrass;
    int avgAmountOfDeadAnimalLifeDuration;
    int avgAmountOfChildrenCount;
    Genes bestGenes;


    public StatisticsSaver(int day, double avgEnergy, double avgAmountOfAnimals, double avgAmountOfGrass,
                           double avgAmountOfDeadAnimalLifeDuration, double avgAmountOfChildrenCount,
                           Genes bestGenes) {
        this.day = day;
        this.avgEnergy = (int) avgEnergy;
        this.avgAmountOfAnimals = (int) avgAmountOfAnimals;
        this.avgAmountOfGrass = (int) avgAmountOfGrass;
        this.avgAmountOfDeadAnimalLifeDuration = (int) avgAmountOfDeadAnimalLifeDuration;
        this.avgAmountOfChildrenCount = (int) avgAmountOfChildrenCount;
        this.bestGenes = bestGenes;
    }
}
