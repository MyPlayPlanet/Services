package net.myplayplanet.services;

import net.myplayplanet.services.internal.CommandExecutor;

public interface IService {

    default String getName() {
        return this.getClass().getName();
    }

    default void init() {
    }

    default void disable() {
    }

    default void registerCommand(CommandExecutor executor) {

    }
}