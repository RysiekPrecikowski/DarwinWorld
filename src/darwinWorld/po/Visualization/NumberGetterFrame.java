package darwinWorld.po.Visualization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class NumberGetterFrame extends JFrame {
    public final JButton button = new JButton("submit");
    public final JTextField numberGetter = new JTextField(10);
    public JPanel panel = new JPanel();

    public NumberGetterFrame(String title, Component relativeTo ) throws HeadlessException {
        super(title);

        numberGetter.setSize(100,100);
        numberGetter.setVisible(true);
        setSize(100,100);
        setVisible(true);
        panel.add(numberGetter);
        JLabel label = new JLabel(title);
        panel.add(label);
        panel.add(button);
        panel.setVisible(true);
        setSize(300, 150);
        add(panel);
        setVisible(true);
        setLocationRelativeTo(relativeTo);
    }
    public void addButtonActionListener(ActionListener listener){
        button.addActionListener(listener);
    }
}
