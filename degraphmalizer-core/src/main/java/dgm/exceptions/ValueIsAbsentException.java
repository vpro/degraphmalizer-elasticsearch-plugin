/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package dgm.exceptions;

/**
 * User: rico
 * Date: 13/05/2013
 */
public class ValueIsAbsentException extends DegraphmalizerException
{
    public ValueIsAbsentException() {
        super("Value was not present");
    }
}
