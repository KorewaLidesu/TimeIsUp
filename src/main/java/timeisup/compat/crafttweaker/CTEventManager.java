package timeisup.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventHandle;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.events.timeisup.EventManager")
@ZenRegister
public class CTEventManager {
	
	
	public static final CTEventManager INSTANCE = new CTEventManager();
	
	@ZenMethod
	public static CTEventManager getInstance() {
		return INSTANCE;
	}
	
	@ZenMethod
	public void clear() {
		ctdangerous.clear();
		ctemergency.clear();
		cttimeisup.clear();
		cttimertick.clear();
	}
	
	private final EventList<DangerousEvent> ctdangerous = new EventList<>();
	
	@ZenMethod
    public IEventHandle onDangerous(IEventHandler<DangerousEvent> ev) {
		return ctdangerous.add(ev);
	}
	
	public boolean hasDangerous() {
        return ctdangerous.hasHandlers();
    }
	
	public void publishDangerous(DangerousEvent event) {
		ctdangerous.publish(event);
    }
	
	private final EventList<EmergencyEvent> ctemergency = new EventList<>();
	
	@ZenMethod
	public IEventHandle onEmergency(IEventHandler<EmergencyEvent> ev) {
		return ctemergency.add(ev);
	}
	
	public boolean hasEmergency() {
        return ctemergency.hasHandlers();
    }
	
	public void publishEmergency(EmergencyEvent event) {
		ctemergency.publish(event);
    }
	
	private final EventList<TimeIsUpEvent> cttimeisup = new EventList<>();
	
	@ZenMethod
	public IEventHandle onTimeIsUp(IEventHandler<TimeIsUpEvent> ev) {
		return cttimeisup.add(ev);
	}
	
	public boolean hasTimeIsUp() {
        return cttimeisup.hasHandlers();
    }
	
	public void publishTimeIsUp(TimeIsUpEvent event) {
		cttimeisup.publish(event);
    }
	
	private final EventList<BaseEvent> cttimertick = new EventList<>();
	
	@ZenMethod
	public IEventHandle onTimerTick(IEventHandler<BaseEvent> ev) {
		return cttimertick.add(ev);
	}
	
	public boolean hasTimerTick() {
        return cttimertick.hasHandlers();
    }
	
	public void publishTimerTick(BaseEvent event) {
		cttimertick.publish(event);
    }

}
