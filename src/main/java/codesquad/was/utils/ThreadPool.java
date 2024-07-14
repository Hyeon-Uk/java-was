package codesquad.was.utils;

import codesquad.framework.coffee.annotation.Coffee;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Coffee
public class ThreadPool {
    private final ExecutorService executorService;
    public ThreadPool(){
        this.executorService = Executors.newFixedThreadPool(50);
    }

    public void execute(Runnable runnable){
        executorService.execute(runnable);
    }
}
