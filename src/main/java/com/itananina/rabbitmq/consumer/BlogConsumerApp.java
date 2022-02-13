package com.itananina.rabbitmq.consumer;

import com.rabbitmq.client.*;

import java.util.Scanner;

public class BlogConsumerApp {
    private static final String EXCHANGE_NAME = "topic_exchange";
    private static final String ROUTING = "itblog.";


    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String command = scanner.nextLine();
            String[] splitStr = command.trim().split("\\s+");

            switch (splitStr[0]) {
                case "set_topic":
                    channel.queueBind(queueName, EXCHANGE_NAME, ROUTING + splitStr[1]);
                    System.out.println(" [*] Waiting for messages with routing key (" + ROUTING + splitStr[1] + "):");
                    break;
                case "unset_topic":
                    channel.queueUnbind(queueName, EXCHANGE_NAME, ROUTING + splitStr[1]);
                    System.out.println(" [*] Stopped waiting for messages with routing key (" + ROUTING + splitStr[1] + "):");
                    break;
            }

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        }
        scanner.close();
    }
}
