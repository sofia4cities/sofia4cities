package com.indracompany.sofia2.examples.scalability.msgs;

import lombok.Getter;

public class Injector {
	
	public Injector(int injector, String dataToInsert) {
		this.injector = injector;
		this.dataToInsert = dataToInsert;
	}
	
	@Getter private int injector;
	@Getter private String dataToInsert;
	
	@Override
	public String toString() {
		return Integer.toString(injector);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Injector))
			return false;
		Injector that = (Injector) o;
		return this.injector == that.injector;
	}
	
	@Override
	public int hashCode() {
		return injector;
	}
}


