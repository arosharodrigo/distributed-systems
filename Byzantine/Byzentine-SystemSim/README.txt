
Background
==========

1. There are participants/nodes as COMMANDER, NORMAL, FAULTY (roles)

2. There are only two types of commands - ATTACK, RETREAT

3. For simulation purpose only one commander is created.

4. Initially commander send selected command to all other normal and faulty participants/nodes.

5. Normal and Faulty nodes also send the command to all other participants(without commander and itself)
    which it received from its commander.

6. Normal and Faulty nodes put messages to their own message list which received from others.

7. If message list is fulfilled it will execute the command and clear the current received message.