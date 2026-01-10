package com.app.tools.kafka;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.app.tools.dummy.Person;

public class PersonWriter implements ItemWriter<Person> {

	  @Override
	    public void write(Chunk<? extends Person> chunk) {
	        for (Person p : chunk) {
	            System.out.println("WRITE => " + p);
	        }
	    }

}
