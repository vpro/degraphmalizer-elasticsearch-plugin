/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package dgm.configuration.javascript;

import java.io.IOException;
import java.util.Map;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public interface JavascriptSubgraph {
    void addEdge(String label, String index, String type, String id, boolean inwards, Map<String, Object> properties) throws IOException;
}
