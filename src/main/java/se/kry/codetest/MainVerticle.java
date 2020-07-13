package se.kry.codetest;

import io.vertx.core.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import se.kry.codetest.converter.ServiceConverter;
import se.kry.codetest.model.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainVerticle extends AbstractVerticle {

    private HashMap<String, Service> services = new HashMap<>();
    private DBConnector connector;
    private BackgroundPoller poller = new BackgroundPoller();
    private RouteHandlers handlers;


    @Override
    public void start(Future<Void> startFuture) {
        connector = new DBConnector(vertx);
        handlers = new RouteHandlers(connector, services);

        connector.getAllServices().setHandler(arServices -> {
            if (arServices.succeeded()) {
                List<Service> servicesFromDB = ServiceConverter.convertResultSetToList(arServices.result());
                servicesFromDB.forEach(s -> services.put(s.getUrl(), s));
                startPoller();
            }
        });

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        startPoller();
        setRoutes(router);
        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        System.out.println("KRY code test service started");
                        startFuture.complete();
                    } else {
                        startFuture.fail(result.cause());
                    }
                });
    }

    private void setRoutes(Router router) {
        router.route("/*").handler(StaticHandler.create());
        router.get("/services").handler(handlers.getAllServices());
        router.post("/service").handler(handlers.createService());
        router.delete("/service").handler(handlers.deleteService());
        router.patch("/service").handler(handlers.updateService());
    }


    private void startPoller() {
        vertx.setPeriodic(5000, timerId -> {
            poller.pollServices(services).setHandler(ar -> {
                if (ar.succeeded()) {
                    Map<String, Service.Status> polledResults = ar.result();
                    polledResults.entrySet()
                            .stream()
                            .filter(e -> !services.get(e.getKey()).getStatus().equals(e.getValue()))
                            .forEach(e -> {
                                String url = e.getKey();
                                Service.Status status = e.getValue();

                                Service service = services.get(url);
                                service.setStatus(status);

                                connector.updateService(url, service);
                            });
                }
            });
        });
    }

}



