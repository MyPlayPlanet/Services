package net.myplayplanet.services;

import lombok.Getter;

public abstract class AbstractService {

    @Getter
    private static AbstractService instance;

    public AbstractService(){
        instance = this;
    }

    public void init() {
    }

    public void disable() {

    }

}