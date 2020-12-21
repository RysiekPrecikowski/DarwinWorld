package darwinWorld.po.Visualization;

import darwinWorld.po.MapRelated.Animal;
import darwinWorld.po.MapRelated.Grass;
import darwinWorld.po.MapRelated.MapRectangle;
import darwinWorld.po.MapRelated.Vector2d;
import darwinWorld.po.WorldRelated.WorldMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MapRenderer extends JPanel implements MouseListener {
    private final WorldMap worldMap;
    private final Visualization visualization;
    private final MapRectangle map;

    private int widthScale;
    private int heightScale;

    public MapRenderer(WorldMap worldMap, Visualization visualization) {
        this.worldMap = worldMap;
        this.visualization = visualization;
        this.map = worldMap.getMap();

        addMouseListener(this);
    }


    private int scaleX(int toScale){
        return (toScale)*widthScale;
    }
    private int scaleY(int toScale){
        return (toScale)*heightScale;
    }

    private int scaleHeight(int toScale){
        return (toScale)*heightScale;
    }

    private int scaleWidth(int toScale){
        return toScale*widthScale;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        this.setSize(visualization.getMapFrame().getWidth(), visualization.getMapFrame().getHeight());
        this.setLocation(0, 0);

        widthScale = getWidth() / (worldMap.getMap().getWidth());
        heightScale = getHeight() / (worldMap.getMap().getHeight());


        graphics.setColor(new Color(226, 173, 66));
        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());


        graphics.setColor(new Color(26, 232, 50));
        graphics.fillRect(scaleX(worldMap.getJungle().getLowerLeft().getX()),
                scaleY(worldMap.getJungle().getLowerLeft().getY()),
                scaleWidth(worldMap.getJungle().getWidth()),
                scaleHeight(worldMap.getJungle().getHeight()));


        graphics.setColor(new Color(34, 75, 25, 181));
        for(Grass grass : worldMap.getMap().getGrassMap().values()) {
            graphics.fillOval(scaleX(grass.getPosition().getX()),
                    scaleY(grass.getPosition().getY()),
                    scaleWidth(1),
                    scaleHeight(1));
        }

        for (Vector2d position : map.getAnimalMap().keySet()) {
//                System.out.println(animal);
            Animal animal = (Animal) map.getElementAt(position);
            graphics.setColor(animal.toColor());

            graphics.fillOval(scaleX(animal.getPosition().getX()),
                    scaleY(animal.getPosition().getY()),
                    scaleWidth(1),
                    scaleHeight(1));
        }
    }

    public boolean checkIfClickedOnAnimal(int x, int y){
        Vector2d pos = new Vector2d(x/widthScale, y/heightScale);
        if (worldMap.getMap().getElementAt(pos) == null)
            return false;
        return worldMap.getMap().getElementAt(pos).getClass() == Animal.class;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        System.out.println(checkIfClickedOnAnimal(e.getX(), e.getY()));

        if(checkIfClickedOnAnimal(e.getX(), e.getY())){
            Vector2d pos = new Vector2d(e.getX()/widthScale, e.getY()/heightScale);
            Animal animal = (Animal) worldMap.getMap().getElementAt(pos);

            visualization.animalIsClicked(animal);

            this.repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        System.out.println(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
