package com.onlinemuseum.service;

import com.onlinemuseum.controller.ArtistController;
import com.onlinemuseum.domain.entity.Artist;
import com.onlinemuseum.domain.entity.Artwork;
import com.onlinemuseum.domain.entity.Style;
import com.onlinemuseum.dto.ArtistToShowInArtworkDto;
import com.onlinemuseum.dto.ArtworkCreateDto;
import com.onlinemuseum.dto.ArtworkDto;
import com.onlinemuseum.dto.StyleToShowInArtworkDto;
import com.onlinemuseum.mapper.GlobalMapper;
import com.onlinemuseum.repository.ArtistRepository;
import com.onlinemuseum.repository.ArtworkRepository;
import com.onlinemuseum.repository.StyleRepository;
import com.onlinemuseum.response.ArtworkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ArtworkService {
    private final ArtworkRepository artworkRepository;
    private final GlobalMapper globalMapper;
    private final ArtistRepository artistRepository;
    private final StyleRepository styleRepository;

    private final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    public ArtworkService(ArtworkRepository artworkRepository, GlobalMapper globalMapper,
                          ArtistRepository artistRepository, StyleRepository styleRepository) {
        this.artworkRepository = artworkRepository;
        this.globalMapper = globalMapper;
        this.artistRepository = artistRepository;
        this.styleRepository = styleRepository;
    }

    @Value("${global.path}")
    private String path;

    public Optional<ArtworkDto> getArtwork(Long id) {
        Optional<Artwork> artwork = this.artworkRepository.findById(id);

        if (artwork.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(this.globalMapper.map(artwork.get(), ArtworkDto.class));
    }

    public ArtworkResponse createArtwork(ArtworkCreateDto artworkCreateDto, List<MultipartFile> multipartFiles) {
        ArtworkResponse artworkResponse = new ArtworkResponse();
        List<String> failureMessages = new ArrayList<>();

        if (artworkCreateDto.getFullName() == null) {
            failureMessages.add("An Artwork name can't be empty.");
            artworkResponse.setFailureMessages(failureMessages);

            logger.info("An Artwork name can't be empty.");

            return artworkResponse;
        }

        Optional<Artist> artist = artistRepository.findById(artworkCreateDto.getArtistId());
        if (artist.isEmpty()) {
            failureMessages.add(String.format("An artist with id %d not found", artworkCreateDto.getArtistId()));
            artworkResponse.setFailureMessages(failureMessages);

            logger.info("An artist with id \"{}\" not found", artworkCreateDto.getArtistId());

            return artworkResponse;
        }

        Optional<Style> style = styleRepository.findByName(artworkCreateDto.getStyle());
        if (style.isEmpty()) {
            failureMessages.add(String.format("An style with name %s not found", artworkCreateDto.getStyle()));
            artworkResponse.setFailureMessages(failureMessages);

            logger.info("An style with name \"{}\" not found", artworkCreateDto.getStyle());

            return artworkResponse;
        }

        Artwork artworkToSave = this.globalMapper.map(artworkCreateDto, Artwork.class);

        artworkToSave.setArtist(artist.get());
        artworkToSave.setStyle(style.get());

        String descriptionPath = path +
                style.get().getSphere().getName().toLowerCase() + "\\artwork\\description";

        String photoPath = path +
                style.get().getSphere().getName().toLowerCase() + "\\artwork\\picture";

        try {
            if (multipartFiles.size() != 2) {
                failureMessages.add("Selected files count isn't 2.");
                artworkResponse.setFailureMessages(failureMessages);
                logger.info("Selected files count isn't 2.");

                return artworkResponse;
            }

            MultipartFile firstFile = multipartFiles.get(0);
            MultipartFile secondFile = multipartFiles.get(1);
            if (!((Objects.requireNonNull(firstFile.getContentType()).contains("image") &&
                    Objects.requireNonNull(secondFile.getContentType()).contains("text")) ||
                    (Objects.requireNonNull(firstFile.getContentType()).contains("text") &&
                            Objects.requireNonNull(secondFile.getContentType()).contains("image")))
            ) {

                failureMessages.add(String.format(
                        "Selected files must be image and text, but you select %s and %s.",
                        firstFile.getContentType(), secondFile.getContentType()));
                artworkResponse.setFailureMessages(failureMessages);
                logger.info(String.format(
                        "Selected files must be image and text, but you select %s and %s.",
                        firstFile.getContentType(), secondFile.getContentType()));

                return artworkResponse;
            }

            MultipartFile description;
            MultipartFile photo;
            if (Objects.requireNonNull(firstFile.getContentType()).contains("image")) {
                photo = firstFile;
                description = secondFile;
            } else {
                description = firstFile;
                photo = secondFile;
            }

            Optional<Artwork> topArtwork = artworkRepository.findTopByOrderByIdDesc();

            Long id;
            if (topArtwork.isEmpty()) {
                id = 1L;
            } else {
                id = topArtwork.get().getId() + 1;
            }

            String descriptionFileName = id + artworkCreateDto.getFullName() + "." +
                    Objects.requireNonNull(description.getOriginalFilename())
                            .substring(description.getOriginalFilename().lastIndexOf(".") + 1);
            String photoFileName = id + artworkCreateDto.getFullName() + "." +
                    Objects.requireNonNull(photo.getOriginalFilename())
                            .substring(photo.getOriginalFilename().lastIndexOf(".") + 1);

            byte[] bioData = description.getBytes();
            Path savedDescriptionPath = Paths.get(descriptionPath, descriptionFileName);
            Files.write(savedDescriptionPath, bioData);

            byte[] photoData = photo.getBytes();
            Path savedPhotoPath = Paths.get(photoPath, photoFileName);
            Files.write(savedPhotoPath, photoData);

            artworkToSave.setPictureUrl(savedPhotoPath.toString());
            artworkToSave.setDescriptionUrl(savedDescriptionPath.toString());

        } catch (IOException e) {
            failureMessages.add("The files not saved.");
            logger.warn("The files not saved.");
        }

        if (!failureMessages.isEmpty()) {
            artworkResponse.setFailureMessages(failureMessages);

            return artworkResponse;
        }

        Artwork savedArtwork = this.artworkRepository.save(artworkToSave);
        ArtistToShowInArtworkDto artistToShowInArtworkDto = this.globalMapper.map(artist,
                ArtistToShowInArtworkDto.class);
        StyleToShowInArtworkDto styleToShowInArtworkDto = this.globalMapper.map(style,
                StyleToShowInArtworkDto.class);
        ArtworkDto artworkDto = this.globalMapper.map(savedArtwork, ArtworkDto.class);
        artworkDto.setArtist(artistToShowInArtworkDto);
        artworkDto.setStyle(styleToShowInArtworkDto);
        artworkResponse.setArtworkDto(artworkDto);
        logger.info("An artwork with name \"{}\" successfully saved", artworkCreateDto.getFullName());

        return artworkResponse;
    }

    public ArtworkResponse updateArtwork(Long id, ArtworkDto artworkDto, List<MultipartFile> multipartFiles) {
        ArtworkResponse artworkResponse = new ArtworkResponse();
        List<String> failureMessages = new ArrayList<>();

        Optional<Artwork> artworkToUpdate = artworkRepository.findById(id);
        if (artworkToUpdate.isEmpty()) {
            logger.info("An artwork with id {} doesn't exist.", id);
            failureMessages.add(String.format("An artwork with id %d doesn't exist.", id));
            artworkResponse.setFailureMessages(failureMessages);

            return artworkResponse;
        }

        if (artworkDto.getFullName() != null) {
            artworkToUpdate.get().setFullName(artworkDto.getFullName());
        }

        if (artworkDto.getStyle() != null) {
            Optional<Style> style = styleRepository.findByName(artworkDto.getStyle().getName());

            if (style.isEmpty()) {
                logger.info("A style with name {} doesn't exist.", artworkDto.getStyle().getName());
                failureMessages.add(String.format("A style with name %s doesn't exist.",
                        artworkDto.getStyle().getName()));
                artworkResponse.setFailureMessages(failureMessages);

                return artworkResponse;
            }

            artworkToUpdate.get().setStyle(style.get());
        }

        if (artworkDto.getArtist() != null) {
            Optional<Artist> artist = artistRepository.findById(artworkDto.getArtist().getId());

            if (artist.isEmpty()) {
                logger.info("An artist with id {} doesn't exist.", id);
                failureMessages.add(String.format("An artist with id %d doesn't exist.", id));
                artworkResponse.setFailureMessages(failureMessages);

                return artworkResponse;
            }

            artworkToUpdate.get().setArtist(artist.get());
        }

        if (multipartFiles.size() > 2) {
            failureMessages.add("Selected files count are greater than 2.");
            artworkResponse.setFailureMessages(failureMessages);
            logger.info("Selected files count are greater than 2.");

            return artworkResponse;
        }

        if (multipartFiles.size() == 1) {
            MultipartFile file = multipartFiles.get(0);
            if ((Objects.requireNonNull(file.getContentType()).contains("text"))) {
                try {
                    boolean descriptionFullPath = this.updateFile("description",
                            file, artworkToUpdate.get());
                    if (!descriptionFullPath) {
                        failureMessages.add("Can't update description file");
                        artworkResponse.setFailureMessages(failureMessages);

                        return artworkResponse;
                    }
                } catch (IOException e) {
                    failureMessages.add("Can't update bio file");
                    artworkResponse.setFailureMessages(failureMessages);

                    return artworkResponse;
                }
            } else if ((Objects.requireNonNull(file.getContentType()).contains("image"))) {
                try {
                    boolean photoFullPath = this.updateFile("photo", file, artworkToUpdate.get());
                    if (!photoFullPath) {
                        failureMessages.add("Can't update photo file");
                        artworkResponse.setFailureMessages(failureMessages);

                        return artworkResponse;
                    }
                } catch (IOException e) {
                    failureMessages.add("Can't update photo file");
                    artworkResponse.setFailureMessages(failureMessages);

                    return artworkResponse;
                }
            } else {
                failureMessages.add(String.format(
                        "Selected file must be image or text, but you select %s",
                        file.getContentType()));
                artworkResponse.setFailureMessages(failureMessages);
                logger.info(String.format(
                        "Selected file must be image or text, but you select %s",
                        file.getContentType()));

                return artworkResponse;
            }
        } else if (multipartFiles.size() == 2) {
            MultipartFile firstFile = multipartFiles.get(0);
            MultipartFile secondFile = multipartFiles.get(1);
            if (!((Objects.requireNonNull(firstFile.getContentType()).contains("image") &&
                    Objects.requireNonNull(secondFile.getContentType()).contains("text")) ||
                    (Objects.requireNonNull(firstFile.getContentType()).contains("text") &&
                            Objects.requireNonNull(secondFile.getContentType()).contains("image")))
            ) {

                failureMessages.add(String.format(
                        "Selected files must be image and text, but you select %s and %s.",
                        firstFile.getContentType(), secondFile.getContentType()));
                artworkResponse.setFailureMessages(failureMessages);
                logger.info(String.format(
                        "Selected files must be image and text, but you select %s and %s.",
                        firstFile.getContentType(), secondFile.getContentType()));

                return artworkResponse;
            }

            MultipartFile description;
            MultipartFile photo;
            if (Objects.requireNonNull(firstFile.getContentType()).contains("image")) {
                photo = firstFile;
                description = secondFile;
            } else {
                description = firstFile;
                photo = secondFile;
            }

            try {
                boolean descriptionFullPath = this.updateFile("description",
                        description, artworkToUpdate.get());
                if (!descriptionFullPath) {
                    failureMessages.add("Can't update description file");
                    artworkResponse.setFailureMessages(failureMessages);

                    return artworkResponse;
                }
            } catch (IOException e) {
                failureMessages.add("Can't update bio file");
                artworkResponse.setFailureMessages(failureMessages);

                return artworkResponse;
            }

            try {
                boolean photoFullPath = this.updateFile("photo", photo, artworkToUpdate.get());
                if (!photoFullPath) {
                    failureMessages.add("Can't update photo file");
                    artworkResponse.setFailureMessages(failureMessages);

                    return artworkResponse;
                }
            } catch (IOException e) {
                failureMessages.add("Can't update photo file");
                artworkResponse.setFailureMessages(failureMessages);

                return artworkResponse;
            }
        }

        Artwork savedArtwork = this.artworkRepository.save(artworkToUpdate.get());

        artworkResponse.setArtworkDto(this.globalMapper.map(savedArtwork, ArtworkDto.class));
        logger.info("An artwork with name \"{}\" successfully updated.", artworkDto.getFullName());

        return artworkResponse;
    }

    private boolean updateFile(String pathToDelete, MultipartFile file, Artwork artwork) throws IOException {
        String path;
        if (pathToDelete.equals("description")) {
            path = artwork.getDescriptionUrl();
        } else {
            path = artwork.getPictureUrl();
        }

        boolean result = Files.deleteIfExists(Paths.get(path));
        if (result) {
            try {
                byte[] data = file.getBytes();
                Path savedPhotoPath = Paths.get(path);
                Files.write(savedPhotoPath, data);
                return true;
            } catch (IOException e) {
                logger.warn("The files not saved.");

                return false;
            }
        } else {
            logger.warn("Unable to delete the file.");

            return false;
        }
    }


    public ArtworkResponse hideArtwork(Long id) {
        ArtworkResponse artworkResponse = new ArtworkResponse();
        Optional<Artwork> artworkToHide = artworkRepository.findById(id);
        List<String> failureMessages = new ArrayList<>();

        if (artworkToHide.isEmpty()) {
            logger.info("An artwork with id {} doesn't exist.", id);
            failureMessages.add(String.format("An artwork with id %d doesn't exist.", id));

            artworkResponse.setFailureMessages(failureMessages);

            return artworkResponse;
        }

        artworkToHide.get().setEnabled(false);
        Artwork hiddenArtwork = artworkRepository.save(artworkToHide.get());

        artworkResponse.setArtworkDto(globalMapper.map(hiddenArtwork, ArtworkDto.class));

        logger.info("The artwork with id {} was successfully hidden.", id);

        return artworkResponse;
    }

}
