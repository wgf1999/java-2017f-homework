package nju.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Field extends JPanel {

    private final int OFFSET = 40;
    private static final int SPACE = 60;

    public final int MOVE = 1;//用1来标识发生的事件为move

    public String runfile;  //回放的文件
    public String outfile = "out.txt";  //这次游戏运行输出的文件

    private ArrayList tiles = new ArrayList();
    public ArrayList yaoguai = new ArrayList();
    public ArrayList huluwa = new ArrayList();
    private ArrayList thread = new ArrayList();

    private God god;

    private int w = 0;
    private int h = 0;
    private boolean completed = false;

    BufferedWriter bw;

    private String level =
            "...............l...\n" +
                    "..7...........l....\n" +
                    "....6........l.....\n" +
                    "......5.....l......\n" +
                    "........1..x.s.l.l.\n" +
                    "......2.....l......\n" +
                    "....3........l.....\n" +
                    "..4...........l....\n" +
                    "....y..........l...\n";

    public Field() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        initWorld();
    }

    public int getBoardWidth() {
        return this.w;
    }

    public int getBoardHeight() {
        return this.h;
    }

    public final void initWorld() {

        int x = OFFSET;
        int y = OFFSET;

        Tile a;
        Huluwa hlw;
        Yaoguai yg;
        int number = 0;
        int temp=0;


        for (int i = 0; i < level.length(); i++) {

            char item = level.charAt(i);

            if (item == '\n') {
                temp++;
                y += SPACE;
                if (this.w < x) {
                    this.w = x;
                }

                x = OFFSET;
            } else {
                a = new Tile(x, y,i-temp);

                tiles.add(a);
                if (item == '1') {
                    hlw = new Huluwa(x, y, 1, this, "red.png");
                    huluwa.add(hlw);
                } else if (item == '2') {
                    hlw = new Huluwa(x, y, 2, this, "ora.png");
                    huluwa.add(hlw);
                } else if (item == '3') {
                    hlw = new Huluwa(x, y, 3, this, "yell.png");
                    huluwa.add(hlw);
                } else if (item == '4') {
                    hlw = new Huluwa(x, y, 4, this, "gree.png");
                    huluwa.add(hlw);
                } else if (item == '5') {
                    hlw = new Huluwa(x, y, 5, this, "gbl.png");
                    huluwa.add(hlw);
                } else if (item == '6') {
                    hlw = new Huluwa(x, y, 6, this, "blue.png");
                    huluwa.add(hlw);
                } else if (item == '7') {
                    hlw = new Huluwa(x, y, 7, this, "pur.png");
                    huluwa.add(hlw);
                } else if (item == 'y') {
                    hlw = new Huluwa(x, y, 8, this, "yy.png");
                    huluwa.add(hlw);
                } else if (item == 'l') {
                    yg = new Yaoguai(x, y, number++, this, "ll.png");
                    yaoguai.add(yg);
                } else if (item == 's') {
                    yg = new Yaoguai(x, y, number++, this, "she.png");
                    yaoguai.add(yg);
                } else if (item == 'x') {
                    yg = new Yaoguai(x, y, number++, this, "x.png");
                    yaoguai.add(yg);
                }
                x += SPACE;
            }
            h = y;
        }
        god = new God(-1, -1, this, "player.png");
    }

    public Lock lock = new ReentrantLock();

    public void buildWorld(Graphics g) {

        g.setColor(new Color(27, 58, 84));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        ArrayList world = new ArrayList();
        world.addAll(tiles);

        world.addAll(huluwa);

        world.addAll(yaoguai);
        for (int i = 0; i < world.size(); i++) {

            Thing2D item = (Thing2D) world.get(i);

            if (item instanceof Player) {
                g.drawImage(item.getImage(), item.x() + 2, item.y() + 2, this);
            } else {
                g.drawImage(item.getImage(), item.x(), item.y(), this);
            }
            if (completed) {
                g.setColor(new Color(0, 0, 0));
                g.drawString("Completed", 25, 20);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        buildWorld(g);
    }

    //用来记录发生的移动事件
    void record1(int type, int id, Thing2D c, Thing2D t) {
        lock.lock();
        try {
            bw.write(MOVE + " " + type + " " + id + " ");
            bw.write(c.x() + " " + c.y() + " " + t.x() + " " + t.y() + "\n");
        } catch (Exception e) {
        }
        if (type == 1) {
            System.out.print("葫芦娃" + id + "从 ");
            System.out.print(c.x() + " " + c.y() + " 移动到 " + t.x() + " " + t.y() + "\n");
        }
        if (type == 2) {
            System.out.print("妖怪" + id + "从 ");
            System.out.print(c.x() + " " + c.y() + " 移动到 " + t.x() + " " + t.y() + "\n");
        }
        lock.unlock();
    }


    void replay(String line) {    //重放整个战斗过程
        lock.lock();
        String[] str;
        str = line.split(" ");
        int[] a = new int[10];
        for (int i = 0; i < str.length; i++) {
            a[i] = Integer.parseInt(str[i]);
        }
        int event = a[0], type = a[1], id = a[2];
        int nx = a[5], ny = a[6];
        if (event == 1) {
            if (type == 1) {
                Thing2D nt = new Thing2D(nx, ny);
                Iterator<Huluwa> it = huluwa.iterator();
                while (it.hasNext()) {
                    Huluwa th = it.next();
                    if (th.id == id) {
                        th.chargeReplay(nt, th, yaoguai, huluwa);
                        break;
                    }
                }
            }
            if (type == 2) {
                Thing2D nt = new Thing2D(nx, ny);
                Iterator<Yaoguai> it = yaoguai.iterator();
                while (it.hasNext()) {
                    Yaoguai yg = it.next();
                    if (yg.id == id) {
                        yg.chargeReplay(nt, yg, yaoguai, huluwa);
                        break;
                    }
                }
            }

        } else if (event == 2) {
            if (type == 2) {
                Iterator<Yaoguai> it = yaoguai.iterator();
                while (it.hasNext()) {
                    Yaoguai yg = it.next();
                    if (yg.id == id) {
                        it.remove();
                        break;
                    }
                }
            }
            if (type == 1) {
                Iterator<Huluwa> it = huluwa.iterator();
                while (it.hasNext()) {
                    Huluwa th = it.next();
                    if (th.id == id) {
                        it.remove();
                        break;
                    }
                }
            }
        }
        lock.unlock();
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (completed) {
                return;
            }
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                for (int i = 0; i < huluwa.size(); i++) {
                    Thread t = new Thread((Huluwa) huluwa.get(i));
                    thread.add(t);
                    t.start();
                }
                for (int i = 0; i < yaoguai.size(); i++) {
                    Thread t = new Thread((Yaoguai) yaoguai.get(i));
                    thread.add(t);
                    t.start();
                }
                try {
                    FileWriter fw = new FileWriter(outfile, false);
                    bw = new BufferedWriter(fw);
                } catch (Exception te) {

                }
            } else if (key == KeyEvent.VK_S) {
                try {
                    bw.close();
                } catch (Exception ee) {

                }
            } else if (key == KeyEvent.VK_L) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                jfc.showDialog(new JLabel(), "choose");
                File file = jfc.getSelectedFile();
                while (file.isDirectory()) {
                    System.out.println("Directory:" + file.getAbsolutePath());
                    jfc.showDialog(new JLabel(), "choose agian");
                    file = jfc.getSelectedFile();
                }
                runfile = jfc.getSelectedFile().getAbsolutePath();
                RestartLevel();
                Thread t = new Thread(god);
                t.start();
                thread.add(t);
            } else if (key == KeyEvent.VK_A) {
                RestartLevel();
            }
            repaint();

        }
    }

    //停止所有线程
    public void stop() {
        Iterator<Thread> it = thread.iterator();
        while (it.hasNext()) {
            Thread t = it.next();
            t.interrupt();
        }
        thread.clear();
    }

    public void RestartLevel() {
        tiles.clear();
        stop();
        huluwa.clear();
        yaoguai.clear();
        initWorld();
        if (completed) {
            completed = false;
        }
    }
}