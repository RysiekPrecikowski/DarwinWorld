package darwinWorld.po.Simulation;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Parameters {
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getStartEnergy() {
        return startEnergy;
    }

    public int getEnergyPerDay() {
        return energyPerDay;
    }

    public int getGrassEnergy() {
        return grassEnergy;
    }

    public float getJungleRatio() {
        return jungleRatio;
    }

    public int getGrassPerDaySteppe() {
        return grassPerDaySteppe;
    }

    public int getGrassPerDayJungle() {
        return grassPerDayJungle;
    }

    int width;
    int height;
    int startEnergy;
    int energyPerDay;
    int grassEnergy;
    float jungleRatio;
    int grassPerDaySteppe;
    int grassPerDayJungle;
    int startingAnimals;
    int delay;

    static public Parameters loadFromJson() throws FileNotFoundException {
        Gson gson = new Gson();

        return gson.fromJson(new FileReader("src\\darwinWorld\\po\\Simulation\\parameters.json"), Parameters.class);
    }

    public void validate(){
        if (width <=0)
            throw new IllegalArgumentException("width must be positive");
        if (height <=0)
            throw new IllegalArgumentException("height must be positive");
        if (startEnergy <=0)
            throw new IllegalArgumentException("startEnergy must be positive");
        if (energyPerDay <=0)
            throw new IllegalArgumentException("energyPerDay must be positive");
        if (grassEnergy <=0)
            throw new IllegalArgumentException("grassEnergy must be positive");
        if (jungleRatio < 0 || jungleRatio > 1)
            throw new IllegalArgumentException("jungleRatio must be between 0 and 1");
        if (grassPerDaySteppe <=0)
            throw new IllegalArgumentException("grassPerDaySteppe must be positive");
        if (grassPerDayJungle <=0)
            throw new IllegalArgumentException("width must be positive");
        if (startingAnimals <=0)
            throw new IllegalArgumentException("startingAnimals must be positive");
        if (delay <=0)
            throw new IllegalArgumentException("delay must be positive");
    }


}
