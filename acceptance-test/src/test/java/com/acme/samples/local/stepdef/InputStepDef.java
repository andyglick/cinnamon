package com.acme.samples.local.stepdef;

import com.acme.samples.local.context.LocalContext;
import com.acme.samples.local.pages.input.InputPage;
import com.google.common.base.Stopwatch;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.java.guice.ScenarioScoped;
import org.fest.assertions.api.Assertions;
import org.junit.Assert;
import org.openqa.selenium.Keys;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ScenarioScoped
public class InputStepDef {

    private final InputPage page;
    private final LocalContext context;

    @Inject
    public InputStepDef(final InputPage page, final LocalContext context) {
        this.page = page;
        this.context = context;
    }

    @When("I choose to delete the text from {string}")
    public void i_choose_to_delete_the_text_from(final String id) throws Throwable {
        page.deleteFrom(id);
    }

    @Then("{string} shall contain {string}")
    public void shall_contain(final String id, final String content) throws Throwable {
        Assert.assertEquals(content, page.getContent(id));
    }

    @When("I choose to clear the text from {string}")
    public void i_choose_to_clear_the_text_from(final String id) throws Throwable {
        page.clearFrom(id);
    }

    @When("I choose to type {keys} from {string}")
    public void i_choose_to_type_from(final List<String> keys, final String id) throws Throwable {
        final List<CharSequence> actualText = new LinkedList<>();
        for (final String textPortion : keys) {
            try {
                actualText.add(Keys.valueOf(textPortion));
            } catch (final IllegalArgumentException e) {
                actualText.add(textPortion);
            }
        }
        page.typeTextInto(actualText, id);
    }

    @When("I choose to send keys {keys} into {string}")
    public void i_choose_to_send_keys_into(final List<String> keys, final String id) throws Throwable {
        final StringBuilder sb = new StringBuilder();
        for (final String key : keys) {
            sb.append(Keys.valueOf(key));
        }
        page.sendKeysInto(sb.toString(), id);
    }

    @When("I choose to click on {string}")
    public void i_choose_to_click_on(final String id) throws Throwable {
        page.clickOn(id);
    }

    @When("I choose to double click on {string}")
    public void i_choose_to_double_click_on(final String id) throws Throwable {
        page.doubleClickOn(id);
    }

    @When("I choose to scroll element {string} into view")
    public void i_choose_to_scroll_element_into_view(final String id) throws Throwable {
        page.scrollTo(id);
    }

    @Then("I {maybe} see element {string} in the viewport")
    public void i_should_see_element_in_the_viewport(final String maybe, final String id) throws Throwable {
        final boolean should = "should".equals(maybe);
        Assert.assertEquals(should, page.isElementVisibleInViewpoint(id));
    }

    @Given("the following elements:")
    public void the_following_elements(final List<List<String>> elements) throws Throwable {
        // This is for documentation purposes only.
    }

    @When("I choose to type {string} for element {string} with {string} {string}")
    public void i_choose_to_type_for_element_with(final String text, final String locator, final String attribute, final String value)
            throws Throwable {
        page.typeTextIntoSameLocator(text, attribute, value);
    }

    @Then("the following elements shall be displayed:")
    public void the_following_elements_shall_be_displayed(final List<Map<String, String>> expected) throws Throwable {
        Assert.assertEquals(expected, page.getDisplayedSameLocator());
    }

    @When("I choose to type {string} from {string} with keystroke delay {int} milliseconds")
    public void i_choose_to_type_from_with_keystroke_delay_milliseconds(final String textToType, final String id, final int delayMillis)
            throws Throwable {
        final Stopwatch timer = Stopwatch.createStarted();
        context.setTimer(timer);
        page.typeTextInto(textToType, id, delayMillis);
    }

    @Then("it shall take over {int} seconds")
    public void it_shall_take_over_seconds(final int minTimeExpectedSeconds) throws Throwable {
        final Stopwatch timer = context.getTimer();
        Assertions.assertThat(timer.elapsed(TimeUnit.MILLISECONDS)).isGreaterThan(minTimeExpectedSeconds * 1000);
    }
}