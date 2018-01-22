package io.github.wowgz.mylocalmusicplayer.activity;

import android.app.FragmentContainer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.github.wowgz.mylocalmusicplayer.R;
import io.github.wowgz.mylocalmusicplayer.base.FragmentContainerActivity;
import io.github.wowgz.mylocalmusicplayer.fragment.PlayLocalMusicFragment;

public class MainActivity extends FragmentContainerActivity {

    @Override
    protected Fragment createFragment() {
        return new PlayLocalMusicFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }


}
