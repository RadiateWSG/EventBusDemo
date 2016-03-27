# EventBusDemo
基于EventBus库的Demo，分析、注解了EventBus中的代码，主要是为了理解和学习EventBus的原理。

# 简要说明

#### EventBus主要用来消息/事件的传递，却能实现组建之间的解耦。对比其他的消息传递：

- 使用监听器接口（Listener Interface）：
<br/>
1、一个实现了监听器接口的类，必须把它自身注册到它想要监听的类中去。这就使监听与被监听之间保持强关联关系，而且不利于单元测试。
<br/>
2、对比：而EventBus则起到了桥梁作用，想要监听什么对象/事件，在EventBus中去注册（register(Object)）；想要发送某事件的消息，使用EventBus去post（post(Event)）。这样便通过EventBus实现了监听者和被监听者的解耦。

- 使用广播（BroadCastReciver）：
<br/>
1、使用广播的代码臃肿（还有一种说法：它们内部的实现都需要IPC，单从传递效率上来讲，可能并不太适合上层的组件间通信），而且Intent传递数据时，在编译时并不能检查出所设的extra类型与收到时的类型一致。所以一个很常见的错误便是你或者你团队中的其他人改变了Intent所传递的数据，但忘记了对全部的接收器（receiver）进行更新。这种错误在编译时是无法被发现的，只有在运行时才会发现问题。
<br/>
2、对比：使用EventBus所传递的消息/事件，是我们自己定义的Event类，由于接受方和发送方都是与这些类实例打交道，所以所有的数据都可以进行类检查，这样任何由于类型不一致导致的错误都可以在编译期被发现。

#### EventBus可以用来处理异步并发消息。
Android中有很多执行异步操作的方法：AsyncTask、Loader、Executor。

- AsyncTask
<br/>
AsyncTask被设计成为一个工具类，要求我们尽量执行一些短小的操作，如果需要在线程中执行较长时间的任务，那么建议直接使用java.util.concurrent包中提供的各种API，如Executor、 ThreadPoolExecutor以及FutureTask。

- Loader
<br/>
Android 3.0引入了Loader，来解决Activity/Fragment生命周期的问题，但Loader仍然要维持Activity/Fragment中的callback接口LoaderManager.LoaderCallbacks（其中包含onCreateLoader()、onLoadFinished()和onLoaderReset方法），在onCreateLoader()中要实现自己的DataLoader，可以继承抽象类AsyncTaskLoader<T>拓展泛型并覆写其loadInBackground()方法。

- EventBus
<br/>
EventBus中内置了并发处理机制，即支持工作线程向UI线程发送消息/事件。
<br/>
1、onEvent（T event）对应ThreadMode.PostThread。该方法的执行和事件发送者在同一个线程中，适用于对是否在主线程执行无要求的情况，但post线程为主线程，则不能有耗时操作。
<br/>
2、onEventMainThread(T event)对应ThreadMode.MainThread。在主线程执行，不论事件从哪个线程发送过来。
<br/>
3、onEventBackgroundThread(T event)对应ThreadMode.BackgroundThread。如果发送事件的线程不是UI线程，则运行在该线程中。如果发送事件的是UI线程，则它运行在由EventBus维护的一个单独的线程中。多个事件会同步地被这个单独的后台线程所处理。适用于轻微耗时的操作，比如读写数据库。
<br/>
4、onEventAsync（T event）对应ThreadMode.Async。运行在单独的工作线程中，不论发送事件的线程是否为主线程。跟BackgroundThread不一样，该模式的所有线程是独立的，因此适用于长耗时操作，例如网络访问。
<br/>

这样通过EventBus就可以不用维护引用和回调接口，从而实现组件间的消息/事件传递。
<br/>
因为EventBus提供了多种ThreadMode，我们完全也可以使用EventBus来处理耗时操作。比如在打开一个Activity时，我们暂称为“A”，首先需要从服务端获取数据，我们首先注册EventBus；然后定义onEventMainThread(Response data)方法，接受请求到的数据并刷新界面；再然后，可以调用post(Request rq)来通知我们定义的RequestManger来访问网络并请求数据，当然RequestManger注册并定义了onEventAsync(Request rq)方法，它最后会调用EventBus.post(Response data)方法去通知A。
