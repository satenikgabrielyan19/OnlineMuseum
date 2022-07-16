package com.onlinemuseum.service;

import com.onlinemuseum.domain.entity.*;
import com.onlinemuseum.domain.enumentity.*;
import com.onlinemuseum.dto.*;
import com.onlinemuseum.mapper.*;
import com.onlinemuseum.repository.*;
import com.onlinemuseum.response.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArtworkRepository artworkRepository;
    private final GlobalMapper globalMapper;

    private final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          UserRepository userRepository, ArtworkRepository artworkRepository, GlobalMapper globalMapper) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.artworkRepository = artworkRepository;
        this.globalMapper = globalMapper;
    }

    public CommentResponse createComment(Long id, UserCommentCreateDto userComment) {
        CommentResponse commentResponse = new CommentResponse();
        List<String> failureMessages = new ArrayList<>();

        Optional<User> user = userRepository.findByUsername(userComment.getUsername());

        if (user.isEmpty()) {
            logger.info("An user with username \"{}\" doesn't exist.", userComment.getUsername());
            failureMessages.add(String.format("An user with username \"%s\" doesn't exist.", userComment.getUsername()));

            commentResponse.setFailureMessages(failureMessages);

            return commentResponse;
        }

        Optional<Artwork> artwork = artworkRepository.findById(id);

        if (artwork.isEmpty()) {
            logger.info("A artwork with id {} doesn't exist.", id);
            failureMessages.add(String.format("A artwork with id %d doesn't exist.", id));

            commentResponse.setFailureMessages(failureMessages);

            return commentResponse;
        }

        Comment commentToSave = globalMapper.map(userComment, Comment.class);
        commentToSave.setAuthor(user.get());
        commentToSave.setArtwork(artwork.get());
        commentToSave.setState(CommentState.CREATED);

        Comment savedComment = commentRepository.save(commentToSave);

        CommentDto commentDto = globalMapper.map(savedComment, CommentDto.class);
        commentDto.setAuthorUsername(userComment.getUsername());

        List<CommentDto> comments = List.of(commentDto);
        commentResponse.setComments(comments);

        logger.info("The comment was saved successfully.");

        return commentResponse;
    }

    public CommentResponse getComments(Long id) {
        CommentResponse commentResponse = new CommentResponse();
        List<String> failureMessages = new ArrayList<>();
        Optional<Artwork> artwork = artworkRepository.findById(id);

        if (artwork.isEmpty()) {
            logger.info("A artwork with id {} doesn't exist.", id);
            failureMessages.add(String.format("A artwork with id %d doesn't exist.", id));

            commentResponse.setFailureMessages(failureMessages);

            return commentResponse;
        }

        List<Comment> comments = commentRepository.
                findAllByArtworkIdAndStateOrderByCreationDateTime(id, CommentState.CREATED);

        commentResponse.setComments(globalMapper.mapList(comments, CommentDto.class));

        logger.info("All comments of artwork with id {} were successfully got.", id);

        return commentResponse;
    }

    public CommentResponse updateComment(Long artworkId, Long commentId, CommentUpdateDto commentUpdateDto) {
        CommentResponse commentResponse = new CommentResponse();
        List<String> failureMessages = new ArrayList<>();

        Optional<Artwork> artwork = artworkRepository.findById(artworkId);

        if (artwork.isEmpty()) {
            logger.info("An artwork with id {} doesn't exist.", artworkId);
            failureMessages.add(String.format("An artwork with id %d doesn't exist.", artworkId));

            commentResponse.setFailureMessages(failureMessages);

            return commentResponse;
        }

        if (!artwork.get().getEnabled()) {
            logger.info("An artwork with id {} has been deleted.", artworkId);
            failureMessages.add(String.format("An artwork with id %d has been deleted.", artworkId));

            commentResponse.setFailureMessages(failureMessages);

            return commentResponse;
        }

        Optional<Comment> comment = commentRepository.findById(commentId);

        if (comment.isEmpty()) {
            logger.info("A comment with id {} doesn't exist.", commentId);
            failureMessages.add(String.format("A comment with id %d doesn't exist.", commentId));

            commentResponse.setFailureMessages(failureMessages);

            return commentResponse;
        }

        if (comment.get().getState().equals(CommentState.DELETED)) {
            logger.info("A comment with id {} has been deleted.", commentId);
            failureMessages.add(String.format("A comment with id %d has been deleted.", commentId));

            commentResponse.setFailureMessages(failureMessages);

            return commentResponse;
        }

        Comment commentToUpdate = comment.get();

        Comment commentToSave = new Comment();
        commentToSave.setState(CommentState.EDITED);
        commentToSave.setContent(commentUpdateDto.getContent());
        commentToSave.setCreationDateTime(commentUpdateDto.getCreationDateTime());
        commentToSave.setAuthor(commentToUpdate.getAuthor());
        commentToSave.setArtwork(commentToUpdate.getArtwork());
        commentToSave.setParent(commentToUpdate);

        //TODO: we need to update the parent comment of subComments from commentToUpdate to commentToSave

        commentToUpdate.setState(CommentState.DELETED);
        commentToUpdate.setRemovalDateTime(commentUpdateDto.getCreationDateTime());

        commentRepository.save(commentToUpdate);
        Comment savedComment = commentRepository.save(commentToSave);

        CommentDto commentDto = globalMapper.map(savedComment, CommentDto.class);
        List<CommentDto> comments = List.of(commentDto);
        commentResponse.setComments(comments);

        logger.info("The comment with id {} of artwork with id {} was successfully updated.",
                savedComment.getId(), savedComment.getArtwork().getId());

        return commentResponse;
    }

    public CommentResponse deleteComment(Long artworkId, Long commentId, CommentDeleteDto commentDeleteDto) {
        CommentResponse commentResponse = new CommentResponse();
        List<String> failureMessages = new ArrayList<>();

        Optional<Artwork> artwork = artworkRepository.findById(artworkId);

        if (artwork.isEmpty()) {
            logger.info("A artwork with id {} doesn't exist.", artworkId);
            failureMessages.add(String.format("A artwork with id %d doesn't exist.", artworkId));

            commentResponse.setFailureMessages(failureMessages);

            return commentResponse;
        }

        Optional<Comment> comment = commentRepository.findById(commentId);

        if (comment.isEmpty()) {
            logger.info("A comment with id {} doesn't exist.", commentId);
            failureMessages.add(String.format("A comment with id %d doesn't exist.", commentId));

            commentResponse.setFailureMessages(failureMessages);

            return commentResponse;
        }

        if (comment.get().getState().equals(CommentState.DELETED)) {
            logger.info("A comment with id {} has been deleted.", commentId);
            failureMessages.add(String.format("A comment with id %d has been deleted.", commentId));

            commentResponse.setFailureMessages(failureMessages);

            return commentResponse;
        }

        Comment commentToDelete = comment.get();
        commentToDelete.setRemovalDateTime(commentDeleteDto.getRemovalDateTime());
        commentToDelete.setState(CommentState.DELETED);

        //TODO: we need to "delete" subComments of commentToDelete

        Comment savedComment = commentRepository.save(commentToDelete);

        CommentDto commentDto = globalMapper.map(savedComment, CommentDto.class);
        List<CommentDto> comments = List.of(commentDto);
        commentResponse.setComments(comments);

        logger.info("The comment with id {} of artwork with id {} was successfully deleted.",
                savedComment.getId(), savedComment.getArtwork().getId());

        return commentResponse;
    }

}
