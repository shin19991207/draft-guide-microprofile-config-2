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
package io.openliberty.guides.system;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RequestScoped
@Path("properties")
public class SystemResource {

  // tag::config-injection[]
  @Inject
  SystemConfig systemConfig;
  // end::config-injection[]

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getProperties() {
    if (!systemConfig.isInMaintenance()) {
      return Response.ok(System.getProperties()).build();
    } else {
      List<Integer> checkBackTime = systemConfig.getCheckBackTime();
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                     .entity("{ \"error\" : \"Service is currently down for maintenance. "
                     + "Please check back at " 
                     + checkBackTime.get(0) + ":" + checkBackTime.get(1) + " UTC. "
                     + "Contact: " + systemConfig.getEmail() + "\" }")
                     .build();
    }
  }

}
