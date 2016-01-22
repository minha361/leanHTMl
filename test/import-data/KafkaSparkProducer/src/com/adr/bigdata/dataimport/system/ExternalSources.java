package com.adr.bigdata.dataimport.system;

import com.mario.consumer.cache.CacheWrapper;
import com.nhb.common.db.sql.DBIAdapter;

public class ExternalSources
{
    private DBIAdapter dbiAdapter;
    private CacheWrapper cacheWrapper;
    
    public DBIAdapter getDbiAdapter() {
        return this.dbiAdapter;
    }
    
    public void setDbiAdapter(final DBIAdapter dbiAdapter) {
        this.dbiAdapter = dbiAdapter;
    }
    
    public CacheWrapper getCacheWrapper() {
        return this.cacheWrapper;
    }
    
    public void setCacheWrapper(final CacheWrapper cacheWrapper) {
        this.cacheWrapper = cacheWrapper;
    }
    
    public ExternalSources(final DBIAdapter adapter, final CacheWrapper cacheWrapper) {
        this.dbiAdapter = adapter;
        this.cacheWrapper = cacheWrapper;
    }
    
    public static ExternalSources newInstance(final DBIAdapter adapter, final CacheWrapper cacheWrapper) {
        return new ExternalSources(adapter, cacheWrapper);
    }
}
