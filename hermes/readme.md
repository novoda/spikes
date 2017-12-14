# Hermes (messenger of the gods)

Simple pub/sub implementation without tie to any framework.

## Proposition

A very basic way to propagate objects among consumers/subscribers. 

This came as a potential solution to a problem we had in the all-4 app. We have
a set of monitors that "observe" events from the video activity. In order to have them 
all grouped and avoid having multiple calls they all extend the same interface and then 
have no-ops for operations that are not wanted.

By using a pub/sub like this one it means we can handle only the events that we care 
about explicitly instead of ignoring the ones we don't want.

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
 - Add documentation
 - Add support for [Reactive Streams](http://www.reactive-streams.org/) (Rx, Java 9 Flow, etc)
 - Add support for [Coroutines](https://kotlinlang.org/docs/reference/coroutines.html) (Using Channels)
 - Add android demo
 - Add deployment
 - Potentially add support for Java below 8
 
