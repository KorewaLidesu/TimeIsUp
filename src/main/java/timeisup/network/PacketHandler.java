package timeisup.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import timeisup.TimeIsUp;

public class PacketHandler {
	
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(TimeIsUp.MODID);
	
	public static void register() {
		INSTANCE.registerMessage(TimerPacket.TimerPacketHandler.class, TimerPacket.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(WardPacket.WardPacketHandler.class, WardPacket.class, 1, Side.CLIENT);
		INSTANCE.registerMessage(RecoverRatePacket.RecoverRatePacketHandler.class, RecoverRatePacket.class, 2, Side.CLIENT);
	}
	

}
