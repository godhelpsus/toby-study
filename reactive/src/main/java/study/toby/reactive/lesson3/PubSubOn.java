package study.toby.reactive.lesson3;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class PubSubOn {

    // if sub is slower than pub
    private static Publisher<Integer> pubOnPub(Publisher<Integer> pub) {

        return sub -> {
            pub.subscribe(new Subscriber<Integer>() {

                ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory(){
                    @Override
                    public String getThreadNamePrefix() {
                        return "pubOn-";
                    }
                });

                @Override
                public void onSubscribe(Subscription subscription) {
                    log.info("pubOnPub::onSubscribe");
                    subscription.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(Integer integer) {
                    es.execute(()->sub.onNext(integer));
                }

                @Override
                public void onError(Throwable throwable) {
                    es.execute(()->sub.onError(throwable));
                    es.shutdown();
                }

                @Override
                public void onComplete() {
                    es.execute(()->sub.onComplete());
                    es.shutdown();
                }
            });
        };
    }

    // if pub is slower than sub
    private static Publisher<Integer> subOnPub(Publisher<Integer> pub) {

        ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory(){
            @Override
            public String getThreadNamePrefix() {
                return "subOn-";
            }
        });

        return subscriber ->
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {
                        es.execute(()->pub.subscribe(subscriber));
                        es.shutdown();
                    }

                    @Override
                    public void cancel() {

                    }
                });

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
                        log.info("request");
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


    public static void main(String[] args) {

        Publisher<Integer> pub = pub();

        Publisher<Integer> subOnPub = subOnPub(pub);

        Publisher<Integer> pubOnPub = pubOnPub(subOnPub);

        pubOnPub.subscribe(sub());

        log.info("exit");

    }


}
