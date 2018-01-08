package nju.java;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Huluwa extends Figure implements Runnable, Comparable {

    Hulu hulu;//每个葫芦娃都有一个自己的葫芦
    PAIHANG paihang;//"老大"
    COLOR color;//"红"
    private static final int SPACE = 60;
    private static final double DIS = 500000000000.0;

    public enum COLOR {
        红, 橙, 黄, 绿, 青, 蓝, 紫
    }

    enum PAIHANG {
        老一, 老二, 老三, 老四, 老五, 老六, 老七
    }


    public PAIHANG getPaihang() {
        return paihang;
    }

    public void setPaihang(PAIHANG paihang) {
        this.paihang = paihang;
    }

    public COLOR getColor() {
        return color;
    }

    public void setColor(COLOR color) {
        this.color = color;
    }


    public Huluwa(int x, int y, int id, Field field, String pngname) {
        super(x, y, field, pngname);

        URL loc = this.getClass().getClassLoader().getResource(pngname);
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);

        this.id = id;
    }

    public Hulu getHulu() {
        return hulu;
    }

    public void baogao() {

        System.out.print(paihang + " " + color);
    }//报告"老大+红"


    public boolean biggerThan(Comparable brother) {

        if (brother instanceof Huluwa)
            return this.getPaihang().ordinal() > ((Huluwa) brother).getPaihang().ordinal();
        else
            return false;
    }

    public void die(Huluwa hlw, ArrayList huluwa) {
        lock.lock();
        int id = -1;
        Iterator<Huluwa> it = huluwa.iterator();
        while (it.hasNext()) {
            if (it.next() == hlw) {
                id = hlw.id;
                hlw.die = 1;
                it.remove();
                break;
            }
        }
        if (id != -1) {
            try {
                field.bw.write(2 + " " + 1 + " " + id + "\n");
            } catch (Exception e) {

            }
            System.out.print("死亡：" + "葫芦娃" + id + "\n");
        }
        lock.unlock();
    }

    public Lock lock = new ReentrantLock();

    public Thing2D findYg(Huluwa hlw, ArrayList yaoguai) {
        lock.lock();
        Thing2D t = new Thing2D(hlw.x(), hlw.y());
        double dis = DIS;
        Thing2D nt = new Thing2D(t.x(), t.y());
        try {
            for (int i = 0; i < yaoguai.size(); i++) {
                Yaoguai m = (Yaoguai) yaoguai.get(i);
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

    void charge(Thing2D t, Huluwa hlw, ArrayList yaoguai, ArrayList huluwa) {
        lock.lock();

        try {
            int flag = 1;
            for (int i = 0; i < huluwa.size(); i++) {
                Huluwa m = (Huluwa) huluwa.get(i);
                if (m.x() == t.x() && m.y() == t.y()) {
                    flag = 0;
                    break;
                }
            }
            if (flag == 1) {
                if (hlw.x() != t.x() || hlw.y() != t.y()) {
                    Thing2D th = new Thing2D(hlw.x(), hlw.y());
                    field.record1(1, hlw.id, th, t);
                }
                hlw.setX(t.x());
                hlw.setY(t.y());
                for (int i = 0; i < yaoguai.size(); i++) {
                    Yaoguai yg = (Yaoguai) yaoguai.get(i);
                    if (yg.x() == hlw.x() && yg.y() == hlw.y()) {
                        Random rand = new Random();
                        int r = rand.nextInt(100);

                        if (r >= 65) {   //葫芦娃死的可能性为40%
                            hlw.die(hlw, huluwa);
                        } else {
                            yg.die(yg, yaoguai);
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }


    void chargeReplay(Thing2D t, Huluwa hlw, ArrayList yaoguai, ArrayList huluwa) {
        lock.lock();

        try {
            hlw.setX(t.x());
            hlw.setY(t.y());
            for (int i = 0; i < yaoguai.size(); i++) {
                Yaoguai yg = (Yaoguai) yaoguai.get(i);
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
                Thing2D t = findYg(this, field.yaoguai);
                this.charge(t, this, field.yaoguai, field.huluwa);
                this.repaint();

            } catch (Exception e) {

            }
        }

    }

}
