package nju.java;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Tile extends Thing2D {

    public Tile(int x, int y, int i) {
        super(x, y);
        Integer integer=i+1;
        String name="tile"+integer.toString()+".png";
        System.out.println(name);
        URL loc = this.getClass().getClassLoader().getResource(name);
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
    }
}