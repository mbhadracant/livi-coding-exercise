package se.kry.codetest;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.RoutingContext;

import se.kry.codetest.converter.ServiceConverter;
import se.kry.codetest.model.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class RouteHandlers {
    private DBConnector connector;
    private Map<String, Service> services;

    public RouteHandlers(DBConnector connector, Map<String, Service> services) {
        this.connector = connector;
        this.services = services;
    }

     Handler<RoutingContext> getAllServices() {
        return (request) -> connector.getAllServices().setHandler(ar -> {
                if (ar.succeeded()) {
                    ResultSet resultSet = ar.result();
                    List<JsonObject> services = ServiceConverter.convertResultSetToJsonArray(resultSet);

                    request.response()
                            .putHeader("content-type", "application/json")
                            .end(new JsonArray(services).encode());

                } else {
                    System.err.println(ar.cause().getMessage());
                }
            });
    }

    Handler<RoutingContext> createService() {
        return (request) -> {
            JsonObject jsonBody = request.getBodyAsJson();
            String url = jsonBody.getString("url");
            String name = jsonBody.getString("name");
            String timeAdded = Timestamp.from(Instant.now()).toString();

            Service service = new Service
                    .Builder()
                    .setUrl(url)
                    .setName(name)
                    .setTimeAdded(timeAdded)
                    .setStatus(Service.Status.UNKNOWN)
                    .build();

            services.put(service.getUrl(), service);

            connector.createService(service).setHandler(r -> {
                if(r.succeeded()) {
                    request.response()
                            .putHeader("content-type", "text/plain")
                            .end("OK");
                } else {
                    System.err.println(r.cause().getMessage());
                }
            });
        };
    }

    Handler<RoutingContext> deleteService() {
        return (request) -> {
            JsonObject jsonBody = request.getBodyAsJson();
            String url = jsonBody.getString("url");

            connector.deleteService(url).setHandler(rs -> {
                if(rs.succeeded()) {
                    services.remove(url);
                    System.out.println("Service to be deleted: - " + url);
                } else {
                    System.err.println(rs.cause().getMessage());
                }
            });

            request.response()
                    .putHeader("content-type", "text/plain")
                    .end("OK");

        };
    }

    Handler<RoutingContext> updateService() {
        return (request) -> {
            JsonObject jsonBody = request.getBodyAsJson();
            String url = jsonBody.getString("url");
            String name = jsonBody.getString("name");
            String urlToReplace = jsonBody.getString("urlToReplace");


            Service service = services.get(urlToReplace);

            connector.updateService(urlToReplace, service).setHandler(rs -> {
                if(rs.succeeded()) {
                    service.setName(name);
                    service.setUrl(url);
                    services.remove(urlToReplace);
                    services.put(url, service);
                    System.out.println("Service Updated - " + url);
                } else {
                    System.err.println(rs.cause().getMessage());
                }
            });

            request.response()
                    .putHeader("content-type", "text/plain")
                    .end("OK");

        };
    }
}



