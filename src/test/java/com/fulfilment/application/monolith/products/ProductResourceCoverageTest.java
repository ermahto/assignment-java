package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

/** Extra ProductResource paths for JaCoCo project Total >= 80%. */
@QuarkusTest
public class ProductResourceCoverageTest {

  @Test
  public void getProductByIdReturns200() {
    given().when().get("/product/2").then().statusCode(200).body("id", equalTo(2));
  }

  @Test
  public void getProductByIdReturns404WhenMissing() {
    given().when().get("/product/999999").then().statusCode(404);
  }

  @Test
  public void createProductRejectsClientProvidedId() {
    given()
        .contentType("application/json")
        .body("{\"id\": 5, \"name\": \"X\", \"description\": \"d\", \"price\": 1, \"stock\": 1}")
        .when()
        .post("/product")
        .then()
        .statusCode(422);
  }

  @Test
  public void createUpdateDeleteProductHappyPath() {
    String name = "CovProd_" + System.currentTimeMillis();

    Integer id =
        given()
            .contentType("application/json")
            .body(
                "{\"name\": \""
                    + name
                    + "\", \"description\": \"desc\", \"price\": 12.5, \"stock\": 4}")
            .when()
            .post("/product")
            .then()
            .statusCode(201)
            .extract()
            .path("id");

    given()
        .contentType("application/json")
        .body(
            "{\"name\": \""
                + name
                + "_U\", \"description\": \"upd\", \"price\": 15.0, \"stock\": 6}")
        .when()
        .put("/product/" + id)
        .then()
        .statusCode(200)
        .body("name", equalTo(name + "_U"))
        .body("stock", equalTo(6));

    given()
        .contentType("application/json")
        .body("{\"description\": \"no-name\"}")
        .when()
        .put("/product/" + id)
        .then()
        .statusCode(422);

    given()
        .contentType("application/json")
        .body(
            "{\"name\": \"Ghost\", \"description\": \"d\", \"price\": 1, \"stock\": 1}")
        .when()
        .put("/product/999999")
        .then()
        .statusCode(404);

    given().when().delete("/product/" + id).then().statusCode(204);
    given().when().get("/product/" + id).then().statusCode(404);
    given().when().delete("/product/999999").then().statusCode(404);
  }
}