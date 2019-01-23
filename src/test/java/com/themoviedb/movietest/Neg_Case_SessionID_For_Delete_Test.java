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
import baseclasses.ReusableMethods.Constants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Neg_Case_SessionID_For_Delete_Test {
	Properties prop = new Properties();
	String sheetname = "Neg_Session_id";

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
	public void Neg_Case_SessionID_For_Delete() throws Exception {
		XSSFWorkbook ExcelWBook;
		XSSFSheet ExcelWSheet;
		XSSFCell cell;
		String path = "./TheMovieDB.xlsx";
		FileInputStream ExcelFile = new FileInputStream(path);
		ExcelWBook = new XSSFWorkbook(ExcelFile);
		ExcelWSheet = ExcelWBook.getSheet("Neg_Session_id");
		System.out.println("\n*******Test case to verify that valid session id is needed to delete the ratings.********");
		for (int i = 0; i <= ExcelWSheet.getLastRowNum(); i++) {

			RestAssured.baseURI = prop.getProperty("Host");
			cell = ExcelWSheet.getRow(i).getCell(0);
			String id = cell.getStringCellValue();
			cell = ExcelWSheet.getRow(i).getCell(1);
			String Incorrect_Session_id = cell.getStringCellValue();

			System.out.println("\n");
			Response res3 = given().param("api_key", prop.getProperty("KEY"))
					.param("guest_session_id", Incorrect_Session_id).param("language", prop.getProperty("Language"))
					.log().uri().delete("/3/tv/" + id + "/rating").then().assertThat().statusCode(401)
					.contentType(ContentType.JSON).extract().response();

			System.out.println(
					"Test Case Passed: 401 response received. Valid session id is needed to delete the ratings.");
			res3.print();
			String timeStamp = new SimpleDateFormat("EEE, d MMM yyyy h:mm:ss a").format(new Date());
			ExcelUtility_SingleDataSet.setCellData("Test Case Passed", i, 2);
			ExcelUtility_SingleDataSet.setCellData(timeStamp, i, 3);

		}

	}

}
