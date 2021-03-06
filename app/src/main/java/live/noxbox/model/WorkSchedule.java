package live.noxbox.model;

import java.io.Serializable;

public class WorkSchedule implements Serializable {

    private NoxboxTime startTime;
    private NoxboxTime endTime;

    public WorkSchedule(){
        this.startTime = NoxboxTime._25;
        this.endTime = NoxboxTime._27;
    }

    public WorkSchedule(NoxboxTime startTime, NoxboxTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public NoxboxTime getStartTime() {
        if(startTime == null){
            startTime = NoxboxTime._25;
        }
        return startTime;
    }

    public WorkSchedule setStartTime(NoxboxTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public NoxboxTime getEndTime() {
        if(endTime == null){
            endTime = NoxboxTime._27;
        }
        return endTime;
    }

    public WorkSchedule setEndTime(NoxboxTime endTime) {
        this.endTime = endTime;
        return this;
    }
}
