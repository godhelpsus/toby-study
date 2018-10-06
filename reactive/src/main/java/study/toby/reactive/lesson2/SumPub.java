package study.toby.reactive.lesson2;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SumPub {


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
        Publisher<Integer> sumPub = sumPub(pub);
        sumPub.subscribe(logSub());

    }

    private static Publisher<Integer> sumPub(Publisher<Integer> pub) {

        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {


                // 상위 pub과 하위 sub을 연결(subscribe)해준다.
                pub.subscribe(new DelegateSub(sub){

                    int sum = 0;
                    @Override
                    public void onNext(Integer i) {
                        // 상위 pub에서 데이터가 오면(onNext) 함계를 구한다.
                        sum += i;
                    }

                    @Override
                    public void onComplete() {

                        // 상위 pub이 완료되었을때 (10까지 호출되었을때)
                        // 아래 sub으로 데이터를 넘긴다.
                        sub.onNext(sum);
                        sub.onComplete();
                    }
                });
            }
        };
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
