package com.sinapsi.model.impl;

import com.sinapsi.model.ComunicationInfoInterface;

/**
 * Implementation of the comunication interface
 *
 */
public class ComunicationInfo implements ComunicationInfoInterface {

    private String additionalInfo;
    private String description;
    private boolean errorOccured;

    /**
     * Default ctor
     */
    public ComunicationInfo() {
        errorOccured = false;
        description = "";
        additionalInfo = "";
    }

    /**
     * Parametrized ctor
     *
     * @param desc error description
     * @param err true if error occured, false otherwise
     */
    public ComunicationInfo(String desc, boolean err) {
        description = desc;
        errorOccured = err;
    }

    /**
     * Checks whether an error has occured
     *
     * @return boolean
     */
    @Override
    public boolean isErrorOccured() {
        return errorOccured;
    }

    /**
     * Get the error description
     *
     * @return description
     */
    @Override
    public String getErrorDescription() {
        return description;
    }

    /**
     * Get the additional info
     * @return
     */
    @Override
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Set a new description for the current comunication error
     *
     * @param desc error description
     */
    @Override
    public void setErrorDescription(String desc) {
        description = desc;
    }

    /**
     * Set a new additional info
     * @param info additional info
     */
    @Override
    public void setAdditionalInfo(String info) {
        additionalInfo = info;
    }

    /**
     * Set error variable
     *
     * @param error true if there are errors in comunication, false otherwise
     */
    @Override
    public void errorOccured(boolean error) {
        errorOccured = error;
    }
}
