package com.example.learn;

import grpc.example.learn.HelloRequest;
import grpc.example.learn.HelloResponse;
import grpc.example.learn.HelloServiceGrpc;
import grpc.example.learn.Sentiment;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args) throws InterruptedException, SSLException {
        ManagedChannel localhost = NettyChannelBuilder
                .forAddress("localhost", 8443)
                .intercept(new AuthTokenProviderInterceptor())
                .sslContext(GrpcSslContexts.forClient().trustManager(new File("/home/shahin/IdeaProjects/grpc-client/src/main/resources/public.pem")).build())
                .build();
        HelloServiceGrpc.HelloServiceStub helloServiceStub = HelloServiceGrpc.newStub(localhost);
        HelloRequest helloRequest = HelloRequest
                .newBuilder().setAge(23)
                .setName("Hey")
                .addHobbies("My Hobby")
                .putBagOfTricks("new", "trick")
                .setSentiment(Sentiment.HAPPY).build();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        StreamObserver streamObserver = new StreamObserver<HelloResponse>() {
            @Override
            public void onNext(HelloResponse value) {
                System.out.println("Next. " + value.getGreeting());
            }

            @Override
            public void onError(Throwable t) {
                countDownLatch.countDown();
                System.out.println("Something bad happened");
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
                System.out.println("Completed");
            }
        };

        helloServiceStub.greet(helloRequest, streamObserver);
        countDownLatch.await();
    }
}
