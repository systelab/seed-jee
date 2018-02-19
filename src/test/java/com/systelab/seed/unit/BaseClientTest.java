package com.systelab.seed.unit;

import com.systelab.seed.client.BaseClient;
import com.systelab.seed.client.RequestException;
import com.systelab.seed.util.security.implementation.JWTAuthenticationTokenGenerator;

import java.security.Key;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Assertions;

public class BaseClientTest {

    private static String testUserName = "Systelab";
    private static String testPassword = "Systelab";

    public static void login(BaseClient baseClient) throws RequestException {

        Response response = baseClient.login(testUserName, testPassword);

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertNotNull(response.getHeaderString(HttpHeaders.AUTHORIZATION));
        String token = response.getHeaderString(HttpHeaders.AUTHORIZATION);

        String justTheToken = token.substring("Bearer".length()).trim();
        Key key = new JWTAuthenticationTokenGenerator().generateKey();
        Assertions.assertEquals(1, Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getHeader().size());
        Assertions.assertEquals("HS512", Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getHeader().getAlgorithm());
        Assertions.assertEquals(5, Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getBody().size());
        Assertions.assertEquals(testUserName, Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getBody().getSubject());
        Assertions.assertEquals(baseClient.getServerURL().toString().concat("/users/login"), Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getBody().getIssuer());
        Assertions.assertNotNull(Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getBody().getIssuedAt());
        Assertions.assertNotNull(Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getBody().getExpiration());

    }
}
