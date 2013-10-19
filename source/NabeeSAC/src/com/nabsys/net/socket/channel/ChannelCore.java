package com.nabsys.net.socket.channel;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.common.util.AccumByteBuffer;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;

public class ChannelCore {
	private SocketChannel 					interfaceChannel 	= null;
	private Selector 						readSelector 		= null;
	private ExecutorService					messageListener		= null;
	private ExecutorService					server				= null;
	private final Object 					trafficLock 		= new Object();
	public ChannelCore()
	{
	}
	
	public ChannelCore(ExecutorService server)
	{
		this.server = server;
	}
	
	public ChannelCore(SocketChannel interfaceChannel)
	{
		this.interfaceChannel = interfaceChannel;
	}
	
	public void work(IEvent event)
	{
		if(event instanceof IChannelEvent)
		{
			IChannelEvent channelEvent = (IChannelEvent) event;
			try{
				switch(channelEvent.getState())
				{
					case CONNECT :
						connect(channelEvent);
						break;
					case BIND :
						bind(channelEvent);
						break;
					case UNBIND :
						close(channelEvent);
						break;
					case DISCONNECT :
						close(channelEvent);
						break;
					case CLOSE :
						close(channelEvent);
						break;
					default :
				}
			}catch(Exception e){
				fireExceptionStream(channelEvent.getChannel(), e, event.getChannelResult());
			}
		}
		else if(event instanceof DataEvent)
		{
			DataEvent dataEvent = (DataEvent) event;
			try {
				write(dataEvent);
				writeComplete(dataEvent.getChannel(), dataEvent, dataEvent.getChannelResult());
			} catch (SocketClosedException e) {
				fireExceptionStream(dataEvent.getChannel(), e, event.getChannelResult());
			} catch (TimeoutException e) {
				fireExceptionStream(dataEvent.getChannel(), e, event.getChannelResult());
			} catch (NetException e) {
				fireExceptionStream(dataEvent.getChannel(), e, event.getChannelResult());
			} catch (SocketException e) {
				fireExceptionStream(dataEvent.getChannel(), e, event.getChannelResult());
			}
		}
	}
	
	private void writeComplete(Channel c, DataEvent e, IChannelResult r)
	{
		e.getChannelResult().setSuccess();
		c.getHandlerChain().sendUpstream(e);
	}
	
	private void fireExceptionStream(Channel c, Throwable t, IChannelResult r)
	{
		r.setFailure(t);
		c.getHandlerChain().sendUpstream(new ExceptionEvent(c, t, r));
	}

	private void fireDataReceivedStream(Channel c, AccumByteBuffer b)
	{
		ByteBuffer copyBuffer = b.copy(b.getLength());
		IChannelResult r = new ChannelResult(c);
		DataEvent e = new DataEvent(c, EventState.READ, r, c.getRemoteAddress());
		e.setData(b);
		
		c.getHandlerChain().sendUpstream(e);
		
		if(!e.getChannelResult().isSuccess())
		{
			if(e.getChannelResult().getCause() instanceof BufferUnderflowException)
			{
				b.removeAll(b);
				b.write(copyBuffer);
				return;
			}
			else
			{
				fireExceptionStream(c, e.getChannelResult().getCause(), e.getChannelResult());
			}
		}
	}
	
	private void fireChannelEventUpStream(Channel c, EventState s, IChannelResult r)
	{
		ChannelEvent e = new ChannelEvent(c, s, r);
		c.getHandlerChain().sendUpstream(e);
		if(!e.getChannelResult().isSuccess())
		{
			fireExceptionStream(c, e.getChannelResult().getCause(), e.getChannelResult());
		}
	}
	
	private void bind(IEvent event)
	{
		AsyncServerChannel channel = (AsyncServerChannel)event.getChannel();
		
		IChannelResult result = event.getChannelResult();
		SocketAddress localAddress = (SocketAddress) ((ChannelEvent)event).getData("localAddress");

		boolean bound = false;
		boolean serverStarted = false;
		try {
			channel.getServerSocketChannel().socket().setReuseAddress(true);
			channel.getServerSocketChannel().socket().bind(localAddress);
			bound=true;
			result.setSuccess();
			fireChannelEventUpStream(channel, EventState.BIND, result);
			
			Server serverProcess = new Server((AsyncServerChannel)channel, result, localAddress);
			server.execute(serverProcess);
			serverStarted = true;
			
		} catch (IOException e) {
			result.setFailure(e);
			fireExceptionStream(channel, new NetException(0x0003), result);
		} finally {
			if(bound && !serverStarted)
			{
				close(event);
			}
		}
	}

