package com.example.relearn.bakers.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.relearn.bakers.ui.fragments.IngredientsFragment;
import com.example.relearn.bakers.R;
import com.example.relearn.bakers.ui.fragments.RecipeDetailsFragment;
import com.example.relearn.bakers.ui.fragments.VideoFragment;
import com.example.relearn.bakers.model.Recipe;
import com.example.relearn.bakers.model.Step;

public class RecipeActivity extends AppCompatActivity implements RecipeDetailsFragment.RecipeClickListener, VideoFragment.VideoClickListener {

    Recipe recipe;
//    ActionBar actionBar;\
    Toolbar toolbar;
    boolean twoPanes;
//    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recipe = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);

        toolbar.setTitle(recipe.getName());

        if (findViewById(R.id.tablet_linear_layout) != null) {
            twoPanes = true; // Double-pane mode
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (savedInstanceState == null) {

                fragmentManager.beginTransaction()
                        .replace(R.id.recipeFragment, new RecipeDetailsFragment().newInstance(recipe))
                        .commit();
                fragmentManager.beginTransaction()
                        .replace(R.id.recipeContainer, new IngredientsFragment().newInstance(recipe))
                        .commit();
            }
        } else {
            twoPanes = false; // Single-pane mode
                if(savedInstanceState == null)
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipeContainer, new RecipeDetailsFragment().newInstance(recipe))
                            .commit();
        }
    }



    @Override
    public void onIngredientsSelected(View v, Recipe recipe) {

        if (v.getId() == R.id.ingredientCardView) {

            if (twoPanes) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipeContainer, new IngredientsFragment().newInstance(recipe))
                        .commit();
            } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipeContainer, new IngredientsFragment().newInstance(recipe))
                            .addToBackStack(IngredientsFragment.class.getSimpleName())
                            .commit();
            }
        }

    }

    @Override
    public void onStepSelected(int position) {

        Step step = recipe.getSteps().get(position);
        int length = recipe.getSteps().size();
        VideoFragment videoFragment = new VideoFragment();
        Bundle bundle = new Bundle();
        String sDescription = step.getShortDescription();

        bundle.putInt("size", length);
        bundle.putInt("position", position);
        bundle.putString("description", step.getDescription());
        bundle.putString("videoURL", step.getVideoURL());
        bundle.putString("thumbnailUrl", step.getThumbnailURL());


        if (bundle != null) {

            videoFragment.setArguments(bundle);

            if (twoPanes) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipeContainer, videoFragment)
                        .commit();
            } else {
                toolbar.setTitle(sDescription);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipeContainer, videoFragment)
                        .addToBackStack(VideoFragment.class.getSimpleName())
                        .commit();
            }
        }

    }

    @Override
    public void onNextSelected(int position) {
        onStepSelected(position);
    }

    @Override
    public void onPreviousSelected(int position) {
        onStepSelected(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Recipe recipeContents = recipe;
        outState.putParcelable("recipe contents", recipeContents);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        recipe = savedInstanceState.getParcelable("recipe contents");
    }
}
