//package com.ontology.config;
//
//import org.apache.http.HttpHost;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Scope;
//
//@Configuration
//@ComponentScan
//public class ESConfig {
//    @Value("${elasticSearch.host}")
//    private String host;
//
//    @Value("${elasticSearch.port}")
//    private int port;
//
//    @Value("${elasticSearch.client.connectNum}")
//    private Integer connectNum;
//
//    @Value("${elasticSearch.client.connectPerRoute}")
//    private Integer connectPerRoute;
//
//    @Value("${elasticSearch.username}")
//    private String userName;
//    @Value("${elasticSearch.password}")
//    private String password;
//
//    @Bean
//    public HttpHost httpHost(){
//        return new HttpHost(host,port,"http");
//    }
//
////    @Bean(initMethod="init",destroyMethod="close")
////    public ESClientSpringFactory getFactory(){
////        return ESClientSpringFactory.
////                build(httpHost(), connectNum, connectPerRoute);
////    }
//
////    @Bean
////    @Scope("singleton")
////    public RestClient getRestClient(){
////        return getFactory().getClient();
////    }
//
//    @Bean("RHLClient")
//    public RestHighLevelClient restHighLevelClient() {
//        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials(userName, password));
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(httpHost())
//                        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)));
//        return client;
//    }
//
//}