/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.testframework.junits.common;

import org.apache.ignite.*;
import org.apache.ignite.cache.*;
import org.apache.ignite.cache.affinity.*;
import org.apache.ignite.cluster.*;
import org.apache.ignite.compute.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.events.*;
import org.apache.ignite.internal.*;
import org.apache.ignite.lang.*;
import org.apache.ignite.internal.processors.cache.distributed.dht.*;
import org.apache.ignite.internal.processors.cache.distributed.dht.colocated.*;
import org.apache.ignite.internal.processors.cache.distributed.near.*;
import org.apache.ignite.internal.processors.cache.local.*;
import org.apache.ignite.internal.util.typedef.*;
import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.testframework.*;
import org.apache.ignite.testframework.junits.*;
import org.jetbrains.annotations.*;

import javax.net.ssl.*;
import java.util.*;

import static org.apache.ignite.internal.processors.cache.GridCacheUtils.*;
import static org.apache.ignite.cache.CacheMode.*;
import static org.apache.ignite.cache.CachePreloadMode.*;

/**
 * Super class for all common tests.
 */
public abstract class GridCommonAbstractTest extends GridAbstractTest {
    /**
     * @param startGrid If {@code true}, then grid node will be auto-started.
     */
    protected GridCommonAbstractTest(boolean startGrid) {
        super(startGrid);
    }

    /** */
    protected GridCommonAbstractTest() {
        super(false);
    }

    /**
     * @param idx Grid index.
     * @return Cache.
     */
    protected <K, V> GridCache<K, V> cache(int idx) {
        return grid(idx).cachex();
    }

    /**
     * @param idx Grid index.
     * @return Cache.
     */
    protected <K, V> IgniteCache<K, V> jcache(int idx) {
        return grid(idx).jcache(null);
    }

    /**
     * @param idx Grid index.
     * @param name Cache name.
     * @return Cache.
     */
    protected <K, V> GridCache<K, V> cache(int idx, String name) {
        return grid(idx).cachex(name);
    }

    /**
     * @return Cache.
     */
    protected <K, V> GridCache<K, V> cache() {
        return grid().cachex();
    }

    /**
     * @return Cache.
     */
    protected <K, V> IgniteCache<K, V> jcache() {
        return grid().jcache(null);
    }

    /**
     * @return Cache.
     */
    protected <K, V> GridLocalCache<K, V> local() {
        return (GridLocalCache<K, V>)((IgniteKernal)grid()).<K, V>internalCache();
    }

    /**
     * @param cache Cache.
     * @return DHT cache.
     */
    protected static <K, V> GridDhtCacheAdapter<K, V> dht(CacheProjection<K,V> cache) {
        return nearEnabled(cache) ? near(cache).dht() :
            ((IgniteKernal)cache.gridProjection().ignite()).<K, V>internalCache(cache.name()).context().dht();
    }

    /**
     * @return DHT cache.
     */
    protected <K, V> GridDhtCacheAdapter<K, V> dht() {
        return this.<K, V>near().dht();
    }

    /**
     * @param idx Grid index.
     * @return DHT cache.
     */
    protected <K, V> GridDhtCacheAdapter<K, V> dht(int idx) {
        return this.<K, V>near(idx).dht();
    }

    /**
     * @param idx Grid index.
     * @param cache Cache name.
     * @return DHT cache.
     */
    protected <K, V> GridDhtCacheAdapter<K, V> dht(int idx, String cache) {
        return this.<K, V>near(idx, cache).dht();
    }

    /**
     * @param idx Grid index.
     * @param cache Cache name.
     * @return Colocated cache.
     */
    protected <K, V> GridDhtColocatedCache<K, V> colocated(int idx, String cache) {
        return (GridDhtColocatedCache<K, V>)((IgniteKernal)grid(idx)).internalCache(cache);
    }

    /**
     * @param cache Cache.
     * @return {@code True} if near cache is enabled.
     */
    protected static <K, V> boolean nearEnabled(CacheProjection<K,V> cache) {
        CacheConfiguration cfg = ((IgniteKernal)cache.gridProjection().ignite()).
            <K, V>internalCache(cache.name()).context().config();

        return isNearEnabled(cfg);
    }

