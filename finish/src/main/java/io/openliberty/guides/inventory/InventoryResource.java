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
// tag::config-methods[]
package io.openliberty.guides.inventory;

import java.util.Properties;
import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("systems")
public class InventoryResource {

  @Inject
  InventoryManager manager;

  // tag::config-injection[]
  @Inject
  InventoryConfig inventoryConfig;
  // end::config-injection[]

  @GET
  @Path("{hostname}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPropertiesForHost(@PathParam("hostname") String hostname) {

    if (!inventoryConfig.isInMaintenance()) {
      Properties props = manager.get(hostname, inventoryConfig.getPort());
      if (props == null) {
        return Response.status(Response.Status.NOT_FOUND)
                       .entity("{ \"error\" : \"Unknown hostname or the system service "
                       + "may not be running on " + hostname + "\" }")
                       .build();
      }

      manager.add(hostname, props);
      return Response.ok(props).build();
    } else {
      List<Integer> maintenanceWindow = inventoryConfig.getMaintenanceWindow();
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                     .entity("{ \"error\" : \"Service is currently down for maintenance for "
                     + inventoryConfig.getDowntime() + " hours, from "
                     + maintenanceWindow.get(0).toString() + ":00 UTC to " 
                     + maintenanceWindow.get(1).toString() + ":00 UTC. " 
                     + "Contact: " + inventoryConfig.getEmail() + "\" }")
                     .build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response listContents() {
    if (!inventoryConfig.isInMaintenance()) {
      return Response.ok(manager.list()).build();
    } else {
      List<Integer> maintenanceWindow = inventoryConfig.getMaintenanceWindow();
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                     .entity("{ \"error\" : \"Service is currently down for maintenance for "
                     + inventoryConfig.getDowntime() + " hours, from "
                     + maintenanceWindow.get(0).toString() + ":00 UTC to " 
                     + maintenanceWindow.get(1).toString() + ":00 UTC. " 
                     + "Contact: " + inventoryConfig.getEmail().toString() + "\" }")
                     .build();
    }
  }

}
