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
// tag::config-class[]
package io.openliberty.guides.inventory;

import java.util.Optional;
import java.util.List;
import java.util.OptionalInt;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.Config;

import io.openliberty.guides.config.Email;
import io.openliberty.guides.config.ConfigDetailsBean;

@RequestScoped
public class InventoryConfig {    

  @Inject
  @ConfigProperties
  ConfigDetailsBean configDetails;

  @Inject
  Config config;

  // tag::getPortNumber[]
  public int getPortNumber() {
    return configDetails.port;
  }
  // end::getPortNumber[]

  public boolean isInMaintenance() {
    // tag::inMaintenanceGet[]
    return configDetails.inventory_inMaintenance;
    // end::inMaintenanceGet[]
  }

  // tag::getEmail[]
  public Email getEmail() {
    Optional<Email> email = configDetails.email;
    if (email.isPresent()) {
      return email.get();
    }
    return null;
  }
  // end::getEmail[]

  public int getDowntime() {
    OptionalInt downtime = configDetails.downtime;
    if (downtime.isPresent()) {
      return downtime.getAsInt();
    }
    return 0;
  }

  public List<Integer> getMaintenanceWindow() {
    Optional<List<Integer>> maintenanceWindow = config.getOptionalValues("io_openliberty_guides.maintenanceWindow", Integer.class);
    if (maintenanceWindow.isPresent()) {
      return maintenanceWindow.get();
    }
    return null;
  }
}
// end::config-class[]
