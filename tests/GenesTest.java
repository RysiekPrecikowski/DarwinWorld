import darwinWorld.po.MapRelated.Direction;
import darwinWorld.po.MapRelated.Genes;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class GenesTest {
    int n=32;
    Genes genes = new Genes(n);

    @Test
    public void testCategorization() {
        int[] categorized = genes.categorizeGenes(genes.getGenes());
        int[] fromCategorized = genes.categorizedToGenesArr(categorized, n);

        assertArrayEquals(genes.getGenes(), fromCategorized);
    }

    @Test
    public void testFixingGenes() {
        int[] toTest = new int[n];

        for (int i = 0 ; i < n; i++){
            toTest[i] = 0;
        }

        int[] fixed = genes.fixGenes(toTest);

        int[] categorized = genes.categorizeGenes(fixed);

        for(int i = 0; i < Direction.values().length ; i++){
            assertTrue(categorized[i] > 0);
        }
    }
}