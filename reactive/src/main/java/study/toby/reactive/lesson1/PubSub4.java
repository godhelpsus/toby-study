package study.toby.reactive.lesson1;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class PubSub4 {

    public static void main(String[] args) throws InterruptedException {

        Iterable<Integer> list = Arrays.asList(1,2,3,4,5);
        ExecutorService ex = Executors.newCachedThreadPool();

        Publisher pub = new Publisher() {

            Iterator<Integer> ita = list.iterator();

            @Override
            public void subscribe(Subscriber subscriber) {

                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {

                        ex.execute(() -> {
                            try{

                                System.out.println(Thread.currentThread().getName()+" request");
                                if(ita.hasNext()){
                                    subscriber.onNext(ita.next());
                                }else{
                                    subscriber.onComplete();
                                }

                            }catch(RuntimeException ex){
                                subscriber.onError(ex);
                            }
                        });
                    }

                    @Override
                    public void cancel() {
                        System.out.println("cancel");
                    }
                });

            }
        };



        Subscriber<Integer> sub = new Subscriber<Integer>() {

            Subscription subscription = null;

            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                this.subscription = subscription;
                this.subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext " + integer);
                this.subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError" + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        pub.subscribe(sub);

        ex.awaitTermination(10, TimeUnit.MILLISECONDS);
        ex.shutdown();
    }

}
