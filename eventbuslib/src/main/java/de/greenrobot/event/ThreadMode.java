/*
 * Copyright (C) 2012 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.event;

/**
 * Each event handler method has a thread mode, which determines in which thread the method is to be called by EventBus.
 * EventBus takes care of threading independently from the posting thread.
 * 
 * @see EventBus#register(Object)
 * @author Markus
 */
public enum ThreadMode {
    /**
     * Subscriber will be called in the same thread, which is posting the event. This is the default. Event delivery
     * implies the least overhead because it avoids thread switching completely. Thus this is the recommended mode for
     * simple tasks that are known to complete is a very short time without requiring the main thread. Event handlers
     * using this mode must return quickly to avoid blocking the posting thread, which may be the main thread.
     */
    /**
     * @mark
     * 默认ThreadMode，Post操作的线程和订阅者事件响应方法在同一个线程。
     * 当该线程为主线程时，响应方法中不能有耗时操作，否则有卡主线程的风险。
     *
     * 适用场景：对于是否在主线程执行无要求，但若 Post 线程为主线程，不能耗时的操作。
     */
    PostThread,

    /**
     * Subscriber will be called in Android's main thread (sometimes referred to as UI thread). If the posting thread is
     * the main thread, event handler methods will be called directly. Event handlers using this mode must return
     * quickly to avoid blocking the main thread.
     */
    /**
     * @mark
     * 在主线程执行响应方法，因此方法中不能有耗时操作。
     * 如果发布线程就是主线程，则直接调用订阅者的事件响应方法；否则通过主线程的Handler发送消息在主线程处理“订阅者的事件响应函数”
     * @see EventBus#postToSubscription(Subscription, Object, boolean)
     *
     * 适用场景：必须在主线程执行的操作。
     */
    MainThread,

    /**
     * Subscriber will be called in a background thread. If posting thread is not the main thread, event handler methods
     * will be called directly in the posting thread. If the posting thread is the main thread, EventBus uses a single
     * background thread, that will deliver all its events sequentially. Event handlers using this mode should try to
     * return quickly to avoid blocking the background thread.
     */
    /**
     * @mark
     * 响应方法在后台线程中执行。
     * 1、如果发布线程不是主线程，则直接执行订阅者的事件响应函数；
     * 2、如果发布线程是主线程，使用唯一的后台线程去处理，由于后台线程是唯一的，当事件超过一个，它们会被放在队列中依次执行，
     *    最好不要有重度耗时操作或太频繁的轻度耗时操作，以造成其他操作等待。
     *
     * 适用场景：操作轻微耗时且不会过于频繁，即一般的耗时操作都可以放在这里。
     */
    BackgroundThread,

    /**
     * Event handler methods are called in a separate thread. This is always independent from the posting thread and the
     * main thread. Posting events never wait for event handler methods using this mode. Event handler methods should
     * use this mode if their execution might take some time, e.g. for network access. Avoid triggering a large number
     * of long running asynchronous handler methods at the same time to limit the number of concurrent threads. EventBus
     * uses a thread pool to efficiently reuse threads from completed asynchronous event handler notifications.
     */
    /**
     * @mark
     * 不论发布线程是否为主线程，都使用一个空闲线程来处理。和BackgroundThread不同的是，Async类的所有线程是相互独立的，因此不会出现卡线程的问题。
     *
     * 适用场景：长耗时操作，例如网络访问。
     */
    Async
}