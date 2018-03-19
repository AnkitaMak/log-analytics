# log-analytics
Spark streaming picks logs from a file in a directory and stream its content to Kafka along with schema of log. Further spark streaming consumer will pick contents.
Processesing is done on the contents and finally it is persisted into cassandra.
Using spark sql to further do windowing operations on persisted data.
