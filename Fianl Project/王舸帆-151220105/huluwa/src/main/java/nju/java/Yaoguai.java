package nju.java;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Yaoguai extends Figure implements Runnable {
    public BufferedWriter bw;
    private final double DIS = 50000000000.0;
    private static final int SPACE = 60;

    public Yaoguai(int x, int y, int id, Field field, String pngname) {
        super(x, y, field, pngname);

        URL loc = this.getClass().getClassLoader().getResource(pngname);
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
        this.id = id;
    }

    public void move2(int x, int y) {
        int nx = this.x() + x;
        int ny = this.y() + y;
        this.setX(nx);
        this.setY(ny);
    }

    public Lock lock = new ReentrantLock();

    public void die(Yaoguai yg, ArrayList yaoguai) {
        lock.lock();
        int id = -1, type = 2;
        Iterator<Yaoguai> it = yaoguai.iterator();
        while (it.hasNext()) {
            Yaoguai th = it.next();
            if (th == yg) {
                id = yg.id;
                yg.die = 1;
                it.remove();
                break;
            }
        }
        if (id != -1) {
            try {
                field.bw.write(2 + " " + 2 + " " + id + "\n");
            }catch (Exception e) {

            }
            System.out.print("死亡：" + "妖怪" + id + "\n");
        }
        lock.unlock();
    }

    public Thing2D findHl(Yaoguai yg, ArrayList huluwa) {
        lock.lock();
        Thing2D t = new Thing2D(yg.x(), yg.y());
        double dis = DIS;
        Thing2D nt = new Thing2D(t.x(), t.y());
        try {
            for (int i = 0; i < huluwa.size(); i++) {
                Huluwa m = (Huluwa) huluwa.get(i);
                double x = t.x() - m.x();
                x = x * x;
                double y = t.y() - m.y();
                y = y * y;
                if (x + y < dis) {
                    dis = x + y;
                    int flag = 1;
                    int nx = t.x(), ny = t.y();
                    if (t.x() < m.x()) {
                        nx = t.x() + SPACE;
                        flag = 0;
                    }
                    if (t.x() > m.x()) {
                        nx = t.x() - SPACE;
                        flag = 0;
                    }
                    if (t.y() < m.y() && flag == 1) {
                        ny = t.y() + SPACE;
                    }
                    if (t.y() > m.y() && flag == 1) {
                        ny = t.y() - SPACE;
                    }
                    nt.setX(nx);
                    nt.setY(ny);
                }
            }
        } finally {
            lock.unlock();
        }
        return nt;
    }

    void charge(Thing2D t, Yaoguai c, ArrayList yaoguai, ArrayList huluwa) {
        lock.lock();
        try {
            int flag = 1;
            for (int i = 0; i < yaoguai.size(); i++) {
                Yaoguai m = (Yaoguai) yaoguai.get(i);
                if (m.x() == t.x() && m.y() == t.y()) {
                    flag = 0;
                    break;
                }
            }
            if (flag == 1) {
                if (c.x() != t.x() || c.y() != t.y()) {
                    Thing2D tc = new Thing2D(c.x(), c.y());
                    field.record1(2, c.id, tc, t);
                }
                c.setX(t.x());
                c.setY(t.y());
                for (int i = 0; i < huluwa.size(); i++) {
                    Huluwa ca = (Huluwa) huluwa.get(i);
                    if (ca.x() == c.x() && ca.y() == c.y()) {
                        Random rand = new Random();
                        int r = rand.nextInt(100);
                        if (r > 65) {
                            ca.die(ca, huluwa);
                        } else {
                            c.die(c, yaoguai);
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
    void chargeReplay(Thing2D t, Yaoguai c, ArrayList yaoguai, ArrayList huluwa) {
        lock.lock();
        try {
            int flag = 1;
            for (int i = 0; i < yaoguai.size(); i++) {
                Yaoguai m = (Yaoguai) yaoguai.get(i);
                if (m.x() == t.x() && m.y() == t.y()) {
                    flag = 0;
                    break;
                }
            }
            if (flag == 1) {

                c.setX(t.x());
                c.setY(t.y());
                for (int i = 0; i < huluwa.size(); i++) {
                    Huluwa hlw = (Huluwa) huluwa.get(i);
                 }
            }
        } finally {
            lock.unlock();
        }
    }


    public void run() {
        while (!Thread.interrupted()) {
            Random rand = new Random();
            try {
                if (this.die == 1) break;
                Thread.sleep(rand.nextInt(1000) + 1000);

                Thing2D t = findHl(this, field.huluwa);
                this.charge(t, this, field.yaoguai, field.huluwa);

                this.repaint();

            } catch (Exception e) {

            }
        }
    }
}

