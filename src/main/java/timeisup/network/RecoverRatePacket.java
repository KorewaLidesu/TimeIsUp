package timeisup.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import timeisup.events.TimingEventClient;

public class RecoverRatePacket implements IMessage {	
	private int recovered;
	private int cooldown;
	private int maxDuration;
	
	public RecoverRatePacket() {}
	
	public RecoverRatePacket(int maxDuration, int recovered, int cooldown) {
		this.maxDuration = maxDuration;
		this.recovered = recovered;
		this.cooldown = cooldown;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		maxDuration = buf.readInt();
		recovered = buf.readInt();
		cooldown = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(maxDuration);
		buf.writeInt(recovered);
		buf.writeInt(cooldown);
	}
	
	public static class RecoverRatePacketHandler implements IMessageHandler<RecoverRatePacket, IMessage> {

		@Override
		public IMessage onMessage(RecoverRatePacket message, MessageContext ctx) {
			TimingEventClient.maxDuration = message.maxDuration;
			TimingEventClient.cooldown = message.cooldown;
		    TimingEventClient.recovered = message.recovered;
			return null;
		}
		
	}
	
}
