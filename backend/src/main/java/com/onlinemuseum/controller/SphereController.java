package com.onlinemuseum.controller;

import com.onlinemuseum.criteria.*;
import com.onlinemuseum.dto.*;
import com.onlinemuseum.response.*;
import com.onlinemuseum.service.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/spheres")
public class SphereController {
    private final SphereService sphereService;
    private final StyleService styleService;
    private final ArtistService artistService;
    private final ResponseCreator responseCreator;

    private final Logger logger = LoggerFactory.getLogger(SphereController.class);

    @Autowired
    public SphereController(SphereService sphereService, StyleService styleService,
                            ArtistService artistService, ResponseCreator responseCreator) {
        this.sphereService = sphereService;
        this.styleService = styleService;
        this.artistService = artistService;
        this.responseCreator = responseCreator;
    }

    @PostMapping
    public ResponseEntity<?> createSphere(@RequestParam String name) {
        logger.info("Received a request to create sphere with name \"{}\".", name);
        SphereResponse sphereResponse = sphereService.createSphere(name);

        return responseCreator.createResponseEntityWithStatus(sphereResponse);
    }

    @GetMapping
    public ResponseEntity<?> getSpheres() {
        logger.info("Received a request to get all spheres.");
        List<SphereDto> spheres = sphereService.getAllSpheres();

        logger.info("The request to get all spheres has successfully done.");
        return new ResponseEntity<>(spheres, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSphere(@PathVariable Long id,
                                          @Valid @RequestBody SphereUpdateDto sphereUpdateDto) {
        logger.info("Received a request to update a name of the sphere with id {} to {}.",
                id, sphereUpdateDto.getName());
        SphereResponse sphereResponse = sphereService.updateSphere(id, sphereUpdateDto);

        return responseCreator.createResponseEntityWithStatus(sphereResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> hideSphere(@PathVariable("id") Long id) {
        logger.info("Received a request to hide the sphere with id {}.", id);

        SphereResponse sphereResponse = sphereService.hideSphere(id);

        return responseCreator.createResponseEntityWithStatus(sphereResponse);
    }

    @GetMapping("/{sphere}/styles")
    public ResponseEntity<?> getStylesOfSphere(@PathVariable("sphere") String sphere,
                                               @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                               @RequestParam(value = "pageSize", defaultValue = "25") Integer pageSize) {
        logger.info("Received a request to get styles of \"{}\" from page {}.", sphere, pageNumber);
        GenericPageableResponse<StyleDto> styles = styleService.getStylesOfSphere(sphere, pageNumber, pageSize);

        logger.info("The request to get styles of \"{}\" from page {} has successfully done.", sphere, pageNumber);
        return new ResponseEntity<>(styles, HttpStatus.OK);
    }

    @GetMapping("/{sphere}/artists")
    public ResponseEntity<?> getArtistsOfSphere(@PathVariable("sphere") String sphere,
                                                ArtistSearchCriteria artistSearchCriteria) {
        GenericPageableResponse<ArtistDto> artists = artistService.getArtistsOfSphere(sphere, artistSearchCriteria);

        return new ResponseEntity<>(artists, HttpStatus.OK);
    }
}
