/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indra.sofia2.support.util.networking;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NetworkInterfaceUtilsImpl implements NetworkInterfaceUtils {

	private static final Logger logger = LoggerFactory.getLogger(NetworkInterfaceUtils.class);

	@Override
	public InetAddress getIPAddress(String networkInterfaceName, boolean isIPv6) throws SocketException {
		String protocol = (isIPv6 ? "IPv6" : "IPv4");
		logger.info("Retrieving {} address. InterfaceName = {}.", protocol, networkInterfaceName);
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		boolean interfaceFound = false;
		while (interfaces.hasMoreElements()) {
			NetworkInterface iface = interfaces.nextElement();
			if (iface.getName().equals(networkInterfaceName)) {
				interfaceFound = true;
				Enumeration<InetAddress> ipAddresses = iface.getInetAddresses();
				while (ipAddresses.hasMoreElements()) {
					InetAddress ipAddr = ipAddresses.nextElement();
					if ((isIPv6 && (ipAddr instanceof Inet6Address)) || (ipAddr instanceof Inet4Address)) {
						logger.info("The {} address has been retrieved. InterfaceName = {}, address = {}.", protocol,
								networkInterfaceName, ipAddr.getHostAddress());
						return ipAddr;
					}
				}
			}
		}
		String errorMessage;
		if (interfaceFound)
			errorMessage = "The network interface doesn't have an associated " + protocol + " address";
		else
			errorMessage = "The network interface " + networkInterfaceName + " does not exist";
		logger.error(errorMessage);
		throw new SocketException(errorMessage);
	}

	@Override
	public InetAddress getWildcardIPAddress(boolean isIPv6) {
		if (isIPv6)
			return new InetSocketAddress("::", 0).getAddress();
		else
			return new InetSocketAddress(0).getAddress();
	}
	
	@Override
	public InetAddress getLoopbackIPAddress(boolean isIPv6) {
		if (isIPv6)
			return new InetSocketAddress("::1", 0).getAddress();
		else
			return InetAddress.getLoopbackAddress();
	}

}
