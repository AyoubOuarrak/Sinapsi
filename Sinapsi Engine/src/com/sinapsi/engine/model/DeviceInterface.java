package com.sinapsi.engine.model;

/**
 * Device Interface
 * @author Ayoub
 *
 */
public interface DeviceInterface {
    public int getId();
    public int getVersion();
    public String getName();
    public String getModel();
    public String getType();
    public UserInterface getUser();
    
    public void setName(String name);
    public void setModel(String model);
    public void setType(String type);
    public void setUser(UserInterface user);
    public void setVersion(String version);
}
