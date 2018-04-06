package com.indracompany.sofia2.persistence.elasticsearch;

import lombok.Data;

@Data
public class ElasticSearchRequest {
    
    private String index;
    private String type;
    private String id;

}