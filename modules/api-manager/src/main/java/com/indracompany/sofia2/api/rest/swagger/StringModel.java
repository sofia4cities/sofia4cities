/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indracompany.sofia2.api.rest.swagger;
import java.util.Map;

import io.swagger.models.AbstractModel;
import io.swagger.models.properties.Property;

public class StringModel extends AbstractModel {
    private Map<String, Property> properties;
    private String type;
    private String description;
    private Property items;
    private Object example;
    private Integer minItems;
    private Integer maxItems;

    public StringModel() {
        this.type = "";
    }

    public StringModel description(String description) {
        this.setDescription(description);
        return this;
    }

    public StringModel items(Property items) {
        this.setItems(items);
        return this;
    }

    public StringModel minItems(int minItems) {
        this.setMinItems(minItems);
        return this;
    }

    public StringModel maxItems(int maxItems) {
        this.setMaxItems(maxItems);
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Property getItems() {
        return items;
    }

    public void setItems(Property items) {
        this.items = items;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public Object getExample() {
        return example;
    }

    public void setExample(Object example) {
        this.example = example;
    }

    public Integer getMinItems() {
        return minItems;
    }

    public void setMinItems(Integer minItems) {
        this.minItems = minItems;
    }

    public Integer getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
    }

   

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((example == null) ? 0 : example.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((maxItems == null) ? 0 : maxItems.hashCode());
		result = prime * result + ((minItems == null) ? 0 : minItems.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringModel other = (StringModel) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (example == null) {
			if (other.example != null)
				return false;
		} else if (!example.equals(other.example))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (maxItems == null) {
			if (other.maxItems != null)
				return false;
		} else if (!maxItems.equals(other.maxItems))
			return false;
		if (minItems == null) {
			if (other.minItems != null)
				return false;
		} else if (!minItems.equals(other.minItems))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public Object clone() {
    	StringModel cloned = new StringModel();
        super.cloneTo(cloned);

        cloned.properties = this.properties;
        cloned.type = this.type;
        cloned.description = this.description;
        cloned.items = this.items;
        cloned.example = this.example;

        return cloned;

    }

}