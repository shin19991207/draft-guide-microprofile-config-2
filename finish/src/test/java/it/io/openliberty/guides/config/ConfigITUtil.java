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
package it.io.openliberty.guides.config;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.RandomAccessFile;
import java.io.IOException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;

/*
 * ===========================================================================
 *  HELPER METHODS
 * ===========================================================================
 *
 */
public class ConfigITUtil {

  /**
   * Change the inventory.inMaintenance value for the config source.
   */
  public static void switchMaintainingEnvironment(String source, boolean value) {
    if (value) {
      String inMaintenance = "mp.config.profile=maintaining";
      appendFile(source, inMaintenance);
    } else {
      removeLastLineFromFile(source);
    }
  }

  /**
   * Overwrite a local file.
   */
  public static void appendFile(String fileName, String newContent) {
    try {
      File f = new File(fileName);
      if (f.exists()) {
        FileWriter fWriter = new FileWriter(f, true); // true to append
                                                       // false to overwrite.
        BufferedWriter bWriter = new BufferedWriter(fWriter);
        bWriter.newLine();
        bWriter.write(newContent);
        bWriter.close();
        fWriter.close();
        Thread.sleep(1200);
      } else {
        System.out.println("File " + fileName + " does not exist");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void removeLastLineFromFile(String fileName) {
    try {
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
        byte b;
        long length = randomAccessFile.length() ;
        if (length != 0) {
            do {
                length -= 1;
                randomAccessFile.seek(length);
                b = randomAccessFile.readByte();
            } while (b != 10 && length > 0);
            randomAccessFile.setLength(length);
            randomAccessFile.close();
        }
        Thread.sleep(1200);
      } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Response getResponse(Client client, String url) {
    Response response = client.target(url).request().get();
    return response;
  }

  public static String getStringFromURL(Client client, String url) {
    Response response = client.target(url).request().get();
    String result = response.readEntity(String.class);
    response.close();
    return result;
  }

}
