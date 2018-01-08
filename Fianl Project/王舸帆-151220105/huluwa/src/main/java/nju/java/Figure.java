package nju.java;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Figure extends Thing2D {
    public Field field;
    private String pngname;
    private final int SPACE = 60;
    public int die;
    int id;

    public void stop() {
        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Figure(int x, int y, Field field, String pngname) {
        super(x, y);

        this.field = field;
        this.pngname = pngname;

        URL loc = this.getClass().getClassLoader().getResource(pngname);
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);

        die = 0;
    }

    public void repaint() {
        this.field.repaint();
    }
}
