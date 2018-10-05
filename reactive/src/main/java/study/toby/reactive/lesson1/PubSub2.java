package study.toby.reactive.lesson1;

import java.util.Iterator;

public class PubSub2 {


    public static void main(String[] args) {

        Iterable<Integer> ita = () ->
            new Iterator<Integer>() {
                int i=0;
                final int MAX = 10;
                @Override
                public boolean hasNext() {
                    System.out.println("hasNext");
                    return i<MAX;
                }

                @Override
                public Integer next() {
                    System.out.println("next");
                    return ++i;
                }
            };

        // pull
        for(Integer i : ita){
            System.out.println(i);
        }

    }

}
