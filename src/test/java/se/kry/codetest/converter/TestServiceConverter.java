package se.kry.codetest.converter;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se.kry.codetest.model.Service;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TestServiceConverter {

    @Test
    void testConvertResultSetToJsonArray() {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        List<JsonArray> results = new ArrayList<>();
        String url = "https://google.com";
        String name = "Google";
        String timeAdded = "today";
        String status = "OK";

        results.add(new JsonArray().add(url).add(name).add(timeAdded).add(status));

        when(resultSet.getResults()).thenReturn(results);

        List<JsonObject> actual =  ServiceConverter.convertResultSetToJsonArray(resultSet);

        assertEquals(url, actual.get(0).getString("url"));
        assertEquals(name, actual.get(0).getString("name"));
        assertEquals(timeAdded, actual.get(0).getString("timeAdded"));
        assertEquals(status, actual.get(0).getString("status"));
    }


    @Test
    void testConvertResultSetToList() {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        List<JsonArray> results = new ArrayList<>();
        String url = "https://google.com";
        String name = "Google";
        String timeAdded = "today";
        String status = "OK";

        results.add(new JsonArray().add(url).add(name).add(timeAdded).add(status));

        when(resultSet.getResults()).thenReturn(results);

        List<Service> actual =  ServiceConverter.convertResultSetToList(resultSet);

        assertEquals(url, actual.get(0).getUrl());
        assertEquals(name, actual.get(0).getName());
        assertEquals(timeAdded, actual.get(0).getTimeAdded());
        assertEquals(Service.Status.valueOf(status), actual.get(0).getStatus());
    }
}
