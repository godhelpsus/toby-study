package study.toby.reactive.lesson2;

import org.apache.logging.log4j.util.Strings;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReducePubGeneric {


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
//        Publisher<String> reducePub = reducePub(pub, "", (BiFunction<String, Integer, String>)(a, b)-> a+b+"-");
        Publisher<StringBuilder> reducePub = reducePub(pub, new StringBuilder(), (a,b) -> a.append(b+","));
        reducePub.subscribe(logSub());

    }

    // input1 pub T (int)
    // return R (String)
    private static <T,R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {

                pub.subscribe(new DelegateSubGeneric<T,R>(sub){

                    R result = init;

                    @Override
                    public void onNext(T t) {
                        result = bf.apply(result, t);
                    }

                    @Override
                    public void onComplete() {
                        sub.onNext(result);
                        sub.onComplete();
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
