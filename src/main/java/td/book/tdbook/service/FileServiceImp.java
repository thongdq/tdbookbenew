package td.book.tdbook.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import td.book.tdbook.model.Image;
import td.book.tdbook.repository.FileRepository;

@Service
public class FileServiceImp implements FileService {

    @Autowired
    FileRepository fileRepository;

    @Override
    public void save(Image image) {
        fileRepository.save(image);
    }

    @Override
    public Optional<Image> findById(Long id) {
        return fileRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        fileRepository.deleteById(id);
    }

}
