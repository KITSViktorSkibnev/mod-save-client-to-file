package org.folio.rest.impl;

import io.vertx.core.*;
import org.folio.dataimport.util.ExceptionHelper;
import org.folio.rest.jaxrs.model.ClientDefinition;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.tools.utils.TenantTool;
import org.folio.spring.SpringContextUtil;

import javax.ws.rs.core.Response;
import java.util.Map;

import static org.folio.dataimport.util.DaoUtil.constructCriteria;

public class ClientsImpl implements org.folio.rest.jaxrs.resource.Clients {

  private static final String CLIENT_DEFINITION_TABLE = "client_definitions";
  private static final String CLIENT_DEFINITION_EMAIL_FIELD = "'email'";
  private String tenantId;

  public ClientsImpl(Vertx vertx, String tenantId){
    SpringContextUtil.autowireDependencies(this, Vertx.currentContext());
    this.tenantId = TenantTool.calculateTenantId(tenantId);
  }

  public ClientsImpl(){

  }

  @Override
  public void postClients(org.folio.rest.jaxrs.model.ClientDefinition entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext){
    PgUtil.post(CLIENT_DEFINITION_TABLE, entity, okapiHeaders, vertxContext, PostClientsResponse.class, reply -> {
      if (reply.failed()){
        asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(reply.cause())));
      }else{
        asyncResultHandler.handle(Future.succeededFuture((Response)PostClientsResponse.respond201WithApplicationJson(entity, PostClientsResponse.headersFor201())));
      }
    });
  }

  @Override
  public void putClients(ClientDefinition entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
   vertxContext.runOnContext(c -> {
     PgUtil.put(CLIENT_DEFINITION_TABLE, entity, entity.getId(), okapiHeaders, vertxContext, PutClientsResponse.class, reply -> {
       if (reply.failed()){
         asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(reply.cause())));
       }else{
         asyncResultHandler.handle(Future.succeededFuture((Response)PutClientsResponse.respond204()));
       }
     });
   });
  }

  @Override
  public void deleteClientsByClientsId(String clientsId, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(c -> {
      PgUtil.deleteById(CLIENT_DEFINITION_TABLE, clientsId, okapiHeaders, vertxContext, DeleteClientsByClientsIdResponse.class, reply -> {
        if (reply.failed()) {
          asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(reply.cause())));
        }else if (reply.result().getStatus()==404){
          asyncResultHandler.handle(Future.succeededFuture(DeleteClientsByClientsIdResponse.respond404WithTextPlain(reply.result())));
        }else{
          asyncResultHandler.handle(Future.succeededFuture(DeleteClientsByClientsIdResponse.respond204()));
        }
      });
    });
  }

  @Override
  public void getClientsByClientsEmail(String clientsEmail, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(c -> {
      String correctEmail;
      Criteria criteria = null;
      if (clientsEmail.contains("%40")){
        correctEmail = clientsEmail.replace("%40","@");
        criteria = constructCriteria(CLIENT_DEFINITION_EMAIL_FIELD, correctEmail);
      }else{
        criteria = constructCriteria(CLIENT_DEFINITION_EMAIL_FIELD, clientsEmail);
      }
      PgUtil.postgresClient(vertxContext, okapiHeaders).get(CLIENT_DEFINITION_TABLE, ClientDefinition.class, new Criterion(criteria),false, reply -> {
        if (reply.failed()){
          asyncResultHandler.handle(Future.succeededFuture(ExceptionHelper.mapExceptionToResponse(reply.cause())));
        }else if (reply.result().getResults().isEmpty()){
          asyncResultHandler.handle(Future.succeededFuture(GetClientsByClientsEmailResponse.respond404WithTextPlain(reply.result())));
        }else{
          asyncResultHandler.handle(Future.succeededFuture(GetClientsByClientsEmailResponse.respond200WithApplicationJson(reply.result().getResults().get(0))));
        }
      });
    });
  }
}
