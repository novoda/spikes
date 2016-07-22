# Architecture Sample

This is a port of [the google todo app sample](https://github.com/googlesamples/android-architecture) following the principles of Unidirectional data flow.
The current state is a demo using some principles already used by us and some new ideas. The aim is to discuss and improve and use this as basis for learning.

The idea behind this project is to be a plateform for discussing architecture in an environement that is known by all of us.

If you have an idea you can issue a PR to discuss it, if you encountered a requirement that is not present in this demo then you can issue a PR to try and solve it and we can discuss all together on what are the different approaches.

The current solution already covers a bit more than the existing google repo.

Features:
  - Create todos
  - Mark a todo as completed
  - Mark a completed todo as active again
  - Clear all completed todos
  - Statistics screen showing the number of active vs completed todos.
  - Espresso tests covering a good part of UX flows
  - Sync with server ensuring the consistency of data (eg: delete all completed will work even if server has different data set)
  - Allow for concurrent modification without waiting for server reply
  - Fully unit tested presentation and domain layers
  
To come but easy to implement now:
  - Offline modification with eventual consistency
  - Error handling with retry and eventual consistency
