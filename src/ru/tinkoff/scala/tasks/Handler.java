package ru.tinkoff.scala.tasks;

public interface Handler {
    ApplicationStatusResponse performOperation(String id);
}
