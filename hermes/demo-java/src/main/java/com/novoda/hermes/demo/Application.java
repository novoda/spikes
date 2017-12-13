package com.novoda.hermes.demo;

import com.novoda.hermes.Broker;
import com.novoda.hermes.Consumer;
import com.novoda.hermes.Hermes;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Application {

    private final static Hermes hermes = new Hermes()
        .register(Broker.broker(String.class, event -> log("Event logged: " + event)))
        .register(Broker.broker(ExpectedValue.class, new MeConsumer(), new NovodaConsumer(), new MurrayConsumer()));

    private static void log(String message) {
        System.out.println(message);
    }

    public static void main(String... args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            if ("exit".equals(input)) {
                log("Bye, bye!");
                System.exit(0);
            } else {
                hermes.track(input);
                ExpectedValue expectedValue = ExpectedValue.valueFor(input);
                hermes.track(expectedValue);
            }
        }
    }

    private enum ExpectedValue {
        HERMES("hermes"),
        NOVODA("novoda"),
        MURRAY("murray"),
        NONE("");

        private final String name;

        ExpectedValue(String name) {
            this.name = name;
        }

        public static ExpectedValue valueFor(String candidate) {
            if (candidate.isEmpty()) {
                return NONE;
            } else {
                return Arrays.stream(values())
                    .filter((v) -> v.name.equals(candidate))
                    .findFirst()
                    .orElse(NONE);
            }
        }

    }

    private static class MurrayConsumer implements Consumer<ExpectedValue> {

        private Random random = new Random();

        @Override
        public void consume(ExpectedValue event) {
            if (event == ExpectedValue.MURRAY) {
                int width = random.nextInt(1000);
                int height = random.nextInt(1000);
                String url = "http://www.fillmurray.com/" + width + "/" + height;
                log("Have a murray: " + url);
            }
        }
    }

    private static class NovodaConsumer implements Consumer<ExpectedValue> {

        @Override
        public void consume(ExpectedValue event) {
            if (event == ExpectedValue.NOVODA) {
                log("That's a cool place!");
            }
        }

    }

    private static class MeConsumer implements Consumer<ExpectedValue> {

        @Override
        public void consume(ExpectedValue event) {
            if (event == ExpectedValue.HERMES) {
                log("That's me!!!");
            }
        }

    }

}
