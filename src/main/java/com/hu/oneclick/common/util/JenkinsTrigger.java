package com.hu.oneclick.common.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/* Trigger builds remotely in JenkinsCI with Java Example */
public class JenkinsTrigger {

  public static void main(String[] args) throws Exception {
    /* Prepend credentials to fix the Jenkins remote build 403 error */
    String JENKINS_URL = "http://user:gNouIkl2ca1t@54.226.181.123/job/RemoteTriggerExample/build?token=abc-123";
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(JENKINS_URL)).build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println(response.toString());
  }
}