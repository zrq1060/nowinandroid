# Architecture Learning Journey
# 架构学习之旅

In this learning journey you will learn about the Now in Android app architecture: its layers, key classes and the interactions between them.

在本次学习之旅中，您将了解Now In Android应用程序架构：它的层、关键类以及它们之间的交互。

## Goals and requirements
## 目标和需求

The goals for the app architecture are:

应用程序架构的目标是：


*   Follow the [official architecture guidance](https://developer.android.com/jetpack/guide) as closely as possible.
*   尽可能严格地遵循[官方架构指南](https://developer.android.com/jetpack/guide)。
*   Easy for developers to understand, nothing too experimental.
*   易于开发人员理解，没有太实验性。
*   Support multiple developers working on the same codebase.
*   支持多个开发人员在同一代码库上工作。
*   Facilitate local and instrumented tests, both on the developer’s machine and using Continuous Integration (CI).
*   在开发人员的机器上和使用持续集成（CI）时，促进本地和仪器化测试。
*   Minimize build times.
*   最小化构建时间。


## Architecture overview
## 架构概述

The app architecture has three layers: a [data layer](https://developer.android.com/jetpack/guide/data-layer), a [domain layer](https://developer.android.com/jetpack/guide/domain-layer) and a [UI layer](https://developer.android.com/jetpack/guide/ui-layer).

应用架构有三层：数据层、域层和UI层。

<center>
<img src="images/architecture-1-overall.png" width="600px" alt="Diagram showing overall app architecture" />
</center>

> [!NOTE]  
> The official Android architecture is different from other architectures, such as "Clean Architecture". Concepts from other architectures may not apply here, or be applied in different ways. [More discussion here](https://github.com/android/nowinandroid/discussions/1273).
> 
> 官方的Android架构不同于其他架构，比如“Clean architecture”。来自其他体系结构的概念可能不适用于这里，或者以不同的方式应用。[这里有更多的讨论。](https://github.com/android/nowinandroid/discussions/1273).


The architecture follows a reactive programming model with [unidirectional data flow](https://developer.android.com/jetpack/guide/ui-layer#udf). With the data layer at the bottom, the key concepts are:

该体系结构遵循具有[单向数据流](https://developer.android.com/jetpack/guide/ui-layer#udf)的响应式编程模型。数据层位于最底层，关键概念如下：


*   Higher layers react to changes in lower layers.
*   高层对低层的变化作出反应。
*   Events flow down.
*   事件向下流动。
*   Data flows up.
*   数据向上流动。

The data flow is achieved using streams, implemented using [Kotlin Flows](https://developer.android.com/kotlin/flow).

数据流是使用流实现的，使用[Kotlin Flows](https://developer.android.com/kotlin/flow)实现。


### Example: Displaying news on the For You screen
### 示例：在For You屏幕上显示新闻

When the app is first run it will attempt to load a list of news resources from a remote server (when the `prod` build flavor is selected, `demo` builds will use local data). Once loaded, these are shown to the user based on the interests they choose.

当应用程序第一次运行时，它将尝试从远程服务器加载新闻资源列表（当选择prod构建风格时，demo构建将使用本地数据）。加载后，这些将根据用户选择的兴趣显示给他们。

The following diagram shows the events which occur and how data flows from the relevant objects to achieve this.

下图显示了发生的事件，以及数据如何从相关对象流出以实现此目的。

![Diagram showing how news resources are displayed on the For You screen](images/architecture-2-example.png "Diagram showing how news resources are displayed on the For You screen")


Here's what's happening in each step. The easiest way to find the associated code is to load the project into Android Studio and search for the text in the Code column (handy shortcut: tap <kbd>⇧ SHIFT</kbd> twice).

下面是每一步发生的情况。查找相关代码的最简单方法是将项目加载到Android Studio中，然后在“代码”列中搜索文本（方便的快捷方式：点击SHIFT两次）。

<table>
  <tr>
   <td><strong>Step</strong>
   </td>
   <td><strong>Description（描述）</strong>
   </td>
   <td><strong>Code </strong>
   </td>
  </tr>
  <tr>
   <td>1
   </td>
   <td>On app startup, a <a href="https://developer.android.com/topic/libraries/architecture/workmanager">WorkManager</a> job to sync all repositories is enqueued.

在应用程序启动时，同步所有存储库的WorkManager作业被排队。
   </td>
   <td><code>Sync.initialize</code>
   </td>
  </tr>
  <tr>
   <td>2
   </td>
   <td>The <code>ForYouViewModel</code> calls <code>GetUserNewsResourcesUseCase</code> to obtain a stream of news resources with their bookmarked/saved state. No items will be emitted into this stream until both the user and news repositories emit an item. While waiting, the feed state is set to <code>Loading</code>.

ForYouViewModel调用GetUserNewsResourcesUseCase来获取带有书签/保存状态的新闻资源流。在用户和新闻存储库都发出项之前，不会向此流发出任何项。在等待期间，feed状态被设置为Loading。
   </td>
   <td>Search for usages of <code>NewsFeedUiState.Loading</code>
   </td>
  </tr>
  <tr>
   <td>3
   </td>
   <td>The user data repository obtains a stream of <code>UserData</code> objects from a local data source backed by Proto DataStore.

用户数据存储库从Proto DataStore支持的本地数据源获取UserData对象流。
   </td>
   <td><code>NiaPreferencesDataSource.userData</code>
   </td>
  </tr>
  <tr>
   <td>4
   </td>
   <td>WorkManager executes the sync job which calls <code>OfflineFirstNewsRepository</code> to start synchronizing data with the remote data source.

WorkManager执行同步作业，该作业调用OfflineFirstNewsRepository开始与远程数据源同步数据。
   </td>
   <td><code>SyncWorker.doWork</code>
   </td>
  </tr>
  <tr>
   <td>5
   </td>
   <td><code>OfflineFirstNewsRepository</code> calls <code>RetrofitNiaNetwork</code> to execute the actual API request using <a href="https://square.github.io/retrofit/">Retrofit</a>.

OfflineFirstNewsRepository调用RetrofitNiaNetwork来使用Retrofit执行实际的API请求。

   </td>
   <td><code>OfflineFirstNewsRepository.syncWith</code>
   </td>
  </tr>
  <tr>
   <td>6
   </td>
   <td><code>RetrofitNiaNetwork</code> calls the REST API on the remote server.

RetrofitNiaNetwork调用远程服务器上的REST API。
   </td>
   <td><code>RetrofitNiaNetwork.getNewsResources</code>
   </td>
  </tr>
  <tr>
   <td>7
   </td>
   <td><code>RetrofitNiaNetwork</code> receives the network response from the remote server.

RetrofitNiaNetwork从远程服务器接收网络响应。
   </td>
   <td><code>RetrofitNiaNetwork.getNewsResources</code>
   </td>
  </tr>
  <tr>
   <td>8
   </td>
   <td><code>OfflineFirstNewsRepository</code> syncs the remote data with <code>NewsResourceDao</code> by inserting, updating or deleting data in a local <a href="https://developer.android.com/training/data-storage/room">Room database</a>.

OfflineFirstNewsRepository通过在本地Room数据库中插入、更新或删除数据，将远程数据与NewsResourceDao同步。
   </td>
   <td><code>OfflineFirstNewsRepository.syncWith</code>
   </td>
  </tr>
  <tr>
   <td>9
   </td>
   <td>When data changes in <code>NewsResourceDao</code> it is emitted into the news resources data stream (which is a <a href="https://developer.android.com/kotlin/flow">Flow</a>).

当NewsResourceDao中的数据发生变化时，它会被发送到新闻资源数据流（即Flow）中。
   </td>
   <td><code>NewsResourceDao.getNewsResources</code>
   </td>
  </tr>
  <tr>
   <td>10
   </td>
   <td><code>OfflineFirstNewsRepository</code> acts as an <a href="https://developer.android.com/kotlin/flow#modify">intermediate operator</a> on this stream, transforming the incoming <code>PopulatedNewsResource</code> (a database model, internal to the data layer) to the public <code>NewsResource</code> model which is consumed by other layers.

OfflineFirstNewsRepository充当该流的中间操作符，将传入的PopulatedNewsResource（数据层内部的数据库模型）转换为其他层使用的公共NewsResource模型。
   </td>
   <td><code>OfflineFirstNewsRepository.getNewsResources</code>
   </td>
  </tr>
  <tr>
   <td>11
   </td>
   <td><code>GetUserNewsResourcesUseCase</code> combines the list of news resources with the user data to emit a list of <code>UserNewsResource</code>s.  

GetUserNewsResourcesUseCase将新闻资源列表与用户数据结合起来，发出UserNewsResources列表。
   </td>
   <td><code>GetUserNewsResourcesUseCase.invoke</code>
   </td>
  </tr>
  <tr>
   <td>12
   </td>
   <td>When <code>ForYouViewModel</code> receives the saveable news resources it updates the feed state to <code>Success</code>.

当ForYouViewModel接收到可保存的新闻资源时，它将feed状态更新为Success。

  <code>ForYouScreen</code> then uses the saveable news resources in the state to render the screen.

  ForYouScreen然后使用状态中可保存的新闻资源来渲染屏幕
   </td>
   <td>Search for instances of <code>NewsFeedUiState.Success</code>
   </td>
  </tr>
</table>



## Data layer
## 数据层

The data layer is implemented as an offline-first source of app data and business logic. It is the source of truth for all data in the app.

数据层被实现为应用程序数据和业务逻辑的离线优先源。它是应用程序中所有数据的真实来源。


![Diagram showing the data layer architecture](images/architecture-3-data-layer.png "Diagram showing the data layer architecture")


Each repository has its own models. For example, the `TopicsRepository` has a `Topic` model and the `NewsRepository` has a `NewsResource` model.

每个存储库都有自己的模型。例如，TopicsRepository有一个Topic模型，NewsRepository有一个NewsResource模型。

Repositories are the public API for other layers, they provide the _only_ way to access the app data. The repositories typically offer one or more methods for reading and writing data.

存储库是其他层的公共API，它们提供了访问应用程序数据的唯一途径。存储库通常提供一种或多种读写数据的方法。


### Reading data
### 读取数据

Data is exposed as data streams. This means each client of the repository must be prepared to react to data changes. Data is not exposed as a snapshot (e.g. `getModel`) because there's no guarantee that it will still be valid by the time it is used.

数据以数据流的形式公开。这意味着存储库的每个客户端都必须准备好对数据更改做出反应。数据不会以快照（例如 getModel）的形式公开，因为不能保证它在使用时仍然有效。

Reads are performed from local storage as the source of truth, therefore errors are not expected when reading from `Repository` instances. However, errors may occur when trying to reconcile data in local storage with remote sources. For more on error reconciliation, check the data synchronization section below.

读取是从本地存储作为真实来源执行的，因此从Repository实例读取时不会出现错误。但是，在尝试使本地存储中的数据与远程数据源协调时，可能会出现错误。有关错误协调的更多信息，请查看下面的数据同步部分。

_Example: Read a list of topics_

_示例：阅读一个主题列表_

A list of Topics can be obtained by subscribing to `TopicsRepository::getTopics` flow which emits `List<Topic>`.

可以通过订阅发出 `List<Topic>` 的 `TopicsRepository::getTopics` 流来获取主题列表。

Whenever the list of topics changes (for example, when a new topic is added), the updated `List<Topic>` is emitted into the stream.

每当主题列表发生变化时（例如，添加新主题时），更新的 `List<Topic>` 就会发送到流中。

### Writing data
### 写数据

To write data, the repository provides suspend functions. It is up to the caller to ensure that their execution is suitably scoped.

为了写入数据，存储库提供了挂起函数。调用者需要确保他们的执行具有适当的范围。

_Example: Follow a topic_

_示例：关注主题_

Simply call `UserDataRepository.toggleFollowedTopicId` with the ID of the topic the user wishes to follow and `followed=true` to indicate that the topic should be followed (use `false` to unfollow a topic).

只需使用用户希望关注的主题的ID调用`UserDataRepository.toggleFollowedTopicId`，并使用`followed=true`表示应该关注该主题（使用`false`表示取消关注某个主题）。
### Data sources
### 数据源

A repository may depend on one or more data sources. For example, the `OfflineFirstTopicsRepository` depends on the following data sources:

存储库可能依赖于一个或多个数据源。例如，`OfflineFirstTopicsRepository`依赖于以下数据源：


<table>
  <tr>
   <td><strong>Name</strong>
   </td>
   <td><strong>Backed by（支持的）</strong>
   </td>
   <td><strong>Purpose（目的）</strong>
   </td>
  </tr>
  <tr>
   <td>TopicsDao
   </td>
   <td><a href="https://developer.android.com/training/data-storage/room">Room/SQLite</a>
   </td>
   <td>Persistent relational data associated with Topics

与主题关联的持久关系数据
   </td>
  </tr>
  <tr>
   <td>NiaPreferencesDataSource
   </td>
   <td><a href="https://developer.android.com/topic/libraries/architecture/datastore">Proto DataStore</a>
   </td>
   <td>Persistent unstructured data associated with user preferences, specifically which Topics the user is interested in. This is defined and modeled in a .proto file, using the protobuf syntax.

与用户首选项相关联的持久非结构化数据，特别是用户感兴趣的主题。这是使用protobuf语法在.proto文件中定义和建模的。
   </td>
  </tr>
  <tr>
   <td>NiaNetworkDataSource
   </td>
   <td>Remote API accessed using Retrofit
   </td>
   <td>Data for topics, provided through REST API endpoints as JSON.

主题数据，通过REST API端点以JSON形式提供。
   </td>
  </tr>
</table>



### Data synchronization
### 数据同步

Repositories are responsible for reconciling data in local storage with remote sources. Once data is obtained from a remote data source it is immediately written to local storage. The  updated data is emitted from local storage (Room) into the relevant data stream and received by any listening clients.

存储库负责协调本地存储与远程数据源中的数据。从远程数据源获得数据后，立即将其写入本地存储。更新后的数据从本地存储（Room）发送到相关数据流中，并由任何监听客户端接收。

This approach ensures that the read and write concerns of the app are separate and do not interfere with each other.

这种方法确保应用程序的读写问题是分开的，不会相互干扰。

In the case of errors during data synchronization, an exponential backoff strategy is employed. This is delegated to `WorkManager` via the `SyncWorker`, an implementation of the `Synchronizer` interface.

在数据同步过程中出现错误的情况下，采用指数回退策略。这是通过`SyncWorker`（`Synchronizer`接口的实现）委托给`WorkManager`的。

See the `OfflineFirstNewsRepository.syncWith` for an example of data synchronization.

有关数据同步的示例，请参阅`OfflineFirstNewsRepository.syncWith`。

## Domain layer
## 领域层
The [domain layer](https://developer.android.com/topic/architecture/domain-layer) contains use cases. These are classes which have a single invocable method (`operator fun invoke`) containing business logic.

领域层包含用例。这些类只有一个包含业务逻辑的可调用方法（`operator fun invoke`）。

These use cases are used to simplify and remove duplicate logic from ViewModels. They typically combine and transform data from repositories. 

这些用例用于简化和删除ViewModels中的重复逻辑。它们通常组合并转换来自存储库的数据。

For example, `GetUserNewsResourcesUseCase` combines a stream (implemented using `Flow`) of `NewsResource`s from a `NewsRepository` with a stream of `UserData` objects from a `UserDataRepository` to create a stream of `UserNewsResource`s. This stream is used by various ViewModels to display news resources on screen with their bookmarked state.  

例如，`GetUserNewsResourcesUseCase`将来自`NewsRepository`的新闻资源流（使用`Flow`实现）与来自`UserDataRepository`的`UserData`对象流组合在一起，以创建`UserNewsResources`流。这个流被各种ViewModel用来在屏幕上显示带有书签状态的新闻资源。

Notably, the domain layer in Now in Android _does not_ (for now) contain any use cases for event handling. Events are handled by the UI layer calling methods on repositories directly.

值得注意的是，Now in Android中的域层（目前）不包含任何事件处理的用例。事件由UI层直接调用存储库上的方法来处理。

## UI Layer
## UI层

The [UI layer](https://developer.android.com/topic/architecture/ui-layer) comprises:

UI层包括：


*   UI elements built using [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   使用[Jetpack Compose](https://developer.android.com/jetpack/compose)构建的UI元素    
*   [Android ViewModels](https://developer.android.com/topic/libraries/architecture/viewmodel)

The ViewModels receive streams of data from use cases and repositories, and transforms them into UI state. The UI elements reflect this state, and provide ways for the user to interact with the app. These interactions are passed as events to the ViewModel where they are processed.

ViewModel 从用例和存储库接收数据流，并将其转换为 UI 状态。UI 元素反映此状态，并为用户提供与应用交互的方式。这些交互作为事件传递到 ViewModel 并在那里进行处理。

![Diagram showing the UI layer architecture](images/architecture-4-ui-layer.png "Diagram showing the UI layer architecture")



### Modeling UI state
### UI状态建模

UI state is modeled as a sealed hierarchy using interfaces and immutable data classes. State objects are only ever emitted through the transform of data streams. This approach ensures that:

UI状态被建模为使用接口和不可变数据类的密封层次结构。状态对象只能通过数据流的转换发出。这种方法确保：



*   the UI state always represents the underlying app data - the app data is the source-of-truth.
*   UI 状态始终代表底层应用数据 - 应用数据是真实来源。
*   the UI elements handle all possible states.
*   UI 元素处理所有可能的状态。

**Example: News feed on For You screen**

**示例：For You 屏幕上的新闻提要**

The feed (a list) of news resources on the For You screen is modeled using `NewsFeedUiState`. This is a sealed interface which creates a hierarchy of two possible states:

For You屏幕上的新闻资源提要（列表）是使用NewsFeedUiState建模的。这是一个密封接口，它创建了两个可能状态的层次结构：


*   `Loading` indicates that the data is loading
*   `Loading` 表示数据正在加载
*   `Success` indicates that the data was loaded successfully. The Success state contains the list of news resources.
*   `Success` 表示数据加载成功。Success状态包含新闻资源列表。

The `feedState` is passed to the `ForYouScreen` composable, which handles both of these states.

feedState传递给ForYouScreen可组合组件，该组件处理这两种状态。

### Transforming streams into UI state
### 将流转换为UI状态

ViewModels receive streams of data as cold [flows](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/index.html) from one or more use cases or repositories. These are [combined](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/combine.html) together, or simply [mapped](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/map.html), to produce a single flow of UI state. This single flow is then converted to a hot flow using [stateIn](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/state-in.html). The conversion to a state flow enables UI elements to read the last known state from the flow.

ViewModel以冷流的形式从一个或多个用例或存储库接收数据流。将这些流组合在一起，或者简单地映射，以生成单个UI状态流。然后使用stateIn将此单个流转换为热流。转换为状态流使UI元素能够从流中读取最后一个已知状态。

**Example: Displaying followed topics**

**示例：显示关注的主题**

The `InterestsViewModel` exposes `uiState` as a `StateFlow<InterestsUiState>`. This hot flow is created by obtaining the cold flow of `List<FollowableTopic>` provided by `GetFollowableTopicsUseCase`. Each time a new list is emitted, it is converted into an `InterestsUiState.Interests` state which is exposed to the UI.

`InterestsViewModel`将`uiState`公开为`StateFlow<InterestsUiState>`。这个热流是通过获取`GetFollowableTopicsUseCase`提供的`List<FollowableTopic>`冷流来创建的。

每次发出新列表时，它都会转换为向UI公开的`InterestsUiState.Interests`状态。

### Processing user interactions
### 处理用户交互

User actions are communicated from UI elements to ViewModels using regular method invocations. These methods are passed to the UI elements as lambda expressions.

用户操作通过常规的方法调用从UI元素传递到viewmodel。这些方法作为lambda表达式传递给UI元素。

**Example: Following a topic**

**示例：关注主题**

The `InterestsScreen` takes a lambda expression named `followTopic` which is supplied from `InterestsViewModel.followTopic`. Each time the user taps on a topic to follow this method is called. The ViewModel then processes this action by informing the user data repository.

`InterestsScreen`接受一个名为`followTopic`的lambda表达式，该表达式由`InterestsViewModel.followTopic`提供。每次用户点击要关注的主题时，都会调用此方法。然后ViewModel通过通知用户数据存储库来处理此操作。

## Further reading
## 进一步阅读

[Guide to app architecture](https://developer.android.com/topic/architecture)

[Jetpack Compose](https://developer.android.com/jetpack/compose)
