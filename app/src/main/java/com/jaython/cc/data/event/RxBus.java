package com.jaython.cc.data.event;


import android.support.annotation.NonNull;

import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.ValidateUtil;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * time: 2015/8/21
 * description:Rx
 *
 * @author sunjianfei
 */
public class RxBus {
    private static final String TAG = RxBus.class.getSimpleName();
    private static RxBus instance;
    private Hashtable<String, Map<String, Subject>> mSubjects = new Hashtable<>();

    private RxBus() {
    }

    public static synchronized RxBus get() {
        if (null == instance) {
            instance = new RxBus();
        }
        return instance;
    }

    public <T> Observable<T> register(@NonNull String pageKey,
                                      @NonNull String eventKey,
                                      @NonNull Class<T> clazz) {
        Map<String, Subject> map = mSubjects.get(pageKey);
        if (null == map) {
            map = new HashMap<>();
            mSubjects.put(pageKey, map);
        }
        Subject<T, T> subject = map.get(eventKey);
        if (null == subject) {
            subject = PublishSubject.create();
            map.put(eventKey, subject);
        }
        return subject;
    }

    public void unregister(@NonNull String pageKey,
                           @NonNull String eventKey) {
        Map<String, Subject> map = mSubjects.get(pageKey);
        if (ValidateUtil.isValidate(map)) {
            map.remove(eventKey);
        }
    }

    public void unregister(@NonNull String pageKey) {
        mSubjects.remove(pageKey);
    }

    public void post(@NonNull Object content) {
        post(content.getClass().getName(), content);
    }

    public void post(@NonNull final Object tag, @NonNull final Object content) {
        //Map.Entry<String, Map<Object, Subject>
        Observable.from(mSubjects.entrySet())
                .map(Map.Entry::getValue)
                .filter(ValidateUtil::isValidate)
                .map(objectSubjectMap -> objectSubjectMap.get(tag))
                .filter(subject -> null != subject)
                .subscribe(subject -> subject.onNext(content), Logger::e);
    }
}
