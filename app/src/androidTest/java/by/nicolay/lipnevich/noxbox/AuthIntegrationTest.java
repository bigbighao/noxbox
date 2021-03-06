package live.noxbox;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import live.noxbox.activities.AuthActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class AuthIntegrationTest {

    @Rule
    public ActivityTestRule<AuthActivity> rule = new ActivityTestRule<>(AuthActivity.class);

    @Test
    public void testCommonRequested() {
        // open constructor
        // update fields on it
        // post Noxbox
        // become requested
        // open detailed view
        // accept
        // open activity_chat and send message
        // verify
        // become verified
        // complete
        // check balance
        // check activity_history
        // comment in activity_history
    }

    @Test
    public void testGoogleSignIn() throws Exception {
        Thread.sleep(1000);
        onView(withId(R.id.checkbox)).check(matches(not(isChecked())));
        onView(withId(R.id.googleAuth)).check(matches(not(isEnabled())));
        onView(withId(R.id.checkbox)).perform(click());
        getItemById("googleAuth").clickAndWaitForNewWindow();

        UiObject account = getItemByText("Nicolay.Lipnevich@Gmail.com");
        if(account.exists()) {
            account.clickAndWaitForNewWindow();
        }

        Thread.sleep(5000);
        getItemById("menu").click();

        onView(withText("Logout")).perform(click());
    }

    private static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";
    private static final String NOTIFICATION_TEXT = "NOTIFICATION_TEXT";
    private static final Integer TIMEOUT = 25;
    @Test
    public void testNotification(){
        UiDevice device = getDevice();
        device.openNotification();
        device.wait(Until.hasObject(By.text(NOTIFICATION_TITLE)), TIMEOUT);
        UiObject2 title = device.findObject(By.text(NOTIFICATION_TITLE));
        UiObject2 text = device.findObject(By.text(NOTIFICATION_TEXT));
        assertEquals(NOTIFICATION_TITLE, title.getText());
        assertEquals(NOTIFICATION_TEXT, text.getText());
        title.click();
        device.wait(Until.hasObject(By.text("ESPRESSO")), TIMEOUT);
    }

    private UiObject getItemByText(String text) {
        return getDevice().findObject(new UiSelector().textContains(text));
    }

    private UiObject getItemById(String id) {
        return getDevice().findObject(new UiSelector().resourceId(rule.getActivity().getPackageName()
                + ":id/" + id));
    }

    private UiDevice getDevice() {
        return UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }




}
