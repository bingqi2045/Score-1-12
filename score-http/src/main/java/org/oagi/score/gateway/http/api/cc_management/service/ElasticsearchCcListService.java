package org.oagi.score.gateway.http.api.cc_management.service;

import org.oagi.score.gateway.http.api.cc_management.data.elasticsearch.CoreComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class ElasticsearchCcListService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public SearchPage<CoreComponent> getCcListES(Query criteriaQuery) {
        SearchHits<CoreComponent> hits = elasticsearchOperations.search(criteriaQuery,
                CoreComponent.class, IndexCoordinates.of("core_component"));
        return SearchHitSupport.searchPageFor(hits, criteriaQuery.getPageable());
    }

}
