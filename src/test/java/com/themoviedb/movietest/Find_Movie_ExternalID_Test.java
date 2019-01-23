package com.themoviedb.movietest;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import baseclasses.FormatingResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Find_Movie_ExternalID_Test {
	Properties prop = new Properties();
	
	//Test can be executed as TestNG and Test NG Suite.
	//Right click on testng.xml file adn run as testng suite to run all the tes cases.
	//Test can be executed as maven project as well.
	//Right click on project and run as Mvn Teat.

	@BeforeTest
	public void getEnvironmentData() throws IOException {
		//Update your api key in file env.properties if needed. I have placed my api key for now.
		FileInputStream fis = new FileInputStream("./env.properties");
		prop.load(fis);
	}


	@Test(dataProvider = "FindMovie")
	public void checkDataforMovieID(String id, String Savedtitle) throws IOException {
		System.out.println("\n*******Test case to validate movie title for imdb id (Exteranl)*******");
		RestAssured.baseURI = prop.getProperty("Host");

		Response res = given().param("api_key", prop.getProperty("KEY")).param("language", prop.getProperty("Language"))
				.param("external_source", "imdb_id").log().uri().get("/3/find/" + id).then().assertThat()
				.statusCode(200).and().contentType(ContentType.JSON).and().and().extract().response();

		JsonPath js = FormatingResponse.rawToJson(res);
		String title = js.get("movie_results[0].title");

		System.out.println("Get Details call to MovieDB Api for Original Title worked fine." + title);

		if (title.equals(Savedtitle))
			System.out.println("Test Passed: Title = " + title + " for External id : " + id + ".\n");
		else
			System.out.println("Test Failed: Original Title retured was " + title
					+ " which is incorrect for External id " + id + ".\n");

	}

	@DataProvider(name = "FindMovie")
	public Object[][] movieData() {
		Object[][] Movie2DArray = new Object[][] { { "tt1856101", "Blade Runner 2049" },
				{ "tt3501632", "Thor: Ragnarok" }, { "tt1825683", "Black Panther" },
				{ "tt2250912", "Spider-Man: Homecoming" }, { "tt2395427", "Avengers: Age of Ultron" },
				{ "tt0458339", "Captain America: The First Avenger" } };
		return Movie2DArray;

	}

}
