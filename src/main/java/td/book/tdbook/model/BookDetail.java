package td.book.tdbook.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NonNull;

@Entity
@Table(name = "BOOK_DETAIL")
public class BookDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull private Long id;

    //https://www.baeldung.com/jpa-annotation-postgresql-text-type
    @Column(columnDefinition = "TEXT")
    private String content;

    public BookDetail() {};

    public BookDetail(@NonNull Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
