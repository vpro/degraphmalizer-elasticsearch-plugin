package dgm.modules;

import dgm.Service;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

/**
 * Extends this class so you have two helper methods to do
 * {@link Service} or regular multibindings
 */
public abstract class ServiceModule extends AbstractModule {
    protected <S extends Service> void bindService(Class<S> serviceClass) {
        Multibinder.newSetBinder(binder(), Service.class).addBinding().to(serviceClass).in(Scopes.SINGLETON);
    }

    protected <S> LinkedBindingBuilder<S> multiBind(Class<S> c) {
        return Multibinder.newSetBinder(binder(), c).addBinding();
    }
}
