package net.myplayplanet.services;

import lombok.Getter;

/**
 * Class to provide services to remove many instances from the main classes, to decrease the size to improve the
 * overview
 */
public abstract class AbstractService {

    @Getter
    private static AbstractService instance;

    public AbstractService(){
        instance = this;
    }

    /**
     * Optional initialise method to init the {@link net.myplayplanet.services.ServiceCluster}
     */
    public abstract void init();

}