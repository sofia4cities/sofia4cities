package com.indracompany.sofia2.iotbroker.ssap.generator;

import com.github.javafaker.Faker;
import com.indracompany.sofia2.iotbroker.ssap.generator.pojo.Person;

public class PojoGenerator {
	public static Person generatePerson() {
		Person person = new Person();
		person.setName(Faker.instance().name().firstName());
		person.setSurname(Faker.instance().name().lastName());
		person.setTelephone(Faker.instance().phoneNumber().cellPhone());
		
		return person;
	}

}
