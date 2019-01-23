package com.themoviedb.movietest;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import baseclasses.ExcelUtility_SingleDataSet;
import baseclasses.FormatingResponse;
import baseclasses.ReusableMethods;
import baseclasses.ReusableMethods.Constants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Get_Post_Delete_Rating_Test {
	Properties prop = new Properties();
	String sheetname = "Data";

	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {
		ExcelUtility_SingleDataSet.setExcelFile(Constants.File, sheetname);

	}

	@BeforeTest
	public void getEnvironmentData() throws Exception {
		FileInputStream fis = new FileInputStream("./env.properties");
		prop.load(fis);
	}

	@SuppressWarnings("resource")
	@Test
	public void validateSearch() throws Exception {
		XSSFWorkbook ExcelWBook;
		XSSFSheet ExcelWSheet;
		XSSFCell cell;
		String path = "./TheMovieDB.xlsx";
		FileInputStream ExcelFile = new FileInputStream(path);
		ExcelWBook = new XSSFWorkbook(ExcelFile);
		ExcelWSheet = ExcelWBook.getSheet("Data");

		String guest_session_id = ReusableMethods.getSessionid();
		System.out.println("\n********Test case to validate Get, Post and Delete functions for ratings********");
		for (int i = 0; i <= ExcelWSheet.getLastRowNum(); i++) {
			RestAssured.baseURI = prop.getProperty("Host");
			cell = ExcelWSheet.getRow(i).getCell(0);
			String id = cell.getStringCellValue();

			Response res = given().param("api_key", prop.getProperty("KEY"))
					.queryParam("language", prop.getProperty("Language")).get("/3/tv/" + id).then().assertThat()
					.statusCode(200).and().contentType(ContentType.JSON).and().extract().response();

			JsonPath js = FormatingResponse.rawToJson(res);
			String name = js.get("name");
			cell = ExcelWSheet.getRow(i).getCell(1);
			String Showname = cell.getStringCellValue();
			if (name.equals(Showname)) {
				System.out.println("\nTest case Passed: Showname = " + name + ". When Show ID = " + id);
				String timeStamp = new SimpleDateFormat("EEE, d MMM yyyy h:mm:ss a").format(new Date());
				ExcelUtility_SingleDataSet.setCellData("Test Case Passed", i, 3);
				ExcelUtility_SingleDataSet.setCellData(timeStamp, i, 4);
			} else {
				System.out
						.println("\nTest case Failed: Showname = " + name + " is not Not matching the Show ID = " + id);
				String timeStamp = new SimpleDateFormat("EEE, d MMM yyyy h:mm:ss a").format(new Date());
				ExcelUtility_SingleDataSet.setCellData("Test Case Failed", i, 3);
				ExcelUtility_SingleDataSet.setCellData(timeStamp, i, 4);
			}

			cell = ExcelWSheet.getRow(i).getCell(2);
			String rating = cell.getStringCellValue();
			RestAssured.baseURI = prop.getProperty("Host");
			Response res2 = given().header("Content-Type", "application/json;charset=utf-8")
					.queryParam("api_key", prop.getProperty("KEY")).log().uri()
					.queryParam("guest_session_id", guest_session_id).body("{\"value\":" + rating + "}").when()
					.post("/3/tv/" + id + "/rating").then().assertThat().statusCode(201).contentType(ContentType.JSON)
					.extract().response();

			JsonPath js2 = FormatingResponse.rawToJson(res2);
			String status_message = js2.get("status_message");
			if (status_message.equals("Success."))
				System.out.println(
						"Status Message retured is Success. Rating posted = " + rating + ". When show id = " + id);
			else
				System.out.println("Test Case Failed: Status Message is Not Success.");

			Response res3 = given().param("api_key", prop.getProperty("KEY"))
					.param("guest_session_id", guest_session_id).param("language", prop.getProperty("Language"))
					.delete("/3/tv/" + id + "/rating").then().assertThat().statusCode(200).contentType(ContentType.JSON)
					.extract().response();

			System.out.println("Test Case Passed: Rating was deleted successfully:");
			res3.print();

		}

	}

}
