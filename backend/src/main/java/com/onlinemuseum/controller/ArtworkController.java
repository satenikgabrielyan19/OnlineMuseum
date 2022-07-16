package com.onlinemuseum.controller;

import com.onlinemuseum.dto.*;
import com.onlinemuseum.response.*;
import com.onlinemuseum.service.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/artworks")
public class ArtworkController {
    private final ArtworkService artworkService;
    private final CommentService commentService;
    private final ResponseCreator responseCreator;

    private final Logger logger = LoggerFactory.getLogger(ArtworkController.class);

    @Autowired
    public ArtworkController(ArtworkService artworkService,
                             CommentService commentService, ResponseCreator responseCreator) {
        this.artworkService = artworkService;
        this.commentService = commentService;
        this.responseCreator = responseCreator;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArtwork(@PathVariable("id") Long id) {
        logger.info("Received a request to get artwork with id {}.", id);
        Optional<ArtworkDto> artworkDto = artworkService.getArtwork(id);

        if (artworkDto.isEmpty()) {
            logger.info("An artwork with id \"{}\" not found", id);
            return new ResponseEntity<>(String.format("An artwork with id %d not found", id),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(artworkDto.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> createArtwork(@RequestPart("files") List<MultipartFile> multipartFiles,
                                          @RequestPart("artwork") ArtworkCreateDto artworkCreateDto) {
        logger.info("Received a request to create Artwork.{ " + artworkCreateDto.getFullName() + " }");
        ArtworkResponse artworkResponse = artworkService.createArtwork(artworkCreateDto, multipartFiles);

        return responseCreator.createResponseEntityWithStatus(artworkResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArtwork(@PathVariable Long id,
                                          @RequestPart("files") List<MultipartFile> multipartFiles,
                                          @RequestPart("artwork") ArtworkDto artworkDto) {
        logger.info("Received a request to update an artwork with id {}.", id);
        ArtworkResponse artworkResponse = artworkService.updateArtwork(id, artworkDto, multipartFiles);

        return responseCreator.createResponseEntityWithStatus(artworkResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> hideArtwork(@PathVariable("id") Long id) {
        logger.info("Received a request to hide the artwork with id {}.", id);

        ArtworkResponse artworkResponse = artworkService.hideArtwork(id);

        return responseCreator.createResponseEntityWithStatus(artworkResponse);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> createComment(@PathVariable("id") Long id,
                                           @Valid @RequestBody UserCommentCreateDto userComment) {
        logger.info("Received a request to create comment (\"{}\") by \"{}\".",
                userComment.getContent(), userComment.getUsername());
        CommentResponse commentResponse = commentService.createComment(id, userComment);

        return responseCreator.createResponseEntityWithStatus(commentResponse);
    }

    @GetMapping("/{artworkId}/comments")
    public ResponseEntity<?> getComments(@PathVariable("artworkId") Long id) {
        logger.info("Received a request to get all comments of an artwork with id {}.", id);
        CommentResponse commentResponse = commentService.getComments(id);

        return responseCreator.createResponseEntityWithStatus(commentResponse);
    }

    @PutMapping("/{artworkId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable("artworkId") Long artworkId,
                                           @PathVariable("commentId") Long commentId,
                                           @Valid @RequestBody CommentUpdateDto comment) {
        logger.info("Received a request to update a comment with id {} of an artwork with id {}.",
                commentId, artworkId);

        CommentResponse commentResponse = commentService.updateComment(artworkId, commentId, comment);

        return responseCreator.createResponseEntityWithStatus(commentResponse);
    }

    @DeleteMapping("/{artworkId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("artworkId") Long artworkId,
                                           @PathVariable("commentId") Long commentId,
                                           @Valid @RequestBody CommentDeleteDto comment) {
        logger.info("Received a request to delete a comment with id {} of an artwork with id {}.",
                commentId, artworkId);

        CommentResponse commentResponse = commentService.deleteComment(artworkId, commentId, comment);

        return responseCreator.createResponseEntityWithStatus(commentResponse);
    }

    @PostMapping("/{artworkId}/comments/{commentId}/sub-comments")
    public ResponseEntity<?> createSubComment(@PathVariable("artworkId") Long artworkId,
                                              @PathVariable("commentId") Long commentId,
                                              @Valid @RequestBody UserCommentCreateDto userComment) {
        logger.info("Received a request to create comment (\"{}\") by \"{}\".",
                userComment.getContent(), userComment.getUsername());
        CommentResponse commentResponse = commentService.createComment(commentId, userComment);

        return responseCreator.createResponseEntityWithStatus(commentResponse);
    }
}
