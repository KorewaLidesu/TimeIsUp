package timeisup.capabilities;

import java.util.HashMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import timeisup.Configs;
import timeisup.TimeIsUp;
import timeisup.network.PacketHandler;
import timeisup.network.WardPacket;
import timeisup.world.TimerWorldData;

public class TimerCapability implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

	public HashMap<Integer, Timer> timers = new HashMap<>();
	public int ward = 0;
	private boolean boss_killed = false;
	public Timer global = null;
	
	public void Tick(int currentDimensionKey, EntityLivingBase entity) {
		if(this.isBossKilled(entity.getEntityWorld()))
			return;
		
		if(!Configs.globalTimer)
		timers.forEach((k,timer) -> {
			boolean isCurrent = k == currentDimensionKey;
			if(isCurrent && ward <= 0)timer.decrease(entity);
			else timer.recover();
				
		});
		else
		{
			if(Configs.isDisabled(currentDimensionKey) || ward > 0) global.recover();
			else global.decrease(entity);
		}
		
		if(ward > 0)
		{
			ward--;
			if(ward == 0) {
				PacketHandler.INSTANCE.sendTo(new WardPacket(false), ((EntityPlayerMP)entity));
			}
		}
	}
	
	public void bossKilled() {
		this.boss_killed = true;
	}
	
	public void bossKilled(World world) {
		this.bossKilled();
		if(Configs.coop) {
			TimerWorldData.get(world).setBossKilled();
		}
	}
	
	public boolean isBossKilled() {
		return this.boss_killed;
	}
	
	public boolean isBossKilled(World world) {
		return this.isBossKilled() || (Configs.coop && TimerWorldData.get(world).isBossKilled());
	}
	
	public Timer getOrCreate(World world) {
		
		if(Configs.globalTimer) {
			if(global == null)
				global = Configs.defaultTimer.copy();
		}
		int currentDimensionKey = world.provider.getDimension();
		if(Configs.isDisabled(currentDimensionKey) || this.isBossKilled(world))
			return null;
		
		if(Configs.globalTimer)
			return global;
		
		if(!timers.containsKey(currentDimensionKey))
		{
			Timer timer = Configs.basetimers.getOrDefault(currentDimensionKey, Configs.defaultTimer.copy());
			timers.put(currentDimensionKey, timer);
			return timer;
		}
		return timers.get(currentDimensionKey);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return this.hasCapability(capability, facing) ? (T)this : null;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == TimeIsUp.TIMER;
	}
	
	public static void register()
	{
		CapabilityManager.INSTANCE.register(TimerCapability.class, new TimerStorage(), TimerCapability::new);
	}
	
	public static class TimerStorage implements IStorage<TimerCapability>
	{

		@Override
		public NBTBase writeNBT(Capability<TimerCapability> capability, TimerCapability instance,
				EnumFacing side) {	
			NBTTagCompound nbt = new NBTTagCompound();
			if(!instance.boss_killed) //No need to keep it after boss killed
			{
				NBTTagCompound worlds = new NBTTagCompound();
				instance.timers.forEach((k, timer) -> {
					if(!Configs.isDisabled(k))
						timer.writeNBT(k.toString(), worlds);
				});
				if(instance.global != null)
					instance.global.writeNBT("global_timer", nbt);
				nbt.setTag("worlds", worlds);
			}
			nbt.setBoolean("boss", instance.boss_killed);
			return nbt;
		}

		@Override
		public void readNBT(Capability<TimerCapability> capability, TimerCapability instance,
				EnumFacing side, NBTBase nbt) {	
			NBTTagCompound compound = (NBTTagCompound)nbt;
			instance.boss_killed = compound.getBoolean("boss");
			if(compound.hasKey("worlds") && !instance.boss_killed) //No need to read if boss killed
			compound.getCompoundTag("worlds").getKeySet().forEach((key) -> {
				int[] data = compound.getCompoundTag("worlds").getIntArray(key);
				int registrykey = Integer.parseInt(key);
				instance.timers.put(registrykey, Timer.fromData(registrykey, data));
			});
			
			if(compound.hasKey("global_timer"))
				instance.global = Timer.fromData(0, compound.getIntArray("global_timer"));
		}
		
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) TimeIsUp.TIMER.getStorage().writeNBT(TimeIsUp.TIMER, this, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		TimeIsUp.TIMER.getStorage().readNBT(TimeIsUp.TIMER, this, null, nbt);
	}

}
