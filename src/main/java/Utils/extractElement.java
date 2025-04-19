package Utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class extractElement {
    public static List<WebElement> getWebElementsList(String ulCssSelector, WebDriver driver) {
        final String tagName = "li";
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement ulElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(ulCssSelector)));
        return ulElement.findElements(By.tagName(tagName));
    }
}
