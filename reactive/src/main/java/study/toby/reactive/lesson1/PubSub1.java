package study.toby.reactive.lesson1;

import lombok.extern.java.Log;

import java.util.Iterator;

public class PubSub1 {


    public static void main(String[] args) {

        Iterable<Integer> ita = new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {

                    final int MAX = 10;
                    int i = 0;

                    @Override
                    public boolean hasNext() {
                        System.out.println("hasNext");
                        return i < MAX;
                    }

                    @Override
                    public Integer next() {
                        System.out.println("next");
                        return ++i;
                    }
                };
            }
        };

        for(Integer i : ita){
            System.out.println(i);
        }

    }

}
