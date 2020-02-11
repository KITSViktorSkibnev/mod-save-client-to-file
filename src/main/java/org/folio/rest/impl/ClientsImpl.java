package org.folio.rest.impl;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.folio.dataimport.util.ExceptionHelper;
import org.folio.rest.tools.utils.TenantTool;
import org.folio.service.storage.ClientStorageService;
import org.folio.spring.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;
import java.util.Map;

public class ClientsImpl implements org.folio.rest.jaxrs.resource.Clients {

  private static final Logger LOG = LoggerFactory.getLogger(ClientsImpl.class);

  private String tenantId;

  @Autowired
  ClientStorageService clientStorageService;

  public ClientsImpl(Vertx vertx, String tenantId){
    SpringContextUtil.autowireDependencies(this, Vertx.currentContext());
    this.tenantId = TenantTool.calculateTenantId(tenantId);
  }


  @Override
  public void postClients(org.folio.rest.jaxrs.model.ClientDefinition entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext){
    vertxContext.runOnContext(c ->{
      clientStorageService.saveClient(entity).setHandler(errors -> {
        if(errors.failed()) {
          asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(errors.cause())));
        }else{
          Future.succeededFuture((Response)PostClientsResponse.respond201WithApplicationJson(entity))
            .map(Response.class::cast)
            .otherwise(ExceptionHelper::mapExceptionToResponse)
            .setHandler(asyncResultHandler);
        }
      });
    });
  }
}
