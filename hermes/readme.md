# Hermes (messenger of the gods)

Simple pub/sub implementation without tie to any framework.

## Proposition

A very basic way to propagate objects among consumers/subscribers. 

## Usage (Java)

### Using lambdas
```
Hermes hermes = new Hermes();

hermes.register(Event.class, event -> log("New event: " + event));

hemes.track(new Event("some value"));
```

### Using a consumer implementation

```
hermes.register(Event.class, new Consumer<Event>() {
  public void consume(Event event) {
      log("New event: " + event)
  }
});
```

### Multiple consumers

```
hermes.register(Event.class, new ConsumerA(), new ConsumerB());
```

## Usage (Kotlin)

### Using lambdas
```
val hermes = Hermes()

hermes.register { event: String -> log("Event logged: $event") }

//or

hermes.register<String> { log("Event logged: $it") }

hemes.track(new Event("some value"));
```

### Using consumer implementations
```
hermes.register(SomeConsumer())
```

### Multiple lambdas
```
hermes..register<String>(
   { log("Event logged once more: $it") },
   { log("Event logged and another one: $it") }
)
```

## TODOs

 - Add tests
 - Add support for [Reactive Streams](http://www.reactive-streams.org/) (Rx, Java 9 Flow, etc)
 - Add support for [Coroutines](https://kotlinlang.org/docs/reference/coroutines.html) (Using Channels)
 - Add android demo
 - Potentially add support for Java below 8
 
