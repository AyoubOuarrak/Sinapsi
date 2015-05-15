package com.sinapsi.webservice.engine;

import com.sinapsi.engine.ActivationManager;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.execution.ExecutionInterface;

public class WebServiceActivationManager extends ActivationManager {

    public WebServiceActivationManager(ExecutionInterface defaultExecutionInterface) {
        super(defaultExecutionInterface);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void addToNotifyList(Trigger t) {
        super.addToNotifyList(t);
        //il Trigger t da questo momento deve essere attivato quando si verifica
        //  l'evento specifico
    }

    @Override
    public void removeFromNotifyList(Trigger t) {
        super.removeFromNotifyList(t);
        //il Trigger t da questo momento non deve essere più attivato
    }

}
