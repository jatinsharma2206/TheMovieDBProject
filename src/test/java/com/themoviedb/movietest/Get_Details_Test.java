package com.themoviedb.movietest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

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

public class Get_Details_Test {
	Properties prop = new Properties();

	@BeforeTest
	public void getEnvironmentData() throws IOException {
		FileInputStream fis = new FileInputStream("./env.properties");
		prop.load(fis);
		
	}

	@Test(dataProvider = "MovieId")
	public void checkDataforMovieID(String id, String title) throws IOException {
		System.out.println("\n******Test case to validate movie title based on internal ID******");
		RestAssured.baseURI = prop.getProperty("Host");

		Response res = given().queryParam("api_key", prop.getProperty("KEY"))
				.queryParam("language", prop.getProperty("Language")).log().uri().get(ReusableMethods.addMovieID(id))
				.then().assertThat().statusCode(200).and().contentType(ContentType.JSON).and()
				.body("status", equalTo("Released")).and().extract().response();

		JsonPath js = FormatingResponse.rawToJson(res);
		String original_title = js.get("original_title");

		System.out.println("Get Details call to MovieDB Api for Original Title worked fine.");

		if (original_title.equals(title))
			System.out.println("Test Passed: Original Title = " + title + " for Movie id : " + id + ".\n");
		else
			System.out.println("Test Failed: Original Title retured was " + original_title
					+ " which is incorrect for Movie id " + id + ".\n");

	}

	@DataProvider(name = "MovieId")
	public Object[][] movieData() {
		Object[][] Movie2DArray = new Object[][] { { "2", "Ariel" }, { "3", "Varjoja paratiisissa" },
				{ "5", "Four Rooms" }, { "6", "Judgment Night" }, { "9", "Sonntag im August" },
				{ "12", "Finding Nemo" } };
		return Movie2DArray;

	}

}