    /**
     * @param cache Cache.
     * @return Near cache.
     */
    protected static <K, V> GridNearCacheAdapter<K, V> near(CacheProjection<K,V> cache) {
        return ((IgniteKernal)cache.gridProjection().ignite()).<K, V>internalCache(cache.name()).context().near();
    }

    /**
     * @param cache Cache.
     * @return Colocated cache.
     */
    protected static <K, V> GridDhtColocatedCache<K, V> colocated(CacheProjection<K,V> cache) {
        return ((IgniteKernal)cache.gridProjection().ignite()).<K, V>internalCache(cache.name()).context().colocated();
    }

    /**
     * @return Near cache.
     */
    protected <K, V> GridNearCacheAdapter<K, V> near() {
        return ((IgniteKernal)grid()).<K, V>internalCache().context().near();
    }

    /**
     * @param idx Grid index.
     * @return Near cache.
     */
    protected <K, V> GridNearCacheAdapter<K, V> near(int idx) {
        return ((IgniteKernal)grid(idx)).<K, V>internalCache().context().near();
    }

    /**
     * @param idx Grid index.
     * @return Colocated cache.
     */
    protected <K, V> GridDhtColocatedCache<K, V> colocated(int idx) {
        return (GridDhtColocatedCache<K, V>)((IgniteKernal)grid(idx)).<K, V>internalCache();
    }

    /**
     * @param idx Grid index.
     * @param cache Cache name.
     * @return Near cache.
     */
    protected <K, V> GridNearCacheAdapter<K, V> near(int idx, String cache) {
        return ((IgniteKernal)grid(idx)).<K, V>internalCache(cache).context().near();
    }

    /** {@inheritDoc} */
    @Override protected final boolean isJunitFrameworkClass() {
        return true;
    }

