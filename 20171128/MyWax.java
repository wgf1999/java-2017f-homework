import java.util.concurrent.*;

import static jdk.nashorn.internal.objects.Global.print;
import static net.mindview.util.Print.printnb;

class Car {
    private boolean waxOn = false;

    public synchronized void waxed() {
        waxOn = true; // Ready to buff
        notifyAll();
    }

    public synchronized void buffed() {
        waxOn = false; // Ready for another coat of wax
        notify();
    }

    public synchronized void waitForWaxing()
            throws InterruptedException {
        while (waxOn == false)
            wait();
    }

    public synchronized void waitForBuffing()
            throws InterruptedException {
        while (waxOn == true)
            wait();
    }
}

class MyWaxOn implements Runnable {

    private Car car;
    private Integer id;

    public MyWaxOn(Car car, Integer id){
        this.car = car;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized ("") {
                    car.waitForBuffing();
                    printnb("Wax"+this.id.toString() + ": Wax On!\n");
                    TimeUnit.MILLISECONDS.sleep(200);
                    car.waxed();
                }
            }
        } catch (InterruptedException e) {
            print("Exiting via interrpt");
        }
        print("Ending Wax On task");
    }
}

class MyWaxOff implements Runnable {
    private Car car;

    public MyWaxOff(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        try{
            while(!Thread.interrupted()){
                car.waitForWaxing();
                printnb("Wax Off!\n");
                TimeUnit.MILLISECONDS.sleep(200);
                car.buffed();
            }
        } catch (InterruptedException e){
            print("Exiting via interrupt");
        }
        print("Ending Wax Off task");
    }
}

public class MyWax {
    public static void main(String[] args) throws Exception{
        Car car = new Car();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new MyWaxOff(car));
        exec.execute(new MyWaxOn(car, 1));
        exec.execute(new MyWaxOn(car, 2));
        TimeUnit.MILLISECONDS.sleep(2);
        exec.shutdown();
    }
}
