package dgm.configuration;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configurations
{
    final static private Logger LOG = LoggerFactory.getLogger(Configurations.class);

    /**
     * Find all TypeConfigs with specified source index and source type
     */
    public static Iterable<TypeConfig> configsFor(Configuration cfg, String srcIndex, String srcType)
    {
        StringBuilder logMessage = null;

        if(LOG.isDebugEnabled())
        {
            logMessage = new StringBuilder("Matching request for /");
            logMessage.append(srcIndex).append("/").append(srcType);
            logMessage.append(" to [");
        }

        final List<TypeConfig> configs = new ArrayList<TypeConfig>();

        // find all matching configs
        for(IndexConfig i : cfg.indices().values())
            for(TypeConfig t : i.types().values())
                if (srcIndex.equals(t.sourceIndex()) && (srcType == null || srcType.equals(t.sourceType()))) {
                    if((logMessage != null))
                    {
                        logMessage.append(" /").append(t.targetIndex());
                        logMessage.append("/").append(t.targetType());
                        logMessage.append(", ");
                    }

                    configs.add(t);
                }

        if(logMessage != null)
            LOG.debug(logMessage.append("]").toString());

        return configs;
    }
}
