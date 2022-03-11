package ai.test.sdk;

import com.google.gson.JsonObject;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.interactions.Coordinates;

/**
 * An enhanced RemoteWebElement which uses the results of the Test.ai classifier for improved accuracy.
 * 
 * @author Alexander Wu (alec@test.ai)
 *
 */
public class TestAiElement extends RemoteWebElement
{
	/**
	 * The logger for this class
	 */
	private static Logger log = LoggerFactory.getLogger(TestAiElement.class);

	/**
	 * The webdriver the user is using. We wrap this for when the user calls methods that interact with selenium.
	 */
	private RemoteWebDriver driver;

	/**
	 * The text in this element, as determined by test.ai's classifier
	 */
	private String text;

	/**
	 * The size of this element, in pixels
	 */
	private Dimension size;

	/**
	 * The location of this element, in pixels (offset from the upper left corner of the screen)
	 */
	private Point location;

	/**
	 * The rectangle that can be drawn around this element. Basically combines size and location.
	 */
	private Rectangle rectangle;

	/**
	 * The tag name of this element, as determined by test.ai's classifier
	 */
	private String tagName;

	/**
	 * Coordinates for clicking/taping this element.
	 * 
	 * @see #click()
	 */
	private int cX, cY;

	/**
	 * Constructor, creates a new TestAiElement
	 * 
	 * @param elem The element data returned by the FD API, as JSON
	 * @param driver The driver the user is using to interact with their app
	 * @param multiplier The screen density multiplier to use
	 */
	TestAiElement(JsonObject elem, RemoteWebDriver driver, double multiplier)
	{
		this.driver = driver;

		text = JsonUtils.stringFromJson(elem, "text");
		size = new Dimension(JsonUtils.intFromJson(elem, "width") / (int) multiplier, JsonUtils.intFromJson(elem, "height") / (int) multiplier);

		location = new Point(JsonUtils.intFromJson(elem, "x") / (int) multiplier, JsonUtils.intFromJson(elem, "y") / (int) multiplier);

		// this.property = property //TODO: not referenced/implemented on python side??
		rectangle = new Rectangle(location, size);
		tagName = JsonUtils.stringFromJson(elem, "class");

		cX = location.x + size.width / 2;
		cY = location.y + size.height / 2;
	}

	@Override
	public String getText()
	{
		return text;
	}

	@Override
	public Dimension getSize()
	{
		return size;
	}

	@Override
	public Point getLocation()
	{
		return location;
	}

	@Override
	public Rectangle getRect()
	{
		return rectangle;
	}

	@Override
	public String getTagName()
	{
		return tagName;
	}

	@Override
	public void clear()
	{
		sendKeys("", false, true); // TODO: this is incredibly hacky
	}

	@Override
	public WebElement findElement(By by)
	{
		return driver.findElement(by);
	}

	@Override
	public List<WebElement> findElements(By by)
	{
		return driver.findElements(by);
	}

	@Override
	public String getAttribute(String name)
	{
		return null;
	}

	@Override
	public String getCssValue(String propertyName)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDisplayed()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEnabled()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSelected()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void click()
	{
		click(true);
	}

	/**
	 * Attempts to perform a click action on this element
	 * 
	 * @param jsClick Set `true` to use javascript to perform the click.
	 */
	@SuppressWarnings("deprecation")
	public void click(boolean jsClick)
	{
		if (jsClick)
		{
			log.debug("Performing a JavaScript click on the element at ({}, {}), the click will be performed at ({}, {})", location.x, location.y, cX, cY);
			driver.executeScript(String.format("document.elementFromPoint(%d, %d).click();", cX, cY));
		}

		else
		{
			log.debug("Performing a standard selenium click on the element at ({}, {}), the click will be performed at ({}, {})", location.x, location.y, cX, cY);
			driver.getMouse().click(new Coordinates() {
				public Point onScreen()
				{
					throw new UnsupportedOperationException("Not supported yet.");
				}

				public Point inViewPort()
				{
					return new Point(cX, cY);
				}

				public Point onPage()
				{
					return inViewPort();
				}

				public Object getAuxiliary()
				{
					return "dummy";
					// return getId();
				}
			});
		}

	}

	@Override
	public void sendKeys(CharSequence... keysToSend)
	{
		sendKeys(String.join("", keysToSend), true, true);
	}

	/**
	 * Attempts to type the specified String ({@code value}) into this element.
	 * 
	 * @param value The String to type into this element.
	 * @param clickFirst Set {@code true} to tap this element (e.g. to focus it) first before sending keys.
	 * @param jsKeys Set {@code true} to use javascript to set the {@code value} property of this element.
	 */
	public void sendKeys(String value, boolean clickFirst, boolean jsKeys)
	{
		if (jsKeys)
			driver.executeScript(String.format("document.elementFromPoint(%d, %d).value = `%s`;", cX, cY, value.replace("`", "\\`")));
		else
		{
			if (clickFirst)
				click();

			new Actions(driver).sendKeys(value).perform();
		}
	}

	@Override
	public void submit()
	{
		sendKeys("\n", false, true);
	}
}
