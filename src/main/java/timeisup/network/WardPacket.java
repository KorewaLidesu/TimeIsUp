package timeisup.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import timeisup.events.TimingEventClient;

public class WardPacket implements IMessage {
	private boolean warded;
	
	public WardPacket() {}
	
	public WardPacket(boolean warded) {
		this.warded = warded;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		warded = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(warded);
	}
	
	public static class WardPacketHandler implements IMessageHandler<WardPacket, IMessage> {

		@Override
		public IMessage onMessage(WardPacket message, MessageContext ctx) {
			TimingEventClient.warded = message.warded;
		    TimingEventClient.ward = 70;
			return null;
		}
		
	}
	
}
