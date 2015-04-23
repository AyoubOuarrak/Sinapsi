package com.sinapsi.model.components;

import com.sinapsi.engine.ExecutionInterface;
import com.sinapsi.engine.system.SMSAdapter;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.Action;
import com.sinapsi.model.parameters.FormalParamBuilder;
import com.sinapsi.utils.HashMapBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * ActionSendSMS class. This Action will send a new sms.
 * This action must be parametrized to know at execution phase
 * the text message body and the recipient number
 * Notice that this action is completely platform-independent:
 * it relies on other facades/adapters like SystemFacade and
 * SMSAdapter.
 */
public class ActionSendSMS extends Action {


    public static final int ACTION_SEND_SMS_ID = 1;

    public static final String ACTION_SEND_SMS = "ACTION_SEND_SMS";

    @Override
    public void activate(ExecutionInterface di) {
        SMSAdapter sa = (SMSAdapter) di.getSystemFacade().getSystemService(SystemFacade.SERVICE_SMS);
        JSONObject pjo = getParamsObj(params);
        SMSAdapter.Sms sms = new SMSAdapter.Sms();
        try{
            sms.setAddress(pjo.getString("number"));
            sms.setMsg(pjo.getString("msg"));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        sa.sendSMSMessage(sms);//TODO: check returned boolean
    }

    @Override
    protected JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("number", FormalParamBuilder.Types.STRING, false)
                .put("msg", FormalParamBuilder.Types.STRING, false)
                .create();
    }

    @Override
    public int getId() {
        return ACTION_SEND_SMS_ID;
    }

    @Override
    public String getName() {
        return ACTION_SEND_SMS;
    }

    @Override
    public int getMinVersion() {
        return 1;
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(SystemFacade.REQUIREMENT_SMS_SEND, 1)
                .create();
    }
}