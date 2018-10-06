package study.toby.reactive;

import lombok.extern.java.Log;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@SpringBootApplication
public class ReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);

        System.out.println("# hello Spring-Boot. #");
//        log.info("# hello Spring-Boot. #");

    }

    @RestController
    public static class Controller{

        @RequestMapping("/hello")
        public Publisher<String> hello(String name){
            return new Publisher<String>() {
                @Override
                public void subscribe(Subscriber<? super String> sub) {
                    sub.onSubscribe(new Subscription() {
                        @Override
                        public void request(long l) {
                            sub.onNext("hello "+name);
                            System.out.println(name);
                            sub.onComplete();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            };
        }
    }
}
