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
package com.indra.sofia2.support.util.rest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.xml.soap.SOAPMessage;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Lazy
@Configuration
public class RestTools {

    public static class ConfigProps {
        public static final String PROXY_HOST = "rest.proxy.host";
        public static final String PROXY_PORT = "rest.proxy.port";
        public static final String AUTH_USER = "rest.auth.user";
        public static final String AUTH_PASS = "rest.auth.pass";
        public static final String CONN_MAX_ROUTE = "rest.connection.maxRoute";
        public static final String CONN_MAX_TOTAL = "rest.connection.maxTotal";
        public static final String CONN_TIMEOUT_MS = "rest.connection.timeoutMillis";
    }

    public static final String REST_TEMPLATE_BEAN_NAME = "rest-template-s2";

    @Value("${" + ConfigProps.CONN_MAX_ROUTE + ":100}")
    private Integer maxRouteConnections = 100;
    @Value("${" + ConfigProps.CONN_MAX_TOTAL + ":200}")
    private Integer maxTotalConnections = 200;
    @Value("${" + ConfigProps.CONN_TIMEOUT_MS + ":5000}")
    private Integer connectTimeout = 5000;
    @Value("${" + ConfigProps.PROXY_HOST + ":}")
    private String proxyHost;
    @Value("${" + ConfigProps.PROXY_PORT + ":0}")
    private Integer proxyPort;
    @Value("${" + ConfigProps.AUTH_USER + ":}")
    private String authUser;
    @Value("${" + ConfigProps.AUTH_PASS + ":}")
    private String authPass;

    public ClientHttpRequestFactory createFactory(String proxyHost, int proxyPort, String user, String pass) {

        PoolingHttpClientConnectionManager connectionManager;
        connectionManager = new PoolingHttpClientConnectionManager();

        connectionManager.setMaxTotal(maxTotalConnections);
        connectionManager.setDefaultMaxPerRoute(maxRouteConnections);
        RequestConfig config = RequestConfig
                .custom()
                .setConnectTimeout(connectTimeout)
                .build();

        HttpClientBuilder builder = HttpClientBuilder
                .create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config);

        // --- add credentials if found
        if (!StringUtils.isEmpty(user)) {
            builder.setDefaultCredentialsProvider(getCredentialsProvider(user, pass));
        }

        // --- set proxy when required
        if (!StringUtils.isEmpty(proxyHost) && proxyPort > 0) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            builder.setProxy(proxy);
        }

        return new HttpComponentsClientHttpRequestFactory(builder.build());
    }

    @Lazy
    @Bean(name = REST_TEMPLATE_BEAN_NAME)
    public RestTemplate createRestTemplate() {
        return new RestTemplate(createFactory(proxyHost, proxyPort, authUser, authPass));
    }

    /**
     * Genera credenciales para el usuario/password proporcionados.
     * @param user El nombre de usuario.
     * @param pass La password.
     * @return Credenciales con el usuario/password dados, con AuthScope=ANY.
     */
    public CredentialsProvider getCredentialsProvider (String user, String pass) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(user, pass));
        return credentialsProvider;
    }

    /**
     * Si existen datos de autenticacion leidos desde las properties, incluye los header de autorizacion necesarios.
     * @param msg El mensaje en el que se quieren incluir las cabeceras de autorizacion.
     * @return EL mensaje con las cabeceras 'Authorization' y 'Proxy-Authorization', con autenticacion simple.
     */
    public SOAPMessage includeMessageAuth (SOAPMessage msg) {

        if (!StringUtils.isEmpty(authUser)) {
            byte [] auth = Base64.encode((authUser + ":" + authPass).getBytes());
            String authValue = "Basic " + new String(auth);
            msg.getMimeHeaders().addHeader("Authorization", authValue);
            msg.getMimeHeaders().addHeader("Proxy-Authorization", authValue);
        }
        return msg;
    }

    /**
     * Si el valor de proxy host no esta vacio, crea una URL que pase por el proxy.
     * @param url La URL base.
     * @return URL con conexion a traves del proxy especificado en las properties.
     * @throws MalformedURLException
     */
    public URL createProxyURL (String url) throws  MalformedURLException {

        if (!StringUtils.isEmpty(proxyHost)) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            return createProxyURL(url, proxy);
        } else {
            return new URL(url);
        }
    }

    /**
     * Crea una URL que pase por el proxy especificado en el parametro.
     * @param url La URL base.
     * @param proxy El proxy de paso para las peticiones a traves de esta URL.
     * @return URL con conexion a traves del proxy especificado como parametro.
     * @throws MalformedURLException
     */
    public URL createProxyURL(String url, final Proxy proxy) throws MalformedURLException {

        return new URL(null, url, new URLStreamHandler() {

            protected URLConnection openConnection(URL url) throws IOException {

                return new URL(url.toString()).openConnection(proxy);
            }
        });
    }

    //<editor-fold desc="Getter / Setter">
    public Integer getMaxRouteConnections() {
        return maxRouteConnections;
    }

    public void setMaxRouteConnections(Integer maxRouteConnections) {
        this.maxRouteConnections = maxRouteConnections;
    }

    public Integer getMaxTotalConnections() {
        return maxTotalConnections;
    }

    public void setMaxTotalConnections(Integer maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public String getAuthPass() {
        return authPass;
    }

    public void setAuthPass(String authPass) {
        this.authPass = authPass;
    }
    //</editor-fold>
}
