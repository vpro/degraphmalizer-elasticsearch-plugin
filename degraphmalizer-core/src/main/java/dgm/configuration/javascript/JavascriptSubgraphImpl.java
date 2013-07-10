package dgm.configuration.javascript;

import dgm.ID;
import dgm.JSONUtilities;
import dgm.Subgraph;
import dgm.graphs.MutableSubgraph;

import java.io.IOException;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Javascript interface to subgraph
 */
public class JavascriptSubgraphImpl implements JavascriptSubgraph {
    final MutableSubgraph subgraph = new MutableSubgraph();

    final private ObjectMapper om;
    final private Scriptable scope;
    final private Context cx;


    public JavascriptSubgraphImpl(ObjectMapper om, Context cx, Scriptable scope) {
        this.om = om;
        this.cx = cx;
        this.scope = scope;
    }

    @Override
    public final void addEdge(String label, String index, String type, String id, boolean inwards, Map<String, Object> properties) throws IOException {
        final ID other = new ID(index, type, id, 0);

        // call subgraph method
        final Subgraph.Direction d;

        if (inwards) {
            d = Subgraph.Direction.INWARDS;
        } else {
            d = Subgraph.Direction.OUTWARDS;
        }

        final MutableSubgraph.Edge e = subgraph.beginEdge(label, other, d);

        for (Map.Entry<String, Object> p : properties.entrySet()) {
            // convert into JsonNode
            final JsonNode result = JSONUtilities.fromJSONObject(om, cx, scope, p.getValue());

            // Store it
            e.property(p.getKey(), result);
        }
    }

    public final void setProperty(String key, Object value) throws IOException {
        // convert into JsonNode
        final JsonNode result = JSONUtilities.fromJSONObject(om, cx, scope, value);
        subgraph.property(key, result);
    }
}
