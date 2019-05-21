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

    public void initMock() {
        System.out.println("Service " + this.getClass().getName() + " started in test mode!");
    }

    public void disable() {

    }
}