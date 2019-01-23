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
import baseclasses.ReusableMethods.Constants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Validate_Network_with_id_Test {
	Properties prop = new Properties();
	String sheetname = "NetworkInfo";

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
	public void Validate_Network() throws Exception {
		XSSFWorkbook ExcelWBook;
		XSSFSheet ExcelWSheet;
		XSSFCell cell;
		String path = "./TheMovieDB.xlsx";
		FileInputStream ExcelFile = new FileInputStream(path);
		ExcelWBook = new XSSFWorkbook(ExcelFile);
		ExcelWSheet = ExcelWBook.getSheet("NetworkInfo");
		
		System.out.println("\n********Test case to validate network information based on ID********");

		for (int i = 0; i <= ExcelWSheet.getLastRowNum(); i++) {
			RestAssured.baseURI = prop.getProperty("Host");
			cell = ExcelWSheet.getRow(i).getCell(0);
			String id = cell.getStringCellValue();

			Response res = given().param("api_key", prop.getProperty("KEY")).log().uri().get("/3/network/" + id).then()
					.assertThat().statusCode(200).and().contentType(ContentType.JSON).and().extract().response();

			JsonPath js = FormatingResponse.rawToJson(res);
			String name = js.get("name");
			String homepage = js.get("homepage");

			cell = ExcelWSheet.getRow(i).getCell(1);
			String Network_name = cell.getStringCellValue();

			cell = ExcelWSheet.getRow(i).getCell(2);
			String homepageurl = cell.getStringCellValue();
			if (name.equals(Network_name) & homepage.equals(homepageurl)) {
				System.out.println("Test case Passed: Network Name = " + name + ", Homepage url = " + homepage
						+ " When Network ID = " + id + "\n");
				String timeStamp = new SimpleDateFormat("EEE, d MMM yyyy h:mm:ss a").format(new Date());
				ExcelUtility_SingleDataSet.setCellData("Test Case Passed", i, 3);
				ExcelUtility_SingleDataSet.setCellData(timeStamp, i, 4);
			} else {
				System.out.println("Test case Failed: Network Name = " + name + ", Homepage url = " + homepage
						+ " Is Not matching for Network ID = " + id + "\n");
				String timeStamp = new SimpleDateFormat("EEE, d MMM yyyy h:mm:ss a").format(new Date());
				ExcelUtility_SingleDataSet.setCellData("Test Case Failed", i, 3);
				ExcelUtility_SingleDataSet.setCellData(timeStamp, i, 4);
			}

		}

	}

}
