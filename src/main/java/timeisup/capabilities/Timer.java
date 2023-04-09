package timeisup.capabilities;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import timeisup.Configs;
import timeisup.ItemRegistry;
import timeisup.events.custom.TimeIsUpTickEvent;
import timeisup.network.PacketHandler;
import timeisup.network.TimerPacket;

public class Timer {

	public static final DamageSource TIME_IS_UP = (new DamageSource("timeIsUp")).setDamageBypassesArmor().setDamageAllowedInCreativeMode();
	
	private int tickDuration;
	private int tickRecover;
	private int tickPerDuration;
	private int maxDuration;
	private int amountPerRecover;
	private int potionTimer;
	
	public Timer(int baseTickDuration, int tickPerDuration, int maxDuration, int amountPerRecover) {
		this(baseTickDuration, tickPerDuration, maxDuration, 0, amountPerRecover);
	}
	
	public Timer(int baseTickDuration, int tickPerDuration, int maxDuration, int tickRecover, int amountPerRecover) {
		this.tickDuration = baseTickDuration;
		this.tickPerDuration = tickPerDuration;
		this.tickRecover = tickRecover;
		this.maxDuration = maxDuration;
		this.amountPerRecover = amountPerRecover;
		this.potionTimer = 1;
	}
	
	public void recover() {
		if(this.tickDuration >= this.maxDuration)
			return;
		this.tickRecover++;
		if(this.tickRecover >= this.tickPerDuration) {
			addDuration(this.amountPerRecover);
			this.tickRecover = 0;			
		}		
	}
	
	public int getMaxDuration() {
		return maxDuration;
	}
	
	public int getRecoverAmount() {
		return amountPerRecover;
	}
	
	public int getRecoverCooldown() {
		return tickPerDuration;
	}
	
	public void sync(EntityLivingBase entity) {
		PacketHandler.INSTANCE.sendTo(new TimerPacket(tickDuration), ((EntityPlayerMP)entity));
	}
	
	/**
	 * Decreases the given entity's timer and apply timer effects
	 * @param entity
	 */
	public void decrease(EntityLivingBase entity) {
		if(entity.isEntityAlive()) {
			if(Configs.potionEffect)
				--potionTimer; //Potion effects are applied when potionTimer reaches 0
			if(tickDuration > 0) {
				
				if(tickDuration < Configs.emergency)
				{
					if(!MinecraftForge.EVENT_BUS.post(new TimeIsUpTickEvent.EmergencyEvent(entity.world, (EntityPlayerMP) entity, tickDuration)))
					if(potionTimer <= 0) {
						potionTimer = Configs.cooldownEffects;
						addEffects(entity, Configs.emergencyEffects);
					}
				}
				else if(tickDuration < Configs.dangerous)
				{
					if(!MinecraftForge.EVENT_BUS.post(new TimeIsUpTickEvent.DangerousEvent(entity.world, (EntityPlayerMP) entity, tickDuration)))
					if(potionTimer <= 0) {
						potionTimer = Configs.cooldownEffects;
						addEffects(entity, Configs.dangerousEffects);
					}
				}
				else MinecraftForge.EVENT_BUS.post(new TimeIsUpTickEvent(entity.world, (EntityPlayerMP) entity, tickDuration));
				
				PotionEffect exileInstance = entity.getActivePotionEffect(ItemRegistry.exileEffect);
				if(exileInstance != null) {
					this.tickDuration -= exileInstance.getAmplifier()+1;
					if(this.tickDuration < 1)
						this.tickDuration = 1;
				}
				this.tickDuration--;
				
				//Re sync timer with client once every 30 seconds
				if(tickDuration % 600 == 0 && entity instanceof EntityPlayerMP)
					sync(entity);
			}
			//When timer reaches 0.
			else {
				if(MinecraftForge.EVENT_BUS.post(new TimeIsUpTickEvent.TimeIsUpEvent(entity.world, (EntityPlayerMP) entity, tickDuration))) return;
				//Set world to hardcore if hardcoreTimer is true.
				if(Configs.hardcoreTimer && !entity.getEntityWorld().getWorldInfo().isHardcoreModeEnabled()) entity.getEntityWorld().getWorldInfo().setHardcore(true);
				if(potionTimer <= 0) {
					potionTimer = Configs.cooldownEffects;
					addEffects(entity, Configs.TimeIsUpEffects);
				}
				else if(!Configs.potionEffect)
					entity.attackEntityFrom(Timer.TIME_IS_UP, Float.MAX_VALUE);
			}
		}
	}
	
	public int getDuration() {
		return this.tickDuration;
	}
	
	public void addDuration(int duration) {
		this.tickDuration += duration;
	}
	
	public void setDuration(int duration) {
		this.tickDuration = duration;
	}
	
	public void writeNBT(String key, NBTTagCompound nbt) {
		nbt.setIntArray(key, new int[] {tickDuration, tickPerDuration, maxDuration, tickRecover, amountPerRecover});
	}
	
	public static Timer fromData(int worldkey, int... data) {
		Timer timer = Configs.basetimers.getOrDefault(worldkey, Configs.defaultTimer.copy()).copy();
		timer.tickRecover = data[3];
		timer.tickDuration = data[0];
		return timer;
	}
	
	public Timer copy() {
		return new Timer(this.tickDuration, this.tickPerDuration, this.maxDuration, this.amountPerRecover);
	}
	
	private static PotionEffect copyEffect(PotionEffect effect) {
		return new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles());
	}
	
	private static void addEffects(EntityLivingBase entity, List<PotionEffect> instances) {
		for(PotionEffect effect : instances)
		{
			entity.addPotionEffect(copyEffect(effect));
		}
	}
	
}
