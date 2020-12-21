package darwinWorld.po.Visualization;

import darwinWorld.po.Simulation.Statistics;
import darwinWorld.po.WorldRelated.WorldMap;

import javax.swing.*;
import java.awt.*;

public class StatisticsRenderer extends JPanel {

    private final Statistics statistics;
    public StatisticsRenderer(WorldMap worldMap){
        this.statistics = worldMap.getStatistics();
    }

    protected void paintComponent(Graphics graphics) {
        graphics.clearRect(0,0,getWidth(),getHeight());
        int y = 0;
        graphics.setFont(graphics.getFont().deriveFont(20f));
        for(String line: statistics.getGenesStatistics().split("\n")){
            graphics.drawString(line,10, y += graphics.getFontMetrics().getHeight());
        }
        for(String line: statistics.getAnimalsStatistics().split("\n")){
            graphics.drawString(line,10, y += graphics.getFontMetrics().getHeight());
        }

        for(String line: statistics.getWorldStatistics().split("\n")){
            graphics.drawString(line,10, y += graphics.getFontMetrics().getHeight());
        }
    }
}
