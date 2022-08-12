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

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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

  public int getPort() {
    return port;
  }

  public boolean isInMaintenance() {
    return inMaintenance;
  }

  public Email getEmail() {
    return email;
  }
}
