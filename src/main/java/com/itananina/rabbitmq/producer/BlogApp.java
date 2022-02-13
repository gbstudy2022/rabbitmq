package com.itananina.rabbitmq.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class BlogApp {
    private static final String EXCHANGE_NAME = "topic_exchange";
    private final static String ROUTING = "itblog.";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); //подняла в докере c прописанными 5672 для проги, 15672 для админа

        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String message = scanner.nextLine();
                String[] splitStr = message.trim().split("\\s+");
                for (String el: splitStr) {
                    switch (el){
                        case "php":
                        case "java":
                            channel.basicPublish(EXCHANGE_NAME, ROUTING+el, null, message.getBytes("UTF-8"));
                            System.out.println(" [x] Sent '" + message + "'");
                            break;
                    }
                }
            }
            scanner.close();
        }

    }
}
