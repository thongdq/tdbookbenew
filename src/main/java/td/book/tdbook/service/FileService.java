package td.book.tdbook.service;

import java.util.Optional;

import td.book.tdbook.model.Image;

public interface FileService {
    void save(Image image);
    Optional<Image> findById(Long id);
    void deleteById(Long id);
}
