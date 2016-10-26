package com.iyuba.music.entity.comment;

/**
 * Created by 10202 on 2015/12/15.
 */
public class CommentAgree {
    private int commentid;
    private int userid;
    private int agree;

    public int getCommentid() {
        return commentid;
    }

    public void setCommentid(int commentid) {
        this.commentid = commentid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getAgree() {
        return agree;
    }

    public void setAgree(int agree) {
        this.agree = agree;
    }

    @Override
    public String toString() {
        return "CommentAgree{" +
                "commentid=" + commentid +
                ", userid=" + userid +
                ", agree=" + agree +
                '}';
    }
}
