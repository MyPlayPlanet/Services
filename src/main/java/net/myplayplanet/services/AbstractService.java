package net.myplayplanet.services;

import lombok.Getter;

public abstract class AbstractService {

    @Getter
    private ServiceCluster cluster;

    public AbstractService(ServiceCluster cluster) {
        this.cluster = cluster;
    }

    public void init() {
    }

    public void disable() {

    }
}