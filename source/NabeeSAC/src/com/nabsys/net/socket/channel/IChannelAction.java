package com.nabsys.net.socket.channel;

public interface IChannelAction {
	
	IChannelAction CLOSE = new IChannelAction() {
        public void completedEvent(IChannelResult result) {
        	result.getChannel().close();
        }
    };
    
    IChannelAction CLOSE_ON_FAILURE = new IChannelAction() {
        public void completedEvent(IChannelResult result) {
        	if(!result.isSuccess())
        	{
        		result.getChannel().close();
        	}
        }
    };
    
	public void completedEvent(IChannelResult result);
}
