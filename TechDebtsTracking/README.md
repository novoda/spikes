This is a project to introducte `@TechDebt` annotations into our source code.

`@TechDebt` is an annotation which defines 3 concepts we considered worth to take into account when creating a `TechDebt`:
* `Description`: A short description of the issue.
* `Size`: One of `XSMALL`, `SMALL`, `MEDIUM`, `LARGE`, `XLARGE`.
* `link`: Where to find more info about the issue. Can be either a link to `JIRA` or `Github Issues`. 

### Why an annotation? ###

There are mainly two reasons:

1. The Tech Debts are created and fixed in code, so it makes sense to document them as you code. If when creating a tech debt you are jumping into `JIRA` or `Github` then you are simply doing context switching.
2. The annotation allow us to include a bit of both `syntactic sugar` and `syntactic salt`. `Syntactic sugar` because it adds some nice semantic to the piece of information being dropped. `Syntactic salt` because it will force us to think about this important factors of a `Tech Debt`. `Description`, `Size`, ...

### Yeah but... why is this different from a `TODO`? 

Again, the syntactic reaons **PLUS** the annotation is accompanied by an annotation procesor which in case of finding one of those will output a warning in the command line, something like this:

```
/path/YourClass.java:10: warning: TechDebt detected!!!
public class YourClass {
       ^
  Description: We need to solve this because bla and ble.
  Size: LARGE
  Link: http://link-to-github/
```

**ALSO** we can easily create a custom parser for the Jenkins Warnings plugin to track down this particular type of warning, this allow to define thresholds in the same way you can do for `PMD`, `Findbugs`, `Checkstyle`.

### Why is this not a custom `PMD` rule?

Because the intention of `PMD` is to find common programming flaws, I found `Tech Debts` sensibly different. 

I think this separation of concerns is important; you don't really want to lose visibility of your `Tech Debts` by hiding them between `PMD` warnings. Also because your team and you might decide to have a different strategy in regards on when to tackle `Tech Debts`. Sometimes you create a `Tech Debt` and want to give it time enough to see how the code evolves, realise about better ways of solving your problem, etc.

### You did talk about a Custom Parser for Jenkins Warning Plugin. Can you ellaborate? 

Yes I can. 

The plugin is this one: `https://wiki.jenkins-ci.org/display/JENKINS/Warnings+Plugin`. If you already have it installed then congratulations, you are almost done.

Once it is installed then you can simply go into `Manage Jenkins` -> `Configure System` and scroll down a bit till you get to `Compiler Warnings`. There simply create a custom parser:

The regular expression that matches the warnings is: `\s*\n*(\/.*):(\d+):(.*):\n*.*\n.*\n.*\n\s*Description:(.*\n)\s*Size:(.*\n)\s*Link:(.*\n)`

The parsing programme is:
```
import hudson.plugins.warnings.parser.Warning

String fileName = matcher.group(1)
String lineNumber = matcher.group(2)
String description = matcher.group(4)
String size = matcher.group(5)
String link = matcher.group(6)

String message =  "["+description + "] \n["+ link + "]" 


return new Warning(fileName, Integer.parseInt(lineNumber), size, "Tech Debt", message);
```

This will provide to Jenkins all the info it needs in order to keep the `Tech Debts` into track via charts, etc.

**Finally** make sure in your project configuration you have added a `Post Build Action` of the type `scan for compiler warnings` where you select your previously created parser. 



