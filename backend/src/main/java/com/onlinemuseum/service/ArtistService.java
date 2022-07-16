package com.onlinemuseum.service;

import com.onlinemuseum.controller.ArtistController;
import com.onlinemuseum.criteria.ArtistSearchCriteria;
import com.onlinemuseum.criteria.ArtworkSearchCriteria;
import com.onlinemuseum.domain.entity.Artist;
import com.onlinemuseum.domain.entity.Artwork;
import com.onlinemuseum.domain.entity.Sphere;
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
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final GlobalMapper globalMapper;
    private final SphereRepository sphereRepository;
    private final ArtworkRepository artworkRepository;

    private final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    @Autowired
    public ArtistService(ArtistRepository artistRepository, GlobalMapper globalMapper,
                         SphereRepository sphereRepository, ArtworkRepository artworkRepository) {
        this.artistRepository = artistRepository;
        this.globalMapper = globalMapper;
        this.sphereRepository = sphereRepository;
        this.artworkRepository = artworkRepository;
    }

    @Value("${global.path}")
    private String path;

    private Integer pageSize = 10;

    public GenericPageableResponse<ArtistDto> getArtistsOfSphere(String sphere,
                                                                 ArtistSearchCriteria artistSearchCriteria) {
        Integer pageNumber = artistSearchCriteria.getPageNumber();
        Integer pageSize = artistSearchCriteria.getPageSize();

        String sortField = artistSearchCriteria.getSortField();
        if (sortField == null || sortField.isEmpty()) {
            sortField = "fullName";
        }

        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(artistSearchCriteria.getSortDirection());
        } catch (Exception e) {
            sortDirection = Sort.Direction.ASC;
        }

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize,
                Sort.by(sortDirection, sortField));

        Page<Artist> artists = artistRepository.findByCriteria(artistSearchCriteria.getFullName(), artistSearchCriteria.getBirthYearFrom(),
                artistSearchCriteria.getBirthYearTo(), artistSearchCriteria.getStyle()
                , pageRequest
                , sphere
        );

        System.out.println(artists);
