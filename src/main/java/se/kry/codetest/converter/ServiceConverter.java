package se.kry.codetest.converter;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import se.kry.codetest.model.Service;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceConverter {

    public static List<JsonObject> convertResultSetToJsonArray(ResultSet resultSet) {
        return resultSet.getResults()
                .stream()
                .map(r -> new JsonObject()
                        .put("url", r.getString(0))
                        .put("name", r.getString(1))
                        .put("timeAdded", r.getString(2))
                        .put("status", Service.Status.valueOf(r.getString(3))))
                .collect(Collectors.toList());
    }

    public static List<Service> convertResultSetToList(ResultSet resultSet) {
        return resultSet.getResults()
                .stream()
                .map(r ->
                        new Service.Builder()
                                .setUrl(r.getString(0))
                                .setName(r.getString(1))
                                .setTimeAdded(r.getString(2))
                                .setStatus(Service.Status.valueOf(r.getString(3)))
                                .build())
                .collect(Collectors.toList());
    }
}
