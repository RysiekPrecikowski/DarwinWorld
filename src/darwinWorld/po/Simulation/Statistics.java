package darwinWorld.po.Simulation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import darwinWorld.po.MapRelated.Animal;
import darwinWorld.po.MapRelated.Direction;
import darwinWorld.po.MapRelated.Genes;
import darwinWorld.po.MapRelated.MapRectangle;
import darwinWorld.po.WorldRelated.WorldMap;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;

public class Statistics {
    private final MapRectangle map;
    private int day = 0;
    private int dayToSaveSimulation = Integer.MAX_VALUE;

    private int totalDeadAnimalLifeDuration = 0;
    private int currentEnergy = 0;
    private int currentAmountOfAnimals = 0;
    private int currentChildrenCount = 0;
    private int currentGrassAmount = 0;

    private double avgEnergy = 0;
    private double avgAmountOfAnimals = 0;
    private double avgAmountOfGrass = 0;
    private double avgAmountOfDeadAnimalLifeDuration = 0;
    private double avgAmountOfChildrenCount = 0;
    private final HashMap<Genes, Integer> bestGenesEver = new HashMap<>();

    private final int[] genesTotalAmount = new int[Direction.values().length];
    public HashMap<Genes, LinkedList<Animal>> getGenesMap() {
        return genesMap;
    }

    private final HashMap<Genes, LinkedList<Animal>> genesMap = new HashMap<>();
    private int maxAmountOfBestGenes = 0;
    private Genes bestGenes = null;

    public Statistics(WorldMap worldMap) {
        map = worldMap.getMap();
    }

    public void setDayToSaveSimulation(int dayToSaveSimulation) {
        this.dayToSaveSimulation = dayToSaveSimulation;
    }

    public int getCurrentAmountOfAnimals() {
        return currentAmountOfAnimals;
    }

    public String getAnimalsStatistics(){
        int averageEnergy = 0;
        int averageLifeDuration = 0;
        int averageAmountOfChildren = 0;

        if (currentAmountOfAnimals != 0) {
            averageEnergy = currentEnergy / currentAmountOfAnimals;
            averageAmountOfChildren = currentChildrenCount / currentAmountOfAnimals;
        }

        if (map.getDeadAnimals().size() != 0)
            averageLifeDuration = totalDeadAnimalLifeDuration/ map.getDeadAnimals().size();

        return "average energy for alive animals: " + averageEnergy + "\n" +
                "average amount of children for alive animals: " + averageAmountOfChildren + "\n" +
                "average life duration for dead animals: " + averageLifeDuration + "\n";
    }

    public String getGenesStatistics(){
        StringBuilder statistics = new StringBuilder();

        if(currentAmountOfAnimals != 0) {

            statistics.append("genes statistics:\n");
            int i = 0;
            DecimalFormat decimalFormat = new DecimalFormat("##.#");
            for (int genesAmount : genesTotalAmount) {
                double percentage = ((double) genesAmount / (double) (currentAmountOfAnimals * map.getAllAnimals().getFirst().getGenes().getTotalAmount())) * 100;

                statistics.append(i).append(" = ").append(i * 360 / Direction.values().length).append("° : ").append(decimalFormat.format(percentage)).append("%\n");
                i += 1;
            }
        }else{
            statistics.append("no animals\n");
        }
        statistics.append("best genes:\n");

        if (getBestGenes() != null) {
            for (int i = 0; i < getBestGenes().getTotalAmount(); i += 1) {
                statistics.append(getBestGenes().getGenes()[i]).append(" ");
                if (i == getBestGenes().getTotalAmount() / 2 - 1)
                    statistics.append("\n");
            }
            statistics.append("\nAmount of animalas with best genes: ").append(genesMap.get(getBestGenes()).size()).append("\n");
        }else {
            statistics.append("no animals - no genes");
        }
        return statistics.toString();
    }

    public String getWorldStatistics(){
        return "Alive animals: " + getCurrentAmountOfAnimals() + "\n" +
                "Amount of Grass: " + getCurrentGrassAmount() + "\n" +
                "Day: " + day;
    }

    public Genes getBestGenes(){
        if (bestGenes == null ) {
            maxAmountOfBestGenes = 0;
            for (Genes genes : genesMap.keySet()) {
                if (genesMap.get(genes).size() > maxAmountOfBestGenes) {
                    maxAmountOfBestGenes = genesMap.get(genes).size();
                    bestGenes = genes;
                }
            }
        }
        return bestGenes;
    }

