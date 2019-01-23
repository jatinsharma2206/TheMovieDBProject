package com.themoviedb.movietest;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import baseclasses.FormatingResponse;
import baseclasses.ReusableMethods;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Post_Call_Movie_Rating_Test {
	Properties prop = new Properties();

	@BeforeTest
	public void getData() throws IOException {
		FileInputStream fis = new FileInputStream("./env.properties");
		prop.load(fis);
	}

	@Test(dataProvider = "rating")
	public void Post_Call_Movie_Rating(String id, String rating) throws IOException {
		System.out.println("\n********Test case to validate post action for rating the movies********");
		RestAssured.baseURI = prop.getProperty("Host");
		Response res2 = given().header("Content-Type", "application/json;charset=utf-8")
				.queryParam("api_key", prop.getProperty("KEY")).log().uri()
				.queryParam("guest_session_id", ReusableMethods.getSessionid()).body("{\"value\":" + rating + "}")
				.when().post("/3/movie/" + id + "/rating").then().assertThat().statusCode(201)
				.contentType(ContentType.JSON).extract().response();

		JsonPath js2 = FormatingResponse.rawToJson(res2);
		String status_message = js2.get("status_message");
		if (status_message.equals("Success."))
			System.out.println("Status Message retured is Success");
		else
			System.out.println("Test Case Failed: Status Message is Not Success.");

	}

	@DataProvider(name = "rating")
	public Object[][] ratingData() {
		Object[][] ratings = new Object[][] { { "3", "7.5" }, { "5", "8.5" }, { "12", "9" }, { "14", "9.5" },
				{ "17", "7.5" } };
		return ratings;
	}
}
