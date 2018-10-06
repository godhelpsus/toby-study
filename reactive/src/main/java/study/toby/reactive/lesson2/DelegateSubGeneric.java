package study.toby.reactive.lesson2;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DelegateSubGeneric<T, R> implements Subscriber<T> {

    Subscriber sub;

    public DelegateSubGeneric(Subscriber<? super R> sub) {
        this.sub = sub;
    }

    public void onSubscribe(Subscription subscription) {
        sub.onSubscribe(subscription);// 그냥 전달만
    }

    public void onNext(T t) {
        sub.onNext(t); // 그냥 전달만
    }

    public void onError(Throwable throwable) {
        sub.onError(throwable); // 그냥 전달만
    }

    public void onComplete() {
        sub.onComplete(); // 그냥 전달만
    }
}
