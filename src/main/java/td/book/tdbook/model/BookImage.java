package td.book.tdbook.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOOK_IMAGES")
public class BookImage {

    @EmbeddedId
    private BookImageId id;

    @Column(name = "created_on")
    private Date createdOn = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "BOOK_ID")
    @JsonIgnore
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("imageId")
    @JoinColumn(name = "IMAGE_ID")
    @JsonIgnore
    private Image image;

    public BookImageId getId() {
        return id;
    }

    public void setId(BookImageId id) {
        this.id = id;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public int hashCode() {
        return Objects.hash(book, image);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
 
        if (obj == null || getClass() != obj.getClass())
            return false;
 
        BookImage that = (BookImage) obj;
        return Objects.equals(book, that.book) &&
               Objects.equals(image, that.image);
    }
}
