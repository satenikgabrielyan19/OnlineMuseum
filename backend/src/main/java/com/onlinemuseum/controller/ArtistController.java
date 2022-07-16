package com.onlinemuseum.controller;

import com.onlinemuseum.criteria.ArtistSearchCriteria;
import com.onlinemuseum.criteria.ArtworkSearchCriteria;
import com.onlinemuseum.dto.ArtistCreateDto;
import com.onlinemuseum.dto.ArtistDto;
import com.onlinemuseum.dto.ArtworkDto;
import com.onlinemuseum.response.ArtistResponse;
import com.onlinemuseum.response.GenericPageableResponse;
import com.onlinemuseum.service.ArtistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/artists")
public class ArtistController {
    private final ArtistService artistService;
    private final ResponseCreator responseCreator;

    private final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    @Autowired
    public ArtistController(ArtistService artistService, ResponseCreator responseCreator) {
        this.artistService = artistService;
        this.responseCreator = responseCreator;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArtist(@PathVariable("id") Long id) {
        logger.info("Received a request to get artist with id {}.", id);
        Optional<ArtistDto> artistDto = artistService.getArtist(id);

        if (artistDto.isEmpty()) {
            logger.info("An artist with id \"{}\" not found", id);
            return new ResponseEntity<>(String.format("An artist with id %d not found", id),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(artistDto.get(), HttpStatus.OK);
    }

    @GetMapping("/{id}/artworks")
    public ResponseEntity<?> getArtworksOfArtist(@PathVariable Long id,
                                                 ArtworkSearchCriteria artistSearchCriteria){
        logger.info("Received a request to get artist's artworks with id {}.", id);
        Optional<GenericPageableResponse<ArtworkDto>> artworks =
                artistService.getArtworksOfArtist(id, artistSearchCriteria);

        if (artworks.isEmpty()){
            return new ResponseEntity<>(String.format("An artist with id %d not found", id),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(artworks.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> createArtist(@RequestPart("files") List<MultipartFile> multipartFiles,
                                         @Valid @RequestPart("artist") ArtistCreateDto artistCreateDto) {
        logger.info("Received a request to create Artist.{ " + artistCreateDto.getFullName() + " }");
        ArtistResponse artistResponse = artistService.createArtist(artistCreateDto, multipartFiles);

        return responseCreator.createResponseEntityWithStatus(artistResponse);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArtist(@PathVariable Long id,
                                          @RequestPart("files") List<MultipartFile> multipartFiles,
                                          @RequestPart("artist") ArtistDto artistDto) {
        logger.info("Received a request to update an artist with id {}.", id);
        ArtistResponse artistResponse = artistService.updateArtist(id, artistDto, multipartFiles);

        return responseCreator.createResponseEntityWithStatus(artistResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> hideArtist(@PathVariable("id") Long id) {
        logger.info("Received a request to hide the artist with id {}.", id);

        ArtistResponse artistResponse = artistService.hideArtist(id);

        return responseCreator.createResponseEntityWithStatus(artistResponse);
    }
}
