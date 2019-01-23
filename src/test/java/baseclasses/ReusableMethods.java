package baseclasses;

import static io.restassured.RestAssured.given;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import java.util.Scanner;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ReusableMethods {

	static Properties prop = new Properties();

	// @BeforeTest
	// public void getData() throws IOException {
	// FileInputStream fis = new FileInputStream("./env.properties");
	// prop.load(fis);
	// }

	public class Constants {
		public static final String File = "./TheMovieDB.xlsx";
		public static final int count = 4;
	}

	public static String pathForMovie() {
		String res = "/3/search/movie";
		return res;
	}

	public static String getPayloadData() {

		String b = "{\"location\":{\"lat\":-38.383494,\"lng\":33.427362},\"accuracy\":25,\"name\":\"Frontline house\","
				+ "\"phone_number\":\"983 893 3937\",\"address\":\"29, side layout, cohen 09\",\"types\":[\"shoe park\"],"
				+ "\"website\":\"http://google.com/\",\"language\":\"French-IN\"}";
		return b;
	}

	@Test
	public static void getApiKey() throws IOException {

		System.out.println("Please Enter the Api key for Validation: ");
		Scanner scan = new Scanner(System.in);
		String apiKey = scan.nextLine();
		scan.close();
		String fullText = "\nApiKey=" + apiKey;

		Writer output;
		output = new BufferedWriter(new FileWriter("./env2.properties", true));
		output.append(fullText);
		output.close();

	}

	@Test
	public static String getSessionid() throws IOException {
		FileInputStream fis = new FileInputStream("./env.properties");
		prop.load(fis);
		RestAssured.baseURI = prop.getProperty("Host");

		Response res = given().queryParam("api_key", prop.getProperty("KEY")).when()
				.get(ReusableMethods.addGuestSession()).then().assertThat().statusCode(200).and()
				.contentType(ContentType.JSON).and().extract().response();

		JsonPath js = FormatingResponse.rawToJson(res);
		String guest_session_id = js.get("guest_session_id");
		System.out.println("\nNew guest session id : " + guest_session_id + "\n");
		return guest_session_id;
	}

	public static String addMovieID(String id) {
		String MoviePayload = "3/movie/" + id;
		return MoviePayload;
	}

	public static String addSearchBody() {
		String Searchbody = "/3/search/tv";
		return Searchbody;
	}

	public static String addGuestSession() {
		String GuestSession = "/3/authentication/guest_session/new";
		return GuestSession;
	}

}
