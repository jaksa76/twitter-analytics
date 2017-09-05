This is a skeleton application for the Twitter Sentiment Analysis exercise.

The main class is com.zuhlke.ta.web.Application. Run it, open http://localhost:4567/ and
on the web page you can enter a keyword in the search box. After some time, a tweet sentiment
timeline will appear in the results.

Have a look at the Application and JobService classes to understand the basic workflow.
JobService relies on an implementation of TweetService.

On starting the application a connection to the Twitter streaming API will be made in order to
start collecting new tweets. For this to work valid OAuth keys will need to be set in the
src/main/resources/twitter4j.properties file. In addition the geographical bounding box whaere tweets
will be collected from can be set in src/main/resources/config.properties, along with the number
of new tweets that will be collected before they are sent to the TweetStore implementation.

Your goal is to provide a distributed and highly scalable implementation of the TweetService.
If you want, you can replace any or every part of the application.
Two naive implementations of TweetService are provided as a reference:
InMemoryTweetService and MapDBTweetService. For both of these implementations we import the tweets
when the application starts. You will probably want to have a separate import process and comment out the
relative code in the Application class.
