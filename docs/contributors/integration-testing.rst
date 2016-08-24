Acceptance Testing
==================

To improve confidence in the system, there are several containers that will execute a range of commands on tools that would use this tool (pip, maven etc) and then confirm the output.

Building the Containers
-----------------------

As an example, let's build the Maven acceptance test container, called hangar-test-maven. 

1. Navigate to */etc/docker/hangar-test-maven* within the project
2. Run *docker build -t hangar-test-maven .* (note the dot to specify the local directory)

With the container built, you would want to specify the IP and Port of the instance of Hangar you want to test against.

.. code::

  docker@default:~$ docker run --rm -e HANGAR_IP='10.0.2.2' -e HANGAR_PORT='8080' hangar-test-maven
  Hangar Integration Test - Maven
  -------------------------------
  Scenario 1 - Package Snapshot... [OK - 76s]
  ---- System Tests
  1..1
  ok 1 Compiled target jar exists
  Scenario 2 - Deploy Snapshot.... [OK - 8s]
  1..1
  ok 1 Compiled target jar exists
  Scenario 3 - Deploy Release.... [OK - 6s]
  1..1
  ok 1 Compiled target jar exists
  Scenario 4 - Get custom artifacts [OK - 76s]
 
.. note:: 
  
  In the instance above, we're testing this from within docker-machine running on Windows using VirtualBox to a instance of Hangar running within the Eclipse IDE, local to my machine. 
  To do this, we pass in the Gateway IP which is normally 10.0.2.2 - which will route this traffic to be localhost on the parent instance. Handy!