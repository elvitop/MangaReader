package models;

public class ChapterModel {
    private String message;
    private int max;
    private String filedir;
    private String chapternum;
    private String  mangaid;
    private String chaptertitle;
    private String manganame;
    private int chapterid;

    public String getChaptertitle() {
        return chaptertitle;
    }

    public void setChaptertitle(String chaptertitle) {
        this.chaptertitle = chaptertitle;
    }

    public int getChapterid() {
        return chapterid;
    }

    public void setChapterid(int chapterid) {
        this.chapterid = chapterid;
    }






    public String getManganame() {
        return manganame;
    }

    public void setManganame(String manganame) {
        this.manganame = manganame;
    }



    public String getChapternum() {
        return chapternum;
    }

    public void setChapternum(String chapternum) {
        this.chapternum = chapternum;
    }


    public String getMangaid() {
        return mangaid;
    }

    public void setMangaid(String mangaid) {
        this.mangaid = mangaid;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getFiledir() {
        return filedir;
    }

    public void setFiledir(String filedir) {
        this.filedir = filedir;
    }


}
