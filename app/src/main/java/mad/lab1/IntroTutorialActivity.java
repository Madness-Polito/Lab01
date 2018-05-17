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
        sliderPage1.setTitle(getString(R.string.tutorial_welcome));
        sliderPage1.setDescription(getString(R.string.tutorial_welcome_text));
        sliderPage1.setImageDrawable(R.drawable.book_icon_img);
        //TODO add icon of pazzia libro condivisione
        sliderPage1.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle(getString(R.string.tutorial_signin));
        sliderPage2.setDescription(getString(R.string.tutorial_signin_text));
        sliderPage2.setImageDrawable(R.drawable.authentication_img);
        sliderPage2.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle(getString(R.string.tutorial_profile));
        sliderPage3.setDescription(getString(R.string.tutorial_profile_text));
        sliderPage3.setImageDrawable(R.drawable.show_profile_img);
        sliderPage3.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle(getString(R.string.tutorial_chat));
        sliderPage4.setDescription(getString(R.string.tutorial_chat_text));
        sliderPage4.setImageDrawable(R.drawable.all_books_img);
        sliderPage4.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage4));

        SliderPage sliderPage5 = new SliderPage();
        sliderPage5.setTitle(getString(R.string.tutorial_letsstart));
        sliderPage5.setDescription(getString(R.string.tutorial_letsstart_text));
        sliderPage5.setImageDrawable(R.drawable.all_books_img);
        sliderPage5.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage5));

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