    /** {@inheritDoc} */
    @Override protected final void setUp() throws Exception {
        // Disable SSL hostname verifier.
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override public boolean verify(String s, SSLSession sslSes) {
                return true;
            }
        });

        getTestCounters().incrementStarted();

        super.setUp();
    }

    /** {@inheritDoc} */
    @Override protected final void tearDown() throws Exception {
        getTestCounters().incrementStopped();

        super.tearDown();
    }

    /** {@inheritDoc} */
    @Override protected final Ignite startGridsMultiThreaded(int cnt) throws Exception {
        Ignite g = super.startGridsMultiThreaded(cnt);

        awaitPartitionMapExchange();

        return g;
    }

    /**
     * @throws InterruptedException If interrupted.
     */
    @SuppressWarnings("BusyWait")
    protected void awaitPartitionMapExchange() throws InterruptedException {
        for (Ignite g : G.allGrids()) {
            for (GridCache<?, ?> c : ((IgniteEx)g).cachesx()) {
                CacheConfiguration cfg = c.configuration();

                if (cfg.getCacheMode() == PARTITIONED && cfg.getPreloadMode() != NONE && g.cluster().nodes().size() > 1) {
                    CacheAffinityFunction aff = cfg.getAffinity();

                    GridDhtCacheAdapter<?, ?> dht = dht(c);

                    GridDhtPartitionTopology<?, ?> top = dht.topology();

                    for (int p = 0; p < aff.partitions(); p++) {
                        long start = 0;

                        for (int i = 0; ; i++) {
                            // Must map on updated version of topology.
                            Collection<ClusterNode> affNodes = c.affinity().mapPartitionToPrimaryAndBackups(p);

                            int exp = affNodes.size();

                            Collection<ClusterNode> owners = top.nodes(p, -1);

                            int actual = owners.size();

                            if (affNodes.size() != owners.size() || !affNodes.containsAll(owners)) {
                                LT.warn(log(), null, "Waiting for topology map update [grid=" + g.name() +
                                    ", p=" + p + ", nodes=" + exp + ", owners=" + actual +
                                    ", affNodes=" + affNodes + ", owners=" + owners +
                                    ", locNode=" + g.cluster().localNode().id() + ']');

                                if (i == 0)
                                    start = System.currentTimeMillis();

                                Thread.sleep(200); // Busy wait.

                                continue;
                            }

                            if (i > 0)
                                log().warning("Finished waiting for topology map update [grid=" + g.name() +
                                    ", p=" + p + ", duration=" + (System.currentTimeMillis() - start) + "ms]");

                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * @param cache Cache.
     * @return Key for which given cache is primary.
     * @throws IgniteCheckedException If failed.
     */
    protected Integer primaryKey(CacheProjection<?, ?> cache)
        throws IgniteCheckedException {
        return primaryKeys(cache, 1, 1).get(0);
    }

    /**
     * @param cache Cache.
     * @param cnt Keys count.
     * @return Collection of keys for which given cache is primary.
     * @throws IgniteCheckedException If failed.
     */
    protected List<Integer> primaryKeys(CacheProjection<?, ?> cache, int cnt)
        throws IgniteCheckedException {
        return primaryKeys(cache, cnt, 1);
    }

    /**
     * @param cache Cache.
     * @param cnt Keys count.
     * @param startFrom Start value for keys search.
     * @return Collection of keys for which given cache is primary.
     * @throws IgniteCheckedException If failed.
     */
    protected List<Integer> primaryKeys(CacheProjection<?, ?> cache, int cnt, int startFrom)
        throws IgniteCheckedException {
        assert cnt > 0 : cnt;

        List<Integer> found = new ArrayList<>(cnt);

        ClusterNode locNode = cache.gridProjection().ignite().cluster().localNode();

        CacheAffinity<Integer> aff = cache.<Integer, Object>cache().affinity();

        for (int i = startFrom; i < startFrom + 100_000; i++) {
            Integer key = i;

            if (aff.isPrimary(locNode, key)) {
                found.add(key);

                if (found.size() == cnt)
                    return found;
            }
        }

        throw new IgniteCheckedException("Unable to find " + cnt + " keys as primary for cache.");
    }

    /**
     * @param cache Cache.
     * @return Key for which given cache is backup.
     * @throws IgniteCheckedException If failed.
     */
    protected Integer backupKey(CacheProjection<?, ?> cache)
        throws IgniteCheckedException {
        return backupKeys(cache, 1, 1).get(0);
    }

    /**
     * @param cache Cache.
     * @param cnt Keys count.
     * @return Collection of keys for which given cache is backup.
     * @throws IgniteCheckedException If failed.
     */
    protected List<Integer> backupKeys(CacheProjection<?, ?> cache, int cnt)
        throws IgniteCheckedException {
        return backupKeys(cache, cnt, 1);
    }

    /**
     * @param cache Cache.
     * @param cnt Keys count.
     * @param startFrom Start value for keys search.
     * @return Collection of keys for which given cache is backup.
     * @throws IgniteCheckedException If failed.
     */
    protected List<Integer> backupKeys(CacheProjection<?, ?> cache, int cnt, int startFrom)
        throws IgniteCheckedException {
        assert cnt > 0 : cnt;

        List<Integer> found = new ArrayList<>(cnt);

        ClusterNode locNode = cache.gridProjection().ignite().cluster().localNode();

        CacheAffinity<Integer> aff = cache.<Integer, Object>cache().affinity();

        for (int i = startFrom; i < startFrom + 100_000; i++) {
            Integer key = i;

            if (aff.isBackup(locNode, key)) {
                found.add(key);

                if (found.size() == cnt)
                    return found;
            }
        }

        throw new IgniteCheckedException("Unable to find " + cnt + " keys as backup for cache.");
    }

    /**
     * @param cache Cache.
     * @return Keys for which given cache is neither primary nor backup.
     * @throws IgniteCheckedException If failed.
     */
    protected Integer nearKey(CacheProjection<?, ?> cache)
        throws IgniteCheckedException {
        return nearKeys(cache, 1, 1).get(0);
    }

    /**
     * @param cache Cache.
     * @param cnt Keys count.
     * @return Collection of keys for which given cache is neither primary nor backup.
     * @throws IgniteCheckedException If failed.
     */
    protected List<Integer> nearKeys(CacheProjection<?, ?> cache, int cnt)
        throws IgniteCheckedException {
        return nearKeys(cache, cnt, 1);
    }

    /**
     * @param cache Cache.
     * @param cnt Keys count.
     * @param startFrom Start value for keys search.
     * @return Collection of keys for which given cache is neither primary nor backup.
     * @throws IgniteCheckedException If failed.
     */
    protected List<Integer> nearKeys(CacheProjection<?, ?> cache, int cnt, int startFrom)
        throws IgniteCheckedException {
        assert cnt > 0 : cnt;

        List<Integer> found = new ArrayList<>(cnt);

        ClusterNode locNode = cache.gridProjection().ignite().cluster().localNode();

        CacheAffinity<Integer> aff = cache.<Integer, Object>cache().affinity();

        for (int i = startFrom; i < startFrom + 100_000; i++) {
            Integer key = i;

            if (!aff.isPrimaryOrBackup(locNode, key)) {
                found.add(key);

                if (found.size() == cnt)
                    return found;
            }
        }

        throw new IgniteCheckedException("Unable to find " + cnt + " keys as backup for cache.");
    }

    /**
     * @param cache Cache.
     * @param cnt Keys count.
     * @param startFrom Start value for keys search.
     * @return Collection of keys for which given cache is primary.
     * @throws IgniteCheckedException If failed.
     */
    protected List<Integer> primaryKeys(IgniteCache<?, ?> cache, int cnt, int startFrom)
        throws IgniteCheckedException {
        CacheProjection<?, ?> prj = GridTestUtils.getFieldValue(cache, "delegate");

        return primaryKeys(prj, cnt, startFrom);
    }

    /**
     * @param cache Cache.
     * @param cnt Keys count.
     * @param startFrom Start value for keys search.
     * @return Collection of keys for which given cache is backup.
     * @throws IgniteCheckedException If failed.
     */
    protected List<Integer> backupKeys(IgniteCache<?, ?> cache, int cnt, int startFrom)
        throws IgniteCheckedException {
        CacheProjection<?, ?> prj = GridTestUtils.getFieldValue(cache, "delegate");

        return backupKeys(prj, cnt, startFrom);
    }

    /**
     * @param cache Cache.
     * @param cnt Keys count.
     * @param startFrom Start value for keys search.
     * @return Collection of keys for which given cache is neither primary nor backup.
     * @throws IgniteCheckedException If failed.
     */
    protected List<Integer> nearKeys(IgniteCache<?, ?> cache, int cnt, int startFrom)
        throws IgniteCheckedException {
        CacheProjection<?, ?> prj = GridTestUtils.getFieldValue(cache, "delegate");

        return nearKeys(prj, cnt, startFrom);
    }

    /**
     * @param key Key.
     * @param cacheName Cache name.
     * @return Cache.
     */
    protected <K, V> IgniteCache<K, V> primaryCache(Object key, @Nullable String cacheName) {
        ClusterNode node = grid(0).cache(cacheName).affinity().mapKeyToNode(key);

        assertNotNull(node);

        return grid((String)node.attribute(GridNodeAttributes.ATTR_GRID_NAME)).jcache(cacheName);
    }

    /**
     * @param cache Cache.
     * @return Collection of keys for which given cache is primary.
     * @throws IgniteCheckedException If failed.
     */
    protected Integer primaryKey(IgniteCache<?, ?> cache)
        throws IgniteCheckedException {
        CacheProjection<?, ?> prj = GridTestUtils.getFieldValue(cache, "delegate");

        return primaryKey(prj);
    }

    /**
     * @param cache Cache.
     * @return Keys for which given cache is backup.
     * @throws IgniteCheckedException If failed.
     */
    protected Integer backupKey(IgniteCache<?, ?> cache)
        throws IgniteCheckedException {
        CacheProjection<?, ?> prj = GridTestUtils.getFieldValue(cache, "delegate");

        return backupKey(prj);
    }

    /**
     * @param cache Cache.
     * @return Key for which given cache is neither primary nor backup.
     * @throws IgniteCheckedException If failed.
     */
    protected Integer nearKey(IgniteCache<?, ?> cache)
        throws IgniteCheckedException {
        CacheProjection<?, ?> prj = GridTestUtils.getFieldValue(cache, "delegate");

        return nearKey(prj);
    }

    /**
     * @param comp Compute.
     * @param task Task.
     * @param arg Task argument.
     * @return Task future.
     * @throws IgniteCheckedException If failed.
     */
    protected <R> ComputeTaskFuture<R> executeAsync(IgniteCompute comp, ComputeTask task, @Nullable Object arg)
        throws IgniteCheckedException {
        comp = comp.withAsync();

        assertNull(comp.execute(task, arg));

        ComputeTaskFuture<R> fut = comp.future();

        assertNotNull(fut);

        return fut;
    }

    /**
     * @param comp Compute.
     * @param taskName Task name.
     * @param arg Task argument.
     * @return Task future.
     * @throws IgniteCheckedException If failed.
     */
    protected <R> ComputeTaskFuture<R> executeAsync(IgniteCompute comp, String taskName, @Nullable Object arg)
        throws IgniteCheckedException {
        comp = comp.withAsync();

        assertNull(comp.execute(taskName, arg));

        ComputeTaskFuture<R> fut = comp.future();

        assertNotNull(fut);

        return fut;
    }

    /**
     * @param comp Compute.
     * @param taskCls Task class.
     * @param arg Task argument.
     * @return Task future.
     * @throws IgniteCheckedException If failed.
     */
    @SuppressWarnings("unchecked")
    protected <R> ComputeTaskFuture<R> executeAsync(IgniteCompute comp, Class taskCls, @Nullable Object arg)
        throws IgniteCheckedException {
        comp = comp.withAsync();

        assertNull(comp.execute(taskCls, arg));

        ComputeTaskFuture<R> fut = comp.future();

        assertNotNull(fut);

        return fut;
    }

    /**
     * @param evts Events.
     * @param filter Filter.
     * @param types Events types.
     * @return Future.
     * @throws IgniteCheckedException If failed.
     */
    protected <T extends IgniteEvent> IgniteFuture<T> waitForLocalEvent(IgniteEvents evts,
        @Nullable IgnitePredicate<T> filter, @Nullable int... types) throws IgniteCheckedException {
        evts = evts.withAsync();

        assertTrue(evts.isAsync());

        assertNull(evts.waitForLocal(filter, types));

        IgniteFuture<T> fut = evts.future();

        assertNotNull(fut);

        return fut;
    }

    /**
     * @param ignite Grid.
     * @return {@link org.apache.ignite.IgniteCompute} for given grid's local node.
     */
    protected IgniteCompute forLocal(Ignite ignite) {
        return ignite.compute(ignite.cluster().forLocal());
    }

    /**
     * @param prj Projection.
     * @return {@link org.apache.ignite.IgniteCompute} for given projection.
     */
    protected IgniteCompute compute(ClusterGroup prj) {
        return prj.ignite().compute(prj);
    }

    /**
     * @param prj Projection.
     * @return {@link org.apache.ignite.IgniteMessaging} for given projection.
     */
    protected IgniteMessaging message(ClusterGroup prj) {
        return prj.ignite().message(prj);
    }

    /**
     * @param prj Projection.
     * @return {@link org.apache.ignite.IgniteMessaging} for given projection.
     */
    protected IgniteEvents events(ClusterGroup prj) {
        return prj.ignite().events(prj);
    }

    /**
     * @param cfg Configuration.
     * @param cacheName Cache name.
     * @return Cache configuration.
     */
    protected CacheConfiguration cacheConfiguration(IgniteConfiguration cfg, String cacheName) {
        for (CacheConfiguration ccfg : cfg.getCacheConfiguration()) {
            if (F.eq(cacheName, ccfg.getName()))
                return ccfg;
        }

        fail("Failed to find cache configuration for cache: " + cacheName);

        return null;
    }
}
