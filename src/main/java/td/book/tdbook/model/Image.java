package td.book.tdbook.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

//@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "images")
public class Image {

    public Image(String fileName, String contentType, Object object, byte[] bytes) {
    }

    public Image(String name, String type, byte[] img, String caption) {
        this.name = name;
        this.type = type;
        this.img = img;
        this.caption = caption;
    }

    public Image(String name, String type, byte[] img, String caption, String url) {
        this.name = name;
        this.type = type;
        this.img = img;
        this.caption = caption;
        this.url = url;
    }

    public Image(String name, String type, String caption, String url, String s3_bucket, String s3_key) {
        this.name = name;
        this.type = type;
        this.caption = caption;
        this.url = url;
        this.s3_bucket = s3_bucket;
        this.s3_key = s3_key;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull private Long id;

    @Column
    private String name;

    @Column
    private String type;

    @Column
    private String url;

    @Column
    private String caption;

    @Column
    //@Lob if using this annotation => ERROR: column "img" is of type bytea but expression is of type bigint
    private byte[] img;

    @Column
    private String s3_bucket;

    @Column
    private String s3_key;

    @OneToMany(
        mappedBy = "image",
        fetch = FetchType.LAZY
    )
    private List<BookImage> bookImages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getS3_bucket() {
        return s3_bucket;
    }

    public void setS3_bucket(String s3_bucket) {
        this.s3_bucket = s3_bucket;
    }

    public String getS3_key() {
        return s3_key;
    }

    public void setS3_key(String s3_key) {
        this.s3_key = s3_key;
    }

}
