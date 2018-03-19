name := "untitled"

version := "0.1"

scalaVersion := "2.10.4"
resolvers += "Apache HBase" at "https://repository.apache.org/content/repositories/releases"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-streaming-kafka" % "1.6.0",
  "org.apache.kafka" %% "kafka" % "0.8.2.1",
  "org.apache.spark" %% "spark-streaming" % "1.6.0",
  "org.apache.spark" %% "spark-core" % "1.6.0",
  "org.apache.spark" %% "spark-sql" % "1.6.0",
  /*"org.apache.spark" %% "spark-sql" % "2.0.0",*/
  "org.apache.spark" %% "spark-hive" % "1.6.0",
  "org.apache.hadoop" % "hadoop-client" % "1.2.1",
  "org.apache.hbase" % "hbase" % "1.2.0",
  "org.apache.hbase" % "hbase-client" % "1.2.0",
  "org.apache.hbase" % "hbase-common" % "1.2.0",
  "org.apache.phoenix" % "phoenix-core" % "4.13.1-HBase-1.3",
  "org.apache.hbase" % "hbase" % "0.90.2",
  "it.nerdammer.bigdata" %% "spark-hbase-connector" % "1.0.3",
  "com.datastax.spark" %% "spark-cassandra-connector" % "1.6.0" ,
  "com.google.guava" % "guava" % "16.0.1"


)