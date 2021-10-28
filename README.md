## Author

Lê Hùng Thiện ([thien.fun](https://thien.fun))

## Introduction

> Conflict-Free Replicated Data Types (CRDTs) are data structures that power real-time collaborative applications in distributed systems. CRDTs can be replicated across systems, they can be updated independently and concurrently without coordination between the replicas, and it is always mathematically possible to resolve inconsistencies that might result.

## Requirement

> Study LWW-Element-Set, Graph , and implement a state-based LWW-Element-Graph with test cases. The graph must contain functionalities to add a vertex/edge, remove a vertex/edge, check if a vertex is in the graph, query for all vertices connected to a vertex, find any path between two vertices, and merge with concurrent changes from other graph/replica. Test cases should be clearly written and document what aspect of CRDT they test.

## How to run

From command line, cd to project folder, ex:

```shell
cd lww-element-graph/
```

To build:

```shell
./gradlew clean build
```

To run test:

```shell
./gradlew test
```

To run as a JVM application:

```shell
./gradlew run
```

## References

* [https://en.wikipedia.org/wiki/Conflict-free_replicated_data_type](https://en.wikipedia.org/wiki/Conflict-free_replicated_data_type)
* [https://github.com/pfrazee/crdt_notes](https://github.com/pfrazee/crdt_notes)
* [https://hal.inria.fr/inria-00555588/PDF/techreport.pdf](https://hal.inria.fr/inria-00555588/PDF/techreport.pdf)
* [https://www.baeldung.com/cs/simple-paths-between-two-vertices](https://www.baeldung.com/cs/simple-paths-between-two-vertices)
