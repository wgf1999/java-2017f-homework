/**
 * Created by yangmengfei on 2017/12/21.
 */
package nju.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class God extends Figure implements Runnable {
    public God(int x, int y, Field field, String pic) {
        super(x, y, field, pic);
    }

    public void run() {
        while (!Thread.interrupted()) {
            Random rand = new Random();
            try {
                String file = field.runfile;
                FileReader fr = new FileReader(file);
                BufferedReader bf = new BufferedReader(fr);
                String line = "";
                while ((line = bf.readLine()) != null) {
                    Thread.sleep(rand.nextInt(150));
                    field.replay(line);
                    this.repaint();
                }
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
