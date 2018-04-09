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
package com.indracompany.sofia2.persistence.elasticsearch.api;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ESCountService {

	@Autowired
	ESBaseApi connector;

    public long getMatchAllQueryCountByType(String type, String... indexes) {
        QueryBuilder query = matchAllQuery();
        log.info("getMatchAllQueryCount query =>"+ query.toString());
        long count = connector.getClient().prepareSearch(indexes).setQuery(query).setTypes(type).setSize(0).execute().actionGet().getHits().getTotalHits();
        return count;
    }
    
    public long getMatchAllQueryCount(String... indexes) {
        QueryBuilder query = matchAllQuery();
        log.info("getMatchAllQueryCount query =>"+ query.toString());
        long count = connector.getClient().prepareSearch(indexes).setQuery(query).setSize(0).execute().actionGet().getHits().getTotalHits();
        return count;
    }


    public long getQueryCount(String jsonQueryString,String... indexes) {
    	log.info("getQueryCount query =>"+ jsonQueryString.toString());
        WrapperQueryBuilder build = QueryBuilders.wrapperQuery(jsonQueryString);
        long count = connector.getClient().prepareSearch(indexes).setQuery(build).setSize(0).execute().actionGet().getHits().getTotalHits();
        return count;
    }

}