package com.nabsys.net.socket.channel;

public interface IChannelResult {
	Channel getChannel();
    boolean isDone();
    boolean isSuccess();
    Throwable getCause();
    boolean setSuccess();
    boolean setFailure(Throwable cause);
    void registFinalAction(IChannelAction action);
    boolean removeFinalAction(IChannelAction action);
}
