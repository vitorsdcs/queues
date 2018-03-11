# Job Queues

A queue that manages assignments of jobs to agents based on their skillsets.

## Using a Database

All processed events are stored in memory by default. If you wish to persist them in a database, checkout to "mongodb" branch. Note that this will make MongoDB an extra dependency for this project. MongoDB is currently the only supported database.

## Running

You must provide the path to a JSON file containing a list of events to be processed by the queue. A sample input file named "sample-input.json" is already included in this project.

```
lein run <input.json>
```

## Testing

You can run all the tests by running the following command:

```
lein test
```

## Dependencies

* Clojure 1.8.0
* Java >= 1.7.0
* Leiningen