    public int getCurrentGrassAmount(){
        return currentGrassAmount;
    }

    public void actualizeDeadAnimalLifeDuration(int diff){
        totalDeadAnimalLifeDuration += diff;
    }

    public void actualizeChildrenAmount(int diff){
        currentChildrenCount += diff;
    }

    public void actualizeAnimalsAmount(int diff){
        currentAmountOfAnimals += diff;
    }

    public void actualizeEnergy(int diff){
        currentEnergy += diff;
    }

    public void animalDied(Animal animal){
        actualizeAnimalsAmount(-1);
        actualizeChildrenAmount(-animal.getChildrenList().size());
        actualizeDeadAnimalLifeDuration(animal.getLifeDuration());

        deleteGeneFromAnimal(animal);

        for (int i = 0; i < animal.getGenes().getTotalAmount(); i++) {
            genesTotalAmount[animal.getGenes().getGenes()[i]] -= 1;
        }
    }

    public void animalPlaced(Animal animal){
        addGeneFromAnimal(animal);
        actualizeAnimalsAmount(1);
        actualizeEnergy(animal.getEnergy());
        for (int i = 0; i < animal.getGenes().getTotalAmount(); i++) {
            genesTotalAmount[animal.getGenes().getGenes()[i]] += 1;
        }
    }

    public void deleteGeneFromAnimal(Animal animal){
        if(animal.getGenes() == bestGenes)
            bestGenes = null;
        if (!genesMap.containsKey(animal.getGenes()))
            return;
        LinkedList<Animal> animals = genesMap.get(animal.getGenes());

        animals.remove(animal);

        if (animals.size() == 0){
            genesMap.remove(animal.getGenes());
            bestGenes = null;
        }
    }

    public void addGeneFromAnimal(Animal animal){
        if (genesMap.get(animal.getGenes()) != null){
            genesMap.get(animal.getGenes()).add(animal);
        } else {
            LinkedList<Animal> animals = new LinkedList<>();
            animals.add(animal);
            genesMap.put(animal.getGenes(), animals);
        }

        if (genesMap.get(animal.getGenes()).size() > maxAmountOfBestGenes){
            maxAmountOfBestGenes = genesMap.get(animal.getGenes()).size();
            bestGenes = animal.getGenes();
        }
    }

    public void nextDay(int day) throws IOException {
        this.day = day;
        if (day < dayToSaveSimulation){
            avgAmountOfAnimals += (double) currentAmountOfAnimals / (double) dayToSaveSimulation;
            avgEnergy += (double) currentEnergy / (double) dayToSaveSimulation / (double) currentAmountOfAnimals;
            avgAmountOfGrass += (double) currentGrassAmount / (double) dayToSaveSimulation;
            avgAmountOfChildrenCount += (double) currentChildrenCount / (double) dayToSaveSimulation/ (double) currentAmountOfAnimals;
            if (map.getDeadAnimals().size() != 0)
                avgAmountOfDeadAnimalLifeDuration += (double) totalDeadAnimalLifeDuration /(double) dayToSaveSimulation / (double) map.getDeadAnimals().size() ;

            if (bestGenesEver.containsKey(getBestGenes())){
                bestGenesEver.replace(getBestGenes(), bestGenesEver.get(getBestGenes()) + 1);
            } else {
                bestGenesEver.put(getBestGenes(), 1);
            }
        } else if (day == dayToSaveSimulation) {
            System.out.println("saving" + day);
            System.out.println(dayToSaveSimulation);
            saveToJson();
        }
    }

    public void saveToJson() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Genes bestGenes = null;
        int maxAmount = 0;
        for (Genes genes : bestGenesEver.keySet()){
            if (bestGenesEver.get(genes) > maxAmount){
                maxAmount = bestGenesEver.get(genes);
                bestGenes = genes;
            }
        }

        StatisticsSaver toSave = new StatisticsSaver(day, avgEnergy,avgAmountOfAnimals,
                avgAmountOfGrass,avgAmountOfDeadAnimalLifeDuration,avgAmountOfChildrenCount, bestGenes);

        FileWriter fileWriter = new FileWriter("src/darwinWorld/po/Simulation/statisticsSave_"+map.getWorldMap().getWorldID()+".json");
        gson.toJson(toSave, fileWriter);
        fileWriter.close();
    }

    public void actualizeGrassAmount(int grassAmount){
        this.currentGrassAmount = grassAmount;
    }

}
