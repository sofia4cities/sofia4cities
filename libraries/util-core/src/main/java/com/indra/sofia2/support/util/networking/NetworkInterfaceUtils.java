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

import java.net.InetAddress;
import java.net.SocketException;

public interface NetworkInterfaceUtils {
	
	/**
	 * Returns the IP address of the given network interface
	 * @param networkInterfaceName
	 * @param isIPv6
	 * @return
	 * @throws InvalidNetworkInterfaceException
	 */
	public InetAddress getIPAddress(String networkInterfaceName, boolean isIPv6) throws SocketException;
	
	/**
	 * Returns the wildcard IP address
	 * @param isIPv6
	 * @return
	 */
	public InetAddress getWildcardIPAddress(boolean isIPv6);
	
	/**
	 * Returns the loopback IP address
	 * @param isIPv6
	 * @return
	 */
	public InetAddress getLoopbackIPAddress(boolean isIPv6);
	
}
