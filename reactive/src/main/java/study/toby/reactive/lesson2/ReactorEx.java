package study.toby.reactive.lesson2;

import reactor.core.publisher.Flux;

public class ReactorEx {

    public static void main(String[] args) {

        Flux.<Integer>create(e -> {
            e.next(1);
            e.next(2);
            e.next(3);
            e.next(4);
            e.next(5);
            e.complete();
        })
        .filter(s -> s%2==0)
        .map(s->s*10)
//        .log()
//        .reduce(0, (a,b)->a+b)
//        .log()
        .subscribe(System.out::println);

    }
}
