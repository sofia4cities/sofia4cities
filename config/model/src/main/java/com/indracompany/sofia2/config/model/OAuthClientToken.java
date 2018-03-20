package com.indracompany.sofia2.config.model;
import java.io.Serializable;
import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_client_token")
public class OAuthClientToken implements Serializable {
	private static final long serialVersionUID = 8810301060389684282L;

	@Id
    @Column(name = "token_id", unique = true, nullable = false)
    private String tokenId;

    @Lob
    @Column(name = "token")
    private Blob token;

    @Column(name = "authentication_id")
    private String authenticationId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "client_id")
    private String clientId;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Blob getToken() {
        return token;
    }

    public void setToken(Blob token) {
        this.token = token;
    }

    public String getAuthenticationId() {
        return authenticationId;
    }

    public void setAuthenticationId(String authenticationId) {
        this.authenticationId = authenticationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OAuthClientToken)) return false;

        OAuthClientToken that = (OAuthClientToken) o;

        if (authenticationId != null ? !authenticationId.equals(that.authenticationId) : that.authenticationId != null)
            return false;
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        if (tokenId != null ? !tokenId.equals(that.tokenId) : that.tokenId != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tokenId != null ? tokenId.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (authenticationId != null ? authenticationId.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        return result;
    }
}
