docker run --name "cont1" -it --rm -p 8081:8080 -v $PWD/build/libs:/app anapsix/alpine-java java -jar /app/vertxhaeval-1.0-SNAPSHOT-all.jar
