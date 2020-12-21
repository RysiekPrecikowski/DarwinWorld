package darwinWorld.po.Visualization;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener {

    private final Visualization visualization;

    public KeyboardListener(Visualization visualization) {
        this.visualization = visualization;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_P){
            if(visualization.getTimer().isRunning()){
                visualization.getTimer().stop();
                ActionEvent actionEvent = new ActionEvent(this,0,"timerStopped");
                visualization.actionPerformed(actionEvent);
            } else {
                visualization.getTimer().start();
                ActionEvent actionEvent = new ActionEvent(this,0,"timerStarted");
                visualization.actionPerformed(actionEvent);
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_G){
            if(visualization.areGenesMarked()){
                visualization.unmarkGenes();
                ActionEvent actionEvent = new ActionEvent(this,0,"genesUnmarked");
                visualization.actionPerformed(actionEvent);

            } else {
              visualization.markGenes();
                ActionEvent actionEvent = new ActionEvent(this,0,"genesMarked");
                visualization.actionPerformed(actionEvent);
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
