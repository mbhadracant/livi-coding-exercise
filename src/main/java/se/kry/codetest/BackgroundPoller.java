package se.kry.codetest;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import se.kry.codetest.model.Service;

import java.util.*;

public class BackgroundPoller {

  public Future<Map<String, Service.Status>> pollServices(Map<String, Service> services) {
    WebClient client = WebClient.create(Vertx.currentContext().owner());
    Map<String, Service.Status> polledResults = new HashMap<>();
     List<Future> futures = new ArrayList<>();

     for(String url : services.keySet()) {
         Future<String> future = Future.future();

         client.get(url).send(ar -> {
             if (ar.succeeded()) {
                 HttpResponse<Buffer> response = ar.result();
                 if (response.statusCode() == 200) {
                     polledResults.put(url, Service.Status.OK);
                 } else {
                     polledResults.put(url, Service.Status.FAIL);
                 }
                 future.complete();
             } else {
                 future.fail(ar.cause());
             }
         });

         futures.add(future);


     }

     Future<Map<String, Service.Status>> future = Future.future();

     CompositeFuture.all(futures).setHandler(ar -> {
         if(ar.succeeded()) {
            future.complete(polledResults);
         } else {
             future.fail(ar.cause());
         }
     });

     return future;
  }
}
