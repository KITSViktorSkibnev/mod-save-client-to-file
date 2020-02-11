package org.folio.service.storage;

import io.vertx.core.Future;
import org.folio.rest.jaxrs.model.ClientDefinition;
import org.folio.rest.jaxrs.model.Errors;

public interface ClientStorage {
  /**
   * Method safe client in storage
   * @param client
   * @return errors if safe of client is not successful
   */
  Future<Errors> saveClient(ClientDefinition client);

  /**
   * Method read client from storage
   * @return client
   */
  ClientDefinition readClient();

}
