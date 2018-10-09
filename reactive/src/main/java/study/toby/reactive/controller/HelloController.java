package study.toby.reactive.controller;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class HelloController {

    @RequestMapping("/hello")
    public Publisher<String> hello(String name){

        return new Publisher<String>() {
            @Override
            public void subscribe(Subscriber<? super String> s) {
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {
                        s.onNext("Hello "+name);

                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        s.onComplete();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

    }

}
