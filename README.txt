This is a skeleton application for the Twitter Sentiment Analysis exercise.

The main class is com.zuhlke.ta.web.Application. Run it, open http://localhost:4567/ and
on the web page you can enter a keyword in the search box. After some time, a tweet sentiment
timeline will appear in the results.

Have a look at the Application and JobService classes to understand the basic workflow.
JobService relies on an implementation of TweetService.

Your goal is to provide a distributed and highly scalable implementation of the TweetService.
If you want, you can replace any or every part of the application.
A naive implementations of TweetService is provided as a reference:
InMemoryTweetService. In this implementation we import the tweets
when the application starts. You will probably want to have a separate import process and comment out the
relative code in the Application class.
