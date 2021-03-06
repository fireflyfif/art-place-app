package dev.iotarho.artplace.app.model.genes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dev.iotarho.artplace.app.model.ImageLinks;

public class GeneContent implements Parcelable {

    @SerializedName("id")
    private String id;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("name")
    private String name;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("description")
    private String description;

    @SerializedName("image_versions")
    private List<String> imageVersions = null;

    @SerializedName("_links")
    private ImageLinks links;

    public final static Parcelable.Creator<GeneContent> CREATOR = new Creator<GeneContent>() {
        @SuppressWarnings({"unchecked"})
        public GeneContent createFromParcel(Parcel in) {
            return new GeneContent(in);
        }

        public GeneContent[] newArray(int size) {
            return (new GeneContent[size]);
        }
    };

    protected GeneContent(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
        this.updatedAt = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.displayName = ((String) in.readValue((String.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.imageVersions, (java.lang.String.class.getClassLoader()));
        this.links = ((ImageLinks) in.readValue((ImageLinks.class.getClassLoader())));
    }

    public GeneContent() {
    }

    public String getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getImageVersions() {
        return imageVersions;
    }

    public ImageLinks getLinks() {
        return links;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(createdAt);
        dest.writeValue(updatedAt);
        dest.writeValue(name);
        dest.writeValue(displayName);
        dest.writeValue(description);
        dest.writeList(imageVersions);
        dest.writeValue(links);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
