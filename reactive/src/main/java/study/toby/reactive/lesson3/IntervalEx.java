package study.toby.reactive.lesson3;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class IntervalEx {


    public static void main(String[] args) throws InterruptedException {


        Flux.interval(Duration.ofMillis(1)) // demon thread 로 실행됨
            .take(10)
            .subscribe(s-> log.info("onNext : {}", s));


        TimeUnit.MILLISECONDS.sleep(1000);

        System.out.println("exit");

    }

}
