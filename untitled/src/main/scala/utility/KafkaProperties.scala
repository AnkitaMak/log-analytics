package utility

import java.util.{Properties, UUID}

import kafka.message.DefaultCompressionCodec

object KafkaProperties {
  private val props = new Properties()

  def properties():Properties={
    props.put("compression.codec", DefaultCompressionCodec.codec.toString)
    props.put("producer.type", "sync")
    props.put("metadata.broker.list", "quickstart.cloudera:9092")
    props.put("message.send.max.retries", "5")
    props.put("request.required.acks", "-1")
    props.put("serializer.class", "kafka.serializer.StringEncoder")
    props.put("client.id", UUID.randomUUID().toString())
    props.put("bootstrap.servers","quickstart.cloudera:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    return props
  }


}
