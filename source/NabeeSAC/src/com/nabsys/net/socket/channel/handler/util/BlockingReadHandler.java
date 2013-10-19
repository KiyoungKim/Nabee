package com.nabsys.net.socket.channel.handler.util;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.socket.channel.DataEvent;
import com.nabsys.net.socket.channel.handler.ChannelHandler;

public class BlockingReadHandler<E> extends ChannelHandler {

	private final BlockingQueue<DataEvent> replyQueue = new LinkedBlockingQueue<DataEvent>(1);

	public BlockingReadHandler() {
	}

	@Override
	public void messageReceived(DataEvent e){
		if(!replyQueue.offer(e))
		{
			fireExceptionStream(e.getChannel(), new NetException("BlockingReadHandler queue overflow size : " + replyQueue.size()), e.getChannelResult());
			return;
		}
		super.messageReceived(e);
	}

	@SuppressWarnings("unchecked")
	public E read(long timeout, TimeUnit unit) throws IOException,
			InterruptedException {
		E result = null;

		DataEvent e = replyQueue.poll(timeout, unit);

		if (e == null)
            throw new TimeoutException(0x000D);
		
		if (e instanceof DataEvent) {
			result = (E) e.getMessage();
		} else {
			throw new IllegalStateException();
		}

		return result;
	}
}
