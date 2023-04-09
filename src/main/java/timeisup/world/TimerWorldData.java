package timeisup.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import timeisup.TimeIsUp;

public class TimerWorldData extends WorldSavedData {

	private static final String DATA_NAME = TimeIsUp.MODID + "_WorldData";
	
	private boolean boss_killed = false;
	
	public TimerWorldData() {
		super(DATA_NAME);
	}
	
	public TimerWorldData(String name) {
		super(name);
	}
	
	public boolean isBossKilled() {
		return this.boss_killed;
	}
	
	public void setBossKilled() {
		this.boss_killed = true;
		this.markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.boss_killed = nbt.getBoolean("boss");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("boss", this.boss_killed);
		return nbt;
	}

	
	public static TimerWorldData get(World world) {

		  MapStorage storage = world.getMapStorage();
		  TimerWorldData instance = (TimerWorldData) storage.getOrLoadData(TimerWorldData.class, DATA_NAME);

		  if (instance == null) {
		    instance = new TimerWorldData();
		    storage.setData(DATA_NAME, instance);
		  }
		  return instance;
	}
}
