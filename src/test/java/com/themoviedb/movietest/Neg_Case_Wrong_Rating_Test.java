package com.themoviedb.movietest;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import baseclasses.ReusableMethods;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Neg_Case_Wrong_Rating_Test {
	Properties prop = new Properties();

	@BeforeTest
	public void getData() throws IOException {
		FileInputStream fis = new FileInputStream("./env.properties");
		prop.load(fis);	
	}

	@Test(dataProvider = "rating")
	public void Neg_Case_Wrong_Rating(String id, String rating) throws IOException {
		System.out.println("\n********Test case to verify that api give 400 response for incorrect rating.********");
		RestAssured.baseURI = prop.getProperty("Host");
		Response res2 = given().header("Content-Type", "application/json;charset=utf-8")
				.queryParam("api_key", prop.getProperty("KEY")).log().uri()
				.queryParam("guest_session_id", ReusableMethods.getSessionid()).body("{\"value\":" + rating + "}")
				.when().post("/3/movie/" + id + "/rating").then().assertThat().statusCode(400)
				.contentType(ContentType.JSON).extract().response();

		res2.print();
		System.out.println("Test Passed: Response 400 : Bad Request received for incorrect rating value. \n");

	}

	@DataProvider(name = "rating")
	public Object[][] ratingData() {
		Object[][] ratings = new Object[][] { { "3", "7.1" }, { "5", "11" }, { "12", "%#$" }, { "14", "5.4a" },
				{ "17", "7." } };
		return ratings;
	}
}
