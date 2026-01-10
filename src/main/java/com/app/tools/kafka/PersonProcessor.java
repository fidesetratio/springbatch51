package com.app.tools.kafka;

import org.springframework.batch.item.ItemProcessor;

import com.app.tools.dummy.Person;

public class PersonProcessor implements ItemProcessor<PersonEvent, Person>{

	 	@Override
	    public Person process(PersonEvent event) {
	        Person p = new Person();
	        p.setId(event.getId());
	        p.setName(event.getName());
	        System.out.println("Processing event: " + event);
	        return p;
	    }

}
