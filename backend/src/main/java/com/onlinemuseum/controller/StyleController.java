package com.onlinemuseum.controller;

import com.onlinemuseum.criteria.ArtworkSearchCriteria;
import com.onlinemuseum.dto.ArtistDto;
import com.onlinemuseum.dto.ArtworkDto;
import com.onlinemuseum.dto.StyleDto;
import com.onlinemuseum.response.GenericPageableResponse;
import com.onlinemuseum.response.StyleResponse;
import com.onlinemuseum.service.StyleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/style")
public class StyleController {
    private final StyleService styleService;
    private final ResponseCreator responseCreator;


    @Autowired
    public StyleController(StyleService styleService, ResponseCreator responseCreator) {
        this.styleService = styleService;
        this.responseCreator = responseCreator;
    }

    private final Logger logger = LoggerFactory.getLogger(StyleController.class);

    @PostMapping()
    public ResponseEntity<?> createStyle(@RequestBody StyleDto styleCreateDto) {
        logger.info("Received a request to create Style.{ " + styleCreateDto.getName() + " }");
        StyleResponse styleResponse = styleService.createStyle(styleCreateDto);
        return responseCreator.createResponseEntityWithStatus(styleResponse);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> hideStyle(@PathVariable("id") Long id) {
        logger.info("Received a request to hide the style with id {}.", id);

        StyleResponse styleResponse = styleService.hideStyle(id);
        return responseCreator.createResponseEntityWithStatus(styleResponse);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateStyle(@PathVariable Long id,
                                         @RequestBody StyleDto styleDto) {
        logger.info("Received a request to update an style with id {}.", id);
        StyleResponse styleResponse = styleService.updateStyle(id, styleDto);
        return responseCreator.createResponseEntityWithStatus(styleResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStyle(@PathVariable("id") Long id) {
        logger.info("Received a request to get style with id {}.", id);
        Optional<StyleDto> styleDto = styleService.getStyle(id);
//
        if (styleDto.isEmpty()) {
            logger.info("An style with id \"{}\" not found", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(styleDto.get(), HttpStatus.OK);

    }

    @GetMapping("/{id}/artworks")
    public ResponseEntity<?> getArtworksOfArtist(@PathVariable Long id, ArtworkSearchCriteria artistSearchCriteria){
        logger.info("Received a request to get style's artworks with id {}.", id);
        Optional<GenericPageableResponse<ArtworkDto>> artworks =
                styleService.getArtworksOfStyle(id, artistSearchCriteria);

        if (artworks.isEmpty()){
            return new ResponseEntity<>(String.format("A style with id %d not found", id),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(artworks.get(), HttpStatus.OK);
    }

    @GetMapping("/{id}/artists")
    public ResponseEntity<?> getArtistsOfStyle(@PathVariable Long id,
                                               @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                               @RequestParam(value = "pageSize", defaultValue = "25") Integer pageSize){
        logger.info("Received a request to get style's artworks with id {}.", id);
        Optional<GenericPageableResponse<ArtistDto>> artists =
                styleService.getArtistsOfStyle(id,pageNumber,pageSize );

        if (artists.isEmpty()){
            return new ResponseEntity<>(String.format("A artist with id %d not found", id),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(artists.get(), HttpStatus.OK);
    }
}
