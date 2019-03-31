package net.myplayplanet.services.schedule;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ScheduledTaskProvider {

    @Getter
    private static ScheduledTaskProvider instance;
    private List<IScheduledTask> updatebleObjects;

    public ScheduledTaskProvider(){
        instance = this;
        updatebleObjects = new ArrayList<>();
    }

    public <T extends IScheduledTask> void register(T t){
        this.updatebleObjects.add(t);
    }

}
