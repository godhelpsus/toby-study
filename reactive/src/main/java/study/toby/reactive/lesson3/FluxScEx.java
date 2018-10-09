package study.toby.reactive.lesson3;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class FluxScEx {

    public static void main(String[] args) {


        Flux.range(0, 10)
            .publishOn(Schedulers.newSingle("pub"))
            .log()
            .subscribeOn(Schedulers.newSingle("sub"))
            .subscribe( s-> System.out.println(s) );

        System.out.println("exit");

    }
}
