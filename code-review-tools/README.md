# Code review tools

This aims to serve as a repository for any tools and artifacts that are useful for reviewing code and analysing its effectiveness.

## Android

No tools currently available.

## iOS

- `uikit` (bash script) measures the number of files which import UIKit. This can be used as a (rough) indication that code is improperly coupled to UI libraries.
- `lines` (bash script) simply gives the number of lines per file. This can then be used to plot a histogram. The point of this is to quickly visualise large files and begin discussions around making them smaller. See an example histogram [here](https://docs.google.com/spreadsheets/d/1m-wIygVxOLJ2KciEokNgpOHeVgIcrM5FwXkoSB-rAyI/pubchart?oid=1766467159&format=interactive)
- `imports` (bash script) records the number of import statements per file. This can also be used to plot a histogram, and indicates code which is too tightly coupled to other code. See an example histogram [here](https://docs.google.com/spreadsheets/d/1m-wIygVxOLJ2KciEokNgpOHeVgIcrM5FwXkoSB-rAyI/pubchart?oid=472356679&format=interactive)

There are limitations to these scripts will would be addressed if we feel these are viable and useful:
- `uikit` and `imports` do not walk the tree of their dependencies. Therefore, if class A imports header B, and header B has an import of header C, it won't record that class A also implicitly imports C. This would also solve implicit imports in `.m` file from their `.h`
- there is no mechanism to include and exclude directories. Currently, `Pods/` is ignored but it would be nice to enable more customisations
- currently these are simple bash scripts. Perhaps it would be nice to make them more interactive or use a better scripting environment e.g. Gradle, with unified configuration options and entry points.

