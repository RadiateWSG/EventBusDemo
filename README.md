# EventBusDemo
基于EventBus库的Demo，分析、注解了EventBus中的代码，主要是为了理解和学习EventBus的原理。

# 简要说明
EventBus主要用来消息/事件的传递，却能实现组建之间的解耦。对比其他的消息传递：

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
<br/>


EventBus可以用来处理异步并发消息。
Android中有很多执行异步操作的方法：AsyncTask、Loader、Executor。
- AsyncTask
<br/>
AsyncTask被设计成为一个工具类，要求我们尽量执行一些短小的操作，如果需要在线程中执行较长时间的任务，那么建议直接使用java.util.concurrent包中提供的各种API，如Executor、 ThreadPoolExecutor以及FutureTask。
- Loader
<br/>
Android 3.0引入了Loader，来解决Activity/Fragment生命周期的问题，但Loader仍然要维持Activity/Fragment中的callback接口LoaderManager.LoaderCallbacks（其中包含onCreateLoader()、onLoadFinished()和onLoaderReset方法），在onCreateLoader()中要实现自己的DataLoader，可以继承抽象类AsyncTaskLoader<T>拓展泛型并覆写其loadInBackground()方法。
