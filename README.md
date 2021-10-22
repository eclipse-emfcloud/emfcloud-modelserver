# Model Server [![build-status-server](https://img.shields.io/jenkins/build?jobUrl=https://ci.eclipse.org/emfcloud/job/eclipse-emfcloud/job/emfcloud-modelserver/job/master/)](https://ci.eclipse.org/emfcloud/job/eclipse-emfcloud/job/emfcloud-modelserver/job/master/)

For more information, please visit the [EMF.cloud Website](https://www.eclipse.org/emfcloud/). If you have questions, contact us on our [discussions page](https://github.com/eclipse-emfcloud/emfcloud/discussions) and have a look at our [communication and support options](https://www.eclipse.org/emfcloud/contact/).

## Build
To build the model server as standalone JAR and execute all component tests execute the following maven goal in the root directory:
```bash
mvn clean install
```

### Maven Repositories [![build-status-server](https://img.shields.io/jenkins/build?jobUrl=https://ci.eclipse.org/emfcloud/job/deploy-emfcloud-modelserver-m2/&label=publish)](https://ci.eclipse.org/emfcloud/job/deploy-emfcloud-modelserver-m2/)
- <i>Snapshots: </i> https://oss.sonatype.org/content/repositories/snapshots/org/eclipse/emfcloud/modelserver/

### P2 Update Sites [![build-status-server](https://img.shields.io/jenkins/build?jobUrl=https://ci.eclipse.org/emfcloud/job/deploy-emfcloud-modelserver-p2/&label=publish)](https://ci.eclipse.org/emfcloud/job/deploy-emfcloud-modelserver-p2/)
- <i>Snapshots: </i> https://download.eclipse.org/emfcloud/modelserver/p2/nightly/

### Code Coverage

The latest code coverage can be found here: [`org.eclipse.emfcloud.modelserver.codecoverage/jacoco/index.html`](./releng/org.eclipse.emfcloud.modelserver.codecoverage/jacoco/index.html).

The code coverage report is generated with [JaCoCo](https://www.eclemma.org/jacoco/) and is integrated in the Maven build. In the package `com.eclipsesource.modelserver.codecoverage` all code coverages are aggregated into one report.

When executing the Maven build locally, the detailed results are computed and can be investigated in more detail.

## Run
### Execute from IDE
To run the example model server within an IDE, run the main method of `ExampleServerLauncher.java` as a Java Application, located in the module `org.eclipse.emfcloud.modelserver.example`.


### Execute Standalone JAR
To run the model server standalone JAR, run this command in your terminal:
```bash
cd  examples/org.eclipse.emfcloud.modelserver.example/target/
java -jar org.eclipse.emfcloud.modelserver.example-X.X.X-SNAPSHOT-standalone.jar
```

#### Usage
```
usage: java -jar org.eclipse.emfcloud.modelserver.example-X.X.X-SNAPSHOT-standalone.jar
       [-e] [-h] [-p <arg>] [-r <arg>] [-u <arg>]

options:
 -e,--errorsOnly          Only log errors
 -h,--help                Display usage information about ModelServer
 -p,--port <arg>          Set server port, otherwise default port 8081 is used
 -r,--root <arg>          Set workspace root
 -u,--uiSchemaUri <arg>   Set UI schema folder uri
```

## Model Server API

### Parameters
- The query parameter `?modeluri=` accepts files in the loaded workspace as well as absolute file paths.
- Parameters in brackets `[]` are optional.
  - If no format is specified, the default format is JSON.
  - [WebSocket] The parameter `livevalidation` defaults to false. If set to true the websocket will recieve validation results automatically on model changes.

<br/>

### HTTP Endpoints
If the model server is up and running, you can access the model server API via `http://localhost:8081/api/v1/*`.

The following table shows the current HTTP endpoints: 

|Category|Description|HTTP method|Path|Input
|-|-|:-:|-|-
|__Models__|Get all available models in the workspace|__GET__|`/models`|query parameter: `[?format=...]`
| |Get model|__GET__|`/models`|query parameter: `?modeluri=...[&format=...]`
| |Create new model|__POST__|`/models`|query parameter: `?modeluri=...[&format=...]` <br> application/json
| |Update model|__PATCH__|`/models`|query parameter: `?modeluri=...[&format=...]` <br> application/json
| |Delete model|__DELETE__|`/models`|query parameter: `?modeluri=...`
| |Close model|__POST__|`/close`|query parameter: `?modeluri=...`
| |Save|__GET__|`/save`|query parameter: `?modeluri=...`
| |SaveAll|__GET__|`/saveall`| -
| |Undo|__GET__|`/undo`|query parameter: `?modeluri=...`
| |Redo|__GET__|`/redo`|query parameter: `?modeluri=...`
| |Execute commands|__PATCH__|`/edit`|query parameter: `?modeluri=...`
| |Get all available model URIs in the workspace|__GET__|`/modeluris`| -
| |Get model element by id|__GET__|`/modelelement`|query parameter: `?modeluri=...&elementid=...[&format=...]`
| |Get model element by name <br> (Returns the first element that matches the given `elementname`)|__GET__|`/modelelement`|query parameter: `?modeluri=...&elementname=...[&format=...]`
|__JSON schema__ |Get the type schema of a model as a JSON schema|__GET__|`/typeschema`|query parameter: `?modeluri=...`
| |Get the UI schema of a certain view element|__GET__|`/uischema`|query parameter: `?schemaname=...`
|__Server actions__|Ping server|__GET__|`/server/ping`| -
| |Update server configuration|__PUT__|`/server/configure`|application/json
|__Model Validation__|Validate Model|__GET__|`/validation`| query parameter: `?modeluri=...`
| |Get list of constraints|__GET__|`/validation/constraints`|query parameter: `?modeluri=...`

<br/>

#### Server Configuration
Per default, updating the server configuration (`/server/configure`) with a new workspaceRoot, enables queueing of further incoming requests until configuration is completed.
Please see `ModelServerRouting` for details.

<br/>

### WebSocket Endpoints

Subscriptions are implemented via websockets `ws://localhost:8081/api/v1/*`.

The following table shows the current WS endpoints: 

|Description|Path|Input|Returns
|-|-|-|-
|Subscribe to model changes|`/subscribe`|query parameter: `?modeluri=...[&format=...][&timeout=...][&livevalidation=-...]`|`sessionId`

<br/>

The following table shows accepted messages from a valid WS connection:

|Type|Description|Example message
|-|-|-
`keepAlive`|Keep WS connection alive if timeout is defined|`{ type: 'keepAlive', data: '' }`

<br/>

## Java Client API

The model server project features a Java-based client API that eases integration with the model server.
The interface declaration is as defined below. Please note that the `Model` class is a POJO with a model uri and content.

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


### REST API Example

```Java
// You can customize the underlying okhttp instance by passing it in as a 1st parameter 
ModelServerClient client = new ModelServerClient("http://localhost:8081/api/v1/");

// perform simple GET
client.get("SuperBrewer3000.json")
      .thenAccept(response -> System.out.println("GET: " + response.body()));

// perform same GET, but expect an EObject
client.get("SuperBrewer3000.json", "xmi")
      .thenAccept(response -> System.out.println("GET: " + response.body()));

// perform GET ALL
client.getAll()
      .thenAccept(response -> System.out.println("GET ALL: " + response.body()));

// perform PATCH update
client.update("SuperBrewer3000.json", "{ <payload> }")
      .thenAccept(response -> System.out.println(response.body()));

// perform PATCH update with XMI format
client.update("SuperBrewer3000.json", brewingUnit_EObject, "xmi")
  .thenAccept(response -> {
    client.get("SuperBrewer3000.json").thenAccept(resp -> {
      System.out.println(client.decode(resp.body(), "xmi"));
    });
  });
}
```

### Executing Commands

To perform changes on the model, clients may issue `PATCH` requests to update
the model state incrementally in the server.  These updates are broadcast to
subscribers as incremental updates (see below).

Consider the following JSON payload for a `PATCH` request to add change the name
of the workflow in the example *Super Brewer 3000* model and to add another task
to it:

```json
{
    "eClass": "http://www.eclipse.org/emfcloud/modelserver/command#//CompoundCommand",
    "type": "compound",
    "commands": [
        {
            "eClass": "http://www.eclipse.org/emfcloud/modelserver/command#//Command",
            "type": "set",
            "owner": {
                "eClass":"http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
                "$ref":"SuperBrewer3000.json#//@workflows.0"
          },
          "feature": "name",
          "dataValues": [ "Auto Brew" ]
        },
        {
            "eClass": "http://www.eclipse.org/emfcloud/modelserver/command#//Command",
            "type": "add",
            "owner": {
                "eClass":"http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
                "$ref":"SuperBrewer3000.json#//@workflows.0"
            },
            "feature": "nodes",
            "objectValues": [
                {
                    "eClass":"http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
                    "$ref":"//@commands.1/@objectsToAdd.0"
                }
            ],
            "objectsToAdd": [
                {
                    "eClass":"http://www.eclipsesource.com/modelserver/example/coffeemodel#//AutomaticTask",
                    "name":"Brew"
                }
            ],
            "indices": [ 1 ]
        }
    ]
}
```

This is a JSON representation of an EMF `CompoundCommand` containing two commands, a
`SetCommand` that changes the name of the first workflow in the model, and an
`AddCommand` that adds a new `AutomaticTask` to that workflow.  The `SetCommand` does
not require any index because the `name` feature is single-valued.  The `AddCommand`
here explicitly adds an position `1`, but this can also be omitted to simply append
to the end of the list.  Notice how each command indicates the `owner` object in the
model to which the change is applied using a cross-document reference.  And in the case
of the `AddCommand`, the object to be added does not yet exist in the model, so it must
be included in the payload of the command, itself.  Thus it is contained in the
`objectsToAdd` property and indicate via an in-document reference in the `objectValues`
property.  Other commands, such as the `RemoveCommand`, would indicate objects in the
`objectValues` property that already exist in the model (to be removed in that case),
and so those would be cross-document references and the `objectsToAdd` is unused.

To execute this command, issue a `PATCH` request to the `edit` endpoint like:

```
    PATCH http://localhost:8081/api/v1/edit?modeluri=SuperBrewer3000.json
    Content-type: application/json
    { "data" : <payload> }
```

The model server project already provides a default set of commands but it is also possible to plug in your custom metamodel-specific commands by providing `CommandContributions` specified with your model server module.

All commands are executed on a transactional command stack within an **EMF transactional editing domain**. The use of an EMF transactional editing domain on the server side provides a more reliable way of executing commands through transactions and therefore making a clear separation between the end user's operations. In addition, it enables us to make use of `RecordingCommands` which record the changes made to objects via the custom metamodel's API and therefore provide automatic undo/redo support for custom commands.

### WebSocket Subscriptions Example

If you want to be notified about any changes happening on a certain model, 
you can subscribe with a `SubscriptionListener` and define a format for the responses, which is TypedSubscriptionListener for `xmi` in this example.

Please also see a basic running example in `org.eclipse.emfcloud.modelserver.example.client`.

```Java
ModelServerClient client = new ModelServerClient("http://localhost:8081/api/v1/");
String subscriptionId = "SuperBrewer3000.json";
client.subscribe(subscriptionId, new XmiToEObjectSubscriptionListener() {
  @Override
  public void onOpen(final Response<String> response) {
    System.out.println("Connected: " + response.getMessage());
  }

  @Override
  public void onSuccess(final Optional<String> message) {
    System.out.println("Success: " + message.get());
  }

  @Override
  public void onIncrementalUpdate(final CCommandExecutionResult incrementalUpdate) {
    System.out.println("Incremental update from model server received: " + incrementalUpdate.toString());
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
    System.out.println("Full <XmiEObject> update from model server received: " + fullUpdate.toString());
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
client.unsubscribe(subscriptionId);
```

The kind of message received depends on the operation. For an `update` call (`PUT` request on the model), the message is the new content of the model (`onFullUpdate`).  For an incremental update applied by a `PATCH` request with an edit command (see above), the message is the result of the command that was executed (`onIncrementalUpdate`). The command execution result consists of the original client command, an execution type (e.g., 'execute', 'undo', 'redo'), the affected objects from the executed command and any recorded changes.

## Contributing
All involved code must adhere to the provided codestyle and checkstyle settings.

### Eclipse IDE Setup

#### Requirements
- Please make sure your Eclipse workspace uses a JRE of Java 9 or higher.
- Install the Eclipse Checkstyle Plug-in via its update site `https://checkstyle.org/eclipse-cs/#!/install`.

#### Configure Checkstyle
To configure Checkstyle for the new project right click on the project, choose `Checkstyle > Configure project(s) from blueprint...` and select `org.eclipse.emfcloud.modelserver.common` as blueprint project.
Run `Checkstyle > Check Code with Checkstyle` to make sure Checkstyle is activated correctly.

#### Import Existing Projects
Import all maven projects via `File > Import... > Existing Maven Projects > Root directory: $REPO_LOCATION`.

Please also import the codestyle project via `File > Import... > Existing Projects into Workspace > Root directory: $REPO_LOCATION/releng > org.eclipse.emfcloud.modelserver.codestyle`.

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
