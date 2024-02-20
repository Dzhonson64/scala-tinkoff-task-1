package ru.tinkoff.scala.tasks;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HandlerImpl implements Handler {
    private final Client client;
    private final AtomicInteger retriesCounter;

    public HandlerImpl(Client client) {
        this.client = client;
        this.retriesCounter = new AtomicInteger(0);
    }

    @Override
    public ApplicationStatusResponse performOperation(String id) {

        try {
            Object o = getApplicationStatusCompletableFuture(id)
                    .orTimeout(15, TimeUnit.SECONDS)
                    .exceptionally(Response.Failure::new)
                    .get();
            return getApplicationStatusResponse((Response) o);
        } catch (InterruptedException | ExecutionException e) {
            return new ApplicationStatusResponse.Failure(null, retriesCounter.get());
        }


    }

    private  CompletableFuture<Object> getApplicationStatusCompletableFuture(String id){
        CompletableFuture<Response> applicationStatusFuture1
                = CompletableFuture.supplyAsync(() -> client.getApplicationStatus1(id));
        CompletableFuture<Response> applicationStatusFuture2
                = CompletableFuture.supplyAsync(() -> client.getApplicationStatus2(id));

        CompletableFuture<Object> completableFuture = CompletableFuture.anyOf(applicationStatusFuture1, applicationStatusFuture2);

        return completableFuture
                .exceptionally(throwable -> {
                    int retryNr = retriesCounter.get();
                    retriesCounter.set(retryNr + 1);
                    return getApplicationStatusCompletableFuture(id);
                });
    }


    private ApplicationStatusResponse getApplicationStatusResponse(Response response) {
        if (response instanceof Response.Success success) {
            return new ApplicationStatusResponse.Success(success.applicationId(), success.applicationStatus());
        }
        if (response instanceof Response.RetryAfter retryAfter) {
            return new ApplicationStatusResponse.Failure(retryAfter.delay(), retriesCounter.get());
        } else {
            return new ApplicationStatusResponse.Failure(null, retriesCounter.get());
        }

    }
}
