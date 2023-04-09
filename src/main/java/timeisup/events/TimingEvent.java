package timeisup.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import timeisup.Configs;
import timeisup.ItemRegistry;
import timeisup.TimeIsUp;
import timeisup.capabilities.Timer;
import timeisup.capabilities.TimerCapability;
import timeisup.network.PacketHandler;
import timeisup.network.RecoverRatePacket;
import timeisup.network.TimerPacket;
import timeisup.world.TimerWorldData;

@EventBusSubscriber(modid = TimeIsUp.MODID)
public class TimingEvent {
	
	@SubscribeEvent
	public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event)
	{  
		if(event.getObject() instanceof EntityPlayerMP) {	
			event.addCapability(new ResourceLocation(TimeIsUp.MODID, "timeisup_timer"), new TimerCapability());
		}
	}
	
	@SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event)
    {     
		TimerCapability original = event.getOriginal().getCapability(TimeIsUp.TIMER, null);
        if(original != null)
        {
           EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
           TimerCapability current = player.getCapability(TimeIsUp.TIMER, null);
           current.timers = original.timers;
           current.global = original.global;
           if(original.isBossKilled())
        	   current.bossKilled();
        }
    }
	
	@SubscribeEvent
	public static void SpawnSet(PlayerRespawnEvent event)
	{
		if(Configs.hardcoreTimer && !event.isEndConquered()) {
			event.player.setGameType(GameType.SPECTATOR);
            event.player.getEntityWorld().getGameRules().setOrCreateGameRule("spectatorsGenerateChunks", "false");
		}
	}
	
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) 
	{
		if(event.side == Side.SERVER && event.phase == Phase.END) {
			TimerCapability cap = event.player.getCapability(TimeIsUp.TIMER, null);
			if(cap != null && !event.player.isCreative() && !event.player.isSpectator())
				cap.Tick(event.player.getEntityWorld().provider.getDimension(), event.player);
		}
	}
	
	@SubscribeEvent
	public static void onLivingKilled(LivingDeathEvent event) {
		Entity source = event.getSource().getTrueSource();
		if(source instanceof EntityPlayerMP && source != event.getEntityLiving())
		{
			if(!Configs.isDisabled(source.world.provider.getDimension()) && ((EntityPlayerMP) source).getRNG().nextFloat() < Configs.exileChance) {
				((EntityPlayerMP) source).addPotionEffect(new PotionEffect(ItemRegistry.exileEffect, 60, Configs.exileStrength, true, true));
			}
			if(Configs.boss != null)
			{
				NBTTagCompound entityNbt = event.getEntityLiving().serializeNBT();
				for(String key : Configs.boss.getKeySet())
				{
					if(!entityNbt.hasKey(key) || !Configs.boss.getTag(key).equals(entityNbt.getTag(key)))
					{
						return; //Killed entity is not the boss
					}
				}
				TimerCapability cap = source.getCapability(TimeIsUp.TIMER, null);
				if(cap != null && !cap.isBossKilled())
				{
					cap.ward = 0;
					WorldServer world = ((EntityPlayerMP) source).getServerWorld();
					if(Configs.coop) {
						if(!TimerWorldData.get(world).isBossKilled()) {
							world.getMinecraftServer().getPlayerList().sendMessage(new TextComponentString(Configs.bossMessage), true);
							PacketHandler.INSTANCE.sendToAll(new TimerPacket(-1));
						}
					} else {
						source.sendMessage(new TextComponentString(Configs.bossMessage));
						PacketHandler.INSTANCE.sendTo(new TimerPacket(-1), ((EntityPlayerMP)source));
					}
					cap.bossKilled(world);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerJoin(EntityJoinWorldEvent event) 
	{
		if(event.getEntity() instanceof EntityPlayerMP)
		{
			TimerCapability cap = event.getEntity().getCapability(TimeIsUp.TIMER, null);
			if(cap != null)
			{
				Timer timer = cap.getOrCreate(event.getWorld());
				int duration = -1;
				if(timer != null)
				{
					duration = timer.getDuration();
					PacketHandler.INSTANCE.sendTo(new RecoverRatePacket(timer.getMaxDuration(), timer.getRecoverAmount(), timer.getRecoverCooldown()), ((EntityPlayerMP)event.getEntity()));
				}
				PacketHandler.INSTANCE.sendTo(new TimerPacket(duration), ((EntityPlayerMP)event.getEntity()));
			}
		}
	}
	
	@SubscribeEvent
	public static void onDamaged(LivingDamageEvent event) {
		PotionEffect frailtyInstance = event.getEntityLiving().getActivePotionEffect(ItemRegistry.frailtyEffect);
		if(frailtyInstance != null) {
			float damage = event.getAmount() * (1f + frailtyInstance.getAmplifier()*0.1f);
			
			if(damage >= event.getEntityLiving().getHealth() && event.getSource().isMagicDamage() && event.getEntityLiving().isPotionActive(MobEffects.POISON)) // avoid poison kill
				damage = event.getEntityLiving().getHealth() - 0.01f;
			
			if(damage > event.getAmount())
				event.setAmount(damage);
		}
	}
	
}
