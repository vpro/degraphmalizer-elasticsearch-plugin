/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package dgm.modules.elasticsearch;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import dgm.ID;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.nnsoft.guice.sli4j.core.InjectLogger;
import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;

/**
 * User: rico
 * Date: 05/06/2013
 */
public class DocumentProvider
{
    @InjectLogger
    protected Logger log;

    protected final Client searchIndex;

    protected LoadingCache<ID, GetResponse> documentCache;

    @Inject
    public DocumentProvider(Client searchIndex)
    {
        this.searchIndex = searchIndex;
        this.documentCache = CacheBuilder.newBuilder().maximumSize(2048).recordStats().build(new DocumentLoader());
    }

    public GetResponse get(ID id) {
        try {
            return documentCache.get(id);
        } catch (ExecutionException ee) {
            log.error("Error retrieving document {} : {}",id,ee.getMessage());
            return null;
        }
    }

    class DocumentLoader extends CacheLoader<ID, GetResponse>
    {
        @Override
        public GetResponse load(ID id) throws Exception
        {
            // query ES for the document
            return searchIndex.prepareGet(id.index(), id.type(), id.id()).execute().actionGet();
        }
    }
}
