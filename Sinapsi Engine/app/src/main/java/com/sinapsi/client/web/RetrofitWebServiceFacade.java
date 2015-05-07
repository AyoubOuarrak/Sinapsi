package com.sinapsi.client.web;

import com.bgp.generator.KeyGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sinapsi.android.AppConsts;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.User;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * WebService draft class.
 * This is now a class containing some example functions
 * to understand how RetroFit initializes a RetrofitInterface
 * variable.
 */
public class RetrofitWebServiceFacade implements SinapsiWebServiceFacade, BGPKeysProvider {

    private PublicKey publicKey; //created by login
    private PrivateKey privateKey;
    private KeyPair keyPair;

    private RetrofitInterface cryptedRetrofit;
    private RetrofitInterface uncryptedRetrofit;

    public RetrofitWebServiceFacade() {

        Gson gson = new GsonBuilder().create();

        RestAdapter cryptedRestAdapter = new RestAdapter.Builder()
                .setEndpoint(AppConsts.SINAPSI_URL)
                .setConverter(new BGPGsonConverter(gson, this))
                .setLog(new AndroidLog("RETROFIT"))
                .build();

        RestAdapter uncryptedRestAdapter = new RestAdapter.Builder()
                .setEndpoint(AppConsts.SINAPSI_URL)
                .setConverter(new GsonConverter(gson))
                .setLog(new AndroidLog("RETROFIT"))
                .build();

        if(AppConsts.DEBUG) {
            cryptedRestAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
            uncryptedRestAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        else {
            cryptedRestAdapter.setLogLevel(RestAdapter.LogLevel.NONE);
            uncryptedRestAdapter.setLogLevel(RestAdapter.LogLevel.NONE);
        }

        cryptedRetrofit = cryptedRestAdapter.create(RetrofitInterface.class);

        uncryptedRetrofit = uncryptedRestAdapter.create(RetrofitInterface.class);
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public KeyPair getKeyPair() {
        return keyPair;
    }


    /**
     * Converts a generic WebServiceCallback to a retrofit's Callback
     * @param wsCallback the WebServiceCallback
     * @param <T> the type
     * @return a retrofit's Callback
     */
    private static <T> Callback<T> convertCallback(final WebServiceCallback<T> wsCallback){
        return new Callback<T>(){
            @Override
            public void success(T t, Response response) {
                wsCallback.success(t,response);
            }
            @Override
            public void failure(RetrofitError error) {
                wsCallback.failure(error);
            }
        };
    }

    /**
     * Check to ensure the keys are not null. Throws a runtime exception
     */
    private void checkKeys(){
        if(publicKey == null || privateKey == null || keyPair == null)
            throw new RuntimeException(
                    "Missing public key. Did you log in?");
    }


    @Override
    public void login(String email, String password, final WebServiceCallback<User> result) {
        KeyGenerator kg = new KeyGenerator();
        final PrivateKey prk = kg.getPrivateKey();
        final PublicKey puk = kg.getPublicKey();
        final KeyPair kp = kg.getKeyPair();
        uncryptedRetrofit.login(email, puk, password, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                RetrofitWebServiceFacade.this.publicKey = puk;
                RetrofitWebServiceFacade.this.privateKey = prk;
                RetrofitWebServiceFacade.this.keyPair = kp;
                result.success(user,response);
            }

            @Override
            public void failure(RetrofitError error) {
                result.failure(error);
            }
        });
    }

    @Override
    public void register(String email, String password, WebServiceCallback<User> result) {
        uncryptedRetrofit.register(email, password, convertCallback(result));
    }

    @Override
    public void getAllDevicesByUser(UserInterface user, WebServiceCallback<List<DeviceInterface>> result) {
        checkKeys();
        cryptedRetrofit.getAllDevicesByUser(user.getEmail(), publicKey, convertCallback(result));
    }

    @Override
    public void registerDevice(UserInterface user,
                               String deviceName,
                               String deviceModel,
                               String deviceType,
                               int deviceClientVersion,
                               WebServiceCallback<DeviceInterface> result) {
        checkKeys();
        cryptedRetrofit.registerDevice(
                deviceName,
                deviceModel,
                deviceType,
                deviceClientVersion,
                user.getId(),
                publicKey,
                convertCallback(result));
    }

    @Override
    public void getAvailableActions(DeviceInterface device, WebServiceCallback<List<MacroComponent>> result) {
        checkKeys();
        cryptedRetrofit.getAvailableActions(
                device.getId(),
                publicKey,
                convertCallback(result));
    }

    @Override
    public void setAvailableActions(DeviceInterface device, List<MacroComponent> actions, WebServiceCallback<String> result) {
        checkKeys();
        cryptedRetrofit.setAvailableActions(
                device.getId(),
                publicKey,
                actions,
                convertCallback(result));
    }

    @Override
    public void getAvailableTriggers(DeviceInterface device, WebServiceCallback<List<MacroComponent>> result) {
        checkKeys();
        cryptedRetrofit.getAvailableTriggers(
                device.getId(),
                publicKey,
                convertCallback(result));
    }

    @Override
    public void setAvailableTriggers(DeviceInterface device, List<MacroComponent> triggers, WebServiceCallback<String> result) {
        checkKeys();
        cryptedRetrofit.setAvailableTriggers(
                device.getId(),
                publicKey,
                triggers,
                convertCallback(result));
    }

    @Override
    public void continueMacroOnDevice(DeviceInterface device, RemoteExecutionDescriptor red, WebServiceCallback<String> result) {

    }


}