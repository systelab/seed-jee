package com.systelab.seed.client;

import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.model.user.User;
import io.qameta.allure.Step;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UserClient extends BaseClient {

    public List<User> get() throws RequestException {
        WebTarget target = getWebTarget().path("users");

        Response response = target.request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, getAuthenticationToken()).get(Response.class);

        if (response.getStatus() != 200) {
            throw new RequestException(response.getStatus());
        }

        return response.readEntity(new GenericType<List<User>>() {
        });
    }

    @Step("Create the user {0}")
    public User create(User user) throws RequestException {
        WebTarget target = this.getWebTarget().path("users/user");

        Response response = target.request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, getAuthenticationToken()).post(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (response.getStatus() != 200) {
            throw new RequestException(response.getStatus());
        }

        return response.readEntity(User.class);
    }

}