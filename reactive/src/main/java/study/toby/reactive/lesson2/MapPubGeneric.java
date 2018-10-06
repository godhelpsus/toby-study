package study.toby.reactive.lesson2;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapPubGeneric {


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
//        Publisher<Integer> mapPub = mapPub(pub, i -> i*10);
        Publisher<String> mapPub = mapPub(pub, i -> "["+i+"]"); // integer -> String
        mapPub.subscribe(logSub());

    }

    // generic 메소드로 변환
    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegateSubGeneric<T, R>(sub){
                    @Override
                    public void onNext(T t) {
                        sub.onNext(f.apply(t));
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
