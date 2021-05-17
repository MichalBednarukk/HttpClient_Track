package pl.bednaruk.httpclient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ChordApp {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("chordName")
    @Expose
    private String chordName;
    @SerializedName("imageResources")
    @Expose
    private String imageResources;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChordName() {
        return chordName;
    }

    public void setChordName(String chordName) {
        this.chordName = chordName;
    }

    public String getImageResources() {
        return imageResources;
    }

    public void setImageResources(String imageResources) {
        this.imageResources = imageResources;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ChordApp.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("chordName");
        sb.append('=');
        sb.append(((this.chordName == null)?"<null>":this.chordName));
        sb.append(',');
        sb.append("imageResources");
        sb.append('=');
        sb.append(((this.imageResources == null)?"<null>":this.imageResources));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}