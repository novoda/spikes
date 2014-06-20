LeanPlum is an AB testing library.

This spike shows an example of a Activity memory leak if you use it like the documentation shows.

Example of leaky code:

https://github.com/novoda/Spikes/blob/master/LeanPlumLeakSpike/app/src/main/java/blundell/leanplumleakspike/LeakActivity.java

Fix for the leak:

https://github.com/novoda/Spikes/blob/master/LeanPlumLeakSpike/app/src/main/java/blundell/leanplumleakspike/NoLeakActivity.java
