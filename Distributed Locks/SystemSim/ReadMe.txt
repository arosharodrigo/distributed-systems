How to start
============
Start 'DecentralizedDistributedLocks' main() method.
System out will be printing according to the events.
    ex: "Acquired resource" content will be printed when some normal participant acquired a resource.


Introduction about simulation
=============================

1. System only has one resource named as 'r1'.
2. There are 4 coordinator participant on ports: 4445, 4446, 4447, 4448
3. There are 3 normal participant on ports: 4449, 4450, 4451.
4. These normal participants trying to access the resource random periodical time
5. If any participant success on acquiring the resource it will be use that resource for 1 second and release it, and
    inform coordinators also in order to release it from coordinator side.
6. If a participant partially get the grant permissions from some coordinators and not fulfil the requirement,
    (grantedCoordinators > allCoordinators/2), it will release all the acquired permissions and also inform
    coordinators to release those locks.