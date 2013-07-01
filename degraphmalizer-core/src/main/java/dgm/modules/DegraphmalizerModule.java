package dgm.modules;

import dgm.Degraphmalizr;
import dgm.degraphmalizr.Degraphmalizer;
import dgm.degraphmalizr.recompute.Recomputer;
import dgm.degraphmalizr.recompute.RecomputerFactoryImpl;

import com.google.inject.AbstractModule;

public class DegraphmalizerModule extends AbstractModule
{
    @Override
    protected final void configure() {
        bind(Degraphmalizr.class).to(Degraphmalizer.class).asEagerSingleton();
        bind(Recomputer.class).to(RecomputerFactoryImpl.class).asEagerSingleton();
        bind(ServiceRunner.class).asEagerSingleton();
    }
}
