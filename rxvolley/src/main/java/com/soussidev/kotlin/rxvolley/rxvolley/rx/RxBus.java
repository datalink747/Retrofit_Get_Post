package com.soussidev.kotlin.rxvolley.rxvolley.rx;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Soussi on 08/10/2017.
 */

public class RxBus {

    private RxBus() {
    }

    private static volatile RxBus mInstance;

    public static RxBus getDefault() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    private final PublishSubject<Object> bus = PublishSubject.create();

    public void post(Object event) {
        if (event instanceof Result) {
            if (((Result) event).isSuccess()) {
                bus.onNext(event);
            } else {
                bus.onError(((Result) event).error);
            }
        }
    }

    public <T> Observable<T> take(final Class<T> eventType) {
        return bus.toSerialized().cast(eventType);
    }

}
