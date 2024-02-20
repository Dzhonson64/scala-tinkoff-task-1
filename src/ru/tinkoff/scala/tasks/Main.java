package ru.tinkoff.scala.tasks;

public class Main {
    public static void main(String[] args) {

        Handler handler = new HandlerImpl(new ClientImpl());
        ApplicationStatusResponse applicationStatusResponse = handler.performOperation("test_id");
        System.out.println(applicationStatusResponse);
    }
}