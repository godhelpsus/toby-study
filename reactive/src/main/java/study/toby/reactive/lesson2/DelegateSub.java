package study.toby.reactive.lesson2;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DelegateSub implements Subscriber<Integer> {

    Subscriber sub;

    public DelegateSub(Subscriber sub) {
        this.sub = sub;
    }

    public void onSubscribe(Subscription subscription) {
        sub.onSubscribe(subscription);// 그냥 전달만
    }

    public void onNext(Integer integer) {
        sub.onNext(integer); // 받은 함수를 적용
    }

    public void onError(Throwable throwable) {
        sub.onError(throwable); // 그냥 전달만
    }

    public void onComplete() {
        sub.onComplete(); // 그냥 전달만
    }
}
