package study.toby.reactive.lesson2;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReducePub {


    /**
     * iterPub -> [data1] -> sumPub -> [Data2] -> logSub
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
        Publisher<Integer> reducePub = reducePub(pub, 0, (BiFunction<Integer, Integer, Integer>)(a, b)-> a + b);
        reducePub.subscribe(logSub());

    }

    private static Publisher<Integer> reducePub(Publisher<Integer> pub, int init, BiFunction<Integer, Integer, Integer> bf) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {

                pub.subscribe(new DelegateSub(sub){

                    int result = init;

                    @Override
                    public void onNext(Integer integer) {
                        result = bf.apply(result, integer);
                    }

                    @Override
                    public void onComplete() {
                        sub.onNext(result);
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
