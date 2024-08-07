package com.tracker.service;

import com.tracker.domain.Document;
import com.tracker.repository.DocumentRepository;
import com.tracker.service.dto.DocumentDTO;
import com.tracker.service.mapper.DocumentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.tracker.domain.Document}.
 */
@Service
@Transactional
public class DocumentService {

    private final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;

    private final DocumentMapper documentMapper;

    public DocumentService(DocumentRepository documentRepository, DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    /**
     * Save a document.
     *
     * @param documentDTO the entity to save.
     * @return the persisted entity.
     */
    public DocumentDTO save(DocumentDTO documentDTO) {
        log.debug("Request to save Document : {}", documentDTO);
        Document document = documentMapper.toEntity(documentDTO);
        document = documentRepository.save(document);
        return documentMapper.toDto(document);
    }

    /**
     * Update a document.
     *
     * @param documentDTO the entity to save.
     * @return the persisted entity.
     */
    public DocumentDTO update(DocumentDTO documentDTO) {
        log.debug("Request to update Document : {}", documentDTO);
        Document document = documentMapper.toEntity(documentDTO);
        document = documentRepository.save(document);
        return documentMapper.toDto(document);
    }

    /**
     * Partially update a document.
     *
     * @param documentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DocumentDTO> partialUpdate(DocumentDTO documentDTO) {
        log.debug("Request to partially update Document : {}", documentDTO);

        return documentRepository
            .findById(documentDTO.getId())
            .map(existingDocument -> {
                documentMapper.partialUpdate(existingDocument, documentDTO);

                return existingDocument;
            })
            .map(documentRepository::save)
            .map(documentMapper::toDto);
    }

    /**
     * Get all the documents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DocumentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Documents");
        return documentRepository.findAll(pageable).map(documentMapper::toDto);
    }

    /**
     * Get one document by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DocumentDTO> findOne(Long id) {
        log.debug("Request to get Document : {}", id);
        return documentRepository.findById(id).map(documentMapper::toDto);
    }

    /**
     * Delete the document by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Document : {}", id);
        documentRepository.deleteById(id);
    }
}
