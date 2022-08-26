// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.config;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.Dependent;

import org.eclipse.microprofile.config.inject.ConfigProperties;

// tag::ConfigProperties[]
@ConfigProperties(prefix = "io_openliberty_guides")
// end::ConfigProperties[]
@Dependent
// tag::ConfigDetailsBean[]
public class ConfigDetailsBean {
    public int port;
    public boolean inventory_inMaintenance;
    public boolean system_inMaintenance;
    public Optional<String> email;
    public Optional<List<Integer>> checkBackTime;
}
// end::ConfigDetailsBean[]
