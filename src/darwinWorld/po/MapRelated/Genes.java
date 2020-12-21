package darwinWorld.po.MapRelated;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Genes {
    private final int[] genes;
    private final int totalAmount;

    public Genes(int totalAmount) {
        this.totalAmount = totalAmount;

        this.genes = generateRandomGenes();
    }

    public Genes(int[] genes, int totalAmount) {
        this.genes = genes;
        this.totalAmount = totalAmount;
    }

    public int[] generateRandomGenes() {
        int[] genes = new int[totalAmount];
        for (int i = 0; i < totalAmount; i += 1) {
            Random rand = new Random();
            genes[i] = rand.nextInt(Direction.values().length);
        }
        genes = fixGenes(genes);
        return genes;
    }

    public int[] categorizeGenes(int[] genes) {
        int[] categorizedGenes = new int[Direction.values().length];
        for (int i = 0; i < Direction.values().length; i += 1) {
            categorizedGenes[i] = 0;
        }

        for (int gene : genes) {
            categorizedGenes[gene] += 1;
        }
        return categorizedGenes;
    }

    public int[] categorizedToGenesArr(int[] categorized, int totalAmount) {
        int[] genes = new int[totalAmount];

        int i = 0;
        int gene = 0;
        for (int amount : categorized) {
            for (int j = 0; j < amount; j += 1) {
                genes[i + j] = gene;
            }
            i += amount;
            gene += 1;
        }

        return genes;
    }

    public Genes getMixedGenes(Genes other) {
        if (this.totalAmount != other.totalAmount)
            throw new IllegalArgumentException("genes amount not identical");

        Random random = new Random();

        int[] newGenes = new int[totalAmount];

        int firstDivisionInd = random.nextInt(totalAmount);
        int secondDivisionInd;

        do {
            secondDivisionInd = random.nextInt(totalAmount);
        } while (secondDivisionInd == firstDivisionInd);


        //genes from this

        int i = 0;

        while (i < Math.min(firstDivisionInd, secondDivisionInd)){
            newGenes[i] = this.genes[i];
            i+=1;
        }
        while (i < Math.max(firstDivisionInd, secondDivisionInd)){
            newGenes[i] = other.genes[i];
            i+=1;
        }
        while (i < totalAmount){
            newGenes[i] = this.genes[i];
            i+=1;
        }

        newGenes = fixGenes(newGenes);
        return new Genes(newGenes, totalAmount);
    }

    public int[] fixGenes(int[] genes) {
        int[] categorized = categorizeGenes(genes);
        Random random = new Random();
        for (int j = 0; j < Direction.values().length; j += 1) {
            if (categorized[j] == 0) {
                int k = random.nextInt(Direction.values().length);

                while (categorized[k] <= 1) {
                    k += 1;
                    k %= Direction.values().length;
                }

                categorized[k] -= 1;
                categorized[j] = 1;
            }
        }

        genes = categorizedToGenesArr(categorized, totalAmount);
        return genes;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.genes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genes genes1 = (Genes) o;
        return totalAmount == genes1.totalAmount &&
                Arrays.equals(genes, genes1.genes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(totalAmount);
        result = 31 * result + Arrays.hashCode(genes);
        return result;
    }

    public int[] getGenes() {
        return genes;
    }

    public int getTotalAmount() {
        return totalAmount;
    }
}
