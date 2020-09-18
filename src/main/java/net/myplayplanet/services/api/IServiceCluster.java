package net.myplayplanet.services.api;

public interface IServiceCluster {
    boolean runCommand(String[] args);

    void startup();

    void startup(Runnable runnable);

    void startup(String[] args);

    void startup(String[] args, Runnable runnable);

    void addServices(IService... IServices);

    void addServices(boolean initiate, IService... IServices);

    void shutdownCluster();

    <T extends IService> T get(Class<T> clazz);
}
