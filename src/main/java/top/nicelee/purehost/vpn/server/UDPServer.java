package top.nicelee.purehost.vpn.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.nicelee.purehost.vpn.LocalVpnService;
import top.nicelee.purehost.vpn.config.ConfigReader;
import top.nicelee.purehost.vpn.dns.DnsPacket;
import top.nicelee.purehost.vpn.dns.Question;
import top.nicelee.purehost.vpn.dns.ResourcePointer;
import top.nicelee.purehost.vpn.ip.CommonMethods;
import top.nicelee.purehost.vpn.ip.IPHeader;
import top.nicelee.purehost.vpn.ip.UDPHeader;

import static java.lang.Thread.sleep;


public class UDPServer implements Runnable {
	public String localIP = "7.7.7.7";
	public int port = 7777;
	public String vpnLocalIP;
	
	final int MAX_LENGTH = 1024;
	byte[] receMsgs = new byte[MAX_LENGTH];

	DatagramSocket udpSocket;
	DatagramPacket packet;
	DatagramPacket sendPacket;
	Pattern patternURL = Pattern.compile("^/([^:]+):(.*)$");

	Thread udpThread;
	public void start(){
		udpThread = new Thread(this);
		udpThread.setName("UDPServer - Thread");
		udpThread.start();
	}

	public void stop(){
		udpSocket.close();
	}
	public UDPServer(String localIP) {
		this.vpnLocalIP = localIP;
		try {
			init();
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void init() throws UnknownHostException, SocketException {
		udpSocket = new DatagramSocket();
		LocalVpnService.Instance.protect(udpSocket);
		port = udpSocket.getLocalPort();
		packet = new DatagramPacket(receMsgs, 28 , receMsgs.length - 28);
	}

	public void service() {
		System.out.println("UDPServer: UDP server starts, the port is:" + port);
		try {
			while (true) {
				udpSocket.receive(packet);
				
				Matcher matcher = patternURL.matcher(packet.getSocketAddress().toString());
				matcher.find();
				
				if (localIP.equals(matcher.group(1))) {
					
				
					NATSession session = NATSessionManager.getSession((short)packet.getPort());
					if( session == null) {
						
						continue;
					}
				
					sendPacket = new DatagramPacket(receMsgs, 28, packet.getLength(), CommonMethods.ipIntToInet4Address(session.RemoteIP), (int)session.RemotePort);
					udpSocket.send(sendPacket);
				}else {
					
					NATSession session = new NATSession();
					session.RemoteIP = CommonMethods.ipStringToInt(matcher.group(1));
					session.RemotePort = (short) packet.getPort();
					Short port = NATSessionManager.getPort(session);
					if( port == null) {
					
						continue;
					}
				


					IPHeader ipHeader = new IPHeader(receMsgs, 0);
					ipHeader.Default();
					ipHeader.setDestinationIP(CommonMethods.ipStringToInt(vpnLocalIP));
					ipHeader.setSourceIP(session.RemoteIP);
					ipHeader.setTotalLength(20 + 8 + packet.getLength());
					ipHeader.setHeaderLength(20);
					ipHeader.setProtocol(IPHeader.UDP);
					ipHeader.setTTL((byte)30);

					UDPHeader udpHeader = new UDPHeader(receMsgs, 20);
					udpHeader.setDestinationPort((short)port);
					udpHeader.setSourcePort(session.RemotePort);
					udpHeader.setTotalLength(8 + packet.getLength());

					LocalVpnService.Instance.sendUDPPacket(ipHeader, udpHeader);
				}
			}
		} catch (SocketException e) {
			//e.printStackTrace();
			//ConfigReader.writeHost(e.toString());
		}catch (IOException e) {
			//e.printStackTrace();
			//ConfigReader.writeHost(e.toString());
		} catch (Exception e) {
			//e.printStackTrace();
			//ConfigReader.writeHost(e.toString());
		} finally {
		
			System.out.println("UDPServer: udpServer closed");
			if (udpSocket != null) {
				udpSocket.close();
			}
		}
	}

	@Override
	public void run() {
		service();
	}
}
