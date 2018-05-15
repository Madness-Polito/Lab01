package mad.lab1;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import mad.lab1.Fragments.AllBooksFragment;

public class IntroTutorialActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();
        //setContentView(R.layout.activity_intro);

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("Welcome!");
        sliderPage1.setDescription("This is a tutorial of Pazzia Libro Condivisione. With this app you'll be able to borrow and lend books with other people via your smartphone!");
        //sliderPage1.setImageDrawable(R.drawable.authentication_img);
        //TODO add icon of pazzia libro condivisione
        sliderPage1.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Authenticate");
        sliderPage2.setDescription("Authenticate yourself using your email or your social accounts!");
        sliderPage2.setImageDrawable(R.drawable.authentication_img);
        sliderPage2.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("Personal Profile");
        sliderPage3.setDescription("Fill your personal profile with your personal informations to be contacted by other users");
        sliderPage3.setImageDrawable(R.drawable.edit_profile_img);
        sliderPage3.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle("Share and borrow books");
        sliderPage4.setDescription("Look for the book you like and borrow from other users next to you!");
        sliderPage4.setImageDrawable(R.drawable.all_books_img);
        sliderPage4.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage4));

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
