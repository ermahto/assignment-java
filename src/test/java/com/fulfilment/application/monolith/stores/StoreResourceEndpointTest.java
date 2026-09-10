package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

/**
 * Broad REST coverage for StoreResource so JaCoCo project Total stays above 80%.
 */
@QuarkusTest
public class StoreResourceEndpointTest {

  @InjectMock LegacyStoreManagerGateway legacyGateway;

  @Test
  public void listStoresReturns200() {
    given().when().get("/store").then().statusCode(200);
  }

  @Test
  public void getStoreByIdReturns200() {
    // Do not rely on import.sql id=1 — other tests may call Store.deleteAll().
    String name = "GetById_" + System.currentTimeMillis();
    Integer id =
        given()
            .contentType("application/json")
            .body("{\"name\": \"" + name + "\", \"quantityProductsInStock\": 2}")
            .when()
            .post("/store")
            .then()
            .statusCode(201)
            .extract()
            .path("id");

    given().when().get("/store/" + id).then().statusCode(200).body("id", equalTo(id));
  }

  @Test
  public void getStoreByIdReturns404WhenMissing() {
    given().when().get("/store/999999").then().statusCode(404);
  }

  @Test
  public void createStoreRejectsClientProvidedId() {
    given()
        .contentType("application/json")
        .body("{\"id\": 99, \"name\": \"BadIdStore\", \"quantityProductsInStock\": 1}")
        .when()
        .post("/store")
        .then()
        .statusCode(422);
  }

  @Test
  public void createUpdatePatchDeleteStoreHappyPath() {
    String name = "CovStore_" + System.currentTimeMillis();

    Integer id =
        given()
            .contentType("application/json")
            .body("{\"name\": \"" + name + "\", \"quantityProductsInStock\": 3}")
            .when()
            .post("/store")
            .then()
            .statusCode(201)
            .extract()
            .path("id");

    given()
        .contentType("application/json")
        .body("{\"name\": \"" + name + "_U\", \"quantityProductsInStock\": 7}")
        .when()
        .put("/store/" + id)
        .then()
        .statusCode(200)
        .body("name", equalTo(name + "_U"))
        .body("quantityProductsInStock", equalTo(7));

    given()
        .contentType("application/json")
        .body("{\"name\": \"" + name + "_P\", \"quantityProductsInStock\": 9}")
        .when()
        .patch("/store/" + id)
        .then()
        .statusCode(200)
        .body("name", equalTo(name + "_P"));

    given().when().get("/store").then().statusCode(200).body(containsString(name + "_P"));

    given().when().delete("/store/" + id).then().statusCode(204);
    given().when().get("/store/" + id).then().statusCode(404);
  }

  @Test
  public void updateStoreReturns404WhenMissing() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"Nope\", \"quantityProductsInStock\": 1}")
        .when()
        .put("/store/999999")
        .then()
        .statusCode(404);
  }

  @Test
  public void updateStoreReturns422WhenNameMissing() {
    given()
        .contentType("application/json")
        .body("{\"quantityProductsInStock\": 1}")
        .when()
        .put("/store/1")
        .then()
        .statusCode(422);
  }

  @Test
  public void patchStoreReturns404WhenMissing() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"Nope\", \"quantityProductsInStock\": 1}")
        .when()
        .patch("/store/999999")
        .then()
        .statusCode(404);
  }

  @Test
  public void deleteStoreReturns404WhenMissing() {
    given().when().delete("/store/999999").then().statusCode(404);
  }
}