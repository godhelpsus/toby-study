package study.toby.reactive.lesson3;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
public class SchedulerEx {

    public static void main(String[] args) {

        // pub -> subOnPub -> sub

        Publisher<Integer> pub = pub();
        Publisher<Integer> subOnPub = subOnPub(pub);
        subOnPub.subscribe(sub());


        log.info("exit");

    }

    // if pub is slower than sub
    private static Publisher<Integer> subOnPub(Publisher<Integer> pub) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        return sub -> es.execute(()-> pub.subscribe(sub));

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

    private static Publisher<Integer> pub() {
        return s ->

                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {
//                        log.info("request");
                        s.onNext(1);
                        s.onNext(2);
                        s.onNext(3);
                        s.onNext(4);
                        s.onNext(5);
                        s.onComplete();
                    }

                    @Override
                    public void cancel() {
                        log.info("cencel");
                    }
                });
    }



}
