package com.indracompany.sofia2.config.model;
import java.io.Serializable;
import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_code")
public class OAuthCode implements Serializable {
	private static final long serialVersionUID = 8018089692223912764L;

	@Id
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Lob
    @Column(name = "authentication")
    private Blob authentication;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Blob getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Blob authentication) {
        this.authentication = authentication;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OAuthCode)) return false;

        OAuthCode oAuthCode = (OAuthCode) o;

        if (authentication != null ? !authentication.equals(oAuthCode.authentication) : oAuthCode.authentication != null)
            return false;
        if (code != null ? !code.equals(oAuthCode.code) : oAuthCode.code != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (authentication != null ? authentication.hashCode() : 0);
        return result;
    }
}
