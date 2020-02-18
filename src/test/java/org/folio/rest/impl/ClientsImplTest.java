package org.folio.rest.impl;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.http.HttpStatus;
import org.folio.rest.jaxrs.model.ClientDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.is;

@RunWith(VertxUnitRunner.class)
public class ClientsImplTest {

  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final String OKAPI_TENANT_HEADER = "X-Okapi-tenant";
  private static final String OKAPI_TOKEN_HEADER = "X-Okapi-Token";
  private static final String BASE_CLIENTS_URL = "http://localhost:9130/clients";
  private static final String WRONG_EMAIL = "ivanov@ukr.net";
  private static final String NEW_EMAIL = "viktor.skibnev@gmail.com";

  protected static RequestSpecification spec;
  private static ClientDefinition clientDefinition = new ClientDefinition();

  @Before
  public void setUp() throws Exception {
    spec = new RequestSpecBuilder()
      .addHeader(CONTENT_TYPE_HEADER, "application/json")
      .addHeader(OKAPI_TENANT_HEADER, "testlib")
      .addHeader(OKAPI_TOKEN_HEADER, "token")
      .build();

    clientDefinition.withId("7dc53df5-703e-49b3-8670-b1c468f47f1f")
      .withLastName("Skibnev")
      .withFirstName("Viktor")
      .withMiddleName("Oleksandrovich")
      .withEmail("skibnev@ukr.net")
      .withPhone("0577232298")
      .withMobilePhone("0506782980")
      .withDateOfBirth(new Date());
  }

  @Test
  public void shouldPostClientInfoToDatabase(){
    RestAssured.given()
      .spec(spec)
      .body(clientDefinition)
      .when()
      .post(BASE_CLIENTS_URL)
      .then()
      .statusCode(HttpStatus.SC_CREATED)
      .body("id", is(clientDefinition.getId()));
  }

  @Test
  public void shouldReturn422OnPostClientWithoutBody(){
    RestAssured.given()
      .spec(spec)
      .body(new JsonObject().toString())
      .when()
      .post(BASE_CLIENTS_URL)
      .then()
      .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
  }

  @Test
  public void shouldGetClientInfoFromDatabase() throws UnsupportedEncodingException {
    RestAssured.given()
      .spec(spec)
      .when()
      .get(BASE_CLIENTS_URL + "/" + clientDefinition.getEmail())
      .then()
      .statusCode(HttpStatus.SC_OK)
      .body("id", is(clientDefinition.getId()));
  }

  @Test
  public void shouldGetClientInfoFromDatabaseWithNotExistEmail(){
    RestAssured.given()
      .spec(spec)
      .when()
      .get(BASE_CLIENTS_URL + "/" + WRONG_EMAIL)
      .then()
      .statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  public void shouldGetClientInfoFromDatabaseWithNotCorrectEndpoint(){
    RestAssured.given()
      .spec(spec)
      .when()
      .get(BASE_CLIENTS_URL)
      .then()
      .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void shouldPutClientInfoWithNewEmail(){
    RestAssured.given()
      .spec(spec)
      .body(clientDefinition.withEmail(NEW_EMAIL))
      .when()
      .put(BASE_CLIENTS_URL)
      .then()
      .statusCode(HttpStatus.SC_NO_CONTENT);

    RestAssured.given()
      .spec(spec)
      .when()
      .get(BASE_CLIENTS_URL + "/" + clientDefinition.getEmail())
      .then()
      .statusCode(HttpStatus.SC_OK)
      .body("id", is(clientDefinition.getId()));
  }

  @Test
  public void shouldDeleteClientInfoFromDatabase(){
    RestAssured.given()
      .spec(spec)
      .when()
      .delete(BASE_CLIENTS_URL + "/" + clientDefinition.getId())
      .then()
      .statusCode(HttpStatus.SC_NO_CONTENT);
  }

  @Test
  public void shouldDeleteClientInfoWhenClientIsNotExist(){
    RestAssured.given()
      .spec(spec)
      .when()
      .delete(BASE_CLIENTS_URL + "/" + UUID.randomUUID().toString())
      .then()
      .statusCode(HttpStatus.SC_NOT_FOUND);
  }

}
