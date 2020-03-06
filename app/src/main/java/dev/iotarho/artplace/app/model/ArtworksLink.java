package dev.iotarho.artplace.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtworksLink implements Parcelable {

    @SerializedName("href")
    @Expose
    private String href;

    public String getHref() {
        return href;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.href);
    }

    public ArtworksLink() {
    }

    protected ArtworksLink(Parcel in) {
        this.href = in.readString();
    }

    public static final Parcelable.Creator<ArtworksLink> CREATOR = new Parcelable.Creator<ArtworksLink>() {
        @Override
        public ArtworksLink createFromParcel(Parcel source) {
            return new ArtworksLink(source);
        }

        @Override
        public ArtworksLink[] newArray(int size) {
            return new ArtworksLink[size];
        }
    };
}
