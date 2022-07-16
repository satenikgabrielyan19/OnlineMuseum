package com.onlinemuseum.criteria;

import lombok.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistSearchCriteria extends SearchCriteria {

    private String fullName = null;

    private Integer birthYearFrom = null;

    private Integer birthYearTo = null;

    private String style = null;
}
