# vertx-ha-eval

# Step1. compile and build
`./gradlew build && ./docker1.sh`

# Step2. add 2 more hosts to the cluster
Open two more terminals and run respectively:
`./docker2.sh`
`./docker3.sh`

# Step3. spawn an 'app'
Open the 4-th terminal and send the POST command (the port can be also 8081, 8082)
`curl -d "spawn -=={{MyApPliCatiOn-1}}==-" -XPOST http://localhost:8083`

...wait a bit and see that the app verticle prints hello every second

# Step4. kill the 'app'
First, we need to find in which container that application runs, dependent on your terminal window the docker instance corresponds to container cont1, cont2 or cont3.
Assuming the app runs in the cont2, let's kill it:
`docker kill --signal=SIGKILL cont1`

# Step5. see the docker instance is down, the 'app' should now be re-deployed to any of the other containers

