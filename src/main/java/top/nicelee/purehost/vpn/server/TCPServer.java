package top.nicelee.purehost.vpn.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.nicelee.purehost.vpn.LocalVpnService;

public class TCPServer implements Runnable{
	
	public String localIP = "6.6.6.6";
	public int port;
	public String vpnLocalIP;
	ServerSocketChannel serverSocketChannel;
	Selector selector = null;

	Thread tcpThread;
	public void start(){
		try{
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			//serverSocketChannel.socket().setReuseAddress(true);
			//serverSocketChannel.socket().bind(null);
			serverSocketChannel.socket().bind(new InetSocketAddress(12320));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			port = serverSocketChannel.socket().getLocalPort();

		}catch (Exception e){
		}
		tcpThread = new Thread(this);
		tcpThread.setName("TCPServer - Thread");
		tcpThread.start();
	}

	public void stop(){
		tcpThread.interrupt();
		try{
			serverSocketChannel.socket().close();
		}catch (Exception e){
		}
		try{
			selector.close();
		}catch (Exception e){
		}

	}
	public TCPServer(String localIP) {
		this.vpnLocalIP = localIP;

	}

	
	public void service() throws Exception {
		//
		// NATSessionManager.createSession(9867,
		// CommonMethods.ipStringToInt("192.168.1.103"), (short) 7777);
		//
		System.out.println("TCPServer: TCP server starts, the port is:" + port);
	
		while (selector.select() > 0) {
	
			if(!selector.isOpen()){
				throw new Exception("TCPServer: selector closed");
			}

			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while (iterator.hasNext()) {
		
				SelectionKey key = null;
				SocketChannel sc = null;
				try {
					key = (SelectionKey) iterator.next();
					iterator.remove();
					if (key.isAcceptable()) {
						ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
						sc = ssc.accept();

						sc.configureBlocking(false);

						TwinsChannel twins = new TwinsChannel(sc, selector);
						twins.connectRemoteSc();
						sc.register(selector, SelectionKey.OP_READ, twins);
					}
					if (key.isReadable()) {
						reveice(key);
					}
				} catch (NullPointerException e) {
					
					try {
						if (sc != null) {
							sc.close();
						}
					} catch (Exception cex) {
						cex.printStackTrace();
					}
				} catch (Exception e) {
					//e.printStackTrace();
					try {
						if (sc != null) {
							sc.close();
						}
						if (key != null) {
							key.cancel();
							key.channel().close();
						}
					} catch (Exception cex) {
						//cex.printStackTrace();
					}
				} finally {

				}
			}
		}
		System.out.println("-----End of program-----");
	}

	Pattern patternURL = Pattern.compile("^/([^:]+):(.*)$");

	public void reveice(SelectionKey key) throws IOException {
		
		if (key == null)
			return;

		SocketChannel sc = (SocketChannel) key.channel();
		
		Matcher matcher = patternURL.matcher(sc.getRemoteAddress().toString());
		matcher.find();

		TwinsChannel twins = (TwinsChannel) key.attachment();
		
		if (localIP.equals(matcher.group(1))) {

			if (!twins.remoteSc.isConnected()) {
				
				twins.remoteSc.finishConnect();
				twins.remoteSc.configureBlocking(false);
			} else {
				
				ByteBuffer buf = ByteBuffer.allocate(2014);
				int bytesRead = sc.read(buf);
				//String content = "";
				while (bytesRead > 0) {
					//content += new String(buf.array(), 0, buf.position());
					buf.flip();
					twins.remoteSc.write(buf);
					buf.clear();
					bytesRead = sc.read(buf);
				}
				
			}
		} else {
			
			ByteBuffer buf = ByteBuffer.allocate(2014);
			int bytesRead = sc.read(buf);
			//String content = "";
			while (bytesRead > 0) {
				//content += new String(buf.array(), 0, buf.position());
				buf.flip();
				twins.localSc.write(buf);
				buf.clear();
				bytesRead = sc.read(buf);
			}
			
		}
	}

	@Override
	public void run() {
		try {
			service();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

}
