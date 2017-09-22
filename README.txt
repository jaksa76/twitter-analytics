This solution uses Amazon's Athena and AthenaJdbc client to get information about the tweets.
All the complexity is pushed into Athena's sql query.

Steps to run the app:
1. change the athenaCredentials.properties with your accessKey and secretKey
2. run the maven command which will install the athena jdbc jar from local lib
    mvn install:install-file -Dfile=./libs/AthenaJDBC41-1.1.0.jar -DgroupId=com.amazonaws -DartifactId=athena.jdbc41 -Dversion=1.1.0 -Dpackaging=jar -DgeneratePom=true
3. run the Application.java and open browser @ http://localhost:4567
4. search for tweets