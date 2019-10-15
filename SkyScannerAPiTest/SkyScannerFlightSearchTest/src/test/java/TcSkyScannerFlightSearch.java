import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;

public class TcSkyScannerFlightSearch {

    public static final String rapidHost = "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com";
    public static final String rapidKey = "87301e271emshdc23426b1260714p1bcc34jsn8f38dc373c00";

    @Test
    public void CreateSessionReturnsCorrectResponse() throws UnirestException {
        String requestBody = "cabinClass=business&children=0&infants=0&country=US&currency=USD&locale=en-US&originPlace=SFO-sky&destinationPlace=LHR-sky&outboundDate=2020-09-01&adults=1";
        HttpResponse<String> session = getSession(requestBody);
        String responseBody = session.getBody();
        int statusCode = session.getStatus();

        Assert.assertEquals("{}", responseBody);
        Assert.assertEquals(201, statusCode);
    }

    @Test
    public void CreateSessionReturnsErrorOnIncorrectData() throws UnirestException {
        String requestBody = "cabinClass=business&children=0&infants=0&country=US&currency=USD&locale=en-US&originPlace=SFO-sky&destinationPlace=LHR-sky&outboundDate=2017-09-01&adults=1";
        HttpResponse<String> session = getSession(requestBody);
        String responseBody = session.getBody();
        int statusCode = session.getStatus();

        Assert.assertEquals(session.getBody().contains("ValidationErrors"), true);
        Assert.assertEquals(400, statusCode);
    }

    @Test
    public void ListPlacesReturnsCorrectResponse() {
        Response response =
                given()
                        .when()
                        .header(new Header("x-rapidapi-host", rapidHost))
                        .header(new Header("x-rapidapi-key", rapidKey))
                        .get("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/autosuggest/v1.0/UK/GBP/en-GB/?query=Sarajevo")
                        .then()
                        .statusCode(200)
                        .extract().response();

        String countryId = getCountryId(response);
        String cityId = getCityId(response);

        Assert.assertEquals(countryId, "BA-sky");
        Assert.assertEquals(cityId, "SARA-sky");
    }

    @Test
    public void BrowseQuotesReturnsCorrectResponse() {
        given()
                .when()
                .header(new Header("x-rapidapi-host", rapidHost))
                .header(new Header("x-rapidapi-key", rapidKey))
                .get("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browsequotes/v1.0/US/USD/en-US/SFO-sky/JFK-sky/2019")
                .then()
                .statusCode(200);
    }

    public String getCountryId(Response response) {
        JSONObject json = new JSONObject(response.asString());
        JSONArray places = json.getJSONArray("Places");
        JSONObject place0 = places.getJSONObject(0);
        return place0.getString("CountryId");
    }

    public String getCityId(Response response) {
        JSONObject json = new JSONObject(response.asString());
        JSONArray places = json.getJSONArray("Places");
        JSONObject place0 = places.getJSONObject(0);
        return place0.getString("CityId");
    }

    public HttpResponse<String> getSession(String body) throws UnirestException {
        return Unirest.post("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/pricing/v1.0")
                .header("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
                .header("x-rapidapi-key", "87301e271emshdc23426b1260714p1bcc34jsn8f38dc373c00")
                .header("content-type", "application/x-www-form-urlencoded")
                .body(body)
                .asString();
    }
}