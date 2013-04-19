package dgm.modules;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dgm.modules.bindingannotations.Degraphmalizes;
import dgm.modules.bindingannotations.Fetches;
import dgm.modules.bindingannotations.Recomputes;
import dgm.modules.elasticsearch.QueryFunction;

import java.util.concurrent.*;

public class ThreadpoolModule extends AbstractModule
{
    private static int MINTHREADPOOLSIZE=2;
    private static int MAXTHREADPOOLSIZE=64;


    @Override
    protected final void configure()
    {
        bind(QueryFunction.class);
    }

    @Provides
    @Singleton
    @Degraphmalizes
    final ExecutorService provideDegraphmalizesExecutor()
    {
        // single threaded updates!
        final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("degraphmalizer").build();

        return Executors.newSingleThreadExecutor(namedThreadFactory);
    }

    @Provides
    @Singleton
    @Recomputes
    final ExecutorService provideRecomputesExecutor()
    {
        final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("recomputer-%d").build();

        return new ThreadPoolExecutor(MINTHREADPOOLSIZE, MAXTHREADPOOLSIZE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                namedThreadFactory);
    }

    @Provides
    @Singleton
    @Fetches
    final ExecutorService provideFetchesExecutor()
    {
        final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("fetcher-%d").build();

        return new ThreadPoolExecutor(MINTHREADPOOLSIZE, MAXTHREADPOOLSIZE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                namedThreadFactory);
    }

}