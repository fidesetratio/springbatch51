package com.app.tools.dummy;

import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(Person item) {
        if (item.getAge() < 18) return null; // skip
        item.setName(item.getName().toUpperCase());
        return item;
    }
}