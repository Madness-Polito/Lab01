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

public class FragmentBooksTutorialActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();
        //setContentView(R.layout.activity_intro);

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("All books");
        sliderPage1.setDescription("In this section you can find all books you can borrow. Press to one for details and to borrow.");
        sliderPage1.setImageDrawable(R.drawable.all_books_img);
        sliderPage1.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage1));


        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("My books");
        sliderPage2.setDescription("In this other section you can find all books you lend. Tap on icon '+' to add a new one.");
        sliderPage2.setImageDrawable(R.drawable.authentication_img);
        sliderPage2.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
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
