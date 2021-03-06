package io.github.wowgz.mylocalmusicplayer.bean;

/**
 * Project  : MyLocalMusicPlayer
 * Author   : 郭朕
 * Date     : 2018/1/22
 */

public class Song {
    private String fileName;
    private String title;
    private int duration;
    private String singer;
    private String album;
    private String year;
    private String type;
    private String size;
    private String fileUrl;

    public Song() {}


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public String toString() {
        return "文件名：" + fileName + '\n' +
                "音乐名：" + title + '\n' +
                "时长：" + (duration * 1.0 / 1000) + "s\n" +
                "歌手：" + singer + '\n' +
                "专辑：" + album + '\n' +
                "年份：" + year + '\n' +
                "文件类型：" + type + '\n' +
                "大小：" + size + '\n' +
                "文件路径：" + fileUrl + '\n';
    }
}
