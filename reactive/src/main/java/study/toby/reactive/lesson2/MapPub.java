package study.toby.reactive.lesson2;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Operators;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapPub {


    /**
     * iterPub -> [data1] -> mapPub -> [Data2] -> logSub
     *                     <- subscribe(logSub)
     *                     -> onSubScribe(subscription)
     *                     -> onNext
     *                     -> onNext ...
     *                     -> onComplete
     *
     * @param args
     */
    public static void main(String[] args) {

        Iterable<Integer> iter = Stream.iterate(1, a->a+1).limit(10).collect(Collectors.toList());

        Publisher<Integer> pub = iterPub(iter);
        Publisher<Integer> mapPub = mapPub(pub, (Function<Integer, Integer>) i -> i*10);
        Publisher<Integer> mapPub2 = mapPub(mapPub, (Function<Integer, Integer>) i -> -i);
        mapPub2.subscribe(logSub());

    }

    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                pub.subscribe(new DelegateSub(sub){
                    @Override
                    public void onNext(Integer integer) {
                        sub.onNext(f.apply(integer));
                    }
                });
            }
        };
    }


    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    System.out.println("onSubscribe");
                    subscription.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(T t) {
                    System.out.println("onNext : "+t);
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println("onError "+ throwable.getMessage());
                }

                @Override
                public void onComplete() {
                    System.out.println("onComplete");
                }
            };
    }

    private static <T> Publisher<T> iterPub(Iterable<T> iter) {
        return new Publisher<T>() {
                @Override
                public void subscribe(Subscriber<? super T> subscriber) {

                    subscriber.onSubscribe(new Subscription() {
                        @Override
                        public void request(long l) {
                           try{
                               iter.forEach(s -> subscriber.onNext(s));
                               subscriber.onComplete();
                           }catch(Throwable t){
                               subscriber.onError(t);
                           }
                        }

                        @Override
                        public void cancel() {

                        }
                    });

                }
            };
    }

}
