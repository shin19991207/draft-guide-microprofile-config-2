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
package io.openliberty.guides.inventory;

import java.util.Optional;
import java.util.List;
import java.util.OptionalInt;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.Config;

@RequestScoped
public class InventoryConfig {    

  @Inject
  @ConfigProperty(name = "io_openliberty_guides.port")
  private int port;

  @Inject
  @ConfigProperty(name = "io_openliberty_guides.inventory_inMaintenance")
  private Boolean inMaintenance;

  @Inject
  @ConfigProperty(name = "io_openliberty_guides.email")
  // tag::email[]
  private Optional<String> email;
  // end::email[]

  // tag::config[]
  @Inject
  Config config;
  // end::config[]

  public int getPort() {
    return port;
  }

  public boolean isInMaintenance() {
    return inMaintenance;
  }

  public String getEmail() {
    if (email.isPresent()) {
      return email.get();
    }
    return null;
  }

  public List<Integer> getCheckBackTime() {
    // tag::getOptionalValues[]
    Optional<List<Integer>> checkBackTime 
      = config.getOptionalValues("io_openliberty_guides.checkBackTime", Integer.class);
    // end::getOptionalValues[]
    if (checkBackTime.isPresent()) {
      return checkBackTime.get();
    }
    return null;
  }

}
