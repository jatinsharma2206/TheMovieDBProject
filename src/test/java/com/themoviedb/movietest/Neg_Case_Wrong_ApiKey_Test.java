package com.themoviedb.movietest;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import baseclasses.FormatingResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Neg_Case_Wrong_ApiKey_Test {
	Properties prop = new Properties();

	@BeforeTest
	public void getEnvironmentData() throws IOException {
		FileInputStream fis = new FileInputStream("./env.properties");
		prop.load(fis);
	}

	@Test(dataProvider = "FindMovie")
	public void Neg_Case_Wrong_ApiKey(String id, String WrongApiKey) throws IOException {
		System.out.println("\n********Test case to check that Invalid Key is not accepted by the Api********");
		RestAssured.baseURI = prop.getProperty("Host");
		System.out.println("");
		Response res = given().param("api_key", WrongApiKey).param("language", prop.getProperty("Language")).log().uri()
				.get("/3/movie/" + id + "/release_dates").then().assertThat().statusCode(401).and()
				.contentType(ContentType.JSON).and().and().extract().response();

		JsonPath js = FormatingResponse.rawToJson(res);
		js.prettyPrint();
		String status_message = js.get("status_message");
		String Expected_msg = "Invalid API key: You must be granted a valid key.";

		if (status_message.equals(Expected_msg))
			System.out.println("Test Passed: Invalid Key is not accepted. Status message = " + status_message + "\n");
		else
			System.out.println("Test Failed: Invalid Key was accepted \n");

	}

	@DataProvider(name = "FindMovie")
	public Object[][] movieData() {
		Object[][] Movie2DArray = new Object[][] { { "501", "3a640701a8d3621019" }, { "502", "234234234234" },
				{ "503", "BlackPanther" }, { "504", "S#$%#$%#$%#@#" }, { "505", "DEDWDAS#$#@$345345" }, { "506", "" } };
		return Movie2DArray;

	}

}
