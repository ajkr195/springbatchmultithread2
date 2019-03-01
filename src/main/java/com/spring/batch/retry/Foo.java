package com.spring.batch.retry;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
@Component
public class Foo {

    @Retryable(include=NumberFormatException.class, backoff = @Backoff(delay = 100, maxDelay = 101), maxAttempts = 5)
    public void out(String foo) {
        System.out.println(foo);
        if (foo.equals("foo")) {
            throw new NumberFormatException();
        }
        else {
            throw new IllegalStateException();
        }
    }

    @Recover
    public void connectionException(NumberFormatException e) {
        System.out.println("Retry failure");
    }
//    @Recover
//    public void connectionException(IllegalStateException e) {
//        System.out.println("Retry failure");
//    }
  

}

