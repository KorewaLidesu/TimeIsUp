package timeisup.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import timeisup.Configs;
import timeisup.TimeIsUp;
import timeisup.capabilities.Timer;
import timeisup.capabilities.TimerCapability;
import timeisup.network.PacketHandler;
import timeisup.network.TimerPacket;

public class CommandTimer extends CommandBase {

    public String getName()
    {
        return "timer";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getUsage(ICommandSender sender)
    {
        return "commands.timer.usage";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
    	if (args.length < 3)
        {
            throw new WrongUsageException("commands.timer.usage", new Object[0]);
        }
    	
    	List<EntityPlayerMP> players = CommandBase.getPlayers(server, sender, args[0]);
    	int time = CommandBase.parseInt(args[2]);
    	
    	if("set".equals(args[1])) {
    		if(args.length >= 4)
    			this.setTimer(sender, time, players, server.getWorld(CommandBase.parseInt(args[3])));
    		else
    			this.setTimer(sender, time, players);
    	} else if("add".equals(args[1])) {
    		if(args.length >= 4)
    			this.addTimer(sender, time, players, server.getWorld(CommandBase.parseInt(args[3])));
    		else
    			this.addTimer(sender, time, players);
    	}
    	
    }
    
    public void setTimer(ICommandSender sender, int time, List<EntityPlayerMP> players) {
		for(EntityPlayerMP player : players) {
			setDuration(sender, time, player, player.getServerWorld(), true);
		}
		notifyCommandListener(sender, this, "commands.timeisup.settimer", time);
	}
	
	public void setTimer(ICommandSender sender, int time, List<EntityPlayerMP> players, WorldServer world) throws CommandException {
		if(world == null || Configs.isDisabled(world.provider.getDimension())) {
			throw new CommandException("commands.timeisup.timerblacklist", world.provider.getDimension());
		}
		
		for(EntityPlayerMP player : players) {
			setDuration(sender, time, player, world, world == player.getServerWorld());
		}
		notifyCommandListener(sender, this, "commands.timeisup.settimerdim", world.provider.getDimension(), time);
	}
		
	/*current dimension*/
	public void addTimer(ICommandSender sender, int time, List<EntityPlayerMP> players) {
		for(EntityPlayerMP player : players) {
			addDuration(sender, time, player, player.getServerWorld(), true);
		}
		notifyCommandListener(sender, this, "commands.timeisup.addtimer", time);
	}
	
	public void addTimer(ICommandSender sender, int time, List<EntityPlayerMP> players, WorldServer world) throws CommandException {
		if(world == null || Configs.isDisabled(world.provider.getDimension())) {
			throw new CommandException("commands.timeisup.timerblacklist", world.provider.getDimension());
		}
		
		for(EntityPlayerMP player : players) {
			addDuration(sender, time, player, world, world == player.getServerWorld());
		}
		notifyCommandListener(sender, this, "commands.timeisup.addtimerdim", time, world.provider.getDimension());
	}
	
	
	
	public static void setDuration(ICommandSender sender, int time, EntityPlayerMP player, WorldServer world, boolean send) {
		TimerCapability timer = player.getCapability(TimeIsUp.TIMER, null);
		if(timer != null) {
			Timer dimTimer =  timer.getOrCreate(world);
			if(dimTimer != null) {
				dimTimer.setDuration(time);
				if(send)
					PacketHandler.INSTANCE.sendTo(new TimerPacket(time), player);
			}
		}
	}
	
	public static void addDuration(ICommandSender sender, int time, EntityPlayerMP player, WorldServer world, boolean send) {	
			TimerCapability timer = player.getCapability(TimeIsUp.TIMER, null);
			if(timer != null) {
				Timer dimTimer = timer.getOrCreate(world);
				if(dimTimer != null) {
					if(-time > dimTimer.getDuration())
						time = -dimTimer.getDuration();
					dimTimer.addDuration(time);
					if(send)
						PacketHandler.INSTANCE.sendTo(new TimerPacket(dimTimer.getDuration()), player);
				}
			}
	}

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        else if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, new String[] {"set", "add"});
        }
        else
        {
            return args.length >= 4 ? getListOfStringsMatchingLastWord(args, Arrays.stream(DimensionManager.getStaticDimensionIDs()).sorted().map(String::valueOf).toArray(String[]::new)) : Collections.emptyList();
        }
        
    }
	
}
