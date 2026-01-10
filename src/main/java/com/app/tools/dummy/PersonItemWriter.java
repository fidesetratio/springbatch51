package com.app.tools.dummy;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class PersonItemWriter implements ItemWriter<Person> {

    @Override
    public void write(Chunk<? extends Person> chunk) {
        for (Person p : chunk) {
            System.out.println("WRITE => " + p);
        }
    }
}