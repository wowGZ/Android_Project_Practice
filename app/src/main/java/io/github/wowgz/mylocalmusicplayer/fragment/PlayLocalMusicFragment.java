package io.github.wowgz.mylocalmusicplayer.fragment;

import android.Manifest;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.wowgz.mylocalmusicplayer.R;
import io.github.wowgz.mylocalmusicplayer.adapter.SongAdapter;
import io.github.wowgz.mylocalmusicplayer.bean.Song;
import io.github.wowgz.mylocalmusicplayer.utils.AudioUtils;
import io.github.wowgz.mylocalmusicplayer.utils.RequestPermissions;

/**
 * Project  : MyLocalMusicPlayer
 * Author   : 郭朕
 * Date     : 2018/1/22
 */

public class PlayLocalMusicFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    private static final String TAG = "PlayLocalMusicFragment";

    private ImageView skipPreviousImageView;
    private ImageView skipNextImageView;
    private ImageView playOrPauseImageView;
    private ImageView playModeImageView;
    private RecyclerView songRecycleImageView;
    private SeekBar seekBar;

    //歌曲寄存器
    private SongAdapter songAdapter;
    //媒体播放器
    private MediaPlayer mediaPlayer = new MediaPlayer();

    //保存查到的所有歌曲
    private List<Song> songList = new ArrayList<>();

    //歌曲在songList中的下标
    private int songIndex = -1;

    //现在播放的歌曲
    private Song currentSong;

    //播放模式的选择，队列播放，单曲循环，随机播放
    private final int PLAY_QUEUE = R.drawable.ic_queue;
    private final int REPEAT_ONE = R.drawable.ic_repeat_one;
    private final int PLAY_RANDOM = R.drawable.ic_random;

    //保存现在的播放模式
    @DrawableRes
    private int playMode = PLAY_RANDOM;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //对媒体适配器进行初始化
        songAdapter = new SongAdapter(getContext(), songList);

        //设置Fragment可以设置标题栏
        setHasOptionsMenu(true);
    }

    //用于获取当前Fragment实例
    public static PlayLocalMusicFragment newInstance() {
        Bundle args = new Bundle();

        PlayLocalMusicFragment fragment = new PlayLocalMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play_music, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        skipNextImageView = view.findViewById(R.id.skip_next_image_view);
        skipPreviousImageView = view.findViewById(R.id.skip_previous_image_view);
        playOrPauseImageView = view.findViewById(R.id.play_image_view);
        playModeImageView = view.findViewById(R.id.play_mode_image_view);
        songRecycleImageView = view.findViewById(R.id.song_recycle_view);
        seekBar = view.findViewById(R.id.seekbar);

        //权限申请,三个参数，分别是：活动，活动权限的字符串数组，请求权限
        RequestPermissions.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                new RequestPermissions.OnPermissionsRequestListener() {
                    @Override
                    public void onGranted() {
                        loadLocalMusic();
                        initEvent();
                    }

                    @Override
                    public void onDenied(List<String> deniedList) {
                        Toast.makeText(getActivity(), "拒绝权限将无法获取歌曲目录", Toast.LENGTH_LONG).show();
                    }
                });
    }

    //释放媒体资源，先判断是否为空，不为空的话先停止播放再释放资源
    @Override
    public void onDestroy() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        super.onDestroy();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RequestPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //异步加载本地歌曲
    //不在UI线程下做耗时操作
    //不在非UI线程下更新UI
    private void loadLocalMusic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Song> songs = AudioUtils.getAllSongs(getContext());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        songAdapter.addSongs(songs);
                    }
                });
            }
        }).start();//让线程开始
    }

    //初始化设置，及设置点击事件
    private void initEvent() {

        songRecycleImageView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songRecycleImageView.setAdapter(songAdapter);

        songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onClick(Song song) {
                playMusic(song);
            }
        });

        changePlayMode();

        skipNextImageView.setOnClickListener(this);
        skipPreviousImageView.setOnClickListener(this);
        playModeImageView.setOnClickListener(this);
        playOrPauseImageView.setOnClickListener(this);
        //设置seekBar的点击事件
        seekBar.setOnSeekBarChangeListener(this);


    }


    //用于更新进度条的线程，此方法使得进度条的长度可以根据歌曲播放的进度更新进度条
    //通过mediaPlayer.getCurrentPosition()获取歌曲播放的实时状态，并用seekBar.setProgress()方法获取当前的歌曲进度
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        }
    };

    //播放歌曲方法
    private void playMusic(Song song) {
        try {
            //重置为准备状态
            mediaPlayer.reset();
            File file = new File(song.getFileUrl());
            mediaPlayer.setDataSource(file.getPath());
            //准备
            mediaPlayer.prepare();
            songIndex = songList.indexOf(song);

            //标题栏显示歌名，子标题显示 歌手-专辑
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle(song.getTitle());
            activity.getSupportActionBar().setSubtitle(song.getSinger() + " - " + song.getAlbum());

            //播放音乐
            mediaPlayer.start();

            //一旦播放音乐就是显示停止图标
            playOrPauseImageView.setImageResource(R.drawable.ic_pause);

            //设置当前播放音乐
            currentSong = song;
            //根据当前播放的音乐时长设置seekBar的最大值
            seekBar.setMax(song.getDuration());

            //更新进度条，具体内容可以看runnable的声明和定义部分
            new Thread(runnable).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //播放下一首歌
    private void playNextSong() {
        //如果不是播放状态无法点击
        if (!mediaPlayer.isPlaying()) {
            return;
        }
        //对播放的歌曲进行更新
        if (playMode == PLAY_RANDOM) {
            songIndex = new Random().nextInt(songList.size());
        } else {
            if (songIndex == songList.size() - 1) songIndex = 0;
            else songIndex = songIndex + 1;
        }
        playMusic(songList.get(songIndex));
    }

    //更改播放模式
    private void changePlayMode() {

        if (playMode == REPEAT_ONE) {
            mediaPlayer.setLooping(true);
        } else if (playMode == PLAY_QUEUE) {
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playMusic(songList.get((songIndex + 1) % songList.size()));
                }
            });
        } else if (playMode == PLAY_RANDOM) {
            mediaPlayer.setLooping(false);//github上是true
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playMusic(songList.get(new Random().nextInt(songList.size())));
                }
            });
        }

        playModeImageView.setImageResource(playMode);
    }

    //设置播放或者暂停按钮的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skip_previous_image_view:
                //如果不是播放状态无法点击
                if (!mediaPlayer.isPlaying()) {
                    return;
                }
                //对播放的歌曲进行更新
                if (songIndex == 0) {
                    songIndex = songList.size() - 1;
                } else {
                    songIndex = songIndex - 1;
                }
                playMusic(songList.get(songIndex));
                break;
            case R.id.play_image_view:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playOrPauseImageView.setImageResource(R.drawable.ic_play);
                } else {
                    //如果当前处于初始状态，没有播放歌曲，则根据播放模式选择第一首歌曲
                    if (songIndex == -1 && !songList.isEmpty()) {
                        if (playMode == PLAY_RANDOM) {
                            songIndex = new Random().nextInt(songList.size());
                        } else {
                            songIndex = 0;
                        }
                        playMusic(songList.get(songIndex));
                    }
                    mediaPlayer.start();
                    playOrPauseImageView.setImageResource(R.drawable.ic_pause);

                    //停止过后，重启线程开始更新进度条
                    //解决暂停之后进度条不会继续更新的bug
                    new Thread(runnable).start();
                }
                break;
            case R.id.skip_next_image_view:
                playNextSong();
                break;
            case R.id.play_mode_image_view:
                //更改播放模式
                if (playMode == PLAY_QUEUE) {
                    playMode = REPEAT_ONE;
                } else if (playMode == REPEAT_ONE) {
                    playMode = PLAY_RANDOM;
                } else if (playMode == PLAY_RANDOM) {
                    playMode = PLAY_QUEUE;
                }
                changePlayMode();
                break;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        //如果当前没有歌，那么不设置进度并且返回
        if (songIndex == -1) {
            seekBar.setProgress(0);
            return;
        }

        //如果当前进度已经到了末尾那么直接跳下一首，并且设置当前进度为0
        if (seekBar.getProgress() == seekBar.getMax()) {
            //只要不是单曲循环就播放下一首
            if (playMode != REPEAT_ONE) {
                playNextSong();
            }
            seekBar.setProgress(0);
            return;
        }

        //使得媒体播放器的进度跟随我们的seekBar的进度，从而使得我们的进度条可以起到显示歌曲进度的作用
        mediaPlayer.seekTo(seekBar.getProgress());
        //如果不是播放状态
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            //解决暂停之后拖拽进度条之后，图标不改变的bug
            playOrPauseImageView.setImageResource(R.drawable.ic_pause);
            //也是为了修改进度条的bug
            new Thread(runnable).start();
        }
    }
}
