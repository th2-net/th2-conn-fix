package com.exactpro.th2.fix.client.impl;

@FunctionalInterface
public interface Destructor {

     void close() throws Exception;
}
