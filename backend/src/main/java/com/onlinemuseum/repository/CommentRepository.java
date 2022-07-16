package com.onlinemuseum.repository;


import com.onlinemuseum.domain.entity.*;
import com.onlinemuseum.domain.enumentity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByArtworkIdAndStateOrderByCreationDateTime(Long id, CommentState commentState);
}
