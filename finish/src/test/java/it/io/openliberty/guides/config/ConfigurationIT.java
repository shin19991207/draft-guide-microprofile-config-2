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
// tag::test[]
package it.io.openliberty.guides.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.	assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class ConfigurationIT {

  private static String port;
  private static String baseUrl;
  private static Client client;
  private static boolean status;

  private static String USER_DIR = System.getProperty("user.dir");
  private static String DEFAULT_CONFIG_FILE = USER_DIR
      + "/src/main/resources/META-INF/microprofile-config.properties";

  private final String CONFIG_TECHNICALSUPPORT = "config/technicalSupport";
  private final String INVENTORY_SYSTEMS = "inventory/systems";
  private final String SYSTEM_PROPERTIES = "system/properties";

  @BeforeAll
  public static void onetimeSetup() {
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

  @Test
  @Order(1)
  // tag::testConfigTechnicalSupport[]
  public void testConfigTechnicalSupport() {
    Response response = getResponse(client, baseUrl + CONFIG_TECHNICALSUPPORT);
    JsonObject obj = response.readEntity(JsonObject.class);

    assertTrue(
      obj.getString("SourceName").contains("server.xml"), 
      "The SourceName should be server.xml");
  } 
  // end::testConfigTechnicalSupport[]

  @Test
  @Order(2)
  // tag::testInventoryServiceStatus[]
  public void testInventoryServiceStatus() {
    Response response = getResponse(client, baseUrl + INVENTORY_SYSTEMS);

    if (!status) {
      int expected = Response.Status.OK.getStatusCode();
      int actual = response.getStatus();
      assertEquals(expected, actual);
    } else {
      assertEquals(503, response.getStatus(),
      "Response code not as expected.");
    }
  } 
  // tag::testInventoryServiceStatus[]
  
  @Test
  @Order(3)
  // tag::testSystemServiceStatus[]
  public void testSystemServiceStatus() {
    Response response = getResponse(client, baseUrl + SYSTEM_PROPERTIES);

    if (!status) {
      int expected = Response.Status.OK.getStatusCode();
      int actual = response.getStatus();
      assertEquals(expected, actual);
    } else {
      assertEquals(503, response.getStatus(),
      "Response code not as expected.");
    }
  } 
  // tag::testSystemServiceStatus[]

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

public static Response getResponse(Client client, String url) {
    Response response = client.target(url).request().get();
    return response;
  }
// end::helpers[]

}
