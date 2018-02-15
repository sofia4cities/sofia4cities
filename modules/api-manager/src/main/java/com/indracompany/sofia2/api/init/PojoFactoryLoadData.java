package com.indracompany.sofia2.api.init;

import java.util.UUID;

public class PojoFactoryLoadData {
	
	
	public static Product createProduct(String name) {
		Product product = new Product();
		product.setCategory("CATEGORY");
		product.setCode(UUID.randomUUID().toString());
		product.setGroupId("GROUP");
		product.setMainImage("IMAGE");
		product.setName(name);
		return product;
	}


}
