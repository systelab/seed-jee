package com.systelab.seed.unit;

import com.systelab.seed.client.BaseClient;
import com.systelab.seed.client.RequestException;
import com.systelab.seed.util.security.implementation.JWTAuthenticationTokenGenerator;

import java.security.Key;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.junit.Assert;

import io.jsonwebtoken.Jwts;

public class BaseClientTest {

    static String testUserName = "quentinada";
    static String testPassword = "quentinada";

    public static void login(BaseClient baseClient) throws RequestException {

        Response response = baseClient.login(testUserName, testPassword);

        Assert.assertEquals(200, response.getStatus());
        Assert.assertNotNull(response.getHeaderString(HttpHeaders.AUTHORIZATION));
        String token = response.getHeaderString(HttpHeaders.AUTHORIZATION);

        String justTheToken = token.substring("Bearer".length()).trim();
        Key key = new JWTAuthenticationTokenGenerator().generateKey();
        Assert.assertEquals(1, Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getHeader().size());
        Assert.assertEquals("HS512", Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getHeader().getAlgorithm());
        Assert.assertEquals(5, Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getBody().size());
        Assert.assertEquals(testUserName, Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getBody().getSubject());
        Assert.assertEquals(baseClient.getServerURL().toString().concat("/users/login"), Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getBody().getIssuer());
        Assert.assertNotNull(Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getBody().getIssuedAt());
        Assert.assertNotNull(Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getBody().getExpiration());

    }
}
