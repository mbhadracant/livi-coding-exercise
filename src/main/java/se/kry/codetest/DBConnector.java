package se.kry.codetest;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import se.kry.codetest.model.Service;

public class DBConnector {

  private final String DB_PATH = "poller.db";
  private final SQLClient client;

  public DBConnector(Vertx vertx){
    JsonObject config = new JsonObject()
        .put("url", "jdbc:sqlite:" + DB_PATH)
        .put("driver_class", "org.sqlite.JDBC")
        .put("max_pool_size", 30);

    client = JDBCClient.createShared(vertx, config);
  }

  public Future<ResultSet> query(String query) {
    return query(query, new JsonArray());
  }


  public Future<ResultSet> query(String query, JsonArray params) {
    if(query == null || query.isEmpty()) {
      return Future.failedFuture("Query is null or empty");
    }
    if(!query.endsWith(";")) {
      query = query + ";";
    }

    Future<ResultSet> queryResultFuture = Future.future();

    client.queryWithParams(query, params, result -> {
      if(result.failed()){
        queryResultFuture.fail(result.cause());
      } else {
        queryResultFuture.complete(result.result());
      }
    });
    return queryResultFuture;
  }

  public Future<ResultSet> createService(Service service) {
    JsonArray jsonArray = new JsonArray() {{
      add(service.getUrl());
      add(service.getName());
      add(service.getTimeAdded());
      add(service.getStatus());
    }};
    return this.query("INSERT INTO service(url, name, timeAdded, status) VALUES (?,?,?,?)", jsonArray);
  }

  public Future<ResultSet> deleteService(String url) {
    JsonArray jsonArray = new JsonArray() {{ add(url); }};
    System.out.println(url);
    return this.query("DELETE FROM service WHERE url=?", jsonArray);
  }

  public Future<ResultSet> getAllServices() {
    return this.query("SELECT * FROM service");
  }

  public Future<ResultSet> updateService(String url, Service service) {
    JsonArray jsonArray = new JsonArray() {{
        add(service.getUrl());
        add(service.getName());
        add(service.getStatus());
        add(url);
    }};
    return this.query("UPDATE service SET url=?, name=?, status=? WHERE url=?", jsonArray);
  }

}
