# Job Queues

Here at Nubank we have a large Customer Experience team, focusing on delivering high quality, friendly support to our
customers. To ensure the efficiency and success of this operation, we organize the work to be done in job queues.

In this exercise you're tasked with developing a simplified version of a job queue. The important entities in this
domain are *jobs* and *agents*.

A *job* is any task that needs to get done. It has a unique id, a type - which denotes the category of that job -, and
an urgency (boolean) flag - indicating whether that job has a high priority.

An *agent* is someone that performs a job. They also have a unique id and two disjoint skill sets: primary and
secondary. Skill sets are a simple list of job types that an agent is allowed to perform.

The core operation of a job queue is the dequeue function, which, given a pool of jobs to be done and agent's job
request, and a set of priority rules, returns the fittest job to be performed by that agent. Your first task is to
implement a dequeue function that abides to these rules:

- You can assume the list of jobs passed in is ordered by the time the they have entered the system.
- Jobs that arrived first should be assigned first, unless it has a "urgent" flag, in which case it has a higher
  priority.
- A job cannot be assigned to more than one agent at a time.
- An agent is not handed a job whose type is not among its skill sets.
- An agent only receives a job whose type is contained among its secondary skill set if no job from its primary
  skill set is available.

A job is considered to be done when the agent it was assigned to requests a new job.

Attached are two files: sample-input.json and sample-output.json. Your program should be able to take the
contents of the sample-input.json file via stdin and produce the contents of sample-output.json on stdout.

This is actually a problem we've already solved here, and we're giving you a chance to present a solution. We will
evaluate your code in a similar way that we usually evaluate code that we send to production: as we rely heavily on
automated tests and our CI tool to ship code to production multiple times per day, having tests that make sure your code
works is a must.

Also, pay attention to code organization and make sure it is readable and clean.

You should deliver a git repository with your code and a short README file outlining the solution and explaining how to
build and run the code, we just ask that you keep your repository private (GitLab and BitBucket offer free private
repositories). You should deliver your code in a functional programming language — Clojure, Haskell, Elixir and Scala
are acceptable.

We also consider that this might be your first tackle on a functional language, so it's ok if your code is not
idiomatic, but do try to program in a functional style. Feel free to ask any questions, but please note that we won't be
able to give you feedback about your code before your deliver. However, we're more than willing to help you
understanding the domain or picking a library, for instance.

Lastly, there is no need to rush with the solution: delivering your exercise earlier than the due date is not a criteria
we take into account when evaluating the exercise: so if you finish earlier than that, please take some time to see what
you could improve. Also, if you think the timeframe may not be enough by any reason, don't hesitate to ask for more
time.

## Usage

FIXME

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
