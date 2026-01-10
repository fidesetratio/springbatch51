package com.app.tools.dummy;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class PersonItemReader implements ItemStreamReader<Person>{
	
	private final List<Person> data;
	private int index = 0;
	
	
	public PersonItemReader(List<Person> data) {
		this.data = data;
	}
	
	

	@Override
	public Person read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		  if (index >= data.size()) return null;
	        return data.get(index++);
	}
	/* 🔁 Restart support */
    @Override
    public void open(ExecutionContext ctx) {
        if (ctx.containsKey("person.reader.index")) {
            this.index = ctx.getInt("person.reader.index");
        }
    }

    @Override
    public void update(ExecutionContext ctx) {
        ctx.putInt("person.reader.index", index);
    }

    @Override
    public void close() { }
}
