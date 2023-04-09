package timeisup.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import timeisup.events.TimingEventClient;

public class TimerPacket implements IMessage {

	private int duration;
	
	public TimerPacket() {}
	
	public TimerPacket(int currentDuration) {
		this.duration = currentDuration;
	}
		
	@Override
	public void fromBytes(ByteBuf buf) {
		duration = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(duration);
	}
	
	
	public static class TimerPacketHandler implements IMessageHandler<TimerPacket, IMessage> {

		@Override
		public IMessage onMessage(TimerPacket message, MessageContext ctx) {
			TimingEventClient.ticksDuration = message.duration;
			if(TimingEventClient.ticksDuration < 0)
		    	TimingEventClient.warded = false;
			return null;
		}
		
	}
	
}
