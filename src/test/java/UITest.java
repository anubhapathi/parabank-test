import com.microsoft.playwright.*;
import org.testng.annotations.*;

import static org.junit.jupiter.api.Assertions.*;

public class UITest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private final String baseUrl = "https://parabank.parasoft.com";
    private static String username;  // Static to persist across tests
    private static final String password = "Test@1234"; 
    private String accountNumber;

    @BeforeClass
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        username = "user" + System.currentTimeMillis(); // Generate only once
    }

    @BeforeMethod
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @Test(priority = 1)
    void userRegistration() {
        page.navigate(baseUrl + "/parabank/register.htm");

        page.fill("[name='customer.firstName']", "Anubha");
        page.fill("[name='customer.lastName']", "Tripathi");
        page.fill("[name='customer.address.street']", "123 Homes 121");
        page.fill("[name='customer.address.city']", "Noida");
        page.fill("[name='customer.address.state']", "UP");
        page.fill("[name='customer.address.zipCode']", "10001");
        page.fill("[name='customer.phoneNumber']", "1234567890");
        page.fill("[name='customer.ssn']", "123-45-6789");
        page.fill("[name='customer.username']", username);
        page.fill("[name='customer.password']", password);
        page.fill("[name='repeatedPassword']", password);

        page.click("input[value='Register']");
        assertTrue(page.textContent(".title").contains("Welcome"));

        System.out.println("Registered Username: " + username);
    }

    @Test(priority = 2, dependsOnMethods = "userRegistration")
    void userLogin() {
        page.navigate(baseUrl + "/parabank/index.htm");
        page.fill("[name='username']", username);
        page.fill("[name='password']", password);
        page.click("[value='Log In']");

        assertTrue(page.textContent(".title").contains("Accounts Overview"));
        System.out.println("This is account overview page");
        
    }

    @Test(priority = 3 ,dependsOnMethods = "userLogin")
    void openNewSavingsAccount() {
    	 page.navigate(baseUrl + "/parabank/openaccount.htm");
        page.selectOption("[name='type']", "1");
        page.click("[value='Open New Account']");
        accountNumber = page.textContent("#newAccountId").trim();
        assertNotNull(accountNumber);

        System.out.println("Opened Account: " + accountNumber);
    }

    @Test(priority = 4, dependsOnMethods = "openNewSavingsAccount")
    void transferFunds() {
        page.navigate(baseUrl + "/parabank/transfer.htm");
        page.fill("[name='amount']", "500");
        page.selectOption("[name='fromAccountId']", accountNumber);
        page.selectOption("[name='toAccountId']", "12345");
        page.click("[value='Transfer']");

        assertTrue(page.textContent(".title").contains("Transfer Complete!"));
    }

    @AfterMethod
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @AfterClass
    static void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
