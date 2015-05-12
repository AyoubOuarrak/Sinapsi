package com.sinapsi.engine;

import com.sinapsi.engine.components.ActionContinueConfirmDialog;
import com.sinapsi.engine.components.ActionLog;
import com.sinapsi.engine.components.ActionLuaScript;
import com.sinapsi.engine.components.ActionSendSMS;
import com.sinapsi.engine.components.ActionSetVariable;
import com.sinapsi.engine.components.ActionSimpleNotification;
import com.sinapsi.engine.components.ActionStringInputDialog;
import com.sinapsi.engine.components.TriggerACPower;
import com.sinapsi.engine.components.TriggerEngineStart;
import com.sinapsi.engine.components.TriggerScreenPower;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.engine.execution.WebExecutionInterface;
import com.sinapsi.engine.log.SinapsiLog;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.engine.components.ActionWifiState;
import com.sinapsi.engine.components.TriggerSMS;
import com.sinapsi.engine.components.TriggerWifi;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.Macro;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Macro engine class. Used to initialize the whole macro execution system
 * and to keep a list of all defined macros.
 */
public class MacroEngine {

    private DeviceInterface device;
    private ActivationManager activator;
    private ComponentFactory factory;
    private SinapsiLog log;

    private HashMap<String,MacroInterface> macros = new HashMap<>();

    /**
     * Creates a new MacroEngine instance with a default component
     * class set.
     * @param currentDevice the device on which this engine is running
     * @param activationManager the activation manager for trigger activation
     * @param sinapsiLog the sinapsi log
     */
    public MacroEngine(DeviceInterface currentDevice,
                       ActivationManager activationManager,
                       SinapsiLog sinapsiLog
                       ){
        device = currentDevice;
        activator = activationManager;
        log = sinapsiLog;


        factory = new ComponentFactory(device, log,
                TriggerSMS.class,
                TriggerWifi.class,
                TriggerEngineStart.class,
                TriggerScreenPower.class,
                TriggerACPower.class,

                ActionWifiState.class,
                ActionSendSMS.class,
                ActionLuaScript.class,
                ActionSetVariable.class,
                ActionContinueConfirmDialog.class,
                ActionLog.class,
                ActionSimpleNotification.class,
                ActionStringInputDialog.class

        );

        sinapsiLog.log("MACROENGINE", "Engine initialized.");
    }

    /**
     * Creates a new MacroEngine instance with a custom component
     * class set.
     * @param currentDevice the devices on which this engine is running
     * @param activationManager the activation manager for trigger activation
     * @param sinapsiLog the sinapsi log
     * @param componentClasses the set of component classes
     */
    public MacroEngine(DeviceInterface currentDevice,
                       ActivationManager activationManager,
                       SinapsiLog sinapsiLog,
                       Class<? extends MacroComponent>[] componentClasses){
        device = currentDevice;
        activator = activationManager;
        log = sinapsiLog;

        factory = new ComponentFactory(device, log, componentClasses);

        sinapsiLog.log("MACROENGINE", "Engine initialized.");
    }

    /**
     * Getter of the ComponentFactory instance generated by this
     * MacroEngine
     * @return the component factory
     */
    public ComponentFactory getComponentFactory(){
        return factory;
    }

    /**
     * Adds a new macro to the list of defined macros. When added,
     * the macro's trigger is registered on the ActivationManager
     * and starts listening for system events.
     * @param m the macro
     */
    public void addMacro(MacroInterface m){
        m.getTrigger().register(activator);
        macros.put(m.getName(), m);
        log.log("MACROENGINE", "Added macro " + m.getId() + ":'" + m.getName()+"' to the engine");
    }

    /**
     * Adds all the macros in the collection to the list of defined
     * macros, registering every trigger on the ActivationManager.
     * @param lm the collection of macros
     */
    public void addMacros(Collection<MacroInterface> lm){
        for(MacroInterface m: lm){
            addMacro(m);
        }
        log.log("MACROENGINE", "Added " + lm.size() + " macros to the engine");
    }

    public void startEngine(){
        log.log("MACROENGINE", "Engine started.");
        activator.setEnabled(true);
        activator.activateForOnEngineStart();
    }

    public void continueMacro(RemoteExecutionDescriptor red){
        //TODO: impl
    }

    public void pauseEngine(){
        activator.setEnabled(false);
    }

    public void resumeEngine(){
        activator.setEnabled(true);
    }
}
