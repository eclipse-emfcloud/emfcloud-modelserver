# Model Server [![build-status-server](https://img.shields.io/jenkins/build?jobUrl=https://ci.eclipse.org/emfcloud/job/eclipse-emfcloud/job/emfcloud-modelserver/job/master/)](https://ci.eclipse.org/emfcloud/job/eclipse-emfcloud/job/emfcloud-modelserver/job/master/)

For more information, please visit the [EMF.cloud Website](https://www.eclipse.org/emfcloud/). If you have questions, contact us on our [discussions page](https://github.com/eclipse-emfcloud/emfcloud/discussions) and have a look at our [communication and support options](https://www.eclipse.org/emfcloud/contact/).

## Prerequisites

The following libraries/frameworks need to be installed on your system:

|                                                                              |           |
| ---------------------------------------------------------------------------- | --------- |
| [Java](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) | `11`      |
| [Maven](https://maven.apache.org/)                                           | `<=3.8.4` |

_Remark:_ There is currently a build problem with the latest Maven version `3.8.5`, which causes a build error for the `p2` build. (See also issues #201 and will be investigated and fixed with issue #203).

## Build

To build the model server as standalone JAR and execute all component tests execute the following maven goal in the root directory:

    mvn clean install

_Remark:_ If you have installed Maven `3.8.5` and do not want to install a lower version, you can also use the provided maven wrapper scripts (that provide Maven `3.8.4`) to build:

For Ubuntu/MacOS:

    ./mvnw mvn clean install

For Windows:

    ./mvnw.cmd mvn clean install

### Maven Repositories [![build-status-server](https://img.shields.io/jenkins/build?jobUrl=https://ci.eclipse.org/emfcloud/job/deploy-emfcloud-modelserver-m2/&label=publish)](https://ci.eclipse.org/emfcloud/job/deploy-emfcloud-modelserver-m2/)

- Snapshots: <https://oss.sonatype.org/content/repositories/snapshots/org/eclipse/emfcloud/modelserver/>

### P2 Update Sites [![build-status-server](https://img.shields.io/jenkins/build?jobUrl=https://ci.eclipse.org/emfcloud/job/deploy-emfcloud-modelserver-p2/&label=publish)](https://ci.eclipse.org/emfcloud/job/deploy-emfcloud-modelserver-p2/)

- Snapshots: <https://download.eclipse.org/emfcloud/modelserver/p2/nightly/>

### Code Coverage

The latest code coverage can be found here: [`org.eclipse.emfcloud.modelserver.codecoverage/jacoco/index.html`](./releng/org.eclipse.emfcloud.modelserver.codecoverage/jacoco/index.html).

The code coverage report is generated with [JaCoCo](https://www.eclemma.org/jacoco/) and is integrated in the Maven build. In the package `com.eclipsesource.modelserver.codecoverage` all code coverages are aggregated into one report.

When executing the Maven build locally, the detailed results are computed and can be investigated in more detail.

## Run

### Execute from IDE

To run the example model server within an IDE, run the main method of `ExampleServerLauncher.java` as a Java Application, located in the module `org.eclipse.emfcloud.modelserver.example`.

### Execute Standalone JAR

To run the model server standalone JAR, run this command in your terminal:

    cd  examples/org.eclipse.emfcloud.modelserver.example/target/
    java -jar org.eclipse.emfcloud.modelserver.example-X.X.X-SNAPSHOT-standalone.jar

#### Usage

    usage: java -jar org.eclipse.emfcloud.modelserver.example-X.X.X-SNAPSHOT-standalone.jar
           [-e] [-h] [-l <arg>] [-p <arg>] [-r <arg>] [-u <arg>]

    options:
     -e,--enableDevLogging    Enable Javalin dev logging (extensive request/response logging
                               meant for development)
     -h,--help                Display usage information about ModelServer
     -l,--logConfig <arg>     Set path to Log4j configuration file (*.xml)
     -p,--port <arg>          Set server port, otherwise a default port is used
     -r,--root <arg>          Set workspace root
     -u,--uiSchemaUri <arg>   Set UI schema folder uri

### Logging

The default logging configuration for the `ExampleServerLauncher` is console output and the LogLevel is set to `INFO`.

To set the path to a custom configuration configuration file you can use the CLI argument `-l,--logConfig`.

The example offers such a configuration file which is used by the launch configuration `ExampleServerLauncher.launch` (`-l=log4j2.xml`).

This example configuration configures the logging behavior as follows:
The default log level is set to `INFO` for the Log4j 2 logger itself and to `DEBUG` for the Model Server application logging.

- Console logging: `Level.FATAL` and `Level.ERROR` write to `stderr`, all levels above write to `stdout`.
- Rolling File Logger: Is configured to log on `Level.DEBUG` and outputs to the log file `logs/emfcloud-modelserver.log`.

[Another example for an external log configuration](https://github.com/eclipse-emfcloud/emfcloud-modelserver-theia/tree/master/examples/coffee-theia/config/log4j2.xml) can be found in the `emfcloud-modelserver-theia` repository.

For more information on the Log4j 2 configuration please visit [the Log4j 2 manual](https://logging.apache.org/log4j/2.x/manual/configuration.html).

#### Development logging

To enable extensive development logging in Javalin for `http` and `websocket` requests, you can use the CLI argument `-e,--enableDevLogging`.

It will output details for each request/response similar to the following snippet:

    2022-01-01 00:00:00,000000000 [JettyServerThreadPool-25] INFO Javalin - JAVALIN REQUEST DEBUG LOG:
    Request: GET [/api/v1/models]
        Matching endpoint-handlers: [BEFORE=*, BEFORE=/api/v1/*, GET=/api/v1/models]
        Headers: {Accept=*/*, User-Agent=PostmanRuntime/7.28.4, Connection=keep-alive, Postman-Token=..., Host=localhost:8081, Accept-Encoding=gzip, deflate, br}
        Cookies: {}
        Body:
        QueryString: modeluri=SuperBrewer3000.coffee
        QueryParams: {modeluri=[SuperBrewer3000.coffee]}
        FormParams: {}
    Response: [200], execution took 5.84 ms
        Headers: {Date=Fri, 01 Jan 2022 00:00:00 GMT, Content-Type=application/json}
        Body is 1015 bytes (starts on next line):
        {
      "type" : "success",
      "data" : {
        "eClass" : "http://www.eclipsesource.com/modelserver/example/coffeemodel#//Machine",
        "children" : [ {
          ...
        } ],
        "name" : "Super Brewer 3000",
        "workflows" : [ {
          "name" : "Simple Workflow",
          ...
        } ]
      }
    }

## Model Server API

### Parameters

- The query parameter `?modeluri=` accepts files in the loaded workspace as well as absolute file paths.
- Parameters in brackets `[]` are optional.
  - If no format is specified, the default format is JSON.
  - [WebSocket] The parameter `livevalidation` defaults to false. If set to true the websocket will recieve validation results automatically on model changes.

### HTTP Endpoints

The Model Server supports two versions of the API (v1 and v2). If the model server is up and running, you can access the model server v1 API via `http://localhost:8081/api/v1/*`,
and the v2 API via `http://localhost:8081/api/v2/*`

#### HTTP Endpoints - V1

<details>
  <summary>HTTP Endpoints - V1</summary>

The following table shows the current HTTP endpoints (v1):

| Category             | Description                                                                                     | HTTP method | Path                      | Input                                                               |
| -------------------- | ----------------------------------------------------------------------------------------------- | :---------: | ------------------------- | ------------------------------------------------------------------- |
| **Models**           | Get all available models in the workspace                                                       |   **GET**   | `/models`                 | query parameter: `[?format=...]`                                    |
|                      | Get model                                                                                       |   **GET**   | `/models`                 | query parameter: `?modeluri=...[&format=...]`                       |
|                      | Create new model                                                                                |  **POST**   | `/models`                 | query parameter: `?modeluri=...[&format=...]` <br> application/json |
|                      | Update model                                                                                    |  **PATCH**  | `/models`                 | query parameter: `?modeluri=...[&format=...]` <br> application/json |
|                      | Delete model                                                                                    | **DELETE**  | `/models`                 | query parameter: `?modeluri=...`                                    |
|                      | Close model                                                                                     |  **POST**   | `/close`                  | query parameter: `?modeluri=...`                                    |
|                      | Save                                                                                            |   **GET**   | `/save`                   | query parameter: `?modeluri=...`                                    |
|                      | SaveAll                                                                                         |   **GET**   | `/saveall`                | -                                                                   |
|                      | Undo                                                                                            |   **GET**   | `/undo`                   | query parameter: `?modeluri=...`                                    |
|                      | Redo                                                                                            |   **GET**   | `/redo`                   | query parameter: `?modeluri=...`                                    |
|                      | Execute commands                                                                                |  **PATCH**  | `/edit`                   | query parameter: `?modeluri=...`                                    |
|                      | Get all available model URIs in the workspace                                                   |   **GET**   | `/modeluris`              | -                                                                   |
|                      | Get model element by id                                                                         |   **GET**   | `/modelelement`           | query parameter: `?modeluri=...&elementid=...[&format=...]`         |
|                      | Get model element by name <br> (Returns the first element that matches the given `elementname`) |   **GET**   | `/modelelement`           | query parameter: `?modeluri=...&elementname=...[&format=...]`       |
| **JSON schema**      | Get the type schema of a model as a JSON schema                                                 |   **GET**   | `/typeschema`             | query parameter: `?modeluri=...`                                    |
|                      | Get the UI schema of a certain view element                                                     |   **GET**   | `/uischema`               | query parameter: `?schemaname=...`                                  |
| **Server actions**   | Ping server                                                                                     |   **GET**   | `/server/ping`            | -                                                                   |
|                      | Update server configuration                                                                     |   **PUT**   | `/server/configure`       | application/json                                                    |
| **Model Validation** | Validate Model                                                                                  |   **GET**   | `/validation`             | query parameter: `?modeluri=...`                                    |
|                      | Get list of constraints                                                                         |   **GET**   | `/validation/constraints` | query parameter: `?modeluri=...`                                    |

<br/>

</details>

#### HTTP Endpoints - V2

The following table shows the current HTTP endpoints (v2):

| Category             | Description                                                                                                                                       | HTTP method | Path                      | Input                                                               |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- | :---------: | ------------------------- | ------------------------------------------------------------------- |
| **Models**           | Get all available models in the workspace                                                                                                         |   **GET**   | `/models`                 | query parameter: `[?format=...]`                                    |
|                      | Get model                                                                                                                                         |   **GET**   | `/models`                 | query parameter: `?modeluri=...[&format=...]`                       |
|                      | Create new model                                                                                                                                  |  **POST**   | `/models`                 | query parameter: `?modeluri=...[&format=...]` <br> application/json |
|                      | Execute commands                                                                                                                                  |  **PATCH**  | `/models`                 | query parameter: `?modeluri=...[&format=...]` <br> application/json |
|                      | Replace model                                                                                                                                     |   **PUT**   | `/models`                 | query parameter: `?modeluri=...[&format=...]` <br> application/json |
|                      | Delete model                                                                                                                                      | **DELETE**  | `/models`                 | query parameter: `?modeluri=...`                                    |
|                      | Close model                                                                                                                                       |  **POST**   | `/close`                  | query parameter: `?modeluri=...`                                    |
|                      | Save                                                                                                                                              |   **GET**   | `/save`                   | query parameter: `?modeluri=...`                                    |
|                      | SaveAll                                                                                                                                           |   **GET**   | `/saveall`                | -                                                                   |
|                      | Undo                                                                                                                                              |   **GET**   | `/undo`                   | query parameter: `?modeluri=...`                                    |
|                      | Redo                                                                                                                                              |   **GET**   | `/redo`                   | query parameter: `?modeluri=...`                                    |
|                      | Get all available model URIs in the workspace                                                                                                     |   **GET**   | `/modeluris`              | -                                                                   |
|                      | Get model element by id                                                                                                                           |   **GET**   | `/modelelement`           | query parameter: `?modeluri=...&elementid=...[&format=...]`         |
|                      | Get model element by name <br> (Returns the first element that matches the given `elementname`)                                                   |   **GET**   | `/modelelement`           | query parameter: `?modeluri=...&elementname=...[&format=...]`       |
| **JSON schema**      | Get the type schema of a model as a JSON schema                                                                                                   |   **GET**   | `/typeschema`             | query parameter: `?modeluri=...`                                    |
|                      | Get the UI schema of a certain view element                                                                                                       |   **GET**   | `/uischema`               | query parameter: `?schemaname=...`                                  |
| **Server actions**   | Ping server                                                                                                                                       |   **GET**   | `/server/ping`            | -                                                                   |
|                      | Update server configuration                                                                                                                       |   **PUT**   | `/server/configure`       | application/json                                                    |
| **Model Validation** | Validate Model                                                                                                                                    |   **GET**   | `/validation`             | query parameter: `?modeluri=...`                                    |
|                      | Get list of constraints                                                                                                                           |   **GET**   | `/validation/constraints` | query parameter: `?modeluri=...`                                    |
| **Internal**         | Create a transaction for auto-composing edits on a model with intermediate results. Returns the ID of the transaction for a websocket (see below) |  **POST**   | `/transaction`            | query parameter: `?modeluri=...`<br/>No body content required       |

<br/>

#### Server Configuration

Per default, updating the server configuration (`/server/configure`) with a new workspaceRoot, enables queueing of further incoming requests until configuration is completed.
Please see `ModelServerRouting` for details.

### WebSocket Endpoints

Subscriptions are implemented via websockets. For v1, `ws://localhost:8081/api/v1/*`. For v2, `ws://localhost:8081/api/v2/*`.

The following table shows the current WS endpoints common to both v1 and v2 APIs:

| Description                | Path         | Input                                                                             | Returns     |
| -------------------------- | ------------ | --------------------------------------------------------------------------------- | ----------- |
| Subscribe to model changes | `/subscribe` | query parameter: `?modeluri=...[&format=...][&timeout=...][&livevalidation=-...]` | `sessionId` |

The following table shows messages accepted from a client on valid WS `/subscribe` connection:

| Type        | Description                                    | Example message                   |
| ----------- | ---------------------------------------------- | --------------------------------- |
| `keepAlive` | Keep WS connection alive if timeout is defined | `{ type: 'keepAlive', data: '' }` |

#### Websocket Endpoints — v2

The following table shows the WS endpoints added in the v2 API:

| Description                                            | Path               | Input                                                                                                 |
| ------------------------------------------------------ | ------------------ | ----------------------------------------------------------------------------------------------------- |
| [Internal] Incremental edits with intermediate results | `/transaction/:id` | `id` returned from `POST` request on `/transaction`</br>query parameter: `?modeluri=...[&format=...]` |

The following table shows messages accepted from a client on the `transaction/:id` endpoint in the v2 API:

| Type        | Description                                                             | Example message                                                                        |
| ----------- | ----------------------------------------------------------------------- | -------------------------------------------------------------------------------------- |
| `execute`   | Execute an edit on the model, via EMF command or JSON Patch             | `{ "type": "execute", "data": { "type": "modelserver.patch" ...}" }`                   |
| `close`     | Close the transaction, putting summary of all changes on the undo stack | `{ "type": "close" }`                                                                  |
| `roll-back` | Cancel the transaction, discarding all changes                          | `{ "type": "roll-back", "message": "Unexpected opening date for course enrollment." }` |

And messages from the server that clients will receive on the `transaction/:id` endpoint:

| Type      | Description                                   | Example message                                                                                                       |
| --------- | --------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| `success` | Successful execution of command or JSON Patch | `{ "type": "success", "data": { "message": "Model successfully updated.", "patch": [ { "op": "add", ... }, ... ] } }` |

## Java Client API

The model server project features a Java-based client API that eases integration with the model server.
The interface declaration is as defined below. Please note that the `Model` class is a POJO with a model uri and content.

<details>
  <summary>v1 Client API</summary>

```Java
public interface ModelServerClientApiV1<A> {

  CompletableFuture<Response<String>> get(String modelUri);

  CompletableFuture<Response<A>> get(String modelUri, String format);

  CompletableFuture<Response<List<Model<String>>>> getAll();

  CompletableFuture<Response<List<Model<A>>>> getAll(String format);

  CompletableFuture<Response<List<String>>> getModelUris();

  CompletableFuture<Response<String>> getModelElementById(String modelUri, String elementid);

  CompletableFuture<Response<A>> getModelElementById(String modelUri, String elementid, String format);

  CompletableFuture<Response<String>> getModelElementByName(String modelUri, String elementname);

  CompletableFuture<Response<A>> getModelElementByName(String modelUri, String elementname, String format);

  CompletableFuture<Response<Boolean>> delete(String modelUri);

  CompletableFuture<Response<Boolean>> close(String modelUri);

  CompletableFuture<Response<String>> create(String modelUri, String createdModelAsJsonText);

  CompletableFuture<Response<A>> create(String modelUri, A createdModel, String format);

  CompletableFuture<Response<String>> update(String modelUri, String updatedModelAsJsonText);

  CompletableFuture<Response<A>> update(String modelUri, A updatedModel, String format);

  CompletableFuture<Response<Boolean>> save(String modelUri);

  CompletableFuture<Response<Boolean>> saveAll();

  CompletableFuture<Response<String>> validate(String modelUri);

  CompletableFuture<Response<String>> getValidationConstraints(String modelUri);

  CompletableFuture<Response<String>> getTypeSchema(String modelUri);

  CompletableFuture<Response<String>> getUiSchema(String schemaName);

  CompletableFuture<Response<Boolean>> configure(ServerConfiguration configuration);

  CompletableFuture<Response<Boolean>> ping();

  CompletableFuture<Response<Boolean>> edit(String modelUri, CCommand command, String format);

  void subscribe(String modelUri, SubscriptionListener subscriptionListener);

  void subscribe(String modelUri, SubscriptionListener subscriptionListener, String format);

  void subscribe(String modelUri, SubscriptionListener subscriptionListener, long timeout);

  void subscribe(String modelUri, SubscriptionListener subscriptionListener, String format, long timeout);

  void subscribeWithValidation(String modelUri, SubscriptionListener subscriptionListener);

  void subscribeWithValidation(String modelUri, SubscriptionListener subscriptionListener, String format);

  void subscribeWithValidation(String modelUri, SubscriptionListener subscriptionListener, long timeout);

  void subscribeWithValidation(String modelUri, SubscriptionListener subscriptionListener, String format,
  long timeout);

  boolean send(String modelUri, String message);

  boolean unsubscribe(String modelUri);

  EditingContext edit();

  boolean close(EditingContext editingContext);

  CompletableFuture<Response<Boolean>> undo(String modelUri);

  CompletableFuture<Response<Boolean>> redo(String modelUri);
}

```

</details>

**v2 Client API:**

```Java
public interface ModelServerClientApiV2<A> {

  CompletableFuture<Response<String>> get(String modelUri);

  CompletableFuture<Response<A>> get(String modelUri, String format);

  CompletableFuture<Response<List<Model<String>>>> getAll();

  CompletableFuture<Response<List<Model<A>>>> getAll(String format);

  CompletableFuture<Response<List<String>>> getModelUris();

  CompletableFuture<Response<String>> getModelElementById(String modelUri, String elementid);

  CompletableFuture<Response<A>> getModelElementById(String modelUri, String elementid, String format);

  CompletableFuture<Response<String>> getModelElementByName(String modelUri, String elementname);

  CompletableFuture<Response<A>> getModelElementByName(String modelUri, String elementname, String format);

  CompletableFuture<Response<Boolean>> delete(String modelUri);

  CompletableFuture<Response<Boolean>> close(String modelUri);

  CompletableFuture<Response<String>> create(String modelUri, String createdModelAsJsonText);

  CompletableFuture<Response<A>> create(String modelUri, A createdModel, String format);

  CompletableFuture<Response<String>> update(String modelUri, String updatedModelAsJsonText);

  CompletableFuture<Response<A>> update(String modelUri, A updatedModel, String format);

  CompletableFuture<Response<Boolean>> save(String modelUri);

  CompletableFuture<Response<Boolean>> saveAll();

  CompletableFuture<Response<String>> validate(String modelUri);

  CompletableFuture<Response<String>> getValidationConstraints(String modelUri);

  CompletableFuture<Response<String>> getTypeSchema(String modelUri);

  CompletableFuture<Response<String>> getUiSchema(String schemaName);

  CompletableFuture<Response<Boolean>> configure(ServerConfiguration configuration);

  CompletableFuture<Response<Boolean>> ping();

  CompletableFuture<Response<String>> edit(String modelUri, CCommand command, String format);

  CompletableFuture<Response<String>> edit(String modelUri, ArrayNode jsonPatch, String format);

  void subscribe(String modelUri, SubscriptionListener subscriptionListener);

  void subscribe(String modelUri, SubscriptionListener subscriptionListener, String format);

  void subscribe(String modelUri, SubscriptionListener subscriptionListener, long timeout);

  void subscribe(String modelUri, SubscriptionListener subscriptionListener, String format, long timeout);

  void subscribeWithValidation(String modelUri, SubscriptionListener subscriptionListener);

  void subscribeWithValidation(String modelUri, SubscriptionListener subscriptionListener, String format);

  void subscribeWithValidation(String modelUri, SubscriptionListener subscriptionListener, long timeout);

  void subscribeWithValidation(String modelUri, SubscriptionListener subscriptionListener, String format,
    long timeout);

  void subscribe(String modelUri, SubscriptionListener subscriptionListener, SubscriptionOptions options);


  boolean send(String modelUri, String message);

  boolean unsubscribe(String modelUri);

  CompletableFuture<Response<String>> undo(String modelUri);

  CompletableFuture<Response<String>> redo(String modelUri);
}
```

<details>
  <summary>v2 Subscription Options API</summary>

```Java
public interface SubscriptionOptions extends Serializable {

   String getFormat();

   boolean isLiveValidation();

   long getTimeout();

   String getPathScheme();

   Map<String, String> getAdditionalOptions();

   default boolean hasAdditionalOptions() {
      return !getAdditionalOptions().isEmpty();
   }

   static Builder builder() {
      return new Impl.Builder();
   }

   interface Builder {

      Builder withFormat(String format);

      Builder withLiveValidation();

      Builder withLiveValidation(boolean validation);

      Builder withTimeout(final long timeout);

      Builder withTimeout(long timeout, TimeUnit unit);

      Builder withPathScheme(String pathScheme);

      Builder withOption(String key, String value);

      SubscriptionOptions build();
   }

}
```

</details>

### REST API Example

```Java
// You can customize the underlying okhttp instance by passing it in as a 1st parameter
ModelServerClient client = new ModelServerClient("http://localhost:8081/api/v2/");

// perform simple GET
client.get("SuperBrewer3000.json")
      .thenAccept(response -> System.out.println("GET: " + response.body()));

// perform same GET, but obtain the result as an EObject
client.get("SuperBrewer3000.json", "json-v2")
      .thenAccept(response -> System.out.println("GET: " + response.body()));

// perform GET ALL
client.getAll()
      .thenAccept(response -> System.out.println("GET ALL: " + response.body()));

// replace the model content via a PATCH update
EObject coffeeMachine = ...;
client.update("SuperBrewer3000.json", coffeeMachine, "json-v2")
      .thenAccept(response -> System.out.println(response.body()));
```

### Executing Commands

To perform changes on the model, clients may issue `PATCH` requests to update
the model state incrementally in the server. These updates are broadcast to
subscribers as incremental updates (see below).

Consider the following JSON payload for a `PATCH` request to add change the name
of the workflow in the example _Super Brewer 3000_ model and to add another task
to it:

<details>
  <summary>v1 PATCH Commands</summary>

```json
{
  "eClass": "http://www.eclipse.org/emfcloud/modelserver/command#//CompoundCommand",
  "type": "compound",
  "commands": [
    {
      "eClass": "http://www.eclipse.org/emfcloud/modelserver/command#//Command",
      "type": "set",
      "owner": {
        "eClass": "http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
        "$ref": "SuperBrewer3000.json#//@workflows.0"
      },
      "feature": "name",
      "dataValues": ["Auto Brew"]
    },
    {
      "eClass": "http://www.eclipse.org/emfcloud/modelserver/command#//Command",
      "type": "add",
      "owner": {
        "eClass": "http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
        "$ref": "SuperBrewer3000.json#//@workflows.0"
      },
      "feature": "nodes",
      "objectValues": [
        {
          "eClass": "http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
          "$ref": "//@commands.1/@objectsToAdd.0"
        }
      ],
      "objectsToAdd": [
        {
          "eClass": "http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
          "name": "Brew"
        }
      ],
      "indices": [1]
    }
  ]
}
```

This is a JSON representation of an EMF `CompoundCommand` containing two commands, a
`SetCommand` that changes the name of the first workflow in the model, and an
`AddCommand` that adds a new `AutomaticTask` to that workflow. The `SetCommand` does
not require any index because the `name` feature is single-valued. The `AddCommand`
here explicitly adds an position `1`, but this can also be omitted to simply append
to the end of the list. Notice how each command indicates the `owner` object in the
model to which the change is applied using a cross-document reference. And in the case
of the `AddCommand`, the object to be added does not yet exist in the model, so it must
be included in the payload of the command, itself. Thus it is contained in the
`objectsToAdd` property and indicate via an in-document reference in the `objectValues`
property. Other commands, such as the `RemoveCommand`, would indicate objects in the
`objectValues` property that already exist in the model (to be removed in that case),
and so those would be cross-document references and the `objectsToAdd` is unused.

To execute this command, issue a `PATCH` request to the `edit` endpoint like:

    PATCH http://localhost:8081/api/v1/edit?modeluri=SuperBrewer3000.json
    Content-type: application/json
    { "data" : <payload> }

The model server project already provides a default set of commands but it is also possible to plug in your custom metamodel-specific commands by providing `CommandContributions` specified with your model server module.

All commands are executed on a transactional command stack within an **EMF transactional editing domain**. The use of an EMF transactional editing domain on the server side provides a more reliable way of executing commands through transactions and therefore making a clear separation between the end user's operations. In addition, it enables us to make use of `RecordingCommands` which record the changes made to objects via the custom metamodel's API and therefore provide automatic undo/redo support for custom commands.

</details>

**v2 Patch, using an EMF Command:**

```json
{
    "type": "modelserver.emfcommand",
    "data": {
        "eClass": "http://www.eclipse.org/emfcloud/modelserver/command#//CompoundCommand",
        "type": "compound",
        "commands": [
            {
                "eClass": "http://www.eclipse.org/emfcloud/modelserver/command#//Command",
                "type": "set",
                "owner": {
                    "eClass": "http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
                    "$ref": "SuperBrewer3000.json#//@workflows.0"
                },
                "feature": "name",
                "dataValues": [
                    "Auto Brew"
                ]
            },
            {
                "eClass": "http://www.eclipse.org/emfcloud/modelserver/command#//Command",
                "type": "add",
                "owner": {
                    "eClass": "http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
                    "$ref": "SuperBrewer3000.json#//@workflows.0"
                },
                "feature": "nodes",
                "objectValues": [
                    {
                        "eClass": "http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
                        "$ref": "//@commands.1/@objectsToAdd.0"
                    }
                ],
                "objectsToAdd": [
                    {
                        "eClass": "http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
                        "name": "Brew"
                    }
                ],
                "indices": [
                    1
                ]
            }
        ]
    }
}
```

This is a JSON representation of an EMF `CompoundCommand` containing two commands, a
`SetCommand` that changes the name of the first workflow in the model, and an
`AddCommand` that adds a new `AutomaticTask` to that workflow. The `SetCommand` does
not require any index because the `name` feature is single-valued. The `AddCommand`
here explicitly adds an position `1`, but this can also be omitted to simply append
to the end of the list. Notice how each command indicates the `owner` object in the
model to which the change is applied using a cross-document reference. And in the case
of the `AddCommand`, the object to be added does not yet exist in the model, so it must
be included in the payload of the command, itself. Thus it is contained in the
`objectsToAdd` property and indicate via an in-document reference in the `objectValues`
property. Other commands, such as the `RemoveCommand`, would indicate objects in the
`objectValues` property that already exist in the model (to be removed in that case),
and so those would be cross-document references and the `objectsToAdd` is unused.

To execute this command, issue a `PATCH` request to the `models` endpoint like:

    PATCH http://localhost:8081/api/v2/models?modeluri=SuperBrewer3000.json
    Content-type: application/json
    { "data" : <payload> }

The model server project already provides a default set of commands but it is also possible to plug in your custom metamodel-specific commands by providing `CommandContributions` specified with your model server module.

All commands are executed on a transactional command stack within an **EMF transactional editing domain**. The use of an EMF transactional editing domain on the server side provides a more reliable way of executing commands through transactions and therefore making a clear separation between the end user's operations. In addition, it enables us to make use of `RecordingCommands` which record the changes made to objects via the custom metamodel's API and therefore provide automatic undo/redo support for custom commands.

**v2 Patch, using a Json Patch with EMF-like paths:**

```json
  {
    "type": "modelserver.patch",
    "data": [
      {
        "op": "replace",
        "path": "SuperBrewer3000.json#//@workflows.0/name",
        "value": "Auto Brew"
      },
      {
        "op": "add",
        "path": "SuperBrewer3000.json#//@workflows.0/nodes/-",
        "value": {
          "$type": "http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
          "name": "Brew"
        }
      }
    ]
  }
```

This Json Patch is equivalent to the EMF Command above, and can be used in the same way. This is the recommended format to use for Web clients,
as manipulating this Json Patch format is a lot easier than EMF Commands.

In this case, we still use an EMF-like path, which contains the URI of the model to edit, the ID of the Object to edit, and the feature to edit:

`modeluri#objectID/featureName` or `modeluri#objectID/featureName/index` (Where the special value `-` can be used to represent the last element of the list).

**v2 Patch, using a Json Patch with standard Json Pointer paths:**

```json
{
  "type": "modelserver.patch",
  "data": [
    {
      "op": "replace",
      "path": "/workflows/0/name",
      "value": "Auto Brew"
    },
    {
      "op": "add",
      "path": "/workflows/0/nodes/-",
      "value": {
        "$type": "http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
        "name": "Brew"
      }
    }
  ]
}
```

In this last case, we use standard Json Pointer paths, instead of EMF-like paths. The model URI is no longer part of the path, as
this concept doesn't exist with Json Patch/Json Pointers. Instead, the `?modeluri=` query parameter will be used. Currently, this format
can't be used to edit multiple resources with a single operation.

### WebSocket Subscriptions Example

If you want to be notified about any changes happening on a certain model,
you can subscribe with a `SubscriptionListener` and define a format for the responses, which is an `EObjectSubscriptionListener` for `json-v2` format in this example.

Please also see a basic running example in `org.eclipse.emfcloud.modelserver.example.client`.

```Java
ModelServerClient client = new ModelServerClient("http://localhost:8081/api/v2/");
String subscriptionId = "SuperBrewer3000.json";

client.subscribe(subscriptionId, new EObjectSubscriptionListener(new JsonCodecV2()) {
  @Override
  public void onOpen(final Response<String> response) {
    System.out.println("Connected: " + response.getMessage());
  }

  @Override
  public void onSuccess(final Optional<String> message) {
    System.out.println("Success: " + message.get());
  }

  @Override
  public void onIncrementalUpdate(final JsonPatch patch) {
     System.out.println("Patch update from model server received: " + patch.toString());
  }

  @Override
  public void onDirtyChange(final boolean isDirty) {
    System.out.println("Dirty State: " + isDirty);
  }

  @Override
  public void onUnknown(final ModelServerNotification notification) {
    System.out.println("Unknown notification of type " + notification.getType() + ": " + notification.getData());
  }

  @Override
  public void onFullUpdate(final EObject fullUpdate) {
    System.out.println("Full <EObject> update from model server received: " + fullUpdate.toString());
  }

  @Override
  public void onError(final Optional<String> message) {
    System.out.println("Error from model server received: " + message.get());
  }

  @Override
  public void onFailure(final Throwable t, final Response<String> response) {
    System.out.println("Failure: " + response.getMessage());
    t.printStackTrace();
  }

  @Override
  public void onFailure(final Throwable t) {
    System.out.println("Failure: ");
    t.printStackTrace();
  }

  @Override
  public void onClosing(final int code, final String reason) {
    System.out.println("Closing connection to model server, reason: " + reason);
  }

  @Override
  public void onClosed(final int code, final String reason) {
    System.out.println("Closed connection to model server, reason: " + reason);
  }
});

// ...

client.unsubscribe(subscriptionId);
```

The kind of message received depends on the operation. For an `update` call (`PATCH` request on the model), the message is the new content of the model (`onFullUpdate`). For an `edit` call (incremental update applied by a `PATCH` request with an edit command or JSON patch — see above), the message is the result of the command that was executed (`onIncrementalUpdate`). In the case of an API v2 client with `json-v2` message format, the incremental update takes the form of a JSON patch describing the changes performed on the server. The patch can be applied to a local copy of the model to synchronize with the server and is modeled in EMF as a `JsonPatch` object.

Subscriptions support a number of options to tweak their behaviour.
For example, to customize the idle timeout interval and receive incremental updates in which the `Operation`s in the JSON Patches use EObject fragment URIs in the `path` property instead of standard JSON Pointers:

```Java
ModelServerClient client = new ModelServerClient("http://localhost:8081/api/v2/");
String subscriptionId = "SuperBrewer3000.json";
SubscriptionListener listener = /* as above */ ;

client.subscribe(subscriptionId, listener, SubscriptionOptions.builder()
    .withTimeout(60, TimeUnit.SECONDS)
    .withPathScheme(ModelServerPathParametersV2.PATHS_URI_FRAGMENTS)
    .build());
```

## Contributing

All involved code must adhere to the provided codestyle and checkstyle settings.

### Eclipse IDE Setup

#### Requirements

- Please make sure your Eclipse workspace uses a JRE of Java 9 or higher.
- Install the Eclipse Checkstyle Plug-in via its update site `https://checkstyle.org/eclipse-cs/#!/install`.

#### Configure Checkstyle

This project uses the common checkstyle ruleset from EMF.cloud. Please follow the [instructions for usage in Eclipse](https://github.com/eclipse-emfcloud/emfcloud/tree/master/codestyle#usage-in-eclipse-ide) to configure this ruleset for a new project.
To configure Checkstyle for a new project in the same workspace your can also right click on the project, choose `Checkstyle > Configure project(s) from blueprint...` and select `org.eclipse.emfcloud.modelserver.common` as blueprint project.
Run `Checkstyle > Check Code with Checkstyle` to make sure Checkstyle is activated correctly.

#### Import Existing Projects

Import all maven projects via `File > Import... > Maven > Existing Maven Projects > Root directory: $REPO_LOCATION`.
You may skip the parent modules (i.e. `org.eclipse.emfcloud.modelserver.*.parent`).

#### Create New Project

When a new project is needed, please stick to the following instructions to guarantee your code will be conform to the existing code conventions.

##### Project-Specific settings

Upon project creation the settings file `org.eclipse.resources.prefs` is created automatically and usually needs no further adjustment.
Please copy and replace (if applicable) the following preferences files from `org.eclipse.emfcloud.modelserver.common` before you start coding:

- `org.eclipse.jdt.core.prefs`
- `org.eclipse.jdt.launching.prefs`
- `org.eclipse.jdt.ui.prefs`
- `org.eclipse.m2e.core.prefs`

#### Commit Changes

Please make sure to include the `.settings` folder as well as the `.checkstyle` settings file to the repository in your initial commit.
