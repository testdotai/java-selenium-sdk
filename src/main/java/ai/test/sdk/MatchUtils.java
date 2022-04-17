package ai.test.sdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import ai.test.sdk.CollectionUtils.Tuple;

/**
 * Static methods for matching bounding boxes to underlying Selenium elements.
 * 
 * @author Alexander Wu (alec@test.ai)
 *
 */
class MatchUtils
{
	/**
	 * The logger for this class
	 */
	private static Logger log = LoggerFactory.getLogger(MatchUtils.class);

	/**
	 * Matches a bounding box returned by the test.ai API to a selenium WebElement on the current page.
	 * 
	 * @param boundingBox The json representing the element returned by the test.ai API.
	 * @param driver The {@code TestAiDriver} to use
	 * @return The best-matching, underlying {@code WebElement} which best fits the parameters specified by {@code boudingBox}
	 */
	public static WebElement matchBoundingBoxToSeleniumElement(JsonObject boundingBox, TestAiDriver driver)
	{
		HashMap<String, Double> newBox = new HashMap<>();
		newBox.put("x", boundingBox.get("x").getAsDouble() / driver.multiplier);
		newBox.put("y", boundingBox.get("y").getAsDouble() / driver.multiplier);
		newBox.put("width", boundingBox.get("width").getAsDouble() / driver.multiplier);
		newBox.put("height", boundingBox.get("height").getAsDouble() / driver.multiplier);

		List<WebElement> elements = driver.driver.findElementsByXPath("//*");
		List<Double> iouScores = new ArrayList<>();

		for (WebElement e : elements)
			try
			{
				iouScores.add(iouBoxes(newBox, e.getRect()));
			}
			catch (StaleElementReferenceException x)
			{
				log.debug("Stale reference to element '{}', setting score of 0", e);
				iouScores.add(0.0);
			}

		List<Tuple<Double, WebElement>> composite = new ArrayList<>();
		for (int i = 0; i < iouScores.size(); i++)
			composite.add(new Tuple<>(iouScores.get(i), elements.get(i)));

		Collections.sort(composite, (o1, o2) -> o2.k.compareTo(o1.k)); // sort the composite values in reverse (descending) order
		composite = composite.stream().filter(x -> x.k > 0).filter(x -> centerHit(newBox, x.v.getRect())).collect(Collectors.toList());

		if (composite.size() == 0)
			throw new NoSuchElementException("Could not find any web element under the center of the bounding box");

		for (Tuple<Double, WebElement> t : composite)
			if (t.v.getTagName().equals("input") || t.v.getTagName().equals(("button")) && t.k > composite.get(0).k * 0.9)
				return t.v;

		return composite.get(0).v;
	}

	/**
	 * Calculate the IOU score of two rectangles. This is derived from the overlap and areas of both rectangles.
	 * 
	 * @param box1 The first box The first rectangle to check (the json returned from the test.ai API)
	 * @param box2 The second box The second rectangle to check (the Rectangle from the selenium WebElement)
	 * @return The IOU score of the two rectangles. Higher score means relative to other scores (obtained from comparisons between other pairs of rectangles) means better match.
	 */
	private static double iouBoxes(Map<String, Double> box1, Rectangle box2)
	{
		return iou(box1.get("x"), box1.get("y"), box1.get("width"), box1.get("height"), (double) box2.x, (double) box2.y, (double) box2.width, (double) box2.height);
	}

	/**
	 * Calculate the IOU score of two rectangles. This is derived from the overlap and areas of both rectangles.
	 * 
	 * @param x The x coordinate of the first box (upper left corner)
	 * @param y The y coordinate of the first box (upper left corner)
	 * @param w The width of the first box
	 * @param h The height of the first box
	 * @param xx The x coordinate of the second box (upper left corner)
	 * @param yy The y coordinate of the second box (upper left corner)
	 * @param ww The width of the second box
	 * @param hh The height of the second box
	 * @return The IOU value of both boxes.
	 */
	private static double iou(double x, double y, double w, double h, double xx, double yy, double ww, double hh)
	{
		double overlap = areaOverlap(x, y, w, h, xx, yy, ww, hh);
		return overlap / (area(w, h) + area(ww, hh) - overlap);
	}

	/**
	 * Determines the amount of area overlap between two rectangles
	 * 
	 * @param x The x coordinate of the first box (upper left corner)
	 * @param y The y coordinate of the first box (upper left corner)
	 * @param w The width of the first box
	 * @param h The height of the first box
	 * @param xx The x coordinate of the second box (upper left corner)
	 * @param yy The y coordinate of the second box (upper left corner)
	 * @param ww The width of the second box
	 * @param hh The height of the second box
	 * @return The amount of overlap, in square pixels.
	 */
	private static double areaOverlap(double x, double y, double w, double h, double xx, double yy, double ww, double hh)
	{
		double dx = Math.min(x + w, xx + ww) - Math.max(x, xx), dy = Math.min(y + h, yy + hh) - Math.max(y, yy);
		return dx >= 0 && dy >= 0 ? dx * dy : 0;
	}

	/**
	 * Convenience function, calculates the area of a rectangle
	 * 
	 * @param w The width of the rectangle
	 * @param h The height of the rectangle
	 * @return The area of the rectangle
	 */
	private static double area(double w, double h)
	{
		return w * h;
	}

	/**
	 * Determines if center point of {@code box1} falls within the area of {@code box2}
	 * 
	 * @param box1 The first rectangle to check (the json returned from the test.ai API)
	 * @param box2 The second rectangle to check (the Rectangle from the selenium WebElement)
	 * @return {@code true} if the center point of {@code box1} falls within the area of {@code box2}
	 */
	private static boolean centerHit(Map<String, Double> box1, Rectangle box2)
	{
		double centerX = box1.get("x") + box1.get("width") / 2, centerY = box1.get("y") + box1.get("height") / 2;
		return centerX > box2.x && centerX < box2.x + box2.width && centerY > box2.y && centerY < box2.y + box2.height;
	}
}
