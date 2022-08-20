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
package it.io.openliberty.guides.system;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

public class SystemEndpointIT {

  private static String USER_DIR = System.getProperty("user.dir");
  private static String DEFAULT_CONFIG_FILE = USER_DIR
      + "/src/main/resources/META-INF/microprofile-config.properties";

  @Test
  public void testGetProperties() {
    String port = System.getProperty("default.http.port");
    String url = "http://localhost:" + port + "/";
    boolean status = serviceInMaintenance();

    if (!status) {

      Client client = ClientBuilder.newClient();

      WebTarget target = client.target(url + "system/properties");
      Response response = target.request().get();

      assertEquals(200, response.getStatus(), "Incorrect response code from " + url);

      JsonObject obj = response.readEntity(JsonObject.class);

      assertEquals(System.getProperty("os.name"), obj.getString("os.name"),
          "The system property for the local and remote JVM should match");

      response.close();
    }
  }

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

}
