/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package dgm.configuration.javascript;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import dgm.JSONUtilities;
import dgm.modules.elasticsearch.ResolvedPathElement;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * User: rico
 * Date: 26/04/2013
 */
public class JavascriptNode
{
    ResolvedPathElement resolvedPathElement;
    Context context;
    Scriptable scope;


    public JavascriptNode(Context context, Scriptable scope, ResolvedPathElement resolvedPathElement)
    {
        this.resolvedPathElement = resolvedPathElement;
        this.context = context;
        this.scope = scope;
    }

    public Edge getEdge() {
        return resolvedPathElement.edge();
    }

    public Vertex getVertex() {
        return resolvedPathElement.vertex();
    }

    public boolean getExists() {
        return resolvedPathElement.getResponse().isPresent();
    }

    public Object getDocument()
    {
        if (getExists())
        {
            final String getResponseString = resolvedPathElement.getResponse().get().getSourceAsString();

            return JSONUtilities.toJSONObject(context, scope, getResponseString);
        }
        return null;
    }
}
