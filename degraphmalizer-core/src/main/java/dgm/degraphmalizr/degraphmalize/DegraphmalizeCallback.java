/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package dgm.degraphmalizr.degraphmalize;

import dgm.exceptions.DegraphmalizerException;

/**
 * @author rico
 */
public interface DegraphmalizeCallback {
    void started(DegraphmalizeRequest request);

    void complete(DegraphmalizeResult result);

    void failed(DegraphmalizerException exception);
}
