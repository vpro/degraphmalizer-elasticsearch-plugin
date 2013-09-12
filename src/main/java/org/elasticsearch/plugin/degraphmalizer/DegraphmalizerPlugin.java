package org.elasticsearch.plugin.degraphmalizer;

import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.plugin.degraphmalizer.updater.UpdaterManager;
import org.elasticsearch.plugins.AbstractPlugin;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents the Degraphmalizer Elastic Search plugin. It specifies its the dependency injection modules
 * and services.
 */
public class DegraphmalizerPlugin extends AbstractPlugin {

    @Override
    public String name()
    {
        return "degraphmalizer";
    }

    @Override
    public String description()
    {
        return "Extract 'inter-document' graph structure and use it to compute new attributes";
    }

    @Override
    public Collection<Class<? extends Module>> modules()
    {
        final ArrayList<Class<? extends Module>> modules = new ArrayList<Class<? extends Module>>();
        modules.add(DegraphmalizerPluginModule.class);
        return modules;
    }

    @Override
    public Collection<Class<? extends LifecycleComponent>> services()
    {
        final ArrayList<Class<? extends LifecycleComponent>> services = new ArrayList<Class<? extends LifecycleComponent>>();
        services.add(UpdaterManager.class);
        return services;
    }
}