//        return null;
        return new GenericPageableResponse<>(
                globalMapper.mapList(artists.getContent(), ArtistDto.class),
                artists.getTotalPages(),
                artists.getNumber()
        );
    }

    public Optional<ArtistDto> getArtist(Long id) {
        Optional<Artist> artist = this.artistRepository.findById(id);

        if (artist.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(this.globalMapper.map(artist.get(), ArtistDto.class));
    }

    public Optional<GenericPageableResponse<ArtworkDto>> getArtworksOfArtist(Long artistId,
                                                                             ArtworkSearchCriteria artworkSearchCriteria) {
        Optional<Artist> artist = artistRepository.findById(artistId);
        if (artist.isEmpty() || !artist.get().getEnabled()) {
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

        Page<Artwork> artworks = artworkRepository.findAllByArtist(artistId, pageRequest);

        return Optional.of(new GenericPageableResponse<>(
                globalMapper.mapList(artworks.getContent(), ArtworkDto.class),
                artworks.getTotalPages(),
                pageNumber
        ));
    }

    public ArtistResponse createArtist(ArtistCreateDto artistCreateDto, List<MultipartFile> multipartFiles) {
        ArtistResponse artistResponse = new ArtistResponse();
        List<String> failureMessages = new ArrayList<>();

        Optional<Sphere> sphere = this.sphereRepository.getByName(artistCreateDto.getSphere());
        if (sphere.isEmpty()) {
            failureMessages.add(String.format("A sphere with name \"%s\" doesn't exists.",
                    artistCreateDto.getSphere()));
            artistResponse.setFailureMessages(failureMessages);

            logger.info("A sphere with name \"{}\" doesn't exist", artistCreateDto.getSphere());

            return artistResponse;
        }

        if (artistCreateDto.getDeathYear() != null && artistCreateDto.getBirthYear() != null &&
                artistCreateDto.getBirthYear().isAfter(artistCreateDto.getDeathYear())) {
            failureMessages.add("Birth year of artist is greater then death year of artist.");
            artistResponse.setFailureMessages(failureMessages);

            logger.info("Birth year of artist is greater then death year of artist.");

            return artistResponse;
        }

        Artist artistToSave = this.globalMapper.map(artistCreateDto, Artist.class);

        artistToSave.setSphere(sphere.get());

        String bioPath = path +
                artistCreateDto.getSphere().toLowerCase() + "\\artist\\bio";

        String photoPath = path +
                artistCreateDto.getSphere().toLowerCase() + "\\artist\\photo";

        try {

            if (multipartFiles.size() != 2) {
                failureMessages.add("Selected files count isn't 2.");
                artistResponse.setFailureMessages(failureMessages);
                logger.info("Selected files count isn't 2.");

                return artistResponse;
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
                artistResponse.setFailureMessages(failureMessages);
                logger.info(String.format(
                        "Selected files must be image and text, but you select %s and %s.",
                        firstFile.getContentType(), secondFile.getContentType()));

                return artistResponse;
            }

            MultipartFile bio;
            MultipartFile photo;
            if (Objects.requireNonNull(firstFile.getContentType()).contains("image")) {
                photo = firstFile;
                bio = secondFile;
            } else {
                bio = firstFile;
                photo = secondFile;
            }

            Optional<Artist> artist = artistRepository.findTopByOrderByIdDesc();

            Long id;
            if (artist.isEmpty()) {
                id = 1L;
            } else {
                id = artist.get().getId() + 1;
            }

            String bioFileName = id + artistCreateDto.getFullName() + "." +
                    Objects.requireNonNull(bio.getOriginalFilename())
                            .substring(bio.getOriginalFilename().lastIndexOf(".") + 1);
            String photoFileName = id + artistCreateDto.getFullName() + "." +
                    Objects.requireNonNull(photo.getOriginalFilename())
                            .substring(photo.getOriginalFilename().lastIndexOf(".") + 1);

            byte[] bioData = bio.getBytes();
            Path savedBioPath = Paths.get(bioPath, bioFileName);
            Files.write(savedBioPath, bioData);

            byte[] photoData = photo.getBytes();
            Path savedPhotoPath = Paths.get(photoPath, photoFileName);
            Files.write(savedPhotoPath, photoData);

            artistToSave.setPhotoUrl(savedPhotoPath.toString());
            artistToSave.setBioUrl(savedBioPath.toString());

        } catch (IOException e) {
            failureMessages.add("The files not saved.");
            logger.warn("The files not saved.");
        }

        if (!failureMessages.isEmpty()) {
            artistResponse.setFailureMessages(failureMessages);
            return artistResponse;
        }

        Artist savedArtist = this.artistRepository.save(artistToSave);

        artistResponse.setArtistDto(this.globalMapper.map(savedArtist, ArtistDto.class));
        logger.info("An artist with name \"{}\" successfully saved", savedArtist.getFullName());

        return artistResponse;
    }

    public ArtistResponse updateArtist(Long id, ArtistDto artistDto, List<MultipartFile> multipartFiles) {
        ArtistResponse artistResponse = new ArtistResponse();
        List<String> failureMessages = new ArrayList<>();

        Optional<Artist> artistToUpdate = artistRepository.findById(id);
        if (artistToUpdate.isEmpty()) {
            logger.info("An artist with id {} doesn't exist.", id);
            failureMessages.add(String.format("An artist with id %d doesn't exist.", id));
            artistResponse.setFailureMessages(failureMessages);

            return artistResponse;
        }

        if (artistDto.getBirthYear() != null && artistDto.getDeathYear() != null &&
                artistDto.getBirthYear().isAfter(artistDto.getDeathYear())) {
            failureMessages.add("Birth year of artist is greater then death year of artist.");
            artistResponse.setFailureMessages(failureMessages);

            logger.info("Birth year of artist is greater then death year of artist.");

            return artistResponse;
        } else {
            if (artistDto.getBirthYear() != null) {
                artistToUpdate.get().setBirthYear(artistDto.getBirthYear());
            }
            if (artistDto.getDeathYear() != null) {
                artistToUpdate.get().setDeathYear(artistDto.getDeathYear());
            }
        }

        if (multipartFiles.size() > 2) {
            failureMessages.add("Selected files count are greater than 2.");
            artistResponse.setFailureMessages(failureMessages);
            logger.info("Selected files count are greater than 2.");

            return artistResponse;
        }

        if (!multipartFiles.get(0).isEmpty()) {
            if (multipartFiles.size() == 1) {
                MultipartFile file = multipartFiles.get(0);
                if ((Objects.requireNonNull(file.getContentType()).contains("text"))) {
                    try {
                        boolean bioFullPath = this.updateFile("bio", file, artistToUpdate.get());
                        if (!bioFullPath) {
                            failureMessages.add("Can't update bio file");
                            artistResponse.setFailureMessages(failureMessages);

                            return artistResponse;
                        }
                    } catch (IOException e) {
                        failureMessages.add("Can't update bio file");
                        artistResponse.setFailureMessages(failureMessages);

                        return artistResponse;
                    }
                } else if ((Objects.requireNonNull(file.getContentType()).contains("image"))) {
                    try {
                        boolean photoFullPath = this.updateFile("photo", file, artistToUpdate.get());
                        if (!photoFullPath) {
                            failureMessages.add("Can't update photo file");
                            artistResponse.setFailureMessages(failureMessages);

                            return artistResponse;
                        }
                    } catch (IOException e) {
                        failureMessages.add("Can't update photo file");
                        artistResponse.setFailureMessages(failureMessages);

                        return artistResponse;
                    }
                } else {
                    failureMessages.add(String.format(
                            "Selected file must be image or text, but you select %s",
                            file.getContentType()));
                    artistResponse.setFailureMessages(failureMessages);
                    logger.info(String.format(
                            "Selected file must be image or text, but you select %s",
                            file.getContentType()));

                    return artistResponse;
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
                    artistResponse.setFailureMessages(failureMessages);
                    logger.info(String.format(
                            "Selected files must be image and text, but you select %s and %s.",
                            firstFile.getContentType(), secondFile.getContentType()));

                    return artistResponse;
                }

                MultipartFile bio;
                MultipartFile photo;
                if (Objects.requireNonNull(firstFile.getContentType()).contains("image")) {
                    photo = firstFile;
                    bio = secondFile;
                } else {
                    bio = firstFile;
                    photo = secondFile;
                }

                try {
                    boolean bioFullPath = this.updateFile("bio", bio, artistToUpdate.get());
                    if (!bioFullPath) {
                        failureMessages.add("Can't update bio file");
                        artistResponse.setFailureMessages(failureMessages);

                        return artistResponse;
                    }
                } catch (IOException e) {
                    failureMessages.add("Can't update bio file");
                    artistResponse.setFailureMessages(failureMessages);

                    return artistResponse;
                }

                try {
                    boolean photoFullPath = this.updateFile("photo", photo, artistToUpdate.get());
                    if (!photoFullPath) {
                        failureMessages.add("Can't update photo file");
                        artistResponse.setFailureMessages(failureMessages);

                        return artistResponse;
                    }
                } catch (IOException e) {
                    failureMessages.add("Can't update photo file");
                    artistResponse.setFailureMessages(failureMessages);

                    return artistResponse;
                }

            }
        }

        if (artistDto.getFullName() != null) {
            artistToUpdate.get().setFullName(artistDto.getFullName());
        }

        Artist savedArtist = this.artistRepository.save(artistToUpdate.get());

        artistResponse.setArtistDto(this.globalMapper.map(savedArtist, ArtistDto.class));
        logger.info("An artist with name \"{}\" successfully updated.", savedArtist.getFullName());

        return artistResponse;
    }

    private boolean updateFile(String pathToDelete, MultipartFile file, Artist artist) throws IOException {
        String path;
        if (pathToDelete.equals("bio")) {
            path = artist.getBioUrl();
        } else {
            path = artist.getPhotoUrl();
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


    public ArtistResponse hideArtist(Long id) {
        ArtistResponse artistResponse = new ArtistResponse();
        Optional<Artist> artistToHide = artistRepository.findById(id);
        List<String> failureMessages = new ArrayList<>();

        if (artistToHide.isEmpty()) {
            logger.info("An artist with id {} doesn't exist.", id);
            failureMessages.add(String.format("An artist with id %d doesn't exist.", id));

            artistResponse.setFailureMessages(failureMessages);

            return artistResponse;
        }

        List<Artwork> artworks = artistToHide.get().getArtworks();
        if (!artworks.isEmpty()) {
            for (Artwork artwork : artworks) {
                artwork.setEnabled(false);
            }
        }

        artistToHide.get().setEnabled(false);
        Artist hiddenArtist = artistRepository.save(artistToHide.get());

        artistResponse.setArtistDto(globalMapper.map(hiddenArtist, ArtistDto.class));

        logger.info("The artist with id {} was successfully hidden.", id);

        return artistResponse;
    }
}
