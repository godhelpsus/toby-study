package study.toby.reactive.lesson3;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TakeEx {

    public static void main(String[] args) {

        Publisher<Integer> pub = sub -> {

            sub.onSubscribe(new Subscription() {

                int i = 0;
                boolean isCancelled = false;

                @Override
                public void request(long l) {
                    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
                    exec.scheduleAtFixedRate(()-> {
                                if(isCancelled) {
                                    exec.shutdown();
                                    return;
                                }
                                sub.onNext(i++);
                            }, 0, 1, TimeUnit.MILLISECONDS);
                }

                @Override
                public void cancel() {
                    log.info("cancel");
                    isCancelled = true;
                }
            });

        };


        Publisher<Integer> takePub = sub -> {

            pub.subscribe(new Subscriber<Integer>() {

                int max = 5;
                Subscription s;

                @Override
                public void onSubscribe(Subscription s) {
                    this.s = s;
                    sub.onSubscribe(this.s);
                }

                @Override
                public void onNext(Integer integer) {
                    if(integer > max){
                        s.cancel();
//                        sub.onComplete();
                    }else{
                        sub.onNext(integer);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    sub.onError(throwable);
                }

                @Override
                public void onComplete() {
                    sub.onComplete();
                }
            });
        };


        takePub.subscribe(sub());
    }

    private static Subscriber<Integer> sub() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.info("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer i) {
                log.info("onNext = {}", i);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("onError = {} " + throwable);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };
    }

}
