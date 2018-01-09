/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb.index;


import java.util.Map;

import org.bson.Document;

import com.indracompany.sofia2.persistence.mongodb.UtilMongoDB;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A class that represents a MongoDB index
 */
@ToString
public class MongoDbIndex {
	
	@Getter @Setter private int version;
	@Getter @Setter private String name;
	@Getter @Setter private String namespace;
	@Getter @Setter private Map<String, Integer> key;
	@Getter @Setter private MongoDbIndexOptions indexOptions;
	
	public MongoDbIndex() {}
	
	public MongoDbIndex(Map<String, Integer> key, MongoDbIndexOptions indexOptions) {
		this.key = key;
		this.indexOptions = indexOptions;
	}
	
	public MongoDbIndex(Map<String, Integer> key) {
		this(key, null);
	}
	
	public MongoDbIndex(String name) {
		this.name = name;
	}
	public static MongoDbIndex fromIndexDocument(Document index_asDocument) {
		MongoDbIndex index = new MongoDbIndex();
		index.setName(index_asDocument.getString("name"));
		index.setKey(new UtilMongoDB().toJavaMap(index_asDocument.get("key", Document.class), Integer.class));
		index.setVersion(index_asDocument.getInteger("v", 1));
		index.setNamespace(index_asDocument.getString("ns"));
		MongoDbIndexOptions indexOptions = new MongoDbIndexOptions();
		indexOptions.setUnique(index_asDocument.getBoolean("unique"));
		indexOptions.setSparse(index_asDocument.getBoolean("sparse"));
		indexOptions.setBackground(index_asDocument.getBoolean("background"));
		indexOptions.setExpireAfterSeconds(index_asDocument.getLong("expireAfterSeconds"));
		if (!indexOptions.isDefaultConfiguration())
			index.setIndexOptions(indexOptions);
		return index;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MongoDbIndex))
			return false;
		MongoDbIndex anotherIndex = (MongoDbIndex) other;
		if (this.name != null)
			return (this.name.equals(anotherIndex.name));
		else
			return this.key.equals(anotherIndex.key);
	}

}
