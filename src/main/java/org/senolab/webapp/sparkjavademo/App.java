package org.senolab.webapp.sparkjavademo;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

import static spark.Spark.*;

public class App {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        final UserService userService = new UserServiceMapImpl();

        post("/users", (request,response) -> {
            response.type("application/json");

            User user = new Gson().fromJson(request.body(), User.class);
            userService.addUser(user);

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
        });

        get("/users", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(userService.getUsers())));
        });

        get("/users/:id", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(userService.getUser(request.params(":id")))));
        });

        put("/users/:id", (request, response) -> {
            response.type("application/json");

            User toEdit = new Gson().fromJson(request.body(), User.class);
            User editedUser = userService.editUser(toEdit);

            if (editedUser != null) {
                return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(editedUser)));
            } else {
                return new Gson().toJson(new StandardResponse(StatusResponse.ERROR, new Gson().toJson("User not found or error in edit")));
            }
        });

        delete("/users/:id", (request, response) -> {
            response.type("application/json");

            userService.deleteUser(request.params(":id"));
            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, "user deleted"));
        });

        options("/users/:id", (request, response) -> {
            response.type("application/json");

            return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, (userService.userExist(request.params(":id"))) ? "User exists" : "User does not exists"));
        });

        notFound((req, res) -> {
            res.type("application/json");
            return "{\"message\":\"Data not found\"}";
        });


    }

    private static String requestAndResponseInfoToString(Request request, Response response) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.requestMethod());
        sb.append(" " + request.url());
        sb.append(" " + request.body());
        HttpServletResponse raw = response.raw();
        sb.append(" Reponse: " + raw.getStatus());
        sb.append(" " + raw.getHeader("content-type"));
        try {
            sb.append(" body size in bytes: " + response.body().getBytes(raw.getCharacterEncoding()).length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


}
