package study.toby.reactive.lesson1;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PubSub3 {

    static class IntObservable extends Observable implements Runnable{

        @Override
        public void run() {

            for(int i=1;i<=10;i++){
                setChanged();
                notifyObservers(i);
                System.out.println(Thread.currentThread().getName());
            }
        }
    }


    public static void main(String[] args) {

        Observer obs = (o, arg) -> {
            System.out.println(Thread.currentThread().getName()+" "+arg);
        };

        IntObservable io = new IntObservable();
        io.addObserver(obs);

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(io);
        es.shutdown();


    }


}
