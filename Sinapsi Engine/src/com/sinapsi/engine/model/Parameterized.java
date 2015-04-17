package com.sinapsi.engine.model;

/**
 * Parameterized interface
 * @author Ayoub
 *
 */
public interface Parameterized {
    public String getFormalParameters();
    public String getActualParameters();
    public void setActualParameters(String params);
}
