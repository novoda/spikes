# SQLiteAnalyzer

### Purpose
This spike aims to generate java utility code from given sql migrations.

### Mechanics
We generate an in-memory sqlite database, run the migrations on it and analyze the resulting tables to construct a DatabaseModel.
This model is then used to generate code.

### Libraries
We use [sqlite-jdbc](https://bitbucket.org/xerial/sqlite-jdbc) to create and analyze the database and 
[javawriter](https://github.com/square/javawriter) to generate java code. 
