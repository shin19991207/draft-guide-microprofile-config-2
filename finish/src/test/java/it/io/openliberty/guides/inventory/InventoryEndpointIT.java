// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::testClass[]
package it.io.openliberty.guides.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class InventoryEndpointIT {

  private static String port;
  private static String baseUrl;
  private Client client;
  private static boolean status;

  private static String USER_DIR = System.getProperty("user.dir");
  private static String DEFAULT_CONFIG_FILE = USER_DIR
      + "/src/main/resources/META-INF/microprofile-config.properties";

  private final String SYSTEM_PROPERTIES = "system/properties";
  private final String INVENTORY_SYSTEMS = "inventory/systems";


  @BeforeAll
  public static void oneTimeSetup() {
    port = System.getProperty("default.http.port");
    baseUrl = "http://localhost:" + port + "/";
    status = serviceInMaintenance();
  }

  @BeforeEach
  public void setup() {
    client = ClientBuilder.newClient();
  }

  @AfterEach
  public void teardown() {
    client.close();
  }

  // tag::tests[]
  @Test
  @Order(1)
  // tag::testHostRegistration[]
  public void testHostRegistration() {
    if (!status) {
      this.visitLocalhost();

      Response response = this.getResponse(baseUrl + INVENTORY_SYSTEMS);
      this.assertResponse(baseUrl, response);

      JsonObject obj = response.readEntity(JsonObject.class);

      JsonArray systems = obj.getJsonArray("systems");

      boolean localhostExists = false;
      for (int n = 0; n < systems.size(); n++) {
        localhostExists = systems.getJsonObject(n).get("hostname").toString()
            .contains("localhost");
        if (localhostExists) {
          break;
        }
      }
      assertTrue(localhostExists, "A host was registered, but it was not localhost");

      response.close();
    }
  }
  // end::testHostRegistration[]

  @Test
  @Order(2)
  // tag::testSystemPropertiesMatch[]
  public void testSystemPropertiesMatch() {
    if (!status) {
      Response invResponse = this.getResponse(baseUrl + INVENTORY_SYSTEMS);
      Response sysResponse = this.getResponse(baseUrl + SYSTEM_PROPERTIES);

      this.assertResponse(baseUrl, invResponse);
      this.assertResponse(baseUrl, sysResponse);

      JsonObject jsonFromInventory = (JsonObject) invResponse.readEntity(JsonObject.class)
          .getJsonArray("systems").getJsonObject(0).get("properties");

      JsonObject jsonFromSystem = sysResponse.readEntity(JsonObject.class);

      String osNameFromInventory = jsonFromInventory.getString("os.name");
      String osNameFromSystem = jsonFromSystem.getString("os.name");
      this.assertProperty("os.name", "localhost", osNameFromSystem, osNameFromInventory);

      String userNameFromInventory = jsonFromInventory.getString("user.name");
      String userNameFromSystem = jsonFromSystem.getString("user.name");
      this.assertProperty("user.name", "localhost", userNameFromSystem,
          userNameFromInventory);

      invResponse.close();
      sysResponse.close();
    }
  }
  // end::testSystemPropertiesMatch[]

  @Test
  @Order(3)
  // tag::testUnknownHost[]
  public void testUnknownHost() {
    if (!status) {
      Response response = this.getResponse(baseUrl + INVENTORY_SYSTEMS);
      this.assertResponse(baseUrl, response);

      Response badResponse = client
          .target(baseUrl + INVENTORY_SYSTEMS + "/" + "badhostname")
          .request(MediaType.APPLICATION_JSON).get();

      assertEquals(404, badResponse.getStatus(),
      "BadResponse expected status: 404. Response code not as expected.");

      String stringObj = badResponse.readEntity(String.class);
      assertTrue(stringObj.contains("error"),
      "badhostname is not a valid host but it didn't raise an error");

      response.close();
      badResponse.close();
    }
  }
  // end::testUnknownHost[]
  // end::tests[]
  
  // tag::helpers[]
  // tag::javadoc[]
  /**
   * Check if mp.config.profile=maintaining in the deafult microporofile-config.properties file.
   */
  // end::javadoc[]
  public static boolean serviceInMaintenance() {
    String line = "";
    try {
      File f = new File(DEFAULT_CONFIG_FILE);
      if (f.exists()) {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        while ((line = reader.readLine()) != null) {
          if (line.contains("mp.config.profile") && !line.contains("#")
              && line.split("=")[1].equals("maintaining")) {
            return true;
          }
        }
        reader.close();
        return false; // "mp.config.profile" does not exist in DEFAULT_CONFIG_FILE
      } else {
        return false; // DEFAULT_CONFIG_FILE does not exist
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * <p>
   * Returns response information from the specified URL.
   * </p>
   *
   * @param url - target URL.
   * @return Response object with the response from the specified URL.
   */
  // end::javadoc[]
  private Response getResponse(String url) {
    return client.target(url).request().get();
  }

  // tag::javadoc[]
  /**
   * <p>
   * Asserts that the given URL has the correct response code of 200.
   * </p>
   *
   * @param url      - target URL.
   * @param response - response received from the target URL.
   */
  // end::javadoc[]
  private void assertResponse(String url, Response response) {
    assertEquals(200, response.getStatus(), "Incorrect response code from " + url);
  }

  // tag::javadoc[]
  /**
   * Asserts that the specified JVM system property is equivalent in both the
   * system and inventory services.
   *
   * @param propertyName - name of the system property to check.
   * @param hostname     - name of JVM's host.
   * @param expected     - expected name.
   * @param actual       - actual name.
   */
  // end::javadoc[]
  private void assertProperty(String propertyName, String hostname, String expected,
      String actual) {
    assertEquals(expected, actual,
        "JVM system property [" + propertyName + "] "
            + "in the system service does not match the one stored in "
            + "the inventory service for " + hostname);
  }

  // tag::javadoc[]
  /**
   * Makes a simple GET request to inventory/localhost.
   */
  // end::javadoc[]
  private void visitLocalhost() {
    Response response = this.getResponse(baseUrl + SYSTEM_PROPERTIES);
    this.assertResponse(baseUrl, response);
    response.close();

    Response targetResponse = client.target(baseUrl + INVENTORY_SYSTEMS + "/localhost")
        .request().get();
    targetResponse.close();
  }
  // end::helpers[]
}
// end::testClass[]
