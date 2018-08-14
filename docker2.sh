docker run --name "cont2" -it --rm -p 8082:8080 -v $PWD/build/libs:/app anapsix/alpine-java java -jar /app/vertxhaeval-1.0-SNAPSHOT-all.jar
