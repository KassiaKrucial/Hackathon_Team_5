package cz.czechitas.selenium;

import okhttp3.Address;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.beans.Transient;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.support.ui.Select;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class TestingHackathoApplication {

    WebDriver browser;
    private static final String URL_OF_APPLICATION = "http://czechitas-datestovani-hackathon.cz/en/";
    private static final String EMAIL = "JaneDoe@email.cz";
    private static final String EMAIL2 = "JaneDoeB@seznam.cz";
    private static final String PASSWORD = "JaneDoe1";
    private static final String NAME_OF_USER = "Jane";
    private static final String SURNAME_OF_USER = "Doe";
    private static final String PHONE_NUMBER = "123456789";

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.gecko.driver", "C:\\Java-Training\\Selenium\\geckodriver.exe");
        browser = new FirefoxDriver();
        browser.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }

    @Test
    public void logInAsUser() {
        browser.navigate().to(URL_OF_APPLICATION);

        logInUser(EMAIL, PASSWORD);

        WebElement header = browser.findElement(By.xpath("//h1[@class='page-heading']"));
        WebElement usernameButton = browser.findElement(By.xpath("//button[@id='user_info_acc']"));

        Assertions.assertEquals("http://czechitas-datestovani-hackathon.cz/en/my-account", browser.getCurrentUrl());
        Assertions.assertEquals("MY ACCOUNT", header.getText());
        Assertions.assertEquals("Jane", usernameButton.getText());
    }

    @Test
    public void logOutAsUser() throws Exception {
        browser.navigate().to(URL_OF_APPLICATION);

        logInUser(EMAIL, PASSWORD);
        logOutUser();

        WebElement logInButton = browser.findElement(By.xpath("/html/body/div/div[1]/header/div[3]/div/div/div[7]/ul/li/a[@title='Log in to your customer account']"));
        WebElement header = browser.findElement(By.xpath("//h1[@class='page-heading']"));

        Assertions.assertEquals("Sign in", logInButton.getText());
        Assertions.assertEquals("AUTHENTICATION", header.getText());
        Assertions.assertTrue(verifyElementAbsent("//button[@id='user_info_acc']"));
    }

    @Test
    public void forgottenPassword() {
        browser.navigate().to(URL_OF_APPLICATION);

        WebElement logInButton = browser.findElement(By.xpath("//a[@class='user_login navigation-link']"));
        logInButton.click();

        WebElement forgotYourPassword = browser.findElement(By.xpath("//a[@title='Recover your forgotten password']"));
        forgotYourPassword.click();

        WebElement email = browser.findElement(By.xpath("//input[@id='email']"));
        email.sendKeys(EMAIL2);

        WebElement retrievePasswordButton = browser.findElement(By.xpath("//button[@type='submit']"));
        retrievePasswordButton.click();

        WebElement greenParagraph = browser.findElement(By.xpath("//p[@class='alert alert-success']"));

        Assertions.assertEquals("A confirmation email has been sent to your address: " + EMAIL2, greenParagraph.getText());
    }

    @Test
    public void createBookingFromHomePageWithoutSingingIn() {
        browser.navigate().to(URL_OF_APPLICATION);

        createBooking();

        WebElement header = browser.findElement(By.xpath("/html/body/div[1]/div[1]/header/div[3]/div/div/div[5]/div[1]/div[1]/h2"));

        Assertions.assertEquals("Room successfully added to your cart", header.getText());
    }

    @Test
    public void createBookingFromHomePageAndInputGuestInfo() {
        browser.navigate().to(URL_OF_APPLICATION);

        createBooking();

        WebElement proceedButton = browser.findElement(By.xpath("//a[@title='Proceed to checkout']"));
        proceedButton.click();

        WebElement proceedButton2 = browser.findElement(By.xpath("/html/body/div/div[2]/div/div[2]/div/section/div/section/div/div[1]/div/div[1]/div[2]/div/div[2]/div[2]/div/a"));
        proceedButton2.click();

        WebElement guestCheckoutButton = browser.findElement(By.xpath("//button[@id='opc_guestCheckout']"));
        guestCheckoutButton.click();

        WebElement customerFirstname = browser.findElement(By.xpath("//input[@id='customer_firstname']"));
        customerFirstname.sendKeys(NAME_OF_USER);

        WebElement customerSurname = browser.findElement(By.xpath("//input[@id='customer_lastname']"));
        customerSurname.sendKeys(SURNAME_OF_USER);

        String custEmail = (System.currentTimeMillis() + "@mail.cz");
        WebElement customerEmail = browser.findElement(By.xpath("//input[@id='email']"));
        customerEmail.sendKeys(custEmail);

        WebElement customerPhone = browser.findElement(By.xpath("//*[@id=\"phone_mobile\"]"));
        customerPhone.sendKeys(PHONE_NUMBER);

        WebElement address = browser.findElement(By.xpath("//input[@id='address1']"));
        address.sendKeys("address1");

        WebElement city = browser.findElement(By.xpath("//input[@id='city']"));
        city.sendKeys("London");

        WebElement postCode = browser.findElement(By.xpath("//input[@id='postcode']"));
        postCode.sendKeys("63700");

        WebElement mobilePhone = browser.findElement(By.xpath("/html/body/div/div[2]/div/div[2]/div/section/div/section/div/div[1]/div/div[2]/div[2]/div/div/form[2]/div[2]/div[9]/div[13]/input"));
        mobilePhone.sendKeys(PHONE_NUMBER);

        WebElement saveButton = browser.findElement(By.xpath("//button[@id='submitGuestAccount']"));
        saveButton.click();

        WebElement nameForAssert = browser.findElement(By.xpath("/html/body/div/div[2]/div/div[2]/div/section/div/section/div/div[1]/div/div[2]/div[2]/div/div[1]/div[2]"));
        WebElement phoneForAssert = browser.findElement(By.xpath("/html/body/div/div[2]/div/div[2]/div/section/div/section/div/div[1]/div/div[2]/div[2]/div/div[3]/div[2]"));

        Assertions.assertEquals(NAME_OF_USER + " " + SURNAME_OF_USER, nameForAssert.getText());
        Assertions.assertEquals(PHONE_NUMBER, phoneForAssert.getText());
    }

    @Test
    public void createNewAddressForSignedInUser() {
        browser.navigate().to(URL_OF_APPLICATION);

        logInUser(EMAIL, PASSWORD);

        WebElement myAddresses = browser.findElement(By.xpath("//a[@title='Addresses']"));
        myAddresses.click();

        WebElement newAddress = browser.findElement(By.xpath("//a[@title='Add an address']"));
        newAddress.click();

        long timeStamp = System.currentTimeMillis();
        WebElement address = browser.findElement(By.xpath("//input[@id='address1']"));
        address.sendKeys("aaa" + timeStamp);

        WebElement city = browser.findElement(By.xpath("//input[@id='city']"));
        city.sendKeys("London");

        WebElement postCode = browser.findElement(By.xpath("//input[@id='postcode']"));
        postCode.sendKeys("12345");

        WebElement phone = browser.findElement(By.xpath("//input[@id='phone']"));
        phone.sendKeys("123456789");

        WebElement phone2 = browser.findElement(By.xpath("//input[@id='phone_mobile']"));
        phone2.sendKeys("123456789");

        WebElement alias = browser.findElement(By.xpath("//input[@id='alias']"));
        alias.clear();
        alias.sendKeys("London " + timeStamp);

        WebElement submitButton = browser.findElement(By.xpath("//button[@id='submitAddress']"));
        submitButton.click();

        List<WebElement> headers = browser.findElements(By.xpath("//h3"));
        WebElement header = headers.get(Math.max(0, (headers.size() - 1)));

        Assertions.assertEquals("LONDON " + timeStamp, header.getText());
    }

    @AfterEach
    public void tearDown() {
        browser.close();
    }

    private void logInUser(String email, String password){
        WebElement logInButton = browser.findElement(By.xpath("//a[@class='user_login navigation-link']"));
        logInButton.click();

        WebElement emailTxt = browser.findElement(By.xpath("//input[@id='email']"));
        emailTxt.sendKeys(email);

        WebElement passwordTxt = browser.findElement(By.xpath("//input[@id='passwd']"));
        passwordTxt.sendKeys(password);

        WebElement createAccountButton = browser.findElement(By.xpath("//button[@id='SubmitLogin']"));
        createAccountButton.click();
    };

    private void logOutUser() {
        WebElement usernameButton = browser.findElement(By.xpath("//button[@id='user_info_acc']"));
        usernameButton.click();

        WebElement logOutOption = browser.findElement(By.xpath("/html/body/div/div[1]/header/div[3]/div/div/div[7]/ul/li/ul/li[3]/a[@title='Log me out']"));
        logOutOption.click();
    }

    private void createBooking() {
        WebElement hotelLocation = browser.findElement(By.xpath("//input[@id='hotel_location']"));
        hotelLocation.sendKeys("b");
        WebElement hotelLocationOption = browser.findElement(By.xpath("//li[@value='12']"));
        hotelLocationOption.click();

        WebElement selectHotel = browser.findElement(By.xpath("//button[@id='id_hotel_button']"));
        selectHotel.click();
        WebElement selectHotelOption = browser.findElement(By.xpath("//li[@data-hotel-cat-id='14']"));
        selectHotelOption.click();

        WebElement checkInDate = browser.findElement(By.xpath("//input[@id='check_in_time']"));
        checkInDate.click();
        WebElement selectCheckInDate = browser.findElement(By.xpath("/html/body/div[2]/table/tbody/tr[5]/td[6]"));
        selectCheckInDate.click();

        WebElement checkOutDate = browser.findElement(By.xpath("//input[@id='check_out_time']"));
        checkOutDate.click();
        WebElement selectCheckOutDate = browser.findElement(By.xpath("/html/body/div[2]/table/tbody/tr[6]/td[1]/a"));
        selectCheckOutDate.click();

        WebElement searchNowButton = browser.findElement(By.xpath("//button[@id='search_room_submit']"));
        searchNowButton.click();

        WebElement bookNowButton = browser.findElement(By.xpath("//a[@data-id-product='1']"));
        bookNowButton.click();
    }

    private boolean verifyElementAbsent(String locator) throws Exception {
        try {
            browser.findElement(By.xpath(locator));
            return false;

        } catch (NoSuchElementException e) {
            return true;
        }
    }

    private boolean verifyElementPresent(String locator) throws Exception {
        try {
            browser.findElement(By.xpath(locator));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static String className(String htmlClass) {
        return "contains(concat(' ', normalize-space(@class), ' '), ' " + htmlClass + " ')";
    }
}
