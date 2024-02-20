package ru.tinkoff.scala.tasks;

public class ClientImpl implements Client{
    @Override
    public Response getApplicationStatus1(String id) {
        return new Response.Success("applicationStatus1", id);
    }

    @Override
    public Response getApplicationStatus2(String id) {
        return new Response.Success("applicationStatus2", id);
    }
}
