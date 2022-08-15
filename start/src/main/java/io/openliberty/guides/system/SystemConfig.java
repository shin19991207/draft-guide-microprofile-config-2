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

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class SystemConfig {

  // tag::config[]
  @Inject
  @ConfigProperty(name = "io_openliberty_guides.system_inMaintenance")
  private Boolean inMaintenance;
  // end::config[]

  // tag::custom-converter[]
  @Inject
  @ConfigProperty(name = "io_openliberty_guides.email")
  private String email;
  // end::custom-converter[]

  public boolean isInMaintenance() {
    return inMaintenance;
  }

  // tag::getEmail[]
  public String getEmail() {
    return email;
  }
  // end::getEmail[]

}
