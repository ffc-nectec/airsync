package ffc.airsync.api

import io.searchbox.client.JestClient
import io.searchbox.client.JestClientFactory
import io.searchbox.client.config.HttpClientConfig
import org.junit.Test

class ElasticTest {
    @Test
    fun test1(){//https://www.elastic.co/blog/found-java-clients-for-elasticsearch
        val factory: JestClientFactory = JestClientFactory()
        factory.setHttpClientConfig(HttpClientConfig.Builder("http://127.0.0.1:9200").multiThreaded(true).build())
        val client :JestClient=factory.`object`

    }

    @Test
    fun testInsert(){
        /*var client :TransportClient= PreBuiltTransportClient(Settings.EMPTY)
          .addTransportAddress(TransportAddress(InetAddress.getByName("127.0.0.1"),9300))

        var json:String = "{" +
        "\"user\":\"max\"," +
        "\"postDate\":\"2013-01-30\"," +
        "\"data\":\"hahaha out kjsdafkf\"" +
    "}"
        var response = client.prepareIndex("twitter", "tweet","32")
          .setSource(json, XContentType.JSON)
          .getGeoJsonHouse()

       printDebug(response.result)
        toString()*/
    }


    @Test
    fun testGet(){
        /*var client :TransportClient= PreBuiltTransportClient(Settings.EMPTY)
          .addTransportAddress(TransportAddress(InetAddress.getByName("127.0.0.1"),9300))

        var response = client.prepareGet("twitter", "tweet", "32").getGeoJsonHouse()
       printDebug(response.source.getValue("data"))*/
    }

    @Test
    fun testDelete(){

        /*var client :TransportClient= PreBuiltTransportClient(Settings.EMPTY)
          .addTransportAddress(TransportAddress(InetAddress.getByName("127.0.0.1"),9300))
        var response = client.prepareDelete("twitter", "tweet", "32").getGeoJsonHouse()
       printDebug(response)*/
    }
}
