/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package org.elasticsearch.plugin.degraphmalizer.updater;

/**
 * User: rico
 * Date: 08/02/2013
 */
public interface StringSerialization<T> {
    String toValue();
    T fromValue(String value);
}
