This is a skeleton application for the Twitter Sentiment Analysis exercise.

The main classes are in com.zuhlke.ta.analysis, where there is a Master and a Worker implementation. The master
co-ordinates partitioning of the tweet data, stored in BigQuery. The worker(s) take the data partitions and run them
through the provided sentiment analysis algorithm. The calculated sentiments are inserted in batches into the specified
BigQuery dataset+table.

TODO:
* create a BigQuery dataset

* copy the tweet data from Google Storage to a BigQuery table using a variation of the following command:
bq load --max_bad_records=1000 --source_format=CSV --quote=“” -F “|” --replace --allow_jagged_rows [yourDataset].[yourTable]
gs://intalert-tweets/csv/tweets_dedup/part-*-of-00035 key:STRING,tweetId:INTEGER,content:STRING,user:STRING,
timestamp:TIMESTAMP,longitude:FLOAT,latitude:FLOAT,geoLocated:BOOLEAN,language:STRING,place:STRING,favourites:INTEGER,
sentiment:FLOAT

* create an "analysed" table

* create a "test" table for use in the WorkerTest test class

* provide a service-account.json file in the root of the project to give credentials for connecting to the BigQuery
datasets/tables - see https://cloud.google.com/docs/authentication/getting-started

* check the web application runs and is able to query your BigQuery data by running the Application class in com.zuhlke
.ta.web.Application

* Follow the TODOs in the Master and Worker classes (i.e. get the Master and Worker to talk to each other and the Worker
to process each partition it fetches from the Master)

* Create Docker images for the Master and Worker applications

* Use the Docker images to deploy to a kubernetes cluster and re-analyse the sentiment of the existing data (on a cloud
provider of your choosing - look at Google Container Engine, kops on AWS or Azure Container Service for good starting
points)

* (Optional) look into a more efficient / even partitioning mechanism

* Investigate scaling up/down the number of Workers - how do you stop the data processing before it completes