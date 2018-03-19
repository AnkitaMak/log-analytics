package utility

object FileSchema {
  def schema():String={
    val FILE_SCHEMA: String = "{" +
      "\"ip\":\"string\"," +
      "\"logParameter1\":\"string\"," +
      "\"logParameter2\":\"string\"," +
      "\"timestamp\":\"string\"," +
      "\"logParameter3\":\"string\"," +
      "\"requestType\":\"string\"," +
      "\"message\":\"string\","+
      "\"httpVersion\":\"string\","+
      "\"code\":\"string\","+
      "\"id\":\"string\""+
      " }"
    return FILE_SCHEMA
  }
}