	private void close(IEvent event)
	{
		Channel channel = event.getChannel();
		boolean isBound = channel.isBound();

		DefaultChannel dc = (DefaultChannel)event.getChannel();
		dc.getReadLock().lock();
		
		try{
			if(channel.getParent() != null)
			{
				Channel parent = channel.getParent();
				parent.getChildren().remove(channel);
			}
			
			if(channel.getChildren() != null)
			{
				ChannelList children = channel.getChildren();
				int size = children.size();
				for(int i=0; i<size; i++)
				{
					children.get(0).close();
				}
				
				if(messageListener != null)
				{
					messageListener.shutdown();
					try {
						messageListener.awaitTermination(10, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
					} finally {
						messageListener.shutdownNow();
					}
				}
				
				try {
					((AsyncServerChannel)channel).getSelector().close();
				} catch (IOException e) {
					event.getChannelResult().setFailure(e);
					fireExceptionStream(channel, e, event.getChannelResult());
				}
				
			}
			else
			{
				try {
					synchronized(channel){
						if(channel.isConnected())
						{
							channel.setDisconnected();
							channel.setReadable(true);
							this.readSelector.close();
							this.interfaceChannel.socket().shutdownInput();
							this.interfaceChannel.socket().shutdownOutput();
							this.interfaceChannel.socket().close();
							this.interfaceChannel = null;
							event.getChannelResult().setSuccess();
							
							fireChannelEventUpStream(channel, EventState.DISCONNECT, event.getChannelResult());
							fireChannelEventUpStream(channel, EventState.CLOSE, event.getChannelResult());
						}
						else
						{
							event.getChannelResult().setSuccess();
						}
					}

					if(isBound)
					{
						channel.setUnbound();
						fireChannelEventUpStream(channel, EventState.UNBIND, event.getChannelResult());
						fireChannelEventUpStream(channel, EventState.CLOSE, event.getChannelResult());
					}
					else
					{
						event.getChannelResult().setSuccess();
					}
				} catch (IOException e) {
					event.getChannelResult().setFailure(e);
					fireExceptionStream(channel, e, event.getChannelResult());
				}
			}
			
			dc.free();
		} finally {
			dc.getReadLock().unlock();
		}
	}
	
	private void connect(IEvent event) throws SocketClosedException
	{
		ChannelEvent ce = (ChannelEvent) event;
		try {
			this.readSelector = Selector.open();
		} catch (IOException e) {
			ce.getChannelResult().setFailure(e);
			throw new NetException(0x0000);
		}
		
		try {
			this.interfaceChannel = SocketChannel.open();
		} catch (IOException e) {
			try {
				this.readSelector.close();
			} catch (IOException e1) {
			}
			ce.getChannelResult().setFailure(e);
			throw new NetException(0x0001);
		}
		
		try {
			this.interfaceChannel.connect(ce.getRemoteAddress());
		} catch (IOException e) {
			try {
				this.readSelector.close();
			} catch (IOException e1) {
			}
			try {
				this.interfaceChannel.socket().shutdownInput();
				this.interfaceChannel.socket().shutdownOutput();
				this.interfaceChannel.close();
			} catch (IOException e1) {
			}
			ce.getChannelResult().setFailure(e);
			throw new NetException(NLabel.get(0x0009) + "[" + ce.getRemoteAddress().toString() +"]");
		}
		
		try {
			this.interfaceChannel.configureBlocking(false);
		} catch (IOException e) {
			try {
				this.readSelector.close();
			} catch (IOException e1) {
			}
			try {
				this.interfaceChannel.socket().shutdownInput();
				this.interfaceChannel.socket().shutdownOutput();
				this.interfaceChannel.close();
			} catch (IOException e1) {
			}
			ce.getChannelResult().setFailure(e);
			throw new NetException(0x0002);
		}
		
		try {
			this.interfaceChannel.socket().setSoLinger(false, 0);
			this.interfaceChannel.socket().setReuseAddress(true);
			this.interfaceChannel.socket().setReceiveBufferSize(event.getChannel().getMaxBufferSize());
			this.interfaceChannel.socket().setSendBufferSize(event.getChannel().getMaxBufferSize());
		} catch(SocketException e) {
			try {
				this.readSelector.close();
			} catch (IOException e1) {
			}
			try {
				this.interfaceChannel.socket().shutdownInput();
				this.interfaceChannel.socket().shutdownOutput();
				this.interfaceChannel.close();
			} catch (IOException e1) {
			}
			ce.getChannelResult().setFailure(e);
			throw new NetException(0x001B);
		}
		
		try {
			this.interfaceChannel.register(readSelector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			try {
				this.readSelector.close();
			} catch (IOException e1) {
			}
			try {
				this.interfaceChannel.socket().shutdownInput();
				this.interfaceChannel.socket().shutdownOutput();
				this.interfaceChannel.close();
			} catch (IOException e1) {
			}
			ce.getChannelResult().setFailure(e);
			throw new SocketClosedException(0x000A);
		}
		
		ce.getChannelResult().setSuccess();
		fireChannelEventUpStream(ce.getChannel(), EventState.OPEN, ce.getChannelResult());
		
		invokeMessageListener(ce.getChannel());
		
		fireChannelEventUpStream(ce.getChannel(), EventState.CONNECT, ce.getChannelResult());
	}
	
	/*private void connect(IEvent event) throws SocketClosedException
	{
		ChannelEvent ce = (ChannelEvent) event;
		try {
			this.readSelector = Selector.open();
		} catch (IOException e) {
			ce.getChannelResult().setFailure(e);
			throw new NetException(0x0000);
		}
		
		try {
			this.interfaceChannel = SocketChannel.open();
		} catch (IOException e) {
			try {
				this.readSelector.close();
			} catch (IOException e1) {
			}
			ce.getChannelResult().setFailure(e);
			throw new NetException(0x0001);
		}
		
		try {
			this.interfaceChannel.socket().setReuseAddress(true);
			this.interfaceChannel.socket().setSoLinger(true, 0);
			this.interfaceChannel.configureBlocking(false);
		} catch (IOException e) {
			try {
				this.readSelector.close();
			} catch (IOException e1) {
			}
			try {
				this.interfaceChannel.socket().shutdownInput();
				this.interfaceChannel.socket().shutdownOutput();
				this.interfaceChannel.close();
			} catch (IOException e1) {
			}
			ce.getChannelResult().setFailure(e);
			throw new NetException(0x0002);
		}
		
		try {
			this.interfaceChannel.connect(ce.getRemoteAddress());
		} catch (IOException e) {
			try {
				this.readSelector.close();
			} catch (IOException e1) {
			}
			try {
				this.interfaceChannel.socket().shutdownInput();
				this.interfaceChannel.socket().shutdownOutput();
				this.interfaceChannel.close();
			} catch (IOException e1) {
			}
			ce.getChannelResult().setFailure(e);
			throw new NetException(NLabel.get(0x0009) + "[" + ce.getRemoteAddress().toString() +"]");
		}
		
		try {
			this.interfaceChannel.socket().setReceiveBufferSize(event.getChannel().getMaxBufferSize());
			this.interfaceChannel.socket().setSendBufferSize(event.getChannel().getMaxBufferSize());
		} catch(SocketException e) {
			try {
				this.readSelector.close();
			} catch (IOException e1) {
			}
			try {
				this.interfaceChannel.socket().shutdownInput();
				this.interfaceChannel.socket().shutdownOutput();
				this.interfaceChannel.close();
			} catch (IOException e1) {
			}
			ce.getChannelResult().setFailure(e);
			throw new NetException(0x001B);
		}
		
		try {
			this.interfaceChannel.register(readSelector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			try {
				this.readSelector.close();
			} catch (IOException e1) {
			}
			try {
				this.interfaceChannel.socket().shutdownInput();
				this.interfaceChannel.socket().shutdownOutput();
				this.interfaceChannel.close();
			} catch (IOException e1) {
			}
			ce.getChannelResult().setFailure(e);
			throw new SocketClosedException(0x000A);
		}
		
		ce.getChannelResult().setSuccess();
		fireChannelEventUpStream(ce.getChannel(), EventState.OPEN, ce.getChannelResult());
		
		invokeMessageListener(ce.getChannel());
		
		fireChannelEventUpStream(ce.getChannel(), EventState.CONNECT, ce.getChannelResult());
	}*/
	
	private final class MessageContainer
	{
		Channel channel; 
		ByteBuffer buffer;
		MessageContainer(Channel channel, ByteBuffer buffer)
		{
			this.channel = channel;
			this.buffer = buffer;
		}
	}
	
	private final class Upstreamer implements Runnable
	{
		private BlockingQueue<MessageContainer> replyQueue;
		Upstreamer(BlockingQueue<MessageContainer> replyQueue)
		{
			this.replyQueue = replyQueue;
		}
		
		public void run() {
			
			AccumByteBuffer recvBuffer = new AccumByteBuffer();
			
			while(true)
			{
				MessageContainer container;
				try {
					if((container = replyQueue.poll(Integer.MAX_VALUE, TimeUnit.SECONDS)) != null)
					{
						if(container.buffer == null)
						{
							break;
						}
						else
						{
							recvBuffer.write(container.buffer);
							fireDataReceivedStream(container.channel, recvBuffer);
						}
					}
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	private final class Receiver implements Runnable 
	{
		private Channel channel;
		private final BlockingQueue<MessageContainer> replyQueue = new LinkedBlockingQueue<MessageContainer>();
		private Upstreamer upStreamer = null;
		Receiver(Channel channel)
		{
			this.channel = channel;
			upStreamer = new Upstreamer(replyQueue);
			Thread t = new Thread(upStreamer);
			t.start();
		}
		
		
		public void run() 
		{
			try {
				int readSocketBufferSize = channel.getMaxBufferSize();
				ByteBuffer tmpReadBuffer = ByteBuffer.allocateDirect(readSocketBufferSize);
				int numKeys;
				while(true)
				{
					try {
						numKeys = readSelector.select(0L);
					} catch (IOException e) {
						throw new NetException(0x0005);
					}
					if(!channel.isReadable())
					{
						synchronized(trafficLock)
						{
							try{
								trafficLock.wait();
							}catch(InterruptedException ex){
							}
						}
					}
					if(numKeys <= 0 && !readSelector.isOpen())
					{
						break;
					}
					if(numKeys > 0)
					{
						Iterator<SelectionKey> itr = readSelector.selectedKeys().iterator();
						while(itr.hasNext())
						{
							SelectionKey key = itr.next();
							itr.remove();
							
							SocketChannel socketChannel = (SocketChannel)key.channel();
							ByteBuffer buffer = read(socketChannel, tmpReadBuffer);
							replyQueue.offer(new MessageContainer(channel, buffer));
						}
					}
					else if(numKeys == 0)
					{
						fireExceptionStream(channel, new TimeoutException(0x000D), new ChannelResult(channel));
					}
				}
			} catch(NetException e) {
				fireExceptionStream(channel, e, new ChannelResult(channel));
			} catch (SocketClosedException e) {
			} catch (ClosedSelectorException e) {
			} catch (Throwable e) {
				fireExceptionStream(channel, e, new ChannelResult(channel));
			} finally {
				replyQueue.offer(new MessageContainer(channel, null));
				ChannelEvent event = new ChannelEvent(channel, EventState.CLOSE, new ChannelResult(channel));
				close(event);
				upStreamer = null;
				channel = null;
			}
		}

		private ByteBuffer read(SocketChannel socketChannel, ByteBuffer tmpReadBuffer) throws NetException, SocketClosedException, SocketException
		{
			AccumByteBuffer accumByteBuffer = new AccumByteBuffer();
			
			while(true){
				try {
					int readLen = socketChannel.read(tmpReadBuffer);

					if(readLen < 0)
					{
						if(accumByteBuffer.size() > 0) break;
						else throw new SocketClosedException(0x000C);
					}
					else if(readLen == 0)
					{
						break;
					}
					else
					{
						tmpReadBuffer.flip();
						accumByteBuffer.write(tmpReadBuffer);
					}
				} catch (SocketClosedException e) {
					throw e;
				} catch (IOException e) {
					throw new SocketClosedException(0x000C);
				} finally {
					tmpReadBuffer.clear();
				}
			}
			
			return accumByteBuffer.getBuffer();
		}
	}
	
	public void write(DataEvent dataEvent) throws SocketClosedException, TimeoutException, NetException, SocketException
	{
		if(!interfaceChannel.isOpen())
		{
			throw new SocketClosedException(0x000A);
		}
		
		DefaultChannel dc = (DefaultChannel)dataEvent.getChannel();
		dc.getWriteLock().lock();
		try{
			ByteBuffer writeBuffer = (ByteBuffer)dataEvent.getMessage();
			writeBuffer.rewind();
	
			while(writeBuffer.position() != writeBuffer.limit()){
				
					int writeLen = 0;
					try {
						writeLen = interfaceChannel.write(writeBuffer);
					} catch (IOException e) {
						throw new SocketClosedException(0x000C);
					}
	
					if(writeLen < 0)
					{
						throw new SocketClosedException(0x000C);
					}
				
			}
		} catch (SocketClosedException e) {
			throw e;
		} catch (TimeoutException e) {
			throw e;
		} finally {
			dc.getWriteLock().unlock();
		}
	}
	
	private final class Server implements Runnable 
	{
		private final NLogger logger = NLogger.getLogger(this.getClass());
		private final Selector selector;
		private final AsyncServerChannel channel;
		Server(AsyncServerChannel channel, IChannelResult result, SocketAddress localAddress)
		{
			this.channel = channel;
			messageListener = Executors.newCachedThreadPool();
			try {
				selector = Selector.open();
			} catch (IOException e) {
				throw new NetException(0x0000);
			}
			
			try {
				channel.getServerSocketChannel().register(selector, SelectionKey.OP_ACCEPT);
			} catch (ClosedChannelException e) {
				throw new NetException(0x0004);
			}
			channel.setSelector(selector);
		}
		
		public void run() {
			while(true)
			{
				try {
					int numKeys;
					
					numKeys = selector.select();
					if(numKeys> 0)
					{
						Iterator<SelectionKey> itr = selector.selectedKeys().iterator();
						
						while(itr.hasNext())
						{
							SelectionKey key = itr.next();
							itr.remove();
							ServerSocketChannel serverSocket = ((ServerSocketChannel)key.channel()); 
							final SocketChannel interfaceChannel = serverSocket.accept();
							
							if(channel.getMaxClientNumber() > 0 && channel.getChildren().size() >= channel.getMaxClientNumber())
							{
								logger.error("overflow max-client number. Disconnect client connection.");
								interfaceChannel.socket().shutdownInput();
								interfaceChannel.socket().shutdownOutput();
								interfaceChannel.close();
								continue;
							}
							
							registAcceptProcessor(channel, interfaceChannel);
						}
					}
				} catch (ClosedSelectorException e) {
					break;
                } catch (Throwable e) {
                	try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                }
			}
		}

		public void registAcceptProcessor(Channel channel, SocketChannel interfaceChannel)
		{
			AsyncAcceptChannel acceptChannel = null;
			try {
				acceptChannel = new AsyncAcceptChannel(channel.getChannelFactory(), channel.getHandlerChainFactory().getHandlerChain(), interfaceChannel);
			} catch (SocketException e) {
				fireExceptionStream(acceptChannel, e, new ChannelResult(acceptChannel));
				try {
					interfaceChannel.socket().shutdownInput();
					interfaceChannel.socket().shutdownOutput();
					interfaceChannel.socket().close();
					return;
				} catch (IOException ex) {
					fireExceptionStream(acceptChannel, ex, new ChannelResult(acceptChannel));
				}
			}
			
			acceptChannel.setHandlerChainFactory(channel.getHandlerChainFactory());
			acceptChannel.setMaxBufferSize(channel.getMaxBufferSize());
			acceptChannel.setReadable(false);
			
			IChannelResult result = new ChannelResult(acceptChannel);
			result.setSuccess();
			fireChannelEventUpStream(acceptChannel, EventState.OPEN, result);
			
			if(channel.addChild(acceptChannel))
			{
				try {
					acceptChannel.invokeMessageListener(messageListener);
					fireChannelEventUpStream(acceptChannel, EventState.CONNECT, result);
					acceptChannel.setReadable(true);
				} catch (IOException e) {
					fireExceptionStream(acceptChannel, e, new ChannelResult(acceptChannel));
					try {
						interfaceChannel.socket().shutdownInput();
						interfaceChannel.socket().shutdownOutput();
						interfaceChannel.socket().close();
					} catch (IOException e1) {
						fireExceptionStream(acceptChannel, e1, new ChannelResult(acceptChannel));
					}
					acceptChannel.free();
				}
			}
			else
			{
				fireExceptionStream(acceptChannel, new Exception("overflow max-client number"), new ChannelResult(acceptChannel));
				try {
					interfaceChannel.socket().shutdownInput();
					interfaceChannel.socket().shutdownOutput();
					interfaceChannel.socket().close();
					acceptChannel.free();
				} catch (IOException e) {
					fireExceptionStream(acceptChannel, e, new ChannelResult(acceptChannel));
				}
			}
		}
	}
	
	private Receiver receiver = null;
	
	protected void notifyMessageListener()
	{
		synchronized(trafficLock)
		{
			trafficLock.notify();
		}
	}

	protected void invokeMessageListener(AsyncAcceptChannel channel, Selector readSelector, ExecutorService messageListener) throws IOException
	{
		if(messageListener != null)
		{
			this.readSelector = readSelector;
			receiver = new Receiver(channel);
			messageListener.execute(receiver);
		}
		else
		{
			readSelector.close();
			throw new NullPointerException("Message Listener is null.");
		}
	}
	
	protected void invokeMessageListener(Channel channel)
	{
		receiver = new Receiver(channel);
		(new Thread(receiver)).start();
	}
}
