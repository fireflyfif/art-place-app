package dev.iotarho.artplace.app.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImagesObject implements Parcelable {

    @SerializedName("href")
    @Expose
    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.href);
    }

    public ImagesObject() {
    }

    protected ImagesObject(Parcel in) {
        this.href = in.readString();
    }

    public static final Parcelable.Creator<ImagesObject> CREATOR = new Parcelable.Creator<ImagesObject>() {
        @Override
        public ImagesObject createFromParcel(Parcel source) {
            return new ImagesObject(source);
        }

        @Override
        public ImagesObject[] newArray(int size) {
            return new ImagesObject[size];
        }
    };
}
