package com.indracompany.sofia2.api.init;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;



/**
 * Info osa_product
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "groupId",
    "imageList",
    "code",
    "name",
    "category",
    "mainImage"
})
public class Product {

    @JsonProperty("groupId")
    private String groupId;
    @JsonProperty("imageList")
    private List<String> imageList = new ArrayList<String>();
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
    @JsonProperty("category")
    private String category;
    @JsonProperty("mainImage")
    private String mainImage;

    @JsonProperty("groupId")
    public String getGroupId() {
        return groupId;
    }

    @JsonProperty("groupId")
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @JsonProperty("imageList")
    public List<String> getImageList() {
        return imageList;
    }

    @JsonProperty("imageList")
    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("mainImage")
    public String getMainImage() {
        return mainImage;
    }

    @JsonProperty("mainImage")
    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(groupId).append(imageList).append(code).append(name).append(category).append(mainImage).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Product) == false) {
            return false;
        }
        Product rhs = ((Product) other);
        return new EqualsBuilder().append(groupId, rhs.groupId).append(imageList, rhs.imageList).append(code, rhs.code).append(name, rhs.name).append(category, rhs.category).append(mainImage, rhs.mainImage).isEquals();
    }

}
