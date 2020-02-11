package org.folio.service.storage;

import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.folio.rest.jaxrs.model.ClientDefinition;
import org.folio.rest.jaxrs.model.Error;
import org.folio.rest.jaxrs.model.Errors;

import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClientStorageService implements ClientStorage {

  private static final Logger logger = LoggerFactory.getLogger(LocalFileStorageService.class);
  private String pathToFile = "C:\\Users\\Viktor_Skibnev\\notes.txt";

  @Override
  public Future<Errors> saveClient(ClientDefinition client){
    Errors errors = new Errors();
    try(FileWriter fileWriter = new FileWriter(pathToFile)){
      fileWriter.write(client.toString());
    }catch(Exception exception){
      logger.error(exception.getMessage());
      Error error = new Error();
      error.setMessage(exception.getMessage());
      errors.setErrors(Arrays.asList(error));
    }
    return Future.succeededFuture(errors);
  }

  @Override
  public ClientDefinition readClient(){
    ClientDefinition clientDefinition = null;
    StringBuilder stringBuilder = new StringBuilder();
    try(FileReader fileReader = new FileReader(pathToFile)){
      Scanner scan = new Scanner(fileReader);
      while(scan.hasNext()){
        stringBuilder.append(scan.nextLine());
      }
      String[] str = (stringBuilder.substring(stringBuilder.indexOf("["), stringBuilder.indexOf("]"))).split(",");
      List<String> allClientFilds = Arrays.stream(str).map(s -> s.substring(s.indexOf(",")))
        .collect(Collectors.toList());
      clientDefinition.setId(allClientFilds.get(0));
      clientDefinition.setLastName(allClientFilds.get(1));
      clientDefinition.setFirstName(allClientFilds.get(2));
      clientDefinition.setMiddleName(allClientFilds.get(3));
      clientDefinition.setEmail(allClientFilds.get(4));
      clientDefinition.setPhone(allClientFilds.get(5));
      clientDefinition.setMobilePhone(allClientFilds.get(6));
      clientDefinition.setDateOfBirth(new SimpleDateFormat("E, MMM dd HH:mm:ss yyyy").parse(allClientFilds.get(7)));
    }catch(Exception exc){
      logger.error(exc.getMessage());
    }
    return clientDefinition;
  }
}
