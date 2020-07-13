package se.kry.codetest;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Currently adds data to the database and requires a empty table to work
 * better approach would be to use a in memory database for the unit tests which would prevent altering the main database
 */
@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  @DisplayName("Start a web server on localhost responding to path /service on port 8080")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void start_http_server(Vertx vertx, VertxTestContext testContext) {
    WebClient.create(vertx)
        .get(8080, "::1", "/services")
        .send(response -> testContext.verify(() -> {
          assertEquals(200, response.result().statusCode());
          testContext.completeNow();
        }));
  }

    @Test
    @DisplayName("Does a POST on /service")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void post(Vertx vertx, VertxTestContext testContext) {
        JsonObject data = new JsonObject();
        data.put("name", "Livi");
        data.put("url", "https://www.livi.com");
        WebClient.create(vertx)
                .post(8080, "::1", "/service")
                .sendJsonObject(data, response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    String bodyFromPost = response.result().bodyAsString();
                    assertEquals("OK", bodyFromPost);
                    WebClient.create(vertx)
                            .get(8080, "::1", "/services")
                            .send(r -> testContext.verify(() -> {
                                assertEquals(200, r.result().statusCode());
                                JsonArray bodyFromGet = r.result().bodyAsJsonArray();
                                assertEquals(1, bodyFromGet.size() );
                                testContext.completeNow();
                            }));
                }));
    }

    @Test
    @DisplayName("Does a DELETE on /service")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void delete(Vertx vertx, VertxTestContext testContext) {
        JsonObject data = new JsonObject();
        data.put("name", "Livi");
        data.put("url", "https://www.livi.com");
        WebClient.create(vertx)
                .post(8080, "::1", "/service")
                .sendJsonObject(data, response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    String bodyFromPost = response.result().bodyAsString();
                    assertEquals("OK", bodyFromPost);
                    WebClient.create(vertx)
                            .delete(8080, "::1", "/services")
                            .sendJsonObject(data,r -> testContext.verify(() -> {
                                assertEquals(200, r.result().statusCode());
                                JsonArray bodyFromGet = r.result().bodyAsJsonArray();
                                assertEquals(0, bodyFromGet.size() );
                                testContext.completeNow();
                            }));
                }));
    }

    @Test
    @DisplayName("Does a PATCH on /service")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void patch(Vertx vertx, VertxTestContext testContext) {
        JsonObject data = new JsonObject();
        data.put("name", "Livi");
        data.put("url", "https://www.livi.com");
        data.put("urlToReplace", "https://www.livi.com");

        WebClient.create(vertx)
                .post(8080, "::1", "/service")
                .sendJsonObject(data, response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    String bodyFromPost = response.result().bodyAsString();
                    assertEquals("OK", bodyFromPost);
                    WebClient.create(vertx)
                            .patch(8080, "::1", "/service")
                            .sendJsonObject(data,r -> testContext.verify(() -> {
                                assertEquals(200, r.result().statusCode());
                                testContext.completeNow();
                            }));
                }));
    }

}
