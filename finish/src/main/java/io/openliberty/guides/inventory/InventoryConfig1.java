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

import io.openliberty.guides.config.Email;

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
  private Email email;

  @Inject
  Config config;

  public int getPort() {
    return configDetails.port;
  }

  public boolean isInMaintenance() {
    return configDetails.inventory_inMaintenance;
  }

  public Email getEmail() {
    Optional<Email> email = configDetails.email;
    if (email.isPresent()) {
      return email.get();
    }
    return null;
  }

  public List<Integer> getMaintenanceWindow() {
    Optional<List<Integer>> maintenanceWindow = config.getOptionalValues("io_openliberty_guides.maintenanceWindow", Integer.class);
    if (maintenanceWindow.isPresent()) {
      return maintenanceWindow.get();
    }
    return null;
  }

  public int getDowntime() {
    OptionalInt downtime = configDetails.downtime;
    if (downtime.isPresent()) {
      return downtime.getAsInt();
    }
    return 0;
  }

}
