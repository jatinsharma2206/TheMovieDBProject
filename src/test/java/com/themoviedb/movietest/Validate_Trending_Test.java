package com.themoviedb.movietest;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import baseclasses.FormatingResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Validate_Trending_Test {
	Properties prop = new Properties();

	@BeforeTest
	public void getEnvironmentData() throws IOException {
		FileInputStream fis = new FileInputStream("./env.properties");
		prop.load(fis);
	}

	@Test
	public void Validate_Trending_feature() throws IOException {

		RestAssured.baseURI = prop.getProperty("Host");
		System.out.println("\n********Test case to validate Trending feature of the Api********");

		Response res = given().param("api_key", prop.getProperty("KEY")).log().uri().get("/3/trending/all/day").then()
				.assertThat().statusCode(200).and().contentType(ContentType.JSON).and().and().extract().response();

		JsonPath js = FormatingResponse.rawToJson(res);
		for (int i = 0; i <= 9; i++) {
			String title = js.get("results[" + i + "].title");
			String release_date = js.get("results[" + i + "].release_date");
			float popularity = js.get("results[" + i + "].popularity");
			System.out.println("\nTrending request worked fine. \nPosition " + (i + 1) + " = " + title + "\nRelease Date :"
					+ release_date + "\nPopularity :" + popularity);
		}

	}

}
