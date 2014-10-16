LeanPlum is an AB testing library.

This spike shows an example of how to retrieve AB variables from Leanplum. 

The Leanplum variables are injected into the `AyBe` class by using annotations. In order Leanplum to know that it should inject the variables there, you should call `Parser.parseVariablesForClasses(AyBe.class)`

When the variables are received from Leanplum, the `onParamsLoaded()` method is fired. 