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

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.AfterAll;
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

  private static String INVENTORY_HOSTS = "inventory/systems";
  private static String CONFIG_TECHNICALSUPPORT = "config/technicalSupport";
  private static String USER_DIR = System.getProperty("user.dir");
  private static String DEFAULT_CONFIG_FILE = USER_DIR
      + "/src/main/resources/META-INF/microprofile-config.properties";
  private static String MAINTAINING_CONFIG_FILE = USER_DIR
      + "/src/main/resources/META-INF/microprofile-config-maintaining.properties";

  @BeforeAll
  // tag::setup[]
  public static void oneTimeSetup() {
    port = System.getProperty("default.http.port");
    baseUrl = "http://localhost:" + port + "/";

    ConfigITUtil.switchMaintainingEnvironment(DEFAULT_CONFIG_FILE, true);

    client = ClientBuilder.newClient();
  }
  // end::setup[]

  @AfterAll
  public static void allTeardown() {
    ConfigITUtil.switchMaintainingEnvironment(DEFAULT_CONFIG_FILE, false);
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
  public void testInventoryInMaintenance() {
    String error = ConfigITUtil.getStringFromURL(client, baseUrl + INVENTORY_HOSTS);

    assertEquals(
         "{ \"error\" : \"Service is currently down for maintenance for "
        + "3 hours, from 13:00 UTC to 16:00 UTC. " 
        + "Contact: alice@guides.openliberty.io\" }",
        error, "The inventory service should be down");
  } 

  @Test
  @Order(2)
  public void testRoleCustomerSupport() {
    ConfigITUtil.appendFile(MAINTAINING_CONFIG_FILE, "role=customerSupport");

    String error = ConfigITUtil.getStringFromURL(client, baseUrl + INVENTORY_HOSTS);

    assertEquals(
         "{ \"error\" : \"Service is currently down for maintenance for "
        + "3 hours, from 13:00 UTC to 16:00 UTC. " 
        + "Contact: bob@guides.openliberty.io\" }",
        error, "The contact email should be bob@guides.openliberty.io");

    ConfigITUtil.removeLastLineFromFile(MAINTAINING_CONFIG_FILE);
  } 

  @Test
  @Order(3)
  public void testConfigTechnicalSupport() {
    Response response = ConfigITUtil.getResponse(client, baseUrl + CONFIG_TECHNICALSUPPORT);
    JsonObject obj = response.readEntity(JsonObject.class);

    assertTrue(
      obj.getString("SourceName").contains("server.xml"), 
      "The SourceName should contain server.xml");

    response.close();
  } 

}
// end::test[]
