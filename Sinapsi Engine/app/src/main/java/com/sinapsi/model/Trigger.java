package com.sinapsi.model;

import com.sinapsi.engine.ActivationManager;
import com.sinapsi.engine.Event;
import com.sinapsi.engine.ExecutionInterface;
import com.sinapsi.model.parameters.StringMatchingModeChoices;
import com.sinapsi.model.parameters.FormalParamBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Trigger interface. This interface must be implemented
 * by every class implementing every different type of trigger.
 * For example, classes like TriggerWifi and TriggerSMS should
 * implement this interface.
 */
public abstract class Trigger implements Parameterized, DistributedComponent {

    protected DeviceInterface executionDevice;
    protected String params;
    protected MacroInterface macro;

    /**
     * Default ctor, needed by ComponentLoader to create an instance
     * with java reflection.
     * DO NOT DIRECTLY CALL THIS: THIS SHOULD BE CALLED ONLY BY
     * ComponentLoader. USE ComponentFactory TO CREATE A NEW INSTANCE
     * INSTEAD.
     */
    public Trigger() {
        executionDevice = null;
        params = null;
        macro = null;
    }


    /**
     * Initializes the new trigger instance.
     * @param executionDevice the device on which this action is going to be executed
     * @param parameters the JSON string containing the actual parameters
     * @param macro the macro that will be executed when this trigger activates
     */
    public void init(DeviceInterface executionDevice, String parameters, MacroInterface macro) {
        this.executionDevice = executionDevice;
        this.params = parameters;
        this.macro = macro;
    }

    /**
     * Method to be overridden in order to be called when a
     * system event regarding the type of trigger occurs in order to
     * make additional actions.
     *
     * @param e  Event object containing infos about the system event
     *           that activated this trigger
     * @param di Device object to give a way to access device
     *           and system main infos and services
     */
    public void onActivate(Event e, ExecutionInterface di) {
        //override this if you want to do something else on trigger activation
    }

    /**
     * Method to be called in order to activate this trigger.
     *
     * @param e  Event object containing infos about the system event
     *           that activated this trigger
     * @param di Device object to give a way to access device
     *           and system main infos and services
     */
    public void activate(Event e, ExecutionInterface di) {
        if (checkParameters(e, di)) {
            onActivate(e, di);
            macro.execute(di);
        }

    }


    public void register(ActivationManager am) {
        am.addToNotifyList(this);
    }

    public void unregister(ActivationManager am) {
        am.removeFromNotifyList(this);
    }

    @Override
    public DeviceInterface getExecutionDevice() {
        return executionDevice;
    }


    @Override
    public String getActualParameters() {
        return params;
    }

    @Override
    public void setActualParameters(String params) {
        this.params = params;
    }

    @Override
    public ComponentTypes getComponentType() {
        return ComponentTypes.TRIGGER;
    }

    /**
     * Method called by Activate in order to check at execution phase
     * if the current state of the system or of the event meets the
     * parameters given at trigger instantiation.
     *
     * @param e  Event object containing infos about the system event
     *           that activated this trigger
     * @param di Device object to give a way to access device
     *           and system main infos and services
     * @return true if each one of the actual parameters equals the
     * extracted parameter from system/event's state.
     */
    protected boolean checkParameters(Event e, ExecutionInterface di) {
        if (getActualParameters() == null) return true;

        JSONObject actualParameterObj;
        try {
            actualParameterObj = new JSONObject(getActualParameters());
        } catch (JSONException e1) {
            e1.printStackTrace();
            return false;
            //we don't rethrow just to not make the system crash for one trigger.
        }

        JSONObject pjo;
        try {
            pjo = actualParameterObj.getJSONObject("parameters");
        } catch (JSONException e1) {
            /*
            there is no parameters array, and, because all trigger parameters
            are optional, and are meant to filter the events, this means 'just
            activate the macro every time (i.e. either when wifi sets on and off)
            */
            e1.printStackTrace();
            macro.execute(di);
            return true;
        }

        JSONObject valuesObj = null;
        try {
            valuesObj = extractParameterValues(e, di);
        } catch (JSONException e1) {
            e1.printStackTrace();
            //This means that the extending class is bad implemented
        }
        if (valuesObj == null) return true;

        Iterator<String> pars = pjo.keys();

        while (pars.hasNext()) {
            String key = pars.next();
            Object parvalue = pjo.opt(key);
            try {
                if (parvalue == null) {
                    continue;
                } else if (parvalue instanceof String) {
                    if (!((String) parvalue).equals(valuesObj.getString(key)))
                        return false;
                } else if (parvalue instanceof Boolean) {
                    if (!((Boolean) parvalue).equals(valuesObj.getBoolean(key)))
                        return false;
                } else if (parvalue instanceof Integer) {
                    if (!((Integer) parvalue).equals(valuesObj.getInt(key)))
                        return false;
                } else if (parvalue instanceof JSONObject) {
                    String advType = ((JSONObject)parvalue).getString("advancedType");
                    if(advType == null) {
                        return false;
                    } else if(advType.equals(FormalParamBuilder.Types.STRING_ADVANCED.toString())){
                        String val = ((JSONObject)parvalue).getString("value");
                        String mode = ((JSONObject)parvalue).getString("matchingMode");
                        StringMatchingModeChoices matchingMode = StringMatchingModeChoices.valueOf(mode);
                        switch (matchingMode){
                            case WHOLE_STRING:
                                if(!val.equals(valuesObj.getString(key)))
                                    return false;
                                break;
                            case CONTAINS:
                                if(!valuesObj.getString(key).contains(val))
                                    return false;
                                break;
                            case REGEX:
                                if(!valuesObj.getString(key).matches(val))
                                    return false;
                                break;
                        }
                    } else {
                        throw new UnsupportedOperationException("Check still not available for this advanced parameter type");
                    }
                } else {
                    throw new UnsupportedOperationException("Check still not available for this parameter type");
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
                return false;
            }
        }

        //TODO: test, check, and probably remake this with a JSONReader (if it will be available on all clients and servers)

        return true;
    }


    @Override
    public String getFormalParameters() {
        JSONObject result = null;
        try {
            result = getFormalParametersJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result != null ? result.toString() : null;
    }

    /**
     * Method the extending class has to implement in order to get
     * a JSON object containing all the necessary infos about the
     * required parameters of the trigger.
     *
     * @return the JSONObject containing the formal parameters
     */
    protected abstract JSONObject getFormalParametersJSON() throws JSONException;

    /**
     * Method the extending class has to implement in order to get
     * a JSON object containing all the necessary values for a check
     * of the given parameters at execution phase. These values are
     * usually extracted from the event that activated the trigger
     * or from the device/system on which the trigger is running.
     *
     * @param e  Event object containing infos about the system event
     *           that activated this trigger
     * @param di Device object to give a way to access device
     *           and system main infos and services
     * @return just return null if no values are supposed to be set,
     * or a JSONObject containing the parameters.
     */
    protected abstract JSONObject extractParameterValues(Event e, ExecutionInterface di) throws JSONException;
}
