package io.github.wowgz.mylocalmusicplayer.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.wowgz.mylocalmusicplayer.R;
import io.github.wowgz.mylocalmusicplayer.bean.Song;

/**
 * Project  : MyLocalMusicPlayer
 * Author   : 郭朕
 * Date     : 2018/1/22
 */
//首先创建一个内部类SongViewHolder继承RecycleView的ViewHolder
//设置控件并且在构造方法里面进行findViewById()
//设置BindHolder()方法，为对应的控件进行初始化赋值，或者为其设置点击事件
//以上步骤都是内部类的设置，内部类设置完成后进行Adapter类的设置
//首先令该类继承RecyclerView.Adapter<SongAdapter.SongViewHolder>
//然后利用alt+enter进行方法的重写，生成需要重写的方法之后就在方法内进行我们需要的内容的编写
//
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private Context context;
    private List<Song> songsList;

    public SongAdapter(Context context, List<Song> songsList) {
        this.context = context;
        this.songsList = songsList;
    }

    public List<Song> getSongList() {
        return songsList;
    }

    public void setSongList(List<Song> songList) {
        songsList.clear();
        songsList.addAll(songList);
        notifyDataSetChanged();
    }

    //添加歌曲的方法
    public void addSongs(List<Song> songs) {
        songsList.addAll(songs);
        notifyDataSetChanged();
    }

    //创建并加载View然后将view传入ViewHolder
    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    //绑定用传入的holder利用songsList.get(position)获取歌，position获取位置
    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.BindHolder(songsList.get(position), position);
    }

    //利用songsList.size()来确定item的数量
    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {

        private TextView numTextView;
        private TextView songNameTextView;
        private TextView infoTextView;
        private ImageView moreInfoImageView;

        public SongViewHolder(View itemView) {

            super(itemView);

            numTextView = itemView.findViewById(R.id.number_text_view);
            songNameTextView = itemView.findViewById(R.id.song_name_text_view);
            infoTextView = itemView.findViewById(R.id.info_text_view);
            moreInfoImageView = itemView.findViewById(R.id.more_info_image_view);

        }

        public void BindHolder(final Song song, int position) {

            numTextView.setText((position + 1) + "");
            songNameTextView.setText(song.getTitle());
            String info = song.getSinger() + " - " + song.getAlbum();
            infoTextView.setText(info);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onClick(song);
//                }
//            });
            //设置点击事件
            moreInfoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle(song.getTitle())
                            .setMessage(song.toString())
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            });
            if (mOnItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onClick(song);
                    }
                });
            }
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(Song song);
    }
}
