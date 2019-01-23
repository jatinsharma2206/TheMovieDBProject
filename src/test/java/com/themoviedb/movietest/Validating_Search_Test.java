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

public class Validating_Search_Test {
	Properties prop = new Properties();

	@BeforeTest
	public void getEnvironmentData() throws IOException {
		FileInputStream fis = new FileInputStream("./env.properties");
		prop.load(fis);
	}

	@Test(dataProvider = "Search")
	public void validateSearch(String searchterm) throws IOException {
		
		System.out.println("\n********Test case to validate Search feature of moviedb api.********");

		RestAssured.baseURI = prop.getProperty("Host");

		Response res = given().param("api_key", prop.getProperty("KEY"))
				.queryParam("language", prop.getProperty("Language")).param("query", searchterm).log().uri()
				.get(ReusableMethods.addSearchBody()).then().assertThat().statusCode(200).and()
				.contentType(ContentType.JSON).and().extract().response();

		JsonPath js = FormatingResponse.rawToJson(res);
		String name = js.get("results[0].name");
		String original_name = js.get("results[0].original_name");
	     //	System.out.println("Original Name = " + original_name);

		System.out.println("Get Details call to MovieDB Api for Original Title worked fine.");

		if (name.equals(searchterm) & original_name.equals(searchterm))
			System.out.println("Test Passed: TV Show found with name = " + name + "\n");
		else
			System.out.println("Test Failed: TV Show found with different name = " + name + "\n");

	}

	@DataProvider(name = "Search")
	public Object[] searchData() {
		Object[] SearchTerm = new Object[] { "This Is Us", "The Typist", "A Million Little Things", "M*A*S*H",
				"8 x 45" };
		return SearchTerm;

	}

}
