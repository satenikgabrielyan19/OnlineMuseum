package com.onlinemuseum.service;

import com.onlinemuseum.criteria.ArtworkSearchCriteria;
import com.onlinemuseum.domain.entity.Artist;
import com.onlinemuseum.domain.entity.Artwork;
import com.onlinemuseum.domain.entity.Sphere;
import com.onlinemuseum.domain.entity.Style;
import com.onlinemuseum.dto.*;
import com.onlinemuseum.mapper.GlobalMapper;
import com.onlinemuseum.repository.*;
import com.onlinemuseum.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class StyleService {
    private final StyleRepository styleRepository;
    private final GlobalMapper globalMapper;
    private final SphereRepository sphereRepository;
    private final ArtworkRepository artworkRepository;

    private final Logger logger = LoggerFactory.getLogger(StyleService.class);

    @Autowired
    public StyleService(StyleRepository styleRepository, GlobalMapper globalMapper,
                        SphereRepository sphereRepository, ArtworkRepository artworkRepository) {
        this.styleRepository = styleRepository;
        this.globalMapper = globalMapper;
        this.sphereRepository = sphereRepository;

        this.artworkRepository = artworkRepository;
    }

    @Value("${global.path}")
    private String path;

    public StyleResponse createStyle(StyleDto styleDto) {
        StyleResponse styleResponse = new StyleResponse();
        List<String> failureMessages = new ArrayList<>();
        Optional<Sphere> sphere = this.sphereRepository.getByName(styleDto.getSphere());
        if (sphere.isEmpty()) {
            failureMessages.add(String.format("A sphere with name \"%s\" doesn't exists.",
                    styleDto.getSphere()));
            styleResponse.setFailureMessages(failureMessages);

            logger.info("A sphere with name \"{}\" doesn't exist", styleDto.getSphere());

            return styleResponse;
        }

        if (styleRepository.existsStyleByName(styleDto.getName())) {
            logger.info("A style with name \"{}\" already exists.", styleDto.getName());
            failureMessages.add(String.format("A style with name \"%s\" already exists.", styleDto.getName()));

            styleResponse.setFailureMessages(failureMessages);

            return styleResponse;
        }

        Style styleToSave = new Style();
        styleToSave.setName(styleDto.getName());
        styleToSave.setSphere(sphere.get());
        styleToSave.setDescriptionUrl(styleDto.getDescriptionUrl());

        Style savedStyle = styleRepository.save(styleToSave);

        styleResponse.setStyleDto(globalMapper.map(savedStyle, StyleDto.class));

        logger.info("A style with name \"{}\" was saved successfully.", styleDto.getName());

        return styleResponse;
    }

    public StyleResponse hideStyle(Long id) {
        StyleResponse styleResponse = new StyleResponse();
        Optional<Style> styleToHide = styleRepository.findById(id);
        List<String> failureMessages = new ArrayList<>();

        if (styleToHide.isEmpty()) {
            logger.info("An style with id {} doesn't exist.", id);
            failureMessages.add(String.format("An style with id %d doesn't exist.", id));

            styleResponse.setFailureMessages(failureMessages);

            return styleResponse;
        }

        styleToHide.get().setEnabled(false);
        Style hiddenStyle = styleRepository.save(styleToHide.get());

        styleResponse.setStyleDto(globalMapper.map(hiddenStyle, StyleDto.class));

        logger.info("The style with id {} was successfully hidden.", id);

        return styleResponse;
    }

    public Optional<StyleDto> getStyle(Long id) {
        Optional<Style> style = this.styleRepository.findById(id);
        if (style.get().getEnabled().equals(false)) {
            return Optional.empty();
        }

        if (style.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(this.globalMapper.map(style.get(), StyleDto.class));
    }


    public StyleResponse updateStyle(Long id, StyleDto styleDto) {
        StyleResponse styleResponse = new StyleResponse();
        Optional<Style> styleToUpdate = styleRepository.findById(id);
        List<String> failureMessages = new ArrayList<>();

        if (styleToUpdate.isEmpty()) {
            logger.info("A style with id {} doesn't exist.", id);
            failureMessages.add(String.format("A sphere with id %d doesn't exist.", id));

            styleResponse.setFailureMessages(failureMessages);

            return styleResponse;
        }

        String styleName = styleDto.getName();

        if (styleRepository.existsStyleByName(styleName)) {
            logger.info("A style with name \"{}\" exists.", styleDto.getName());

            failureMessages.add(String.format("A style with name \"%s\" already exists.", styleName));
        }

        if (!failureMessages.isEmpty()) {
            styleResponse.setFailureMessages(failureMessages);

            return styleResponse;
        }

        styleToUpdate.get().setName(styleDto.getName());
        styleToUpdate.get().setDescriptionUrl(styleDto.getDescriptionUrl());

        Style updatedStyle = this.styleRepository.save(styleToUpdate.get());

        styleResponse.setStyleDto(globalMapper.map(updatedStyle, StyleDto.class));

        logger.info("The style with id {} was updated successfully.", id);

        return styleResponse;
    }

    public GenericPageableResponse<StyleDto> getStylesOfSphere(String sphere, Integer pageNumber, Integer pageSize) {
        Optional<Sphere> getSphere = sphereRepository.getByName(sphere);
        Long id = getSphere.get().getId();

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<Style> styles = styleRepository.findBySphere(pageRequest, id);

        return new GenericPageableResponse<>(
                globalMapper.mapList(styles.getContent(), StyleDto.class),
                styles.getTotalPages(),
                styles.getNumber()
        );
    }

    public Optional<GenericPageableResponse<ArtworkDto>> getArtworksOfStyle(Long styleId,
                                                                            ArtworkSearchCriteria artworkSearchCriteria){
        Optional<Style> style = styleRepository.findById(styleId);
        if (style.isEmpty() || !style.get().getEnabled()) {
            return Optional.empty();
        }

        Integer pageNumber = artworkSearchCriteria.getPageNumber();
        Integer pageSize = artworkSearchCriteria.getPageSize();

        String sortField = artworkSearchCriteria.getSortField();
        if (sortField == null || sortField.isEmpty()) {
            sortField = "fullName";
        }

        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(artworkSearchCriteria.getSortDirection());
        } catch (Exception e) {
            sortDirection = Sort.Direction.ASC;
        }

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize,
                Sort.by(sortDirection, sortField));

        Page<Artwork> artworks = artworkRepository.findAllByStyle(styleId, pageRequest);

        return Optional.of(new GenericPageableResponse<>(
                globalMapper.mapList(artworks.getContent(), ArtworkDto.class),
                artworks.getTotalPages(),
                pageNumber
        ));
    }

    public Optional<GenericPageableResponse<ArtistDto>> getArtistsOfStyle(Long styleId, Integer pageNumber, Integer pageSize){
        Optional<Style> style = styleRepository.findById(styleId);
        if (style.isEmpty() || !style.get().getEnabled()) {
            return Optional.empty();
        }

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<Artist> artists = styleRepository.artistsOfStyle(pageRequest, styleId);

        return Optional.of(new GenericPageableResponse<>(
                globalMapper.mapList(artists.getContent(), ArtistDto.class),
                artists.getTotalPages(),
                pageNumber
        ));
    }
}

